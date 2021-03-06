package ivgroup.master.database.bl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ivgroup.master.database.aspect.EmailAspect;
import ivgroup.master.database.connection.ConnectionProvider;
import ivgroup.master.database.dao.impl.OwnerDAOImpl;
import ivgroup.master.database.dto.google.people.api.GooglePeopleAPICredentials;
import ivgroup.master.database.dto.owner.OwnerCredentials;
import ivgroup.master.database.dto.owner.OwnerDashboard;
import ivgroup.master.database.dto.owner.OwnerInsert;
import ivgroup.master.database.dto.owner.OwnerLoginCredentials;
import ivgroup.master.database.dto.owner.OwnerLoginResponseModel;
import ivgroup.master.database.dto.owner.OwnerSelect;
import ivgroup.master.database.dto.owner.OwnerUpdate;
import ivgroup.master.database.service.GooglePeoplesAPIService;
import ivgroup.master.database.service.IVUserDetailsService;
import ivgroup.master.database.util.JwtUtil;

@Service
public class OwnerBusinessLogic {

	@Autowired
	OwnerDAOImpl odi;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private IVUserDetailsService ivUserDetailsService;
	
	@Autowired
	GooglePeoplesAPIService googlePeoplesApiService;
	

	Logger logger = LoggerFactory.getLogger(OwnerBusinessLogic.class);

	public ResponseEntity<OwnerSelect> selectOwnerById(Long ownerId) {
		OwnerSelect rs = null;

		if (ownerId == null) {
			return new ResponseEntity<OwnerSelect>(rs, HttpStatus.BAD_REQUEST);
		}

		try {
			rs = odi.selectOwnerById(ownerId);
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<OwnerSelect>(rs, HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<OwnerSelect>(rs, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (rs == null) {
			return new ResponseEntity<OwnerSelect>(rs, HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<OwnerSelect>(rs, HttpStatus.OK);
	}

	public ResponseEntity<OwnerLoginResponseModel> loginOwner(OwnerLoginCredentials olc) {
		Long ownerId = null;
		OwnerLoginResponseModel ownerResponseModel = new OwnerLoginResponseModel();
		if (olc == null) {
			return new ResponseEntity<OwnerLoginResponseModel>(ownerResponseModel, HttpStatus.BAD_REQUEST);
		}
		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(olc.getUserName(), olc.getPassword()));
		} catch (BadCredentialsException badCredentialsException) {
			throw new BadCredentialsException("Incorrect UserName And Password");
		}
		try {
			ownerId = odi.loginOwner(olc.getUserName(), olc.getSecretKey());
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<OwnerLoginResponseModel>(ownerResponseModel, HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<OwnerLoginResponseModel>(ownerResponseModel, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (ownerId == null) {
			return new ResponseEntity<OwnerLoginResponseModel>(ownerResponseModel, HttpStatus.BAD_REQUEST);
		}
		ownerResponseModel.setOwnerId(ownerId);
		final UserDetails userDetails = ivUserDetailsService.loadUserByUsername(olc.getUserName());
		final String token = jwtUtil.generateToken(userDetails);

		ownerResponseModel.setAccessToken(token);
		return new ResponseEntity<OwnerLoginResponseModel>(ownerResponseModel, HttpStatus.OK);
	}

	public ResponseEntity<Void> addOwner(OwnerInsert oi) {
		String secretKey = null;
		if (oi == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		try {
			oi.setOwnerPassword(bCryptPasswordEncoder.encode(oi.getOwnerPassword()));
			GooglePeopleAPICredentials googlePeopleResponse=new GooglePeopleAPICredentials();;
			
			try {
				googlePeopleResponse=googlePeoplesApiService.addGooglePeople(oi);
			} catch (IOException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
			} catch (GeneralSecurityException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			GooglePeopleAPICredentials googlePeopleCredential=new GooglePeopleAPICredentials();
			if(googlePeopleResponse.getResourceName()==null&&googlePeopleResponse.geteTag()==null)
			{
			googlePeopleCredential.setResourceName("NA");
			googlePeopleCredential.seteTag("NA");
			}
			else
			{
				googlePeopleCredential.setResourceName(googlePeopleResponse.getResourceName());
				googlePeopleCredential.seteTag(googlePeopleResponse.geteTag());
			}
			
			secretKey = odi.addOwner(oi, googlePeopleCredential);
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (secretKey == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		try {
			EmailAspect.sendEmailWithoutAttachment(oi.getOwnerEmail(), "TO",
					"CRM: Acknoledgement for Account Creation in CRM",
					"    <div id=\"BodyContents\" style=\"width: 100%;height: 30%;\">\r\n"
							+ "        <div id=\"Placeholder\" style=\"width: 100%;height: 2%;\"></div>\r\n"
							+ "        <div id=\"MainDiv\" style=\" width: 100%;height: 100%;align-self: center; background-image: linear-gradient(to right, rgba(253, 179, 117, 0.3), rgba(255, 204, 163, 0.3), rgba(255, 224, 199, 0.3), rgba(232, 248, 252, 0.3), rgba(192, 216, 252, 0.5), rgba(142, 223, 255, 0.5));\">\r\n"
							+ "            <div id=\"Text\" style=\"font-size: large;color: rgb(71, 70, 70);font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center;\">\r\n"
							+ "                Thank you " + oi.getOwnerUserName() + " For Creating Account At IVCRM\r\n"
							+ "                <BR />\r\n" + "                <BR />\r\n"
							+ "                <BR /> Your Secret Key is:\r\n" + "                <BR />\r\n"
							+ "            </div>\r\n"
							+ "            <div id=\"SecretKeyText\" style=\"font-size: x-large;color: rgb(58, 57, 57); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center;\">\r\n"
							+ "                " + secretKey + "\r\n" + "            </div>\r\n"
							+ "            <div id=\"Note\" style=\" margin-top: 5%; font-size: small; color: rgb(102, 102, 102); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center\">\r\n"
							+ "                *Please keep this SecretKey private with you only do not share with anyone else\r\n"
							+ "            </div>\r\n" + "        </div>\r\n" + "    </div>",
					"text/html");
		} catch (MessagingException e) {
			logger.error("Exception: " + e.getMessage());
			try {
				deleteOwner(odi.loginOwner(oi.getOwnerEmail(), secretKey));
			} catch (ClassNotFoundException eIn) {
				logger.error("Exception: " + eIn.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			} catch (SQLException eIn) {
				logger.error("Exception: " + eIn.getMessage());
				return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	public ResponseEntity<Void> deleteOwner(Long ownerId) {
		Boolean rs = false;
		if (ownerId == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		try {
			Long count = odi.checkOwnerDeleteStatus(ownerId);
			if (count == 0) {
				rs = odi.deleteOwner(ownerId);
				GooglePeopleAPICredentials googlePeopleApiCredentials=new GooglePeopleAPICredentials();
				googlePeopleApiCredentials=odi.selectOwnerGooglePeopleAPICredentialsByOwnerId(ownerId);
				try {
					googlePeopleApiCredentials.seteTag(googlePeoplesApiService.getETag(googlePeopleApiCredentials.getResourceName()));
				} catch (IOException e) {
					logger.error("Exception: " + e.getMessage());
					return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
				} catch (GeneralSecurityException e) {
					logger.error("Exception: " + e.getMessage());
					return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				try {
					googlePeoplesApiService.deleteGooglePeople(googlePeopleApiCredentials);
				} catch (IOException | GeneralSecurityException e) {
					logger.error("Exception: " + e.getMessage());
					return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
			} else {
				return new ResponseEntity<Void>(HttpStatus.FAILED_DEPENDENCY);
			}
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Void> updateOwnerOwnerName(Connection c, Long ownerId, String ownerName,GooglePeopleAPICredentials googlePeopleApiCredentials ) {
		Boolean rs = false;

		try {
			rs = odi.updateOwnerOwnerName(c, ownerId, ownerName);
			try {
				googlePeopleApiCredentials.seteTag(googlePeoplesApiService.getETag(googlePeopleApiCredentials.getResourceName()));
				googlePeoplesApiService.updateGooglePeopleOwnerName(googlePeopleApiCredentials, ownerName);
			} catch (IOException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			} catch (GeneralSecurityException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Void> updateOwnerOwnerContact(Connection c, Long ownerId, String ownerContact,GooglePeopleAPICredentials googlePeopleApiCredentials) {
		Boolean rs = false;

		try {
			rs = odi.updateOwnerOwnerContact(c, ownerId, ownerContact);
			try {
				googlePeopleApiCredentials.seteTag(googlePeoplesApiService.getETag(googlePeopleApiCredentials.getResourceName()));
				googlePeoplesApiService.updateGooglePeopleOwnerContactNumber(googlePeopleApiCredentials, ownerContact);
			} catch (IOException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			} catch (GeneralSecurityException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Void> updateOwnerOwnerEmail(Connection c, Long ownerId, String ownerEmail,GooglePeopleAPICredentials googlePeopleApiCredentials) {
		Boolean rs = false;

		try {
			rs = odi.updateOwnerOwnerEmail(c, ownerId, ownerEmail);
			try {
				googlePeopleApiCredentials.seteTag(googlePeoplesApiService.getETag(googlePeopleApiCredentials.getResourceName()));
				googlePeoplesApiService.updateGooglePeopleOwnerEmailAddress(googlePeopleApiCredentials, ownerEmail);
			} catch (IOException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			} catch (GeneralSecurityException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Void> updateOwnerIsActive(Connection c, Long ownerId, Boolean isActive) {
		Boolean rs = false;

		try {
			rs = odi.updateOwnerIsActive(c, ownerId, isActive);
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Void> updateOwnerLastEditOn(Connection c, Long ownerId, Timestamp lastEditOn) {
		Boolean rs = false;

		try {
			rs = odi.updateOwnerLastEditOn(c, ownerId, lastEditOn);
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private ResponseEntity<Void> updateOwnerOwnerUserName(Connection c, Long ownerId, String userName,GooglePeopleAPICredentials googlePeopleApiCredentials) {
		Boolean rs = false;

		try {
			rs = odi.updateOwnerOwnerUserName(c, ownerId, userName);
			try {
				googlePeopleApiCredentials.seteTag(googlePeoplesApiService.getETag(googlePeopleApiCredentials.getResourceName()));
				googlePeoplesApiService.updateGooglePeopleOwnerUserName(googlePeopleApiCredentials, userName);
			} catch (IOException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			} catch (GeneralSecurityException e) {
				logger.error("Exception: " + e.getMessage());
				return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
			}
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@SuppressWarnings("unused")
	private ResponseEntity<Void> updateOwnerOwnerPassword(Connection c, Long ownerId, String password) {
		Boolean rs = false;

		try {
			rs = odi.updateOwnerOwnerPassword(c, ownerId, password);
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	public ResponseEntity<Void> updateOwnerFields(Long ownerId, OwnerUpdate ou) {
		if (ou == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		Connection c = null;
		GooglePeopleAPICredentials googlePeopleApiCredentials=new GooglePeopleAPICredentials();
		try {
			c = ConnectionProvider.getConnection();
			googlePeopleApiCredentials=odi.selectOwnerGooglePeopleAPICredentialsByOwnerId(ownerId);
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		ResponseEntity<Void> rs = null;
		Boolean wentIn = false;
		if (ou.getOwnerName() != null) {
			rs = updateOwnerOwnerName(c, ownerId, ou.getOwnerName(),googlePeopleApiCredentials);
			wentIn = true;
		}
		if (rs != null && rs.getStatusCode() != HttpStatus.OK && wentIn) {
			wentIn = false;
			return rs;
		}
		rs = null;
		if (ou.getOwnerContact() != null) {
			rs = updateOwnerOwnerContact(c, ownerId, ou.getOwnerContact(),googlePeopleApiCredentials);
			wentIn = true;
		}
		if (rs != null && rs.getStatusCode() != HttpStatus.OK && wentIn) {
			wentIn = false;
			return rs;
		}
		rs = null;
		if (ou.getOwnerEmail() != null) {
			rs = updateOwnerOwnerEmail(c, ownerId, ou.getOwnerEmail(),googlePeopleApiCredentials);
			wentIn = true;
		}
		if (rs != null && rs.getStatusCode() != HttpStatus.OK && wentIn) {
			wentIn = false;
			return rs;
		}
		rs = null;
		if (ou.getOwnerUserName() != null) {
			rs = updateOwnerOwnerUserName(c, ownerId, ou.getOwnerUserName(),googlePeopleApiCredentials);
			wentIn = true;
		}
		if (rs != null && rs.getStatusCode() != HttpStatus.OK && wentIn) {
			wentIn = false;
			return rs;
		}
		rs = null;
		/*
		 * if (ou.getOwnerPassword() != null) { rs = updateOwnerOwnerPassword(c,
		 * ownerId, ou.getOwnerPassword()); wentIn = true; } if (rs != null &&
		 * rs.getStatusCode() != HttpStatus.OK && wentIn) { wentIn = false; return rs; }
		 * rs = null;
		 */
		if (ou.getIsActive() != null) {
			rs = updateOwnerIsActive(c, ownerId, ou.getIsActive());
			wentIn = true;
		}
		if (rs != null && rs.getStatusCode() != HttpStatus.OK && wentIn) {
			wentIn = false;
			return rs;
		}
		rs = null;
		if (ou.getLastEditOn() != null) {
			rs = updateOwnerLastEditOn(c, ownerId, ou.getLastEditOn());
			wentIn = true;
		}
		if (rs != null && rs.getStatusCode() != HttpStatus.OK && wentIn) {
			wentIn = false;
			return rs;
		}
		rs = null;
		try {
			c.close();
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	public static String generateSecretCode(String contactNumber) {
		if (contactNumber.isEmpty()) {
			return "NEGATIVE";
		}
		if (contactNumber.isBlank()) {
			return "NEGATIVE";
		}
		Random r = new Random();
		String secretCode = ("" + r.nextInt(1000));
		String contactKey = contactNumber.substring(0, Math.min(contactNumber.length(), 6));

		return (secretCode + contactKey);
	}

	public ResponseEntity<Void> updateOwnerOwnerSecretKey(Long ownerId) {
		Boolean rs = false;
		OwnerCredentials oc = null;
		String secretKey = null;
		if (ownerId == null) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		try {
			oc = odi.selectOwnerCredentials(ownerId);
			secretKey = generateSecretCode(oc.getOwnerContact());
			if (secretKey.equals("NEGATIVE")) {
				return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			rs = odi.updateOwnerOwnerSecretKey(ConnectionProvider.getConnection(), ownerId, secretKey);
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (!rs) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}
		try {
			EmailAspect.sendEmailWithoutAttachment(oc.getOwnerEmail(), "TO", "CRM: Update SecretKey Request",
					"    <div id=\"BodyContents\" style=\"width: 100%;height: 30%;\">\r\n"
							+ "        <div id=\"Placeholder\" style=\"width: 100%;height: 2%;\"></div>\r\n"
							+ "        <div id=\"MainDiv\" style=\" width: 100%;height: 100%;align-self: center;  background-image: linear-gradient(to right, rgba(253, 179, 117, 0.3), rgba(255, 204, 163, 0.3), rgba(255, 224, 199, 0.3), rgba(232, 248, 252, 0.3), rgba(192, 216, 252, 0.5), rgba(142, 223, 255, 0.5));\">\r\n"
							+ "            <div id=\"Text\" style=\"font-size: large;color: rgb(71, 70, 70);font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center;\">\r\n"
							+ "                Hey " + oc.getUserName()
							+ " A new SecretKey Generation Request has Arrivied from your acount\r\n"
							+ "                <BR />\r\n" + "                <BR />\r\n"
							+ "                <BR /> Your Secret Key is:\r\n" + "                <BR />\r\n"
							+ "            </div>\r\n"
							+ "            <div id=\"SecretKeyText\" style=\"font-size: x-large;color: rgb(58, 57, 57); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center;\">\r\n"
							+ "                " + secretKey + "\r\n" + "            </div>\r\n"
							+ "            <div id=\"Note\" style=\" margin-top: 5%; font-size: small; color: rgb(102, 102, 102); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center\">\r\n"
							+ "                *Please keep this SecretKey private with you only do not share with anyone else\r\n"
							+ "            </div>\r\n" + "        </div>\r\n" + "    </div>",
					"text/html");
		} catch (MessagingException e) {
			return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	public ResponseEntity<Integer> sendOTPVerificationEmail(String emailId) {
		Integer otp = null;
		if (emailId.isEmpty()) {
			return new ResponseEntity<Integer>(otp, HttpStatus.BAD_REQUEST);
		}
		if (emailId.isBlank()) {
			return new ResponseEntity<Integer>(otp, HttpStatus.BAD_REQUEST);
		}
		try {
			Random r = new Random();
			otp = r.nextInt(10000);
			EmailAspect.sendEmailWithoutAttachment(emailId, "TO", "CRM: OTP Verification",
					"    <div id=\"BodyContents\" style=\"width: 100%;height: 30%; \">\r\n"
							+ "        <div id=\"Placeholder\" style=\"width: 100%;height: 2%;\"></div>\r\n"
							+ "        <div id=\"MainDiv\" style=\" width: 100%;height: 100%;align-self: center;  background-image: linear-gradient(to right, rgba(253, 179, 117, 0.3), rgba(255, 204, 163, 0.3), rgba(255, 224, 199, 0.3), rgba(232, 248, 252, 0.3), rgba(192, 216, 252, 0.5), rgba(142, 223, 255, 0.5));\">\r\n"
							+ "            <div id=\"Text\" style=\"font-size: large;color:rgb(102, 102, 102); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center;\">\r\n"
							+ "                <font color=\"rgb(6, 15, 139);\">This is an Email for OTP Verification</font>\r\n"
							+ "                <BR /> We had just received an Update request for your SecretKey Now!!!\r\n"
							+ "                <BR />\r\n" + "                <BR /> Your OTP is:\r\n"
							+ "                <BR />\r\n" + "            </div>\r\n"
							+ "            <div id=\"SecretKeyText\" style=\"font-size: x-large;color: rgb(58, 57, 57); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center;\">\r\n"
							+ "                " + otp + "\r\n" + "            </div>\r\n"
							+ "            <div id=\"Note\" style=\" margin-top: 5%; font-size: small; color: rgb(102, 102, 102); font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;text-align: center\">\r\n"
							+ "                *Please keep this OTP private with you only do not share with anyone else\r\n"
							+ "            </div>\r\n" + "        </div>\r\n" + "    </div>",
					"text/html");
		} catch (MessagingException e) {
			return new ResponseEntity<Integer>(otp, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Integer>(otp, HttpStatus.OK);
	}
	
	public ResponseEntity<OwnerDashboard> getOwnerDashboardDetails(Long ownerId) 
	{
		OwnerDashboard ownerDashboardDetails=new OwnerDashboard();
		try {
			ownerDashboardDetails=odi.getOwnerDashboardDetails(ownerId);
			ownerDashboardDetails.setTopExecutives(odi.getTopExecutives(ownerId));
		} catch (ClassNotFoundException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<OwnerDashboard>(ownerDashboardDetails,HttpStatus.NOT_FOUND);
		} catch (SQLException e) {
			logger.error("Exception: " + e.getMessage());
			return new ResponseEntity<OwnerDashboard>(ownerDashboardDetails,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<OwnerDashboard>(ownerDashboardDetails,HttpStatus.OK); 
	}

}

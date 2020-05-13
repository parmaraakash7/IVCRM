package ivgroup.master.database.dao.schema;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import ivgroup.master.database.dto.enquiry.EnquiryDetailsForNewProductTicketInsert;
import ivgroup.master.database.dto.enquiry.EnquiryInsert;
import ivgroup.master.database.dto.enquiry.SelectEnquiryDetailsByProductListId;

public interface EnquiryDAO 
{
	public SelectEnquiryDetailsByProductListId			selectEnquiryAndProductIdByProductListId(Long productListId)					throws SQLException,ClassNotFoundException;
	public Long 										addEnquiry(EnquiryInsert ei)													throws SQLException,ClassNotFoundException;
	public Boolean										addEnquiryProduct(Long enquiryId,Long productId) 								throws SQLException,ClassNotFoundException;
	public EnquiryDetailsForNewProductTicketInsert		selectEnquiryForNewProductTicketInsert(Long enquiryId)							throws SQLException,ClassNotFoundException;
	public Boolean										updateEnquiryClient(Connection c,Long enquiryId,Long clientId)					throws SQLException,ClassNotFoundException;
	public Boolean										updateEnquiryCountry(Connection c,Long enquiryId,Long countryId)				throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryState(Connection c,Long enquiryId,Long stateId)					throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryCity(Connection c,Long enquiryId,Long cityId)						throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryArea(Connection c,Long enquiryId,Long areaId)						throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryAddressLine1(Connection c,Long enquiryId,String addressLine1)		throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryAddressLine2(Connection c,Long enquiryId,String addressLine2)		throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryAddressLine3(Connection c,Long enquiryId,String addressLine3)		throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryLatitude(Connection c,Long enquiryId,String latitude)				throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryLongitude(Connection c,Long enquiryId,String longitude)			throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryPincode(Connection c,Long enquiryId,String pincode)				throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryEnquiryRemarks(Connection c,Long enquiryId,String enquiryRemarks)	throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryEnquiryType(Connection c,Long enquiryId,Long enquiryType)			throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryLastEditBy(Connection c,Long enquiryId,Long lastEditBy)			throws SQLException,ClassNotFoundException;
	public Boolean 										updateEnquiryLastEditOn(Connection c,Long enquiryId,Timestamp lastEditOn)		throws SQLException,ClassNotFoundException;
	public Boolean 										deleteEnquiryProduct(Long productId)											throws SQLException,ClassNotFoundException;
	public Boolean 										deleteEnquiry(Long enquiryId)													throws SQLException,ClassNotFoundException;
	
}
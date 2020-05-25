package ivgroup.master.database.dto.notification;

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;

public class NotificationSelect {

	@NotNull(message="CompanyExecutiveID cannot be null")
	Long CompanyExecutiveID;
	
	@NotNull(message="CompanyExecutiveName cannot be null")
	String CompanyExecutiveName;
	
	@NotNull(message="NotificationID cannot be null")
	Long NotificationID;
	
	@NotNull(message="NotificationSubject cannot be null")
	String NotificationSubject;
	
	@NotNull(message="NotificationDescription cannot be null")
	String NotificationDescription;
	
	@NotNull(message = "NotificationTime cannot be null")
	Timestamp NotificationTime;
	
	@NotNull(message="ReadIndex cannot be null")
	Boolean ReadIndex;

	public Long getCompanyExecutiveID() {
		return CompanyExecutiveID;
	}

	public void setCompanyExecutiveID(Long companyExecutiveID) {
		CompanyExecutiveID = companyExecutiveID;
	}

	public String getCompanyExecutiveName() {
		return CompanyExecutiveName;
	}

	public void setCompanyExecutiveName(String companyExecutiveName) {
		CompanyExecutiveName = companyExecutiveName;
	}

	public Long getNotificationID() {
		return NotificationID;
	}

	public void setNotificationID(Long notificationID) {
		NotificationID = notificationID;
	}

	public String getNotificationSubject() {
		return NotificationSubject;
	}

	public void setNotificationSubject(String notificationSubject) {
		NotificationSubject = notificationSubject;
	}

	public String getNotificationDescription() {
		return NotificationDescription;
	}

	public void setNotificationDescription(String notificationDescription) {
		NotificationDescription = notificationDescription;
	}

	public Timestamp getNotificationTime() {
		return NotificationTime;
	}

	public void setNotificationTime(Timestamp notificationTime) {
		NotificationTime = notificationTime;
	}

	public Boolean getReadIndex() {
		return ReadIndex;
	}

	public void setReadIndex(Boolean readIndex) {
		ReadIndex = readIndex;
	}

	public NotificationSelect(@NotNull(message = "CompanyExecutiveID cannot be null") Long companyExecutiveID,
			@NotNull(message = "CompanyExecutiveName cannot be null") String companyExecutiveName,
			@NotNull(message = "NotificationID cannot be null") Long notificationID,
			@NotNull(message = "NotificationSubject cannot be null") String notificationSubject,
			@NotNull(message = "NotificationDescription cannot be null") String notificationDescription,
			@NotNull(message = "NotificationTime cannot be null") Timestamp notificationTime,
			@NotNull(message = "ReadIndex cannot be null") Boolean readIndex) {
		super();
		CompanyExecutiveID = companyExecutiveID;
		CompanyExecutiveName = companyExecutiveName;
		NotificationID = notificationID;
		NotificationSubject = notificationSubject;
		NotificationDescription = notificationDescription;
		NotificationTime = notificationTime;
		ReadIndex = readIndex;
	}
	
	public NotificationSelect() {}
}
package pcu.vo;

import java.util.Date;
/**
 * 디바이스 등록 검증 키 
 * 
 * @author hyun
 *
 */
public class Device {
	Air lastAir;
	
	String did; 		//pk	
	

	String email; 		//device owner
	String authKey;		//vertification authKey
	
	double latitude;	//위도
    double longitude; 	//경도
    private String imageUrl;
    
    private String title;		//제목
    private String description;	//설명
    
    
    Date regDate;
    Date lastUpdate;
    
    public static final String NAME 		= "device"; // air id
    
    public static final String TITLE 		= "title"; // air id
    public static final String DESCRIPTION	= "description";
    
    public static final String DID 			= "did";
    public static final String EMAIL 		= "email";
    public static final String AUTHKEY 		= "authkey";
    public static final String LATITUDE		= "latitude";
    public static final String LONGITUDE	= "longitude";
    public static final String REGDATE		= "regdate";    
    public static final String LASTUPDATE	= "lastupdate";
    
    public static final String COMMAND		= "command";
    public static final Object COMM_CREATE 	= "create";
    public static final String COMM_UPDATE	= "update";
    public static final String COMM_DELETE	= "delete";
    public static final String COMM_READ	= "read";

	public static final String COMM_LIST 	= "list";

	
    
    
	public Device() {
		super();	
	}
	
	public Device(String title, String description , String did, String email, String authKey, double latitude, double longitude, 
			Date regDate,	Date lastUpdate) {
		super();
		this.did = did;
		this.email = email;
		this.authKey = authKey;
		this.latitude = latitude;
		this.longitude = longitude;
		this.regDate = regDate;
		this.lastUpdate = lastUpdate;
		this.title	= title;
		this.description= description;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getauthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Air getLastAir() {
		return lastAir;
	}

	public void setLastAir(Air lastAir) {
		this.lastAir = lastAir;
	}
	
	@Override
	public String toString() {
		return "Device [did=" + did + ", email=" + email + ", authKey=" + authKey + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", imageUrl=" + imageUrl + ", title=" + title + ", description="
				+ description + ", regDate=" + regDate + ", lastUpdate=" + lastUpdate + "]";
	}
	
	
}
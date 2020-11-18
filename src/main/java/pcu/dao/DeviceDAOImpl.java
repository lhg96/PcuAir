package pcu.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.datastore.Query.SortDirection;


import pcu.util.CustomUtil;
import pcu.vo.Air;
import pcu.vo.Device;
import pcu.vo.Result;

public class DeviceDAOImpl implements DeviceDAO {
	private static final Logger log = Logger.getLogger(DeviceDAOImpl.class.getName());
	
	int size = 100; //list 출력개수 
	
	private DatastoreService datastore;
	
	public DeviceDAOImpl() {
		System.out.println("---------init DeviceDao----------------");
		datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	private Device entityToDevice(Entity entity) {
		if (entity == null)
			return null;
		String title = (String) entity.getProperty(Device.TITLE);
		String description = (String) entity.getProperty(Device.DESCRIPTION);
		
		String did = (String) entity.getProperty(Device.DID);
		String email = (String) entity.getProperty(Device.EMAIL);
		String authKey = (String) entity.getProperty(Device.AUTHKEY);
		double latitude = (double) entity.getProperty(Device.LATITUDE);
		double longitude = (double) entity.getProperty(Device.LONGITUDE);

		//long regDate = (long) entity.getProperty(Device.REGDATE);
		//long lastUpdate = (long) entity.getProperty(Device.LASTUPDATE);
		//String regDateStr 	= CustomUtil.convertMilliSecondsToFormattedDate(regDate);
		//String lastUpdateStr = CustomUtil.convertMilliSecondsToFormattedDate(lastUpdate);
		
		Date regDate 	= null;
		Date lastUpdate = null;
		try {
			regDate = (Date) entity.getProperty(Device.REGDATE);
			lastUpdate = (Date) entity.getProperty(Device.LASTUPDATE);
		} catch (ClassCastException e) {
			log.info(e.toString());
		}

		Device device = new Device(title, description, did, email, authKey, latitude, longitude, regDate, lastUpdate);
		return device;
	}
	
	@Override
	public boolean isExist(String did) throws SQLException {
		boolean exist = true;
		Key key = KeyFactory.createKey(Device.NAME, did);
		Entity entity = null;		
		try {			
			entity = datastore.get(key);
			if(entity==null) exist = false;
		} catch (EntityNotFoundException e) {
			log.warning(e.toString());
			exist = false;
		}		
		return exist;
	}
	
	public List<Device> entitiesToDevice(Iterator<Entity> results) {
		List<Device> resultDevice = new ArrayList<>();
	    while (results.hasNext()) {  // We still have data
	      resultDevice.add(entityToDevice(results.next()));      // Add the Book to the List
	    }
	    return resultDevice;
	}
	
	
	@Override
	public String createDevice(Device device) throws SQLException {
		String 	authKey 	= generateAuthKey();
		//long 	regDate 	= new Date().getTime();
		//long 	lastUpdate 	= regDate;
		Date regDate 	= new Date();
		Date lastUpdate	= new Date();
		
		Key deviceKey = KeyFactory.createKey(Device.NAME, device.getDid());
		Entity entity = new Entity(deviceKey);
		
		entity.setProperty(Device.TITLE, 			device.getTitle());
		entity.setProperty(Device.DESCRIPTION, 		device.getDescription());	
		
		entity.setProperty(Device.DID, 			device.getDid());
		entity.setProperty(Device.EMAIL, 		device.getEmail());		
		entity.setProperty(Device.LATITUDE, 	device.getLatitude());
		entity.setProperty(Device.LONGITUDE, 	device.getLongitude());
		
		entity.setProperty(Device.AUTHKEY, 		authKey);		
		
		entity.setProperty(Device.REGDATE, 		regDate);
		entity.setProperty(Device.LASTUPDATE, 	lastUpdate);
		datastore.put(entity);	
		
		return device.getDid();
	}

	@Override
	public Device readDevice(String did) throws SQLException {
		Key key = KeyFactory.createKey(Device.NAME, did);
		Entity entity = null;
		Device device = null;
		try {
			entity = datastore.get(key);
			device = entityToDevice(entity);
		} catch (EntityNotFoundException e) {
			log.info("Not exist Device:" + key.toString());
		}				
		return device;
	}

	@Override
	public void updateDevice(Device device) throws SQLException {
		Date lastUpdate = new Date();
		
		Key deviceKey = KeyFactory.createKey(Device.NAME, device.getDid());
		Entity entity = new Entity(deviceKey);
		entity.setProperty(Device.TITLE, 			device.getTitle());
		entity.setProperty(Device.DESCRIPTION, 		device.getDescription());
		
		entity.setProperty(Device.DID, 			device.getDid());
		entity.setProperty(Device.EMAIL, 		device.getEmail());
		entity.setProperty(Device.AUTHKEY, 		device.getauthKey());
		entity.setProperty(Device.LATITUDE, 	device.getLatitude());
		entity.setProperty(Device.LONGITUDE, 	device.getLongitude());
		entity.setProperty(Device.REGDATE, 		device.getRegDate());
		entity.setProperty(Device.LASTUPDATE, 	lastUpdate);
		
		datastore.put(entity);		
	}

	@Override
	public void deleteDevice(String did) throws SQLException {
		Key deviceKey = KeyFactory.createKey(Device.NAME, did);
		datastore.delete(deviceKey);		
	}

	@Override
	public Result<Device> listDevices(String startCursor) throws SQLException {
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10); // Only show 10 at a time
		if (startCursor != null && !startCursor.equals("")) {
		      fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor)); // Where we left off
		}
		
		final Query query = new Query(Device.NAME)
				.addSort(Device.REGDATE, SortDirection.DESCENDING) // 날자역순으로 sorting
				;
		PreparedQuery preparedQuery = datastore.prepare(query);
	    QueryResultIterator<Entity> results = preparedQuery.asQueryResultIterator(fetchOptions);
	    
	    List<Device> resultDevice = entitiesToDevice(results);     // Retrieve and convert Entities
	    Cursor cursor = results.getCursor();              // Where to start next time
	    if (cursor != null && resultDevice.size() == size) {         // Are we paging? Save Cursor
	      String cursorString = cursor.toWebSafeString();               // Cursors are WebSafe
	      return new Result<>(resultDevice, cursorString);
	    } else {
	      return new Result<>(resultDevice);
	    }
	}
	
	private String generateAuthKey() {
		return UUID.randomUUID().toString();
	}

	

}

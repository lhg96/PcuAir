package pcu.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultIterator;

import pcu.util.ShardedCounter;
import pcu.vo.Air;
import pcu.vo.Result;


/**
 * 20190515 update modify Dao
 * 
 * @author arim-hyun
 *
 */
public class AirDAOImpl implements AirDAO {
	private static final Logger log = Logger.getLogger(AirDAOImpl.class.getName());

	//int size = 50; // list 출력 개수

	public AirDAOImpl() {
		System.out.println("---------init AirDao----------------");
	}

	/**
	 * convert to entity to object
	 * 
	 * @param entity
	 * @return
	 */
	private Air entityToAir(Entity entity) {
		if (entity == null)
			return null;
		String aid = (String) entity.getProperty(Air.AID);
		String did = (String) entity.getProperty(Air.DID);

		double dust10 = (double) entity.getProperty(Air.DUST10);
		double dust25 = (double) entity.getProperty(Air.DUST25);
		double dust100 = (double) entity.getProperty(Air.DUST100);

		double voc = (double) entity.getProperty(Air.VOC);
		double co = (double) entity.getProperty(Air.CO);
		double radon = (double) entity.getProperty(Air.RADON);
		double temp = (double) entity.getProperty(Air.TEMP);
		double hum = (double) entity.getProperty(Air.HUM);

		Date regDate = null;
		try {
			regDate = (Date) entity.getProperty(Air.REGDATE);
		} catch (ClassCastException e) {
			log.info(e.toString());
		}

		// String regDateStr = CustomUtil.convertMilliSecondsToFormattedDate(regDate);

		Air air = new Air(aid, did, dust10, dust25, dust100, voc, co, radon, hum, temp, regDate);

		return air;
	}

	private List<Air> entitiesToAir(Iterator<Entity> results) {
		List<Air> resultAir = new ArrayList<>();
		while (results.hasNext()) { // We still have data
			resultAir.add(entityToAir(results.next()));
		}
		return resultAir;
	}

	private String generateAID(String did) {
		ShardedCounter counter = new ShardedCounter(did);

		long index = counter.getCount();
		counter.increment();
		String outIndex = String.format("%08d", index);
		return did + outIndex;
	}

	@Override
	public String createAir(Air air) throws SQLException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		String aid = generateAID(air.getDid());
		// long regDate = new Date().getTime();
		Date regDate = new Date();
		air.setAid(aid);

		Key airKey = KeyFactory.createKey(Air.NAME, aid);
		Entity entity = new Entity(airKey);

		entity.setIndexedProperty(Air.AID, aid);
		entity.setIndexedProperty(Air.DID, air.getDid());
		entity.setIndexedProperty(Air.DUST10, air.getDust10());
		entity.setIndexedProperty(Air.DUST25, air.getDust25());
		entity.setIndexedProperty(Air.DUST100, air.getDust100());
		entity.setIndexedProperty(Air.VOC, air.getVoc());
		entity.setIndexedProperty(Air.CO, air.getCo());
		entity.setIndexedProperty(Air.RADON, air.getRadon());
		entity.setIndexedProperty(Air.HUM, air.getHum());
		entity.setIndexedProperty(Air.TEMP, air.getTemp());

		entity.setIndexedProperty(Air.REGDATE, regDate);
		datastore.put(entity);

		return air.getAid();
	}

	@Override
	public Air readAir(String aId) throws SQLException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Key airKey = KeyFactory.createKey(Air.NAME, aId);
		Entity entity = null;
		Air air = null;
		try {
			entity = datastore.get(airKey);
			air = entityToAir(entity);
		} catch (EntityNotFoundException e) {
			log.info("Not exist Device:" + airKey.toString());
		}
		return air;
	}

	@Override
	public void updateAir(Air air) throws SQLException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key airKey = KeyFactory.createKey(Air.NAME, air.getAid());
		Entity entity = new Entity(airKey);
		entity.setIndexedProperty(Air.AID, air.getAid());
		entity.setIndexedProperty(Air.DID, air.getDid());
		entity.setIndexedProperty(Air.DUST10, air.getDust10());
		entity.setIndexedProperty(Air.DUST25, air.getDust25());
		entity.setIndexedProperty(Air.DUST100, air.getDust100());
		entity.setIndexedProperty(Air.VOC, air.getVoc());
		entity.setIndexedProperty(Air.CO, air.getCo());
		entity.setIndexedProperty(Air.RADON, air.getRadon());
		entity.setIndexedProperty(Air.HUM, air.getHum());
		entity.setIndexedProperty(Air.TEMP, air.getTemp());
		entity.setIndexedProperty(Air.REGDATE, air.getRegDate());
		datastore.put(entity);
	}

	@Override
	public void deleteAir(String aId) throws SQLException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key airKey = KeyFactory.createKey(Air.NAME, aId);
		datastore.delete(airKey);
	}

	@Override
	public void deleteAll(String did) throws SQLException {
		final long start = System.currentTimeMillis();
		int deleted_count = 0;
		boolean is_finished = false;
		
		String kind = Air.NAME;
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter keyFilter = new FilterPredicate(Air.DID, FilterOperator.EQUAL, did);
		
		while (System.currentTimeMillis() - start < 16384) {
			final Query query = new Query(kind)
					.setFilter(keyFilter);
			
			query.setKeysOnly();
			final ArrayList<Key> keys = new ArrayList<Key>();
			for (final Entity entity : datastore.prepare(query).asIterable(FetchOptions.Builder.withLimit(128))) {
				keys.add(entity.getKey());
			}
			keys.trimToSize();

			if (keys.size() == 0) {
				is_finished = true;
				break;
			}
			while (System.currentTimeMillis() - start < 16384) {
				try {
					datastore.delete(keys);
					deleted_count += keys.size();
					break;

				} catch (Throwable ignore) {
					continue;
				}
			}
		}		
		log.info("*** deleted " + deleted_count + " entities form " + kind);
		//delete counter
		ShardedCounter counter = new ShardedCounter(did);
		counter.deleteAll();		
	}
	
	public Air getLastAir(String did) throws SQLException {
		Result<Air> result = listAirs(null, did, 1);
		List<Air> airList = result.result;
		if(!airList.isEmpty()) {
			return result.result.get(0);
		}else {
			return null;
		}
	}
	
	@Override
	public Result<Air> listAirs(String startCursor, String did, int size) throws SQLException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String kind = Air.NAME;
		
		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(size); // Only show 10 at a time
		if (startCursor != null && !startCursor.equals("")) {
		      fetchOptions.startCursor(Cursor.fromWebSafeString(startCursor)); // Where we left off
		}
		
		Filter keyFilter = new FilterPredicate(Air.DID, FilterOperator.EQUAL, did);
		
		Query query = new Query(kind)
				.setFilter(keyFilter)
				//.addFilter(Air.DID, FilterOperator.EQUAL, did)
				.addSort(Air.REGDATE, SortDirection.DESCENDING) // 날자역순으로 sorting		
				;
		
		PreparedQuery preparedQuery = datastore.prepare(query);
	    QueryResultIterator<Entity> results = preparedQuery.asQueryResultIterator(fetchOptions);
	    
	    List<Air> resultAir = entitiesToAir(results);     // Retrieve and convert Entities
	    Collections.reverse(resultAir);
	    Cursor cursor = results.getCursor();              // Where to start next time
	    if (cursor != null && resultAir.size() == size) {         // Are we paging? Save Cursor
	      String cursorString = cursor.toWebSafeString();               // Cursors are WebSafe
	      return new Result<>(resultAir, cursorString);
	    } else {
	      return new Result<>(resultAir);
	    }
	}

}

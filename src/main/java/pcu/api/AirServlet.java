package pcu.api;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pcu.dao.AirDAOImpl;
import pcu.vo.Air;
import pcu.vo.Result;

/**
 * 
 * 2019 spring project air https://www.youtube.com/watch?v=HDBPmYFVQho
 * 
 * test url
 * get insert
 * http://localhost:8080/air?did=a101&dust25=10&voc=200
 * 
 * post
 * http://localhost:8080/air
 * command:view
 * did:a101
 * 
 * 20200220 update c-post, s - put , d-delete, r - get
 * 
 * 
 * @author arim-hyun
 *
 */
@WebServlet(name = "air", urlPatterns = { "/api/air" })
public class AirServlet extends HttpServlet {	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(AirServlet.class.getName());
	
	Gson gson;

	@Override
	public void init() throws ServletException {		
		super.init();
		System.out.println("---------init APT servlet-----------------");
		gson 	= new GsonBuilder().setPrettyPrinting().create();
		AirDAOImpl airDao = new AirDAOImpl();
		this.getServletContext().setAttribute("airDao", airDao);			
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {		
		
	}
	

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		try {
			createAir(request, response);		
		} catch (SQLException e) {
			log.warning(e.getMessage());
		}		
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		AirDAOImpl airDao = (AirDAOImpl) this.getServletContext().getAttribute("airDao");
		
		String did = request.getParameter(Air.DID);
		String aid = request.getParameter(Air.AID);
		String command = request.getParameter(Air.COMMAND);

		if (command.equals(Air.COMM_DELETE)) {
			if (aid == null) {
				responseMessage(response, false, "no aid");
				return;
			}
			
			try {
				airDao.deleteAir(aid);
				responseMessage(response, true, aid);
			} catch (SQLException e) {
				log.warning(e.toString());
				responseMessage(response, false, e.toString());
			}		
			
		}else if (command.equals(Air.COMM_DELETE_ALL)) {
			if (did == null) {
				responseMessage(response, false, "no did");
				return;
			}
			try {
				airDao.deleteAll(did);
				responseMessage(response, true, did);
			} catch (SQLException e) {
				log.warning(e.toString());
				responseMessage(response, false, e.toString());
			}
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
		String did = request.getParameter(Air.DID);
		String aid = request.getParameter(Air.AID);
		String command = request.getParameter(Air.COMMAND);
		//String period 	= request.getParameter(Air.PERIOD);
		String cursor	= request.getParameter(Air.CURSOR);
		
		AirDAOImpl airDao = (AirDAOImpl) this.getServletContext().getAttribute("airDao");
				
		if (command == null) {
			responseMessage(response, false, "no command");
			return;
		}
		
		try {
			if (command.equals(Air.COMM_LIST)) {
				if (did == null) {
					responseMessage(response, false, "no did");
					return;
				}
				Result<Air> airs  = null;
				airs = airDao.listAirs(cursor,did, 180); //50 set			
				response(response, airs);
			}else if (command.equals(Air.COMM_LAST)) {
				if (did == null) {
					responseMessage(response, false, "no did");
					return;
				}
				Air air = airDao.getLastAir(did); 
				response(response, air);
			}else if (command.equals(Air.COMM_READ)) {
				if (aid == null) {
					responseMessage(response, false, "no aid");
					return;
				}
				Air air = airDao.readAir(aid);
				response(response, air);			
			} 			
		}catch(SQLException e) {
			log.warning(e.toString());
			responseMessage(response, false, e.toString());
		} 
		
	}


	// --------------------------------------------------------------------------------------
	private void responseMessage(final HttpServletResponse response, boolean isSuccess, String message)
			throws IOException {
		response(response, new ResonseMessage(isSuccess, message));
	}

	private void response(final HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		response.getWriter().println(gson.toJson(obj));
	}

	class ResonseMessage {
		boolean success;
		String message;

		public ResonseMessage(boolean success, String message) {
			super();
			this.success = success;
			this.message = message;
		}

	}

	private void createAir(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		String did 	= request.getParameter(Air.DID);		
		if(did==null) {
			responseMessage(response,false,"no did");
			return;
		}	
		double dust10 	= convertDouble(request.getParameter(Air.DUST10));
		double dust25 	= convertDouble(request.getParameter(Air.DUST25)); 
		double dust100 	= convertDouble(request.getParameter(Air.DUST100));
		double voc 		= convertDouble(request.getParameter(Air.VOC));
		double co 		= convertDouble(request.getParameter(Air.CO));
		double radon	= convertDouble(request.getParameter(Air.RADON));
		double hum 		= convertDouble(request.getParameter(Air.HUM));
		double temp		= convertDouble(request.getParameter(Air.TEMP));
		
		
		Air air = new Air();
		//no aid, regdate
		air.setDid(did);
		air.setDust10(dust10);
		air.setDust25(dust25);
		air.setDust100(dust100);
		air.setVoc(voc);
		air.setCo(co);
		air.setRadon(radon);
		air.setHum(hum);
		air.setTemp(temp);		
		
		AirDAOImpl airDao = (AirDAOImpl) this.getServletContext().getAttribute("airDao");
		String aid = airDao.createAir(air);
		
		air = airDao.readAir(aid);		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(gson.toJson(air));			
		
	}
	
	private double convertDouble(String inStr) {
		double value = 0;
		if (inStr != null && inStr.trim().length() > 0) {
			try {
				value = Double.parseDouble(inStr);
			} catch (NumberFormatException e) {
				System.out.println(e.toString());
			}
		}
		return value;
	}

	
	
	public String ConvertMilliSecondsToFormattedDate(long milliSeconds) {
		String dateFormat = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return simpleDateFormat.format(calendar.getTime());
	}
	
	
	
	// ----------CRUD------------
	/*
	private Entity getEntity(Key key) {
		Entity entity = null;
		try {
			entity = datastore.get(key);
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return entity;
	}

	private List<Air> readAir(String did, String period, int amount) {
		List<Air> airs = new ArrayList<>();
		final Query q = new Query(Air.NAME).addSort(Air.REGDATE, SortDirection.DESCENDING) // 날자역순으로 sorting
				.setFilter(new FilterPredicate(Air.DID, FilterOperator.EQUAL, did));

		PreparedQuery pq = datastore.prepare(q);

		if (period == null) {
			List<Entity> entitys = pq.asList(FetchOptions.Builder.withLimit(amount)); // Retrieve up to five posts
			entitys.forEach(entity -> {
				Air air = entityToObject(entity);
				airs.add(air);
			});
		} else if (period.equals("sec")) {
			airs.addAll(average(pq.asIterable(), 1000, amount));
		} else if (period.equals("min")) {
			airs.addAll(average(pq.asIterable(), 1000 * 60, amount));
		} else if (period.equals("hour")) {
			airs.addAll(average(pq.asIterable(), 1000 * 60 * 60, amount));
		} else if (period.equals("day")) {
			airs.addAll(average(pq.asIterable(), 1000 * 60 * 60 * 30, amount));
		}
		return airs;
	}

	private void delete(Key key) {
		datastore.delete(key);
	}

	private boolean deleteAll(String did) {
		// AsyncMemcacheService memcache =
		// MemcacheServiceFactory.getAsyncMemcacheService();
		// memcache.clearAll()
		try {
			final Query q = new Query(Air.NAME).setFilter(new FilterPredicate(Air.DID, FilterOperator.EQUAL, did));
			PreparedQuery pq = datastore.prepare(q);
			pq.asIterable().forEach(entity -> {
				datastore.delete(entity.getKey());
			});
		} catch (Exception e) {
			log.warning(e.toString());
			return false;
		}
		return true;
	}
	*/
	
	/*
	 aggregate 기능은 잠시 중지
	private List<Air> average(final Iterable<Entity> entitys, long period, int amount) {
		List<Air> avergageAirs = new ArrayList<>();

		// AtomicInteger atomInt = new AtomicInteger(0);
		// AtomicLong atomLong = new AtomicLong(0);
		long tempTime = 0;
		int tempCount = 0;

		int totalCount = 0;
		int avgCount = 0;
		for (Entity entity : entitys) {
			long time = (long) entity.getProperty(Air.REGDATE);
			if (Math.abs(time - tempTime) > period) {
				tempTime = time;
				if (++tempCount <= amount) {
					// 나중에 평균으로 계산할것
					// 시간 평균으로 수정할것
					Air air = entityToObject(entity);
					// -----
					avergageAirs.add(air);

				} else {
					avgCount++;					
					break;
				}
			} else {

			}
		}
		return avergageAirs;
	}*/
}
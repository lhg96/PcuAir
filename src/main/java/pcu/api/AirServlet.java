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
	
	
}
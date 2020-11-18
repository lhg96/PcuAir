package pcu.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pcu.dao.DeviceDAO;
import pcu.dao.DeviceDAOImpl;
import pcu.util.CustomUtil;
import pcu.vo.Device;
import pcu.vo.Result;

/**
 * 
 * example : http://www.vogella.com/tutorials/GoogleAppEngineJava/article.html
 * 
 * 
 * input 값만 있기 때문에 별도의 인증절차는 필요없음 data save 절차 20181204
 * 
 * inert example get
 * http://localhost:8080/api/device?did=a102&email=aaa@gmail.com&latitude=100&longitude=200
 * 
 * post
 * 
 * 20200220 update c-post, s - put , d-delete, r - get
 * 
 * @author hyun
 *
 */
@WebServlet(name = "DeviceServlet", urlPatterns = { "/api/device" })
public class DeviceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(DeviceServlet.class.getName());
	
	Gson gson;	

	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("---------init APT servlet-----------------");		
		gson = new GsonBuilder().setPrettyPrinting().create();
		DeviceDAO deviceDao = new DeviceDAOImpl();
		this.getServletContext().setAttribute("deviceDao", deviceDao);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DeviceDAO deviceDao = (DeviceDAO) this.getServletContext().getAttribute("deviceDao");
		String command 	= request.getParameter(Device.COMMAND);				

		String title = request.getParameter(Device.TITLE);
		String description = request.getParameter(Device.DESCRIPTION);
		
		String did = request.getParameter(Device.DID);
		String authKey = request.getParameter(Device.AUTHKEY);
		
		String email = request.getParameter(Device.EMAIL);
		double latitude = CustomUtil.convertDouble(request.getParameter(Device.LATITUDE));
		double longitude = CustomUtil.convertDouble(request.getParameter(Device.LONGITUDE));
		
		
		if (did == null) {
			responseMessage(response, false, "no did");
			return;
		}
		if (command == null) {
			responseMessage(response, false, "no command");
			return;
		}
		
		if (command.equals(Device.COMM_UPDATE)) {
			try {
				Device device = deviceDao.readDevice(did);
				
				device.setTitle(title);
				device.setDescription(description);
				device.setEmail(email);
				device.setLatitude(latitude);
				device.setLongitude(longitude);
				
				deviceDao.updateDevice(device);
				response(response, device);
				// responseMessage(response, true, did);
			}catch (SQLException e) {
				log.warning(e.toString());
				responseMessage(response, false, e.toString());
			}
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DeviceDAO deviceDao = (DeviceDAO) this.getServletContext().getAttribute("deviceDao");
		
		String command = request.getParameter(Device.COMMAND);		
		String did = request.getParameter(Device.DID);
		
		if (did == null) {
			responseMessage(response, false, "no did");
			return;
		}
		if (command == null) {
			responseMessage(response, false, "no command");
			return;
		}
		
		if (command.equals(Device.COMM_DELETE)) {
			try {
				if (!deviceDao.isExist(did)) {
					responseMessage(response, false, "no entity");					
				}else {
					deviceDao.deleteDevice(did);
					responseMessage(response, true, did);
				}
			}catch (SQLException e) {
				log.warning(e.toString());
				responseMessage(response, false, e.toString());
			}
		}else {
			responseMessage(response, false, "wrong command");
		}
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String command = request.getParameter(Device.COMMAND);		
		
		String title = request.getParameter(Device.TITLE);
		String description = request.getParameter(Device.DESCRIPTION);
		
		String did = request.getParameter(Device.DID);
		String authKey = request.getParameter(Device.AUTHKEY);
		
		String email = request.getParameter(Device.EMAIL);
		double latitude = CustomUtil.convertDouble(request.getParameter(Device.LATITUDE));
		double longitude = CustomUtil.convertDouble(request.getParameter(Device.LONGITUDE));
		
		DeviceDAO deviceDao = (DeviceDAO) this.getServletContext().getAttribute("deviceDao");
		
		
		if (command == null) {
			responseMessage(response, false, "no command");
			return;
		}
		
		try {
			/*
			 * 인증키는 학교 버젼에서는 절반만 사용 if(authKey==null) {
			 * responseMessage(response,false,"no authKey"); return; }
			*/		
			if(command.equals(Device.COMM_LIST)) {			
				Result<Device> resultList = null;			
				resultList = deviceDao.listDevices("");//mfix					
				response(response,resultList);
				return;
			}else if (command.equals(Device.COMM_READ)) {	
				if (did == null) {
					responseMessage(response, false, "no did");
					return;
				}
				if (!deviceDao.isExist(did)) {
					responseMessage(response, false, "no entity");				
				}else {
					Device device = deviceDao.readDevice(did);
					response(response, device);
					// responseMessage(response, true, did);
				}
			}else {
				responseMessage(response, false, "wrong command");
			}
		}catch(SQLException e) {
			log.warning(e.toString());
			responseMessage(response, false, e.toString());
		}
	}	
	
	/**
	 * create object
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			createDevice(request, response);
		} catch (SQLException e) {			
			e.printStackTrace();
		}		

	}

	private void response(final HttpServletResponse response, Object obj) throws IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(gson.toJson(obj));
	}
	
	private void createDevice(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
		String title = request.getParameter(Device.TITLE);
		String description = request.getParameter(Device.DESCRIPTION);
		
		String did = request.getParameter(Device.DID);
		String email = request.getParameter(Device.EMAIL);
		double latitude = CustomUtil.convertDouble(request.getParameter(Device.LATITUDE));
		double longitude = CustomUtil.convertDouble(request.getParameter(Device.LONGITUDE));

		if (email == null)
			email = "guest";
		if (did != null) {
			Device device = new Device();
			device.setTitle(title);
			device.setDescription(description);
			device.setDid(did);
			device.setEmail(email);
			device.setLatitude(latitude);
			device.setLongitude(longitude);
			
			DeviceDAO deviceDao = (DeviceDAO) this.getServletContext().getAttribute("deviceDao");			
			boolean exist = deviceDao.isExist(did);			
			// regist device
			if (!exist) {				
				deviceDao.createDevice(device);
				device = deviceDao.readDevice(did);
				response(response, device);
			}else {
				responseMessage(response, false, "exist Device");
			}
		

		} else {
			// response(response,false);
			responseMessage(response, false, "please input device ID.");
		}
	}
	
	private void responseMessage(final HttpServletResponse response, boolean isSuccess, String message)
			throws IOException {
		response(response, new ResonseMessage(isSuccess, message));
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
}

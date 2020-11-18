package pcu.api;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import pcu.dao.AirDAO;
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
 * 
 * @author arim-hyun
 *
 */
@WebServlet(name = "time", urlPatterns = { "/api/time" })
public class NTPTime extends HttpServlet {	
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(NTPTime.class.getName());
	
	Gson gson;

	@Override
	public void init() throws ServletException {		
		super.init();
		System.out.println("---------init APT servlet-----------------");
		gson 	= new GsonBuilder().setPrettyPrinting().create();			
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException {
		
		TimeZone zone = TimeZone.getTimeZone("Asia/Seoul");
		Calendar cal = Calendar.getInstance(zone);	
		
		int hour = cal.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
		int min = cal.get(Calendar.MINUTE); // gets hour in 24h format
		int day = cal.get(Calendar.DAY_OF_WEEK );
		
		String message = hour+"/"+min+"/"+day;
		
		response.setContentType("text/plain;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");		
		response.getWriter().println(message);
		//response.getWriter().println(min);
		//response.getWriter().println(day);
	}
	
	class RATime{
		int hour;
		int min;
		int day;		
		public RATime(int hour, int min, int day) {
			super();
			this.hour = hour;
			this.min = min;
			this.day = day;
		}
		public int getHour() {
			return hour;
		}
		public void setHour(int hour) {
			this.hour = hour;
		}
		public int getMin() {
			return min;
		}
		public void setMin(int min) {
			this.min = min;
		}
		public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
	}
}
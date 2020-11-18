package pcu.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomUtil {
	public static String convertMilliSecondsToFormattedDate(long milliSeconds) {
		String dateFormat = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return simpleDateFormat.format(calendar.getTime());
	}
	


	public static double convertDouble(String inStr) {
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
}

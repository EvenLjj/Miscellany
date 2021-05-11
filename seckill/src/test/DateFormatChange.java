package test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatChange {
	
	private  static String format="yyyy-MM-dd hh:mm:ss";
	
	public static void main(String[] args){
		long time=1472182502193l;
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		Date date=new Date(time);
		String result=sdf.format(date);
		System.out.println(result);
	}
}

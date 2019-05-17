package cn.yunrui.intfirectrlsys.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.yunrui.intfirectrlsys.util.Util;

/**
 * 
 * @author Administrator 测试用
 *
 */
public class Test5 {
	public static void main(String[] args) throws ParseException{
	
	String a=Util.getcurYear()+"-"+Util.getcurMonth()+"-"+"01"+" 00:00:00";
	String b=Util.getDate()+" "+"23:59:59";
	SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date  date0 = formatter.parse(a);
	Date  date=new Date();
	 System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAstartTimeAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA "+b);
	 SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 Calendar calendar = Calendar.getInstance();
	 Date today = calendar.getTime();
	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	 String result = format.format(today);
     String T = result + " " + "00:00:00";
     String M = result + " " + "59:59:59";  ////endTime Mon Apr 22 23:59:59 CST 2019  ;  endTime Wed Apr 24 11:59:59 CST 2019
     Date startTime = sdf2.parse(T);
     Date endTime = sdf2.parse(M);
     System.out.println("startTime "+startTime);
     System.out.println("endTime "+endTime);
}
}
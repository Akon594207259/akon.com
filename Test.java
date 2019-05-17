package cn.yunrui.intfirectrlsys.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//测试用

/**
 * 
 * @author Administrator
 *
 */
public class Test {
	
	public static void main(String[] args) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
			for (int i = 2018; i <= 2099; i++) {
			Map<String,Object> map = new HashMap<String, Object>();	
				map.put("year",i);
				list.add(map);
			}
	}
	
	
	public static List<Map<String,Object>> getYear(){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		for (int i = 1999; i <= 2099; i++) {
		Map<String,Object> map = new HashMap<String, Object>();	
			map.put("year",i);
			list.add(map);
		}
	     	return list;
	
	}
	
	public static String getDate(String date) throws ParseException{
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");    
	     SimpleDateFormat sdw = new SimpleDateFormat("dd");    
	     Date da = new Date();  
	     da = sd.parse(date);
	     String test = sdw.format(da);
	     if(test.startsWith("0")) {
	     	test = test.replaceFirst("0","");
		}
	     	return test;
	
	}
	public static List getZb_Column(String ym)throws Exception{
	
	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");    
	
	Calendar calendar = Calendar.getInstance();  
   
	calendar.setTime(sd.parse(ym));  
  
	System.out.println(calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
   
	StringBuffer zb = new StringBuffer();

	
	List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
	for(int i=1;i<=calendar.getActualMaximum(Calendar.DAY_OF_MONTH);i++){
		
    	String weekday=getWeekday(ym+"-"+i);
    	Map<String, Object> aa = new HashMap<String, Object>();
    	for (int j = 0; j <= list.size(); j++) {
    		aa.put("day",i);
    		aa.put("week",weekday);
		}
    	list.add(aa);
	}
	
    	return list;
		
	}
	 public static String getWeekday(String date){
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");    
        SimpleDateFormat sdw = new SimpleDateFormat("E");    
        Date d = null;    
        try {    
            d = sd.parse(date);    
        } catch (Exception e) {    
            e.printStackTrace();    
        }  
        return sdw.format(d);  
     } 

	 public static	int getDay() throws ParseException{
		 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		 String date = "2018-04";
		 Date riqi =  formatter.parse(date);
			Calendar calendar = Calendar.getInstance(); 
			calendar.setTime(riqi);
			int d =  calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  
			int e = d+1;
			System.out.println("看一看这是几天啊"+d);
		 return e;
	 }
	 
	  public static int day_difference(String date1, String date2 ) throws ParseException {
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
	        //跨年的情况会出现问题哦
	        //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 1
	        Date fDate=sdf.parse(date1);
	        Date oDate=sdf.parse(date2);
	        Calendar aCalendar = Calendar.getInstance();
	        aCalendar.setTime(fDate);
	        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	        aCalendar.setTime(oDate);
	        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	        int result=day2-day1;
	        return result;
	    }
	  
	  public static String getNewMonth(String s) throws ParseException{
		  
		  
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");
			Date da = sd.parse(s);
			  Calendar   calendar   =   new   GregorianCalendar(); 
			  calendar.setTime(da); 
			  calendar.add(calendar.MONTH, 1);
			  da=calendar.getTime(); 
			  SimpleDateFormat ff = new SimpleDateFormat("yyyy-MM");
			  String www = ff.format(da);
			 String result =  www +"-01";
			 return result;
	  }
	 
}

 


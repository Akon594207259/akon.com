package cn.yunrui.intfirectrlsys.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Administrator
 *
 */
public class Test1 {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

//		for (int i = 0; i < 6; i++) {
//
//			Calendar cal4 = Calendar.getInstance();
//			cal4.set(5, 1);
//			cal4.set(11, 0);
//			cal4.set(12, 0);
//			cal4.set(13, 0);
//			cal4.add(2, i - i * 2 + 1);
//			Date endTime4 = cal4.getTime();
//			cal4.add(2, i - i * 2);
//			Date startTime4 = cal4.getTime();
//
//			System.err.println("endTime4" + endTime4);
//
//		}
		 Calendar cal = Calendar.getInstance();
         cal.set(11, 0);
         cal.set(12, 0);
         cal.set(13, 0);
         Date startTime1 = cal.getTime();
         SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd");
         System.out.println("开始时间"+format.format(startTime1));
         
         cal.add(5, 1);
         Date endTime1 = cal.getTime();
        
         System.out.println("结束时间"+format.format(endTime1));
	}
}

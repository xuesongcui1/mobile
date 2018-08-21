package com.hoomsun.mobile.tools;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {
	/**
	 * 获得上个月
	 * @param
	 * @param
	 * @return
	 */

	public static  String beforMonth(int  a){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
	    c.add(Calendar.MONTH, -a);
	    Date m = c.getTime();
	    String mon = format.format(m);
		return mon;
	}
	
	/**
	 * 获取当前时间（年月日时分秒）
	 * @return
	 */
	public static String currentTime(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日  hh:mm:ss");
	    String mon = format.format(new Date());
		return mon;
	}
	
	/**
	 * 获取n年前为某年
	 * @return
	 */
	public static String beforeYear(int n){
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.YEAR, -n);
		Date date = c.getTime();
		String year = format.format(date);
		return year;
	}
	
	/**
	 * 获取当前时间，单位秒
	 * @return
	 */
	public static long getCurrentTime(){
		return new Date().getTime();
	}
	
	/**
	 * 获取三个月之前的时间，单位秒
	 * @return
	 */
	public static long getBeforeTime(){
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, -6);
		return c.getTime().getTime();
	}
	
	/**
	 * 获取当前时间（格式：yyyyMM）
	 * @return
	 */
	public static String getCurrentDate(){
		SimpleDateFormat sim = new SimpleDateFormat("yyyyMM");
		Calendar calendar = Calendar.getInstance();
		String lastTime = sim.format(calendar.getTime());
		return lastTime;
	}

	/**
	 * 判断当前时间是否为运营商账单日
	 * @return
	 */
	public static boolean isBillDate() {
		boolean flag=false;
		long time = System.currentTimeMillis();
		String currentTime = new SimpleDateFormat("yyyyMMdd").format(new Date(time));
		String substring = currentTime.substring(6);
		int day = Integer.parseInt(substring);
		if(day>3){
			flag=false;
		}else{
			flag=true;
		}
		return flag;
	}
	
	 public static String dateFormat(String date) {
		    
	    if(date.length() == 8) {
	      date = date.substring(0, 4) + "/" + date.substring(4,6) + "/" + date.substring(6,8);
	    }
	    if(date.contains("-")) {
	      date = date.replace("-", "/");
	    }else if(date.contains("年")) {
	      date = date.replace("年", "/").replace("月", "/").replace("日", "");
	    }
	    return date;
	  }
	 
	 /**
	  * 获取当前时间 格式 yyyy-MM-dd
	  * @param date
	  * @return  
	  * @Description:
	  */
	 public static String currentDateToString() {
		 SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar calendar = Calendar.getInstance();
		 String lastTime = sim.format(calendar.getTime());
		 return lastTime;
	 }
	 
	 
	 /**
	  * 获取n月前的那个月份  yyyy-MM
	  * @param date
	  * @return  
	  * @Description:
	  */
	 public static String parseDateToStringM(int i) {
		 SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM");
		 Calendar calendar = Calendar.getInstance();
		 calendar.add(Calendar.MONTH, i);
		 calendar.setTime(new Date());
		 String lastTime = sim.format(calendar.getTime());
		 return lastTime;
	 }
	 
	 /**
	  * 获取n月前的那个月份的第一天格式  yyyy-MM-dd
	  * @param date
	  * @return  
	  * @Description:
	  */
	 public static String parseDateToStringB(int i) {
		 SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar calendar = Calendar.getInstance();
		 calendar.add(Calendar.MONTH, i);
		 calendar.setTime(new Date());
		 calendar.set(Calendar.DAY_OF_MONTH,1);
		 String lastTime = sim.format(calendar.getTime());
		 return lastTime;
	 }
	 
	 /**
	  * 获取n月前的那个月份的最后一天格式 yyyy-MM-dd
	  * @param date
	  * @return  
	  * @Description:
	  */
	 public static String parseDateToStringE(int i) {
		 SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
		 Calendar calendar = Calendar.getInstance();
		 calendar.add(Calendar.MONTH, i);
		 calendar.setTime(new Date());
		 calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); 
		 String lastTime = sim.format(calendar.getTime());
		 return lastTime;
	 }
	 
	 /**
	  * 获取当前时间 格式Tue Aug 21 2018 03:30:12 GMT+0800 (中国标准时间)
	  * @param date
	  * @return  
	  * @Description:
	  */
	 public static String parseCurrentDateToString() {
		 String  str =new Date().toString();
		 String[] array = str.split(" ");
		 String result = "Tue "+array[1]+" "+array[2]+" "+array[5]+" "+array[3]+" GMT+0800 (中国标准时间)";
		return result;
	 }
}	


package com.hoomsun.mobile.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GetMonth {
	/**
	 * 获得yyyy/MM格式的上个num个月
	 * @param year
	 * @param
	 * @return
	 */

	public static  String beforMonth(int year,int nowMonth,int num ){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy/MM" );
		 cal.set(Calendar.YEAR,year);
		 cal.set(Calendar.MONTH, nowMonth-1);
		 cal.add(Calendar.MONTH, -num);//从现在算，之前一个月,如果是2个月，那么-1-----》改为-2
		return sdf.format(cal.getTime());
			
	}

	/**
	 * 获得yyyy/MM格式的上个num个月
	 * @param year
	 * @param
	 * @return
	 */

	public static  String beforMonth1(int year,int nowMonth,int num ){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy/MM/dd" );
		cal.set(Calendar.YEAR,year);
		cal.set(Calendar.MONTH, nowMonth-1);
		cal.add(Calendar.MONTH, -num);//从现在算，之前一个月,如果是2个月，那么-1-----》改为-2
		return sdf.format(cal.getTime());

	}

	/**
	 * 获得当前月
	 * @param
	 * @param
	 * @return
	 */

	public static  String nowMonth(){
		Date date=new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMM" );
		return sdf.format(date);
	}	
	
	
	/**
	 * 获得今天
	 * @param
	 * @param
	 * @return
	 */

	public static  String today(){
		Date date=new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMMdd" );
		return sdf.format(date);
	}
	/**
	 * 获得今天
	 * @param
	 * @param
	 * @return
	 */

	public static  String today1(){
		Date date=new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy-MM-dd" );
		return sdf.format(date);
	}
	/**
	 * 获得上个月
	 * @param year
	 * @param
	 * @return
	 */

	public static  String beforMon(int year,int nowMonth,int num ){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMM" );
		 cal.set(Calendar.YEAR,year);
		 cal.set(Calendar.MONTH, nowMonth-1);
		 cal.add(Calendar.MONTH, -num);//从现在算，之前一个月,如果是2个月，那么-1-----》改为-2
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 获得上个月
	 * @param year
	 * @param
	 * @return
	 */

	public static  String beforMont(int year,int nowMonth,int num ){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyy-MM" );
		 cal.set(Calendar.YEAR,year);
		 cal.set(Calendar.MONTH, nowMonth-1);
		 cal.add(Calendar.MONTH, -num);//从现在算，之前一个月,如果是2个月，那么-1-----》改为-2
		return sdf.format(cal.getTime());
	}
	
	/**
	 * 一个月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String lastDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);

		return sdf.format(cal.getTime());
	}

	/**
	 * 一个月的最后一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String lastDateOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);

		return sdf.format(cal.getTime());
	}
	
	
	
	/**
	 * 一个月的第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String firstDateOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return sdf.format(cal.getTime());
	}


	/**
	 * 一个月的第一天
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String firstDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month - 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return sdf.format(cal.getTime());
	}

	
	
	
	/**
	 * n分钟后
	 * @return
	 */
	public static  Calendar  afterDate(int n){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Calendar nowTime = Calendar.getInstance();
		 nowTime.add(Calendar.MINUTE, n);//n分钟后的时间
		return nowTime;
	}
	/**
	 * 获得当年月
	 * @return
	 */
	public static int[] nowYearMonth(){
		int[] day=new int[2];
		Date date=new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat( "yyyyMM" );
		String result=sdf.format(date);
		day[0]=new Integer(result.substring(0, 4)) ;
		
		day[1]=new Integer(result.substring(4)) ;
		return day;
		
	}
	/**
	 * 获取从当前月开始的前六个月，的第一天以及最后一天   格式yyyyMMdd
	 * @return
	 */
	public static List<String> getQueryMonth(){
		List<String> mothList = new ArrayList<String>(20);
		//获取当年年月日
		Date date=new Date();
		SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");
		String result=sdf.format(date);
		//截取年
		String nowYear = result.substring(0,4);
		//截取月
		String nowMonth = result.substring(4,6);
		//获取从当前月开始的前六个月     的第一天以及最后一天   下标双数未每月第一天    单数为每月最后一天
		for(int i=0;i<6;i++) {
			String beforeMonth = beforMon(Integer.parseInt(nowYear),Integer.parseInt(nowMonth),i);
			String year = beforeMonth.substring(0,4);
			String month = beforeMonth.substring(4);
			String StartTime = firstDate(Integer.parseInt(year),Integer.parseInt(month));
			String endTime = lastDate(Integer.parseInt(year),Integer.parseInt(month));
			mothList.add(StartTime);
			mothList.add(endTime);
		}
		mothList.set(1, result);
		return mothList;
		
	}
	
	
}

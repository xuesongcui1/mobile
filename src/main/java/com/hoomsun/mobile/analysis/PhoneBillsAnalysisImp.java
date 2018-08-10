package com.hoomsun.mobile.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 移动数据解析
 * @author 崔
 *
 */
public class PhoneBillsAnalysisImp{

	private Logger logger = LoggerFactory.getLogger(PhoneBillsAnalysisImp.class);
	

	public List<Map<String, String>> analysisJson(List<String> data,
			String phoneNumber, String... agrs) {
		//logger.warn("------------移动解析数据开始----data为：" + JSONArray.fromObject(data));
		
		List<Map<String, String>> list=new ArrayList<>();
    	for (int i = 0; i < data.size(); i++) {
    		List<Map<String, String>> list1=new ArrayList<>();
    		try {
    			JSONObject json1=JSONObject.fromObject(data.get(i));
    	    	 list1=(List<Map<String, String>>) json1.get("data");
			} catch (Exception e) {
				logger.error("------------移动解析:"+phoneNumber+"第"+i+"次数据异常，异常数据为：" + data.get(i),e);
				continue;
			}
	    	
	    	for (int j = 0; j < list1.size(); j++) {
	    		try {
			     Map<String, String>	map=new HashMap<>();
			     Map<String, String>	json=list1.get(j);
	             map.put("CallNumber", json.get("anotherNm").toString());// 被叫号码
	             map.put("CallType", json.get("commType").toString());// "通话类型",
	             map.put("CallAddress", json.get("commPlac").toString());// 归属地
	             map.put("CallWay", json.get("commMode").toString());	// "类型"
	             map.put("CallMoney", json.get("commFee").toString());// "费用(分)",
	             map.put("CallTime", json.get("startTime").toString());// 通话开始时间
	             try {
	            	 String CallDuration=this.getCallDuration(json.get("commTime").toString());
		             map.put("CallDuration", CallDuration);// 时长(秒)
				} catch (Exception e) {
					logger.error("------------移动解析:"+phoneNumber+"第"+i+"次数据异常，异常原因为：第"+j+"条数据通话时长解析错误，异常数据为：" +json.get("commTime").toString(),e);
					 map.put("CallDuration", "0秒");// 时长(秒)
				}
	            
	             list.add(map);
	         	
				} catch (Exception e) {
					logger.error("------------移动解析:"+phoneNumber+"第"+i+"次数据异常，异常原因为：第"+j+"条数据解析错误，异常数据为："+list1.get(j).toString() ,e);
				}  
			}	
	    }
		return list;
	}

	public List<Map<String, String>> analysisHtml(List<String> data,
			String phoneNumber, String... agrs) {
		
		logger.warn("------------移动解析数据开始----data为：" + JSONArray.fromObject(data));
		
		List<Map<String, String>> list=new ArrayList<>();
    	for (String item : data) {
    		Document parse = Jsoup.parse(item);
			Elements packs = parse.getElementsByClass("pack-time");
			for (Element pack : packs) {
				Elements lis = pack.select("li");
				for (Element li : lis) {
					Elements ps = li.getElementsByTag("p");
					if(ps.size() == 8) {
						Map<String, String>	map=new HashMap<>();
						map.put("CallNumber", ps.get(3).text().replace("对方号码：", ""));// 被叫号码
						map.put("CallType", ps.get(5).text().replace("通信类型：", ""));// "通话类型",
						map.put("CallAddress", ps.get(1).text().replace("通信地点：", ""));// 归属地
						map.put("CallWay", ps.get(2).text().replace("通信方式：", ""));	// "类型"
						map.put("CallMoney", ps.get(6).text().replace("实收费：", ""));// "费用(分)",
						map.put("CallTime", ps.get(0).text().replace("起始时间：", ""));// 通话开始时间
						
						String CallDuration=this.getCallDuration(ps.get(4).text().replace("通信时长：", ""));
						map.put("CallDuration", CallDuration);// 时长(秒)
						list.add(map);
					}else {
						logger.warn("------该行解析错误，这行信息为："+ ps.html());
					}
				}
			}
    	}
		return list;
	}

	/**
	 * 获取通话时长
	 * @param callDuration **时**分**秒
	 * @return
	 */
	   public  String getCallDuration(String callDuration){
	     String minutes="";
	     String seconds="";
	     int hour,minute,second=0;
	     if (callDuration.contains("秒")) {
	       if (callDuration.contains("分")) {
	         if (callDuration.contains("时")) {
	           hour=new Integer(callDuration.split("时")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	           minutes=callDuration.split("时")[1];
	           minute=new Integer(minutes.split("分")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	           seconds=minutes.split("分")[1];
	           second=new Integer(seconds.split("秒")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	           return hour*60*60+minute*60+second+"秒";
	        }else {
	             minutes= callDuration.split("分")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", "");
	             minute=new Integer(minutes);
	             seconds=callDuration.split("分")[1];
	            second=new Integer(seconds.split("秒")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	           return minute*60+second+"秒";
	        }
	       
	        }else if (callDuration.contains("时")) {
	        if (callDuration.contains("秒")) {
	            hour=new Integer(callDuration.split("时")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	            seconds=callDuration.split("时")[1];
	             
	             second=new Integer(seconds.split("秒")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	             return hour*60*60+second+"秒";
	        }else {
	          hour=new Integer(callDuration.split("时")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	          return hour*60*60+"秒";
	        }
	      }
	     }else if (callDuration.contains("分")) {
	       if (callDuration.contains("时")) {
	         hour=new Integer(callDuration.split("时")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	         minutes=callDuration.split("时")[1];
	         minute=new Integer(minutes.split("分")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	         return hour*60*60+minute*60+"秒";
	      }else {
	         minutes= callDuration.split("分")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", "");
	           minute=new Integer(minutes);
	           return minute*60+"秒";
	    }
	      
	    }else if (callDuration.contains("时")) {
	     
	         hour=new Integer(callDuration.split("时")[0].replace("时", "").replace("小", "").replace("分", "").replace("钟", "").replace("秒", ""));
	         return hour*60*60+"秒";
	       
	    
	    }
	  return callDuration;
	    
	   }
	   
	   public static void main(String[] args) {
		String str = "";
	   }
}

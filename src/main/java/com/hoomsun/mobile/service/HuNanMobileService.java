package com.hoomsun.mobile.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;










import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.hoomsun.mobile.tools.ConstantInterface;
import com.hoomsun.mobile.tools.GetMonth;
import com.hoomsun.mobile.tools.PushSocket;
import com.hoomsun.mobile.tools.PushState;
import com.hoomsun.mobile.tools.Resttemplate;

@Service
public class HuNanMobileService {
	private Logger logger = LoggerFactory.getLogger(HuNanMobileService.class);
	public Map<String, String> doGetCode(HttpServletRequest request, String phoneCode) {
		Map<String, String> map = new HashMap<String, String>(16);
		logger.warn("------湖南移动获取手机验证码-----开始-----手机号：" + phoneCode);
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		
		try {
			HtmlPage loginPage = webClient.getPage("http://wap.hn.10086.cn/wap/static/login/Login.html");
			Thread.sleep(3000);
			double random =  Math.random();
			String str = String.valueOf(random);
			if(loginPage.getTitleText().contains("湖南移动掌上营业厅")){
				logger.warn(loginPage.asText());
				
				String loadPath = "http://wap.hn.10086.cn/wap/login/sendSmsNew";
				WebRequest webRequest = new WebRequest(new URL(loadPath));
				webRequest.setHttpMethod(HttpMethod.GET);
				List<NameValuePair> list1 = new ArrayList<NameValuePair>();		
				list1.add(new NameValuePair("serialNumber", phoneCode));
				list1.add(new NameValuePair("chanId", "E004"));
				list1.add(new NameValuePair("operType", "SENDSMS"));
				list1.add(new NameValuePair("goodsName", "发送短信验证码"));
				list1.add(new NameValuePair("pageName", "Login.html"));
				list1.add(new NameValuePair("loginType", "2"));
				list1.add(new NameValuePair("ajaxSubmitType", "post"));
				list1.add(new NameValuePair("ajax_randomcode", str));
			    webRequest.setRequestParameters(list1);
				String sendCode = webClient.getPage(webRequest).getWebResponse().getContentAsString();
				Thread.sleep(2000); 
				
				JSONObject sendCode_test = JSONObject.fromObject(sendCode);
				logger.warn("---------湖南移动登录阶段:发验证码包请求结果:"+sendCode_test.toString()
						+"-------" + phoneCode);
				String sendCodeInfo = sendCode_test.getString("X_RESULTCODE");
				if("0".equals(sendCodeInfo)){
					map.put("errorCode", "0000");
					map.put("errorInfo", "已将短信随机码发送至手机，请查收！");
					request.getSession().setAttribute(phoneCode+"HN-Mobile", webClient);
				}else{
					map.put("errorCode", "0001");
					map.put("errorInfo", "发送验证码出错！");
				}
			}else{
				map.put("errorCode", "0001");
				map.put("errorInfo", "网页异常！");
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			map.put("errorCode", "0001");
			map.put("errorInfo", "系统异常！");
		}
		return map;
	}
	
	public Map<String, Object> doGetDetail(HttpServletRequest request,String phoneCode,
			String messageCode, String servicePassword,String longitude, String latitude,String uuid,String phoneType) {
		logger.warn("------湖南移动登录部分------开始-----手机号：" + phoneCode + "		短信验证码："+ messageCode +"		服务密码："+servicePassword + "		phoneType:"+phoneType);
		Map<String, Object> map = new HashMap<String, Object>(16);
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		PushSocket.pushnew(map, uuid, "1000", "登录中");
		PushState.state(phoneCode, "callLog", 100);
		
		String temp = "1000";
		Object webClientobj = request.getSession().getAttribute(phoneCode+"HN-Mobile");
		WebClient webClient = null;
		if(webClientobj!=null){
			try{
				double random =  Math.random();
				String str = String.valueOf(random);
				webClient = (WebClient) webClientobj;
				//登录发包
				JSONObject login_test = getLoginCode(webClient,phoneCode,messageCode,str);
				String loginCode = login_test.getString("X_RESULTCODE");
				if(!"-1".equals(loginCode)){
					//获取servicePwd
					HtmlPage login1 = webClient.getPage("http://wap.hn.10086.cn/wap/static/home/Index.html");
					Thread.sleep(1500);
					logger.warn(login1.asText());
					//在这个页面才能执行JS获取到servicePwd
					HtmlPage login = webClient.getPage("http://wap.hn.10086.cn/wap/static/doBusiness/phoneDetailBill.html");
					Thread.sleep(1500);
					ScriptResult stemp = login.executeJavaScript("strEnc('426581','13975181223'.substring(0,8),'13975181223'.substring(1,9),'13975181223'.substring(3,11));");
					Thread.sleep(500);
					String strs = stemp.toString();
					String servicePwd = getServicePwd(strs);
					login.getElementById("loginServicePwdId").setAttribute("value", servicePassword);
					login.getElementById("loginServicePwdId").setAttribute("value", servicePwd.substring(0,6));
					login.getElementById("pwd1").setAttribute("value", servicePwd.substring(6));
					login.executeJavaScript("loginSubmit()");
					HtmlPage getInfoPage = (HtmlPage) login.executeJavaScript("loginSubmit()").getNewPage();
					logger.warn("--------------------"+getInfoPage.asText());
					//获取错误提示信息
					String errorInfo = getInfoPage.getElementById("loginServicePwdMessageId").getTextContent();
						
					if("".equals(errorInfo)||errorInfo==null){
						PushSocket.pushnew(map, uuid, "2000", "登录成功");
						temp = "2000";
						Thread.sleep(1000); 
						//获取明细
						PushSocket.pushnew(map, uuid, "5000", "数据获取中");
						
						list = getInfos(getInfoPage, phoneCode);
						if(list.size() < 3){
							logger.warn("----------------湖南电信登陆失败：服务密码错误！请重新登陆认证！"+phoneCode+"--------------");
							map.put("errorCode", "0001");
							map.put("errorInfo", "数据不足三个月,未推送数据");
							PushSocket.pushnew(map, uuid, "6000", "数据不足三个月,未推送数据");
							PushSocket.pushnew(map, uuid, "8000", "数据不足三个月,未推送数据");
							PushState.stateTelecom(phoneCode, "callLog", 300, "数据不足三个月,未推送数据");
							webClient.close();
							return map;
						}
						logger.warn("------------湖南移动数据解析完成"+phoneCode+"----------------"+list.toString());
						PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
						temp = "6000";
						map.put("phone", phoneCode);// 认证手机号
						map.put("pwd", servicePassword);// 手机查询密码
						map.put("longitude", longitude);// 经度
						map.put("latitude", latitude);// 维度
						map.put("data", list);// 通话详单
						map = new Resttemplate().SendMessage(map, ConstantInterface.port + "/HSDC/message/operator");
						// 推送结果
						String statusResult = "0000";
						String statusCode = "errorCode";
						if (statusResult.equals(map.get(statusCode))) {
							temp = "8000";
							PushSocket.pushnew(map, uuid, "8000", "认证成功");
							PushState.stateTelecom(phoneCode, "callLog", 300,"认证成功");
						} else if("2222".equals(map.get(statusCode))){
							logger.warn("----------------湖南移动数据推送失败："+phoneCode+"--------------"+map.get("errorInfo").toString());
							PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
							PushState.state(phoneCode, "callLog", 200, map.get("errorInfo").toString());
						}else{
							PushSocket.pushnew(map, uuid, "8000", "认证成功");
							PushState.stateTelecom(phoneCode, "callLog", 300,"推送数据失败");
						}
					}else{
						logger.warn("----------------湖南电信登陆失败：服务密码错误！请重新登陆认证！"+phoneCode+"--------------");
						map.put("errorCode", "0001");
						map.put("errorInfo", errorInfo);
						PushSocket.pushnew(map, uuid, "3000", errorInfo);
						PushState.state(phoneCode, "callLog", 200, errorInfo);
					}
				}else{
					logger.warn("----------------湖南电信登陆失败：短信验证码输入错误"+phoneCode+"--------------");
					map.put("errorCode", "0001");
					map.put("errorInfo", login_test.getString("X_RESULTINFO"));
					PushSocket.pushnew(map, uuid, "3000", login_test.getString("X_RESULTINFO"));
					PushState.state(phoneCode, "callLog", 200, login_test.getString("X_RESULTINFO"));
				}
				
			}catch (Exception e) {
				// TODO Auto-generated catch block
				if("1000".equals(temp)){
					PushSocket.pushnew(map, uuid, "3000", "系统异常,请重新认证");
					PushState.state(phoneCode, "callLog", 200, "系统异常,请重新认证");
				}else if("2000".equals(temp)){
					PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
					PushSocket.pushnew(map, uuid, "8000", "认证成功");
					PushState.stateTelecom(phoneCode, "callLog", 300,"网络异常，数据未推送");
				}else if("6000".equals(temp)){
					PushSocket.pushnew(map, uuid, "8000", "系统异常,请重新认证");
					PushState.stateTelecom(phoneCode, "callLog", 300,"网络异常，数据未推送");
				}else if("8000".equals(temp)){
					PushSocket.pushnew(map, uuid, "8000", "认证成功");
					PushState.stateTelecom(phoneCode, "callLog", 300,"认证成功");
					webClient.close();
					return map;
				}
				map.put("errorCode", "0001");
				map.put("errorInfo", "系统异常,请重新认证");
			}
		}else{
			map.put("errorCode", "0001");
			map.put("errorInfo", "操作错误，请重新登陆！");
			PushSocket.pushnew(map, uuid, "3000", "操作错误，请重新登陆");
			PushState.state(phoneCode, "callLog", 200, "操作错误，请重新登陆");
		}
		if(webClient!=null){
			webClient.close();
		}
		return map;
	}
	
	
	/**
	 * 点击方法获取明细
	 * @param getInfoPage
	 * @param phoneCode
	 * @return
	 */
	public List<Map<String,Object>> getInfos(HtmlPage getInfoPage,String phoneCode){
		List<Map<String,Object>> infolist = new ArrayList<Map<String,Object>>();
		List<DomElement> list = new ArrayList<DomElement>();
		
		try{
			int[] yearMonth = GetMonth.nowYearMonth();
			for(int i=0;i<4;i++){
				//yyyyMM
				String beforMon = GetMonth.beforMon(yearMonth[0], yearMonth[1], i);
				String year = beforMon.substring(0,4);
				String month = beforMon.substring(4);
				//yyyyMMdd
				String endDay = GetMonth.lastDate(Integer.parseInt(year) , Integer.parseInt(month)).substring(6);
				//JS选择月份
				HtmlPage infoPge1 = (HtmlPage) getInfoPage.executeJavaScript("queryDetailBill('"+beforMon+"',this);").getNewPage();
				Thread.sleep(1000);
				HtmlPage infoPge2 = (HtmlPage) infoPge1.executeJavaScript("$('#endDate').val("+endDay+");").getNewPage();
				Thread.sleep(1000);
				//JS执行查询
				HtmlPage infoPge = (HtmlPage) infoPge2.executeJavaScript("queryDetailBillReq();").getNewPage();
				Thread.sleep(3000);
				DomNodeList<DomElement> infos = (DomNodeList<DomElement>) infoPge.getElementsByTagName("ul");
				logger.warn("---------------明细所在标签"+infos.toString()+"-----------"+infos.size());
				DomElement info = infos.get(infos.size()-1);
				list.add(info);
			}
			infolist = getItems(list);
		}catch (Exception e) {
			logger.warn("--------------------湖南移动查询详单时进入异常"+phoneCode+"---------------------------"+e);
		}
		return infolist;
	}
	
	/**
	 * 点击解析方法
	 * @param list
	 * @param str
	 * @return
	 */
	public List<Map<String,Object>> getItems(List<DomElement> list){
		List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
		for (DomElement domElement : list) {
			DomNodeList<HtmlElement> liList = null;
			try {
				liList = domElement.getElementsByTagName("li");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.warn("-----------没有查找到<LI>------------continue");
				continue;
			}
			
			for (HtmlElement htmlElement : liList) {
				Map<String,Object> infoMap = new HashMap<String,Object>();
				DomNodeList<HtmlElement> pList = null;
				try {
					pList = htmlElement.getElementsByTagName("p");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.warn("-----------没有查找到<P>-----------continue");
					continue;
				}
				String CallNumber = pList.get(0).getTextContent();
				infoMap.put("CallNumber", getstr(CallNumber));// 被叫号码
				String CallType = pList.get(6).getTextContent();
				infoMap.put("CallType",getstr(CallType) );// "通话类型",
				String CallAddress = pList.get(4).getTextContent();
				infoMap.put("CallAddress", getstr(CallAddress));// 归属地
				String CallWay = pList.get(1).getTextContent();
				infoMap.put("CallWay",getstr(CallWay) );	// "类型"主叫被叫
				String CallMoney = pList.get(3).getTextContent();
				infoMap.put("CallMoney", getstr(CallMoney));// "费用(分)",
				String CallTime = pList.get(5).getTextContent();
				infoMap.put("CallTime", getstr(CallTime));// 通话开始时间
				String CallDuration = pList.get(2).getTextContent();
				infoMap.put("CallDuration", getCallDuration(getstr(CallDuration)));// 时长(秒)
				logger.warn("--------------湖南移动单条解析后明细"+infoMap.toString());
				itemList.add(infoMap);
			}
		}
		return itemList;
	}
	
	public String getstr(String str){
		if(str.contains("：")){
			str = str.substring(str.indexOf("：")+1);
		}
		return str;
	}
	
	
	/**
	 * 登录发包
	 * @param webClient
	 * @param phoneCode
	 * @param messageCode
	 * @param str
	 * @return
	 */
	public JSONObject getLoginCode(WebClient webClient,String phoneCode,String messageCode ,String str){
		JSONObject login_test  = null;
		try{
			String loadPath = "http://wap.hn.10086.cn/wap/login/SmsLogin";
			WebRequest webRequest = new WebRequest(new URL(loadPath));
			webRequest.setHttpMethod(HttpMethod.GET);
			List<NameValuePair> list1 = new ArrayList<NameValuePair>();		
			list1.add(new NameValuePair("SERIAL_NUMBER", phoneCode));
			list1.add(new NameValuePair("LOGIN_TYPE", "2"));
			list1.add(new NameValuePair("USER_PASSSMS", messageCode));
			list1.add(new NameValuePair("chanId", "E004"));
			list1.add(new NameValuePair("operType", "LOGIN"));
			list1.add(new NameValuePair("goodsName", "短信密码登录"));
			list1.add(new NameValuePair("loginType", "1"));
			list1.add(new NameValuePair("pageName", "Login.html"));
			list1.add(new NameValuePair("ajaxSubmitType", "post"));
			list1.add(new NameValuePair("ajax_randomcode", str));
		    webRequest.setRequestParameters(list1);
			String login = webClient.getPage(webRequest).getWebResponse().getContentAsString();
			Thread.sleep(2000); 
			
			login_test = JSONObject.fromObject(login);
			logger.warn("---------湖南移动登录:"+login_test.toString()
					+"-------" + phoneCode);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("--------------------湖南移动登录发包时进入异常"+phoneCode+"---------------------------");
		}
		return login_test;
	}

	
	
	/**
	 * 获取通话时长
	 * @param callDuration **时**分**秒
	 * @return
	 */
	public static  String getCallDuration(String callDuration){
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
	
	
	public String  getServicePwd(String str) {
		int a = str.indexOf("=");
		int b = str.indexOf(" ");
		str = str.substring(a+1,b);
		System.out.println(str);
		return str;
	}
}

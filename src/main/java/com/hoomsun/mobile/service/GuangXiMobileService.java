package com.hoomsun.mobile.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.hoomsun.mobile.analysis.PhoneBillsAnalysisImp;
import com.hoomsun.mobile.tools.ConstantInterface;
import com.hoomsun.mobile.tools.Dates;
import com.hoomsun.mobile.tools.ImgUtil;
import com.hoomsun.mobile.tools.PushSocket;
import com.hoomsun.mobile.tools.PushState;
import com.hoomsun.mobile.tools.Resttemplate;
import com.hoomsun.mobile.tools.WebClientFactory;

import net.sf.json.JSONArray;

/**
 * 广西移动
 *@Title:  
 *@Description:  
 *@Author:Administrator  
 *@Since:2018年8月16日  
 *@Version:1.1.0
 */
@Service
public class GuangXiMobileService {
	
	 private Logger logger = LoggerFactory.getLogger(GuangXiMobileService.class);
	  /**
		 * 第一步
		 * 
		 * @param request
		 * @param userNumber
		 * @return
		 * @Description:
		 */
		public Map<String, String> doGetCode(HttpServletRequest request, String phoneCode) {
			Map<String, String> map = new HashMap<String, String>(16);

			logger.warn("------广西移动-----开始-----手机号：" + phoneCode);
			
			map.put("errorCode", "0000");
			map.put("errorInfo", "成功");
			
			request.getSession().setAttribute(phoneCode + "one", "true");
			logger.warn("------广西移动------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
			return map;
		}

		/**
		 * 登录
		 * 
		 * @param request
		 * @param userNumber
		 * @param duanxinCode
		 * @return
		 * @Description:
		 */
		public Map<String, Object> doGetDetail(HttpServletRequest request,String phoneCode,
				String messageCode, String servicePassword,String longitude, String latitude,String uuid,String phoneType) {
			
			logger.warn("------广西浙江移动登录部分------开始-----手机号：" + phoneCode + "		短信验证码："+ messageCode +"		服务密码："+servicePassword + "		phoneType:"+phoneType);
			Map<String, Object> map = new HashMap<String, Object>(16);

			Object obj = request.getSession().getAttribute(phoneCode + "one");

			if (obj == null) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "登录超时");
				logger.warn("------广西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
				return map;
			} else {
				String flag = (String)obj;
				if(flag.equals("true")) {
					map = this.oneLogin(request, phoneCode, servicePassword);
				}else if(flag.equals("false")) {
					map = this.twoLogin(request, phoneCode, messageCode, servicePassword, longitude, latitude, uuid, phoneType);
				}
			}
			return map;
		}

		
		
		
		/**
		 * 登录
		 * @param request
		 * @param phoneCode 手机号
		 * @param servicePassword 服务密码
		 * @return  
		 * @Description:
		 */
		private Map<String,Object> oneLogin(HttpServletRequest request,String phoneCode, String servicePassword){
			
			logger.warn("------广西移动登录部分----one-----开始-----手机号：" + phoneCode);
			
			Map<String, Object> map = new HashMap<String, Object>(16);
			
			WebClient webClient = new WebClientFactory().getWebClient();
			try {
				//图片验证码打码失败，重新打码
				String str = this.getImg(request, webClient, phoneCode, servicePassword);
				
				if(str.isEmpty()) {
					logger.warn("-----广西移动登录成功---one----手机号："+ phoneCode );
					
					webClient.getPage("http://service.gx.10086.cn/fee/ng/qrydetailinfo.jsp");
					
					Thread.sleep(4000);
					
					map.put("errorCode", "0000");
					map.put("errorInfo", "登录成功");
					Map<String,String>  info = new HashMap<String,String>();
					map.put("data", info);
					info.put("state", "2222");//不需要发送第二次短信验证码
					request.getSession().setAttribute(phoneCode + "YD-webClient", webClient);
					request.getSession().setAttribute(phoneCode + "one", "false");
				}else {
					map.put("errorCode", "0002");
					map.put("errorInfo", str);
					logger.warn("------广西移动登录失败----one----结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
					return map ;
				}
			   
			} catch (Exception e) {
				logger.error(phoneCode + "广西移动登录失败----手机号："+phoneCode, e);
				map.put("errorCode", "0002");
				map.put("errorInfo", "网络异常，请重试！");
				webClient.close();
			}
			logger.warn("------广西移动登录---one---结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
			return map;
		}
		
		/**
		 * 获取详情
		 * @param request
		 * @param phoneCode
		 * @param messageCode
		 * @param servicePassword
		 * @param longitude
		 * @param latitude
		 * @param uuid
		 * @param phoneType
		 * @return  
		 * @Description:
		 */
		public Map<String, Object> twoLogin(HttpServletRequest request,String phoneCode,
				String messageCode, String servicePassword,String longitude, String latitude,String uuid,String phoneType) {
			
			logger.warn("------广西移动登录部分----two-----开始-----手机号：" + phoneCode);
			Map<String, Object> map = new HashMap<String, Object>(16);
			
			PushSocket.pushnew(map, uuid, "1000", "登录中");
			PushState.state(phoneCode, "callLog", 100);
			int a = 1;
			Object driverObj = request.getSession().getAttribute(phoneCode + "YD-webClient");

			if (driverObj != null) {
				WebClient webClient = (WebClient) driverObj;
				try {
					
					//登录成功
					PushSocket.pushnew(map, uuid, "2000", "登录成功");
					PushSocket.pushnew(map, uuid, "5000", "数据获取中");
					
					a=5;
					//放所有未解析的数据
					List<String> data =  new ArrayList<String>();
					
					boolean flag = true;
					int i = 1;
					while(flag) {
						//获取详情
						String url = "http://service.gx.10086.cn/ncs/querydetailinfo/QueryDetailInfoAction/queryBusi.menu";
						WebRequest requests = new WebRequest(new URL(url));
						requests.setAdditionalHeader("Content-Type",
								"application/x-www-form-urlencoded");
						requests.setAdditionalHeader("Origin",
								"http://service.gx.10086.cn");
						requests.setAdditionalHeader("Referer",
								"http://service.gx.10086.cn/fee/ng/qrydetailinfo.jsp");
						List<NameValuePair> list = new ArrayList<NameValuePair>();
						list.add(new NameValuePair("queryType", "1"));
						list.add(new NameValuePair("query_mode", "1"));
						list.add(new NameValuePair("monthQuery", "on"));
						list.add(new NameValuePair("start_date", Dates.parseDateToStringB(-5)));
						list.add(new NameValuePair("end_date", Dates.currentDateToString()));
						list.add(new NameValuePair("_zoneId", "queryDetailResult"));
						list.add(new NameValuePair("_tmpDate", Dates.parseCurrentDateToString()));
						list.add(new NameValuePair("_menuId", "20141029"));
						list.add(new NameValuePair("_buttonId", "query"));
						list.add(new NameValuePair("iPage", (i++)+""));
						requests.setRequestParameters(list);
						requests.setHttpMethod(HttpMethod.POST);
						HtmlPage page1 = webClient.getPage(requests);
						Thread.sleep(3000);
						// System.out.println(page1.asXml());
						String tip = page1.asXml();
						logger.warn("----asdfg==="+tip);
						Thread.sleep(3000);
						if(tip.contains("没有找到该详单信息") || tip.contains("您操作频繁，请稍后再试")) {
							flag = false;
						}else {
							data.add(tip);
						}
					}
					
					/*for (int i = 0; i < 6; i++) {
						for (int j = 0; j < 3; j++) {
							//获取详情
							String url = "http://service.gx.10086.cn/ncs/querydetailinfo/QueryDetailInfoAction/queryBusi.menu";
							WebRequest requests = new WebRequest(new URL(url));
							requests.setAdditionalHeader("Content-Type",
									"application/x-www-form-urlencoded");
							requests.setAdditionalHeader("Origin",
									"http://service.gx.10086.cn");
							requests.setAdditionalHeader("Referer",
									"http://service.gx.10086.cn/fee/ng/qrydetailinfo.jsp");
							List<NameValuePair> list = new ArrayList<NameValuePair>();
							
							list.add(new NameValuePair("query_mode", "1"));
							list.add(new NameValuePair("queryType", "0"));
							list.add(new NameValuePair("start_date", ""));
							list.add(new NameValuePair("end_date", ""));
							list.add(new NameValuePair("monthQuery", Dates.parseDateToStringM(-i)));
							list.add(new NameValuePair("stDate", Dates.parseDateToStringB(-i)));
							list.add(new NameValuePair("edDate", Dates.parseDateToStringE(-i)));
							list.add(new NameValuePair("nowDate", Dates.currentDateToString()));
							list.add(new NameValuePair("showAll", ""));
							list.add(new NameValuePair("showDetail", ""));
							list.add(new NameValuePair("newBill", ""));
							list.add(new NameValuePair("iPage", (j+1)+""));
							list.add(new NameValuePair("linkType", "5"));
							list.add(new NameValuePair("iPara", ""));
							list.add(new NameValuePair("_zoneId", "queryDetailResult"));
							list.add(new NameValuePair("_tmpDate", Dates.parseCurrentDateToString()));
							list.add(new NameValuePair("_menuId", "20141029"));
							list.add(new NameValuePair("_buttonId", null));
							
							requests.setRequestParameters(list);
							requests.setHttpMethod(HttpMethod.POST);
							HtmlPage page1 = webClient.getPage(requests);
							Thread.sleep(3000);
							// System.out.println(page1.asXml());
							String tip = page1.asXml();
							logger.warn("----asdfg==="+tip);
							Thread.sleep(3000);
							if(tip.contains("没有找到该详单信息") || tip.contains("您操作频繁，请稍后再试")) {
								
							}else {
								data.add(tip);
							}
						}
					}*/
					
					PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
					// 数据解析
					List<Map<String, String>> dataList = this.analysisHtml(data, phoneCode, "");
					map.put("phone", phoneCode);// 认证手机号
					map.put("pwd", servicePassword);// 手机查询密码
					map.put("longitude", longitude);// 经度
					map.put("latitude", latitude);// 维度
					map.put("data", dataList);// 通话详单
					map = new Resttemplate().SendMessage(map, ConstantInterface.port + "/HSDC/message/operator");

					// 推送结果
					String statusResult = "0000";
					String statusCode = "errorCode";
					if (statusResult.equals(map.get(statusCode))) {
						PushSocket.pushnew(map, uuid, "8000", "认证成功");
						PushState.state(phoneCode, "callLog", 300);
					} else {
						PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
						PushState.state(phoneCode, "callLog", 200, map.get("errorInfo").toString());
					}
				} catch (Exception e) {
					logger.error(phoneCode + "广西获取详情失败", e);
					map.put("errorCode", "0002");
					map.put("errorInfo", "网络异常，请重试！");
					PushState.state(phoneCode, "callLog", 200, "认证失败,系统繁忙！");
					PushSocket.pushnew(map, uuid, (a + 2) * 1000 + "", "认证失败,系统繁忙！");
				}finally {
					webClient.close();
				}
			}
			logger.warn("------广西移动详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
			return map;
		}
		

		/**
		 * 图片验证码打码错误后进行重试
		 * 
		 * @param request
		 * @param driver
		 * @return
		 * @throws Exception
		 * @Description:
		 */
		public String getImg(HttpServletRequest request,WebClient webClient,String phoneCode, String servicePassword) throws Exception {
			
			//登录
			HtmlPage loginPage = webClient.getPage("http://service.gx.10086.cn/public/LoginAction/loginWindowForm_h.action");
			
			Thread.sleep(3000);
			//用户名
			loginPage.getElementById("float_mobileNum").setAttribute("value", phoneCode);
			//密码
			loginPage.getElementById("float_password").setAttribute("value", servicePassword);
			//图片验证码
			loginPage.getElementById("float_operVerifyImg").click();
			Thread.sleep(3000);
			
			HtmlImage htmlImg = (HtmlImage) loginPage.getElementById("float_operVerifyImg");
			
			String path = request.getServletContext().getRealPath("/validateimg");
			
			String code = ImgUtil.saveImg(htmlImg, "yd", path, "png");
				
			loginPage.getElementById("float_validCode").setAttribute("value", code);
			
			//对alert弹框进行监听
	        List<String> list = new ArrayList<String>();
	        CollectingAlertHandler alert = new CollectingAlertHandler(list);
	        webClient.setAlertHandler(alert);
			//登录
			loginPage.getElementById("float_login_btn").click();
				
			if(list.size() > 0){
				//登录失败
				String str = list.get(0).trim();
	        	if(str.contains("你输入的验证码错误，请重新输入！")){
	        		logger.warn("---你输入的验证码错误，请重新输入！----验证码打码失败，进行重试");
	        		return this.getImg(request, webClient, phoneCode, servicePassword);
	        	}else{
	        		webClient.close();
	        		return str;
	        	}
	        }
			//登录成功，返回空字符串
			return "";
		}
	
		/**
		 * 解析数据
		 * @param data
		 * @param phoneNumber
		 * @param agrs
		 * @return  
		 * @Description:
		 */
		public List<Map<String, String>> analysisHtml(List<String> data,
				String phoneNumber, String... agrs) {
			
			logger.warn("------------移动解析数据开始----data为：" + JSONArray.fromObject(data));
			
			List<Map<String, String>> list=new ArrayList<>();
	    	for (String item : data) {
	    		Document parse = Jsoup.parse(item);
				Element pack = parse.getElementsByClass("table_detail_free").get(2);
				Elements trs = pack.select("tr");
				for (int i = 1 ; i < trs.size() ; i++) {
					Elements tds = trs.get(i).select("td");
					if(tds.size() == 7) {
						Map<String, String>	map=new HashMap<>();
						map.put("CallNumber", tds.get(3).text().replace("对方号码：", ""));// 被叫号码
						map.put("CallType", tds.get(5).text().replace("通信类型：", ""));// "通话类型",
						map.put("CallAddress", tds.get(1).text().replace("通信地点：", ""));// 归属地
						map.put("CallWay", tds.get(2).text().replace("通信方式：", ""));	// "类型"
						map.put("CallMoney", tds.get(6).text().replace("实收费：", ""));// "费用(分)",
						map.put("CallTime", tds.get(0).text().replace("起始时间：", ""));// 通话开始时间
						
						String CallDuration=new PhoneBillsAnalysisImp().getCallDuration(tds.get(4).text().replace("通信时长：", ""));
						map.put("CallDuration", CallDuration);// 时长(秒)
						list.add(map);
					}else {
						logger.warn("------该行解析错误，这行信息为："+ tds.html());
					}
				
				}
	    	}
			return list;
		}
}

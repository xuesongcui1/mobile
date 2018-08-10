package com.hoomsun.mobile.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hoomsun.mobile.analysis.PhoneBillsAnalysisImp;
import com.hoomsun.mobile.tools.ConstantInterface;
import com.hoomsun.mobile.tools.DriverUtil;
import com.hoomsun.mobile.tools.ImgUtil;
import com.hoomsun.mobile.tools.PushSocket;
import com.hoomsun.mobile.tools.PushState;
import com.hoomsun.mobile.tools.Resttemplate;

import net.sf.json.JSONArray;

/**
 * 
 *@Title:  
 *@Description:  
 *@Author:Administrator  
 *@Since:2018年7月20日  
 *@Version:1.1.0
 */

@Service
public class ZheJiangMobileService {

	
	  private Logger logger = LoggerFactory.getLogger(ZheJiangMobileService.class);
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

			logger.warn("------浙江移动-----开始-----手机号：" + phoneCode);
			
			map.put("errorCode", "0000");
			map.put("errorInfo", "成功");
			
			request.getSession().setAttribute(phoneCode + "one", "true");
			logger.warn("------浙江移动------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
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
			
			logger.warn("------浙江移动登录部分------开始-----手机号：" + phoneCode + "		短信验证码："+ messageCode +"		服务密码："+servicePassword + "		phoneType:"+phoneType);
			Map<String, Object> map = new HashMap<String, Object>(16);

			Object obj = request.getSession().getAttribute(phoneCode + "one");

			if (obj == null) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "登录超时");
				logger.warn("------浙江移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
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
			
			logger.warn("------浙江移动登录部分------开始-----手机号：" + phoneCode);
			
			Map<String, Object> map = new HashMap<String, Object>(16);
			
			WebDriver driver = DriverUtil.getDriverInstance("chrome");
			try {
				
			    //登录
				driver.get("https://zj.ac.10086.cn/login");
				
				logger.warn("----登录2222----"+driver.getPageSource());
				
				//图片验证码打码失败，重新打码
				driver = this.getImg(request, driver, phoneCode, servicePassword);
				try {
					Alert alt = driver.switchTo().alert();
					String errorInfo = alt.getText();
					map.put("errorCode", "0002");
					map.put("errorInfo", errorInfo);
					alt.accept();
					DriverUtil.close(driver);
					logger.warn("------浙江移动登录------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
					return map ;
				} catch (Exception e) {
					logger.warn("-----登录成功---" + driver.getPageSource());
					
					if(DriverUtil.waitByTitle("我的移动_中国移动通信", driver, 20)) {
						driver.get("http://service.zj.10086.cn/yw/detail/queryHisDetailBill.do?menuId=13009");
						
						if(DriverUtil.waitByClassName("search-js", driver, 15) && DriverUtil.clickByClassName("search-js", driver, 15)) {
							
							driver.findElement(By.className("search-js")).click();
							
							/*JavascriptExecutor js = (JavascriptExecutor) driver;
							js.executeScript("setQueryOrload('1');");
							*/
							if(DriverUtil.waitByXpath("/html/body/div[4]/div[4]/div/p", driver, 15) 
									&& DriverUtil.visibilityByXpath("/html/body/div[4]/div[4]/div/p", driver, 10)) {
								String str = driver.findElement(By.xpath("/html/body/div[4]/div[4]/div/p")).getText();
								if(str.contains("成功")) {
									map.put("errorCode", "0000");
									Map<String,String>  info = new HashMap<String,String>();
									map.put("data", info);
									info.put("state", "1111");//需要发送第二次短信验证码
								}else {
									map.put("errorCode", "0001");
									driver.quit();
								}
								map.put("errorInfo", str);
								
								request.getSession().setAttribute(phoneCode + "YD-driver", driver);
								request.getSession().setAttribute(phoneCode + "one", "false");
							
							}else {
								//点击没点上，则重新点击
								driver.findElement(By.className("search-js")).click();
								
								if(DriverUtil.waitByXpath("/html/body/div[4]/div[4]/div/p", driver, 15) 
										&& DriverUtil.visibilityByXpath("/html/body/div[4]/div[4]/div/p", driver, 10)) {
									String str = driver.findElement(By.xpath("/html/body/div[4]/div[4]/div/p")).getText();
									if(str.contains("成功")) {
										map.put("errorCode", "0000");
										Map<String,String>  info = new HashMap<String,String>();
										map.put("data", info);
										info.put("state", "1111");//需要发送第二次短信验证码
									}else {
										map.put("errorCode", "0001");
										driver.quit();
									}
									map.put("errorInfo", str);
									
									request.getSession().setAttribute(phoneCode + "YD-driver", driver);
									request.getSession().setAttribute(phoneCode + "one", "false");
								}else {
									throw new Exception();
								}
							}
						}else {
							throw new Exception();
						}
						
					}else {
						throw new Exception();
					}
					
				}
			   
			} catch (Exception e) {
				logger.error(phoneCode + "浙江移动登录失败", e);
				map.put("errorCode", "0002");
				map.put("errorInfo", "网络异常，请重试！");
//				DriverUtil.close(driver);
			}
			logger.warn("------浙江移动登录------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
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
			
			Map<String, Object> map = new HashMap<String, Object>(16);
			
			PushSocket.pushnew(map, uuid, "1000", "登录中");
			PushState.state(phoneCode, "callLog", 100);
			int a = 1;
			Object driverObj = request.getSession().getAttribute(phoneCode + "YD-driver");

			if (driverObj != null) {
				WebDriver driver = (WebDriver) driverObj;
				try {
					List<String>  month = new ArrayList<String>();
					
					logger.warn("------详情----"+ driver.getPageSource());
					
					if(DriverUtil.waitByClassName("search-indent", driver, 15) && DriverUtil.visibilityByClassName("search-indent", driver, 10)) {
						
						List<WebElement> list = driver.findElements(By.className("monthList"));
						
						for (int i = 0; i < list.size() ; i++) {
							String str = list.get(i).getAttribute("data-value");
							month.add(str);
						}
					}
					
					
					driver.findElement(By.id("validateCode")).sendKeys(messageCode);
					
					driver.findElement(By.className("tiji")).click();
					
					Thread.sleep(3000);
					try {
						Alert alt = driver.switchTo().alert();
						String errorInfo = alt.getText();
						
						map.put("errorCode", "0002");
						map.put("errorInfo", errorInfo);
						alt.accept();
						PushSocket.pushnew(map, uuid, "3000", errorInfo);
					    PushState.state(phoneCode, "callLog", 200, errorInfo);
						logger.warn("------浙江移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
					} catch (Exception e) {
						//登录成功
						PushSocket.pushnew(map, uuid, "2000", "登录成功");
						PushSocket.pushnew(map, uuid, "5000", "数据获取中");
						a = 5;
						
						List<String> data = new ArrayList<String>();
						
						for (int i = 0; i < month.size() ; i++) {
							String str = month.get(i);
							
							driver.get("http://service.zj.10086.cn/yw/detail/queryHisDetailBill.do?bid=&menuId=13009&listtype=1&month="+ str );
							
							if(DriverUtil.waitByClassName("pagecontent", driver, 15)) {
								String detail = driver.getPageSource();
								logger.warn(phoneCode + "-----移动详单第" + i + "次获取成功，数据为" + detail);
								data.add(detail);
							}else {
								logger.warn(phoneCode + "-----移动详单第" + i + "次获取失败，数据为" + driver.getPageSource());
							}
							
						}
						
						logger.warn(phoneCode + ": 该用户获取的数据总数为：" + data.size());
						if (data.size() < 3) {
							PushSocket.pushnew(map, uuid, "7000", "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过3个月)");
							PushState.state(phoneCode, "callLog", 200, "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过3个月)");
							map.put("errorCode", "0009");
							map.put("errorInfo", "数据获取不完全，请重新再次认证！(注：请确认手机号使用时长超过3个月)");

							logger.warn("------浙江移动详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
							return map;
						}

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
					}
				} catch (Exception e) {
					logger.error(phoneCode + "浙江获取详情失败", e);
					map.put("errorCode", "0002");
					map.put("errorInfo", "网络异常，请重试！");
					PushState.state(phoneCode, "callLog", 200, "认证失败,系统繁忙！");
					PushSocket.pushnew(map, uuid, (a + 2) * 1000 + "", "认证失败,系统繁忙！");
				}finally {
					DriverUtil.close(driver);
				}
			}
			logger.warn("------浙江移动详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
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
		public WebDriver getImg(HttpServletRequest request,WebDriver driver,String phoneCode, String servicePassword) throws Exception {
			
			
			if (DriverUtil.waitById("imageCode", driver, 15)
					&& DriverUtil.visibilityById("imageCode", driver, 2)) {
				
				driver.findElement(By.id("phone_number")).sendKeys(phoneCode);
				driver.findElement(By.id("serverPwd")).sendKeys(servicePassword);
				
				// 获取图片Element
				WebElement verifyImg = driver.findElement(By.id("imageCode"));
				// 验证码图片路径
				String path = request.getServletContext().getRealPath("/validateimg");
				String code = ImgUtil.saveImg(verifyImg, driver, path, "yd", "png");

				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("document.getElementById(\"loginYzm\").focus();");

				// 输入图片验证码
				driver.findElement(By.id("loginYzm")).sendKeys(code);
				Thread.sleep(2000);
				js.executeScript("document.getElementById(\"loginYzm\").blur();");
		
				driver.findElement(By.id("loginbtn")).click();
				
				Thread.sleep(4000);
				
				try {
					Alert alt = driver.switchTo().alert();
					String errorInfo = alt.getText();
					
					if(errorInfo.contains("验证码") && !errorInfo.contains("服务密码")) {
						alt.accept();
						DriverUtil.close(driver);
	   				    driver = this.getImg(request, driver, phoneCode, servicePassword);
	   				}
				} catch (Exception e) {
					//没有获取到弹窗，证明登录成功。
				}
				
			} else {
				driver.get("https://zj.ac.10086.cn/login");
				driver = this.getImg(request, driver, phoneCode, servicePassword);
			}
			
			return driver;
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
				Elements packs = parse.getElementsByClass("content2");
				for (Element pack : packs) {
					Elements tds = pack.select("td");

					if(tds.size() == 14) {
						Map<String, String>	map=new HashMap<>();
						map.put("CallNumber", tds.get(5).text().replace("对方号码：", ""));// 被叫号码
						map.put("CallType", tds.get(3).text().replace("通信类型：", ""));// "通话类型",
						map.put("CallAddress", tds.get(7).text().replace("通信地点：", ""));// 归属地
						map.put("CallWay", tds.get(4).text().replace("通信方式：", ""));	// "类型"
						map.put("CallMoney", tds.get(12).text().replace("实收费：", ""));// "费用(分)",
						map.put("CallTime", tds.get(1).text().replace("起始时间：", ""));// 通话开始时间
						
						String CallDuration=new PhoneBillsAnalysisImp().getCallDuration(tds.get(2).text().replace("通信时长：", ""));
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

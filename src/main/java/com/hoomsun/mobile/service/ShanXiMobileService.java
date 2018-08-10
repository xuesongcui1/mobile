package com.hoomsun.mobile.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.Augmenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hoomsun.mobile.analysis.PhoneBillsAnalysisImp;
import com.hoomsun.mobile.tools.ConstantInterface;
import com.hoomsun.mobile.tools.DriverUtil;
import com.hoomsun.mobile.tools.MyCYDMDemo;
import com.hoomsun.mobile.tools.PushSocket;
import com.hoomsun.mobile.tools.PushState;
import com.hoomsun.mobile.tools.Resttemplate;

/**
 * 陕西移动
 *@Title:  
 *@Description:  
 *@Author:Administrator  
 *@Since:2018年7月10日  
 *@Version:1.1.0
 */
@Service
public class ShanXiMobileService {

	  
	  private Logger logger = LoggerFactory.getLogger(ShanXiMobileService.class);
	  /**
		 * 获取移动验证码
		 * 
		 * @param request
		 * @param userNumber
		 * @return
		 * @Description:
		 */
		public Map<String, String> doGetCode(HttpServletRequest request, String phoneCode) {
			Map<String, String> map = new HashMap<String, String>(16);

			logger.warn("------陕西移动获取手机验证码-----开始-----手机号：" + phoneCode);

			// 添加谷歌驱动
			System.setProperty(ConstantInterface.chromeDriverKey, ConstantInterface.chromeDriverValue);

			ChromeDriver driver = new ChromeDriver();
			driver.manage().window().maximize();
			
			/*System.setProperty(ConstantInterface.phantomJsKey, ConstantInterface.phantomJsValue);
			DesiredCapabilities dcaps = new DesiredCapabilities();
    		//ssl证书支持
    		dcaps.setCapability("acceptSslCerts", true);
    		//截屏支持
    		dcaps.setCapability("takesScreenshot", true);
    		//css搜索支持
    		dcaps.setCapability("cssSelectorsEnabled", true);
    		//js支持
    		dcaps.setJavascriptEnabled(true);
    		//驱动支持
    		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,ConstantInterface.phantomJsValue);*/
		
//    		PhantomJSDriver driver = new PhantomJSDriver();  
			//浏览器窗口最大化  
			driver.manage().window().maximize(); 
			driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);
			try {
				driver.get("http://wap.sn.10086.cn/h5/personal/html/login.html");

				if (DriverUtil.waitById("smsPhoneNo", driver, 15) && DriverUtil.visibilityById("smsPhoneNo", driver, 2)
						&& DriverUtil.clickByLinkText("获取短信码", driver, 2)) {
					// 输入手机号
					driver.findElementById("smsPhoneNo").sendKeys(phoneCode);
					// 获取短信验证码
					driver.findElementByLinkText("获取短信码").click();
					// 查看短信发送结果
					if (DriverUtil.visibilityById("errorMsg", driver, 10)) {
						String tips = driver.findElementById("errorMsg").getText();
						if (tips.contains("下发到您的手机")) {
							map.put("errorCode", "0000");
							map.put("errorInfo", "已将短信随机码发送至手机，请查收!");
							request.getSession().setAttribute(phoneCode + "one", "true");
							request.getSession().setAttribute(phoneCode + "YD-driver", driver);
						} else {
							driver.quit();
							map.put("errorCode", "0002");
							map.put("errorInfo", tips);
						}
					} else {
						// 点击短信验证码没点击上，重新点击
						// 获取短信验证码
						driver.findElementByLinkText("获取短信码").click();

						if (DriverUtil.visibilityById("errorMsg", driver, 10)) {
							String tips = driver.findElementById("errorMsg").getText();
							if (tips.contains("下发到您的手机")) {
								map.put("errorCode", "0000");
								map.put("errorInfo", "已将短信随机码发送至手机，请查收!");
								request.getSession().setAttribute(phoneCode + "YD-driver", driver);
								request.getSession().setAttribute(phoneCode + "one", "true");
							} else {
								driver.quit();
								map.put("errorCode", "0002");
								map.put("errorInfo", tips);
							}
						} else {
							throw new Exception();
						}
					}
				} else {
					driver.quit();
					map = this.doGetCode(request, phoneCode);
					return map;
				}
			} catch (Exception e) {
				logger.error(phoneCode + "获取移动手机验证码失败", e);
				driver.quit();
				map = this.doGetCode(request, phoneCode);
				return map;
			}
			logger.warn("------陕西移动获取手机验证码------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
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
			logger.warn("------陕西移动登录部分------开始-----手机号：" + phoneCode + "		短信验证码："+ messageCode +"		服务密码："+servicePassword + "		phoneType:"+phoneType);
			Map<String, Object> map = new HashMap<String, Object>(16);

			Object driverObj = request.getSession().getAttribute(phoneCode + "YD-driver");

			if (driverObj == null) {
				map.put("errorCode", "0001");
				map.put("errorInfo", "请先发送短信验证码，或者短信验证码已失效");
				logger.warn("------陕西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
				return map;
			} else {
				WebDriver driver = (WebDriver) driverObj;
				try {
					String oneFlag = (String)request.getSession().getAttribute(phoneCode + "one");
					
					if(oneFlag.endsWith("true")) {
						if (DriverUtil.waitById("smsPwd", driver, 15) && DriverUtil.visibilityById("smsPwd", driver, 2)) {
							
							// 输入短信验证码
							driver.findElement(By.id("smsPwd")).sendKeys(messageCode);
							
						
							
							Thread.sleep(2000);
							// 截取图片验证码并打码，若打码失败进行重试
							driver = this.getImg(request, driver);
							
							//查看是否登录成功
							if (DriverUtil.waitById("errorMsg", driver, 5)
									&& DriverUtil.visibilityById("errorMsg", driver, 2)) {
								//登录失败
								String tips = driver.findElement(By.id("errorMsg")).getText();
								map.put("errorCode", "0002");
								map.put("errorInfo", tips);
								request.getSession().setAttribute(phoneCode + "YD-driver", driver);
								request.getSession().setAttribute(phoneCode + "one", "true");
								
								logger.warn("------陕西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
								return map;
							}else if(DriverUtil.waitById("login", driver, 20)){
								//登录成功
								driver.get("http://wap.sn.10086.cn/h5/personal/html/detailedQuery.html");
								Thread.sleep(3000);
								if(DriverUtil.waitById("myModal3", driver, 15)) {
									if (driver.getPageSource().contains("校验验证码")) {
										String display = driver.findElement(By.id("myModal3")).getAttribute("style");
										
										if(display.contains("block")) {
											map.put("errorCode", "0000");
											map.put("errorInfo", "登录成功");
											
											Map<String,String>  info = new HashMap<String,String>();
											map.put("data", info);
											
											info.put("state", "1111");//需要发送第二次短信验证码
											
											request.getSession().setAttribute(phoneCode + "YD-driver", driver);
											request.getSession().setAttribute(phoneCode + "one", "false");
											
											logger.warn("------陕西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
											return map;
										}else if(display.contains("none")){
											
												map.put("errorCode", "0000");
												map.put("errorInfo", "登录成功");
												
												Map<String,String>  info = new HashMap<String,String>();
												map.put("data", info);
												
												info.put("state", "2222");//不需要发送第二次短信验证码
												
												request.getSession().setAttribute(phoneCode + "YD-driver", driver);
												request.getSession().setAttribute(phoneCode + "one", "false");
												
												logger.warn("------陕西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
												return map;
										}
									}else {
										throw new Exception();
									}
								}else {
									throw new Exception();
								}
								
							}
						}else {
							throw new Exception();
						}
					}else if(oneFlag.equals("false")) {
						
						PushSocket.pushnew(map, uuid, "1000", "登录中");
						PushState.state(phoneCode, "callLog", 100);
						
						String display = driver.findElement(By.id("myModal3")).getAttribute("style");
						if(display.contains("block")) {
							JavascriptExecutor js = (JavascriptExecutor) driver;
							// 需要输入第二次短信验证码。
							if (DriverUtil.waitById("smsPwd", driver, 15) && DriverUtil.visibilityById("smsPwd", driver, 2)) {
								// 输入第二次短信验证码
								driver.findElement(By.id("smsPwd")).sendKeys(messageCode);
								// 点击校验验证码
								js.executeScript("ckeck_code();");
								
								logger.warn("-----检验结果----" + driver.getPageSource());
								
								if (DriverUtil.waitById("hint_info", driver, 6)
										&& DriverUtil.visibilityById("hint_info", driver, 1)) {
									// 检验失败后，失败原因
									String err = driver.findElement(By.id("hint_info")).getText();
									map.put("errorCode", "0002");
									map.put("errorInfo", err);
									
									driver.quit();
									
									PushSocket.pushnew(map, uuid, "3000", err);
				                    PushState.state(phoneCode, "callLog", 200, err);
									logger.warn("------陕西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
									return map;
								}
							}
						}
					}

				} catch (Exception e) {
					logger.error(phoneCode + "移动登录失败", e);
					map.put("errorCode", "0005");
					map.put("errorInfo", "网络繁忙");
					driver.manage().deleteAllCookies();
					driver.quit();
					logger.warn("------陕西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
					return map;
				}
				logger.warn("------陕西移动登录部分------结束-----手机号：" + phoneCode + "结果："+map.toString());
				logger.warn("------陕西移动获取详情部分------开始-----手机号：" + phoneCode);
				int a = 1;
				// js
				JavascriptExecutor js = (JavascriptExecutor) driver;
				List<String> data = new ArrayList<String>();
				try {
					
					// 获取详情
					if (DriverUtil.waitById("topfloat", driver, 15) && DriverUtil.visibilityById("topfloat", driver, 3)) {

						PushSocket.pushnew(map, uuid, "2000", "登录成功");

						PushSocket.pushnew(map, uuid, "5000", "数据获取中");
						a = 5;

						List<WebElement> list = driver.findElement(By.id("topfloat")).findElements(By.tagName("li"));

						for (int i = 0; i < list.size(); i++) {

							try {
								WebElement webElement = list.get(i);

								if (DriverUtil.clickByElement(webElement, driver, 10)) {
									// 点击月份
									webElement.click();
									Thread.sleep(2000);
									// 点击通话详单
									driver.findElement(By.className("icon4")).click();

									if (DriverUtil.waitByClassName("pack-time", driver, 15)
											&& DriverUtil.visibilityByClassName("pack-time", driver, 3)) {
										String detail = driver.getPageSource();
										logger.warn(phoneCode + "-----移动详单第" + i + "次获取成功，数据为" + detail);
										data.add(detail);
									} else {
										logger.warn(phoneCode + "-----移动详单第" + i + "次获取失败，数据为" + driver.getPageSource());
									}

									driver.get("http://wap.sn.10086.cn/h5/personal/html/detailedQuery.html");

									if (DriverUtil.waitById("topfloat", driver, 15)
											&& DriverUtil.visibilityById("topfloat", driver, 3)) {
										list = driver.findElement(By.id("topfloat")).findElements(By.tagName("li"));
									}
								}
							} catch (Exception e) {
								logger.error(phoneCode + "-----移动详单第" + i + "次获取失败，数据为" + driver.getPageSource());
							}
						}

						logger.warn(phoneCode + ": 该用户获取的数据总数为：" + data.size());
						if (data.size() < 3) {
							PushSocket.pushnew(map, uuid, "7000", "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过3个月)");
							PushState.state(phoneCode, "callLog", 200, "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过3个月)");
							map.put("errorCode", "0009");
							map.put("errorInfo", "数据获取不完全，请重新再次认证！(注：请确认手机号使用时长超过3个月)");

							driver.manage().deleteAllCookies();
							driver.quit();
							logger.warn("------陕西移动登录详情------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
							return map;
						}

						PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
						// 数据解析
						List<Map<String, String>> dataList = new PhoneBillsAnalysisImp().analysisHtml(data, phoneCode, "");
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
					logger.error(phoneCode + "移动获取详情失败", e);
					map.put("errorCode", "0005");
					map.put("errorInfo", "网络繁忙");
					PushState.state(phoneCode, "callLog", 200, "认证失败,系统繁忙！");
					PushSocket.pushnew(map, uuid, (a + 2) * 1000 + "", "认证失败,系统繁忙！");
				}finally {
					driver.manage().deleteAllCookies();
					driver.quit();
				}
				logger.warn("------陕西移动获取详情部分------结束-----手机号：" + phoneCode + "返回结果：" + map.toString());
			}
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
		public WebDriver getImg(HttpServletRequest request, WebDriver driver) throws Exception {

			JavascriptExecutor js = (JavascriptExecutor) driver;
			if (DriverUtil.waitById("validateCodeImg", driver, 10)
					&& DriverUtil.visibilityById("validateCodeImg", driver, 2)) {
				
				js.executeScript("changeCode()");
				
				// 获取图片Element
				WebElement verifyImg = driver.findElement(By.id("validateCodeImg"));
				// 验证码图片路径
				String path = request.getServletContext().getRealPath("/validateimg");
				String code = saveImg(verifyImg, driver, path, "yd", "png");

				js.executeScript("document.getElementById(\"validateCode\").focus();");

				// 输入图片验证码
				driver.findElement(By.id("validateCode")).sendKeys(code);
				Thread.sleep(2000);
				js.executeScript("document.getElementById(\"validateCode\").blur();");
				// 登录
				js.executeScript("login();remember_me();");

				if (DriverUtil.waitById("errorMsg", driver, 5) && DriverUtil.visibilityById("errorMsg", driver, 5)) {
					String tips = driver.findElement(By.id("errorMsg")).getText();
					if ((!tips.contains("短信验证码") && tips.contains("验证码错误")) || tips.contains("图形验证码")) {
						driver = this.getImg(request, driver);
						return driver;
					}
				}
			}else {
				// 登录
				js.executeScript("login();remember_me();");
			}
			return driver;
		}

		/**
		 * 返回打码后的验证码
		 * 
		 * @param element
		 * @param driver
		 * @param path
		 *            图片绝对地址
		 * @param prefix
		 * @param suffix
		 * @return
		 * @throws Exception
		 */
		public static String saveImg(WebElement element, WebDriver driver, String path, String prefix, String suffix)
				throws Exception {
			File file = new File(path + File.separator);
			if (!file.exists()) {
				file.mkdir();
			}
			BufferedImage bufferedImage = createElementImages(driver, element);
			String fileName = prefix + System.currentTimeMillis() + "." + suffix;
			ImageIO.write(bufferedImage, suffix, new File(file, fileName));

			Map<String, Object> imagev = MyCYDMDemo.Imagev(file + "/" + fileName);
			// 读取图片验证码
			String code = imagev.get("strResult").toString();
			return code;
		}

		/**
		 * 截取验证码图片
		 * 
		 * @param driver
		 * @param webElement
		 * @return
		 * @throws IOException
		 */
		private static BufferedImage createElementImages(WebDriver driver, WebElement webElement) throws IOException {
			// 获得webElement的位置和大小。
			Point location = webElement.getLocation();
			Dimension size = webElement.getSize();
			// 创建全屏截图。
			BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(takeScreenshot(driver)));
			// 截取webElement所在位置的子图。
			BufferedImage croppedImage = originalImage.getSubimage(location.getX(), location.getY(), size.getWidth(),
					size.getHeight());
			return croppedImage;
		}

		private static byte[] takeScreenshot(WebDriver driver) throws IOException {
			WebDriver augmentedDriver = new Augmenter().augment(driver);
			return ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.BYTES);
		}
	  
	
}

package com.hoomsun.mobile.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.conn.HttpHostConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CollectingAlertHandler;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.hoomsun.mobile.analysis.PhoneBillsAnalysisImp;
import com.hoomsun.mobile.tools.ConstantInterface;
import com.hoomsun.mobile.tools.PushSocket;
import com.hoomsun.mobile.tools.PushState;
import com.hoomsun.mobile.tools.Resttemplate;

import net.sf.json.JSONObject;

/**
 * 移动运营商
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Service
public class ChinaMobileService {
    private long timeStamp = System.currentTimeMillis();
    private String path = "";
    private Logger logger = LoggerFactory.getLogger(ChinaMobileService.class);

    
    /**
     * 获取移动验证码
     * @param request
     * @param userNumber
     * @return  
     * @Description:
     */
    public Map<String, String> getChinaMobileCode(HttpServletRequest request, String userNumber) {
        Map<String, String> map = new HashMap<String, String>(16);
        HttpSession session = request.getSession();

//        logger.warn("为" + userNumber + "： 用户分配新----------------------------移动");
//        String result = Scheduler.sendGet(Scheduler.getIp);
//        if (result.contains("请在1秒后再次请求")) {
//            logger.warn("为" + userNumber + "： 分配ip时访问过快--------------------移动");
//            map.put("errorCode", "0001");
//            map.put("errorInfo", "当前使用系统人数过多，请点击重试！");
//            return map;
//        }
//        logger.warn(userNumber + "： 用户本次分配到的ip为：" + Scheduler.ip + ":" + Scheduler.port + "----------------------------移动");

        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        
//         final WebClient webClient = new WebClient(BrowserVersion.CHROME, Scheduler.ip, Scheduler.port);
         // 开启cookie管理
         webClient.getCookieManager().setCookiesEnabled(true);
         webClient.getOptions().setCssEnabled(false);
         webClient.getOptions().setTimeout(900000);
         webClient.getOptions().setJavaScriptEnabled(true);
         webClient.setJavaScriptTimeout(900000);
         webClient.getOptions().setRedirectEnabled(true);
         webClient.getOptions().setThrowExceptionOnScriptError(false);
         webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
         webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        //验证是否是移动用户
        try {
            TextPage page = webClient.getPage("https://login.10086.cn/chkNumberAction.action?userName=" + userNumber);
            String statusFalse = "false";
            if (statusFalse.equals(page.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "非移动用户请注册互联网用户登录");
                webClient.close();
                return map;
            }
            //发送登录手机验证码
            TextPage page1 = webClient.getPage("https://login.10086.cn/sendRandomCodeAction.action?userName=" + userNumber + "&type=01&channelID=12003");
            String returnBack1 = "0";
            String returnBack2 = "4005";
            String returnBack3 = "1";
            String returnBack4 = "2";
            String returnBack5 = "3";
            if (returnBack1.equals(page1.getContent())) {
                map.put("errorCode", "0000");
                map.put("errorInfo", "已将短信随机码发送至手机，请查收!");
            } else if (returnBack2.equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "手机号码有误，请重新输入!");
            } else if (returnBack3.equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "对不起，短信随机码暂时不能发送，请一分钟以后再试！");
            } else if (returnBack4.equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "短信下发数已达上限！");
            } else if (returnBack5.equals(page1.getContent())) {
                map.put("errorCode", "0001");
                map.put("errorInfo", "对不起，短信发送次数过于频繁！");
            } else {
                map.put("errorCode", "0002");
                map.put("errorInfo", "短信验证码发送错误");
            }
            session.setAttribute(userNumber + "YD-webClient", webClient);
        } catch (HttpHostConnectException e) {
            map.put("errorCode", "0003");
            map.put("errorInfo", "网络繁忙，请刷新后重新再试");
            logger.warn(e.getMessage() + "  获取移动验证码   mrlu", e);
            webClient.close();
        } catch (Exception e) {
            logger.warn(e.getMessage() + "  获取移动验证码   mrlu", e);
            map.put("errorCode", "0003");
            map.put("errorInfo", "网络繁忙，请刷新后重新再试");
            webClient.close();
        }
        return map;
    }

    /**
     * 登录
     * @param request
     * @param userNumber
     * @param duanxinCode
     * @return  
     * @Description:
     */
    public Map<String, String> chinaMobilLoad(HttpServletRequest request, String userNumber, String duanxinCode) {
        Map<String, String> map = new HashMap<String, String>(16);
        HttpSession session = request.getSession();

        Object client = session.getAttribute(userNumber + "YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
        	//执行官网js对短信验证码进行加密
        	WebClient webClient = (WebClient) client;
            try {
                HtmlPage page = webClient.getPage("https://login.10086.cn/login.html");
                Object javaScriptResult = page.executeJavaScript("encrypt(\"" + duanxinCode + "\")").getJavaScriptResult();
                System.out.println(javaScriptResult.toString());

                String loadPath = "https://login.10086.cn/login.htm";
                //登录
                URL url = new URL(loadPath);
                WebRequest webRequest = new WebRequest(url);
                webRequest.setHttpMethod(HttpMethod.GET);
                webRequest.setAdditionalHeader("Referer", "https://login.10086.cn/login.html?channelID=12003&backUrl=http://shop.10086.cn/i/");
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new NameValuePair("accountType", "01"));
                list.add(new NameValuePair("account", userNumber));
                list.add(new NameValuePair("password", javaScriptResult.toString()));
                list.add(new NameValuePair("pwdType", "02"));
                list.add(new NameValuePair("smsPwd", ""));
                list.add(new NameValuePair("inputCode", ""));
                list.add(new NameValuePair("backUrl", "http://shop.10086.cn/i/"));
                list.add(new NameValuePair("rememberMe", "0"));
                list.add(new NameValuePair("channelID", "12003"));
                list.add(new NameValuePair("protocol", "https:"));
                String ls = String.valueOf(System.currentTimeMillis());
                list.add(new NameValuePair("timestamp", ls));

                webRequest.setRequestParameters(list);
                UnexpectedPage page2 = webClient.getPage(webRequest);

                JSONObject jsonObject = JSONObject.fromObject(page2.getWebResponse().getContentAsString());
                String keyResult = "result";
                String keyCode = "code";
                if (jsonObject.get(keyResult) == null || jsonObject.get(keyCode) == null) {
                    map.put("errorCode", "0003");
                    map.put("errorInfo", "服务器繁忙");
                    return map;
                }

                String result = jsonObject.get("result").toString();
                String code = jsonObject.get("code").toString();
                String backCode = "0";
                if (!backCode.equals(result)) {
                    String backCode2 = "6001";
                    String backCode3 = "6002";
                    if (backCode2.equals(code) || backCode3.equals(code)) {
                        map.put("errorCode", "0002");
                        map.put("errorInfo", "短信随机码不正确或已过期，请重新获取");
                        return map;
                    } else {
                        map.put("errorCode", "0003");
                        map.put("errorInfo", "系统繁忙");
                        return map;
                    }
                }

                //跳转到个人中心页面
                String assertAcceptURL = jsonObject.get("assertAcceptURL").toString();
                String artifact = jsonObject.get("artifact").toString();
                String backUrl = "http://shop.10086.cn/i/?f=home&welcome="+System.currentTimeMillis();

                path = assertAcceptURL + "?backUrl=" + backUrl + "&artifact=" + artifact;
                webClient.getPage(path);
                Thread.sleep(2000);
                webClient.getPage("http://shop.10086.cn/i/v1/cust/message/"+userNumber+"/noread?_=" + System.currentTimeMillis());
                webClient.getPage("http://shop.10086.cn/i/v1/auth/loginfo?_="+ System.currentTimeMillis());
                webClient.getPage("http://shop.10086.cn/i/v1/cust/mergecust/"+userNumber+"?_="+ System.currentTimeMillis());
                webClient.getPage("http://shop.10086.cn/i/v1/cust/cartNumber/"+userNumber+"?userId="+userNumber+"&userType=0&_="+ System.currentTimeMillis());
                UnexpectedPage info = webClient.getPage("http://shop.10086.cn/i/v1/cust/mergecust/"+userNumber+"?_="+ System.currentTimeMillis());
                logger.warn("详细信息："+info.getWebResponse().getContentAsString());
//                进入详单页面
                String path2 = "http://shop.10086.cn/i/apps/serviceapps/billdetail/index.html";
                webClient.getPage(path2);
                Thread.sleep(3000);
                //判断当前登录用户地区是否开放此功能
                UnexpectedPage page5 = webClient.getPage("http://shop.10086.cn/i/v1/res/funcavl?_=" + System.currentTimeMillis());


                JSONObject jsonObject1 = JSONObject.fromObject(page5.getWebResponse().getContentAsString());
                String retMsg = "retMsg";
                String accessMsg = "可用性成功";
                if (jsonObject1.get(retMsg) == null || !jsonObject1.get(retMsg).toString().contains(accessMsg)) {
                    map.put("errorCode", "0004");
                    map.put("errorInfo", "抱歉，暂时不提供该地区用户信息");
                    return map;
                }
                session.setAttribute(userNumber + "YD-webClient", webClient);
                map.put("errorCode", "0000");
                map.put("errorInfo", "操作成功");
            } catch (Exception e) {
                logger.error("  移动登录失败  mrlu", e);
                webClient.getCookieManager().clearCookies();
                map.put("errorCode", "0005");
                map.put("errorInfo", "网络繁忙");
                webClient.close();
            }
        }
        return map;
    }


    /**
     * 获取图片验证码
     * @param request
     * @return  
     * @Description:
     */
    public Map<String, Object> getDetialImageCode(HttpServletRequest request, String userNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Map<String, String> mapPath = new HashMap<String, String>(16);

        HttpSession session = request.getSession();

        Object client = session.getAttribute(userNumber + "YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            try {
                WebClient webClient = (WebClient) client;
                UnexpectedPage page7 = webClient.getPage("http://shop.10086.cn/i/authImg?t=" + Math.random());
                System.out.println(page7.getWebResponse().getContentAsString());
                String path = request.getServletContext().getRealPath("/vecImageCode");
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                String fileName = "yidong" + System.currentTimeMillis() + ".png";
                BufferedImage bi = ImageIO.read(page7.getInputStream());
                ImageIO.write(bi, "png", new File(file, fileName));


                mapPath.put("imagePath", request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/vecImageCode/" + fileName);
                map.put("data", mapPath);
                map.put("errorCode", "0000");
                map.put("errorInfo", "验证码获取成功");
            } catch (Exception e) {
                logger.error("  获取移动详单图片验证码失败   mrlu", e);
                map.put("errorCode", "0002");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }

    /**
     * 发送短信验证码
     * @param request
     * @param userNumber
     * @return
     */
    public Map<String, String> getDetialMobilCode(HttpServletRequest request, String userNumber) {

        Map<String, String> map = new HashMap<String, String>(16);
        HttpSession session = request.getSession();

        Object client = session.getAttribute(userNumber + "YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            return map;
        } else {
            WebClient webClient = (WebClient) client;
            try {
            	webClient.getPage("http://shop.10086.cn/i/apps/serviceapps/billdetail/showvec.html");
            	long time = System.currentTimeMillis();
            	String jQueryStr = "jQuery" + time + "520_"+System.currentTimeMillis();
                session.setAttribute("jQueryStr",jQueryStr);
                //获取通话详单时发送手机验证码
                URL url1 = new URL("https://shop.10086.cn/i/v1/fee/detbillrandomcodejsonp/" + userNumber + "?callback=" + jQueryStr +"&_=" + System.currentTimeMillis());

                WebRequest webRequest1 = new WebRequest(url1);
                webRequest1.setHttpMethod(HttpMethod.GET);
                webRequest1.setAdditionalHeader("Referer", "http://shop.10086.cn/i/?f=billdetailqry&welcome=" + System.currentTimeMillis());
                UnexpectedPage page6 = webClient.getPage(webRequest1);
                String statusSuccess = "success";
                if (!page6.getWebResponse().getContentAsString().contains(statusSuccess)) {
                    try {

                        String results = page6.getWebResponse().getContentAsString();
                        int s = (jQueryStr + "(").length();
                        String json = results.substring(s);
                        results = json.substring(0, json.length() - 1);
                        JSONObject jsonObject = JSONObject.fromObject(results);
                        String infoMes = jsonObject.get("retMsg").toString();
                        if (infoMes.contains("信息为空")) {
                            logger.warn(userNumber+"移动运营商出现session信息为空情况，跳转第三方页面认证---------------------");
                            infoMes = "认证过程中出现未知错误，请返回上一页面重新开始认证;";
                            map.put("errorCode", "1011");
                            map.put("errorInfo", infoMes);
                        }else{
                            map.put("errorCode", "0002");
                            map.put("errorInfo", infoMes);
                        }
                        return map;
                    } catch (Exception e) {
                        map.put("errorCode", "0002");
                        map.put("errorInfo", "短信发送失败");
                        return map;
                    }

                }else {
                	map.put("errorCode", "0000");
                	map.put("errorInfo", "短信发送成功");
                }
            } catch (Exception e) {
                logger.error("  获取移动详单手机验证码   mrlu", e);
                map.put("errorCode", "0003");
                map.put("errorInfo", "系统繁忙");
            }
        }
        return map;
    }

    /**
     * 获取详情
     * @param request
     * @param userNumber 
     * @param phoneCode
     * @param fuwuSec
     * @param imageCode
     * @param longitude
     * @param latitude
     * @param uuid
     * @return
     * @throws InterruptedException  
     * @Description:
     */
    public Map<String, Object> getDetailAccount(HttpServletRequest request, String userNumber, String phoneCode,
                                                String fuwuSec, String imageCode, String longitude, String latitude, String uuid) {
       
    	
    	
    	Map<String, Object> map = new HashMap<String, Object>(16);
        System.out.println("---移动---" + userNumber);

        PushSocket.pushnew(map, uuid, "1000", "登录中");
        PushState.state(userNumber, "callLog", 100);
        String signle = "1000";

        Map<String, Object> dataMap = new HashMap<String, Object>(16);
        HttpSession session = request.getSession();

        Object client = session.getAttribute(userNumber + "YD-webClient");
        if (client == null) {
            map.put("errorCode", "0001");
            map.put("errorInfo", "登录超时");
            PushSocket.pushnew(map, uuid, "3000", "登录超时");
            PushState.state(userNumber, "callLog", 200, "登录超时。");
            return map;
        } else {
        	WebClient webClient = (WebClient) client;
            try {

                UnexpectedPage precheck = webClient.getPage("http://shop.10086.cn/i/v1/res/precheck/"+userNumber+"?captchaVal="+imageCode+"&_="+System.currentTimeMillis());
                logger.warn("验证码预校验："+precheck.getWebResponse().getContentAsString());
                HtmlPage page3 = webClient.getPage(path);
                //对服务密码和手机验证码进行加密
                List<String> alert = new ArrayList<String>();
                CollectingAlertHandler collectingAlertHandler = new CollectingAlertHandler(alert);
                webClient.setAlertHandler(collectingAlertHandler);

                page3.executeJavaScript("alert(base64encode(utf16to8('" + fuwuSec + "')))");
                page3.executeJavaScript("alert(base64encode(utf16to8('" + phoneCode + "')))");

                String pwdTempSerCode = "";
                String pwdTempRandCode = "";
                if (alert.size() > 0) {
                    pwdTempSerCode = alert.get(0);
                    pwdTempRandCode = alert.get(1);
                }
                String str = userNumber.substring(0, 8) + System.currentTimeMillis();
                str = session.getAttribute("jQueryStr").toString();
                //身份进行二次验证
                URL url2 = new URL("https://shop.10086.cn/i/v1/fee/detailbilltempidentjsonp/" + userNumber +
                        "?callback=" + str + "&pwdTempSerCode=" + pwdTempSerCode +
                        "&pwdTempRandCode=" + pwdTempRandCode + "&captchaVal=" + imageCode + "&_=" + System.currentTimeMillis());

                WebRequest webRequest2 = new WebRequest(url2);
                webRequest2.setAdditionalHeader("Cookie", this.getCookie(webClient));
                webClient.addRequestHeader("Referer",  "http://shop.10086.cn/i/?f=billdetailqry&welcome=" + System.currentTimeMillis());
                webClient.addRequestHeader("Host",  "shop.10086.cn");
                webClient.addRequestHeader("Accept-Language",  "zh-CN");
                webClient.addRequestHeader("Connection",  "Keep-Alive");
                webClient.addRequestHeader("Accept",  "application/javascript, */*;q=0.8");
                webClient.addRequestHeader("User-Agent",  "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
                webClient.addRequestHeader("Accept-Encoding",  "gzip, deflate");
                
                webRequest2.setHttpMethod(HttpMethod.GET);
                UnexpectedPage page8 = webClient.getPage(webRequest2);
                Thread.sleep(1000);
                String result = page8.getWebResponse().getContentAsString();
                String successStatus = "认证成功";
                logger.warn(userNumber + ":本次二次身份认证结果为:" + result);
                if (!page8.getWebResponse().getContentAsString().contains(successStatus)) {
                    String jquerys = "jQuery";
                    if (result.contains(jquerys)) {
                        int s = (str + "(").length();
                        String json = result.substring(s);
                        result = json.substring(0, json.length() - 1);
                    }
                    JSONObject jsonObject = JSONObject.fromObject(result);

                    String retMsg = jsonObject.get("retMsg").toString();

                    if (retMsg.contains("请先登录")) {
                        logger.warn(System.currentTimeMillis() + ": " + userNumber + ":移动二次身份认证时出现session信息为空，跳转第三方页面认证！原信息为:" + retMsg);
                        retMsg = "系统繁忙，请稍后重试";
                        PushSocket.pushnew(map, uuid, "7000", retMsg);
                        PushState.state(userNumber, "callLog", 200, retMsg);
                    }else{
                        PushSocket.pushnew(map, uuid, "3000", retMsg);
                        PushState.state(userNumber, "callLog", 200, retMsg);
                    }
                    map.put("errorCode", "0002"); 
                    map.put("errorInfo", retMsg);
                    return map;
                }
                PushSocket.pushnew(map, uuid, "2000", "登录成功");

                PushSocket.pushnew(map, uuid, "5000", "数据获取中");
                signle = "5000";
                
                List<String> dataList = this.getDetial(userNumber, webClient,str);
                
                //退出
                url2 = new URL("http://shop.10086.cn/i/v1/auth/userlogout?_=" + System.currentTimeMillis());

                webRequest2 = new WebRequest(url2);
                webRequest2.setAdditionalHeader("Cookie", this.getCookie(webClient));
                webClient.addRequestHeader("Referer",  "http://shop.10086.cn/i/?f=home&welcome=" + System.currentTimeMillis());
                webClient.addRequestHeader("Host",  "shop.10086.cn");
                webClient.addRequestHeader("Accept-Language",  "zh-CN");
                webClient.addRequestHeader("Connection",  "Keep-Alive");
                webClient.addRequestHeader("Accept",  "application/json, text/javascript, */*; q=0.01");
                webClient.addRequestHeader("User-Agent",  "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
                
                webRequest2.setHttpMethod(HttpMethod.GET);
                
                webClient.getPage(webRequest2);
                
                logger.warn(userNumber + ": 该用户获取的数据总数为：" + dataList.size());
                int boundCount3 = 3;
                if (dataList.size() < boundCount3) {
                    PushSocket.pushnew(map, uuid, "7000", "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过3个月,若手机号使用时长超过三个月，请明天再试！)");
                    PushState.state(userNumber, "callLog", 200, "数据获取不完全，请重新认证！(注：请确认手机号使用时长超过3个月,若手机号使用时长超过三个月，请明天再试！)");
                    map.put("errorCode", "0009");
                    map.put("errorInfo", "数据获取不完全，请重新再次认证！(注：请确认手机号使用时长超过3个月,若手机号使用时长超过三个月，请明天再试！)");
                    return map;
                }
                PushSocket.pushnew(map, uuid, "6000", "数据获取成功");
                signle = "4000";
            /*    //通话详单数据
                dataMap.put("data", dataList);
                //手机
                dataMap.put("userPhone", userNumber);
                //服务密码
                dataMap.put("serverCard", fuwuSec);
                //经度
                dataMap.put("longitude", longitude);
                //纬度
                dataMap.put("latitude", latitude);
                map.put("errorCode", "0000");
                map.put("errorInfo", "查询成功");
                map.put("data", dataList.toString());
                map = new Resttemplate().SendMessage(dataMap, ConstantInterface.port + "/HSDC/message/mobileCallRecord");*/
                
                List<Map<String, String>> data = new PhoneBillsAnalysisImp().analysisJson(dataList, userNumber, "");
                map.put("phone", userNumber);//认证手机号
                map.put("pwd", fuwuSec);//手机查询密码
                map.put("longitude", longitude);//经度
                map.put("latitude", latitude);//维度
                map.put("data", data);//通话详单
                map = new Resttemplate().SendMessage(map, ConstantInterface.port + "/HSDC/message/operator");
                
                //推送结果  未写
                String statusResukt = "0000";
                String statusCode = "errorCode";
                if (statusResukt.equals(map.get(statusCode))) {
                    PushSocket.pushnew(map, uuid, "8000", "认证成功");
                    PushState.state(userNumber, "callLog", 300);
                } else {
                    PushSocket.pushnew(map, uuid, "9000", map.get("errorInfo").toString());
                    PushState.state(userNumber, "callLog", 200, map.get("errorInfo").toString());
                }
                webClient.getCookieManager().clearCookies();
                webClient.close();
            } catch (Exception e) {
                logger.error(" -------------------- 获取移动详单异常  mrlu----------", e);
                map.put("errorCode", "0004");
                map.put("errorInfo", "系统繁忙");
                PushState.state(userNumber, "callLog", 200, "认证失败,系统繁忙！");
                webClient.getCookieManager().clearCookies();
                webClient.close();
            }
        }
        return map;
    }

    
    /**
	 * 获取移动详单
	 * @param userNumber
	 * @param webClient
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 * @throws InterruptedException 
	 */
	private List<String> getDetial(String userNumber,WebClient webClient,String str) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		//获取当前时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		String sDate = simpleDateFormat.format(cal.getTime());
		
		//存放每个月的详单
		List<String> datas = new ArrayList<String>();
		//存放出错的月份
		List<String> errorDates = new ArrayList<String>();
		
		int boundCount = 7;
		loop: for (int i = 1; i < boundCount; i++) {
			logger.warn(userNumber + "     " + i + "次开始获取数据。日期 " + sDate);
			String results = "";
			
			//获取每个月的详单
			Object page9 = webClient.getPage("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/" + userNumber
					+ "?callback="+ str + "&curCuror=1&step=200&qryMonth="
					+ sDate + "&billType=02&_=" + System.currentTimeMillis());
			
			
			if (page9 instanceof HtmlPage) {
				logger.warn(userNumber + "   " + "第"+i+"次请求出错  " + sDate);
				for (int j = 0; j < 3; j++) {
					logger.warn(userNumber + "   " + "请求出错，进行重试，现在开始第" + j + "次请求  " + sDate);
					Object page10 = webClient.getPage("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/"
							+ userNumber + "?callback=" + str
							+ "&curCuror=1&step=200&qryMonth=" + sDate + "&billType=02&_="
							+ System.currentTimeMillis());
					
					
					if (page10 instanceof UnexpectedPage) {
						UnexpectedPage pages = (UnexpectedPage) page10;
						results = pages.getWebResponse().getContentAsString();
						
						if(!results.contains("临时身份凭证不存在")) {
							logger.warn(userNumber + "   " + j + ":本次请求成功 " + sDate +"    result:" + results);
							datas.add(this.getData(results,str));
						}else {
							logger.warn(userNumber + "   " + j + ":本次请求失败 " + sDate +"    result:" + results);
						}
						break;
					} else if (j == 2) {
						logger.warn(userNumber + "   " + sDate + "：三次未请求到数据，跳过进行下一月账单读取");
						errorDates.add(sDate);
						cal.add(Calendar.MONTH, -1);
						sDate = simpleDateFormat.format(cal.getTime());
						continue loop;
					}
					Thread.sleep(2000);
				}
				
				
			} else {
				UnexpectedPage pages = (UnexpectedPage) page9;
				results = pages.getWebResponse().getContentAsString();
				
				if(!results.contains("临时身份凭证不存在")) {
					logger.warn(userNumber + "   " + i + "次请求成功!" + sDate +"    result:" + results );
					datas.add(this.getData(results,str));
				}else {
					logger.warn(userNumber + "   " + i + "次请求失败!" + sDate +"    result:" + results );
				}
			}

//			int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
//			String json = results.substring(s);
//			results = json.substring(0, json.length() - 1);

			// 判断数据真伪与重复性
//			if (results.contains("startTime") && results.contains("commPlac")) {
//				JSONObject jsonObject = JSONObject.fromObject(results);
//				String startDate = jsonObject.getString("startDate");
				// 确认获取的6个月账单不会重复
//				if (!startDates.contains(startDate)) {
//					datas.add(results);
//					startDates.add(startDate);
//				} else {
//					logger.warn(userNumber + ":   获取时间:" + sDate + "   " + i + "次请求数据为重复数据,数据为:---------"
//							+ results + "---------");
//				}
//			} else {
//				logger.warn(userNumber + ":   获取时间:" + sDate + "   " + i + "次请求数据异常,异常数据为:---------" + results
//						+ "---------");
//			}
			cal.add(Calendar.MONTH, -1);
			sDate = simpleDateFormat.format(cal.getTime());
			Thread.sleep(2000);
		}
		if(errorDates.size() > 0) {
			
			loop2:for (int k = 0; k < errorDates.size(); k++) {
				
				    String currentDate = errorDates.get(0);
				
					String results = "";
					
					for (int k2 = 0; k2 < 3; k2++) {
						Object page10 = webClient.getPage("https://shop.10086.cn/i/v1/fee/detailbillinfojsonp/"
								+ userNumber + "?callback="+ str
								+ "&curCuror=1&step=200&qryMonth=" + currentDate + "&billType=02&_="
								+ System.currentTimeMillis());
						if (page10 instanceof UnexpectedPage) {
							UnexpectedPage pages = (UnexpectedPage) page10;
							results = pages.getWebResponse().getContentAsString();
							logger.warn(userNumber + "重试成功,日期为："+ currentDate + "数据为：" + results);
							k2 = 4;
							datas.add(this.getData(results,str));
						}
						if(k2 == 2) {
							continue loop2;
						}
					}
					
//					int s = ("jQuery183045411546722870333_" + timeStamp + "(").length();
//					String json = results.substring(s);
//					results = json.substring(0, json.length() - 1);
	
					// 判断数据真伪与重复性
//					if (results.contains("startTime") && results.contains("commPlac")) {
//						JSONObject jsonObject = JSONObject.fromObject(results);
//						String startDate = jsonObject.getString("startDate");
//						// 确认获取的6个月账单不会重复
//						if (!startDates.contains(startDate)) {
//							datas.add(results);
//							startDates.add(startDate);
//						} else {
//							logger.warn(userNumber + ":   获取时间:" + currentDate + "   " + k + "次请求数据为重复数据,数据为:---------"
//									+ results + "---------");
//						}
//					} else {
//						logger.warn(userNumber + ":   获取时间:" + currentDate + "   " + k + "次请求数据异常,异常数据为:---------" + results
//								+ "---------");
//					}
					Thread.sleep(2000);
			}
		}
		return datas;
	}
	
    private String getData(String results,String str ) {
		int s = ("jQuery"+ str + "_" + timeStamp + "(").length();
		String json = results.substring(s);
		results = json.substring(0, json.length() - 1);
		return results;
    }
	
	/**
	 * 获取cookie
	 * 
	 * @param
	 * @return
	 */
	private String getCookie(WebClient webClient) {
		// 获得cookie用于发包
		Set<Cookie> cookies = webClient.getCookieManager().getCookies();
		StringBuffer tmpcookies = new StringBuffer();

		for (Cookie cookie : cookies) {
			String name = cookie.getName();
			String value = cookie.getValue();
			tmpcookies.append(name + "=" + value + ";");
		}
		String str = tmpcookies.toString();
		if (!str.isEmpty()) {
			str = str.substring(0, str.lastIndexOf(";"));
		}
		return str;
	}
	
}

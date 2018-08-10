
package com.hoomsun.mobile.tools;

/**
 * 配置一些常量
 * @author 
 * @date
 */
public interface ConstantInterface {
    /**
     * 公用配置
     */
    String ieDriverKey = "webdriver.ie.driver";
    String chromeDriverKey = "webdriver.chrome.driver";
    String phantomJsKey = "phantomjs.binary.path";
  
    /**
     * 测试环境
     */

//    String MyCYDMDemoDLLPATH = "C://yundamaAPI-x64.dll";
//    String port = "http://117.34.70.217:8080";
//   // String port = "http://192.168.3.4:8080";
//    String ieDriverValue = "C:\\Program Files\\iedriver\\IEDriverServer.exe";
//    String chromeDriverValue = "C:\\Program Files\\iedriver\\chromedriver.exe";
//    String phantomJsValue = "C:\\Program Files\\iedriver\\phantomjs.exe";
//    String getAddress="http://113.200.105.34:8065";
//    String ip="117.34.70.214:8080";

    

  /**
  * 正式环境
  */
     String MyCYDMDemoDLLPATH = "C://yundamaAPI-x64.dll";
     String port="http://10.1.1.12:8080";
     String ieDriverValue = "C:\\Program Files\\iedriver\\IEDriverServer.exe";
     String chromeDriverValue = "C:\\Program Files\\iedriver\\chromedriver.exe";
     String phantomJsValue = "C:\\Program Files\\iedriver\\phantomjs.exe";
     String getAddress="http://10.1.1.17:8065";
     String ip="http://weisha.fanjiedata.com:8080";//113.200.105.35


//    
//    /***
//     * 解决客户认证不了的问题
//     */
//    String MyCYDMDemoDLLPATH = "C://yundamaAPI-x64.dll";
//    String port="data.fanjiedata.com:8080";
//    String ieDriverValue = "C:\\Program Files\\iedriver\\IEDriverServer.exe";
//    String chromeDriverValue = "C:\\Program Files\\iedriver\\chromedriver.exe";
//    String phantomJsValue = "C:\\Program Files\\iedriver\\phantomjs.exe";
//    String getAddress="http://113.200.105.34:8065";
//    String ip="weisha.fanjiedata.com:8080";//113.200.105.35
//    String hm = "http://117.34.70.217:8080";
}


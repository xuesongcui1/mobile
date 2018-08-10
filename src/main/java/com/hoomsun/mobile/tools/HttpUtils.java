package com.hoomsun.mobile.tools;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by Howard on 2017/11/6
 */
public class HttpUtils {

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip)) {
            ip = ip.split(",")[0];
        }
        return ip;

    }


    public JSONObject getConnection(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.connect();

        JSONObject jsono = JSONObject.fromObject(IOUtils.toString(connection.getInputStream()));
        connection.disconnect();
        return jsono;
    }

    public static String sendInfo(String sendUrl, String data) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(sendUrl);
        StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);// 构造请求数据
        post.setEntity(myEntity);// 设置请求体
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity, "UTF-8");
            }
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static String getJson(String url) {
        String result = null;
        try {
            // 根据地址获取请求
            HttpGet request = new HttpGet(url);
            // 获取当前客户端对象
            HttpClient httpClient = new DefaultHttpClient();

            // 通过请求对象获取响应对象
            HttpResponse response = httpClient.execute(request);
            GetMethod method = new GetMethod();

            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        } catch (Exception e) {

            return null;
        }
        return result;
    }


    public static JSONObject httpsRequest(String requestUrl, String requestMethod, String outputStr) throws Exception {
        JSONObject jsonObject = null;
        // 创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager[] tm = {new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};

        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return true;
            }
        });
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        URL url = new URL(requestUrl);
        HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
        httpUrlConn.setSSLSocketFactory(ssf);
        httpUrlConn.setReadTimeout(10000);
        httpUrlConn.setConnectTimeout(12000);
        httpUrlConn.setDoOutput(true);
        httpUrlConn.setDoInput(true);
        httpUrlConn.setUseCaches(false);
        //httpUrlConn.setRequestProperty("User-Agent:","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
        // 设置请求方式（GET/POST）
        httpUrlConn.setRequestMethod(requestMethod);

        if ("GET".equalsIgnoreCase(requestMethod)) {
            httpUrlConn.connect();
        }

        // 当有数据需要提交时
        if (null != outputStr) {
            OutputStream outputStream = httpUrlConn.getOutputStream();
            // 注意编码格式，防止中文乱码
            outputStream.write(outputStr.getBytes("UTF-8"));
            outputStream.close();
        }

        // 将返回的输入流转换成字符串
        String buffer = IOUtils.toString(httpUrlConn.getInputStream());
        httpUrlConn.disconnect();
        jsonObject = JSONObject.fromObject(buffer.toString());
        return jsonObject;
    }

    public static String returnFeeSSLPost(SSLConnectionSocketFactory sslFactory, String url, String data) throws Exception {

        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslFactory).build();
        try {
            HttpPost httpost = new HttpPost(url);
            StringEntity myEntity = new StringEntity(data, ContentType.APPLICATION_JSON);// 构造请求数据
            httpost.setEntity(myEntity);// 设置请求体

            CloseableHttpResponse response = httpclient.execute(httpost);
            System.out.println("response:" + response);
            httpost.addHeader("Connection", "keep-alive");
            httpost.addHeader("Accept", "*/*");
            httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpost.addHeader("Host", "api.mch.weixin.qq.com");
            httpost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpost.addHeader("Cache-Control", "max-age=0");
            httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            httpost.setEntity(new StringEntity(data, "UTF-8"));

            try {
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    String jsonStr = EntityUtils.toString(entity, "UTF-8");
                    EntityUtils.consume(entity);
                    return jsonStr;
                }
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return "";
    }

    public static String getFullPath(HttpServletRequest request) {
        String basePath = request.getRequestURL().toString();
        String queryString = request.getQueryString();
        if (StringUtils.isNotEmpty(queryString)) {
            queryString = "?" + queryString;
        } else {
            queryString = "";
        }

        return basePath + queryString;
    }

    public static String sendPost(String url, String data) {
        String result="";
        BufferedReader reader = null;
        HttpURLConnection http = null;
        try {
            URL urlGet = new URL(url);
            http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("POST"); // 必须是get方式请求
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒

            byte[] writebytes = data.getBytes();
            // 设置文件长度
            http.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
            OutputStream outwritestream = http.getOutputStream();
            outwritestream.write(writebytes);
            outwritestream.flush();
            outwritestream.close();
            http.connect();
            System.out.println("PostData: "+http.getResponseCode());

            if (http.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(http.getInputStream()));
                result = reader.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (http != null) {
                http.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }




    public static String sendPost(String url, Map<String,String> data){
        try {
            CookieStore cookieStore = new BasicCookieStore();
            // 创建HttpClientBuilder
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setDefaultCookieStore(cookieStore);
            // HttpClient
            CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
            // 请求地址
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent","Mozilla/5.0 (Linux; U; Android 4.3; en-us; SM-N900T Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
            // 创建参数队列
            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            for(Map.Entry<String,String> entry : data.entrySet()){
                paramsList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));//数据
            }
            UrlEncodedFormEntity entity;
            entity = new UrlEncodedFormEntity(paramsList, "UTF-8");
            httpPost.setEntity(entity);
            CloseableHttpResponse response = closeableHttpClient.execute(httpPost);
            // getEntity()
            HttpEntity httpEntity = response.getEntity();
            int code = response.getStatusLine().getStatusCode();
            System.out.println("postDataCode:"+code);
            if (httpEntity != null) {
                return EntityUtils.toString(httpEntity, "UTF-8");
            }
        } catch (Exception e) {

        }
        return "{\"errorInfo\": \"推送超时!\",\"errorCode\": \"1111\"}";
    }

    public static String getSignNo(String str) {
        String text = "";
        str = str.substring(str.indexOf("?") + 1);
        String[] array = str.split("&");
        for (String item : array) {
            if (item.contains("sign_account_no")) {
                text = item.split("=")[1];
            }
        }
        return text;
    }

    public static String getCookie(CookieStore cookieStore){
        List<Cookie> cookies=cookieStore.getCookies();
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

    public static String getCookie(WebClient webClient) {
        // 获得cookie用于发包
        Set<com.gargoylesoftware.htmlunit.util.Cookie> cookies = webClient.getCookieManager().getCookies();
        StringBuffer tmpcookies = new StringBuffer();

        for (com.gargoylesoftware.htmlunit.util.Cookie cookie : cookies) {
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


    public static HttpClient getSSLInsecureClient() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
        return HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
    }

//    public static void main(String[] args) {
//        try {
//            StringBuilder accessTokenUrl = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token");
//            accessTokenUrl.append("?appid=").append("wx616de22f6ce97d9e");
//            accessTokenUrl.append("&secret=").append("4fe74b4b9e4db9112d98fdcbc421c5cb");
//            accessTokenUrl.append("&code=").append("12345648915321");
//            accessTokenUrl.append("&grant_type=authorization_code");
//            String result = getJson(accessTokenUrl.toString());
//            System.out.println(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}

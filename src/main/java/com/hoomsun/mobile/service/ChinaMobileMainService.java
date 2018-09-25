package com.hoomsun.mobile.service;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.hoomsun.mobile.dao.SupportMapper;
import com.hoomsun.mobile.tools.CrawlerUtil;
import com.hoomsun.mobile.tools.Dates;

/**
 * 移动动态配置service
 *@Title:  
 *@Description:  
 *@Author:Administrator  
 *@Since:2018年7月10日  
 *@Version:1.1.0
 */
@Service
public class ChinaMobileMainService {
	
	private Logger logger = LoggerFactory.getLogger(ChinaMobileMainService.class);

	@Autowired
	private SupportMapper supportmapper;
	
	
	/**
	 * 查询使用全国移动还是各省份移动
	 * 
	 * @param request
	 * @return
	 * @Description:
	 */
	public Map<String, Object> getMobileMethod(HttpServletRequest request, String phoneNumber) {
		
		logger.warn("--------移动动态配置查询开始-------手机号："+ phoneNumber);
		
		Map<String, Object> map = new HashMap<String, Object>(16);
		Map<String, Object> data = new HashMap<String, Object>(16);
		try {
			
			//是否为账单日,若是账单日，走全国移动
			boolean isBillDate = Dates.isBillDate();
			if(isBillDate) {
				map.put("data", data);
				map.put("errorCode", "0000");
				map.put("errorInfo", "查询成功");
				data.put("state", "0");
				logger.warn("--------移动动态配置查询结束-------手机号："+ phoneNumber + "返回结果："+ map.toString());
				return map;
			}
			
			
			//获取手机具体归属地
			CrawlerUtil craw = new CrawlerUtil();
			WebClient webClient = craw.setWebClient();
			WebRequest webRequest = new WebRequest(new URL(
					"http://www.ip138.com:8080/search.asp?mobile="+phoneNumber+"&action=mobile"));
			webRequest.setHttpMethod(HttpMethod.GET);
			HtmlPage page1 = webClient.getPage(webRequest);
			
			logger.warn(page1.asText());
			
			HtmlTableDataCell td =  (HtmlTableDataCell) page1.getByXPath("/html/body/table[2]/tbody/tr[3]/td[2]").get(0);
			
			String province = td.getTextContent();
			
			String[] ss = province.split(" ");
			
			province = ss[0];
			logger.warn("--------移动动态配置-------手机号："+ phoneNumber + "所在城市："+ province);
			
			Map<String, Object> info = supportmapper.queryOperatorManage("移动",province);
			
			map.put("data", data);
			map.put("errorCode", "0000");
			map.put("errorInfo", "查询成功");
			
			if(info != null && (info.get("support_flag") != null && info.get("support_flag").equals("1") )) {
				//支持该省份
				data.put("state", "1");
				int total =  (Integer)info.get("total_num");
				data.put("total_num", total);
				
				Map<String, Object> detail = new HashMap<String, Object>(16);
				data.put("detail", detail);
				for (int i = 1; i <= total; i++) {
					detail.put("params_"+ i, info.get("params_"+ i));
					String address = "http://" + info.get("long_link_ip") + info.get("address_"+ i);
					detail.put("address_"+ i, address);
				}
				data.put("longLinkIp", info.get("long_link_ip"));
				
			}else {
				info = supportmapper.queryOperatorManage("移动","全国");
				if(info != null && (info.get("support_flag") != null && info.get("support_flag").equals("1"))) {
					//支持全国接口
					data.put("state", "0");
				}else {
					//全国接口和各省份接口都不支持
					data.put("state", "2");
				}
			}
			
		} catch (IOException e) {
			logger.error("-------移动动态配置查询失败-----手机号："+ phoneNumber);
			map.put("errorCode", "0001");
			map.put("errorInfo", "系统繁忙，请稍后重试");
		}
		logger.warn("--------移动动态配置查询结束-------手机号："+ phoneNumber + "返回结果："+ map.toString());
		return map;
	}
}

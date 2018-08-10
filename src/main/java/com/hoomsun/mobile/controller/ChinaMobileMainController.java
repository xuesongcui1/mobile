package com.hoomsun.mobile.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hoomsun.mobile.service.ChinaMobileMainService;

import io.swagger.annotations.ApiOperation;

/**
 * 移动运营商
 *
 * @author HotWong
 * @date 2016/10/31
 */
@Controller
@RequestMapping("mobileDynamic")
public class ChinaMobileMainController {
	
    @Autowired
    private ChinaMobileMainService chinaMobileMainService;
    

    @ApiOperation(value = "移动动态配置查询", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "query",method = RequestMethod.POST)
    public Map<String,Object> getMobileMethod(HttpServletRequest request,String phoneCode) {
    	return chinaMobileMainService.getMobileMethod(request,phoneCode);
    }
    
    
    @ApiOperation(value = "杀进程")
    @ResponseBody
    @RequestMapping(value = "clearProcess",method = RequestMethod.POST)
    public boolean clearProcess(HttpServletRequest request) {
    	try {
    		Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
    		Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
		} catch (IOException e) {
			return false;
		}
    	return  true ;
    }
    
   
}

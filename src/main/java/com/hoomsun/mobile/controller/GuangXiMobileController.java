package com.hoomsun.mobile.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hoomsun.mobile.service.GuangXiMobileService;

import io.swagger.annotations.ApiOperation;
/**
 * 广西移动
 *@Title:  
 *@Description:  
 *@Author:Administrator  
 *@Since:2018年8月16日  
 *@Version:1.1.0
 */
@Controller
@RequestMapping("guangXiMobile")
public class GuangXiMobileController {
	private static Logger logger = LoggerFactory.getLogger(GuangXiMobileController.class);
	
	@Autowired
	private GuangXiMobileService guangXiMobileService;
	 
    @ApiOperation(value = "第一步,啥也不做", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "doGetCode",method = RequestMethod.POST)
    public Map<String,String> doGetCode(HttpServletRequest request, @RequestParam("phoneCode")String phoneCode) {
    	logger.warn("-------广西移动第一步开始，手机号："+phoneCode+"---------");
        return guangXiMobileService.doGetCode(request,phoneCode);
    }

    @ApiOperation(value = "第二步，获取账单信息", notes = "参数：手机号码，短信验证码，服务密码")
    @ResponseBody
    @RequestMapping(value = "doGetDetail",method = RequestMethod.POST)
    public Map<String,Object> doGetDetail(HttpServletRequest request, @RequestParam("phoneCode")String phoneCode, @RequestParam("messageCode")String messageCode,
                                               @RequestParam("servicePassword")String servicePassword,  @RequestParam("longitude") String longitude, 
                                               @RequestParam("latitude") String latitude,@RequestParam("UUID")String uuid ,@RequestParam("phoneType")String phoneType ){
    	logger.warn("-------广西移动获取详单control开始---------手机号："+phoneCode);
    	return guangXiMobileService.doGetDetail(request, phoneCode, messageCode, servicePassword, longitude, latitude, uuid,phoneType);
    }
}

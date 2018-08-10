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

import com.hoomsun.mobile.service.ShanXiMobileService;

import io.swagger.annotations.ApiOperation;

/**
 * 
 *@Title:  陕西移动
 *@Description:  
 *@Author:Administrator  
 *@Since:2018年7月10日  
 *@Version:1.1.0
 */
/**
 * phoneCode 手机号
 * messageCode  短信验证码
 * servicePassword 服务密码
 * idCard   身份证号
 * userName  姓名
 * longitude 经度
 * latitude 纬度
 * UUID
 */
@Controller
@RequestMapping("shanXiMobile")
public class ShanXiMobileController {

	private static Logger logger = LoggerFactory.getLogger(ShanXiMobileController.class);
	
	@Autowired
	private ShanXiMobileService shanXiMobileService;
	 
    @ApiOperation(value = "第一步，获取移动手机验证码", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "doGetCode",method = RequestMethod.POST)
    public Map<String,String> doGetCode(HttpServletRequest request, @RequestParam("phoneCode")String phoneCode) {
    	logger.warn("-------陕西移动获取登录手机验证码control开始，手机号："+phoneCode+"---------");
        return shanXiMobileService.doGetCode(request,phoneCode);
    }

 /*   @ApiOperation(value = "第二步，输入手机验证码+手机号", notes = "参数：手机号码，手机验证码")
    @ResponseBody
    @RequestMapping(value = "doLogin",method = RequestMethod.POST)
    public Map<String,Object> doLogin(HttpServletRequest request, @RequestParam("phoneCode") String phoneCode, @RequestParam("messageCode") String messageCode) {
    	logger.warn("-------陕西移动登录control开始，手机号："+phoneCode+"---------");
    	return shanXiMobileService.doLogin(request,phoneCode,messageCode);
    }*/

    @ApiOperation(value = "第二步，获取账单信息", notes = "参数：手机号码，短信验证码，服务密码")
    @ResponseBody
    @RequestMapping(value = "doGetDetail",method = RequestMethod.POST)
    public Map<String,Object> doGetDetail(HttpServletRequest request, @RequestParam("phoneCode")String phoneCode, @RequestParam("messageCode")String messageCode,
                                               @RequestParam("servicePassword")String servicePassword,  @RequestParam("longitude") String longitude, 
                                               @RequestParam("latitude") String latitude,@RequestParam("UUID")String uuid ,@RequestParam("phoneType")String phoneType ){
    	logger.warn("-------陕西移动获取详单control开始---------");
    	return shanXiMobileService.doGetDetail(request, phoneCode, messageCode, servicePassword, longitude, latitude, uuid,phoneType);
    }
}

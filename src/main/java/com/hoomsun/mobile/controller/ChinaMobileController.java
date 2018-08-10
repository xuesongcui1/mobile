package com.hoomsun.mobile.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hoomsun.mobile.service.ChinaMobileService;

import io.swagger.annotations.ApiOperation;

/**
 * 移动运营商
 *
 * @author HotWong
 * @date 2016/10/31
 */
@Controller
@RequestMapping("chinaMobile")
public class ChinaMobileController {
	private static Logger logger = LoggerFactory.getLogger(ChinaMobileController.class);
	
    @Autowired
    private ChinaMobileService chinaMobileService;
    
    @ApiOperation(value = "第一步，获取移动手机验证码", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "getChinaMobileCode",method = RequestMethod.POST)
    @Async
    public Map<String,String> getChinaMobileCode(HttpServletRequest request, @RequestParam("userNumber")String userNumber) {
    	logger.warn("-------移动获取登录手机验证码control开始，手机号："+userNumber+"---------");
        return chinaMobileService.getChinaMobileCode(request,userNumber);
    }

    @ApiOperation(value = "第二步，输入手机验证码+手机号", notes = "参数：手机号码，手机验证码")
    @ResponseBody
    @RequestMapping(value = "chinaMobilLoad",method = RequestMethod.POST)
    @Async
    public Map<String,String> chinaMobilLoad(HttpServletRequest request, @RequestParam("userNumber") String userNumber, @RequestParam("phoneCode") String phoneCode) {
    	logger.warn("-------移动登录control开始，手机号："+userNumber+"---------");
    	return chinaMobileService.chinaMobilLoad(request,userNumber,phoneCode);
    }

    @ApiOperation(value = "第三步，获取详情页面的验证码", notes = "参数：无")
    @ResponseBody
    @RequestMapping(value = "getDetialImageCode",method = RequestMethod.POST)
    @Async
    public Map<String,Object> getDetialImageCode(HttpServletRequest request, @RequestParam("userNumber") String userNumber){
    	logger.warn("-------移动获取图片验证码control开始---------");
        return chinaMobileService.getDetialImageCode(request,userNumber);
    }

    @ApiOperation(value = "第四步，输入手机获得手机验证码", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "getDetialMobilCode",method = RequestMethod.POST)
    @Async
    public Map<String,String> getDetialMobilCode(HttpServletRequest request, @RequestParam("userNumber")String userNumber){
    	logger.warn("-------移动获取第二次手机验证码control开始---------");
    	return chinaMobileService.getDetialMobilCode(request,userNumber);
    }

    @ApiOperation(value = "第5步，获取账单信息", notes = "参数：手机号码")
    @ResponseBody
    @RequestMapping(value = "getDetailAccount",method = RequestMethod.POST)
    @Async
    public Map<String,Object> getDetailAccount(HttpServletRequest request, @RequestParam("userNumber")String userNumber, @RequestParam("phoneCode")String phoneCode,
                                               @RequestParam("fuwuSec")String fuwuSec, @RequestParam("imageCode")String imageCode ,
                                               @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude,@RequestParam("UUID")String uuid ){
    	logger.warn("-------移动获取详单control开始---------");
    	return chinaMobileService.getDetailAccount(request, userNumber, phoneCode, fuwuSec, imageCode, longitude, latitude, uuid);
    }
}

package com.hoomsun.mobile.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;

/**
 *@Title:App电信服务接口
 *@Description:  根据APP的归属地参数判断是否支持该城市
 *@Author: Big丶Young 
 *@Since:2018年3月5日  
 *@Version:1.1.0
 */
@Mapper
public interface SupportMapper {

	   /**
	    * 根据运营商类型和省份查询信息
	    * @return  
	    * @Description:
	    */
	   @Select("select * from operator_management where operator_type = #{operator_type} and operator_province = #{operator_province}")
	   Map<String,Object> queryOperatorManage(@Param("operator_type")String operator_type,@Param("operator_province")String operator_province);
	   
}

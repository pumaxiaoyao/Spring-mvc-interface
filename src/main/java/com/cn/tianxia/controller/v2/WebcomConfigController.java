package com.cn.tianxia.controller.v2;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cn.tianxia.controller.BaseController;
import com.cn.tianxia.po.BaseResponse;
import com.cn.tianxia.po.v2.JSONArrayResponse;
import com.cn.tianxia.service.v2.WebcomConfigService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * @ClassName WebcomConfigController
 * @Description 网站配置接口
 * @author Hardy
 * @Date 2019年2月5日 下午6:54:32
 * @version 1.0.0
 */
@Controller
public class WebcomConfigController extends BaseController{
    
    @Autowired
    private WebcomConfigService webcomConfigService;
    
    
    /**
     * 
     * @Description 获取网站公告图接口
     * @param request
     * @param response
     * @param cagent
     * @return
     */
    @RequestMapping(value="webcom.do")
    @ResponseBody
    public  JSONArray webcom(HttpServletRequest request, HttpServletResponse response,String cagent){
        logger.info("调用查询网站广告图接口开始=================start=============");
        try {
            if(StringUtils.isBlank(cagent)){
                logger.info("请求参数,平台编码不能为空");
                cagent = null;
            }
            return webcomConfigService.getBanner(cagent);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询网站广告图接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用查询网站广告图接口异常");
        }
    }
    

    /**
     * 
     * @Description 获取网站公告
     * @param request
     * @param response
     * @param cagent
     * @return
     */
    @RequestMapping(value="gonggao.do")
    @ResponseBody
    public  JSONArray gonggao(HttpServletRequest request, HttpServletResponse response,String cagent){
        logger.info("调用获取网站公告接口开始=============START===============");
        try {
            if(StringUtils.isBlank(cagent)){
                cagent = null;
            }
            return webcomConfigService.getNoticeInfo(cagent);
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取网站公告接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用获取网站公告接口异常");
        }
    }
    
    
    /**
     * 
     * @Description 获取网站设置
     * @param request
     * @param response
     * @param cagent
     * @param type
     * @return
     */
    @RequestMapping(value="webcomconfig.do")
    @ResponseBody
    public  JSONObject selectWebcomConfig(HttpServletRequest request, HttpServletResponse response,String cagent,Integer type){
        logger.info("调用查询网站设置接口开始===============START====================");
        try {
            if(StringUtils.isBlank(cagent)){
                return BaseResponse.faild("0", "请求参数异常:平台编码不能为空");
            }
            if(type == null){
                return BaseResponse.faild("0", "请求参数异常:类型不能为空");
            }
            return webcomConfigService.getWebcomConfig(cagent, type);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询网站设置接口异常:{}",e.getMessage());
            return BaseResponse.error("0", "查询网站设置异常");
        }
    }
    
    /**
     * 
     * @Description 查询手机网站设置信息
     * @param request
     * @param response
     * @param cagent
     * @param type
     * @return
     */
    @RequestMapping(value="mobleWebcomConfig.do")
    @ResponseBody
    public  JSONArray selectMobleWebcomConfig(HttpServletRequest request, HttpServletResponse response,String cagent,Integer type){
        logger.info("调用查询平台手机网站设置接口开始================START====================");
        try {
            if(StringUtils.isBlank(cagent)){
                return JSONArrayResponse.faild("请求参数异常:平台编码不能为空");
            }
            switch (type) {
                case 0:
                    type = 10;
                    break;
                case 1:
                    type = 11;
                default:
                    type = 12;
                    break;
                }
            
            //查询手机端网站设置
            return webcomConfigService.getMobileWebcomConfig(cagent, type);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用查询平台手机网站设置接口异常:{}",e.getMessage());
            return JSONArrayResponse.faild("调用查询平台手机网站设置接口异常");
        }
    }
}

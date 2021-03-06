package com.cn.tianxia.pay.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cn.tianxia.common.PayEntity;
import com.cn.tianxia.pay.po.PayResponse;
import com.cn.tianxia.pay.service.PayService;
import com.cn.tianxia.pay.utils.HttpUtils;
import com.cn.tianxia.pay.utils.MD5Utils;
import com.cn.tianxia.pay.utils.MapUtils;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName SKPPayServiceImpl
 * @Description 爽快付支付
 * @author Hardy
 * @Date 2019年1月14日 下午4:12:42
 * @version 1.0.0
 */
public class SKPPayServiceImpl implements PayService {

    //日志
    private static final Logger logger = LoggerFactory.getLogger(SKPPayServiceImpl.class);
    
    private String sid;//商户号
    
    private String payUrl;//支付请求地址
    
    private String notifyUrl;//回调地址
    
    private String md5Key;//签名key

    //构造器,初始化参数
    public SKPPayServiceImpl(Map<String,String> data,String type) {
        if(MapUtils.isNotEntity(data)){
            if(data.containsKey(type)){
                JSONObject jsonObject = JSONObject.fromObject(data.get(type));
                if(jsonObject.containsKey("sid")){
                    this.sid = jsonObject.getString("sid");
                }
                if(jsonObject.containsKey("payUrl")){
                    this.payUrl = jsonObject.getString("payUrl");
                }
                if(jsonObject.containsKey("notifyUrl")){
                    this.notifyUrl = jsonObject.getString("notifyUrl");
                }
                if(jsonObject.containsKey("md5Key")){
                    this.md5Key = jsonObject.getString("md5Key");
                }
            }
        }
    }

    @Override
    public JSONObject wyPay(PayEntity payEntity) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 扫码支付
     */
    @Override
    public JSONObject smPay(PayEntity payEntity) {
        logger.info("[SKP]爽快支付扫码支付开始============START===========");
        try {
            //获取支付请求参数
            Map<String,String> data = sealRequest(payEntity);
            //生成签名串
            String sign = ganeratorSign(data);
            data.put("sign", sign);
            logger.info("[SKP]爽快支付扫码支付请求参数报文:{}",JSONObject.fromObject(data).toString());
            //发起HTTP请求
            String formStr = HttpUtils.generatorForm(data, payUrl);
            return PayResponse.sm_form(payEntity, formStr, "下单成功");
//            String response = HttpUtils.toPostForm(data, payUrl);
//            if(StringUtils.isBlank(response)){
//                logger.info("[SKP]爽快支付扫码支付发起HTTP请求无响应结果");
//                return PayResponse.error("[SKP]爽快支付扫码支付发起HTTP请求无响应结果");
//            }
//            logger.info("[SKP]爽快支付扫码支付发起HTTP请求响应结果:{}",response);
//            //解析响应结果
//            JSONObject jsonObject = JSONObject.fromObject(response);
//            if(jsonObject.containsKey("code") && "100".equals(jsonObject.getString("code"))){
//                //成功
//                String payUrl = jsonObject.getJSONObject("data").getString("payUrl");
//                if(StringUtils.isBlank(payEntity.getMobile())){
//                    //PC
//                    return PayResponse.sm_qrcode(payEntity, payUrl, "下单成功");
//                }
//                
//                return PayResponse.sm_link(payEntity, payUrl, "下单成功");
//            }
//            return PayResponse.error("下单失败:"+response);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SKP]爽快支付扫码支付异常:{}",e.getMessage());
            return PayResponse.error("[SKP]爽快支付扫码支付异常");
        }
    }

    @Override
    public String callback(Map<String, String> data) {
        logger.info("[SKP]爽快支付回调验签开始==============START===========");
        try {
            //获取回调通知原签名串
            String sourceSign = data.get("sign");
            logger.info("[SKP]爽快支付回调验签获取原签名串:{}",sourceSign);
            String sign = ganeratorSign(data);
            logger.info("[SKP]爽快支付回调验签生成加密串:{}",sign);
            if(sourceSign.equalsIgnoreCase(sign)) return "success";
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SKP]爽快支付回调验签异常:{}",e.getMessage());
        }
        return "faild";
    }
    
    /**
     * 
     * @Description 组装支付请求参数
     * @param entity
     * @return
     * @throws Exception
     */
    private Map<String,String> sealRequest(PayEntity entity) throws Exception{
        logger.info("[SKP]爽快支付组装支付请求参数开始==============START==================");
        try {
            //创建参数存储对象
            Map<String,String> data = new HashMap<>();
            String amount = new DecimalFormat("0.00").format(entity.getAmount());//订单金额，单位为元
            data.put("sid",sid);//是  商户号
            data.put("amount",amount);//是   订单金额,数值类型（元）
            data.put("outTradeNo",entity.getOrderNo());//是   商户订单号
            data.put("orderType","2");//订单类型:1-收款;2-充值
            data.put("notifyUrl",notifyUrl);//是    异步回调地址(商户接收支付结果的地址)
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SKP]爽快支付组装支付请求参数异常:{}",e.getMessage());
            throw new Exception("[SKP]爽快支付组装支付请求参数异常");
        }
    }
    
    
    /**
     * 
     * @Description 生成签名串
     * @param data
     * @return
     * @throws Exception
     */
    private String ganeratorSign(Map<String,String> data) throws Exception{
        logger.info("[SKP]爽快支付生成签名串开始=================START==================");
        try {
            
            //签名规则:将以上参数（不包括sign本身）按字典排序后拼接上@@+商户秘钥然后用md5加密。
            //例如md5(amount=100& notifyUrl=xxx&orderType=alipay&outTradeNo=2018000001&sid=xxx@@77963b7a931377ad4ab5ad6a9cd718aa)。（商户秘钥可在“对接信息”页面查看）
            StringBuffer sb = new StringBuffer();
            //排序
            Map<String,String> map = MapUtils.sortByKeys(data);
            Iterator<String> iterator = map.keySet().iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String val = map.get(key);
                if("sign".equalsIgnoreCase(key)) continue;
                sb.append("&").append(key).append("=").append(val);
            }
            String signStr = sb.append("@@").append(md5Key).toString().replaceFirst("&", "");
            logger.info("[SKP]爽快支付生成待签名串:{}",signStr);
            String sign = MD5Utils.md5toUpCase_32Bit(signStr).toLowerCase();
            logger.info("[SKP]爽快支付生成加密签名串:{}",sign);
            return sign;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("[SKP]爽快支付生成签名串异常:{}",e.getMessage());
            throw new Exception("[SKP]爽快支付生成签名串异常");
        }
    }

    
    public static void main(String[] args) {
        JSONObject data = new JSONObject();
        JSONObject ali = new JSONObject();
        ali.put("sid", "S1124");
        ali.put("payUrl", "https://api.d3cq.com/api/order/pay");
        ali.put("notifyUrl", "http://txw.tx8899.com/XCY/Notify/SKPNotify.do");
        ali.put("md5Key", "4bf5fea6c9b6629434dfd01ac4412772");
        
        JSONObject wx = new JSONObject();
        wx.put("sid", "S1124");
        wx.put("payUrl", "https://api.d3cq.com/api/order/pay");
        wx.put("notifyUrl", "http://txw.tx8899.com/XCY/Notify/SKPNotify.do");
        wx.put("md5Key", "4bf5fea6c9b6629434dfd01ac4412772");
        
        data.put("ali", ali);
        data.put("wx", wx);
        
        System.err.println(data.toString());
    }
}

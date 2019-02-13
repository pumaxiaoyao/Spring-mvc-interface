package com.cn.tianxia.service.v2.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.tianxia.dao.v2.ContactConfigDao;
import com.cn.tianxia.entity.v2.ContactConfigEntity;
import com.cn.tianxia.po.BaseResponse;
import com.cn.tianxia.service.v2.ContactUsService;

import net.sf.json.JSONObject;

/**
 * 
 * @ClassName ContactUsServiceImpl
 * @Description 联系我们接口实现类
 * @author Hardy
 * @Date 2019年2月4日 下午6:29:25
 * @version 1.0.0
 */
@Service
public class ContactUsServiceImpl implements ContactUsService {
    
    //日志
    private static final Logger logger = LoggerFactory.getLogger(ContactUsServiceImpl.class);

    @Autowired
    private ContactConfigDao contactConfigDao;
    
    @Override
    public JSONObject getContackUsInfo(String cagent) {
        logger.info("调用获取平台联系信息业务开始==================START================");
        try {
            
            ContactConfigEntity contactConfigEntity = contactConfigDao.selectByCagent(cagent);
            if(contactConfigEntity == null){
                logger.info("没有该平台的联系信息");
                return BaseResponse.error("0", "没有该平台的联系信息");
            }
            JSONObject data = new JSONObject();
            data.put("id",contactConfigEntity.getId());
            data.put("cagent",contactConfigEntity.getCagent());
            data.put("qq",contactConfigEntity.getQq());
            data.put("wechat",contactConfigEntity.getWechat());
            data.put("qqcode",contactConfigEntity.getQqcode());
            data.put("wechatcode",contactConfigEntity.getWechatcode());
            data.put("customer",contactConfigEntity.getCustomer());
            data.put("website",contactConfigEntity.getWebsite());
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("调用获取平台联系信息业务异常:{}",e.getMessage());
            return BaseResponse.error("0", "调用获取平台联系信息业务异常");
        }
    }

}

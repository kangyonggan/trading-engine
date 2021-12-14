package com.kangyonggan.tradingEngine.service;


import com.kangyonggan.tradingEngine.constants.enums.EmailCheckStatus;
import com.kangyonggan.tradingEngine.dto.req.CheckEmailCodeReq;
import com.kangyonggan.tradingEngine.dto.req.SendEmailReq;

/**
 * @author kyg
 */
public interface IEmailService {


    /**
     * 发送模板邮件
     *
     * @param req
     */
    void sendEmail(SendEmailReq req);

    /**
     * 校验邮箱验证码是否正确
     *
     * @param req
     * @return
     */
    EmailCheckStatus checkEmailCode(CheckEmailCodeReq req);
}

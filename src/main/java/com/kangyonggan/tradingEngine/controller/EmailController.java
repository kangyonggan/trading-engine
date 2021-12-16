package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.components.MessageHandler;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.dto.req.SendEmailReq;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.service.IEmailService;
import com.kangyonggan.tradingEngine.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author kyg
 */
@RestController
@ApiVersion(1)
@RequestMapping("email")
public class EmailController extends BaseController {

    @Autowired
    private IEmailService emailService;

    @Autowired
    private MessageHandler messageHandler;

    /**
     * 发验证码
     *
     * @param req
     * @return
     */
    @PostMapping("sendCode")
    @AnonymousAccess
    public Result<Void> sendCode(@RequestBody SendEmailReq req) {
        if (userService.getUserByEmail(req.getEmail()) == null) {
            return Result.getFailure(ErrorCode.USER_EMAIL_NOT_EXISTS);
        }

        // 发送邮箱验证码
        sendEmail(req);
        return Result.getSuccess();
    }

    /**
     * 发送邮箱验证码
     *
     * @param req
     */
    private void sendEmail(SendEmailReq req) {
        // 生成验证码
        String verifyCode = NumberUtil.getNumberStr(6);
        req.setVerifyCode(verifyCode);
        // 标题（标题中带上验证码，不然标题一样会发送失败）
        String subject = String.format("%s验证码为%s", messageHandler.getMsg("EMAIL." + req.getType().name()), verifyCode);
        req.setSubject(subject);
        req.setData(new HashMap<>(8));
        req.getData().put("verifyCode", verifyCode);

        emailService.sendEmail(req);
    }

}

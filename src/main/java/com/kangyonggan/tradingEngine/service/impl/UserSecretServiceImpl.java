package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.components.RedisManager;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.RedisKeys;
import com.kangyonggan.tradingEngine.constants.enums.*;
import com.kangyonggan.tradingEngine.dto.req.CheckEmailCodeReq;
import com.kangyonggan.tradingEngine.dto.req.GoogleReq;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.entity.UserSecret;
import com.kangyonggan.tradingEngine.mapper.UserSecretMapper;
import com.kangyonggan.tradingEngine.service.IEmailService;
import com.kangyonggan.tradingEngine.service.IUserSecretService;
import com.kangyonggan.tradingEngine.service.IUserService;
import com.kangyonggan.tradingEngine.util.GoogleAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户密钥表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-18
 */
@Service
public class UserSecretServiceImpl extends ServiceImpl<UserSecretMapper, UserSecret> implements IUserSecretService {

    @Autowired
    private RedisManager redisManager;

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IUserService userService;

    @Override
    public String generateGoogleSecret(String uid) throws Exception {
        String secretKey = GoogleAuthenticator.generateSecretKey();
        redisManager.set(RedisKeys.GOOGLE_SECRET + uid, secretKey, 3600);
        return secretKey;
    }

    @Override
    @Valid
    public void saveGoogleSecret(GoogleReq req) {
        String secretKey = redisManager.get(RedisKeys.GOOGLE_SECRET + req.getUid());
        if (!GoogleAuthenticator.checkCode(secretKey, req.getGoogleCode(), System.currentTimeMillis())) {
            throw new BizException(ErrorCode.GOOGLE_SECRET_ERROR);
        }

        User user = userService.getUserByUid(req.getUid());
        checkVerifyCode(user.getEmail(), EmailType.GOOGLE_SECRET, req.getEmailCode());
        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("uid", req.getUid()).eq("type", UserSecretType.GOOGLE.name());
        UserSecret userSecret = baseMapper.selectOne(qw);
        if (userSecret == null) {
            userSecret = new UserSecret();
            userSecret.setUid(req.getUid());
            userSecret.setPriKey(secretKey);
            userSecret.setType(UserSecretType.GOOGLE.name());
            baseMapper.insert(userSecret);
        } else {
            userSecret.setPriKey(secretKey);
            userSecret.setEnable(Enable.YES.getValue());
            baseMapper.updateById(userSecret);
        }

    }

    @Override
    public Boolean hasGoogleVerify(String uid) {
        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("uid", uid).eq("type", UserSecretType.GOOGLE.name()).eq("enable", Enable.YES.getValue());
        return baseMapper.selectCount(qw) > 0;
    }

    @Override
    public String getGoogleSecret(String uid) {
        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("uid", uid).eq("type", UserSecretType.GOOGLE.name()).eq("enable", Enable.YES.getValue());

        UserSecret userSecret = baseMapper.selectOne(qw);
        return userSecret == null ? null : userSecret.getPriKey();
    }

    /**
     * 校验邮箱验证码
     *
     * @param email
     * @param emailType
     * @param verifyCode
     */
    private void checkVerifyCode(String email, EmailType emailType, String verifyCode) {
        // dev环境不校验验证码
        if (AppConstants.ENV_DEV.equals(env)) {
            return;
        }
        CheckEmailCodeReq req = new CheckEmailCodeReq();
        req.setType(emailType);
        req.setEmail(email);
        req.setVerifyCode(verifyCode);
        EmailCheckStatus checkStatus = emailService.checkEmailCode(req);
        if (checkStatus.equals(EmailCheckStatus.FAILURE)) {
            throw new BizException(ErrorCode.USER_VERIFY_CODE_ERROR);
        } else if (checkStatus.equals(EmailCheckStatus.INVALID)) {
            throw new BizException(ErrorCode.USER_VERIFY_CODE_INVALID);
        }
    }
}

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
import com.kangyonggan.tradingEngine.dto.req.UserSecretReq;
import com.kangyonggan.tradingEngine.dto.res.UserSecretRes;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.entity.UserSecret;
import com.kangyonggan.tradingEngine.mapper.UserSecretMapper;
import com.kangyonggan.tradingEngine.service.IEmailService;
import com.kangyonggan.tradingEngine.service.IUserSecretService;
import com.kangyonggan.tradingEngine.service.IUserService;
import com.kangyonggan.tradingEngine.util.GoogleAuthenticator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<UserSecretRes> getApis(String uid) {
        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("uid", uid);
        qw.eq("type", UserSecretType.API.name());
        qw.eq("enable", Enable.YES.getValue());
        qw.orderByDesc("id");
        List<UserSecret> list = baseMapper.selectList(qw);
        List<UserSecretRes> resList = new ArrayList<>();
        for (UserSecret userSecret : list) {
            UserSecretRes res = new UserSecretRes();
            BeanUtils.copyProperties(userSecret, res);
            res.setId(String.valueOf(userSecret.getId()));
            res.setPriKey("********");
            resList.add(res);
        }
        return resList;
    }

    @Override
    public UserSecretRes saveApi(UserSecretReq req) {
        String secret = getGoogleSecret(req.getUid());
        if (StringUtils.isEmpty(secret) || !GoogleAuthenticator.checkCode(secret, req.getGoogleCode(), System.currentTimeMillis())) {
            throw new BizException(ErrorCode.GOOGLE_SECRET_ERROR);
        }

        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("uid", req.getUid()).eq("type", UserSecretType.API.name()).eq("enable", Enable.YES.getValue());
        if (baseMapper.selectCount(qw) >= 30) {
            throw new BizException(ErrorCode.USER_API_MAX);
        }

        UserSecret userSecret = new UserSecret();
        userSecret.setUid(req.getUid());
        userSecret.setRemark(req.getRemark());
        userSecret.setType(UserSecretType.API.name());
        userSecret.setPubKey(generateApiKey());
        userSecret.setPriKey(generateSecretKey());
        baseMapper.insert(userSecret);
        UserSecretRes res = new UserSecretRes();
        BeanUtils.copyProperties(userSecret, res);
        res.setId(String.valueOf(userSecret.getId()));
        return res;
    }

    @Override
    public void updateApi(UserSecretReq req) {
        UserSecret userSecret = new UserSecret();
        userSecret.setId(req.getId());
        userSecret.setRemark(req.getRemark());
        baseMapper.updateById(userSecret);
    }

    @Override
    public UserSecretRes getApi(Long id, Long googleCode, String uid) {
        String secret = getGoogleSecret(uid);
        if (StringUtils.isEmpty(secret) || !GoogleAuthenticator.checkCode(secret, googleCode, System.currentTimeMillis())) {
            throw new BizException(ErrorCode.GOOGLE_SECRET_ERROR);
        }

        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("id", id).eq("type", UserSecretType.API.name()).eq("uid", uid).eq("enable", Enable.YES.getValue());
        UserSecret userSecret = baseMapper.selectOne(qw);
        if (userSecret == null) {
            return null;
        }
        UserSecretRes res = new UserSecretRes();
        BeanUtils.copyProperties(userSecret, res);
        res.setId(String.valueOf(userSecret.getId()));
        return res;
    }

    @Override
    public void deleteApi(UserSecretReq req) {
        UserSecret userSecret = new UserSecret();
        userSecret.setId(req.getId());
        userSecret.setEnable(Enable.NO.getValue());
        baseMapper.updateById(userSecret);
    }

    @Override
    public UserSecret getApiByApiKey(String apiKey) {
        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("pub_key", apiKey);
        qw.eq("type", UserSecretType.API.name());
        qw.eq("enable", Enable.YES.getValue());

        return baseMapper.selectOne(qw);
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

    /**
     * 生成ApiKey
     *
     * @return
     */
    private String generateApiKey() {
        String apiKey = generateSecretKey();

        QueryWrapper<UserSecret> qw = new QueryWrapper<>();
        qw.eq("pub_key", apiKey);
        if (baseMapper.selectCount(qw) > 0) {
            return generateApiKey();
        }
        return apiKey;
    }

    /**
     * 生成SecretKey
     *
     * @return
     */
    private String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }
}

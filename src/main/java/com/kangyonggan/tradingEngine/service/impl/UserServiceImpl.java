package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kangyonggan.tradingEngine.annotation.Valid;
import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.components.DistributedLock;
import com.kangyonggan.tradingEngine.components.RedisManager;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.LockId;
import com.kangyonggan.tradingEngine.constants.RedisKeys;
import com.kangyonggan.tradingEngine.constants.enums.EmailCheckStatus;
import com.kangyonggan.tradingEngine.constants.enums.EmailType;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.constants.enums.LoginType;
import com.kangyonggan.tradingEngine.dto.req.*;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.mapper.UserMapper;
import com.kangyonggan.tradingEngine.service.IEmailService;
import com.kangyonggan.tradingEngine.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.util.Digests;
import com.kangyonggan.tradingEngine.util.Encodes;
import com.kangyonggan.tradingEngine.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private RedisManager redisManager;

    @Autowired
    private IEmailService emailService;

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    @Valid
    public User login(UserLoginReq req) {
        User user = getUserByEmail(req.getEmail());
        if (user == null) {
            throw new BizException(ErrorCode.USER_EMAIL_NOT_EXISTS);
        }
        if (LoginType.BY_CODE.equals(req.getLoginType())) {
            // 校验邮箱验证码
            if (!AppConstants.ENV_DEV.equals(env)) {
                checkVerifyCode(req.getEmail(), EmailType.LOGIN, req.getVerifyCode());
            }
        } else if (LoginType.BY_PWD.equals(req.getLoginType())) {
            // 验证密码
            byte[] salt = Encodes.decodeHex(user.getSalt());
            byte[] hashPassword = Digests.sha1(req.getPassword().getBytes(), salt, AppConstants.HASH_INTERATIONS);
            String realPassword = Encodes.encodeHex(hashPassword);
            if (!user.getPassword().equals(realPassword)) {
                log.warn("密码错误, email:{}", req.getEmail());
                throw new BizException(ErrorCode.USER_PASSWORD_ERROR);
            }
        } else {
            // 未知登录类型
            throw new BizException(ErrorCode.PARAMS_ERROR);
        }

        // token
        user.setToken(getToken());
        redisManager.set(RedisKeys.TOKEN + user.getToken(), user, AppConstants.TOKEN_EXPIRE_TIME);

        return user;
    }

    @Override
    @Valid
    public void register(UserRegisterReq req) {
        // 校验邮箱验证码
        checkVerifyCode(req.getEmail(), EmailType.REGISTER, req.getVerifyCode());

        // 保存用户相关信息
        saveUserInfo(req);
    }

    @Override
    public User getUserInfoByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return redisManager.getAndRefresh(RedisKeys.TOKEN + token, AppConstants.TOKEN_EXPIRE_TIME);
    }

    @Override
    public User getUserByUid(String uid) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    @Valid
    public void logout(UserLogoutReq req) {
        // 删除redis中的token
        redisManager.delete(RedisKeys.TOKEN + req.getToken());
    }

    @Override
    @Valid
    public void setPassword(SetPwdReq req) {
        // 查询用户邮箱
        User user = getUserByUid(req.getUid());
        // 校验邮箱验证码
        checkVerifyCode(user.getEmail(), EmailType.SET_PASSWORD, req.getVerifyCode());
        // 更新密码
        updatePassword(user, req.getPassword());
    }

    /**
     * 生成随机不重复token
     *
     * @return
     */
    private String getToken() {
        String nextVal = String.valueOf(redisManager.increment(RedisKeys.INCR) % 1000000);
        return UUID.randomUUID().toString() + StringUtils.leftPad(nextVal, 4, "0");
    }

    /**
     * 保存用户信息
     *
     * @param req
     */
    private void saveUserInfo(UserRegisterReq req) {
        // user
        User user = new User();
        // 在锁中获取uid，防止并发获取重复uid，从而导致其中一个注册失败
        ((DistributedLock) () -> user.setUid(getUid())).getLock(LockId.GET_UID);
        user.setEmail(req.getEmail());
        baseMapper.insert(user);
    }

    /**
     * 生成uid，数据库查重，必须在分布式锁中完成
     *
     * @return
     */
    private String getUid() {
        String uid = NumberUtil.getNumberStr(8);

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("uid", uid);
        if (baseMapper.selectCount(wrapper) == 0) {
            return uid;
        }

        return getUid();
    }

    /**
     * 校验邮箱验证码
     *
     * @param email
     * @param emailType
     * @param verifyCode
     */
    private void checkVerifyCode(String email, EmailType emailType, String verifyCode) {
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
     * 更新密码
     *
     * @param user
     * @param password
     * @return
     */
    private void updatePassword(User user, String password) {
        byte[] saltArr = Digests.generateSalt(AppConstants.SALT_SIZE);
        String salt = Encodes.encodeHex(saltArr);
        String enPassword = Encodes.encodeHex(Digests.sha1(password.getBytes(), saltArr, AppConstants.HASH_INTERATIONS));

        user.setPassword(enPassword);
        user.setSalt(salt);

        baseMapper.updateById(user);
    }

}

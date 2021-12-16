package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.components.BizException;
import com.kangyonggan.tradingEngine.constants.AppConstants;
import com.kangyonggan.tradingEngine.constants.enums.EmailCheckStatus;
import com.kangyonggan.tradingEngine.constants.enums.EmailType;
import com.kangyonggan.tradingEngine.constants.enums.Enable;
import com.kangyonggan.tradingEngine.constants.enums.ErrorCode;
import com.kangyonggan.tradingEngine.dto.req.CheckEmailCodeReq;
import com.kangyonggan.tradingEngine.dto.req.PermissionReq;
import com.kangyonggan.tradingEngine.dto.res.PermissionRes;
import com.kangyonggan.tradingEngine.entity.Permission;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.mapper.PermissionMapper;
import com.kangyonggan.tradingEngine.service.IEmailService;
import com.kangyonggan.tradingEngine.service.IPermissionService;
import com.kangyonggan.tradingEngine.service.IUserService;
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
 * 权限表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-15
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Autowired
    private IEmailService emailService;

    @Autowired
    private IUserService userService;

    @Value("${spring.profiles.active}")
    private String env;

    @Override
    public List<PermissionRes> getPermissionList(String uid) {
        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("uid", uid);
        qw.eq("enable", Enable.YES.getValue());
        qw.orderByDesc("id");
        List<Permission> list = baseMapper.selectList(qw);
        List<PermissionRes> resList = new ArrayList<>();
        for (Permission permission : list) {
            PermissionRes res = new PermissionRes();
            BeanUtils.copyProperties(permission, res);
            res.setId(String.valueOf(permission.getId()));
            res.setSecretKey("********");
            resList.add(res);
        }
        return resList;
    }

    @Override
    public PermissionRes savePermission(PermissionReq req) {
        User user = userService.getUserByUid(req.getUid());
        checkVerifyCode(user.getEmail(), EmailType.API, req.getEmailCode());

        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("uid", req.getUid()).eq("enable", Enable.YES.getValue());
        if (baseMapper.selectCount(qw) >= 30) {
            throw new BizException(ErrorCode.USER_API_MAX);
        }

        Permission permission = new Permission();
        permission.setUid(req.getUid());
        permission.setRemark(req.getRemark());
        permission.setApiKey(generateApiKey());
        permission.setSecretKey(generateSecretKey());
        baseMapper.insert(permission);
        PermissionRes res = new PermissionRes();
        BeanUtils.copyProperties(permission, res);
        res.setId(String.valueOf(permission.getId()));
        return res;
    }

    @Override
    public PermissionRes getPermission(Long id, String emailCode, String uid) {
        User user = userService.getUserByUid(uid);
        checkVerifyCode(user.getEmail(), EmailType.API, emailCode);

        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("id", id).eq("uid", uid).eq("enable", Enable.YES.getValue());
        Permission permission = baseMapper.selectOne(qw);
        if (permission == null) {
            return null;
        }
        PermissionRes res = new PermissionRes();
        BeanUtils.copyProperties(permission, res);
        res.setId(String.valueOf(permission.getId()));
        return res;
    }

    @Override
    public void updatePermission(PermissionReq req) {
        Permission permission = new Permission();
        permission.setId(req.getId());
        permission.setWhiteList(req.getWhiteList());
        permission.setRemark(req.getRemark());
        baseMapper.updateById(permission);
    }

    @Override
    public void deletePermission(PermissionReq req) {
        Permission permission = new Permission();
        permission.setId(req.getId());
        permission.setEnable(Enable.NO.getValue());
        baseMapper.updateById(permission);
    }

    /**
     * 生成ApiKey
     *
     * @return
     */
    private String generateApiKey() {
        String apiKey = generateSecretKey();

        QueryWrapper<Permission> qw = new QueryWrapper<>();
        qw.eq("api_key", apiKey);
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

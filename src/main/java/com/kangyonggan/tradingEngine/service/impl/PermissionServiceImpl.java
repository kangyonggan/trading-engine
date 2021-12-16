package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.constants.enums.Enable;
import com.kangyonggan.tradingEngine.dto.req.PermissionReq;
import com.kangyonggan.tradingEngine.dto.res.PermissionRes;
import com.kangyonggan.tradingEngine.entity.Permission;
import com.kangyonggan.tradingEngine.mapper.PermissionMapper;
import com.kangyonggan.tradingEngine.service.IPermissionService;
import org.springframework.beans.BeanUtils;
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
}

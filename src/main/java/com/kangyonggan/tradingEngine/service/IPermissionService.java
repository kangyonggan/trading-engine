package com.kangyonggan.tradingEngine.service;

import com.kangyonggan.tradingEngine.dto.req.PermissionReq;
import com.kangyonggan.tradingEngine.dto.res.PermissionRes;
import com.kangyonggan.tradingEngine.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-15
 */
public interface IPermissionService extends IService<Permission> {

    /**
     * 查询权限列表
     *
     * @param uid
     * @return
     */
    List<PermissionRes> getPermissionList(String uid);

    /**
     * 添加权限
     *
     * @param req
     */
    PermissionRes savePermission(PermissionReq req);

    /**
     * 查看权限
     *
     * @param id
     * @param googleCode
     * @param uid
     * @return
     */
    PermissionRes getPermission(Long id, Long googleCode, String uid);

    /**
     * 修改权限
     *
     * @param req
     */
    void updatePermission(PermissionReq req);

    /**
     * 删除权限
     *
     * @param req
     */
    void deletePermission(PermissionReq req);

    /**
     * 获取ApiKey对应的权限
     *
     * @param apiKey
     * @return
     */
    Permission getPermissionByApiKey(String apiKey);

}

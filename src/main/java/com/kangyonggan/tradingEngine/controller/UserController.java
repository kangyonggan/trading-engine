package com.kangyonggan.tradingEngine.controller;

import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.req.PermissionReq;
import com.kangyonggan.tradingEngine.dto.req.SetPwdReq;
import com.kangyonggan.tradingEngine.dto.req.UserLoginReq;
import com.kangyonggan.tradingEngine.dto.req.UserLogoutReq;
import com.kangyonggan.tradingEngine.dto.res.PermissionRes;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.service.IPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author kyg
 */
@RestController
@RequestMapping("user")
@ApiVersion(1)
public class UserController extends BaseController {

    @Autowired
    private IPermissionService permissionService;

    /**
     * 登录
     *
     * @param req
     * @return
     */
    @PostMapping("login")
    @AnonymousAccess
    public Result<User> login(@RequestBody UserLoginReq req) {
        return Result.getSuccess(userService.login(req));
    }

    /**
     * 登出
     *
     * @return
     */
    @GetMapping("logout")
    public Result<Void> logout() {
        User user = currentUser();
        if (user == null) {
            return Result.getSuccess();
        }

        UserLogoutReq req = new UserLogoutReq();
        req.setUid(user.getUid());
        req.setToken(user.getToken());

        userService.logout(req);
        return Result.getSuccess();
    }

    /**
     * 设置密码
     *
     * @param req
     * @return
     */
    @PostMapping("setPassword")
    public Result<Void> setPassword(@RequestBody SetPwdReq req) {
        req.setUid(currentUid());
        userService.setPassword(req);
        return Result.getSuccess();
    }

    /**
     * 查询权限列表
     *
     * @return
     */
    @GetMapping("permission")
    public Result<List<PermissionRes>> permission() {
        return Result.getSuccess(permissionService.getPermissionList(currentUid()));
    }

    /**
     * 添加权限
     *
     * @param req
     * @return
     */
    @PostMapping("permission")
    public Result<PermissionRes> savePermission(@RequestBody PermissionReq req) {
        req.setUid(currentUid());
        return Result.getSuccess(permissionService.savePermission(req));
    }

    /**
     * 修改权限
     *
     * @param req
     * @return
     */
    @PutMapping("permission")
    public Result<Void> updatePermission(@RequestBody PermissionReq req) {
        req.setUid(currentUid());
        permissionService.updatePermission(req);
        return Result.getSuccess();
    }

    /**
     * 删除权限
     *
     * @return
     */
    @DeleteMapping("permission")
    public Result<Void> deletePermission(@RequestBody PermissionReq req) {
        req.setUid(currentUid());
        permissionService.deletePermission(req);
        return Result.getSuccess();
    }

}

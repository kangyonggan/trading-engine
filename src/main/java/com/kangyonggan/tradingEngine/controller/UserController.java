package com.kangyonggan.tradingEngine.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.req.*;
import com.kangyonggan.tradingEngine.dto.res.AccountRes;
import com.kangyonggan.tradingEngine.dto.res.PermissionRes;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.entity.User;
import com.kangyonggan.tradingEngine.entity.UserAccountLog;
import com.kangyonggan.tradingEngine.service.IPermissionService;
import com.kangyonggan.tradingEngine.service.IUserAccountLogService;
import com.kangyonggan.tradingEngine.service.IUserAccountService;
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

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IUserAccountLogService userAccountLogService;

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
    @DeleteMapping("logout")
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
     * 查看权限
     *
     * @param id
     * @param emailCode
     * @return
     */
    @GetMapping("permission/{id:[\\d+]}")
    public Result<PermissionRes> getPermission(@PathVariable Long id, @RequestParam String emailCode) {
        return Result.getSuccess(permissionService.getPermission(id, emailCode, currentUid()));
    }

    /**
     * 删除权限
     *
     * @return
     */
    @DeleteMapping("permission")
    public Result<Void> deletePermission(PermissionReq req) {
        req.setUid(currentUid());
        permissionService.deletePermission(req);
        return Result.getSuccess();
    }

    /**
     * 用户账户列表
     *
     * @return
     */
    @GetMapping("account")
    public Result<List<AccountRes>> accounts() {
        return Result.getSuccess(userAccountService.getAccounts(currentUid()));
    }

    /**
     * 用户账户流水
     *
     * @param req
     * @return
     */
    @GetMapping("accountLog")
    public Result<Page<UserAccountLog>> accountLog(AccountLogReq<UserAccountLog> req) {
        req.setUid(currentUid());
        return Result.getSuccess(userAccountLogService.getAccountLogs(req));
    }

}

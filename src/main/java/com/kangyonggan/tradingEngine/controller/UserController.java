package com.kangyonggan.tradingEngine.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kangyonggan.tradingEngine.annotation.AnonymousAccess;
import com.kangyonggan.tradingEngine.annotation.ApiVersion;
import com.kangyonggan.tradingEngine.dto.UserDto;
import com.kangyonggan.tradingEngine.dto.req.*;
import com.kangyonggan.tradingEngine.dto.res.AccountRes;
import com.kangyonggan.tradingEngine.dto.res.Result;
import com.kangyonggan.tradingEngine.dto.res.UserSecretRes;
import com.kangyonggan.tradingEngine.entity.UserAccountLog;
import com.kangyonggan.tradingEngine.service.IUserAccountLogService;
import com.kangyonggan.tradingEngine.service.IUserAccountService;
import com.kangyonggan.tradingEngine.service.IUserSecretService;
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
    private IUserAccountService userAccountService;

    @Autowired
    private IUserAccountLogService userAccountLogService;

    @Autowired
    private IUserSecretService userSecretService;

    /**
     * 登录
     *
     * @param req
     * @return
     */
    @PostMapping("login")
    @AnonymousAccess
    public Result<UserDto> login(@RequestBody UserLoginReq req) {
        return Result.getSuccess(userService.login(req));
    }

    /**
     * 登出
     *
     * @return
     */
    @DeleteMapping("logout")
    public Result<Void> logout() {
        UserDto user = currentUser();
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
    @GetMapping("api")
    public Result<List<UserSecretRes>> getApis() {
        return Result.getSuccess(userSecretService.getApis(currentUid()));
    }

    /**
     * 添加权限
     *
     * @param req
     * @return
     */
    @PostMapping("api")
    public Result<UserSecretRes> saveApi(@RequestBody UserSecretReq req) {
        req.setUid(currentUid());
        return Result.getSuccess(userSecretService.saveApi(req));
    }

    /**
     * 修改权限
     *
     * @param req
     * @return
     */
    @PutMapping("api")
    public Result<Void> updateApi(@RequestBody UserSecretReq req) {
        req.setUid(currentUid());
        userSecretService.updateApi(req);
        return Result.getSuccess();
    }

    /**
     * 查看权限
     *
     * @param id
     * @param googleCode
     * @return
     */
    @GetMapping("api/{id}")
    public Result<UserSecretRes> getPermission(@PathVariable Long id, @RequestParam Long googleCode) {
        return Result.getSuccess(userSecretService.getApi(id, googleCode, currentUid()));
    }

    /**
     * 删除权限
     *
     * @return
     */
    @DeleteMapping("api")
    public Result<Void> deleteApi(UserSecretReq req) {
        req.setUid(currentUid());
        userSecretService.deleteApi(req);
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

    /**
     * 生成谷歌认证密钥
     *
     * @return
     * @throws Exception
     */
    @GetMapping("googleSecret")
    public Result<String> generateGoogleSecret() throws Exception {
        return Result.getSuccess(userSecretService.generateGoogleSecret(currentUid()));
    }

    /**
     * 保存谷歌认证密钥
     *
     * @param req
     * @return
     */
    @PostMapping("googleSecret")
    public Result<Void> saveGoogleSecret(@RequestBody GoogleReq req) {
        req.setUid(currentUid());
        userSecretService.saveGoogleSecret(req);
        return Result.getSuccess();
    }

}

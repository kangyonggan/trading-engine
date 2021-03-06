package com.kangyonggan.tradingEngine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kangyonggan.tradingEngine.dto.UserDto;
import com.kangyonggan.tradingEngine.dto.req.SetPwdReq;
import com.kangyonggan.tradingEngine.dto.req.UserLoginReq;
import com.kangyonggan.tradingEngine.dto.req.UserLogoutReq;
import com.kangyonggan.tradingEngine.entity.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
public interface IUserService extends IService<User> {

    /**
     * 登录
     *
     * @param req
     * @return
     */
    UserDto login(UserLoginReq req);

    /**
     * 根据token获取用户信息
     *
     * @param token
     * @return
     */
    UserDto getUserInfoByToken(String token);

    /**
     * 根据uid获取用户信息
     *
     * @param uid
     * @return
     */
    User getUserByUid(String uid);

    /**
     * 根据邮箱获取用户信息
     *
     * @param email
     * @return
     */
    User getUserByEmail(String email);

    /**
     * 登出
     *
     * @param req
     */
    void logout(UserLogoutReq req);

    /**
     * 设置密码
     *
     * @param req
     */
    void setPassword(SetPwdReq req);

}

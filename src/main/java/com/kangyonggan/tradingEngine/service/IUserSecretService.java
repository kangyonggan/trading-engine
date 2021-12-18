package com.kangyonggan.tradingEngine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kangyonggan.tradingEngine.dto.req.GoogleReq;
import com.kangyonggan.tradingEngine.entity.UserSecret;

/**
 * <p>
 * 用户密钥表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-18
 */
public interface IUserSecretService extends IService<UserSecret> {

    /**
     * 获取谷歌认证密钥
     *
     * @param uid
     * @return
     * @throws Exception
     */
    String getGoogleSecret(String uid) throws Exception;

    /**
     * 保存谷歌认证密钥
     *
     * @param req
     */
    void saveGoogleSecret(GoogleReq req);

    /**
     * 是否进行了谷歌认证
     *
     * @param uid
     * @return
     */
    Boolean hasGoogleVerify(String uid);

}

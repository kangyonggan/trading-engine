package com.kangyonggan.tradingEngine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kangyonggan.tradingEngine.dto.req.GoogleReq;
import com.kangyonggan.tradingEngine.dto.req.UserSecretReq;
import com.kangyonggan.tradingEngine.dto.res.UserSecretRes;
import com.kangyonggan.tradingEngine.entity.UserSecret;

import java.util.List;

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
     * 生成谷歌认证密钥
     *
     * @param uid
     * @return
     * @throws Exception
     */
    String generateGoogleSecret(String uid) throws Exception;

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

    /**
     * 获取用户的谷歌认证密钥
     *
     * @param uid
     * @return
     */
    String getGoogleSecret(String uid);

    /**
     * 查询权限列表
     *
     * @param uid
     * @return
     */
    List<UserSecretRes> getApis(String uid);

    /**
     * 添加权限
     *
     * @param req
     * @return
     */
    UserSecretRes saveApi(UserSecretReq req);

    /**
     * 修改权限
     *
     * @param req
     */
    void updateApi(UserSecretReq req);

    /**
     * 查看权限
     *
     * @param id
     * @param googleCode
     * @param uid
     * @return
     */
    UserSecretRes getApi(Long id, Long googleCode, String uid);

    /**
     * 删除权限
     *
     * @param req
     */
    void deleteApi(UserSecretReq req);

    /**
     * 根据apiKey查询用户api
     *
     * @param apiKey
     * @return
     */
    UserSecret getApiByApiKey(String apiKey);
}

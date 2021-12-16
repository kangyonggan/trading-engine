package com.kangyonggan.tradingEngine.mapper;

import com.kangyonggan.tradingEngine.dto.res.AccountRes;
import com.kangyonggan.tradingEngine.entity.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户账户表 Mapper 接口
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    /**
     * 查询用户账户列表
     *
     * @param uid
     * @return
     */
    List<AccountRes> selectAccount(@Param("uid") String uid);
}

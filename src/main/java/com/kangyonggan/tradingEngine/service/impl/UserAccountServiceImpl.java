package com.kangyonggan.tradingEngine.service.impl;

import com.kangyonggan.tradingEngine.entity.UserAccount;
import com.kangyonggan.tradingEngine.mapper.UserAccountMapper;
import com.kangyonggan.tradingEngine.service.IUserAccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements IUserAccountService {

}

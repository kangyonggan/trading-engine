package com.kangyonggan.tradingEngine.service.impl;

import com.kangyonggan.tradingEngine.entity.UserAccountLog;
import com.kangyonggan.tradingEngine.mapper.UserAccountLogMapper;
import com.kangyonggan.tradingEngine.service.IUserAccountLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户日志表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-14
 */
@Service
public class UserAccountLogServiceImpl extends ServiceImpl<UserAccountLogMapper, UserAccountLog> implements IUserAccountLogService {

}

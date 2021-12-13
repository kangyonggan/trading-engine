package com.kangyonggan.tradingEngine.service;

import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 交易对配置表 服务类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
public interface ISymbolConfigService extends IService<SymbolConfig> {

    /**
     * 查询交易对配置
     *
     * @param symbol
     * @return
     */
    SymbolConfig getSymbolConfig(String symbol);

}

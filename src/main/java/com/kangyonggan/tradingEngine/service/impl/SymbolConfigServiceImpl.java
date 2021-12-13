package com.kangyonggan.tradingEngine.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kangyonggan.tradingEngine.entity.SymbolConfig;
import com.kangyonggan.tradingEngine.mapper.SymbolConfigMapper;
import com.kangyonggan.tradingEngine.service.ISymbolConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交易对配置表 服务实现类
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
@Service
public class SymbolConfigServiceImpl extends ServiceImpl<SymbolConfigMapper, SymbolConfig> implements ISymbolConfigService {

    private final Map<String, SymbolConfig> symbolConfigMap = new HashMap<>(16);

    @PostConstruct
    public void init() {
        List<SymbolConfig> symbolConfigs = baseMapper.selectList(null);
        for (SymbolConfig symbolConfig : symbolConfigs) {
            symbolConfigMap.put(symbolConfig.getSymbol(), symbolConfig);
        }
    }

    @Override
    public SymbolConfig getSymbolConfig(String symbol) {
        return symbolConfigMap.get(symbol);
    }
}

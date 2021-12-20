package com.kangyonggan.tradingEngine.mapper;

import com.kangyonggan.tradingEngine.entity.Trade;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * <p>
 * 交易表 Mapper 接口
 * </p>
 *
 * @author mbg
 * @since 2021-12-13
 */
public interface TradeMapper extends BaseMapper<Trade> {

    /**
     * 查询交易对的最新价
     *
     * @param symbol
     * @return
     */
    BigDecimal selectPrice(@Param("symbol") String symbol);
}

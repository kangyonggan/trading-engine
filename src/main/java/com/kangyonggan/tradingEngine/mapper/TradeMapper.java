package com.kangyonggan.tradingEngine.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kangyonggan.tradingEngine.dto.res.TradeRes;
import com.kangyonggan.tradingEngine.entity.Trade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
     * 获取最近30条成交记录
     *
     * @param symbol
     * @return
     */
    List<TradeRes> selectLast30Trade(@Param("symbol") String symbol);
}

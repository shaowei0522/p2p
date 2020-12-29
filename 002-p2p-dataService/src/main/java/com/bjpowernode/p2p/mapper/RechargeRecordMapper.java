package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.RechargeRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    void updateRechargeRecordStatus(@Param("out_trade_no") String out_trade_no, int i);

    List<RechargeRecord> selectRechargeRecordByStatusZero();

//    根据订单的编号查询订单的信息
    RechargeRecord selectRechargeRecordByRechargeNo(@Param("out_trade_no") String out_trade_no);
}
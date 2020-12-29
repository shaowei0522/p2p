package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.IncomeRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    List<IncomeRecord> selectIncomePlanByStatusAndCycle();
}
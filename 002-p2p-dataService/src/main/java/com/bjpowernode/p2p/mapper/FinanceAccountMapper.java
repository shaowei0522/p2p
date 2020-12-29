package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.FinanceAccount;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    FinanceAccount selectByUid(Integer id);

    int updateAvailableMoneyByUserId(FinanceAccount financeAccount);

    FinanceAccount selectAccountByUserId(Integer uid);

//    充值账户
    int updateAvailableMoneyByUserIdAndTotalAmount(Map<String, Object> paramsMap2);
}
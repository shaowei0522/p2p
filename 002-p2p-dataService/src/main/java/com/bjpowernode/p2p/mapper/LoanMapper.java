package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.Loan;
import com.bjpowernode.p2p.util.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LoanMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Loan record);

    int insertSelective(Loan record);

    Loan selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Loan record);

    int updateByPrimaryKey(Loan record);

    Double selectYearRate();

    List<Loan> selectLoanType(@Param("start") int start,
                              @Param("pageSize") int pageSize,
                              @Param("ptype") String ptype);

    int selectLoanCount(@Param("ptype") String ptype);

    Loan selectLoanById(@Param("id") String id);

    int updateLoanLeftMoney(@Param("loanId") String loanId, @Param("bidMoney") String bidMoney, Integer version);

    List<Loan> selectByStatus();
}
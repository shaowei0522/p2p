package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.Bid;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Bid record);

    int insertSelective(Bid record);

    Bid selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Bid record);

    int updateByPrimaryKey(Bid record);

    Double selectMoneyAmount();

    List<Bid> selectBidByLoanInfo(@Param("id") String id);

    List<Bid> selectBidByLoanId(Integer id);
}
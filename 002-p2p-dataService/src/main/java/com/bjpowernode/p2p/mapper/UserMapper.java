package com.bjpowernode.p2p.mapper;

import com.bjpowernode.p2p.model.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

//    查询用户的个数
    int selectUserCount();

    User selectByPhone(@Param("phone") String phone);

    User selectUserByPassword(User user);
}
package com.example.demo.dao.mapper;

import com.example.demo.dao.domain.User;
import com.example.demo.dao.dto.UserDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapperExt {
    List<User> selectByCondition(@Param("record")UserDto record);
}
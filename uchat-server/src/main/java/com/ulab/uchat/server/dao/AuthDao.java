package com.ulab.uchat.server.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.ulab.uchat.server.security.auth.UserAuthInfo;

public interface AuthDao {
    UserAuthInfo findByUsername(@Param("name") String name);
}

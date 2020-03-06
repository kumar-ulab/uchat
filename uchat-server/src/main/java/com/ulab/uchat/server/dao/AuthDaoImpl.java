package com.ulab.uchat.server.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.server.dao.mapper.MapperUser;
import com.ulab.uchat.server.security.auth.UserAuthInfo;

@Repository
public class AuthDaoImpl implements AuthDao {
	@Autowired MapperUser userMapper;
	
    public UserAuthInfo findByUsername(String name) {
    	User user = userMapper.selectUserByLogin(name);
    	if (user != null) {
    		return new UserAuthInfo(user);
    	}
    	return null;
    }
}

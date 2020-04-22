package com.ulab.uchat.server.dao.mapper;

import com.ulab.uchat.model.pojo.ChatDevice;
import com.ulab.uchat.model.pojo.User;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface MapperUser {
	User selectUserByEmail(@Param("email") String email);
	User selectUserByLogin(@Param("login") String login);
	int addUser(@Param("user") User user);
	List<User> selectPairedUsers(@Param("userId") String userId);
	int addPatientDoctorRelation(@Param("patientId") String patientId, @Param("doctorId") String doctorId);
	int updateUser(@Param("user") User user, @Param("password") String password);
	ChatDevice selectDevice(@Param("userId") String userId);
	int updateAndInsertDevice(@Param("userId") String userId, @Param("deviceType") String deviceType, @Param("pushAddress") String pushAddress);
}

package com.ulab.uchat.model.mapper;

import com.ulab.uchat.model.pojo.Patient;

import org.apache.ibatis.annotations.Param;

public interface MapperPatient {
	Patient selectById(@Param("id") String id);
}

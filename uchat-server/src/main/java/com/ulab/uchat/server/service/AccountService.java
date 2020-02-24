package com.ulab.uchat.server.service;

import org.springframework.stereotype.Service;

import com.ulab.uchat.model.pojo.Person;

@Service
public class AccountService {
	public String generatePatientId(Person person) {
		//TO DO
		return "" + System.currentTimeMillis();
	}
	
	public String generatePatientPassword(Person person) {
		//TO DO
		return "ulab123456";
	}
}

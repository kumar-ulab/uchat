package com.ulab.uchat.server.rest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ulab.uchat.model.pojo.Doctor;
import com.ulab.uchat.model.pojo.Patient;
import com.ulab.uchat.model.pojo.Person;
import com.ulab.uchat.pojo.LoginInfo;
import com.ulab.uchat.pojo.LoginRsp;
import com.ulab.uchat.server.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

@Api
@RestController
@RequestMapping(value="/account")
public class AccountApi {

	@Autowired AccountService accountService;
	
	@ResponseBody
    @RequestMapping(value="/user", 
    				produces= "application/json",
    				method= RequestMethod.POST,
    				consumes={"application/json"})	
	
	public LoginRsp login(@RequestBody LoginInfo loginRequest) {
		LoginRsp loginRsp = new LoginRsp();
		loginRsp.setToken("token" + System.currentTimeMillis());
		return loginRsp;
	}

	@ResponseBody
    @RequestMapping(value="/patient", 
    				produces= "application/json",
    				method= RequestMethod.POST,
    				consumes={"application/json"})
	public Patient invitePatient(@RequestBody Person person) {
		Patient patient = new Patient();
		patient.setId(accountService.generatePatientId(person));
		patient.setPassword(accountService.generatePatientPassword(person));
		patient.setPerson(person);
		return patient;
	}

	@ResponseBody
    @RequestMapping(value="/doctor", 
    				produces= "application/json",
    				method= RequestMethod.POST,
    				consumes={"application/json"})
	public Patient register(@RequestBody Person person) {
		Patient patient = new Patient();
		patient.setId(accountService.generatePatientId(person));
		patient.setPassword(accountService.generatePatientPassword(person));
		patient.setPerson(person);
		return patient;
	}

	@ResponseBody
    @RequestMapping(value="/doctor/{doctor}/patients", 
    				produces= "application/json",
    				method= RequestMethod.GET)
	public Patient[] getPatients(@PathVariable("doctor") String doctorId) {
		//TO DO
		Patient[] patients = new Patient[3];
		Patient patient = new Patient();
		Person person = new Person();
		person.setFirstName("a1");
		person.setFirstName("b1");
		person.setEmail("ab1@abc.com");
		person.setIdentity("111111");
		patient.setId(accountService.generatePatientId(person));
		patient.setPassword(accountService.generatePatientPassword(person));
		patient.setPerson(person);
		patients[0] = patient;
		
		patient = new Patient();
		person = new Person();
		person.setFirstName("a2");
		person.setFirstName("b2");
		person.setEmail("ab2@abc.com");
		person.setIdentity("222222");
		patient.setId(accountService.generatePatientId(person));
		patient.setPassword(accountService.generatePatientPassword(person));
		patient.setPerson(person);
		patients[1] = patient;
		
		patient = new Patient();
		person = new Person();
		person.setFirstName("a3");
		person.setFirstName("b3");
		person.setEmail("ab3@abc.com");
		person.setIdentity("333333");
		patient.setId(accountService.generatePatientId(person));
		patient.setPassword(accountService.generatePatientPassword(person));
		patient.setPerson(person);
		patients[2] = patient;
		return patients;
	}

	@ResponseBody
    @RequestMapping(value="/patient/{patient}/doctors", 
    				produces= "application/json",
    				method= RequestMethod.GET)
	public Doctor[] getDoctors(@PathVariable("patient") String patient) {
		//TO DO
		Doctor[] doctors = new Doctor[1];
		Doctor doctor = new Doctor();
		Person person = new Person();
		person.setFirstName("x1");
		person.setFirstName("y1");
		person.setEmail("xy1@abc.com");
		person.setIdentity("999999");
		doctor.setId(accountService.generatePatientId(person));
		doctor.setPassword(accountService.generatePatientPassword(person));
		doctor.setPerson(person);
		doctors[0] = doctor;
		return doctors;
	}
}

package com.ulab.uchat.server.rest.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ulab.uchat.constant.Constants;
import com.ulab.uchat.model.pojo.Doctor;
import com.ulab.uchat.model.pojo.Patient;
import com.ulab.uchat.model.pojo.Person;
import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.GeneralRsp;
import com.ulab.uchat.pojo.LoginInfo;
import com.ulab.uchat.pojo.LoginRsp;
import com.ulab.uchat.server.config.AppConfig;
import com.ulab.uchat.server.dao.mapper.MapperUser;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.helper.CodeHelper;
import com.ulab.uchat.server.security.auth.ResponseUserToken;
import com.ulab.uchat.server.security.auth.UserAuthInfo;
import com.ulab.uchat.server.security.service.AuthService;
import com.ulab.uchat.server.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(value="/api/auth")
public class AuthApi {

	@Autowired AccountService accountService;
	@Autowired AuthService authService;
	@Autowired AppConfig appConig;
	@Autowired CodeHelper codeHelper;
	@Autowired MapperUser mapperUser;
	
	@ResponseBody
    @RequestMapping(value="/user", 
    				produces= "application/json",
    				method= RequestMethod.POST,
    				consumes={"application/json"})	
    @ApiOperation(value = "login", notes = "login to get access token")	
	public LoginRsp login(@RequestBody LoginInfo loginInfo) {
		UserAuthInfo userAuthInfo = accountService.login(loginInfo);
    	LoginRsp rsp = new LoginRsp();
    	rsp.setToken(userAuthInfo.getToken());
    	rsp.setUser(userAuthInfo.getUser());
    	rsp.setExpiration(appConig.getExpirationSeconds());
    	return rsp;
	}

	@ResponseBody
    @RequestMapping(value="/doctor", 
    				produces= "application/json",
    				method= RequestMethod.POST,
    				consumes={"application/json"})
	public User doctorSignup(@RequestBody Doctor doctor) {
		return accountService.signUp(doctor);
	}


	@ResponseBody
    @RequestMapping(value="/user/password", 
    				produces= "application/json",
    				method= RequestMethod.PUT)
    @ApiOperation(value = "change password via code", notes = "change password via code")
	public GeneralRsp updatePasswordByCode(
			@RequestParam("code") String code,
			@RequestParam("password") String password) {
		GeneralRsp rsp = new GeneralRsp();
		if (accountService.updatePasswordByCode(code, password)) {
			rsp.setStatus(0);
			rsp.setMessage("password is changed");
		} else {
			rsp.setStatus(1);
			rsp.setMessage("code is incorrect or expired!");
			throw new AppException("code is incorrect or expired!");
		}
		return rsp;
	}

	@ResponseBody
    @RequestMapping(value="/user/code", 
    				produces= "application/json",
    				method= RequestMethod.POST)
    @ApiOperation(value = "sendPasswordCodeMail", notes = "send password code mail")
	public GeneralRsp mailCode(
			@RequestParam("email") String email) {
		accountService.createCode(email);
		GeneralRsp rsp =  new GeneralRsp();
		rsp.setMessage("mail is sent to " + email);
		return rsp;
	}
}

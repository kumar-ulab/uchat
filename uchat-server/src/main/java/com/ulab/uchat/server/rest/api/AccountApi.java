package com.ulab.uchat.server.rest.api;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ulab.uchat.model.pojo.Doctor;
import com.ulab.uchat.model.pojo.Patient;
import com.ulab.uchat.model.pojo.Person;
import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.ClientMsg;
import com.ulab.uchat.pojo.GeneralRsp;
import com.ulab.uchat.pojo.ServerMsg;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.security.domain.ErrorStatus;
import com.ulab.uchat.server.security.service.AuthService;
import com.ulab.uchat.server.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api
@RestController
@RequestMapping(value="/api/account")
public class AccountApi {

	@Autowired AccountService accountService;
	@Autowired AuthService authService;
	
	@ResponseBody
    @RequestMapping(value="/user/{userId}", 
    				produces= "application/json",
    				method= RequestMethod.DELETE,
    				consumes={"application/json"})
    @ApiOperation(value = "logout", notes = "logout chat")
    @ApiImplicitParams({@ApiImplicitParam(name = "Chat-Token", value = "chat token got after login", required = true, dataType = "string", paramType = "header")})	
	public GeneralRsp logout(@PathVariable("userId") String userId) {
		validatePermission(userId);
//		accountService.logout(token);
		return new GeneralRsp();
	}
	
	@ResponseBody
    @RequestMapping(value="/doctor/{doctorId}/patient", 
    				produces= "application/json",
    				method= RequestMethod.POST,
    				consumes={"application/json"})
    @ApiOperation(value = "invitePatient", notes = "invite patient")
    @ApiImplicitParams({@ApiImplicitParam(name = "Chat-Token", value = "chat token got after login", required = true, dataType = "string", paramType = "header")})	
	public Patient invitePatient(@RequestBody Person person,
								 @PathVariable("doctorId") String doctorId) {
		validatePermission(doctorId);
		return accountService.invitePatient(doctorId, person);
	}

	@ResponseBody
    @RequestMapping(value="/user/{userId}/pairs", 
    				produces= "application/json",
    				method= RequestMethod.GET)
    @ApiOperation(value = "getChatPairs", notes = "get chat pairs of User")
    @ApiImplicitParams({@ApiImplicitParam(name = "Chat-Token", value = "chat token got after login", required = true, dataType = "string", paramType = "header")})	
	public List<User> getPairsOfUser(
			@PathVariable("userId") String userId) {
		validatePermission(userId);
		return accountService.getChatPairs(userId);
	}
	
	private void validatePermission(String owerId) {
		Authentication authtication = SecurityContextHolder.getContext().getAuthentication();
		String userId= authtication.getName();
		if (!owerId.equals(userId)) {
			throw new AppException(ErrorStatus.No_Permission, "no permission");
		}
	}

	@ResponseBody
    @RequestMapping(value="/test", 
    				produces= "application/json",
    				method= RequestMethod.POST)
    @ApiOperation(value = "nouse", notes = "no use")
	public ServerMsg getPairsOfUser(@RequestBody ClientMsg msg) {
		return new ServerMsg();
	}
}

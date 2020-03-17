package com.ulab.uchat.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ulab.uchat.server.dao.mapper.MapperUser;
import com.ulab.uchat.model.pojo.Patient;
import com.ulab.uchat.model.pojo.Person;
import com.ulab.uchat.model.pojo.User;
import com.ulab.uchat.pojo.LoginInfo;
import com.ulab.uchat.server.exception.AppException;
import com.ulab.uchat.server.helper.CodeHelper;
import com.ulab.uchat.server.helper.MailHelper;
import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.auth.UserAuthInfo;
import com.ulab.uchat.server.security.domain.ErrorStatus;
import com.ulab.uchat.server.security.service.AuthService;
import com.ulab.uchat.types.UserType;

@Service
public class AccountService {
	@Autowired AuthService authService;
	@Autowired MapperUser mapperUser;
	@Autowired JwtUtils jwtTokenUtil;
	@Autowired CodeHelper codeHelper;
	@Autowired MailHelper mailHelper;
	
	public void generateUserId(User user) {
		UserType type = UserType.parse(user.getType());
		user.setId(type.getFlag() + System.currentTimeMillis());
	}
	
	public String generatePatientPassword(Person person) {
		String pass = "ulab123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(pass);
		return encodedPassword;
	}
	
	@Transactional
	public User signUp(User user) {
        User existUser = mapperUser.selectUserByEmail(user.getEmail());
        if(existUser != null) {
            throw new AppException(ErrorStatus.Duplicat_User, "user already Exist");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(user.pickPassword());
        user.setPassword(encodedPassword);
        generateUserId(user);
        mapperUser.addUser(user);
        return user;		
	}

	@Transactional
    public UserAuthInfo login(LoginInfo loginInfo) {    		
    	String username = loginInfo.getLogin();
    	String password = loginInfo.getPassword();
    	return authService.login(username, password);
    }
    
	@Transactional
	public void logout(String token) {
        authService.logout(token);
	}
	
	@Transactional
	public List<User> getChatPairs(String userId) {
		return mapperUser.selectPairedUsers(userId);
	}

	@Transactional
	public Patient invitePatient(String doctorId, Person person) {
		Patient patient = new Patient();
		patient.setPassword(generatePatientPassword(person));
		patient.setPerson(person);
		generateUserId(patient);
		mapperUser.addUser(patient);
		mapperUser.addPatientDoctorRelation(patient.getId(), doctorId);
		return patient;
	}

	@Transactional
	public User updateUser(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (user.pickPassword() != null) {
	        String encodedPassword = encoder.encode(user.pickPassword());
	        user.setPassword(encodedPassword);
        }
		mapperUser.updateUser(user, user.pickPassword());
		return user;
	}

	public boolean updatePasswordByCode(String userId, String code, String password) {
		if (codeHelper.refreshCodeIfValid(userId, code)) {
			User user = new User();
			user.setId(userId);
			user.setPassword(password);
			updateUser(user);
			return true;
		}
		return false;
	}

	public void createCode(String userId) {
		codeHelper.refreshCode(userId);
		String code = codeHelper.createCodeFromTs(userId, System.currentTimeMillis());
		User user = mapperUser.selectUserByLogin(userId);
		mailHelper.sendMail(user.getEmail(), "Uchat reset password", "reset password code: " + code);
	}
}

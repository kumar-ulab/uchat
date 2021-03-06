package com.ulab.uchat.server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import com.ulab.uchat.server.helper.HttpHelper;
import com.ulab.uchat.server.helper.CodeHelper.Code;
import com.ulab.uchat.server.helper.MailHelper;
import com.ulab.uchat.server.security.JwtUtils;
import com.ulab.uchat.server.security.auth.UserAuthInfo;
import com.ulab.uchat.server.security.domain.ErrorStatus;
import com.ulab.uchat.server.security.service.AuthService;
import com.ulab.uchat.types.UserType;

@Service
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

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
    	try {
        	return authService.login(username, password);
    	} catch(Exception e) {
    		if (authService.remoteAuthentication(username, password)) {
    			User user = new User();
    			user.setEmail(username);
    			user.setIdentity(username);
    			user.setPassword(password);
    			user.setFirstName("UDesign");
    			user.setLastName("Doctor");
    			signUp(user);
    			logger.info("auto import uDesign account: " + username);
            	return authService.login(username, password);
    		} else {
    			throw e;
    		}
    	}
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
		User doctor = mapperUser.selectUserByLogin(doctorId);
		String title = "Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " invite you join uChat";
		String html = "<a href=\"https://" + System.getenv("UCHAT_HOST") + "/download/uchat.zip\">download the app</a>";
		mailHelper.sendMail(person.getEmail(), title, html);
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

	public boolean updatePasswordByCode(String code, String password) {
		Code c = codeHelper.devryptCode(code);
		if (codeHelper.refreshCodeIfValid(c)) {
			User user = new User();
			user.setId(c.userId);
			user.setPassword(password);
			updateUser(user);
			return true;
		}
		return false;
	}

	public void createCode(String email) {
		User user = mapperUser.selectUserByLogin(email);
		if (user == null) {
			throw new AppException(ErrorStatus.No_Such_User, "no such user");
		}
		String userId = user.getId();
		codeHelper.refreshCode(userId);
		String code = codeHelper.encryptCode(userId, System.currentTimeMillis());
		mailHelper.sendMail(user.getEmail(), "Uchat reset password", "reset password code: " + code);
	}
}

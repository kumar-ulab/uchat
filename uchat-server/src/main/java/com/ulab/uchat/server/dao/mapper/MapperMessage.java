package com.ulab.uchat.server.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.ulab.uchat.model.pojo.Message;

@Repository
public interface MapperMessage {
	
	int saveMessage(Message message);
	
	List<Message> findLeaveMessageByToUserId(@Param("userId") String userId);
	
	int updateStatus(@Param("id")String id, @Param("status") int status);
	
	int batchUpdateStatus(@Param("status") int status, @Param("ids") List<String> ids);
	
	int deleteMessage();
	
}

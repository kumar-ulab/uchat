package com.ulab.uchat.server.task;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Component;

@Component
public class TaskManager {
	
	private Map<String, ExecutorService> executors = new ConcurrentHashMap<String, ExecutorService>();

	
	public void submitTask(Runnable task, String key) {
		ExecutorService executor = executors.get(key);
		if (executor == null) {
			executor = Executors.newSingleThreadExecutor(); 
			executors.put(key, executor);
		}
		executor.submit(task);
	}
	
	public PushMessageTask createPushMessageTask(String title, String content, List<String> deviceTokens) {
		return new PushMessageTask(title, content, deviceTokens); 
	}
	
	public void shutdown(String key) {
		ExecutorService executor = executors.get(key);
		if (executor != null) {
			executor.shutdown();
			executors.remove(key);
		}
	}

}

package com.gorim.api;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.gorim.model.GameEvent;

@Controller
public class GameEventsController {
	  @Autowired private SimpMessagingTemplate messagingTemplate;
	  
	  public void sendEventToAllUsers(JSONObject event, List<String> usernames) {
		  for(String user : usernames)
			  this.sendEventToUser(user, event);
	  }
	  
	  public void sendEventToUser(String recipientId, JSONObject event) {
		  System.out.println("GameEventsController: recipientId=" + recipientId + "; event=" + event.toJSONString());
		  messagingTemplate.convertAndSendToUser(
			  recipientId,
			  "/queue/messages",
			  (new GameEvent("game", event)).toJSON()
		  );
	  }
	  
	  

}

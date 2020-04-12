package com.sgk.joker.websoket.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import com.sgk.joker.websoket.model.Request.AddPlayer;
import com.sgk.joker.websoket.model.Request.NewGame;
import com.sgk.joker.websoket.model.Request.PlayerMessage;
import com.sgk.joker.websoket.Service.GameManager;

import com.sgk.joker.websoket.model.GameInfo;
import com.sgk.joker.websoket.model.Greeting;
import com.sgk.joker.websoket.model.HelloMessage;
import com.sgk.joker.websoket.model.ServerError;

@Controller
public class PlayerController {
	
	protected final Log logger = LogFactory.getLog("com.sgk.joker.websoket.controller.PlayerController");
	
	public static final String SUBSCRIBE_TOPIC_GAMES = "/topic/games";//should this be a queue?
	
	public static final String ENDPOINT_GET_ALL_GAMES = "/getAllGames";
	public static final String ENDPOINT_NEW_GAME = "/newGame";
	public static final String ENDPOINT_ADD_PLAYER = "/addPlayer";
	public static final String ENDPOINT_PLAYER_MESSAGE = "/playerMessage";

	@Autowired
	GameManager gameManager;

	//TODO remove after testing
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(HelloMessage message) throws Exception {
		Thread.sleep(100); // simulated delay
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}

	@MessageMapping(ENDPOINT_GET_ALL_GAMES)
	@SendTo(SUBSCRIBE_TOPIC_GAMES)
	public List<GameInfo> getAllGames() {
		return gameManager.getAllGames();
	}

	@MessageMapping(ENDPOINT_NEW_GAME)
	@SendTo(SUBSCRIBE_TOPIC_GAMES)
	public  List<GameInfo> newGame(NewGame message) {		
		gameManager.newGame(message.getName());
		return gameManager.getAllGames();		
	}
	
	@MessageMapping(ENDPOINT_ADD_PLAYER)
	@SendTo(SUBSCRIBE_TOPIC_GAMES)
	public  List<GameInfo> addPlayer(@Payload AddPlayer addPlayer, Principal principal) {
		
		logger.info("addPlayer :" + addPlayer.getName() + " principal :" + principal.getName());
		
		gameManager.addPlayer(principal.getName(), addPlayer.getGameId(), addPlayer.getName(), addPlayer.getExistingId(), addPlayer.getPos());
		return gameManager.getAllGames();
	}	
	
	@MessageMapping(ENDPOINT_PLAYER_MESSAGE)
	public void processPlayerMessage(@Payload PlayerMessage playerMessage) {
		gameManager.processPlayerMessage(playerMessage);
	}	

    @MessageExceptionHandler
    //@SendToUser(destinations="/queue/errors", broadcast=false)
    //@SendToUser("/queue/errors")
    @SendToUser(value = "/queue/errors", broadcast = false)
    public ServerError handleException(Exception exception) {
    	logger.error(exception.getMessage(), exception);  
      return new ServerError(exception.toString(), exception.getClass().getName());
    }

}

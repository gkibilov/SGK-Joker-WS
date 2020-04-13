package com.sgk.joker.websoket.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.sgk.joker.websoket.WebSocketConfig;
import com.sgk.joker.websoket.model.GameInfo;
import com.sgk.joker.websoket.model.GameState;
import com.sgk.joker.websoket.model.JokerAction;
import com.sgk.joker.websoket.model.JokerReaction;
import com.sgk.joker.websoket.model.Status;
import com.sgk.joker.websoket.model.Request.PlayerMessage;
import com.sgk.joker.websoket.model.Request.PlayerMessage.MessageType;

@Service
public class GameManager {
	
	protected final Log logger = LogFactory.getLog("com.sgk.joker.websoket.model.Service.GameManager");
	
	static int MAX_GAMES = 10;
	//static private Map<String, GameState> games = new ConcurrentHashMap<String, GameState>();
	static private Cache<String, Object> games = CacheBuilder.newBuilder()
 		    										.maximumSize(10)
 		    										.expireAfterAccess(5, TimeUnit.MINUTES).build();
	
	static private Cache<String, Object> expiredGames = CacheBuilder.newBuilder()
 		    												.maximumSize(10)
 		    												.expireAfterAccess(1, TimeUnit.MINUTES).build();
		
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	public GameManager(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	private static Random random =  new Random();

	public GameState newGame(String name) {
		if (games.size() >= 10)
			throw new IllegalStateException("Max number of games is running, please try again later!");
		
		//generate unique game id
		String gameId = ((Integer)random.nextInt()).toString();
		while (games.getIfPresent(gameId) != null) {
			gameId = ((Integer)random.nextInt()).toString();
		}		
		
		GameState game = new GameState();
		game.setGameId(gameId);
		game.setGameName(name);
		games.put(gameId, game);
		
		return game;
	}

	public GameState getGame(String gameId) {
		
		GameState game = (GameState) games.getIfPresent(gameId);
		
		if (game == null) {
			game = (GameState) expiredGames.getIfPresent(gameId);
		}
		
		if (game == null)
			throw new IllegalStateException("No such game found!");
		
		return game;
	}

	public void expireGame(String gameId) {
		GameState game = (GameState) games.getIfPresent(gameId);
		games.invalidate(gameId);		
		expiredGames.put(gameId, game);		
	}

	public List<GameInfo> getAllGames() {
		
		List<GameInfo> gi = new ArrayList<GameInfo>();
		
		for ( Object gs: games.asMap().values()) {
			gi.add(new GameInfo((GameState) gs));
		}

		return gi;
	}
	
	public void addPlayer (String newPlayerId, String gameId, String name, String existingId, Integer pos) {
		
		GameState gs = getGame(gameId);

		String id = gs.addPlayer(newPlayerId, name, existingId, pos);	
		
		logger.info("Send message to user: " + id + " at " + WebSocketConfig.SUBSCRIBE_USER_REPLY);
		
		messagingTemplate.convertAndSendToUser(id, WebSocketConfig.SUBSCRIBE_USER_REPLY, gs.getPlayerState(id, null));
	}


	public void processPlayerMessage(PlayerMessage playerMessage) {
		
		String gameId = playerMessage.getGameId();
		
		GameState gs = getGame(gameId);//throws exception if game is not found
		
		String playerId = playerMessage.getPlayerId();
			
		if(!gs.isValidPlayer(playerId)) {
			throw new IllegalStateException("Not a valid player id!");
		}
		
		
		MessageType messageType = playerMessage.getType();
		
		switch (messageType) {
		case START:
			startGame(gameId, playerId);
			break;
		case SET_KOZYR:
			gs.setKozyr(playerId, playerMessage.getKozyrSuite());
			break;
		case CALL:
			gs.call(playerId, playerMessage.getWantQty());
			break;
		case ACTION:
			 action(gameId, playerId, playerMessage.getCardId(), playerMessage.getJokerAction());
			break;
		case REACTION:
			reaction(gameId, playerId, playerMessage.getCardId(), playerMessage.getJokerReaction());
			break;
		case MESSAGE:
			gs.addMessage(playerMessage.getMessage());
			break;
		default:
			throw new IllegalStateException("Not a valid messageType: " + messageType);
		}		
		
		logger.info("Send message to user: " + playerId + " at " + WebSocketConfig.SUBSCRIBE_USER_REPLY);
		messagingTemplate.convertAndSendToUser(playerId, WebSocketConfig.SUBSCRIBE_USER_REPLY, gs.getPlayerState(playerId,messageType));
		
		for (String oId : gs.getOpponentIds(playerId)) {
			logger.info("Send message to user: " + oId + " at " + WebSocketConfig.SUBSCRIBE_USER_REPLY);
			messagingTemplate.convertAndSendToUser(oId, WebSocketConfig.SUBSCRIBE_USER_REPLY, gs.getPlayerState(oId, messageType));
		}
	}
		
	private void startGame(String gameId, String playerId) {
		
		GameState state = getGame(gameId);

		synchronized(state) {
			if (!state.isGameOn()) {
				state.setGameOn(true);
				state.assignCards();
				state.setStatus(Status.DEALT);
			}
		}
		//return state.getPlayerState(playerId, PlayerMessage.MessageType.START);
	}
	

	private void action(String gameId, String playerId, 
							  int cardId, JokerAction jokerAction) {

		GameState state = getGame(gameId);
		
		if ((cardId == 0 || cardId == 1) && jokerAction == null){
			jokerAction = JokerAction.getWantActionForSuite(state.getCurrentPlay().getKozyr().getSuite());
		}
		
		state.action(playerId, cardId, jokerAction);
		
		//return state.getPlayerState(playerId, PlayerMessage.MessageType.ACTION);
	}
	

	private void reaction(String gameId, String playerId, 
						   		 Integer cardId, JokerReaction jokerReaction) {
	
		GameState state = getGame(gameId);
		
		if ((cardId == 0 || cardId == 1) && jokerReaction == null){
			jokerReaction = JokerReaction.TAKE;
		}
		
		state.react(playerId, cardId, jokerReaction);
		
		if (state.getStatus() == Status.GAME_OVER) {
			expireGame(gameId);
		}

		//return state.getPlayerState(playerId, PlayerMessage.MessageType.REACTION);
	}	

}

package com.sgk.joker.websoket.model.Request;

import com.sgk.joker.websoket.model.CardSuite;
import com.sgk.joker.websoket.model.JokerAction;
import com.sgk.joker.websoket.model.JokerReaction;

public class PlayerMessage {
	
	public enum MessageType {
		START,
		CALL,
		SET_KOZYR,
		ACTION,
		REACTION,
		MESSAGE
	}
	
	MessageType type;
	
	private String gameId;
	private String payerId;
	
	private CardSuite kozyrSuite;
	
	private Integer wantQty;
	
	private Integer cardId;
	private JokerReaction jokerReaction;
	private JokerAction jokerAction;
	
	private String message;
	
	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public Integer getWantQty() {
		return wantQty;
	}

	public void setWantQty(Integer wantQty) {
		this.wantQty = wantQty;
	}

	public CardSuite getKozyrSuite() {
		return kozyrSuite;
	}

	public void setKozyrSuite(CardSuite kozyrSuite) {
		this.kozyrSuite = kozyrSuite;
	}

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public JokerReaction getJokerReaction() {
		return jokerReaction;
	}

	public void setJokerReaction(JokerReaction jokerReaction) {
		this.jokerReaction = jokerReaction;
	}

	public JokerAction getJokerAction() {
		return jokerAction;
	}

	public void setJokerAction(JokerAction jokerAction) {
		this.jokerAction = jokerAction;
	}
	
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

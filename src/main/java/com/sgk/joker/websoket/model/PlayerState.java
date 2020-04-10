package com.sgk.joker.websoket.model;

import java.util.List;

import com.sgk.joker.websoket.model.Player;
import com.sgk.joker.websoket.model.Request.PlayerMessage;

public class PlayerState {

	private GameState state;
	
	private Player player;
	
	private List<Player> opponents;
	
	private PlayerMessage.MessageType requestType;
	
	public PlayerState() {
		super();
	}

	public PlayerState(Player player, GameState gameState, PlayerMessage.MessageType requestType) {
		this.player = player;
		this.state = gameState;
		this.requestType = requestType;
		this.opponents = gameState.getOpponents(player.getId());
		//Collections.sort(opponents);
	}
	
	public PlayerMessage.MessageType getRequestType() {
		return requestType;
	}
	
	public void setRequestType(PlayerMessage.MessageType requestType) {
		this.requestType = requestType;
	}

	public GameState getState() {
		return state;
	}

	public void setState(GameState state) {
		this.state = state;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public List<Player> getOpponents() {
		return opponents;
	}

	public void setOpponents(List<Player> opponents) {
		this.opponents = opponents;
	}

}

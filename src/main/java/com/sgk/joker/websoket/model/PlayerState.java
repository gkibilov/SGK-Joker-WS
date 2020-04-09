package com.sgk.joker.websoket.model;

import java.util.List;

import com.sgk.joker.websoket.model.Player;

public class PlayerState {
	
	private GameState state;
	
	private Player player;
	
	private List<Player> opponents;

	
	public PlayerState(Player player, GameState gameState) {
		this.player = player;
		this.state = gameState;
		this.opponents = gameState.getOpponents(player.getId());
		//Collections.sort(opponents);
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

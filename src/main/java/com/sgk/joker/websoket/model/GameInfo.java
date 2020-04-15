package com.sgk.joker.websoket.model;

import java.util.List;

public class GameInfo implements Comparable <GameInfo>{
	
	private String gameId;
	private String gameName;
	
	private int roundNumber = 0;	
	
	private Status status = Status.NOT_STARTED;
	
	private List<Player> players;
	

	public GameInfo(GameState gs) {
		this.gameId = gs.getGameId();
		this.gameName = gs.getGameName();
		this.roundNumber = gs.getRoundNumber();
		this.status = gs.getStatus();
		
		this.players = gs.getOpponents("ALL");
		
	}

	@Override
	public int compareTo(GameInfo o) {
		return o.gameId.compareTo(this.gameId);
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public int getRoundNumber() {
		return roundNumber;
	}

	public void setRoundNumber(int roundNumber) {
		this.roundNumber = roundNumber;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

}

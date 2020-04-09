package com.sgk.joker.websoket.model.Request;

public class AddPlayer {
	
	public AddPlayer(String gameId, String name, String existingId, Integer pos) {
		this.gameId = gameId;
		this.name = name;
		this.existingId = existingId;
		this.pos = pos;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExistingId() {
		return existingId;
	}
	public void setExistingId(String existingId) {
		this.existingId = existingId;
	}
	public Integer getPos() {
		return pos;
	}
	public void setPos(Integer pos) {
		this.pos = pos;
	}
	
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	private String gameId;
	private String name;
	private String existingId;
	private Integer pos;
}

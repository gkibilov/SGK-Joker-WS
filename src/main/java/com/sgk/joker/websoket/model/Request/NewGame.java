package com.sgk.joker.websoket.model.Request;

public class NewGame {

	private String name;
	
	private Boolean isPrivate = false;
	
	public NewGame() {
	}
	
	public Boolean isPrivate() {
		return isPrivate;
	}
	
	public void setPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public NewGame(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

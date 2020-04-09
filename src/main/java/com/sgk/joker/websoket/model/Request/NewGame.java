package com.sgk.joker.websoket.model.Request;

public class NewGame {

	private String name;

	public NewGame() {
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

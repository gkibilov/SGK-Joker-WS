package com.sgk.joker.websoket.model;

public enum JokerReaction {
	WANT(0), TAKE(1);
	private int value; 
	
	private JokerReaction(int value) { this.value = value; }

	public int getValue() {
		return value;
	}
}


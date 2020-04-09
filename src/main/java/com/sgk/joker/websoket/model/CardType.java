package com.sgk.joker.websoket.model;

public enum CardType {
	
	SIX(0), SEVEN(1), EIGHT(2), NINE(3), TEN(4), JACK(5), QUEEN(6), KING(8), ACE(9), JOKER(10);
	
	private int value; 
	
	private CardType(int value) { this.value = value; }

	public int getValue() {
		return value;
	}
}

package com.sgk.joker.websoket.model;

public enum JokerAction {
	WANT_HART(0), WANT_DIAMOND(1), WANT_CLUB(2), WANT_SPADE(3), TAKE_HART(4), TAKE_DIAMOND(5), TAKE_CLUB(6), TAKE_SPADE(7);
	private int value; 
	
	private JokerAction(int value) { this.value = value; }

	public int getValue() {
		return value;
	}
	
	static public JokerAction getWantActionForSuite(CardSuite suite) {
		
		switch (suite) {
		
		case HART:
			return WANT_HART;
		case DIAMOND:
			return WANT_DIAMOND;
		case CLUB:
			return WANT_CLUB;
		case SPADE:
			return WANT_SPADE;
		default:
			return WANT_HART;	
		}

	}
}


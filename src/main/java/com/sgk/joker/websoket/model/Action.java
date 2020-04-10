package com.sgk.joker.websoket.model;

public class Action {
	
	public Action() {
		super();
	}

	private int cardId;
	private JokerAction jokerAction;
	private JokerReaction jokerReaction;
	private int playerPosition = 0;
	
	public int getPlayerPosition() {
		return playerPosition;
	}

	public void setPlayerPosition(int playerPosition) {
		this.playerPosition = playerPosition;
	}

	public Action(int playerPosition, int cardId, JokerAction jokerAction) {
		super();
		this.cardId = cardId;
		this.jokerAction = jokerAction;
		this.playerPosition = playerPosition;
	}
	
	public Action(int playerPosition, int cardId, JokerReaction jokerReaction) {
		super();
		this.cardId = cardId;
		this.jokerReaction = jokerReaction;
		this.playerPosition = playerPosition;
	}
	
	public JokerReaction getJokerReaction() {
		return jokerReaction;
	}
	
	public void setJokerReaction(JokerReaction jokerReaction) {
		this.jokerReaction = jokerReaction;
	}
	
	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public JokerAction getJokerAction() {
		return jokerAction;
	}
	public void setJokerAction(JokerAction jokerAction) {
		this.jokerAction = jokerAction;
	}
	
    protected CardSuite getActingSuite() {

		CardSuite res = null;
		
		if (jokerAction != null) {
			
			switch (jokerAction) {
			case WANT_HART:
			case TAKE_HART:
				res = CardSuite.HART;
				break;
			case WANT_DIAMOND:
			case TAKE_DIAMOND:
				res = CardSuite.DIAMOND;
				break;
			case WANT_CLUB:
			case TAKE_CLUB:
				res = CardSuite.CLUB;
				break;				
			case WANT_SPADE:
			case TAKE_SPADE:
				res = CardSuite.SPADE;
				break;				
			}
		}
		else
			res = Card.cardMap.get(this.getCardId()).getSuite();
		
		return res;
	}
	
	protected boolean isJokerActionWant() {
		if (jokerAction != null) {
			switch (jokerAction) {
			case WANT_HART:
			case WANT_DIAMOND:
			case WANT_CLUB:	
			case WANT_SPADE:
				return true;
			default:
				return false;
			}
		}
		return false;
	}
	
	public boolean isJokerActionTake() {
		if (jokerAction != null) {
			switch (jokerAction) {
			case TAKE_HART:
			case TAKE_DIAMOND:
			case TAKE_CLUB:	
			case TAKE_SPADE:
				return true;
			default:
				return false;
			}
		}
		else
			return false;
	}
	
	public CardSuite getSuite() {
		return Card.cardMap.get(this.getCardId()).getSuite();
	}
	
	public CardType getType() {
		return Card.cardMap.get(this.getCardId()).getType();
	}
	
}

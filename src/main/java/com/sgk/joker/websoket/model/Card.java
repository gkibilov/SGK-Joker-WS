package com.sgk.joker.websoket.model;

import java.util.HashMap;

import com.sgk.joker.websoket.model.CardSuite;
import com.sgk.joker.websoket.model.CardType;

public class Card implements Comparable<Card>{
	
	
	public static HashMap <Integer, Card> cardMap = new HashMap<Integer, Card>();
	static{
		
		cardMap.put(1, new Card(1, CardSuite.BEZ, CardType.JOKER));
		cardMap.put(2, new Card(2, CardSuite.BEZ, CardType.JOKER));
		
		//Spades
		cardMap.put(3, new Card(3, CardSuite.SPADE, CardType.SEVEN));
		cardMap.put(4, new Card(4, CardSuite.SPADE, CardType.EIGHT));
		cardMap.put(5, new Card(5, CardSuite.SPADE, CardType.NINE));
		cardMap.put(6, new Card(6, CardSuite.SPADE, CardType.TEN));
		cardMap.put(7, new Card(7, CardSuite.SPADE, CardType.JACK));
		cardMap.put(8, new Card(8, CardSuite.SPADE, CardType.QUEEN));
		cardMap.put(9, new Card(9, CardSuite.SPADE, CardType.KING));
		cardMap.put(10, new Card(10, CardSuite.SPADE, CardType.ACE));
		
		//Clubs
		cardMap.put(11, new Card(11, CardSuite.CLUB, CardType.SEVEN));
		cardMap.put(12, new Card(12, CardSuite.CLUB, CardType.EIGHT));
		cardMap.put(13, new Card(13, CardSuite.CLUB, CardType.NINE));
		cardMap.put(14, new Card(14, CardSuite.CLUB, CardType.TEN));
		cardMap.put(15, new Card(15, CardSuite.CLUB, CardType.JACK));			
		cardMap.put(16, new Card(16, CardSuite.CLUB, CardType.QUEEN));			
		cardMap.put(17, new Card(17, CardSuite.CLUB, CardType.KING));		
		cardMap.put(18, new Card(18, CardSuite.CLUB, CardType.ACE));					
		
		//Diamonds
		cardMap.put(19, new Card(19, CardSuite.DIAMOND, CardType.SIX));
		cardMap.put(20, new Card(20, CardSuite.DIAMOND, CardType.SEVEN));
		cardMap.put(21, new Card(21, CardSuite.DIAMOND, CardType.EIGHT));
		cardMap.put(22, new Card(22, CardSuite.DIAMOND, CardType.NINE));
		cardMap.put(23, new Card(23, CardSuite.DIAMOND, CardType.TEN));
		cardMap.put(24, new Card(24, CardSuite.DIAMOND, CardType.JACK));
		cardMap.put(25, new Card(25, CardSuite.DIAMOND, CardType.QUEEN));
		cardMap.put(26, new Card(26, CardSuite.DIAMOND, CardType.KING));
		cardMap.put(27, new Card(27, CardSuite.DIAMOND, CardType.ACE));
		
		//Harts
		cardMap.put(28, new Card(28, CardSuite.HART, CardType.SIX));
		cardMap.put(29, new Card(29, CardSuite.HART, CardType.SEVEN));
		cardMap.put(30, new Card(30, CardSuite.HART, CardType.EIGHT));		
		cardMap.put(31, new Card(31, CardSuite.HART, CardType.NINE));
		cardMap.put(32, new Card(32, CardSuite.HART, CardType.TEN));
		cardMap.put(33, new Card(33, CardSuite.HART, CardType.JACK));
		cardMap.put(34, new Card(34, CardSuite.HART, CardType.QUEEN));
		cardMap.put(35, new Card(35, CardSuite.HART, CardType.KING));
		cardMap.put(36, new Card(36, CardSuite.HART, CardType.ACE));			
	};
	
	
	int id;
	CardSuite suite;
	CardType type;
	
	public Card (int id, CardSuite suite, CardType type) {
		this.id = id;
		this.suite = suite;
		this.type = type;
	}
	
	public Card (int id) {
		this.id = id;
		if (id <= 36)
		{
			this.suite = cardMap.get(id).getSuite();
			this.type = cardMap.get(id).getType();
		}
	}
	
	public Card(CardSuite kozyr) {
		this.id = -1;
		this.suite = kozyr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public CardSuite getSuite() {
		return suite;
	}
	public void setSuite(CardSuite suite) {
		this.suite = suite;
	}
	
	public CardType getType() {
		return type;
	}
	public void setType(CardType type) {
		this.type = type;
	}

	@Override
	public int compareTo(Card o) {
		return this.id - o.id;
	}
}

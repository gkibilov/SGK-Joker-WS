package com.sgk.joker.websoket.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.sgk.joker.websoket.model.Card;
import com.sgk.joker.websoket.model.Player;

public class GameState {
	
	protected final Log logger = LogFactory.getLog("com.sgk.joker.websoket.model.GameState");
	
	Integer testNumCards = null;
	
	int version = 0;
	
	private String gameId;
	private String gameName;
	
	private int roundNumber = 0;
	
	private int prevRoundNumber = 0;
	private int [] prevAssignment = null;		
	
	private int numCards = 0;
	
	private int actingPlayerPosition = 0;
	
	private int currentTurnPosition = 0;

	private Status status = Status.NOT_STARTED;
	
	private List<Integer> cardNumbers = new ArrayList<Integer>();
	private List<String> messages = new  ArrayList<String>();
	

	//private List<Player> players = new ArrayList<Player>();
	private Map<String, Player> players = new HashMap<String, Player>();
	
	private PlayState prevPlay = null;
	private PlayState currentPlay = new PlayState();
	
	synchronized public List<String> getMessages() {
		return messages;
	}
	
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	synchronized public void addMessage(String message) {
		if (messages.size() > 100) {
			messages.remove(0);
		}
		messages.add(message);

		version++;
	}
	
	
	public List<Integer> getCardNumbers() {
		return cardNumbers;
	}
	
	public void setCardNumbers(List<Integer> cardNumbers) {
		this.cardNumbers = cardNumbers;
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
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
	
	public PlayState getCurrentPlay() {
		if(prevPlay != null && this.status == Status.DEALT && this.actingPlayerPosition == this.currentTurnPosition)
			return this.prevPlay;
		else
			return currentPlay;
	}

	public void setCurrentPlay(PlayState currentPlay) {
		this.currentPlay = currentPlay;
	}

	public int getCurrentTurnPosition() {
		return currentTurnPosition;
	}

	public void setCurrentTurnPosition(int currentTurnPosition) {
		this.currentTurnPosition = currentTurnPosition;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getActingPlayerPosition() {
		return actingPlayerPosition;
	}

	public boolean isValidPlayer(String playerId) {
		return players.containsKey(playerId);
	}
	
	public int getNumCards() {
		return numCards;
	}
	public void setNumCards(int numCards) {
		this.numCards = numCards;
	}

	public int getRoundNumber() {
		return roundNumber;
	}
	public void setRoundNumber(int roundNumber) {
		// noop this.roundNumber = roundNumber;
	}
	
	private Card getKozyr() {
		return currentPlay == null ? null : currentPlay.getKozyr();
	}
	
	public void setKozyr(String playerId, CardSuite kozyr) {
		if (players.get(playerId).getPosition() != actingPlayerPosition)
			throw new IllegalStateException("Not your turn to call the suite!");
		this.currentPlay.setKozyr(new Card(kozyr));
		
		if(this.numCards == 9)
			this.prevPlay.setKozyr(new Card(kozyr));
		
		version++;
	}
	
	public void reset(String tableName) {
		roundNumber = 0;
		
		this.status = Status.NOT_STARTED;
		
		//full reset
		if(tableName != null) {
			this.gameId = ((Long)random.nextLong()).toString();
			players.clear();
		}
		else {
			for (Player p : players.values()) {
				p.reset();
			}
		}
	}

	public boolean isGameOn() {
		return !status.equals(Status.NOT_STARTED) && !status.equals(Status.GAME_OVER);
	}
	public void setGameOn(boolean gameOn) {
		if (gameOn && !canStartGame())
			throw new IllegalStateException("Need all 4 players to start the game!"); 
		
		//do not remove players
		reset(null);

		this.status = Status.STARTED;
	}

	public synchronized String addPlayer(String newPlayerId, String name, String existingId, Integer pos) {
		
		//support for name update and position change
		if(!StringUtils.isEmpty(existingId)) {
			Player alterPlayer = null;
			for (Player p: players.values()) {
				if (existingId.equals(p.getId()))
						alterPlayer = p;
				else {
					if (!StringUtils.isEmpty(name) && p.getName().equalsIgnoreCase(name))		
						throw new IllegalStateException("Player with the name '"+ p.getName() +"' already exists, please pick a different name!"); 
			
					if (pos != null && p.getPosition() == pos.intValue())
						throw new IllegalStateException("Player with the name '"+ p.getName() +"' already is occupying position " + pos); 		
				}
			}		
			
			if(alterPlayer != null) {
				if(!StringUtils.isEmpty(name))
					alterPlayer.setName(name);
				if(pos != null)
					alterPlayer.setPosition(pos);
				
				logger.info("Player '" + alterPlayer.getName() +"' resitted at position " +  alterPlayer.getPosition() + " with id: " + alterPlayer.getId() );
			}
			

			
			return existingId;			
		}
		
		if (players.size() >= 4)
			throw new IllegalStateException("Can not have more than 4 players, sorry!");
		for (Player p: players.values()) {
			if (p.getName().equalsIgnoreCase(name))
				throw new IllegalStateException("Player with the name '"+ p.getName() +"' already exists, please pick a different name!"); 
		
			if (pos != null && p.getPosition() == pos.intValue())
				throw new IllegalStateException("Player with the name '"+ p.getName() +"' already is occupying position " + pos); 		
		}
		
		//generate unique id
		String id = newPlayerId;
		
		if(id == null) {
			id = ((Long)random.nextLong()).toString();
			while (players.containsKey(id)) {
				id = ((Long)random.nextLong()).toString();
			}
		}
		
		players.put(id, new Player(this, name, pos != null ? pos : players.size() + 1, id));
		
		version++;
		
		logger.info("Player '" + name +"' sitted at position " +  (pos != null ? pos : players.size() + 1) + " with id: " + id+ " Game Id: " + this.gameId);
		
		return id;
	}
	
	private void advanceCurrentTurnPosition() {
		currentTurnPosition++;
		if(currentTurnPosition > 4)
			currentTurnPosition = 1;
	}
	
	public boolean isPlayersTurn(String id) {
		return players.get(id).getPosition() == currentTurnPosition;
	}
	
	public void assignCards() {
		assignCards(false, null, null);
	}

	public void assignCards(boolean revert, int c[], Integer rn) {
		
		if(revert) {
			rn = this.prevRoundNumber;
			c = this.prevAssignment;
		}
		
		if(rn == null) {
			prevRoundNumber = roundNumber;
			roundNumber++;
		}
		else
			roundNumber = rn;
		
		if(roundNumber > 1) {
			this.prevPlay = new PlayState(currentPlay);
		}
		currentPlay.reset();
		
		actingPlayerPosition = roundNumber%4 == 0 ? 4 : roundNumber%4;
		currentTurnPosition = actingPlayerPosition;
		
		this.currentPlay.setKozyr(null);
		
		
		if(c == null) {
			c = new int[36];
			for (int i = 0; i<36; i++)
				c[i]=i+1;
			shuffle(c);
		}
		
		this.prevAssignment = c;
		
		if (roundNumber <= 8)
			numCards = roundNumber;
		else if (roundNumber <= 12 || roundNumber >= 21)
			numCards = 9;
		else
			numCards = 21 - roundNumber;
		
		
		//TODO remove after testing
		if (testNumCards != null) {
			numCards = testNumCards;
			if (testNumCards < 9)
				testNumCards++;
		}
		
		cardNumbers.add(numCards);
		
		int counter = 0;
		for (int j = 1; j<=numCards; j++) {
			for(Player p : players.values())
				p.addCard(c[counter++], j==1);
		}
		
		Card kk = null;
		if(counter < 36) {
			kk = new Card(c[counter]);
			this.currentPlay.setKozyr(kk);
		}	
		
		if(roundNumber > 1) {
			this.prevPlay.setKozyr(kk);
		}
		
		version++;
	}

	private static Random random =  new Random();

	public static void shuffle(int[] array) {
		int count = array.length;
		for (int i = count; i > 1; i--) {
			swap(array, i - 1, random.nextInt(i));
		}
	}
	private static void swap(int[] array, int i, int j) {
		int temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	public PlayerState getPlayerState(String id) {
		if(!isValidPlayer(id)) {
			throw new IllegalStateException("Not a valid player id!");
		}
		return new PlayerState (players.get(id), this);
	}
	public List<Player> getOpponents(String id) {
		List<Player> opponents = new ArrayList <Player> ();
		Integer curPlayerPos = null;
		
		//return empty list if this is the only player
		if (players.size() < 2)
		{
			if(id.equals("ALL"))
				opponents.addAll(this.players.values());//return yourself
			return opponents;
		}
		
		Player[] pa = new Player[4];
		
		for (Player p : players.values()) {
			if(p.getId() != id) {
				pa[p.getPosition()-1] = p.getOpponentCopy(p);
			}
			else
				curPlayerPos = p.getPosition();
		}
		
		if(curPlayerPos != null) {
			switch (curPlayerPos) {
			case 1:
				opponents.add(pa[1]);
				opponents.add(pa[2]);
				opponents.add(pa[3]);
				break;
			case 2:
				opponents.add(pa[2]);
				opponents.add(pa[3]);
				opponents.add(pa[0]);
				break;				
			case 3:
				opponents.add(pa[3]);
				opponents.add(pa[0]);
				opponents.add(pa[1]);
				break;				
			case 4:
				opponents.add(pa[0]);
				opponents.add(pa[1]);
				opponents.add(pa[2]);
				break;
			}
			
		}
		else
			return Arrays.asList(pa);
				
		return opponents;
	}
	
	private boolean canStartGame() {
		return players.size() == 4;
	}

	public void call(String playerId, int wantQty) {
		
		if(!isValidPlayer(playerId)) {
			throw new IllegalStateException("Not a valid player id!");
		}
		
		if (status != Status.DEALT) {
			// || Status.CALLS_MADE;
			//TODO: for friendly play allow option to:
			//if this is previous caller and next caller has not made a call yet allow to set again

			throw new IllegalStateException("To make a call app needs ot be in DEALT status, current state is: " + status);
		}
		
		if (this.getKozyr() == null) {
			throw new IllegalStateException("Can not make a call when kozyr is not set!");
		}
		
		if(!this.isPlayersTurn(playerId)) {		
			//TODO: for friendly play allow option to:
			//if this is previous caller and next caller has not made a call yet allow to set again
			
			throw new IllegalStateException("Not this players turn to act!");
		}
		
		validateCall(playerId, wantQty);
		
		players.get(playerId).setCall(wantQty);	
		players.get(playerId).setbWantsAll(wantQty == this.numCards);
			
		setCantCallNumberOnTheLastPlayer();
		
		this.advanceCurrentTurnPosition();
		
		//everyone made calls
		if(this.currentTurnPosition == this.actingPlayerPosition) {
			
			//calculate HuntLevel
			int calls = 0;
			for (Player p : this.players.values()) {
				calls += p.getCall();
			}
			this.currentPlay.setHuntLevel(this.numCards - calls);
			status = Status.CALLS_MADE;
		}
		
		version++;
	}

	private void setCantCallNumberOnTheLastPlayer() {
		int lastPos = actingPlayerPosition == 1 ? 4 : (actingPlayerPosition + 3) %4;
		int wants = 0;
		Player lp = null;
		for ( Player p : players.values()) {
			if (p.getPosition() == lastPos) 
				lp = p;
			else if (p.getCall() != null)
				wants += p.getCall();
		}
		
		lp.setCantCallNumer(this.numCards - wants);
	}

	private void validateCall(String playerId, int wantQty) {
		if(wantQty < 0 || wantQty > numCards) {
			throw new IllegalStateException("Please call between 0 and " + numCards);
		}
		
		if (players.get(playerId).getCantCallNumer() != null && players.get(playerId).getCantCallNumer().equals(wantQty)) {
			throw new IllegalStateException("Cant call "+wantQty);
		}
	}

	public void action(String playerId, int cardId, JokerAction jokerAction) {
		if(!isValidPlayer(playerId)) {
			throw new IllegalStateException("Not a valid player id!");
		}
		
		if (status != Status.CALLS_MADE && status != Status.PLAY_DONE) {
			throw new IllegalStateException("To make a play app needs ot be in CALLS_MADE or PLAY_DONE status, current state is: " + status);
		}
		
		if(!this.isPlayersTurn(playerId)) {		
			throw new IllegalStateException("Not this players turn to act!");
		}
		
		//ignore bogus joker actions
		if(jokerAction != null && cardId > 2) {		
			jokerAction = null;
		}
		
		//clear the table
		this.currentPlay.getActions().clear();
		
		players.get(playerId).removeCard(cardId);
		
		currentPlay.addAction(players.get(playerId).getPosition(), cardId, jokerAction);
		
		status = Status.PLAY_STARTED;
		
		this.advanceCurrentTurnPosition();
		
		version++;		
	}

	synchronized public void react(String playerId, int cardId, JokerReaction jokerReaction) {
		if(!isValidPlayer(playerId)) {
			throw new IllegalStateException("Not a valid player id!");
		}
		
		if (status != Status.PLAY_STARTED) {
			throw new IllegalStateException("To make a reaction play app needs ot be in PLAY_STARTED status, current state is: " + status);
		}
		
		if(!this.isPlayersTurn(playerId)) {		
			throw new IllegalStateException("Not this players turn to act!");
		}
		
		//ignore bogus joker reaction
		if(jokerReaction != null && cardId > 2) {		
			jokerReaction = null;
		}
		
		if (jokerReaction == null) {
			validateReaction(playerId, cardId);
		}		
		
		players.get(playerId).removeCard(cardId);
		
		currentPlay.addReaction(players.get(playerId).getPosition(), cardId, jokerReaction);
		
		this.advanceCurrentTurnPosition();
		
		//everyone made their play?
		if(this.currentTurnPosition == this.actingPlayerPosition) {

			calculatePlayResult();
			status = Status.PLAY_DONE;
			
			//set new acting position based on who took the play
			this.actingPlayerPosition = this.currentPlay.getWinningAction().getPlayerPosition();
			this.setCurrentTurnPosition(this.actingPlayerPosition);
		
			//hand is over?
			if(players.get(playerId).getCards().isEmpty()) {
				//calculate results table
				updateTakesForAllPalyers();
				calculateHandResult();		
				
				this.currentPlay.setHuntLevel(null);
				//game over?
				if(roundNumber == 24) {
					status = Status.GAME_OVER;
				}
				else {
					//deal next hand
					assignCards();
					status = Status.DEALT;
				}
			}
		}
		
		version++;		
	}

	private void validateReaction(String playerId, int cardId) {
	
		Card rCard = Card.cardMap.get(cardId);
		
		CardSuite actingSuite = currentPlay.getActingSuite();
		
		if(actingSuite != rCard.suite) {
			if(players.get(playerId).hasSuite(actingSuite)) {
				throw new IllegalStateException("Please react with the acting suite, acting suite is: " + actingSuite);
			} else if (currentPlay.getKozyr().getSuite() != CardSuite.BEZ && 
					   rCard.suite != currentPlay.getKozyr().getSuite() && 
					   players.get(playerId).hasSuite(currentPlay.getKozyr().getSuite())) {
				throw new IllegalStateException("Please react with kozyr!");				
			}
		}
		else if (currentPlay.isJokerActionWant() && players.get(playerId).hasHigherSuiteCard(rCard)) {
			throw new IllegalStateException("Please react with the highest card in sute: " + actingSuite);
		}
	}

	private void calculatePlayResult() {
		for (Player p : players.values()) {
			if(p.getPosition() == currentPlay.getWinningAction().getPlayerPosition()) {
				p.setTaken(p.getTaken()+1);
				break;
			}
		}		
	}
	
	private void updateTakesForAllPalyers() {
		for (Player p : players.values()) {
			p.addTakes();
		}
	}
	
	private void calculateHandResult() {
		for (Player p : players.values()) {
			p.calculateHandResult();
		}	
	}
	
	protected int getNumOfBonusesThisRound(int n) {
		int numBonuses = 0;
		for (Player p : players.values()) {
			if (Player.isBonusRoundForPlayer(p, n))
				numBonuses++;
		}	
		return numBonuses;
	}

	public String setTestNumCards(Integer numCards) {
		this.testNumCards = numCards;
		return this.gameId;
	}

/*	
	public void fastForward(PlayerController cntrl, Integer roundNumber) {
		
		if (this.status == Status.NOT_STARTED || this.status == Status.GAME_OVER) 
			throw new IllegalStateException("You can only fast forward a started game! Current State is: " + this.status); 
		
		logger.info("Started Fast Forward at state: " + this.status);
		
		int iter = 0;
		while (roundNumber > this.getRoundNumber()) {
			
			logger.info("Fast Forward iterration # " + iter++ + " State is '" + this.status + "' Round# " + this.getRoundNumber());
			
			for (Player p : this.players.values()) {
				if(roundNumber == this.getRoundNumber())
					break;
				
				if (p.getPosition() != this.getCurrentTurnPosition())
					continue;
				
				boolean bError = false;
	
				if (this.status == Status.DEALT) {
					try {						
						if (this.getKozyr() == null) {							
							logger.info("Fast Forward iterration set kozyr");
							cntrl.setKozyr(gameId, p.getId(), CardSuite.BEZ);
						}
	
						cntrl.call(gameId, p.getId(), this.getNumCards()/2);
						
					} catch (Exception e)
					{
						bError = true;
					}
					if (bError)
						logger.info("Fast Forward error making call");
					else
						logger.info("Fast Forward made call");
					continue;
				}		
				
				else if(this.status == Status.CALLS_MADE || this.status == Status.PLAY_STARTED || this.status == Status.PLAY_DONE) {
					//act-react
					int cardIndex = 0;
					do {
						boolean actionMade = false;
						bError = false;
						try {
							if((this.status == Status.CALLS_MADE || this.status == Status.PLAY_DONE) && p.getPosition() == this.getActingPlayerPosition()) {	
								actionMade = true;
								cntrl.action(gameId, p.getId(), p.getCards().get(cardIndex).getId(), null);
								logger.info("Fast Forward made action " + p.getName());
							}
							else if (p.getPosition() == this.currentTurnPosition) {
									actionMade = true;
									cntrl.reaction(gameId, p.getId(), p.getCards().get(cardIndex).getId(), null);
									logger.info("Fast Forward made reaction " + p.getName());
							}
						} catch (Exception e) {
							bError = true;
							logger.info("Fast Forward action or reaction error " + p.getName());
						}
						cardIndex++;
						
						if(!actionMade)
							logger.info("Fast Forward action or reaction skipped " + p.getName());

					} while (bError);
				}
				
			}//for players

			
		}//while roundNumber
	}
*/
}

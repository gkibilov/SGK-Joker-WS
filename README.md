# SGK-Joker-WS
Websoket+ STOMP based joker game server

- assigning a unique user id as principal to each connecting client (see AssignPrincipalHandshakeHandler)
- usage of messagingTemplate.convertAndSendToUser for targeting a specific client see GameManager.processPlayerMessage

## Expected behavior
- Connect to /sgk-joker-ws.connect
- Users call /app/getAllGames, /app/newGame, /app/addPlayer, /app/playerMessage) with coresponding messages described below
- The User receives player state to /private/reply: PlayerState
- The User receives error message to /queue/errors: String
- All connected users receive list of game infos to /topic/games: List<GameInfo>

## Request Messages

public class NewGame {
	private String name;
}

public class AddPlayer {	
	private String gameId;
	private String name;
	private String existingId;
	private Integer pos;
}
public class PlayerMessage {
	
	public enum MessageType {
		START,
		CALL,
		SET_KOZYR,
		ACTION,
		REACTION,
		MESSAGE
	}
	
	MessageType type;
	
	private String gameId;
	private String payerId;
	
	private CardSuite kozyrSuite;
	
	private Integer wantQty;
	
	private Integer cardId;
	private JokerReaction jokerReaction;
	private JokerAction jokerAction;
	
	private String message;	
}

public enum CardSuite {	
	HART,DIAMOND,CLUB,SPADE,BEZ
}
public enum JokerAction {
	WANT_HART, WANT_DIAMOND, WANT_CLUB, WANT_SPADE, TAKE_HART, TAKE_DIAMOND, TAKE_CLUB, TAKE_SPADE;
}
public enum JokerReaction {
	WANT, TAKE;
}

## Response Messages

public class GameInfo {	
	private String gameId;
	private String gameName;	
	private int roundNumber = 0;		
	private Status status = Status.NOT_STARTED;	
	private List<Player> players;
}

public class PlayerState {	
	private GameState state;	
	private Player player;	
	private List<Player> opponents;
}

package com.sgk.joker.websoket;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
//import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.sgk.joker.websoket.model.GameInfo;
import com.sgk.joker.websoket.model.Greeting;
import com.sgk.joker.websoket.model.HelloMessage;
import com.sgk.joker.websoket.model.PlayerState;
import com.sgk.joker.websoket.model.ServerError;
import com.sgk.joker.websoket.model.Request.AddPlayer;
import com.sgk.joker.websoket.model.Request.NewGame;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlayerControllerTests {

	@LocalServerPort
	private int port;

	private static SockJsClient sockJsClient;

	private static WebSocketStompClient stompClient;
	//private static WebSocketStompClient stompClient2;

	private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
	
	static String gameId ;
	static String playerId = "";
	static boolean addPlayerQueueworked = false;

	//@BeforeEach
	@BeforeAll
	public static void setup() {
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		sockJsClient = new SockJsClient(transports);

		
		stompClient = new WebSocketStompClient(sockJsClient);
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
		
		//stompClient2 =  new WebSocketStompClient(sockJsClient);
		//stompClient2.setMessageConverter(new MappingJackson2MessageConverter());
	}

	//@Test
	public void getGreeting() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/greetings", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return Greeting.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						Greeting greeting = (Greeting) payload;
						try {
							assertEquals("Hello, Spring!", greeting.getContent());
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					session.send("/app/hello", new HelloMessage("Spring"));
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		stompClient.connect("ws://localhost:{port}/sgk-joker-ws.connect", this.headers, handler, this.port);

		if (latch.await(100, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		}
		else {
			fail("Greeting not received");
		}

	}
	
	
	@Test
	@Order(1)
	public void addNewGame() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();
		
		Gson gson = new Gson();


		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/games", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return List.class;
						//return ResolvableType.forClassWithGenerics(List.class, GameInfo.class).getType();
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						List<?> gameInfos = (List<?>) payload;
						try {
							assertEquals(1, gameInfos.size());					
							
							JsonElement jsonElement = gson.toJsonTree(gameInfos.get(0));
							GameInfo gi = gson.fromJson(jsonElement, GameInfo.class);
							
							assertEquals("Test Game 1", gi.getGameName());
							assertNotNull(gi.getGameId());
							
							gameId = gi.getGameId();
							
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					session.send("/app/newGame", new NewGame("Test Game 1"));
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		stompClient.connect("ws://localhost:{port}/sgk-joker-ws.connect", this.headers, handler, this.port);

		if (latch.await(100, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		}
		else {
			fail("Game infos were not recived");
		}

	}	
	

	@Test
	@Order(2)
	public void getAllGames() throws Exception {

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();
		
		Gson gson = new Gson();


		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				session.subscribe("/topic/games", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return List.class;
						//return ResolvableType.forClassWithGenerics(List.class, GameInfo.class).getType();
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						List<?> gameInfos = (List<?>) payload;
						try {
							assertEquals(1, gameInfos.size());					
							
							JsonElement jsonElement = gson.toJsonTree(gameInfos.get(0));
							GameInfo gi = gson.fromJson(jsonElement, GameInfo.class);
							
							assertEquals("Test Game 1", gi.getGameName());
							assertNotNull(gi.getGameId());
							
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					session.send("/app/getAllGames", null);
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		stompClient.connect("ws://localhost:{port}/sgk-joker-ws.connect", this.headers, handler, this.port);

		if (latch.await(100, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		}
		else {
			fail("Game infos were not recived");
		}

	}	

	
	@Test
	@Order(3)
	public void addPlayer() throws Exception {
		
		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();
		
		Gson gson = new Gson();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				
				session.subscribe("/topic/games", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return List.class;
						//return ResolvableType.forClassWithGenerics(List.class, GameInfo.class).getType();
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						List<?> gameInfos = (List<?>) payload;
						try {
							assertEquals(1, gameInfos.size());					
							
							JsonElement jsonElement = gson.toJsonTree(gameInfos.get(0));
							GameInfo gi = gson.fromJson(jsonElement, GameInfo.class);
							
							assertEquals("Test Game 1", gi.getGameName());
							assertEquals(gameId, gi.getGameId());
							
							assertEquals(1, gi.getPlayers().size());
							assertEquals("Test Player 1", gi.getPlayers().get(0).getName());
							
							addPlayerQueueworked = true;
							
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							session.disconnect();
							latch.countDown();
						}
					}
				});
				
				session.subscribe("/user/reply", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						//return PlayerState.class;
						return Object.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						//PlayerState ps = (PlayerState) payload;
						
						JsonElement jsonElement = gson.toJsonTree(payload);
						PlayerState ps = gson.fromJson(jsonElement, PlayerState.class);
						
						try {
							assertNotNull(ps.getPlayer().getId());
							playerId = ps.getPlayer().getId();
							
						} catch (Throwable t) {
							failure.set(t);
						} finally {
							//session.disconnect();
							//latch.countDown();
						}
					}
				});						
				
				session.subscribe("/user/queue/errors", new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return ServerError.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						ServerError error = (ServerError)payload;				
						failure.set(new Exception(error.getMessage()));										
						session.disconnect();
						latch.countDown();
					}
				});						
				
				try {
					session.send("/app/addPlayer", new AddPlayer(gameId, "Test Player 1", null, null));
				} catch (Throwable t) {
					failure.set(t);
					latch.countDown();
				}
			}
		};

		stompClient.connect("ws://localhost:{port}/sgk-joker-ws.connect", this.headers, handler, this.port);

		if (latch.await(100, TimeUnit.SECONDS)) {
			if (failure.get() != null) {
				throw new AssertionError("", failure.get());
			}
		}
		else {
			fail("Game infos were not recived");
		}
		
		
		
		
		//this confirms both queues worked
		assertTrue(!playerId.isEmpty() && addPlayerQueueworked);

	}
	

	private class TestSessionHandler extends StompSessionHandlerAdapter {

		private final AtomicReference<Throwable> failure;

		public TestSessionHandler(AtomicReference<Throwable> failure) {
			this.failure = failure;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			this.failure.set(new Exception(headers.toString()));
		}

		@Override
		public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
			this.failure.set(ex);
		}

		@Override
		public void handleTransportError(StompSession session, Throwable ex) {
			this.failure.set(ex);
		}
	}
}

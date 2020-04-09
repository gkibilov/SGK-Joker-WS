package com.sgk.joker.websoket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Assign a random username as principal for each websocket client. This is
 * needed to be able to communicate with a specific client.
 */
public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {

	private static Random random = new Random();
	public static final String ATTR_PRINCIPAL = "__principal__";
	public static final String ATTR_PLAYER_ID = "username";
	
	private static Set<String> uniqueUserId = new HashSet<String>();
	
	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		final String name;
		if (!attributes.containsKey(ATTR_PRINCIPAL)) {
			name = generateUniqueUserId();
			attributes.put(ATTR_PRINCIPAL, name);
		} else {
			name = (String) attributes.get(ATTR_PRINCIPAL);
		}
		return new Principal() {
			@Override
			public String getName() {
				return name;
			}
		};
	}
	
	synchronized private String generateUniqueUserId() {
		String uid = null;
		do{
			uid = ((Long)random.nextLong()).toString();
		} while(uniqueUserId.contains(uid));
		uniqueUserId.add(uid);
		return uid;
	}
}
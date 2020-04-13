package com.sgk.joker.websoket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Assign a unique username as principal for each websocket client. This is
 * needed to be able to communicate with a specific client.
 */
public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {

	private static Random random = new Random();
	public static final String ATTR_PRINCIPAL = "__principal__";
	public static final String ATTR_PLAYER_ID = "username";
	
	private static Set<String> uniqueUserId = new HashSet<String>();
	
	protected final Log logger = LogFactory.getLog("com.sgk.joker.websoket.AssignPrincipalHandshakeHandler");
	
	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		
		final String name;
		
		if (request instanceof ServletServerHttpRequest) {

			if (!attributes.containsKey(ATTR_PRINCIPAL)) {				
				name = generateUniqueUserId();
				attributes.put(ATTR_PRINCIPAL, name);
				logger.info("AssignPrincipalHandshakeHandler.determineUser gennerated new: " + name);						
			}
			else {
				name = (String) attributes.get(ATTR_PRINCIPAL);
				logger.info("AssignPrincipalHandshakeHandler.determineUser use existing: " + name);
			}
		}
		else {
			name = "undefined";
			logger.warn("AssignPrincipalHandshakeHandler.determineUser not ServletServerHttpRequest principal name will be undefined");
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
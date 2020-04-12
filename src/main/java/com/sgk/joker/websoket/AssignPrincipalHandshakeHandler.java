package com.sgk.joker.websoket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpSession;

/**
 * Assign a random username as principal for each websocket client. This is
 * needed to be able to communicate with a specific client.
 */
public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {

	private static Random random = new Random();
	public static final String ATTR_PRINCIPAL = "__principal__";
	public static final String ATTR_PLAYER_ID = "username";
	
	private static Set<String> uniqueUserId = new HashSet<String>();
	
	private static Map<String, String> sessionTOUser = new HashMap<String, String>();
	
	
	protected final Log logger = LogFactory.getLog("com.sgk.joker.websoket.AssignPrincipalHandshakeHandler");
	
	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		
		final String name;
		
		HttpSession session = null;
		
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			session = servletRequest.getServletRequest().getSession();
			logger.info("determineUser for session id: " + session.getId());

			if (!attributes.containsKey(ATTR_PRINCIPAL)) {
				
				if(sessionTOUser.get(session.getId()) != null) {
					name = sessionTOUser.get(session.getId());
					attributes.put(ATTR_PRINCIPAL, name);
					logger.info("determineUser for principal " + request.getPrincipal() + " reuse id: " + name + " for session: " + session.getId());
				}
				else {	
					name = generateUniqueUserId();
					sessionTOUser.put(session.getId(), name);
					attributes.put(ATTR_PRINCIPAL, name);
					logger.info("determineUser for principal " + request.getPrincipal() + " gennerated new id: " + name);
				}
				
			}
			else {
					name = (String) attributes.get(ATTR_PRINCIPAL);
					logger.info("determineUser for principal " + request.getPrincipal() + " reuse id: " + name);
			}
		}
		else {
			name = "unknown";
			logger.info("determineUser for principal " + request.getPrincipal() + " reuse id: " + name);
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
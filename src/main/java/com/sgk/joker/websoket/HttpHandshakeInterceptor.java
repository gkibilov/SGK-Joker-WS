package com.sgk.joker.websoket;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import  org.springframework.util.StringUtils;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {
	
	protected final Log logger = LogFactory.getLog("com.sgk.joker.websoket.HandshakeInterceptor");


	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
			String authToken = servletRequest.getServletRequest().getParameter("auth_token");
			logger.info("HttpHandshakeInterceptor.beforeHandshake auth_token: " +  authToken);
			if(!StringUtils.isEmpty(authToken) && !authToken.equalsIgnoreCase("undefined")) {
				attributes.put(AssignPrincipalHandshakeHandler.ATTR_PRINCIPAL, authToken);
			}
			return true;
		}
		
		logger.warn("HttpHandshakeInterceptor.beforeHandshake non ServletServerHttpRequest");
		
		return false;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		// TODO Auto-generated method stub
		
	}
}
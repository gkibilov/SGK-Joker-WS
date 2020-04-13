package com.sgk.joker.websoket;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.MultiValueMap;

public class UserSessionChannelInterceptor implements ChannelInterceptor {
	
	protected final Log logger = LogFactory.getLog("com.sgk.joker.websoket.UserSessionChannelInterceptor");
	

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {

		logger.info("UserSessionChannelInterceptor");
		
		MessageHeaders headers = message.getHeaders();
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		
		@SuppressWarnings("unchecked")
		MultiValueMap<String, String> multiValueMap = headers.get(StompHeaderAccessor.NATIVE_HEADERS, MultiValueMap.class);
	
		for (Map.Entry<String,List<String>> head : multiValueMap.entrySet()) 
		{
			logger.info(head.getKey() + "#" + head.getValue());
			
			if (head.getKey().equals("USER_ID")) {
				AssignPrincipalHandshakeHandler.setUserId("", head.getValue().get(0));
			}
		}

		
		return ChannelInterceptor.super.preSend(message, channel);
	}

}

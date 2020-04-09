package com.sgk.joker.websoket;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import com.sgk.joker.websoket.controller.PlayerController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static Log logger = LogFactory.getLog("com.sgk.joker.websoket.WebSocketConfig");
    
    public static final String ENDPOINT_CONNECT = "/sgk-joker-ws.connect";
    
    public static final String SUBSCRIBE_USER_REPLY = "/reply";
    public static final String SUBSCRIBE_USER_PREFIX = "/private";


    
	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic","/queue", SUBSCRIBE_USER_REPLY);
		config.setUserDestinationPrefix(SUBSCRIBE_USER_PREFIX);
		config.setApplicationDestinationPrefixes("/app");
	}

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    	
    	//registry.addEndpoint("/sgk-joker-ws").withSockJS();
//    	public static final String ENDPOINT_GET_ALL_GAMES = "/getAllGames";
//    	public static final String ENDPOINT_NEW_GAME = "/newGame";
//    	public static final String ENDPOINT_ADD_PLAYER = "/addPlayer";
//    	public static final String ENDPOINT_PLAYER_MESSAGE = "/playerMessage";
        registry.addEndpoint(ENDPOINT_CONNECT, 
        					PlayerController.ENDPOINT_GET_ALL_GAMES,
        					PlayerController.ENDPOINT_NEW_GAME,
        					PlayerController.ENDPOINT_ADD_PLAYER,
                            PlayerController.ENDPOINT_PLAYER_MESSAGE)
            .setAllowedOrigins("*")
            // assign a random userId as principal for each websocket client to communicate with a specific client
            //.withSockJS();
        	.setHandshakeHandler(new AssignPrincipalHandshakeHandler()).withSockJS();
        	//.addInterceptors(new HttpHandshakeInterceptor()).withSockJS();
    }

    // various listeners for debugging purpose

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        logger.info("<==> handleSubscribeEvent: username="+event.getUser().getName()+", event="+event);
    }

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {
    	logger.info("===> handleConnectEvent: username="+event.getUser().getName()+", event="+event);
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
    	logger.info("<=== handleDisconnectEvent: username="+event.getUser().getName()+", event="+event);
    }	

}

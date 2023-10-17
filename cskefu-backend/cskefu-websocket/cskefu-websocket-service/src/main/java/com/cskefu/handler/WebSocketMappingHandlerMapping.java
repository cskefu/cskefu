package com.cskefu.handler;

import com.cskefu.annotations.WebSocketMapping;
import org.springframework.beans.BeansException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class WebSocketMappingHandlerMapping extends SimpleUrlHandlerMapping {
	
	private Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

    @Override
    public void initApplicationContext() throws BeansException {
        Map<String, Object> beanMap = obtainApplicationContext().getBeansWithAnnotation(WebSocketMapping.class);
        beanMap.values().forEach(bean -> {
            if (!(bean instanceof WebSocketHandler)) {
                throw new RuntimeException(String.format("Controller [%s] doesn't implement WebSocketHandler interface.", bean.getClass().getName()));
            }
            WebSocketMapping annotation = AnnotationUtils.getAnnotation(bean.getClass(), WebSocketMapping.class);
            //webSocketMapping 映射到管理中
            handlerMap.put(Objects.requireNonNull(annotation).value(), (WebSocketHandler) bean);
        });
        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.setUrlMap(handlerMap);
        super.initApplicationContext();
    }
}
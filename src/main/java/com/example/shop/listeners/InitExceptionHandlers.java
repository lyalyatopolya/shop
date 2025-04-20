package com.example.shop.listeners;

import com.example.shop.web.exception.CustomErrorHandler;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InitExceptionHandlers {

    @EventListener
    public void logSessionInits(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(
                sessionInitEvent -> VaadinSession.getCurrent().setErrorHandler(new CustomErrorHandler()));
    }

}
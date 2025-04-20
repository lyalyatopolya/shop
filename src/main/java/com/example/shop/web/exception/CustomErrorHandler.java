package com.example.shop.web.exception;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.ResourceAccessException;

public class CustomErrorHandler implements ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

    @Override
    public void error(ErrorEvent errorEvent) {
        if (errorEvent.getThrowable() instanceof ResourceAccessException) {
            logger.error("Something wrong happened", errorEvent.getThrowable());
            UI.getCurrent().access(() -> {
                Notification notification = new Notification("Отсутствует подключение к api");
                notification.setDuration(3000);
                notification.setPosition(Notification.Position.MIDDLE);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
            });
        }
    }
}

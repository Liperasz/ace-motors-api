package io.github.liperasz.acemotors.event;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEvent;

// Evento disparado sempre que um recurso novo é criado (POST)
public class ResourceCreatedEvent extends ApplicationEvent {

    private final HttpServletResponse response;

    private final Long id;

    public ResourceCreatedEvent (Object source, HttpServletResponse response, Long id) {
        super(source);
        this.response = response;
        this.id = id;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public Long getId() {
        return id;
    }
}

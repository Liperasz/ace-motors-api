package io.github.liperasz.acemotors.event.listener;

import io.github.liperasz.acemotors.event.ResourceCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

// Escuta o ResourceCreatedEvent e adiciona o header Location na resposta
// (ex: POST /categories -> Location: http://localhost:8080/categories/1)
@Component
public class ResourceCreatedListener {

    @EventListener
    public void onResourceCreated(ResourceCreatedEvent event) {
        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(event.getId())
                .toUri();

        event.getResponse().setHeader("Location", uri.toASCIIString());
    }
}

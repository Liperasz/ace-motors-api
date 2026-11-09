package io.github.liperasz.acemotors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

// VIA_DTO: as páginas (Page) saem num JSON estável: { "content": [...], "page": { size, number, totalElements, totalPages } }
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class AceMotorsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AceMotorsApplication.class, args);
    }

}

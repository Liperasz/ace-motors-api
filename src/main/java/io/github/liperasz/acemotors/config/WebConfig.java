package io.github.liperasz.acemotors.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

// Expõe a pasta de uploads como arquivos estáticos: GET /uploads/products/xxx.webp
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebConfig (@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // A pasta precisa terminar com "/" para o Spring tratá-la como diretório
        String location = Path.of(uploadDir).toAbsolutePath().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}

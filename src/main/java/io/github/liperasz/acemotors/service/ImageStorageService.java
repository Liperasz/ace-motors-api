package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.service.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

// Salva as imagens em uma pasta do servidor; o banco guarda só a URL (RNF-A04..06)
@Service
public class ImageStorageService {

    private final Path uploadDir;

    public ImageStorageService (@Value("${app.upload-dir}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir).toAbsolutePath();
    }

    // Salva a imagem em <upload-dir>/<folder>/ e devolve a URL pública (/uploads/<folder>/<arquivo>)
    public String saveWebp(MultipartFile file, String folder) {
        if (file.isEmpty() || !isWebp(file)) {
            throw new BusinessException("image.invalid", "Arquivo vazio ou que não é WebP");
        }

        String fileName = UUID.randomUUID() + ".webp";

        try {
            Path folderPath = uploadDir.resolve(folder);
            Files.createDirectories(folderPath);
            file.transferTo(folderPath.resolve(fileName));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar a imagem", e);
        }

        return "/uploads/" + folder + "/" + fileName;
    }

    // Apaga o arquivo de uma URL salva antes (usado ao trocar a imagem)
    public void delete(String url) {
        if (url == null || !url.startsWith("/uploads/")) {
            return;
        }

        try {
            Files.deleteIfExists(uploadDir.resolve(url.substring("/uploads/".length())));
        } catch (IOException e) {
            // Não impede a operação: no pior caso fica um arquivo sobrando na pasta
        }
    }

    // Um arquivo WebP começa com "RIFF" e tem "WEBP" a partir do byte 8
    private boolean isWebp(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            return bytes.length > 12
                    && new String(bytes, 0, 4).equals("RIFF")
                    && new String(bytes, 8, 4).equals("WEBP");
        } catch (IOException e) {
            return false;
        }
    }
}

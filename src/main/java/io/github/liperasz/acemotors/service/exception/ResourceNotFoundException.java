package io.github.liperasz.acemotors.service.exception;

// Lançada quando um recurso buscado pelo id não existe (vira 404 no ApiExceptionHandler)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException (String resource, Long id) {
        super(resource + " com id " + id + " não encontrado(a)");
    }
}

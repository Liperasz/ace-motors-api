package io.github.liperasz.acemotors.exceptionhandler;

import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

// Trata as exceções de todos os controllers em um lugar só.
// Toda resposta de erro é uma lista de ApiError (mensagem para o usuário + mensagem para o desenvolvedor)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public ApiExceptionHandler (MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public record ApiError(String userMessage, String developerMessage) {
    }

    // JSON mal formado ou com atributo desconhecido
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ApiError> errors = List.of(new ApiError(message("message.invalid"), rootCause(ex)));
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    // Erros de Bean Validation (@Valid): um erro por campo inválido
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ApiError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toApiError)
                .toList();
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    // Bean Validation em parâmetros da URL (ex: @Positive em ?subtotal=)
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ApiError> errors = ex.getAllErrors().stream()
                .map(error -> new ApiError(messageSource.getMessage(error, LocaleContextHolder.getLocale()),
                        error.toString()))
                .toList();
        return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
    }

    // Recurso não encontrado -> 404
    @ExceptionHandler({ResourceNotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        List<ApiError> errors = List.of(new ApiError(message("resource.not-found"), ex.toString()));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    // Violação de integridade no banco (ex: excluir categoria que ainda tem produtos)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        List<ApiError> errors = List.of(new ApiError(message("resource.operation-not-allowed"), rootCause(ex)));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // Parâmetro com tipo errado na URL (ex: /categories/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        List<ApiError> errors = List.of(new ApiError(message("parameter.invalid"), ex.getMessage()));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // Ordenação por um campo que não existe (ex: ?sort=abc)
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Object> handlePropertyReference(PropertyReferenceException ex, WebRequest request) {
        List<ApiError> errors = List.of(new ApiError(message("parameter.invalid"), ex.getMessage()));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    // Regras de negócio (ex: cliente inativo, cupom expirado)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusiness(BusinessException ex, WebRequest request) {
        List<ApiError> errors = List.of(new ApiError(message(ex.getMessageKey()), ex.getMessage()));
        return handleExceptionInternal(ex, errors, new HttpHeaders(), ex.getStatus(), request);
    }

    // Demais erros que o próprio Spring trata (parâmetro faltando, método HTTP errado, URL inexistente...)
    // vêm com corpo no formato ProblemDetail; aqui ele é convertido para o mesmo formato de ApiError
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode statusCode, WebRequest request) {

        if (body == null || body instanceof ProblemDetail) {
            String key = statusCode.value() == 404 ? "resource.not-found" : "request.invalid";
            String developerMessage = body instanceof ProblemDetail problem ? problem.getDetail() : ex.getMessage();
            body = List.of(new ApiError(message(key), developerMessage));
        }

        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    private ApiError toApiError(FieldError fieldError) {
        String userMessage = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
        return new ApiError(userMessage, fieldError.toString());
    }

    private String message(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String rootCause(Exception ex) {
        return NestedExceptionUtils.getMostSpecificCause(ex).toString();
    }
}

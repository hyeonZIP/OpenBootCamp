package hyeonzip.openbootcamp.common.exception;

import hyeonzip.openbootcamp.common.response.ApiResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OpenBootCampException.class)
    public ResponseEntity<ApiResponse<Void>> handleOpenBootCampException(OpenBootCampException e) {
        log.warn("[OpenBootCampException] errorCode={}, message={}", e.getErrorCode(),
            e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
            .status(errorCode.getStatus())
            .body(ApiResponse.fail(e.getMessage(), errorCode.name()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("[ValidationException] {}", message);
        return ResponseEntity
            .badRequest()
            .body(ApiResponse.fail(message, ErrorCode.INVALID_INPUT.name()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(
        NoResourceFoundException e) {
        log.warn("[NoResourceFoundException] {}", e.getMessage());
        return ResponseEntity
            .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
            .body(ApiResponse.fail(ErrorCode.RESOURCE_NOT_FOUND.getMessage(),
                ErrorCode.RESOURCE_NOT_FOUND.name()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[UnhandledException]", e);
        return ResponseEntity
            .internalServerError()
            .body(ApiResponse.fail(
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                ErrorCode.INTERNAL_SERVER_ERROR.name()
            ));
    }
}

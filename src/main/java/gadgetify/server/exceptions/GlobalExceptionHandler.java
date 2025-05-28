package gadgetify.server.exceptions;

import gadgetify.server.dtos.Response;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response<Void>> handleRuntimeException(RuntimeException e) {
        var data = Response.<Void>builder()
                .success(false)
                .error("Error, please try again later or contact support.")
                .build();
        return ResponseEntity.badRequest().body(data);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception e) {
        var data = Response.<Void>builder()
                .success(false)
                .error("Error, please try again later or contact support.")
                .build();
        return ResponseEntity.internalServerError().body(data);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Response<Void>> handleAuthorizationDeniedException(AuthorizationDeniedException e) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        if (user == null) user = "anonymousUser";
        var data = Response.<Void>builder()
                .success(false)
                .error(user.equals("anonymousUser") ? "Unauthorized" : "Forbidden")
                .build();
        return ResponseEntity.status(user.equals("anonymousUser") ? 401 : 403).body(data);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        var data = Response.<Void>builder()
                .success(false)
                .error(e.getMessage())
                .build();
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        var data = Response.<Void>builder()
                .success(false)
                .error(e.getMessage())
                .build();
        return ResponseEntity.badRequest().body(data);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        var data = Response.<Void>builder()
                .success(false)
                .error(errorMessages)
                .build();
        return ResponseEntity.badRequest().body(data);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String errorMessage;
        if (e.getMessage().contains("duplicate key")) {
            errorMessage = "Duplicate data";
        } else {
            errorMessage = "Invalid data";
        }
        var data = Response.<Void>builder()
                .success(false)
                .error(errorMessage)
                .build();
        return ResponseEntity.badRequest().body(data);
    }

}

package br.com.links_vault_back_springboot.exception;

import br.com.links_vault_back_springboot.dto.ApiResponseDTO;
import br.com.links_vault_back_springboot.dto.FieldErrorDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.security.auth.login.CredentialException;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO> handleRuntimeException(
            RuntimeException runtimeException
    ) {
        ApiResponseDTO apiResponseDTO = ApiResponseDTO
                .builder()
                .message(runtimeException.getMessage() == null ? "Ocorreu um erro interno, tente novamente" : runtimeException.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        List<FieldErrorDTO> fieldErrorDTOS = new ArrayList<>();

        methodArgumentNotValidException.getBindingResult().getFieldErrors().forEach(err -> {
            FieldErrorDTO fieldErrorDTO = new FieldErrorDTO(err.getDefaultMessage(), err.getField());
            fieldErrorDTOS.add(fieldErrorDTO);
        });

        ApiResponseDTO apiResponseDTO = ApiResponseDTO
                .builder()
                .message("Campos inválidos.")
                .data(fieldErrorDTOS)
                .build();

        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiResponseDTO> handleEntityExistsException(
            EntityExistsException entityExistsException
    ) {
        ApiResponseDTO apiResponseDTO = ApiResponseDTO
                .builder()
                .message(entityExistsException.getMessage())
                .data(null)
                .build();

        return new ResponseEntity<>(apiResponseDTO, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(CredentialException.class)
    public ResponseEntity<ApiResponseDTO> handleCredentialException(
            CredentialException credentialException
    ) {
        ApiResponseDTO apiResponseDTO = ApiResponseDTO
                .builder()
                .message(credentialException.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponseDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseDTO> handleEntityNotFoundException(
            EntityNotFoundException entityNotFoundException
    ) {
        ApiResponseDTO apiResponseDTO = ApiResponseDTO
                .builder()
                .message(entityNotFoundException.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO);
    }

}

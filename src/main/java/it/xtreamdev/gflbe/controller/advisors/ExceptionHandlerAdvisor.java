package it.xtreamdev.gflbe.controller.advisors;

import it.xtreamdev.gflbe.dto.common.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ExceptionHandlerAdvisor {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(HttpClientErrorException ex) {

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorDTO.builder()
                        .message(ex.getStatusText())
                        .statusCode(ex.getRawStatusCode())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errorMessage = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error.getDefaultMessage() != null)
                errorMessage.add(error.getDefaultMessage());
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorDTO.builder()
                        .message(String.join("|", errorMessage))
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .build());
    }


}

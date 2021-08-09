package it.xtreamdev.gflbe.controller;

import it.xtreamdev.gflbe.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@RestController
public class ExceptionHandlerController {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDTO handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errorMessage = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error.getDefaultMessage() != null)
                errorMessage.add(error.getDefaultMessage());
        });
        return ErrorDTO.builder()
                .message(String.join("|", errorMessage))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }


}

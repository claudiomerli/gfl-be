package it.xtreamdev.gflbe.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class GLFException extends RuntimeException{

    private String message;
    private HttpStatus status;

}

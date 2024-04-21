package io.github.tomcatlab.kfc.registry;

import lombok.Data;
import org.springframework.http.HttpStatus;
@Data
public class ExceptionResponse {
    private HttpStatus httpStatus;
    private String message;
    public ExceptionResponse(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}

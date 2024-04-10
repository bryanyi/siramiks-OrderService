package com.siramiks.OrderService.exception;


import com.siramiks.OrderService.feign.ProductResponse.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class ProductResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
    ErrorResponse errorResponse = ErrorResponse.builder()
            .errorMessage(exception.getMessage())
            .errorCode(exception.getErrorCode())
            .build();
    return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(exception.getStatus()));
  }
}

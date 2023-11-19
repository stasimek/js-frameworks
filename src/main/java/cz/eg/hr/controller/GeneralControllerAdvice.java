package cz.eg.hr.controller;

import cz.eg.hr.rest.Errors;
import cz.eg.hr.rest.ValidationError;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<ValidationError> errorList = result.getFieldErrors().stream()
                .map(e -> new ValidationError(e.getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(new Errors(errorList));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Errors> handleValidationException(IllegalArgumentException ex) {
        List<ValidationError> errorList = List.of(new ValidationError(null, ex.getMessage()));
        return ResponseEntity.badRequest().body(new Errors(errorList));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Errors> handleValidationException(DataIntegrityViolationException ex) {
        List<ValidationError> errorList = List.of(new ValidationError(null, ex.getMessage()));
        return ResponseEntity.badRequest().body(new Errors(errorList));
    }

}

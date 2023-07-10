package com.msys.digitalwallet.common.exception;


import com.msys.digitalwallet.wallet.model.ApiError;
import com.msys.digitalwallet.wallet.model.ApiValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionController {
    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        String errorMessage = messageSource.getMessage("malformed.json.message", new Object[0], Locale.getDefault());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ApiError(httpStatus, errorMessage, exception);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessage = messageSource.getMessage("validation.error.message", new Object[0], Locale.getDefault());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(httpStatus, errorMessage, exception);
        List<FieldError> errors = exception.getBindingResult().getFieldErrors();
        apiError.setErrors(errors.stream().map(fieldError ->
                new ApiValidationError(fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage())
        ).collect(Collectors.toList()));
        return apiError;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException exception) {
        String errorMessage = messageSource.getMessage("validation.error.message", new Object[0], Locale.getDefault());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiError apiError = new ApiError(httpStatus, errorMessage, exception);
        Set<ConstraintViolation<?>> errors = exception.getConstraintViolations();
        apiError.setErrors(errors.stream().map(fieldError ->
                new ApiValidationError(fieldError.getInvalidValue(), fieldError.getMessage())
        ).collect(Collectors.toList()));
        return apiError;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(MissingServletRequestParameterException exception) {
        String errorMessage = messageSource.getMessage("missing.parameter.message", new Object[0], Locale.getDefault());
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ApiError(httpStatus, errorMessage, exception);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiError handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        return new ApiError(httpStatus, HttpStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase(), exception);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleRecordNotFoundException(ResourceNotFoundException exception) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        return new ApiError(httpStatus, exception.getMessage(), exception);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBusinessException(BusinessException exception) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ApiError(httpStatus, exception.getMessage(), exception);
    }

    @ExceptionHandler(ResourceAlreadyAvailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleResourceAlreadyAvailableException(ResourceAlreadyAvailableException exception) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ApiError(httpStatus, exception.getMessage(), exception);
    }

    @ExceptionHandler(com.msys.digitalwallet.common.exception.ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(ValidationException exception) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String errorMessage = messageSource.getMessage("validation.error.message", new Object[0], Locale.getDefault());
        ApiError apiError = new ApiError(httpStatus, errorMessage, exception);
        List<String> errors = exception.getMessages();
        apiError.setErrors(errors.stream().map(ApiValidationError::new
        ).collect(Collectors.toList()));
        return apiError;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleAll(Exception exception) {
        exception.printStackTrace();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        return new ApiError(httpStatus, exception);
    }
}

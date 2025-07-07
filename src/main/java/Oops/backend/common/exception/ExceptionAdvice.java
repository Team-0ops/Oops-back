package Oops.backend.common.exception;


import Oops.backend.common.response.BaseResponse;
import Oops.backend.common.status.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity<BaseResponse> validation(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return BaseResponse.onFailure(ErrorStatus.VALIDATION_ERROR, errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();

        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return BaseResponse.onFailure(ErrorStatus.VALIDATION_ERROR, errorMessage);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        e.printStackTrace();

        return BaseResponse.onFailure(ErrorStatus._NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception e) {
        log.error("Unhandled Exception: ", e);
        return BaseResponse.onFailure((ErrorStatus._INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<BaseResponse> handleGeneralException(GeneralException e) {
        e.printStackTrace();

        return BaseResponse.onFailure(e.getErrorStatus(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConversionFailedException.class})
    public ResponseEntity<BaseResponse> handleConversionFailedException(Exception e) {
        return BaseResponse.onFailure((ErrorStatus._BAD_REQUEST));
    }
}

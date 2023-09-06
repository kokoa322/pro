package com.baesullin.pro.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException e) {
        log.error("Null값이 들어올 수 없습니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.NULL_POINTER_EXCEPTION);
    }

    @ExceptionHandler(value = SignatureException.class)
    protected ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        log.error("잘못된 JWT 서명입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.WRONG_TYPE_SIGNATURE);
    }

    @ExceptionHandler(value = MalformedJwtException.class)
    protected ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException e) {
        log.error("유효하지 않은 구성의 JWT 토큰입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.WRONG_TYPE_TOKEN);
    }

    @ExceptionHandler(value = ExpiredJwtException.class)
    protected ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("만료된 JWT 토큰입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.EXPIRED_ACCESS_TOKEN);
    }

    @ExceptionHandler(value = UnsupportedJwtException.class)
    protected ResponseEntity<ErrorResponse> handleUnsupportedJwtException(UnsupportedJwtException e) {
        log.error("지원되지 않는 형식이나 구성의 JWT 토큰입니다.");
        return ErrorResponse.toResponseEntity(ErrorCode.WRONG_TYPE_TOKEN);
    }

    @ExceptionHandler(value = RestClientException.class)
    protected ResponseEntity<ErrorResponse> handleUnsupportedJwtException(RestClientException e) {
        log.error("API 로드에 실패했습니다");
        return ErrorResponse.toResponseEntity(ErrorCode.API_LOAD_FAILURE);
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(Exception ex){
        log.warn("파일 용량 초과 문제: {}",ex.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.IMAGE_SIZE_EXCESS);
    }
}

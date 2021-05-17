package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> signUpRestrictedException(SignUpRestrictedException sre, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(sre.getCode()).message(sre.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException afe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(afe.getCode()).message(afe.getErrorMessage()), HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException afe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(afe.getCode()).message(afe.getErrorMessage()), HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> updateCustomerException(UpdateCustomerException uce, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(uce.getCode()).message(uce.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> saveAddressException(SaveAddressException sae, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(sae.getCode()).message(sae.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressNotFoundException(AddressNotFoundException anfe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(anfe.getCode()).message(anfe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressNotFoundException(CategoryNotFoundException cnfe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(cnfe.getCode()).message(cnfe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> restaurantNotFoundException(RestaurantNotFoundException rnfe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(rnfe.getCode()).message(rnfe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> invalidRatingException(InvalidRatingException ire, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(ire.getCode()).message(ire.getErrorMessage()), HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> couponNotFoundException(CouponNotFoundException cnfe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(cnfe.getCode()).message(cnfe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ErrorResponse> paymentMethodNotFoundException(PaymentMethodNotFoundException pmnfe, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(
                new ErrorResponse().code(pmnfe.getCode()).message(pmnfe.getErrorMessage()), HttpStatus.NOT_FOUND
        );
    }
}

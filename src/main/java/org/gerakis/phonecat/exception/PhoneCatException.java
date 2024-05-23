package org.gerakis.phonecat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Payload information missing or inconsistent")
public class PhoneCatException extends Exception{
    public PhoneCatException(String message) {
        super(message);
    }
    public PhoneCatException(String message, Throwable origin) {super(message, origin);}
}
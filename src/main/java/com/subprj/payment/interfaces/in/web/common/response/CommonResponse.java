package com.subprj.payment.interfaces.in.web.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse<T> {
    private int status;
    private String message;
    private T data;

    public static <T> CommonResponse <T> with(HttpStatus status, String message, T data) {
        return new CommonResponse<>(status.value(), message ,data);
    }
}

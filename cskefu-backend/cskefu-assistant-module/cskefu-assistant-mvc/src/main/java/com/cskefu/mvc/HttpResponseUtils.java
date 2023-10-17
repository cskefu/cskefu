package com.cskefu.mvc;

import com.cskefu.base.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HttpResponseUtils {

    private HttpResponseUtils() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    private static <T> void response(HttpServletResponse response, HttpStatus status, Result<T> result, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON.getType());
        response.setStatus(status.value());
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Message");
        response.addHeader("Message", URLEncoder.encode(message, StandardCharsets.UTF_8));
    }

    public static void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response(response, HttpStatus.UNAUTHORIZED, Result.builder().code(HttpStatus.UNAUTHORIZED.value()).success(false).message(message).build(), message);
    }

    public static <T> void okResponse(HttpServletResponse response, T data) throws IOException {
        response(response, HttpStatus.OK, Result.builder().code(ResultEnum.OK.getCode()).data(null).success(true).build(), null);
    }

    public static void okResponse(HttpServletResponse response) throws IOException {
        okResponse(response, null);
    }
}

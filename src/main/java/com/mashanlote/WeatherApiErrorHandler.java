package com.mashanlote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashanlote.model.ApiError;
import com.mashanlote.model.exceptions.AuthorizationException;
import com.mashanlote.model.exceptions.BadRequestException;
import com.mashanlote.model.exceptions.NotFoundException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseErrorHandler;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Component
public class WeatherApiErrorHandler implements ResponseErrorHandler {

    ObjectMapper mapper;

    public WeatherApiErrorHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        ApiError apiError = mapper.readValue(response.getBody(), ApiError.class);
        if (apiError.error().code() == 1006) {
            throw new NotFoundException();
        }
        var statusCode = response.getStatusCode();
        if (statusCode == HttpStatusCode.valueOf(400)) {
            throw new BadRequestException();
        } else if (statusCode == HttpStatusCode.valueOf(401)) {
            throw new AuthenticationException();
        } else if (statusCode == HttpStatusCode.valueOf(403)) {
            throw new AuthorizationException();
        }
    }
}

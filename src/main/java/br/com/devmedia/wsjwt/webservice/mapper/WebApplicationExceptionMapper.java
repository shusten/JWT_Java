package br.com.devmedia.wsjwt.webservice.mapper;

import br.com.devmedia.wsjwt.domain.ErrorMessage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

@Override
public Response toResponse(WebApplicationException exception) {
        return Response.status(exception.getResponse().getStatus())
        .entity(ErrorMessage.builder()
        .addErro(exception.getMessage())
        .addStatusCode(exception.getResponse().getStatus())
        .addStatusMessage(exception.getResponse()
        .getStatusInfo().toString())
        .build())
        .type(MediaType.APPLICATION_JSON)
        .build();
        }

        }
package br.com.devmedia.wsjwt.webservice.filter;

import br.com.devmedia.wsjwt.webservice.exception.UnauthenticatedException;
import br.com.devmedia.wsjwt.webservice.jwt.JWTSecurityContext;
import br.com.devmedia.wsjwt.webservice.jwt.KeyGenerator;
import br.com.devmedia.wsjwt.webservice.jwt.TokenJWTUtil;
import br.com.devmedia.wsjwt.webservice.jwt.UserDetails;
import br.com.devmedia.wsjwt.webservice.resource.LoginJWTResource;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;
import java.util.List;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private KeyGenerator keyGenerator = new KeyGenerator();

    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader != null && authorizationHeader.contains("Bearer ")) {
            String token = authorizationHeader.substring("Bearer".length()).trim();

            Key key = keyGenerator.generateKey();

            if (TokenJWTUtil.tokenValido(token, key)) {
                String nome = TokenJWTUtil.recuperarNome(token, key);
                List<String> regras = TokenJWTUtil.recuperarRoles(token, key);
                UserDetails userDetails = new UserDetails(nome, regras);

                boolean secure = requestContext.getSecurityContext().isSecure();
                requestContext.setSecurityContext(new JWTSecurityContext(userDetails, secure));
                return;
            }
        } else if (acessoParaLoginNaAPI(requestContext)) {
            return;
        } else if (acessoParaMetodosDeConsulta(requestContext)) {
            return;
        }
        throw new UnauthenticatedException("Token inválido/expirado ou usuário não autenticado!");
    }

    private boolean acessoParaLoginNaAPI(ContainerRequestContext requestContext) {
        return requestContext.getUriInfo().getAbsolutePath().toString()
                .equals(uriInfo.getBaseUriBuilder().path(LoginJWTResource.class).build().toString());
    }

    private boolean acessoParaMetodosDeConsulta(ContainerRequestContext requestContext) {
        return "GET".equalsIgnoreCase(requestContext.getMethod());
    }

}
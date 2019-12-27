package br.com.devmedia.wsjwt.webservice.resource;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Path;
import br.com.devmedia.wsjwt.domain.Usuario;
import br.com.devmedia.wsjwt.service.UsuarioService;
import br.com.devmedia.wsjwt.webservice.exception.UnauthenticatedException;
import br.com.devmedia.wsjwt.webservice.jwt.JWTSecurityContext;
import br.com.devmedia.wsjwt.webservice.jwt.TokenJWTUtil;
import br.com.devmedia.wsjwt.webservice.jwt.UserDetails;

import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
@Path("/login")
public class LoginJWTResource {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response autenticarUsuario(@FormParam("login") String login,
                                      @FormParam("password") String password) {
        Usuario usuario = validarCredenciais(login, password);
        String token = TokenJWTUtil.gerarToken(usuario.getUsername(),
                usuario.recuperarRoles());

        return Response.ok().header("Authorization",
                "Bearer " + token).build();
    }

    //=============================== Atualizar Token ========================
    @POST
    @Path("refresh")
    @PermitAll
    public Response atualizarToken(@Context ContainerRequestContext requestContext) {
        JWTSecurityContext JWTSecurityContext = (JWTSecurityContext) requestContext.getSecurityContext();
        UserDetails userDetails = (UserDetails) JWTSecurityContext.getUserPrincipal();
        String token = TokenJWTUtil.gerarToken(userDetails.getName(), userDetails.getRoles());

        return Response.ok().header("Authorization", "Bearer " + token).build();
    }


    private Usuario validarCredenciais(String login, String password) {
        UsuarioService usuarioService = new UsuarioService();

        Usuario usuario = usuarioService.recuperarUsuarioComLoginESenha
                (login, password);

        if (usuario == null)
            throw new UnauthenticatedException
                    ("Usuário não autenticado: nome do usuário ou senha inválidos!");

        return usuario;
    }

}

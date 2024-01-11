package br.com.mike.service;

import br.com.mike.record.AutenticacaoRecord;
import br.com.mike.record.TokenRecord;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.springframework.http.ResponseEntity;

@RegisterRestClient(baseUri = "http://localhost:8081/seguranca")
public interface AutenticationService {

    @GET()
    @Path("/validarToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validarToken(@QueryParam("token") String token) throws Exception;

    @POST
    @Path("/criarToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response criarToken(@QueryParam("id") String id, @QueryParam("tempoToken") Integer tempoToken) throws Exception;

    @POST
    @Path("/atualizarToken")
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizarToken(@QueryParam("token") String token) throws Exception;
}

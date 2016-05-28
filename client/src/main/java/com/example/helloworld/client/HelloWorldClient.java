package com.example.helloworld.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.example.helloworld.api.Saying;

@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public interface HelloWorldClient {

    @GET
    Saying receiveHi();

    @GET
    @Path("custom")
    Saying receiveHi(@QueryParam("message") String message);

}
package com.example.helloworld.client;

import feign.Feign;
import feign.Feign.Builder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;

public class ClientFactory
{

  private final String url;

  public ClientFactory(String url)
  {
    this.url = url;
  }

  public HelloWorldClient newHelloWorldClient()
  {
    return feign().target(HelloWorldClient.class, url);
  }

  Builder feign()
  {
    return Feign.builder()
        .contract(new JAXRSContract())
        .encoder(new JacksonEncoder())
        .decoder(new JacksonDecoder());
  }

}

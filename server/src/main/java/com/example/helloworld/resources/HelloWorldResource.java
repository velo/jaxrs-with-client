package com.example.helloworld.resources;

import com.example.helloworld.api.Saying;
import com.example.helloworld.client.HelloWorldClient;

public class HelloWorldResource implements HelloWorldClient
{

  @Override
  public Saying receiveHi()
  {
    return new Saying("HI");
  }

}

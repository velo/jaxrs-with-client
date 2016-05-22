package com.example.helloworld.client;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.example.helloworld.api.Saying;

import feign.Feign.Builder;
import feign.mock.HttpMethod;
import feign.mock.MockClient;

public class ClientFactoryTest
{

  private ClientFactory factory;

  @Before
  public void setup()
  {
    MockClient mockClient = new MockClient()
        .ok(HttpMethod.GET  , "mock:///hello-world", "{\"content\":\"HowdyTest\"}");
    this.factory = new ClientFactory("mock://")
    {
      @Override
      Builder feign()
      {
        return super.feign().client(mockClient);
      }
    };
  }

  @Test
  public void test()
  {
    HelloWorldClient helloWorldClient = factory.newHelloWorldClient();
    Saying result = helloWorldClient.receiveHi();

    assertThat(result, notNullValue());
    assertThat(result.getContent(), equalTo("HowdyTest"));
  }

}

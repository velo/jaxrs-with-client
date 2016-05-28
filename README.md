# jaxrs-with-client

[![Build Status](https://travis-ci.org/velo/jaxrs-with-client.svg?branch=master)](https://travis-ci.org/velo/jaxrs-with-client?branch=master) 
[![Coverage Status](https://coveralls.io/repos/github/velo/jaxrs-with-client/badge.svg?branch=master)](https://coveralls.io/github/velo/jaxrs-with-client?branch=master) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.marvinformatics/jaxrs-with-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.marvinformatics/jaxrs-with-client/) 
[![Issues](https://img.shields.io/github/issues/velo/jaxrs-with-client.svg)](https://github.com/velo/jaxrs-with-client/issues) 
[![Forks](https://img.shields.io/github/forks/velo/jaxrs-with-client.svg)](https://github.com/velo/jaxrs-with-client/network) 
[![Stars](https://img.shields.io/github/stars/velo/jaxrs-with-client.svg)](https://github.com/velo/jaxrs-with-client/stargazers)

Zero effort API client
======================

Original post can be found https://velo.github.io/2016/05/28/Zero-effort-A-P-I-client.html[here].

Recently I start using https://github.com/Netflix/feign[Netflix feign] to write clients to access third party APIs (thank you for the hint http://carlosbecker.com/[Becker])

On Feign you describe your client as a https://docs.oracle.com/javase/tutorial/java/concepts/interface.html[java interface] and feign maps it into http requests.

With a few https://github.com/Netflix/feign#basics[lines of code] you can create a client to access github API:
```
interface GitHub {
  @RequestLine("GET /repos/{owner}/{repo}/contributors")
  List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
}

static class Contributor {
  String login;
  int contributions;
}

public static void main(String... args) {
  GitHub github = Feign.builder()
                       .decoder(new GsonDecoder())
                       .target(GitHub.class, "https://api.github.com");

  // Fetch and print a list of the contributors to this library.
  List<Contributor> contributors = github.contributors("netflix", "feign");
  for (Contributor contributor : contributors) {
    System.out.println(contributor.login + " (" + contributor.contributions + ")");
  }
}
```

After a while accessing 3rd party APIs, came the need to access my own API.  So, it was just a matter of writing an interface for my own API.

Guess what, this interface was very, very similar to my controller class.  Same annotations, similar names, same parameters felt like copy and paste.  http://velo.github.io/tag/reuse/[I hate repeting myself]!  So I decided to create one interface to define both the client and the server.


===== Example

To demonstrate what I mean, I took the http://www.dropwizard.io/0.9.2/docs/getting-started.html[Dropwizard hello world] project and tweak to create https://github.com/velo/jaxrs-with-client[feign client].  

Why Dropwizard? No reason, just wanna a simple JAX-RS application.

First things first, I extracted the interface for HelloWorldResource:
```
@Path("/hello-world")
@Produces(MediaType.APPLICATION_JSON)
public interface HelloWorldClient {

    @GET
    Saying receiveHi();

    @GET
    @Path("custom")
    Saying receiveHi(@QueryParam("message") String message);

}
```

This is pretty much the same interface I would need for a feign-client.

The main advantages of this are:

* Eliminate control JAX-RS annotations from the Resource class
```
public class HelloWorldResource implements HelloWorldClient {

    @Override
    public Saying receiveHi() {
        return new Saying("HI");
    }

    @Override
    public Saying receiveHi(String message) {
        return new Saying(message);
    }

}
```
* Compile time check on client
* Code reuse
* Java client for your API with zero effort, ensured to match the server

Now, to invoke the client is a simple https://github.com/velo/jaxrs-with-client/blob/master/server/src/test/java/com/example/helloworld/IntegrationTest.java#L53[code like this]:
```
  HelloWorldClient client = Feign.builder().contract(new JAXRSContract())
    .encoder(new JacksonEncoder())
    .decoder(new JacksonDecoder())
    .target(HelloWorldClient.class, url);
  Saying saying = client.receiveHi("Howdy mate!");
```

I also decided to move the client side of things into a https://github.com/velo/jaxrs-with-client/tree/master/client[new project].  Now I can publish a java client to anyone interested on it.

This project also exposes a https://github.com/velo/jaxrs-with-client/blob/master/client/src/main/java/com/example/helloworld/client/ClientFactory.java[ClientFactory].  This factory 'knows' how to create all clients for my API.  A third-party that decides to use my client doesn't need to know about feign, just about my client API.

This is how I use the client on integration tests:
```
  ClientFactory factory = new ClientFactory("http://localhost:" + RULE.getLocalPort());
  Saying saying = factory.newHelloWorldClient().receiveHi("Howdy mate!");
  assertThat(saying.getContent()).isEqualTo("Howdy mate!");
```

To sumarize:

* extract interface
* move interface + DTOs to client project
* add client dependency on server
* created a factory to hide feign "complexity" (optional)

Hope that is useful to people out there. Till next time!

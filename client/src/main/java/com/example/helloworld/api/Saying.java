package com.example.helloworld.api;

public class Saying
{

  private String content;

  public Saying()
  {
    super();
  }

  public Saying(String content)
  {
    this.setContent(content);
  }

  public String getContent()
  {
    return content;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

}

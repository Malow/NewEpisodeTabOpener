package com.github.malow.newepisodetabopener.apirequests;

public class LoginRequest
{
  public String apikey;
  public String userkey;
  public String username;

  public LoginRequest(String apikey, String userkey, String username)
  {
    this.apikey = apikey;
    this.userkey = userkey;
    this.username = username;
  }
}

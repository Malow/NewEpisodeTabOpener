package com.github.malow.newepisodetabopener.apiresponses;

import java.util.List;

public class FindResponse
{
  public static class Data
  {
    public String name;
    public int id;
  }

  public List<Data> movie_results;
  public List<Data> person_results;
  public List<Data> tv_results;
  public List<Data> tv_episode_results;
  public List<Data> tv_season_results;
}

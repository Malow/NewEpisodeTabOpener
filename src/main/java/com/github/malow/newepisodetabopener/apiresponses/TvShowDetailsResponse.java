package com.github.malow.newepisodetabopener.apiresponses;

public class TvShowDetailsResponse
{
  public static class LastEpisodeToAir
  {
    public String air_date;
    public int season_number;
    public int episode_number;
  }

  public int id;
  public LastEpisodeToAir last_episode_to_air;
}

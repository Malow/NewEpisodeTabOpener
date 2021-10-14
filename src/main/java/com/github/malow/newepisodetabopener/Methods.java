package com.github.malow.newepisodetabopener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.confighandler.ConfigHandler;
import com.github.malow.newepisodetabopener.apiresponses.FindResponse;
import com.github.malow.newepisodetabopener.apiresponses.TvShowDetailsResponse;
import com.mashape.unirest.http.Unirest;

public class Methods
{
  private static final String CONFIG_FILE_PATH = "newEpisodeTapOpenerConfig.cfg";
  private static final String API_BASE_URL = "https://api.themoviedb.org/3";

  public static void addSeries(String imdbId) throws Exception
  {
    TvShow tvShow = new TvShow(imdbId);
    NewEpisodeTabOpenerConfig config = ConfigHandler.loadConfig(CONFIG_FILE_PATH, NewEpisodeTabOpenerConfig.class);
    if (config.watchedTvShows.contains(tvShow))
    {
      throw new Exception("Already watching that TvShow");
    }

    String responseJson = Unirest.get(API_BASE_URL + "/find/" + imdbId)
        .header("Authorization", "Bearer " + config.apiKey)
        .queryString("external_source", "imdb_id")
        .asString().getBody().toString();
    try
    {
      FindResponse response = GsonSingleton.fromJson(responseJson, FindResponse.class);
      if (response == null || response.tv_results == null || response.tv_results.size() != 1)
      {
        throw new Exception("Couldn't find the show, or found multiple results!");
      }
      tvShow.name = response.tv_results.get(0).name;
      tvShow.theMovieDbId = response.tv_results.get(0).id;
      tvShow.lastFoundEpisode = getLatestEpisode(tvShow.theMovieDbId);
      config.watchedTvShows.add(tvShow);
      ConfigHandler.saveConfig(CONFIG_FILE_PATH, config);
      MaloWLogger.info("Successfully added " + tvShow.name + " to watched TvShows");
    }
    catch (Exception e)
    {
      MaloWLogger.error("Error, responseJson: " + responseJson, e);
    }
  }

  public static Episode getLatestEpisode(int theMovieDbId) throws Exception
  {
    NewEpisodeTabOpenerConfig config = ConfigHandler.loadConfig(CONFIG_FILE_PATH, NewEpisodeTabOpenerConfig.class);
    String responseJson = Unirest.get(API_BASE_URL + "/tv/" + theMovieDbId)
        .header("Authorization", "Bearer " + config.apiKey)
        .asString().getBody().toString();
    try
    {
      TvShowDetailsResponse response = GsonSingleton.fromJson(responseJson, TvShowDetailsResponse.class);
      return new Episode(response.last_episode_to_air.season_number,
          response.last_episode_to_air.episode_number,
          LocalDate.parse(response.last_episode_to_air.air_date, DateTimeFormatter.ISO_DATE));
    }
    catch (Exception e)
    {
      MaloWLogger.error("Error, responseJson: " + responseJson, e);
    }
    return null;
  }
}

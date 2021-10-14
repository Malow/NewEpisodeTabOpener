package com.github.malow.newepisodetabopener;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.malow.malowlib.GsonSingleton;
import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.confighandler.ConfigHandler;
import com.github.malow.newepisodetabopener.apiresponses.FindResponse;
import com.mashape.unirest.http.Unirest;

// To transform a cfg file with tvdbIds into one with theMovieDbIds
// Before running manually replace all "theTvDbId" with "theMovieDbId", and remove all the lines before "watchedTvShows" except "apiKey", and in "apiKey" put your TheMovieDb API v4 key
public class MigrationTool
{
  private static final String CONFIG_FILE_PATH = "newEpisodeTapOpenerConfig.cfg";
  private static final String API_BASE_URL = "https://api.themoviedb.org/3";

  public static void migrateFromTheTvDbToTheMovieDb() throws Exception
  {
    String string = Files.readString(Paths.get(CONFIG_FILE_PATH));
    Matcher movieDbStringMatcher = Pattern.compile("\"theMovieDbId\":\\ \"([0-9]*)\"").matcher(string);
    while (movieDbStringMatcher.find())
    {
      String id = movieDbStringMatcher.group(1);
      string = string.replaceFirst("\"theMovieDbId\":\\ \"([0-9]*)\"", "\"theMovieDbId\": " + id);
    }

    Matcher airedMatcher = Pattern
        .compile("\"aired\":\\ \\{\\n\\ *\"year\":\\ ([0-9]*),\\n\\ *\"month\":\\ ([0-9]*),\\n\\ *\"day\":\\ ([0-9]*)\\n\\ *\\}")
        .matcher(string);
    while (airedMatcher.find())
    {
      String year = airedMatcher.group(1);
      String month = airedMatcher.group(2);
      if (month.length() == 1)
      {
        month = "0" + month;
      }
      String day = airedMatcher.group(3);
      if (day.length() == 1)
      {
        day = "0" + day;
      }
      string = string.replaceFirst(
          "\"aired\":\\ \\{\\n\\ *\"year\":\\ ([0-9]*),\\n\\ *\"month\":\\ ([0-9]*),\\n\\ *\"day\":\\ ([0-9]*)\\n\\ *\\}",
          "\"aired\": \"" + year + "-" + month + "-" + day + "\"");
    }

    Files.write(Paths.get(CONFIG_FILE_PATH), string.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.CREATE);

    getTheMovieDbIds();
  }

  // Uses the imdbId to replace the existing theMovieDbIds for all entries in the config file
  public static void getTheMovieDbIds() throws Exception
  {
    NewEpisodeTabOpenerConfig config = ConfigHandler.loadConfig(CONFIG_FILE_PATH, NewEpisodeTabOpenerConfig.class);
    for (TvShow tvShow : config.watchedTvShows)
    {
      String responseJson = Unirest.get(API_BASE_URL + "/find/" + tvShow.imdbId)
          .header("Authorization", "Bearer " + config.apiKey)
          .queryString("external_source", "imdb_id")
          .asString().getBody().toString();
      FindResponse response = GsonSingleton.fromJson(responseJson, FindResponse.class);
      if (response == null || response.tv_results == null || response.tv_results.size() != 1)
      {
        throw new Exception("Couldn't find the show, or found multiple results!");
      }
      tvShow.theMovieDbId = response.tv_results.get(0).id;
      ConfigHandler.saveConfig(CONFIG_FILE_PATH, config);
      MaloWLogger.info("Successfully updated " + tvShow.name);
    }
  }
}

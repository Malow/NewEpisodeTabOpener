package com.github.malow.newepisodetabopener;

import java.awt.Desktop;
import java.net.URI;
import java.time.LocalDateTime;

import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.confighandler.ConfigHandler;
import com.github.malow.malowlib.malowprocess.MaloWProcess;

public class TabOpenerProcess extends MaloWProcess
{
  private static final int HOUR_IN_MILLISECONDS = 1000 * 60 * 60; // 1 hour
  private static final String CONFIG_FILE_PATH = "newEpisodeTapOpenerConfig.cfg";
  private static LocalDateTime lastErrorTabOpened = null;

  @Override
  public void life()
  {
    while (this.stayAlive)
    {
      try
      {
        NewEpisodeTabOpenerConfig config = ConfigHandler.loadConfig(CONFIG_FILE_PATH, NewEpisodeTabOpenerConfig.class);
        for (TvShow tvShow : config.watchedTvShows)
        {
          try
          {
            Episode latestEp = Methods.getLatestEpisode(tvShow.theMovieDbId);
            if (latestEp.isNewerThan(tvShow.lastFoundEpisode))
            {
              tvShow.lastFoundEpisode = latestEp;
              String episodeSearch = tvShow.imdbId + " " + latestEp.toString();
              episodeSearch = episodeSearch.replaceAll(" ", "+");
              String url = "https://rarbg.to/torrents.php?search=" + episodeSearch + "&order=size&by=DESC";
              MaloWLogger.info("Found new episode: " + tvShow.name + " " + latestEp.toString() + ": " + url);
              Desktop.getDesktop().browse(new URI(url));
            }
          }
          catch (Exception e)
          {
            this.handleError(e, tvShow.name);
          }
          Thread.sleep(1000);
        }
        ConfigHandler.saveConfig(CONFIG_FILE_PATH, config);
        MaloWLogger.info("Finished looking for new episodes");
        Thread.sleep(4 * HOUR_IN_MILLISECONDS);
      }
      catch (Exception e)
      {
        try
        {
          this.handleError(e, "");
        }
        catch (Exception e1)
        {
        }
        return;
      }
    }
  }

  public void handleError(Exception e, String showName) throws Exception
  {
    MaloWLogger.error("Error when trying to run for TV show: " + showName, e);
    if (lastErrorTabOpened == null || LocalDateTime.now().isAfter(lastErrorTabOpened.plusDays(1)))
    {
      Desktop.getDesktop().browse(new URI("https://ErrorInNewEpisodeTabOpener"));
      lastErrorTabOpened = LocalDateTime.now();
    }
  }
}

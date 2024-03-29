package com.github.malow.newepisodetabopener;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.github.malow.malowlib.MaloWLogger;
import com.github.malow.malowlib.malowcliapplication.Command;
import com.github.malow.malowlib.malowcliapplication.MaloWCliApplication;
import com.github.malow.malowlib.malowcliapplication.Parameter;
import com.mashape.unirest.http.Unirest;

public class NewEpisodeTabOpener extends MaloWCliApplication
{
  public static void main(String[] args)
  {
    MaloWLogger.setLoggingThresholdToInfo();
    //MigrationTool.migrateFromTheTvDbToTheMovieDb();

    NewEpisodeTabOpener program = new NewEpisodeTabOpener();
    program.run();
  }

  private CloseableHttpClient httpClient = HttpClients.createDefault();
  private TabOpenerProcess tabOpener = new TabOpenerProcess();

  public NewEpisodeTabOpener()
  {
    Unirest.setHttpClient(this.httpClient);
    this.tabOpener.start();
  }

  @Command(description = "Adds a TvShow to watch for opening of tabs for.")
  public void add(@Parameter(description = "The IMDB id (example: 'tt7203552')", flag = "-id") String id) throws Exception
  {
    Methods.addSeries(id);
  }

  @Command(description = "Closes the application")
  @Override
  public void exit()
  {
    this.tabOpener.closeAndWaitForCompletion();
    super.exit();
  }
}

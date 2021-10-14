package com.github.malow.newepisodetabopener;

public class TvShow
{
  public String imdbId;
  public String name;
  public int theMovieDbId;
  public Episode lastFoundEpisode;

  public TvShow(String imdbId)
  {
    this.imdbId = imdbId;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.imdbId == null ? 0 : this.imdbId.hashCode());
    result = prime * result + (this.lastFoundEpisode == null ? 0 : this.lastFoundEpisode.hashCode());
    result = prime * result + (this.name == null ? 0 : this.name.hashCode());
    result = prime * result + this.theMovieDbId;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if ((obj == null) || (this.getClass() != obj.getClass()))
    {
      return false;
    }
    TvShow other = (TvShow) obj;
    if (this.imdbId == null)
    {
      if (other.imdbId != null)
      {
        return false;
      }
    }
    else if (!this.imdbId.equals(other.imdbId))
    {
      return false;
    }
    if (this.lastFoundEpisode == null)
    {
      if (other.lastFoundEpisode != null)
      {
        return false;
      }
    }
    else if (!this.lastFoundEpisode.equals(other.lastFoundEpisode))
    {
      return false;
    }
    if (this.name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!this.name.equals(other.name))
    {
      return false;
    }
    if (this.theMovieDbId != other.theMovieDbId)
    {
      return false;
    }
    return true;
  }
}

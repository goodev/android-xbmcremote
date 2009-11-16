package org.xbmc.model;

public interface ICoverArt {
	public String getArtFolder();
	public long getCrc();
	public int getFallbackCrc();
	
	public int getId();
	public String getName();
}

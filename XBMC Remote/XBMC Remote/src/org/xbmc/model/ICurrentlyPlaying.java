package org.xbmc.model;

import java.io.Serializable;

import org.xbmc.backend.httpapi.type.MediaType;

public interface ICurrentlyPlaying extends Serializable {
	
	/**
	 * Describes the current play status
	 */
	public enum PlayStatus {
		Stopped,
		Paused,
		Playing;
		public static PlayStatus parse(String response) {
			if (response.contains("PlayStatus:Paused") || response.equals("Paused")) {
				return PlayStatus.Paused;
			} else if (response.contains("PlayStatus:Playing") || response.equals("Playing")) {
				return PlayStatus.Playing;
			} else {
				return PlayStatus.Stopped;
			}
		}
	}
	public PlayStatus getPlayStatus();
	public MediaType getType();
	public boolean isPlaying();
	
	public String getFilename();
	public String getTitle();
	
	public int getTime();
	public int getDuration();
	public float getPercentage();
	
	public String getArtist();
	public String getAlbum();
	
}
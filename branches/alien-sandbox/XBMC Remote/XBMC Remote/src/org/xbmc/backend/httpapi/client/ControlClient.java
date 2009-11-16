/*
 *      Copyright (C) 2005-2009 Team XBMC
 *      http://xbmc.org
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XBMC Remote; see the file license.  If not, write to
 *  the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *  http://www.gnu.org/copyleft/gpl.html
 *
 */

package org.xbmc.backend.httpapi.client;

import java.util.HashMap;

import org.xbmc.backend.exceptions.BackendException;
import org.xbmc.backend.httpapi.Connection;
import org.xbmc.backend.httpapi.info.GuiActions;
import org.xbmc.backend.httpapi.type.MediaType;
import org.xbmc.backend.httpapi.type.SeekType;
import org.xbmc.model.ICurrentlyPlaying;
import org.xbmc.model.ICurrentlyPlaying.PlayStatus;

/**
 * The ControlClient class takes care of everything related to controlling
 * XBMC. These are essentially play controls, navigation controls other actions
 * the user may wants to execute. It equally reads the information instead of
 * setting it.
 * 
 * @author Team XBMC
 */
public class ControlClient extends Connection{

	public ControlClient(String host, int port, String username,
			String password, int timeout) throws BackendException {
		super(host, port, username, password, timeout);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Adds a file or folder (<code>fileOrFolder</code> is either a file or a folder) to the current playlist.
	 * @param fileOrFolder
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean addToPlaylist(String fileOrFolder) throws BackendException {
		return getBoolean("AddToPlayList", fileOrFolder);
	}
	
	/**
	 * Starts playing the media file <code>filename</code> .
	 * @param filename File to play
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean playFile(String filename) throws BackendException {
		return getBoolean("PlayFile", filename);
	}
	
	/**
	 * Starts playing/showing the next media/image in the current playlist or,
	 * if currently showing a slideshow, the slideshow playlist. 
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean playNext() throws BackendException {
		return getBoolean("PlayNext");
	}

	/**
	 * Starts playing/showing the previous media/image in the current playlist
	 * or, if currently showing a slidshow, the slideshow playlist.
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean playPrevious() throws BackendException {
		return getBoolean("PlayPrev");
	}
	
	/**
	 * Pauses the currently playing media. 
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean pause() throws BackendException {
		return getBoolean("Pause");
	}
	
	/**
	 * Stops the currently playing media. 
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean stop() throws BackendException {
		return getBoolean("Stop");
	}
	
	/**
	 * Sets the volume as a percentage of the maximum possible.
	 * @param volume New volume (0-100)
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean setVolume(int volume) throws BackendException {
		return getBoolean("SetVolume", String.valueOf(volume));
	}
	
	/**
	 * Seeks to a position. If type is
	 * <ul>
	 * 	<li><code>absolute</code> - Sets the playing position of the currently 
	 *		playing media as a percentage of the media�s length.</li>
	 *  <li><code>relative</code> - Adds/Subtracts the current percentage on to
	 *		the current position in the song</li>
	 * </ul> 
	 * @param type     Seek type, relative or absolute
	 * @param progress Progress
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean seek(SeekType type, int progress) throws BackendException {
		if (type.compareTo(SeekType.absolute) == 0)
			return getBoolean("SeekPercentage", String.valueOf(progress));
		else
			return getBoolean("SeekPercentageRelative", String.valueOf(progress));
	}
	
	/**
	 * Toggles the sound on/off.
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean mute() throws BackendException {
		return getBoolean("Mute");
	}
	
	/**
	 * Retrieves the current playing position of the currently playing media as
	 * a percentage of the media's length. 
	 * @return Percentage (0-100)
	 * @throws BackendException 
	 */
	public int getPercentage() throws BackendException {
		return getInt("GetPercentage");
	}
	
	/**
	 * Retrieves the current volume setting as a percentage of the maximum 
	 * possible value.
	 * @return Volume (0-100)
	 * @throws BackendException 
	 */
	public int getVolume() throws BackendException {
		return getInt("GetVolume");
	}
	
	/**
	 * Navigates... UP!
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean navUp() throws BackendException {
		return getBoolean("Action", String.valueOf(GuiActions.ACTION_MOVE_UP));
	}

	/**
	 * Navigates... DOWN!
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean navDown() throws BackendException {
		return getBoolean("Action", String.valueOf(GuiActions.ACTION_MOVE_DOWN));
	}
	
	/**
	 * Navigates... LEFT!
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean navLeft() throws BackendException {
		return getBoolean("Action", String.valueOf(GuiActions.ACTION_MOVE_LEFT));
	}
	
	/**
	 * Navigates... RIGHT!
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean navRight() throws BackendException {
		return getBoolean("Action", String.valueOf(GuiActions.ACTION_MOVE_RIGHT));
	}
	
	/**
	 * Selects current item.
	 * @return true on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean navSelect() throws BackendException {
		return getBoolean("Action", String.valueOf(GuiActions.ACTION_SELECT_ITEM));
	}
	
	/**
	 * Takes either "video" or "music" as a parameter to begin updating the 
	 * corresponding database. 
	 * 
	 * TODO For "video" you can additionally specify a specific path to be scanned.
	 * 
	 * @param mediaType Either <code>video</code> or <code>music</code>.
	 * @return True on success, false otherwise.
	 * @throws BackendException 
	 */
	public boolean updateLibrary(String mediaType) throws BackendException {
		return getBoolean("ExecBuiltin", "UpdateLibrary(" + mediaType + ")");
	}

	/**
	 * Returns current play state
	 * @return
	 * @throws BackendException 
	 */
	public PlayStatus getPlayState() throws BackendException {
		return PlayStatus.parse(getString("GetCurrentlyPlaying"));
	}
	
	/**
	 * Returns state and type of the media currently playing.
	 * @return
	 * @throws BackendException 
	 */
	public ICurrentlyPlaying getCurrentlyPlaying() throws BackendException {
		final HashMap<String, String> map = getPairs("GetCurrentlyPlaying");
		final ICurrentlyPlaying nothingPlaying = new ICurrentlyPlaying() {
			private static final long serialVersionUID = -1554068775915058884L;
			public boolean isPlaying() { return false; }
			public MediaType getType() { return null; }
			public String getTitle() { return ""; }
			public int getTime() { return 0; }
			public PlayStatus getPlayStatus() { return PlayStatus.Stopped; }
			public float getPercentage() { return 0; }
			public String getFilename() { return ""; }
			public int getDuration() { return 0; }
			public String getArtist() { return ""; }
			public String getAlbum() { return ""; }
		};
		if (map.get("Filename").contains("Nothing Playing")) {
			return nothingPlaying;
		} else {
			final MediaType type = map.get("Type").contains("Audio") ? MediaType.music : MediaType.video;
			switch (type) {
				case music:
					return MusicClient.getCurrentlyPlaying(map);
				case video:
					return VideoClient.getCurrentlyPlaying(map);
				default:
					return nothingPlaying;
			}
		}
	}
	
	
	

	
	
	/**
	 * Contains info about which media type is playing and if it's playing.
	 * @deprecated 
	 */
	public class CurrentlyPlaying {
		public MediaType mediaType;
		public PlayStatus playStatus;
		
		public CurrentlyPlaying(MediaType mediaType, PlayStatus playState) {
			this.mediaType = mediaType;
			this.playStatus = playState;
		}
		public boolean isPlaying() {
			return playStatus.equals(PlayStatus.Playing);
		}
	}
	
	
	

	
	
	public boolean isConnected() {
		return isConnected();
	}
}

package org.xbmc.backend.httpapi.client;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.xbmc.backend.exceptions.BackendException;
import org.xbmc.backend.httpapi.Connection;
import org.xbmc.backend.httpapi.info.GuiSettings;
import org.xbmc.backend.httpapi.type.DirectoryMask;
import org.xbmc.backend.httpapi.type.MediaType;
import org.xbmc.model.MediaLocation;

/**
 * The InfoClient basically takes care of everything else not covered by the
 * other clients (music, video and control). That means its tasks are bound to
 * system related stuff like directory listing and so on. 
 * 
 * @author Team XBMC
 */
public class InfoClient extends Connection{
	
	public InfoClient(String host, int port, String username, String password,
			int timeout) throws BackendException {
		super(host, port, username, password, timeout);
	}

	
	
	/**
	 * Returns the contents of a directory
	 * @param path    Path to the directory
	 * @param mask    Mask to filter
	 * @param offset  Offset (0 for none)
	 * @param limit   Limit (0 for none)
	 * @return
	 * @throws BackendException 
	 */
	public ArrayList<MediaLocation> getDirectory(String path, DirectoryMask mask, int offset, int limit) throws BackendException {
		final ArrayList<String> result = getArray("GetDirectory", 
			path + ";" +
			(mask != null ? mask.toString() : " ") + ";" + 
			(offset > 0 ? offset : " ") + ";" +
			(limit > 0 ? limit : " ")
		);
		final ArrayList<MediaLocation> files = new ArrayList<MediaLocation>();
		for (String file : result) {
			files.add(new MediaLocation(file));
		}
		return files;
	}
	
	/**
	 * Returns all the contents of a directory
	 * @param path    Path to the directory
	 * @return
	 * @throws BackendException 
	 */
	public ArrayList<MediaLocation> getDirectory(String path) throws BackendException {
		return getDirectory(path, null, 0, 0);
	}

	
	/**
	 * Returns all defined shares of a media type
	 * @param type Media type
	 * @return
	 * @throws BackendException 
	 */
	public ArrayList<MediaLocation> getShares(MediaType type) throws BackendException {
		final ArrayList<String> result = getArray("GetShares", type.toString());
		final ArrayList<MediaLocation> shares = new ArrayList<MediaLocation>();
		for (String share : result) {
			shares.add(new MediaLocation(share));
		}
		return shares;
	}
	
	public String getCurrentlyPlayingThumbURI() throws MalformedURLException, URISyntaxException, BackendException {
		ArrayList<String> array = getArray("GetCurrentlyPlaying", "");
		for (String s : array) {
			if (s.startsWith("Thumb")) {
				return generateQuery("FileDownload", s.substring(6));
			}
		}
		return null;
	}
	
	/**
	 * Returns any system info variable, see {@link org.xbmc.backend.httpapi.info.SystemInfo}
	 * @param field Field to return
	 * @return
	 * @throws BackendException 
	 */
	public String getSystemInfo(int field) throws BackendException {
		return getString("GetSystemInfo", String.valueOf(field));
	}
	
	/**
	 * Returns a boolean GUI setting
	 * @param field
	 * @return
	 * @throws BackendException 
	 */
	public boolean getGuiSettingBool(int field) throws BackendException {
		return getBoolean("GetGuiSetting", GuiSettings.MusicLibrary.getType(field) + ";" + GuiSettings.MusicLibrary.getName(field));
	}

	/**
	 * Returns an integer GUI setting
	 * @param field
	 * @return
	 * @throws BackendException 
	 */
	public int getGuiSettingInt(int field) throws BackendException {
		return getInt("GetGuiSetting", GuiSettings.MusicLibrary.getType(field) + ";" + GuiSettings.MusicLibrary.getName(field));
	}
	
	/**
	 * Returns any music info variable see {@link org.xbmc.backend.httpapi.info.http.info.MusicInfo}
	 * @param field Field to return
	 * @return
	 * @throws BackendException 
	 */
	public String getMusicInfo(int field) throws BackendException {
		return getString("GetMusicLabel", String.valueOf(field));
	}

	/**
	 * Returns any video info variable see {@link org.xbmc.backend.httpapi.info.http.info.VideoInfo}
	 * @param field Field to return
	 * @return
	 * @throws BackendException 
	 */
	public String getVideoInfo(int field) throws BackendException {
		return getString("GetVideoLabel", String.valueOf(field));
	}
}
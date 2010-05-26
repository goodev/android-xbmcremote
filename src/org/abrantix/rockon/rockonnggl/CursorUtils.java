package org.abrantix.rockon.rockonnggl;

import java.util.ArrayList;

import org.xbmc.android.util.ClientFactory;
import org.xbmc.api.business.INotifiableManager;
import org.xbmc.api.data.IMusicClient;
import org.xbmc.api.object.Album;
import org.xbmc.api.type.SortType;
import org.xbmc.httpapi.WifiStateException;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.MediaStore;

class CursorUtils {

	/**
	 * 
	 * @param playlistId
	 * @return
	 */
	Cursor getAlbumListFromPlaylist(INotifiableManager manager, Context context) {
		
		final String[] columns = { 
			MediaStore.Audio.Albums._ID, 
			MediaStore.Audio.Albums.ALBUM_KEY, 
			MediaStore.Audio.Albums.ALBUM, 
			MediaStore.Audio.Albums.ARTIST, 
			MediaStore.Audio.Albums.ALBUM_ART, 
			MediaStore.Audio.Albums.LAST_YEAR 
		};
		MatrixCursor cursor = new MatrixCursor(columns);
		try {
			IMusicClient mc = ClientFactory.getMusicClient(manager, context);
			ArrayList<Album> albums = mc.getAlbums(manager, SortType.ARTIST, SortType.ORDER_ASC);
			for (Album album : albums) {
				final Object[] row = {
						album.id,
						String.valueOf(album.getCrc()),
						album.name,
						album.artist,
						null,
						null
				};
				cursor.addRow(row);
			}
			
		} catch (WifiStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return cursor;
	}
}

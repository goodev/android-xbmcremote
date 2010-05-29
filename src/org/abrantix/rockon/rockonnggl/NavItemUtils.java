package org.abrantix.rockon.rockonnggl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.xbmc.android.util.Crc32;
import org.xbmc.android.util.ImportUtilities;
import org.xbmc.api.type.MediaType;
import org.xbmc.api.type.ThumbSize;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.StaleDataException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.provider.MediaStore;
import android.util.Log;

class NavItemUtils {
	private final static String TAG = "AlbumNavItemUtils";

	private String path;
	private File albumCoverFile;
	private Paint labelBgPaint;
	private Paint labelAlbumPaint;
	private Paint labelArtistPaint;
	private Paint labelAlbumBoringPaint;
	private Paint labelArtistBoringPaint;
	
	private static final byte[] BUFFER = new byte[4 * ThumbSize.getPixel(ThumbSize.MEDIUM) * ThumbSize.getPixel(ThumbSize.MEDIUM)];

	protected NavItemUtils(int width, int height, Context ctx) {
		new Canvas();
		new RectF(0, 0, width, height / 4);

		labelBgPaint = new Paint();
		labelBgPaint.setColor(Color.parseColor("#00000000"));
		labelBgPaint.setAntiAlias(true);
		labelBgPaint.setStyle(Style.FILL_AND_STROKE);
		labelBgPaint.setStrokeWidth(0); // 0 = hairline

		labelAlbumPaint = new Paint();
		labelAlbumPaint.setColor(Color.parseColor("#ffffffff"));
		labelAlbumPaint.setStrokeWidth(1.33f);
		labelAlbumPaint.setAntiAlias(true);
		labelAlbumPaint.setSubpixelText(true);
		labelAlbumPaint.setStyle(Style.FILL_AND_STROKE);
		labelAlbumPaint.setTextAlign(Align.CENTER);

		labelArtistPaint = new Paint();
		labelArtistPaint.setColor(Color.parseColor("#ffaaaaaa"));
		labelArtistPaint.setStrokeWidth(1.1f);
		labelArtistPaint.setAntiAlias(true);
		labelArtistPaint.setSubpixelText(true);
		labelArtistPaint.setStyle(Style.FILL_AND_STROKE);
		labelArtistPaint.setTextAlign(Align.CENTER);

		labelAlbumBoringPaint = new Paint();
		labelAlbumBoringPaint.setColor(Color.parseColor("#ffaaaaaa"));
		labelAlbumBoringPaint.setStrokeWidth(1.33f);
		labelAlbumBoringPaint.setAntiAlias(true);
		labelAlbumBoringPaint.setSubpixelText(true);
		labelAlbumBoringPaint.setStyle(Style.FILL_AND_STROKE);
		labelAlbumBoringPaint.setTextAlign(Align.LEFT);

		labelArtistBoringPaint = new Paint();
		labelArtistBoringPaint.setColor(Color.parseColor("#ffffffff"));
		labelArtistBoringPaint.setStrokeWidth(1.1f);
		labelArtistBoringPaint.setAntiAlias(true);
		labelArtistBoringPaint.setSubpixelText(true);
		labelArtistBoringPaint.setStyle(Style.FILL_AND_STROKE);
		labelArtistBoringPaint.setTextAlign(Align.LEFT);
	}

	/**
	 * fill album bitmap
	 * 
	 * @param bitmap
	 * @param position
	 * @return
	 */
	boolean fillAlbumBitmap(NavItem albumNavItem, int width, int height, byte[] colorComponent, int theme) {
		try {

			final File f = ImportUtilities.getCacheFile(MediaType.getArtFolder(MediaType.MUSIC), ThumbSize.MEDIUM, Crc32.formatAsHexLowerCase(Long.parseLong(albumNavItem.albumKey)));
			albumNavItem.cover = BitmapFactory.decodeFile(f.getAbsolutePath());
			
			return albumNavItem.cover != null;
			
//			final FileInputStream is = ImportUtilities.getCacheFileInputStream(MediaType.getArtFolder(MediaType.MUSIC), ThumbSize.MEDIUM, Crc32.formatAsHexLowerCase(Long.parseLong(albumNavItem.albumKey)));
//			if (is != null) {
//				is.read(BUFFER, 0, BUFFER.length);
//				albumNavItem.cover.copyPixelsFromBuffer(ByteBuffer.wrap(BUFFER)); 
//				return true;
//			} else {
//				Log.i(TAG, "Cannot find cover " + albumNavItem.albumKey);
//			}

//			/** Get the path to the album art */
//			switch (theme) {
//			case Constants.THEME_NORMAL:
//				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(albumNavItem.albumId);
//				break;
//			case Constants.THEME_HALFTONE:
//				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(albumNavItem.albumId) + Constants.THEME_HALF_TONE_FILE_EXT;
//				break;
//			case Constants.THEME_EARTHQUAKE:
//				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(albumNavItem.albumId) + Constants.THEME_EARTHQUAKE_FILE_EXT;
//				break;
//			default:
//				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(albumNavItem.albumId);
//				break;
//			}
//
//			/** Access the file */
//			albumCoverFile = new File(path);
//			if (albumCoverFile.exists() && albumCoverFile.length() > 0) {
//			} else {
//				// Log.i(TAG, " - album cover bmp file has a problem "+path);
//				return false;
//			}
//
//			/** Read File and fill bitmap */
//			try {
//				FileInputStream albumCoverFileInputStream = new FileInputStream(albumCoverFile);
//				albumCoverFileInputStream.read(colorComponent, 0, colorComponent.length);
//				albumNavItem.cover.copyPixelsFromBuffer(ByteBuffer.wrap(colorComponent));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//				return false;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return false;
//			}
//
//			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * fill album bitmap
	 * 
	 * @param bitmap
	 * @param position
	 * @return
	 */
	boolean fillAlbumUnknownBitmap(NavItem albumNavItem, Resources res, int width, int height, byte[] colorComponent, int theme) {
		try {
			/** Sanity check */
			if (albumNavItem.cover.getWidth() != width || albumNavItem.cover.getHeight() != height) {
				Log.i(TAG, " - reading pixels from file failed");
				return false;
			}

			/** Get the path to the album art */
			switch (theme) {
			case Constants.THEME_NORMAL:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + Constants.ROCKON_UNKNOWN_ART_FILENAME;
				break;
			case Constants.THEME_HALFTONE:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + Constants.ROCKON_UNKNOWN_ART_FILENAME + Constants.THEME_HALF_TONE_FILE_EXT;
				break;
			case Constants.THEME_EARTHQUAKE:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + Constants.ROCKON_UNKNOWN_ART_FILENAME + Constants.THEME_EARTHQUAKE_FILE_EXT;
				break;
			default:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + Constants.ROCKON_UNKNOWN_ART_FILENAME;
				break;
			}

			/** Access the file */
			albumCoverFile = new File(path);
			if (albumCoverFile.exists() && albumCoverFile.length() > 0) {
			} else {
				// Log.i(TAG, " - album cover bmp file has a problem "+path);
				// AlbumArtUtils.saveSmallUnknownAlbumCoverInSdCard(res,
				// colorComponent, path, theme);
				// if (!albumCoverFile.exists() || !(albumCoverFile.length() >
				// 0))
				return false;
			}

			/** Read File and fill bitmap */
			try {
				FileInputStream albumCoverFileInputStream = new FileInputStream(albumCoverFile);
				albumCoverFileInputStream.read(colorComponent, 0, colorComponent.length);
				albumNavItem.cover.copyPixelsFromBuffer(ByteBuffer.wrap(colorComponent));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * fillAlbumInfo
	 * 
	 * @param albumNavItem
	 * @param position
	 * @return
	 */
	boolean fillAlbumInfo(Cursor albumCursor, NavItem albumNavItem, int position) {

		/** Sanity check */
		if (position < 0 || position >= albumCursor.getCount()) {
			Log.i(TAG, " - reading pixels from file failed");
			return false;
		}

		try {
			/** move cursor */
			albumCursor.moveToPosition(position);

			/** get album info */
			albumNavItem.artistName = albumCursor.getString(albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
			albumNavItem.albumName = albumCursor.getString(albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
			albumNavItem.albumKey = albumCursor.getString(albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_KEY));
			albumNavItem.albumId = String.valueOf(albumCursor.getInt(albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));

			// Log.i(TAG,
			// albumNavItem.albumId+" - "+albumNavItem.artistName+" "+albumNavItem.albumName);

			return true;
		} catch (CursorIndexOutOfBoundsException e) {
			e.printStackTrace();
			return false;
		} catch (StaleDataException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 
	 * @param artistCursor
	 * @param navItem
	 * @param position
	 * @return
	 */
	boolean fillArtistInfo(Cursor artistCursor, NavItem navItem, ArtistAlbumHelper artistAlbumHelper, int position) {
		/** Sanity check */
		if (position < 0 || position >= artistCursor.getCount()) {
			Log.i(TAG, " - reading pixels from file failed");
			return false;
		}

		try {
			/** move cursor */
			artistCursor.moveToPosition(position);

			/** get album info */
			navItem.artistName = artistCursor.getString(artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));

			navItem.artistId = artistCursor.getString(artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID));
			navItem.nAlbumsFromArtist = artistCursor.getInt(artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));
			navItem.nSongsFromArtist = artistCursor.getInt(artistCursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));

			if (artistAlbumHelper != null && navItem.artistId == artistAlbumHelper.artistId) {
				Log.i(TAG, "Artist Album Helper is OUT OF SYNC");
				navItem.albumId = artistAlbumHelper.albumId;
			}

			return true;
		} catch (CursorIndexOutOfBoundsException e) {
			e.printStackTrace();
			return false;
		} catch (StaleDataException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 
	 * @param navItem
	 * @param width
	 * @param height
	 * @param colorComponent
	 * @param theme
	 * @return
	 */
	boolean fillArtistBitmap(NavItem navItem, ArtistAlbumHelper artistAlbumHelper, int width, int height, byte[] colorComponent, int theme) {
		try {
			/** Sanity check */
			if (navItem.cover.getWidth() != width || navItem.cover.getHeight() != height) {
				Log.i(TAG, " - reading pixels from file failed");
				return false;
			}

			/** Get the path to the album art */
			switch (theme) {
			case Constants.THEME_NORMAL:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(artistAlbumHelper.albumId);
				break;
			case Constants.THEME_HALFTONE:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(artistAlbumHelper.albumId) + Constants.THEME_HALF_TONE_FILE_EXT;
				break;
			case Constants.THEME_EARTHQUAKE:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(artistAlbumHelper.albumId)
						+ Constants.THEME_EARTHQUAKE_FILE_EXT;
				break;
			default:
				path = Constants.ROCKON_SMALL_ALBUM_ART_PATH + RockOnFileUtils.validateFileName(artistAlbumHelper.albumId);
				break;
			}

			/** Access the file */
			albumCoverFile = new File(path);
			if (albumCoverFile.exists() && albumCoverFile.length() > 0) {
			} else {
				// Log.i(TAG, " - album cover bmp file has a problem "+path);
				return false;
			}

			/** Read File and fill bitmap */
			try {
				FileInputStream albumCoverFileInputStream = new FileInputStream(albumCoverFile);
				albumCoverFileInputStream.read(colorComponent, 0, colorComponent.length);
				navItem.cover.copyPixelsFromBuffer(ByteBuffer.wrap(colorComponent));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
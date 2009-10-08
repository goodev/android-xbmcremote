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

package org.xbmc.android.guilogic;

import java.util.ArrayList;

import org.xbmc.android.backend.httpapi.HttpApiHandler;
import org.xbmc.android.backend.httpapi.HttpApiThread;
import org.xbmc.android.remote.R;
import org.xbmc.android.remote.activity.DialogFactory;
import org.xbmc.android.remote.activity.ListActivity;
import org.xbmc.android.remote.activity.MusicArtistActivity;
import org.xbmc.httpapi.data.Album;
import org.xbmc.httpapi.data.Artist;
import org.xbmc.httpapi.data.Song;
import org.xbmc.httpapi.type.ListType;
import org.xbmc.httpapi.type.ThumbSize;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class MusicListLogic extends ListLogic {
	
	public static final int ITEM_CONTEXT_QUEUE = 1;
	public static final int ITEM_CONTEXT_PLAY = 2;
	public static final int ITEM_CONTEXT_INFO = 3;
	
	public static final String EXTRA_LIST_TYPE = "listType"; 
	public static final String EXTRA_ALBUM = "album"; 
	public static final String EXTRA_ARTIST = "artist"; 

	private ListType mListType;
	private Album mAlbum;
	private Artist mArtist;
	
	public MusicListLogic(Activity activity, ListView list) {
		super(activity, list);
	}
	
	public void setType(ListType type) {
		mListType = type;
	}
	
	public void onCreate() {
		if (!isCreated()) {
			
			if (mListType == null) {
				final String mt = mActivity.getIntent().getStringExtra(EXTRA_LIST_TYPE);
				mListType = mt != null ? ListType.valueOf(mt) : ListType.albums;
			}
			
			mAlbum = (Album)mActivity.getIntent().getSerializableExtra(EXTRA_ALBUM);
			mArtist = (Artist)mActivity.getIntent().getSerializableExtra(EXTRA_ARTIST);
			
			mActivity.registerForContextMenu(mList);
			
			mList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent nextActivity;
					switch (mListType) {
						case albums:
							Album album = (Album)view.getTag();
							nextActivity = new Intent(view.getContext(), ListActivity.class);
							nextActivity.putExtras(mActivity.getIntent().getExtras());
							nextActivity.putExtra(ListActivity.EXTRA_LOGIC_TYPE, ListLogic.LOGIC_ALBUM);
							nextActivity.putExtra(EXTRA_LIST_TYPE, ListType.songs.toString());
							nextActivity.putExtra(EXTRA_ALBUM, album);
							mActivity.startActivity(nextActivity);
						break;
						case songs:
							Song song = (Song)view.getTag();
							HttpApiThread.music().play(new HttpApiHandler<Boolean>((Activity)view.getContext()), song);
						break;
						case artists:
							Artist artist = (Artist)view.getTag();
							nextActivity = new Intent(view.getContext(), MusicArtistActivity.class);
							nextActivity.putExtras(mActivity.getIntent().getExtras());
							nextActivity.putExtra(ListActivity.EXTRA_LOGIC_TYPE, ListLogic.LOGIC_ALBUM);
							nextActivity.putExtra(EXTRA_ARTIST, artist);
							mActivity.startActivity(nextActivity);
							break;
					}
				}
			});
					
			// depending on list type, fetch albums or songs
			switch (mListType) {
				case albums:
					if (mArtist == null) {
						setTitle("Albums...");
						HttpApiThread.music().getAlbums(new HttpApiHandler<ArrayList<Album>>(mActivity) {
							public void run() {
								setTitle("Albums (" + value.size() + ")");
								mList.setAdapter(new AlbumAdapter(mActivity, value));
							}
						});
					} else {
						setTitle(mArtist.name + " - Albums...");
						HttpApiThread.music().getAlbums(new HttpApiHandler<ArrayList<Album>>(mActivity) {
							public void run() {
								setTitle(mArtist.name + " - Albums (" + value.size() + ")");
								mList.setAdapter(new AlbumAdapter(mActivity, value));
							}
						}, mArtist);
					}
					break;
				case songs:
					if (mAlbum != null) {
						setTitle("Songs...");
						HttpApiThread.music().getSongs(new HttpApiHandler<ArrayList<Song>>(mActivity) {
							public void run() {
								setTitle(mAlbum.name);
								mList.setAdapter(new SongAdapter(mActivity, value));
							}
						}, mAlbum);
					}
					if (mArtist != null) {
						setTitle(mArtist.name + " - Songs...");
						HttpApiThread.music().getSongs(new HttpApiHandler<ArrayList<Song>>(mActivity) {
							public void run() {
								setTitle(mArtist.name + " - Songs (" + value.size() + ")");
								mList.setAdapter(new SongAdapter(mActivity, value));
							}
						}, mArtist);
					}
					break;
				case artists:
					setTitle("Artists...");
					HttpApiThread.music().getArtists(new HttpApiHandler<ArrayList<Artist>>(mActivity) {
						public void run() {
							setTitle("Artists (" + value.size() + ")");
							mList.setAdapter(new ArtistAdapter(mActivity, value));
						}
					});
					break;
				default:
					break;
			}
		}
		super.onCreate();
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, ITEM_CONTEXT_QUEUE, 1, "Queue " + mListType.getSingular());
		menu.add(0, ITEM_CONTEXT_PLAY, 2, "Play " + mListType.getSingular());
		String title = "";
		switch (mListType) {
			case albums:
				final Album album = (Album)((AdapterContextMenuInfo)menuInfo).targetView.getTag();
				title = album.name;
				menu.add(0, ITEM_CONTEXT_INFO, 3, "View Details");
				break;
			case songs:
				final Song song = (Song)((AdapterContextMenuInfo)menuInfo).targetView.getTag();
				title = song.title;
				break;
		}
		menu.setHeaderTitle(title);
	}
	
	public void onContextItemSelected(MenuItem item) {
		switch (mListType) {
			case albums:
				final Album album = (Album)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
				switch (item.getItemId()) {
					case ITEM_CONTEXT_QUEUE:
						HttpApiThread.music().addToPlaylist(new HttpApiHandler<Song>(mActivity), album);
						break;
					case ITEM_CONTEXT_PLAY:
						HttpApiThread.music().play(new HttpApiHandler<Boolean>(mActivity), album);
						break;
					case ITEM_CONTEXT_INFO:
						DialogFactory.getAlbumDetail(mActivity, album).show();
						break;
					default:
						return;
				}
			break;
			case songs:
				final Song song = (Song)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
				HttpApiThread.music().addToPlaylist(new HttpApiHandler<Boolean>(mActivity), song);
				switch (item.getItemId()) {
					case ITEM_CONTEXT_QUEUE:
						HttpApiThread.music().addToPlaylist(new HttpApiHandler<Boolean>(mActivity), song);
						break;
					case ITEM_CONTEXT_PLAY:
						HttpApiThread.music().play(new HttpApiHandler<Boolean>(mActivity), song);
						break;
					case ITEM_CONTEXT_INFO:
						break;
					default:
						return;
				}
			break;
		}
		return;
	}
	
	private class AlbumAdapter extends ArrayAdapter<Album> {
		private Activity mActivity;
		AlbumAdapter(Activity activity, ArrayList<Album> items) {
			super(activity, R.layout.music_item, items);
			mActivity = activity;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				row = inflater.inflate(R.layout.music_item, null);
			} else {
				row = convertView;
			}
			final Album album = this.getItem(position);
			row.setTag(album);
			final TextView title = (TextView)row.findViewById(R.id.MusicItemTextViewTitle);
			final TextView subtitle = (TextView)row.findViewById(R.id.MusicItemTextViewSubtitle);
			final TextView subsubtitle = (TextView)row.findViewById(R.id.MusicItemTextViewSubSubtitle);
			final ImageView icon = (ImageView)row.findViewById(R.id.MusicItemImageViewArt);
			if (position == getCount() - 1) {
				row.setBackgroundResource(R.drawable.back_bottom_rounded);
			} else {
				row.setBackgroundColor(0xfff8f8f8);
			}			
			title.setText(album.name);
			subtitle.setText(album.artist);
			subsubtitle.setText(album.year > 0 ? String.valueOf(album.year) : "");
			
			HttpApiThread.music().getAlbumCover(new HttpApiHandler<Bitmap>(mActivity, album) {
				public void run() {
					if (album.getId() == ((Album)mTag).getId()) {
						if (value == null) {
							icon.setImageResource(R.drawable.home_music);
						} else {
							icon.setImageBitmap(value);
						}
					}
				}
			}, album, ThumbSize.small);
			return row;
		}
	}
	
	private class SongAdapter extends ArrayAdapter<Song> {
		private Activity mActivity;
		SongAdapter(Activity activity, ArrayList<Song> items) {
			super(activity, R.layout.music_item, items);
			mActivity = activity;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				row = inflater.inflate(R.layout.music_item, null);
			} else {
				row = convertView;
			}
			final Song song = this.getItem(position);
			row.setTag(song);
			final TextView title = (TextView)row.findViewById(R.id.MusicItemTextViewTitle);
			final TextView subtitle = (TextView)row.findViewById(R.id.MusicItemTextViewSubtitle);
			final TextView subsubtitle = (TextView)row.findViewById(R.id.MusicItemTextViewSubSubtitle);
			final ImageView icon = (ImageView)row.findViewById(R.id.MusicItemImageViewArt);
			if (position == getCount() - 1) {
				row.setBackgroundResource(R.drawable.back_bottom_rounded);
			} else {
				row.setBackgroundColor(0xfff8f8f8);
			}			
			title.setText(song.track + " " + song.title);
			subtitle.setText(song.artist);
			subsubtitle.setText(song.getDuration());
			
			HttpApiThread.music().getAlbumCover(new HttpApiHandler<Bitmap>(mActivity, mAlbum) {
				public void run() {
					if (value == null) {
						icon.setImageResource(R.drawable.home_music);
					} else {
						icon.setImageBitmap(value);
					}
				}
			}, mAlbum, ThumbSize.small);
			return row;
		}
	}
	
	private class ArtistAdapter extends ArrayAdapter<Artist> {
		private Activity mActivity;
		ArtistAdapter(Activity activity, ArrayList<Artist> items) {
			super(activity, R.layout.listitem_oneliner, items);
			mActivity = activity;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View row;
			if (convertView == null) {
				LayoutInflater inflater = mActivity.getLayoutInflater();
				row = inflater.inflate(R.layout.listitem_oneliner, null);
			} else {
				row = convertView;
			}
			final Artist artist = this.getItem(position);
			row.setTag(artist);
			final TextView title = (TextView)row.findViewById(R.id.MusicItemTextViewTitle);
			if (position == getCount() - 1) {
				row.setBackgroundResource(R.drawable.back_bottom_rounded);
			} else {
				row.setBackgroundColor(0xfff8f8f8);
			}			
			title.setText(artist.name);
			return row;
		}
	}
}

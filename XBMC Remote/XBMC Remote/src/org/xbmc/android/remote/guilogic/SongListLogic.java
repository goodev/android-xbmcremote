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

package org.xbmc.android.remote.guilogic;

import java.util.ArrayList;

import org.xbmc.android.remote.R;
import org.xbmc.android.remote.drawable.CrossFadeDrawable;
import org.xbmc.android.remote.guilogic.holder.ThreeHolder;
import org.xbmc.backend.HttpApiHandler;
import org.xbmc.backend.async.thread.HttpApiThread;
import org.xbmc.backend.httpapi.type.ThumbSize;
import org.xbmc.model.Album;
import org.xbmc.model.Artist;
import org.xbmc.model.Genre;
import org.xbmc.model.Song;

import android.app.Activity;
import android.graphics.BitmapFactory;
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

public class SongListLogic extends ListLogic {
	
	public static final int ITEM_CONTEXT_QUEUE = 1;
	public static final int ITEM_CONTEXT_PLAY = 2;
	public static final int ITEM_CONTEXT_INFO = 3;
	
	private Album mAlbum;
	private Artist mArtist;
	private Genre mGenre;
	
	public void onCreate(Activity activity, ListView list) {
		if (!isCreated()) {
			
			super.onCreate(activity, list);
			
			mAlbum = (Album)mActivity.getIntent().getSerializableExtra(EXTRA_ALBUM);
			mArtist = (Artist)mActivity.getIntent().getSerializableExtra(EXTRA_ARTIST);
			mGenre = (Genre)mActivity.getIntent().getSerializableExtra(EXTRA_GENRE);
			mActivity.registerForContextMenu(mList);
			
			mFallbackBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.icon_music);
			setupIdleListener();
			
			mList.setOnItemClickListener(new OnItemClickListener() {
				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final Song song = ((ThreeHolder<Song>)view.getTag()).getHolderItem();
					if (mAlbum == null) {
						HttpApiThread.music().play(new QueryHandler(
							mActivity, 
							"Playing \"" + song.title + "\" by " + song.artist + "...", 
							"Error playing song!",
							true
						), song);
					} else {
						HttpApiThread.music().play(new QueryHandler(
							mActivity, 
							"Playing album \"" + song.album + "\" starting with song \"" + song.title + "\" by " + song.artist + "...",
							"Error playing song!",
							true
						), mAlbum, song);
					}
				}
			});
					
			mList.setOnKeyListener(new ListLogicOnKeyListener<Song>());
			
			if (mAlbum != null) {
				setTitle("Songs...");
				HttpApiThread.music().getSongs(new HttpApiHandler<ArrayList<Song>>(mActivity) {

					@Override
					public void doFinish(ArrayList<Song> value) {
						setTitle(mAlbum.name);
						mList.setAdapter(new SongAdapter(mActivity, value));
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						
					}
				}, mAlbum);
				
			} else if (mArtist != null) {
				setTitle(mArtist.name + " - Songs...");
				HttpApiThread.music().getSongs(new HttpApiHandler<ArrayList<Song>>(mActivity) {

					@Override
					public void doFinish(ArrayList<Song> value) {
						setTitle(mArtist.name + " - Songs (" + value.size() + ")");
						mList.setAdapter(new SongAdapter(mActivity, value));
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						
					}
				}, mArtist);
				
			} else if (mGenre != null) {
				setTitle(mGenre.name + " - Songs...");
				HttpApiThread.music().getSongs(new HttpApiHandler<ArrayList<Song>>(mActivity) {

					@Override
					public void doFinish(ArrayList<Song> value) {
						setTitle(mGenre.name + " - Songs (" + value.size() + ")");
						mList.setAdapter(new SongAdapter(mActivity, value));
					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub
						
					}
				}, mGenre);
			}
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		// be aware that this must be explicitly called by your activity!
		final ThreeHolder<Song> holder = (ThreeHolder<Song>)((AdapterContextMenuInfo)menuInfo).targetView.getTag();
		menu.setHeaderTitle(holder.getHolderItem().title);
		menu.add(0, ITEM_CONTEXT_QUEUE, 1, "Queue Song");
		menu.add(0, ITEM_CONTEXT_PLAY, 2, "Play Song");
	}
	
	@SuppressWarnings("unchecked")
	public void onContextItemSelected(MenuItem item) {
		// be aware that this must be explicitly called by your activity!
		final ThreeHolder<Song> holder = (ThreeHolder<Song>)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
		switch (item.getItemId()) {
			case ITEM_CONTEXT_QUEUE:
				if (mAlbum == null) {
					HttpApiThread.music().addToPlaylist(new QueryHandler(mActivity, "Song added to playlist.", "Error adding song!"), holder.getHolderItem());
				} else {
					HttpApiThread.music().addToPlaylist(new QueryHandler(mActivity, "Playlist empty, added whole album.", "Song added to playlist."), mAlbum, holder.getHolderItem());
				}
				break;
			case ITEM_CONTEXT_PLAY:
				final Song song = holder.getHolderItem();
				if (mAlbum == null) {
					HttpApiThread.music().play(new QueryHandler(
						mActivity, 
						"Playing \"" + song.title + "\" by " + song.artist + "...", 
						"Error playing song!",
						true
					), song);
				} else {
					HttpApiThread.music().play(new QueryHandler(
						mActivity, 
						"Playing album \"" + song.album + "\" starting with song \"" + song.title + "\" by " + song.artist + "...",
						"Error playing song!",
						true
					), mAlbum, song);
				}
				break;
			default:
				return;
		}
	}
	
	private class SongAdapter extends ArrayAdapter<Song> {
		private Activity mActivity;
		private final LayoutInflater mInflater;
		SongAdapter(Activity activity, ArrayList<Song> items) {
			super(activity, R.layout.listitem_three, items);
			mActivity = activity;
			mInflater = LayoutInflater.from(activity);
		}
		@SuppressWarnings("unchecked")
		public View getView(int position, View convertView, ViewGroup parent) {
			
			View row;
			ThreeHolder<Song> holder;
			
			if (convertView == null) {
				
				row = mInflater.inflate(R.layout.listitem_three, null);
				holder = new ThreeHolder<Song>(
					(ImageView)row.findViewById(R.id.MusicItemImageViewArt),
					(TextView)row.findViewById(R.id.MusicItemTextViewTitle),
					(TextView)row.findViewById(R.id.MusicItemTextViewSubtitle),
					(TextView)row.findViewById(R.id.MusicItemTextViewSubSubtitle)
				);
				row.setTag(holder);
				
				CrossFadeDrawable transition = new CrossFadeDrawable(mFallbackBitmap, null);
				transition.setCrossFadeEnabled(true);
				holder.transition = transition;
			} else {
				row = convertView;
				holder = (ThreeHolder<Song>)convertView.getTag();
			}
			final Song song = getItem(position);
			
			if (mAlbum != null) {
				holder.setText(song.title, song.artist, song.getDuration());
			} else if (mArtist != null) {
				holder.setText(song.title, song.album, song.getDuration());
			} else if (mGenre != null) {
				holder.setText(song.title, song.artist, song.getDuration());
			}
			holder.setHolderItem(song);
			holder.id = song.getId();
			holder.setCoverItem(song);
			holder.setImageResource(R.drawable.icon_album_grey);
			holder.setTemporaryBind(true);
			HttpApiThread.music().getAlbumCover(holder.getCoverDownloadHandler(mActivity, mPostScrollLoader), song, ThumbSize.small);
			return row;
		}
	}
	
	private static final long serialVersionUID = 755529227668553163L;
}

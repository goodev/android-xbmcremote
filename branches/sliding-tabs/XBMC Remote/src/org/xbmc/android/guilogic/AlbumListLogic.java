package org.xbmc.android.guilogic;

import java.util.ArrayList;

import org.xbmc.android.backend.httpapi.HttpApiHandler;
import org.xbmc.android.backend.httpapi.HttpApiThread;
import org.xbmc.android.remote.R;
import org.xbmc.android.remote.activity.DialogFactory;
import org.xbmc.android.remote.activity.MusicLibraryActivity;
import org.xbmc.httpapi.data.Album;
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

public class AlbumListLogic {
	
	public static final int ITEM_CONTEXT_QUEUE = 1;
	public static final int ITEM_CONTEXT_PLAY = 2;
	public static final int ITEM_CONTEXT_INFO = 3;
	
	public static final String EXTRA_LIST_TYPE = "listType"; 
	public static final String EXTRA_ALBUM = "album"; 
	
	private final ListView mList;
	private final Activity mActivity;
	
	private ListType mListType;
	private Album mAlbum;
	
	public AlbumListLogic(Activity activity, ListView list) {
		mList = list;
		mActivity = activity;
		
		final String mt = mActivity.getIntent().getStringExtra(EXTRA_LIST_TYPE);
		final TextView titleView = (TextView)mActivity.findViewById(R.id.titlebar_text);
		mListType = mt != null ? ListType.valueOf(mt) : ListType.albums;
		mAlbum = (Album)mActivity.getIntent().getSerializableExtra(EXTRA_ALBUM);
		
		mActivity.registerForContextMenu(mList);
		
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent nextActivity;
				switch (mListType) {
					case albums:
						Album album = (Album)view.getTag();
						nextActivity = new Intent(view.getContext(), MusicLibraryActivity.class);
						nextActivity.putExtras(mActivity.getIntent().getExtras());
						nextActivity.putExtra(EXTRA_LIST_TYPE, ListType.songs.toString());
						nextActivity.putExtra(EXTRA_ALBUM, album);
						mActivity.startActivity(nextActivity);
					break;
					case songs:
						Song song = (Song)view.getTag();
						HttpApiThread.music().play(new HttpApiHandler<Boolean>((Activity)view.getContext()), song);
					break;
				}
			}
		});
				
		// depending on list type, fetch albums or songs
		switch (mListType) {
			case albums:
				titleView.setText("Albums...");
				HttpApiThread.music().getAlbums(new HttpApiHandler<ArrayList<Album>>(mActivity) {
					public void run() {
						titleView.setText("Albums (" + value.size() + ")");
						mList.setAdapter(new AlbumAdapter(mActivity, value));
					}
				});
				break;
			case songs:
				titleView.setText("Songs...");
				if (mAlbum != null) {
					HttpApiThread.music().getSongs(new HttpApiHandler<ArrayList<Song>>(mActivity) {
						public void run() {
							titleView.setText(mAlbum.name);
							mList.setAdapter(new SongAdapter(mActivity, value));
						}
					}, mAlbum);
				}
				break;
			default:
				break;
		}
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
	
}

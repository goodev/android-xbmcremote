package org.xbmc.android.remote.presentation.controller;

import java.util.ArrayList;
import java.util.Iterator;

import org.xbmc.android.remote.R;
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.android.remote.presentation.activity.EditPlaylistActivity;
import org.xbmc.android.remote.presentation.widget.DragAndDropListView;
import org.xbmc.android.remote.presentation.widget.DragAndDropListView.DragAndDropListener;
import org.xbmc.api.business.DataResponse;
import org.xbmc.api.business.IMusicManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EditPlaylistController extends ListController {
	private static final String TAG = "EditPlaylistController";
	
	private Button mDeleteButton;
	private Button mSaveButton;
	private Button mCancelButton;
	
	private ArrayList<ListItem> mItems;
	private int selectedItemCount;
	private SongAdapter mSongAdapter;
	
	private IMusicManager mMusicManager;
	
	private Resources mResources;
	
	public EditPlaylistController(EditPlaylistActivity mActivity) {
		this.mActivity = mActivity;
	}
	
	@Override
	public void onCreate(Activity activity, Handler handler, AbsListView list) {
		super.onCreate(activity, handler, list);
		mResources = activity.getResources();
		setTitle("Edit Music Playlist");
		
		mMusicManager = ManagerFactory.getMusicManager(this);
		
		DragAndDropListView listView = (DragAndDropListView) list;
		listView.setListener(new DragAndDropListener() {
			public void onDrop(int from, int to) {
				if (from == to) {
					return;
				}
				
				ListItem item = mItems.get(from);
				mItems.remove(from);
				mItems.add(to, item);				
				mSongAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onContextItemSelected(MenuItem item) {
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	}
	
	public void setItemPaths(ArrayList<String> paths) {
		if (paths != null && paths.size() > 0) {
			mItems = new ArrayList<ListItem>();
			for (String path : paths) {
				mItems.add(new ListItem(path));
			}
			mSongAdapter = new SongAdapter(mItems);
			mList.setAdapter(mSongAdapter);
			selectedItemCount = 0;
		} else {
			setNoDataMessage("No tracks in playlist.", R.drawable.icon_playlist_dark);
		}
	}
	
	public void setupButtons(View delete, View save, View cancel) {
		mDeleteButton = (Button) delete;
		mDeleteButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Iterator<ListItem> iterator = mItems.iterator();
				while (iterator.hasNext()) {
					if (iterator.next().isChecked()) {
						iterator.remove();
					}
				}
				mSongAdapter.notifyDataSetChanged();
				incrementSelectedItemCount(-selectedItemCount);
			}
		});
		mSaveButton = (Button) save;
		mSaveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				final ProgressDialog progressDialog = ProgressDialog.show(mActivity, "", mActivity.getString(R.string.processing), true);
				progressDialog.setCancelable(false);
				new PlaylistUpdaterThread(mMusicManager, mItems, mActivity, new Handler() {
					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
							case PlaylistUpdaterThread.DONE_MESSAGE:
								progressDialog.dismiss();
								mActivity.finish();
								break;
							default:
								Log.e(TAG, "Unknown message: " + msg.what);
						}
						super.handleMessage(msg);
					}
				}).start();
			}
		});
		mCancelButton = (Button) cancel;
		mCancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mActivity.finish();
			}
		});
	}
	
	private void incrementSelectedItemCount(int delta) {
		selectedItemCount += delta;
		if (selectedItemCount == 0) {
			mDeleteButton.setVisibility(View.GONE);
			mSaveButton.setVisibility(View.VISIBLE);
			mCancelButton.setVisibility(View.VISIBLE);
		} else {
			mDeleteButton.setText(mResources.getQuantityString(R.plurals.delete_selected_items, selectedItemCount, selectedItemCount));
			mDeleteButton.setVisibility(View.VISIBLE);
			mSaveButton.setVisibility(View.GONE);
			mCancelButton.setVisibility(View.GONE);
		}
	}
	
	private class SongAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private ArrayList<ListItem> mContent;
		
		SongAdapter(ArrayList<ListItem> content) {
			this.mInflater = mActivity.getLayoutInflater();
			this.mContent = content;
		}

		public int getCount() {
			return mContent.size();
		}

		public Object getItem(int position) {
			return mContent.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			
			if (view == null) {
				view = mInflater.inflate(R.layout.edit_playlist_item, parent, false);
			}

			final ListItem item = mContent.get(position);
			
			TextView label = (TextView) view.findViewById(R.id.label);
			label.setText(item.getFilename());
			
			CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
			checkBox.setOnCheckedChangeListener(null);
			checkBox.setChecked(item.isChecked());
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					item.setChecked(isChecked);
					incrementSelectedItemCount(isChecked ? 1 : -1);
				}
			});
			
			return view;
		}
	}
	
	private static class ListItem {
		private final String path;
		private final String filename;
		private boolean checked;
		
		public ListItem(String path) {
			this.path = path;
			this.filename = path.substring(path.replaceAll("\\\\", "/").lastIndexOf('/') + 1);
			this.checked = false;
		}

		public String getPath() {
			return path;
		}

		public String getFilename() {
			return filename;
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}
	}
	
	private static class PlaylistUpdaterThread extends Thread {
		public static final int DONE_MESSAGE = 1;
		
		private final IMusicManager mMusicManager;
		private final ArrayList<ListItem> mItems;
		private final Context mContext;
		private final Handler mHandler;
		
		public PlaylistUpdaterThread(IMusicManager musicManager, ArrayList<ListItem> items, Context context, Handler handler) {
			this.mMusicManager = musicManager;
			this.mItems = items;
			this.mContext = context;
			this.mHandler = handler;
		}
		
		@Override
		public void run() {
			mMusicManager.clearPlaylist(new DataResponse<Boolean>() {
				@Override
				public void run() {
					ArrayList<String> paths = new ArrayList<String>();
					for (ListItem item : mItems) {
						paths.add(item.getPath());
					}
					mMusicManager.addToPlaylist(new DataResponse<Boolean>() {
						@Override
						public void run() {
							mHandler.sendEmptyMessage(DONE_MESSAGE);
						}
					}, paths, mContext);
				}
			}, mContext);
		}
	}
}

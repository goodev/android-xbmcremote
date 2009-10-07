package org.xbmc.android.guilogic;

import java.util.ArrayList;
import java.util.HashMap;

import org.xbmc.android.backend.httpapi.HttpApiHandler;
import org.xbmc.android.backend.httpapi.HttpApiThread;
import org.xbmc.android.remote.R;
import org.xbmc.android.remote.activity.MusicLibraryActivity;
import org.xbmc.android.remote.activity.NowPlayingActivity;
import org.xbmc.httpapi.data.MediaLocation;
import org.xbmc.httpapi.type.MediaType;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FileListLogic {
	
	public static final int MESSAGE_HANDLE_DATA = 1;
	public static final int MESSAGE_CONNECTION_ERROR = 2;
	
	public static final String EXTRA_SHARE_TYPE = "shareType"; 
	public static final String EXTRA_PATH = "path"; 
	
	private final ListView mList;
	private final Activity mActivity;
	
	private HashMap<String, MediaLocation> mFileItems;
	private volatile String mGettingUrl;
	private MediaType mMediaType;
	
	private TextView mTitleView;
	
	// from ListActivity.java
	protected ListAdapter mAdapter;
	
	public FileListLogic(Activity activity, ListView list) {
		mList = list;
		mActivity = activity;
		
		final String st = mActivity.getIntent().getStringExtra(EXTRA_SHARE_TYPE);
		mMediaType = st != null ? MediaType.valueOf(st) : MediaType.music;
		mTitleView = (TextView)mActivity.findViewById(R.id.titlebar_text);
		final String path = mActivity.getIntent().getStringExtra(EXTRA_PATH);
		fillUp(path == null ? "" : path);

		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mFileItems == null)
					return;

				MediaLocation item = mFileItems.get(((MediaLocation)parent.getAdapter().getItem(position)).name);
				if (item.isDirectory) {
					Intent nextActivity = new Intent(mActivity, MusicLibraryActivity.class);
					nextActivity.putExtras(mActivity.getIntent().getExtras());
					nextActivity.putExtra(EXTRA_SHARE_TYPE, mMediaType.toString());
					nextActivity.putExtra(EXTRA_PATH, item.path);
					mActivity.startActivity(nextActivity);
				} else {
					HttpApiThread.control().playFile(new HttpApiHandler<Boolean>(mActivity) {
						public void run() {
							if (value) {
								mActivity.startActivity(new Intent(mActivity, NowPlayingActivity.class));
							}
						}
					}, item.path);
				}
			}
		});
	}
	
	private class FileItemAdapter extends ArrayAdapter<MediaLocation> {
		private Activity mActivity;
		FileItemAdapter(Activity activity, ArrayList<MediaLocation> items) {
			super(activity, R.layout.music_item, items);
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
			final MediaLocation fileItem = this.getItem(position);
			row.setTag(fileItem);
			final TextView title = (TextView)row.findViewById(R.id.MusicItemTextViewTitle);
			final ImageView icon = (ImageView)row.findViewById(R.id.MusicItemImageViewArt);
			if (position == mFileItems.size() - 1) {
				row.setBackgroundResource(R.drawable.back_bottom_rounded);
			} else {
				row.setBackgroundColor(0xfff8f8f8);
			}
			title.setText(fileItem.name);
			icon.setImageResource(R.drawable.icon_folder);
			return row;
		}
	}
	
	
	private void fillUp(final String url) {
		if (mGettingUrl != null)
			return;
		
		mGettingUrl = url;
		mFileItems = null;
		mList.setTextFilterEnabled(false);
		mTitleView.setText("Loading...");
		
		HttpApiHandler<ArrayList<MediaLocation>> mediaListHandler = new HttpApiHandler<ArrayList<MediaLocation>>(mActivity) {
			public void run() {
				mTitleView.setText(url);
				
				mFileItems = new HashMap<String, MediaLocation>();
				for (MediaLocation item : value) {
					mFileItems.put(item.name, item);
				}
				
				setListAdapter(new FileItemAdapter(mActivity, value));
				mList.setTextFilterEnabled(true);
			}
		};
		
		if (mGettingUrl.length() == 0) {
			HttpApiThread.info().getShares(mediaListHandler, mMediaType);
		} else {
			HttpApiThread.info().getDirectory(mediaListHandler, mGettingUrl);
		}
	}
	
    /**
     * Provide the cursor for the list view.
     */
    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            mAdapter = adapter;
            mList.setAdapter(adapter);
        }
    }
}

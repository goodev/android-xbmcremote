package org.xbmc.android.guilogic;

import org.xbmc.android.remote.R;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public abstract class ListLogic {
	
	public static final int LOGIC_ALBUM = 1;
	public static final int LOGIC_FILELIST = 2;
	
	protected final ListView mList;
	protected final Activity mActivity;
	
	private TextView mTitleView;
	private boolean isCreated = false;
	
	public ListLogic(Activity activity, ListView list) {
		mList = list;
		mActivity = activity;
	}
	
	public static ListLogic factory(int logicType, Activity activity, ListView list) {
		switch (logicType) {
		case LOGIC_ALBUM:
			return new MusicListLogic(activity, list);
		case LOGIC_FILELIST:
			return new FileListLogic(activity, list);
		}
		return null;
	}
	
	public void onContextItemSelected(MenuItem item) {
	}
	
	public void onCreateContextMenu() {
	}
	
	public void findTitleView(View parent) {
		mTitleView = (TextView)parent.findViewById(R.id.titlebar_text);	
	}
	
	protected void setTitle(String title) {
		if (mTitleView != null) {
			mTitleView.setText(title);
		}
	}
	
	protected boolean isCreated() {
		return isCreated;
	}
	
	public void onCreate() {
		isCreated = true;
	}
	
}

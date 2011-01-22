package org.xbmc.android.remote.presentation.activity;

import java.io.IOException;

import org.xbmc.android.remote.R;
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.android.remote.presentation.controller.EditPlaylistController;
import org.xbmc.android.remote.presentation.widget.DragAndDropListView;
import org.xbmc.android.util.KeyTracker;
import org.xbmc.android.util.OnLongPressBackKeyTracker;
import org.xbmc.android.util.KeyTracker.Stage;
import org.xbmc.api.business.IEventClientManager;
import org.xbmc.eventclient.ButtonCodes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Build.VERSION;
import android.view.KeyEvent;

public class EditPlaylistActivity extends Activity {
	public static final String EXTRA_PLAYLIST_ITEMS = "playlistItems";
	
	private EditPlaylistController mEditPlaylistController;
	private ConfigurationManager mConfigurationManager;
	private KeyTracker mKeyTracker;
	
	public EditPlaylistActivity() {
		if(Integer.parseInt(VERSION.SDK) < 5) {
			mKeyTracker = new KeyTracker(new OnLongPressBackKeyTracker() {
				@Override
				public void onLongPressBack(int keyCode, KeyEvent event,
						Stage stage, int duration) {
					onKeyLongPress(keyCode, event);
				}
	
				@Override
				public void onShortPressBack(int keyCode, KeyEvent event,
						Stage stage, int duration) {
					callSuperOnKeyDown(keyCode, event);
				}
			});
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_playlist);
		
		mEditPlaylistController = new EditPlaylistController(this);
		mEditPlaylistController.findTitleView(findViewById(R.id.playlist_outer_layout));
		mEditPlaylistController.findMessageView(findViewById(R.id.playlist_outer_layout));
		mEditPlaylistController.onCreate(this, new Handler(), (DragAndDropListView) findViewById(R.id.playlist_list));
		mEditPlaylistController.setItemPaths(getIntent().getStringArrayListExtra(EXTRA_PLAYLIST_ITEMS));
		mEditPlaylistController.setupButtons(findViewById(R.id.delete), findViewById(R.id.save), findViewById(R.id.cancel));
		
		mConfigurationManager = ConfigurationManager.getInstance(this);
	}

	protected void callSuperOnKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mEditPlaylistController.onActivityResume(this);
		mConfigurationManager.onActivityResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mEditPlaylistController.onActivityPause();
		mConfigurationManager.onActivityPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		IEventClientManager client = ManagerFactory.getEventClientManager(mEditPlaylistController);
		try {
			switch (keyCode) {
				case KeyEvent.KEYCODE_VOLUME_UP:
					client.sendButton("R1", ButtonCodes.REMOTE_VOLUME_PLUS, false, true, true, (short)0, (byte)0);
					return true;
				case KeyEvent.KEYCODE_VOLUME_DOWN:
					client.sendButton("R1", ButtonCodes.REMOTE_VOLUME_MINUS, false, true, true, (short)0, (byte)0);
					return true;
			}
		} catch (IOException e) {
			client.setController(null);
			return false;
		}
		client.setController(null);
		boolean handled =  (mKeyTracker != null)?mKeyTracker.doKeyDown(keyCode, event):false;
		return handled || super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		Intent intent = new Intent(this, HomeActivity.class);
		intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		return true;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean handled = (mKeyTracker != null) ? mKeyTracker.doKeyUp(keyCode, event) : false;
		return handled || super.onKeyUp(keyCode, event);
	}
}

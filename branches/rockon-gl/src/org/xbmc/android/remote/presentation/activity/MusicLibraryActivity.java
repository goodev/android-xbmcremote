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

package org.xbmc.android.remote.presentation.activity;

import java.io.IOException;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import org.abrantix.rockon.rockonnggl.Constants;
import org.abrantix.rockon.rockonnggl.NavGLTouchListener;
import org.abrantix.rockon.rockonnggl.RockOnRenderer;
import org.abrantix.rockon.rockonnggl.RockOnWallRenderer;
import org.xbmc.android.remote.R;
import org.xbmc.android.remote.business.ManagerFactory;
import org.xbmc.android.remote.presentation.controller.AlbumListController;
import org.xbmc.android.remote.presentation.controller.ArtistListController;
import org.xbmc.android.remote.presentation.controller.FileListController;
import org.xbmc.android.remote.presentation.controller.MusicGenreListController;
import org.xbmc.android.widget.slidingtabs.SlidingTabActivity;
import org.xbmc.android.widget.slidingtabs.SlidingTabHost;
import org.xbmc.android.widget.slidingtabs.SlidingTabHost.OnTabChangeListener;
import org.xbmc.api.business.IEventClientManager;
import org.xbmc.api.business.INotifiableManager;
import org.xbmc.eventclient.ButtonCodes;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.FrameLayout;
import android.widget.ListView;

public class MusicLibraryActivity extends SlidingTabActivity  {

	private SlidingTabHost mTabHost;
	private AlbumListController mAlbumController;
	private ArtistListController mArtistController;
	private MusicGenreListController mGenreController;
	private AlbumListController mCompilationsController;
	private FileListController mFileController;
	
	private RockOnRenderer mRockOnRenderer;
	private GLSurfaceView mGlSurfaceView;
	private int mBrowseCatMode;
    private Handler mRequestRenderHandler = new Handler(){
    	public void handleMessage(Message msg){
    		if(mGlSurfaceView.getRenderMode() != msg.what)
    			mGlSurfaceView.setRenderMode(msg.what);
    	}
    };
    private Handler	mSetNavigatorCurrent = new Handler(){
    	@Override
    	public void handleMessage(Message msg){
    		try {
            	//updateNavigatorToCurrent(mService);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
    	}
    };
    Handler mAlbumClickHandler = new Handler(){
		int	x;
		int	y;
		@Override
		public void handleMessage(Message msg){
			x = msg.arg1;
			y = msg.arg2;
			if(msg.what == Constants.SINGLE_CLICK){
				if(mRockOnRenderer.getBrowseCat() == Constants.BROWSECAT_ALBUM)
				{
					/* song list cursor */
					int albumId = mRockOnRenderer.getShownElementId(x, y);
					if(albumId < 0){
						this.sendEmptyMessageDelayed(0, Constants.CLICK_ACTION_DELAY);
						return;
					}
				}
				else if(mRockOnRenderer.getBrowseCat() == Constants.BROWSECAT_ARTIST)
				{
					/* song list cursor */
					int artistId = mRockOnRenderer.getShownElementId(x, y);
					if(artistId < 0){
						this.sendEmptyMessageDelayed(0, Constants.CLICK_ACTION_DELAY);
						return;
					}
				}
				
			} 
		}
	};
	
	private static final int MENU_NOW_PLAYING = 301;
	private static final int MENU_UPDATE_LIBRARY = 302;
	private static final int MENU_REMOTE = 303;
	
    private ConfigurationManager mConfigurationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.musiclibrary);
		
		// remove nasty top fading edge
		FrameLayout topFrame = (FrameLayout)findViewById(android.R.id.content);
		topFrame.setForeground(null);
		
		mTabHost = getTabHost();
		
		// add the tabs
		mTabHost.addTab(mTabHost.newTabSpec("tab_albums", "Albums", R.drawable.st_album_on, R.drawable.st_album_off).setBigIcon(R.drawable.st_album_over).setContent(R.id.albumlist_outer_layout));
		mTabHost.addTab(mTabHost.newTabSpec("tab_artists", "Artists", R.drawable.st_artist_on, R.drawable.st_artist_off).setBigIcon(R.drawable.st_artist_over).setContent(R.id.artists_outer_layout));
		mTabHost.addTab(mTabHost.newTabSpec("tab_genres", "Genres", R.drawable.st_genre_on, R.drawable.st_genre_off).setBigIcon(R.drawable.st_genre_over).setContent(R.id.genres_outer_layout));
		mTabHost.addTab(mTabHost.newTabSpec("tab_compilations", "Compilations", R.drawable.st_va_on, R.drawable.st_va_off).setBigIcon(R.drawable.st_va_over).setContent(R.id.compilations_outer_layout));
		mTabHost.addTab(mTabHost.newTabSpec("tab_files", "File Mode", R.drawable.st_filemode_on, R.drawable.st_filemode_off).setBigIcon(R.drawable.st_filemode_over).setContent(R.id.filelist_outer_layout));
		mTabHost.setCurrentTab(0);

		// assign the gui logic to each tab
		mAlbumController = new AlbumListController();
		mAlbumController.findTitleView(findViewById(R.id.albumlist_outer_layout));
		mAlbumController.findMessageView(findViewById(R.id.albumlist_outer_layout));
//		mAlbumController.setGrid((GridView)findViewById(R.id.albumlist_grid));
//		mAlbumController.onCreate(this, (ListView)findViewById(R.id.albumlist_list)); // first tab can be updated now.
		setupWallRenderer();
		
		mFileController = new FileListController();
		mFileController.findTitleView(findViewById(R.id.filelist_outer_layout));
		mFileController.findMessageView(findViewById(R.id.filelist_outer_layout));
		
		mArtistController = new ArtistListController();
		mArtistController.findTitleView(findViewById(R.id.artists_outer_layout));
		mArtistController.findMessageView(findViewById(R.id.artists_outer_layout));

		mGenreController = new MusicGenreListController();
		mGenreController.findTitleView(findViewById(R.id.genres_outer_layout));
		mGenreController.findMessageView(findViewById(R.id.genres_outer_layout));

		mCompilationsController = new AlbumListController();
		mCompilationsController.findTitleView(findViewById(R.id.compilations_outer_layout));
		mCompilationsController.findMessageView(findViewById(R.id.compilations_outer_layout));
		mCompilationsController.setCompilationsOnly(true);

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			public void onTabChanged(String tabId) {
				
				if (tabId.equals("tab_albums")) {
					//mAlbumController.onCreate(MusicLibraryActivity.this, (ListView)findViewById(R.id.albumlist_list));
					setupWallRenderer();
				}
				if (tabId.equals("tab_files")) {
					mFileController.onCreate(MusicLibraryActivity.this, (ListView)findViewById(R.id.filelist_list));
				}
				if (tabId.equals("tab_artists")) {
					mArtistController.onCreate(MusicLibraryActivity.this, (ListView)findViewById(R.id.artists_list));
				}
				if (tabId.equals("tab_genres")) {
					mGenreController.onCreate(MusicLibraryActivity.this, (ListView)findViewById(R.id.genres_list));
				}
				if (tabId.equals("tab_compilations")) {
					mCompilationsController.onCreate(MusicLibraryActivity.this, (ListView)findViewById(R.id.compilations_list));
				}
			}
		});
		
		mConfigurationManager = ConfigurationManager.getInstance(this);
	}
	
	private void setupWallRenderer() {
		
		/** Setup our 3d accelerated Surface */
		mGlSurfaceView = (GLSurfaceView) findViewById(R.id.cube_surface_view);

		/*************************************************
		 * OPENGL ES HACK FOR GALAXY AND OTHERS
		 *************************************************/
		mGlSurfaceView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});
		/************************************************
		 * HACK END
		 ************************************************/
		
		mBrowseCatMode = Constants.BROWSECAT_ALBUM;
		RockOnWallRenderer rockOnWallRenderer = new RockOnWallRenderer(getApplicationContext(), mRequestRenderHandler, Constants.THEME_NORMAL, mBrowseCatMode, (INotifiableManager)ManagerFactory.getMusicManager(mAlbumController));
		mGlSurfaceView.setRenderer(rockOnWallRenderer);
		mRockOnRenderer = (RockOnRenderer) rockOnWallRenderer;

		mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

		/** check if we were able to find any music */
		if (mRockOnRenderer.getAlbumCount() <= 0) {
			return;
		} else {
			if (mRockOnRenderer.getAlbumCursor() != null)
				startManagingCursor(mRockOnRenderer.getAlbumCursor());
		}
		
    	NavGLTouchListener 	navGLTouchListener = new NavGLTouchListener();
//    	navGLTouchListener.setRenderer(mRockOnCubeRenderer);
    	navGLTouchListener.setRenderer(mRockOnRenderer);
    	navGLTouchListener.setClickHandler(mAlbumClickHandler);
    	navGLTouchListener.setTimeoutHandler(mSetNavigatorCurrent);
    	mGlSurfaceView.setOnTouchListener(navGLTouchListener);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		menu.add(0, MENU_NOW_PLAYING, 0, "Now playing").setIcon(R.drawable.menu_nowplaying);
		switch (mTabHost.getCurrentTab()) {
			case 0:
				mAlbumController.onCreateOptionsMenu(menu);
				break;
			case 1:
				mArtistController.onCreateOptionsMenu(menu);
				break;
			case 2:
				mGenreController.onCreateOptionsMenu(menu);
				break;
			case 3:
				mCompilationsController.onCreateOptionsMenu(menu);
				break;
			case 4:
				mFileController.onCreateOptionsMenu(menu);
				break;
		}
		menu.add(0, MENU_UPDATE_LIBRARY, 0, "Update Library").setIcon(R.drawable.menu_refresh);
		menu.add(0, MENU_REMOTE, 0, "Remote control").setIcon(R.drawable.menu_remote);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// first, process individual menu events
		switch (mTabHost.getCurrentTab()) {
		case 0:
			mAlbumController.onOptionsItemSelected(item);
			break;
		case 1:
			mArtistController.onOptionsItemSelected(item);
			break;
		case 2:
			mGenreController.onOptionsItemSelected(item);
			break;
		case 3:
			mCompilationsController.onOptionsItemSelected(item);
			break;
		case 4:
			mFileController.onOptionsItemSelected(item);
			break;
		}
		
		// then the generic ones.
		switch (item.getItemId()) {
		case MENU_REMOTE:
			startActivity(new Intent(this, RemoteActivity.class));
			return true;
		case MENU_UPDATE_LIBRARY:
			mAlbumController.updateLibrary();
			return true;
		case MENU_NOW_PLAYING:
			startActivity(new Intent(this,  NowPlayingActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		switch (mTabHost.getCurrentTab()) {
			case 0:
				mAlbumController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 1:
				mArtistController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 2:
				mGenreController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 3:
				mCompilationsController.onCreateContextMenu(menu, v, menuInfo);
				break;
			case 4:
				mFileController.onCreateContextMenu(menu, v, menuInfo);
				break;
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (mTabHost.getCurrentTab()) {
		case 0:
			mAlbumController.onContextItemSelected(item);
			break;
		case 1:
			mArtistController.onContextItemSelected(item);
			break;
		case 2:
			mGenreController.onContextItemSelected(item);
			break;
		case 3:
			mCompilationsController.onContextItemSelected(item);
			break;
		case 4:
			mFileController.onContextItemSelected(item);
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		IEventClientManager client = ManagerFactory.getEventClientManager(mAlbumController);
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
		return super.onKeyDown(keyCode, event);
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		mAlbumController.onActivityResume(this);
		mArtistController.onActivityResume(this);
		mGenreController.onActivityResume(this);
		mCompilationsController.onActivityResume(this);
		mFileController.onActivityResume(this);
		mConfigurationManager.onActivityResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mAlbumController.onActivityPause();
		mArtistController.onActivityPause();
		mGenreController.onActivityPause();
		mCompilationsController.onActivityPause();
		mFileController.onActivityPause();
		mConfigurationManager.onActivityPause();
	}
}

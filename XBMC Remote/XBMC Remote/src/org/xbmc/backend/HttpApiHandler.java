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

package org.xbmc.backend;

import org.xbmc.backend.httpapi.type.CacheType;

import android.app.Activity;

/**
 * Basically contains two things:
 * <ul>
 *   <li>The callback code of a completed HTTP API command</li>
 *   <li>The result of the HTTP API command</li>
 * </ul>
 * 
 * @author Team XBMC
 * @param <T> Type of the API command's result
 */
public abstract class HttpApiHandler<T> implements Runnable {
	public T value;
	protected final Activity mActivity;
	protected final long mTag;
	protected CacheType mCacheType;
	protected Exception mException = null;
	
	public HttpApiHandler(Activity activity) {
		mActivity = activity;
		mTag = 0;
	}
	public HttpApiHandler(Activity activity, long tag) {
		mActivity = activity;
		mTag = tag;
	}
	public void setCacheType(CacheType type) {
		mCacheType = type;
	}
	public Activity getActivity() {
		return mActivity;
	}
	
	public void setError(Exception e) {
		mException = e;
	}
	
	public void run () {
		if(mException != null) {	//TODO better way to check if there was an error
			onError(mException);
		}else{
			doFinish(value);
		}
		mException = null; // remove the stored exception so handler can be used multiple times
	}
	
	/**
	 * implement this to handle errors, otherwise nothing happens
	 * @param e the Exception happened on the async backend
	 */
	public abstract void onError(Exception e);	

	/**
	 * implement this to handle the finished backend call
	 */
	public abstract void doFinish(T value); 
	/**
	 * Executed before downloading large files. Overload and return false to 
	 * skip downloading, for instance when a list with covers is scrolling.
	 * @return
	 */
	public boolean postCache() {
		return true;
	}
}
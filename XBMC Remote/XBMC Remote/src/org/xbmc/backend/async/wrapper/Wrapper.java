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

package org.xbmc.backend.async.wrapper;

import org.xbmc.android.util.ConnectionManager;
import org.xbmc.backend.HttpApiHandler;
import org.xbmc.backend.exceptions.BackendException;
import org.xbmc.backend.httpapi.client.ControlClient;
import org.xbmc.backend.httpapi.client.InfoClient;
import org.xbmc.backend.httpapi.client.MusicClient;
import org.xbmc.backend.httpapi.client.VideoClient;

import android.os.Handler;

/**
 * Super class of the wrappers, keeps common code.
 * 
 * @author Team XBMC
 */
public abstract class Wrapper {
	
	protected Handler mHandler;
	
	/**
	 * Sets the handler used in the looping thread
	 * @param handler
	 */
	public void setHandler(Handler handler) {
		mHandler = handler;
	}
	
	/**
	 * Returns the InfoClient class
	 * @param handler
	 * @return
	 * @throws BackendException 
	 */
	protected InfoClient info(HttpApiHandler<?> handler) throws BackendException {
		return ConnectionManager.getHttpClient(handler.getActivity()).info;
	}
	
	/**
	 * Returns the ControlClient class
	 * @param handler
	 * @return
	 * @throws BackendException 
	 */
	protected ControlClient control(HttpApiHandler<?> handler) throws BackendException {
		return ConnectionManager.getHttpClient(handler.getActivity()).control;
	}
	
	/**
	 * Returns the VideoClient class
	 * @param handler
	 * @return
	 * @throws BackendException 
	 */
	protected VideoClient video(HttpApiHandler<?> handler) throws BackendException {
		return ConnectionManager.getHttpClient(handler.getActivity()).video;
	}
	
	/**
	 * Returns the MusicClient class
	 * @param handler
	 * @return
	 * @throws BackendException 
	 */
	protected MusicClient music(HttpApiHandler<?> handler) throws BackendException {
		return ConnectionManager.getHttpClient(handler.getActivity()).music;
	}
	
	/**
	 * Calls the UI thread's callback code.
	 * @param handler
	 */
	protected void done(HttpApiHandler<?> handler) {
		handler.getActivity().runOnUiThread(handler);
	}
	
	protected void handleError(HttpApiHandler<?> handler, Exception e) {
		handler.setError(e);
	}
	
}

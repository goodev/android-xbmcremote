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

import java.util.ArrayList;

import org.xbmc.backend.HttpApiHandler;
import org.xbmc.backend.exceptions.BackendException;
import org.xbmc.backend.httpapi.type.DirectoryMask;
import org.xbmc.backend.httpapi.type.MediaType;
import org.xbmc.model.MediaLocation;


/**
 * Asynchronously wraps the {@link org.xbmc.backend.httpapi.client.InfoClient} class.
 * 
 * @author Team XBMC
 */
public class InfoWrapper extends Wrapper {
	
	/**
	 * Returns any system info variable, see {@link org.xbmc.backend.httpapi.info.SystemInfo}
	 * @param handler Wrapped SystemInfo return value
	 * @param field Field to return
	 */
	public void getSystemInfo(final HttpApiHandler<String> handler, final int field) {
		mHandler.post(new Runnable() {
			public void run() { 
				try {
					handler.value = info(handler).getSystemInfo(field);
				} catch (BackendException e) {
					handleError(handler, e);
				}
				done(handler);
			}
		});
	}
	
	/**
	 * Returns all defined shares of a media type
	 * @param handler Wrapped list of media locations
	 * @param type Media type
	 */
	public void getShares(final HttpApiHandler<ArrayList<MediaLocation>> handler, final MediaType type) {
		mHandler.post(new Runnable() {
			public void run() { 
				try {
					handler.value = info(handler).getShares(type);
				} catch (BackendException e) {
					handleError(handler, e);
				}
				done(handler);
			}
		});
	}
	
	/**
	 * Returns the contents of a directory
	 * @param handler Wrapped list of media locations
	 * @param path    Path to the directory
	 * @param mask    Mask to filter
	 * @param offset  Offset (0 for none)
	 * @param limit   Limit (0 for none)
	 * @return
	 */
	public void getDirectory(final HttpApiHandler<ArrayList<MediaLocation>> handler, final String path, final DirectoryMask mask, final int offset, final int limit) {
		mHandler.post(new Runnable() {
			public void run() { 
				try {
					handler.value = info(handler).getDirectory(path, mask, offset, limit);
				} catch (BackendException e) {
					handleError(handler, e);
				}
				done(handler);
			}
		});
	}
	
	/**
	 * Returns the contents of a directory
	 * @param handler Wrapped list of media locations
	 * @param path    Path to the directory
	 * @return
	 */
	public void getDirectory(final HttpApiHandler<ArrayList<MediaLocation>> handler, final String path) {
		mHandler.post(new Runnable() {
			public void run() { 
				try {
					handler.value = info(handler).getDirectory(path);
				} catch (BackendException e) {
					handleError(handler, e);
				}
				done(handler);
			}
		});
	}

}

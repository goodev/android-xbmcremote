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

package com.remote.xbmc;

import java.util.List;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.*;

public class FileLister extends ListActivity {
/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  List<String> testList = XBMCControl.getInstance().getDirectory("/home/topfs/");
	  
	  setListAdapter(new ArrayAdapter<String>(this,
	          android.R.layout.simple_list_item_1, testList));
	  getListView().setTextFilterEnabled(true);
	}
}
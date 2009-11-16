package org.xbmc.backend;

import android.app.Activity;

public class HttpApiHandlerAdapter<T> extends HttpApiHandler<T> {

	public HttpApiHandlerAdapter(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	public HttpApiHandlerAdapter(Activity activity, long tag) {
		super(activity, tag);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doFinish(T value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Exception e) {
		// TODO Auto-generated method stub

	}

}

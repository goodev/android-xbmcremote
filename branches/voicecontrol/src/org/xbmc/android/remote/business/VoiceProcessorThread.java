package org.xbmc.android.remote.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;

import org.xbmc.android.util.EditDistance;
import org.xbmc.api.business.DataResponse;
import org.xbmc.api.business.ITvShowManager;
import org.xbmc.api.business.IVideoManager;
import org.xbmc.api.object.INamedResource;
import org.xbmc.api.object.Movie;
import org.xbmc.api.object.TvShow;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class VoiceProcessorThread extends Thread {
	private static final String TAG = "VoiceProcessorThread";
	
	public static final String RETURN_MESSAGE_RESULTS_KEY = "results";
	
	private final Context mContext;
	private final ITvShowManager mTvManager;
	private final IVideoManager mVideoManager;
	private final Handler mHandler;
	private final ArrayList<String> mVoiceResults;
	
	public VoiceProcessorThread(Context context, ArrayList<String> voiceResults, ITvShowManager tvManager, IVideoManager videoManager, Handler handler) {
		mContext = context;
		mTvManager = tvManager;
		mVideoManager = videoManager;
		mHandler = handler;
		
		mVoiceResults = new ArrayList<String>(voiceResults.size());
		Iterator<String> iter = voiceResults.iterator();
		while (iter.hasNext()) {
			String voiceResult = iter.next();
			voiceResult = scrub(voiceResult);
			if (!voiceResult.equals("")) {
				mVoiceResults.add(voiceResult);
			}
		}
	}
	
	private String scrub(String str) {
		return str.toUpperCase().replaceAll("[^A-Z0-9]*", "");
	}
	
	@Override
	public void run() {
		final PriorityBlockingQueue<Result> sortedResults = new PriorityBlockingQueue<Result>(mVoiceResults.size());
		final CountDownLatch signal = new CountDownLatch(2);
		
		mTvManager.getTvShows(new DataResponse<ArrayList<TvShow>>() {
			@Override
			public void run() {
				processVideos(value, sortedResults);
				signal.countDown();
			}
		}, mContext);
		mVideoManager.getMovies(new DataResponse<ArrayList<Movie>>() {
			@Override
			public void run() {
				processVideos(value, sortedResults);
				signal.countDown();
			}
		}, mContext);
		
		try {
			signal.await();
		} catch (InterruptedException e) {
			Log.e(TAG, "Interrupt occurred while waiting for the signal.", e);
			return;
		}
		
		// Now, we need to narrow down the list and return the top results.
		// If none of the results are better than an 80% match, don't return any of them.
		// If the top result is better than a 10% match AND is more than 5% better than the second result, return it.
		// Otherwise, return the top 5 results.
		final ArrayList<Serializable> returnedResults = new ArrayList<Serializable>();
		int size = sortedResults.size();
		if (size > 0) {
			Result firstResult = sortedResults.poll();
			returnedResults.add(firstResult.getObject());
			if (size > 1) {
				Result secondResult = sortedResults.poll();
				if (firstResult.getCost() > .10 || secondResult.getCost() - firstResult.getCost() < .05) {
					returnedResults.add(secondResult.getObject());
					size -= 2;
					for (int i = 0; i < size; i++) {
						returnedResults.add(sortedResults.poll().getObject());
					}
				}
			}
		}
		
		Message msg = mHandler.obtainMessage();
		Bundle data = new Bundle();
		data.putSerializable(RETURN_MESSAGE_RESULTS_KEY, returnedResults);
		msg.setData(data);
		if (Thread.interrupted()) {
			return;
		}
		mHandler.sendMessage(msg);
	}
	
	private void processVideos(ArrayList<? extends INamedResource> videos, PriorityBlockingQueue<Result> queue) {
		for (INamedResource video : videos) {
			double cost = 1.00;
			for (String voiceResult : mVoiceResults) {
				double tempCost = EditDistance.getLevenshteinDistance(voiceResult, scrub(video.getShortName()));
				cost = Math.min(cost, tempCost);
			}
			if (cost < .20) {
				queue.put(new Result(cost, (Serializable) video));
			}
		}
	}
	
	private static class Result implements Comparable<Result> {
		private final Double mCost;
		private final Serializable mObject;
		
		public Result(double cost, Serializable object) {
			mCost = cost;
			mObject = object;
		}
		
		public int compareTo(Result another) {
			return this.mCost.compareTo(another.mCost);
		}

		public double getCost() {
			return mCost;
		}

		public Serializable getObject() {
			return mObject;
		}
	}
}

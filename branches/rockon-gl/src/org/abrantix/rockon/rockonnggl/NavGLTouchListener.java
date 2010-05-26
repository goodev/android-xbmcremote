package org.abrantix.rockon.rockonnggl;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class NavGLTouchListener implements OnTouchListener {

	private final String TAG = "NavGLTouchListener";

	// int mItemDimension;

	private float mDownX = 0.f;
	private float mDownY = 0.f;
	private double mDownTimestamp = 0;

	private float lastX = 0.f;
	private float lastY = 0.f;
	private double lastTimestamp = 0;

	private float mScrollingSpeed = 0.f;

	private boolean mScrolling = false;
	private boolean mScrollingX = false;
	private boolean mScrollingY = false;
	private boolean mLongClick = false; // filter repeating longclick requests

	// RockOnCubeRenderer mRenderer = null;
	private RockOnRenderer mRenderer = null;
	private Handler mTimeoutHandler = null;

	private Handler mClickHandler = null;
	private Message mMsg;

	public void setRenderer(RockOnRenderer renderer) {
		this.mRenderer = (RockOnRenderer) renderer;
	}

	public void setTimeoutHandler(Handler timeoutHandler) {
		this.mTimeoutHandler = timeoutHandler;
		mTimeoutHandler.sendEmptyMessageDelayed(0, Constants.SCROLLING_RESET_TIMEOUT);
	}

	public void setClickHandler(Handler clickHandler) {
		mClickHandler = clickHandler;
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (!mClickHandler.hasMessages(0)) {
			// Log.i(TAG, "onTouch "+event.getAction());
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTimeoutHandler.removeMessages(0);
				mDownX = event.getX();
				mDownY = event.getY();
				mDownTimestamp = System.currentTimeMillis();
				lastTimestamp = mDownTimestamp;

				lastX = mDownX;
				lastY = mDownY;
				mScrollingSpeed = 0.f;

				// mItemDimension = mRenderer.getItemDimension();
				mRenderer.stopScrollOnTouch();
				mRenderer.saveRotationInitialPosition();
				mRenderer.renderNow();

				// AVOID EVENT FLOODING XXX
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//

				return true;
			case MotionEvent.ACTION_MOVE:
				if (!mScrolling) {
					/**
					 * LONG PRESS
					 */
					if (System.currentTimeMillis() - mDownTimestamp > Constants.MIN_LONG_CLICK_DURATION) {
						if (mClickHandler != null && !mLongClick && !mClickHandler.hasMessages(Constants.SINGLE_CLICK)
								&& !mClickHandler.hasMessages(Constants.LONG_CLICK)) {
							mClickHandler.removeCallbacksAndMessages(null);
							mMsg = new Message();
							mMsg.what = Constants.LONG_CLICK;
							mMsg.arg1 = (int) event.getX();
							mMsg.arg2 = (int) event.getY();
							mClickHandler.sendMessageDelayed(mMsg, mRenderer.getClickActionDelay());
							mRenderer.showClickAnimation(event.getX(), event.getY());
							mLongClick = true;
						}
						return true;
					}
					/**
					 * DID WE START MOVING?
					 */
					else {
						if (Math.abs(event.getY() - mDownY) > Constants.MIN_SCROLL_TOUCH_MOVE * v.getHeight() && !mRenderer.isSpinningX()) {
							mScrolling = true;
							mScrollingY = true;
						} else if (Math.abs(event.getX() - mDownX) > Constants.MIN_SCROLL_TOUCH_MOVE * v.getWidth() && !mRenderer.isSpinningY()) {
							mScrolling = true;
							mScrollingX = true;
						} else {
							// Log.i(TAG,
							// "not moving yet mDownX: "+mDownX+" X: "+event.getX()+" mDownY: "+mDownY+" Y: "+event.getY());
							return true;
						}
					}
				}

				/**
				 * VERTICAL MOVE
				 */
				if (mScrollingY) {
					mRenderer.scrollOnTouchMove(event.getY() - lastY, Constants.SCROLL_MODE_VERTICAL);

					mScrollingSpeed = (float) (-0.5 * (event.getY() - lastY) / (System.currentTimeMillis() - lastTimestamp) // /mItemDimension
					+ (1 - 0.5) * mScrollingSpeed);
				}
				/**
				 * HORIZONTAL MOVE
				 */
				else if (mScrollingX) {
					mRenderer.scrollOnTouchMove(event.getX() - lastX, Constants.SCROLL_MODE_HORIZONTAL);

					mScrollingSpeed = (float) (0.5 * (event.getX() - lastX) / (System.currentTimeMillis() - lastTimestamp) // /mItemDimension
					+ (1 - 0.5) * mScrollingSpeed);
				}

				// Log.i(TAG, "XXXXXXXXXXXXXXXXXXXX");
				// Log.i(TAG, "touch interval:" + (System.currentTimeMillis() -
				// lastTimestamp));
				// Log.i(TAG, "scroll speed:" + mScrollingSpeed);

				/**
				 * SAVE STATE
				 */
				lastY = event.getY();
				lastX = event.getX();
				lastTimestamp = System.currentTimeMillis();

				/**
				 * SHOW MOVEMENT
				 */
				mRenderer.renderNow();
				return true;
			case MotionEvent.ACTION_UP:

				/**
				 * WAS A CLICK
				 */
				if (!mScrolling &&
				// could also verify the Y axis but i like it how it goes back
				// while scrolling
						!mRenderer.isSpinningX()) {
					// we also check if the ymove is big enough
					// reason: when yscrolling the mscrolling is not recorded
					if (System.currentTimeMillis() - mDownTimestamp < Constants.MAX_CLICK_DOWNTIME && Math.abs(event.getY() - mDownY) < // condition
																																		// unnecessary?
							Constants.MIN_SCROLL_TOUCH_MOVE * v.getHeight()) {
						if (mClickHandler != null && !mLongClick && !mClickHandler.hasMessages(Constants.SINGLE_CLICK)
								&& !mClickHandler.hasMessages(Constants.LONG_CLICK)) {
							mClickHandler.removeCallbacksAndMessages(null);
							mMsg = new Message();
							mMsg.what = Constants.SINGLE_CLICK;
							mMsg.arg1 = (int) event.getX();
							mMsg.arg2 = (int) event.getY();
							mClickHandler.sendMessageDelayed(mMsg, mRenderer.getClickActionDelay());
							// mClickHandler.sendEmptyMessageDelayed(
							// Constants.SINGLE_CLICK,
							// mRenderer.getClickActionDelay());
							// Constants.CLICK_ACTION_DELAY);
							mRenderer.showClickAnimation(event.getX(), event.getY());
						}
						return false;
					}
				}

				/**
				 * RESET THE INACTIVITY TIMER
				 */
				mTimeoutHandler.sendEmptyMessageDelayed(0, Constants.SCROLLING_RESET_TIMEOUT);

				/**
				 * INERTIAL VERTICAL MOVE
				 */
				if (mScrollingY) {
					mScrollingSpeed *= .25f + (.75f * Math.abs(event.getY() - mDownY) / v.getHeight() * (v.getHeight() / 360.f));
					mRenderer.inertialScrollOnTouchEnd(mScrollingSpeed, Constants.SCROLL_MODE_VERTICAL);
				} else if (mScrollingX) {
					mScrollingSpeed *= .25f + (.75f * Math.abs(event.getX() - mDownX) / v.getWidth());
					mRenderer.inertialScrollOnTouchEnd(mScrollingSpeed, Constants.SCROLL_MODE_HORIZONTAL);
				}

				/**
				 * RESET STATE
				 */
				mScrolling = false;
				mScrollingX = false;
				mScrollingY = false;
				mLongClick = false;

				/**
				 * SHOW MOVEMENT
				 */
				mRenderer.renderNow();
				return true;
			}
			return false;
		} else {
			Log.i(TAG, "handler has messages!");
			return true;
		}
	}
}
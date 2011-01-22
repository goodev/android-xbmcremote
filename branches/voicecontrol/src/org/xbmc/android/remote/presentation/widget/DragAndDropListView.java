package org.xbmc.android.remote.presentation.widget;

import org.xbmc.android.remote.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class DragAndDropListView extends ListView {
	private boolean mDragMode;
	private int mStartPosition;
	private int mEndPosition;
	private int mDragPointOffset;
	
	private ImageView mDragView;
	
	private DragAndDropListener mListener;
	
	public DragAndDropListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setListener(DragAndDropListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		final int action = evt.getAction();
		final int x = (int) evt.getX();
		final int y = (int) evt.getY();
		
		int position = pointToPosition(x, y);
		if (action == MotionEvent.ACTION_DOWN) {
			if (position == AdapterView.INVALID_POSITION) {
				return true;
			}
			position = position - getFirstVisiblePosition();
			View itemView = getChildAt(position);
			if (itemView != null) {
				ImageView grabber = (ImageView) itemView.findViewById(R.id.grabber);
				if (grabber != null) {
					int[] grabberLocation = new int[] {-1, -1};
					grabber.getLocationOnScreen(grabberLocation);
					int grabberX = grabberLocation[0];
					int grabberWidth = grabber.getWidth();
					if (x >= grabberX && x <= (grabberX + grabberWidth)) {
						mDragMode = true;
					}
				}
			}
		}

		if (!mDragMode) 
			return super.onTouchEvent(evt);

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				mStartPosition = position + getFirstVisiblePosition();
				if (mStartPosition != INVALID_POSITION) {
					int itemPosition = mStartPosition - getFirstVisiblePosition();
					mDragPointOffset = 2*y - getChildAt(itemPosition).getTop() - (int)evt.getRawY();
					startDrag(itemPosition, y);
					drag(x, y);
				}	
				break;
			case MotionEvent.ACTION_MOVE:
				drag(x, y);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
			default:
				mDragMode = false;
				mEndPosition = position;
				stopDrag(mStartPosition - getFirstVisiblePosition());
				if (mListener != null && mStartPosition != INVALID_POSITION && mEndPosition != INVALID_POSITION) 
	        		 mListener.onDrop(mStartPosition, mEndPosition);
				break;
		}
		
		return true;
	}	
	
	private void drag(int x, int y) {
		WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams)mDragView.getLayoutParams();
		layoutParams.x = x;
		layoutParams.y = y - mDragPointOffset;
        WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.updateViewLayout(mDragView, layoutParams);
	}

	private void startDrag(int itemIndex, int y) {
		stopDrag(itemIndex);

		View item = getChildAt(itemIndex);
		if (item == null) return;
		item.setDrawingCacheEnabled(true);
		
        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
        
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = y - mDragPointOffset;

        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.windowAnimations = 0;
        
        Context context = getContext();
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);

        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(imageView, layoutParams);
        mDragView = imageView;
	}

	private void stopDrag(int itemIndex) {
		if (mDragView != null) {
            mDragView.setVisibility(GONE);
            WindowManager windowManager = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
	}

	public interface DragAndDropListener {
		void onDrop(int from, int to);
	}
}

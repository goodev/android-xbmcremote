package org.abrantix.rockon.rockonnggl;

import org.xbmc.api.type.ThumbSize;

public class Constants {

	/** Browse Categories **/
	public static final int BROWSECAT_ARTIST = 0;
	public static final int BROWSECAT_ALBUM = 1;

	/** Theme Types */
	public static final int THEME_NORMAL = 100; // these should not coincide
												// with the Renderer modes
	static final int THEME_HALFTONE = 101;
	static final int THEME_EARTHQUAKE = 102;

	/** Half Tone Theme */
	static final String THEME_HALF_TONE_FILE_EXT = ".halftone";

	/** Earthquake Theme */
	static final String THEME_EARTHQUAKE_FILE_EXT = ".earthquake";

	/** UI scrolling parameters */
	static final float SCROLL_SPEED_SMOOTHNESS = 2.5f; // as the fraction of the
														// overall animation
														// that should be
														// obtained (per second)
	static final float CPU_SMOOTHNESS = 0.1f; // as the fraction of the overall
												// animation that should be
												// obtained (per second)
	static final float MIN_SCROLL = 1.5f; // as the fraction of the cover size
											// (per second)
	// static final float MAX_SCROLL = 6.f; // as the fraction of the cover size
	// (per second)
	static final float MAX_SCROLL = 9.f; // as the fraction of the cover size
											// (per second)
	static final float SCROLL_SPEED_BOOST = 675.f;
	// static final float MAX_LOW_SPEED = .08f; // mScrollingSpeed...
	static final float MIN_SCROLL_TOUCH_MOVE = 0.08f;
	static final double MAX_CLICK_DOWNTIME = 1000;
	static final int MIN_LONG_CLICK_DURATION = 1000;
	static final int MAX_POSITION_OVERSHOOT = 1;
	static final int SCROLLING_RESET_TIMEOUT = 7500;

	/** CLICK MODES */
	public static final int SINGLE_CLICK = 0;
	static final int LONG_CLICK = 1;

	/** UI scrolling modes */
	static final int SCROLL_MODE_VERTICAL = 0;
	static final int SCROLL_MODE_HORIZONTAL = 1;

	/** UI general interaction parameters */
	public static final int CLICK_ACTION_DELAY = 250;

	/** Album Art Stuff */
	static final int REASONABLE_ALBUM_ART_SIZE = ThumbSize.MEDIUM;
	static final int ALBUM_ART_TEXTURE_SIZE = ThumbSize.MEDIUM;

	/** PLAY QUEUE SIZE WHEN NOT SPECIFICALLY DEFINED */

	static final String ROCKON_SMALL_ALBUM_ART_PATH = "/sdcard/albumthumbs/RockOnNg/small/";
	static final String ROCKON_UNKNOWN_ART_FILENAME = "_____unknown";
}
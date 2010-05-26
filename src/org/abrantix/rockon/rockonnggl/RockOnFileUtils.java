package org.abrantix.rockon.rockonnggl;

class RockOnFileUtils {

	/**
	 * Constructor
	 */
	RockOnFileUtils() {

	}

	/**
	 * sanitize the filename
	 * 
	 * @param fileName
	 * @return
	 */
	static String validateFileName(String fileName) {
		if (fileName == null)
			return null;
		fileName = fileName.replace('/', '_');
		fileName = fileName.replace('<', '_');
		fileName = fileName.replace('>', '_');
		fileName = fileName.replace(':', '_');
		fileName = fileName.replace('\'', '_');
		fileName = fileName.replace('?', '_');
		fileName = fileName.replace('"', '_');
		fileName = fileName.replace('|', '_');
		fileName = fileName.replace('(', '_');
		fileName = fileName.replace(')', '_');
		fileName = fileName.replace('[', '_');
		fileName = fileName.replace(']', '_');
		fileName = fileName.replaceAll("%", "");
		return fileName;
	}

}
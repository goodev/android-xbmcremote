package org.xbmc.android.util;

public class EditDistance {
	/**
	 * Returns the Levenshtein Distance as a percentage of the first string.
	 * 
	 * @param str1 the first string
	 * @param str2 the second string
	 * @return the edit distance between them (the lower the better)
	 */
	public static double getLevenshteinDistance(String str1, String str2) {
		int a = str1.length();
		int b = str2.length();
		
		if (str1.equals("") || str2.equals("")) return Double.MAX_VALUE;
		if (str1.contains(str2) && b > 5) return 0.0;
		if (str2.contains(str1) && a > 5) return 0.0;
		
		if (a == 0) return 0.0;
		
		int[][] distance = new int[a + 1][b + 1];
		 
		for (int i = 0; i <= a; i++)
			distance[i][0] = i;
		for (int j = 0; j <= b; j++)
			distance[0][j] = j;
 
		for (int i = 1; i <= a; i++)
			for (int j = 1; j <= b; j++)
				distance[i][j] = Math.min(Math.min(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1),
						distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));
 
		double d = (double)distance[a][b];
		return d/((double)a);
	}
}

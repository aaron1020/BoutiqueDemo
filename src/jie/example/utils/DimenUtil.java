package jie.example.utils;

import jie.example.constant.Constant;

public class DimenUtil {

	public static float convertDipToPx(float dipValue) {
		return dipValue * Constant.screenDensity + 0.5f;
	}

}

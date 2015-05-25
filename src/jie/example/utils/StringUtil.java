package jie.example.utils;

public class StringUtil {

	/**
	 * 判断字符串不为空
	 * 
	 * @param stringValue
	 *            需要判断的字符串
	 * @return boolean：如果不为空，返回true；反之，返回false。
	 */
	public static boolean isNotEmpty(String stringValue) {
		if (stringValue != null && !"".equals(stringValue.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param originalString
	 *            需要在前面添加零的字符串
	 * @param expectStringToLen
	 *            期望字符串达到的长度
	 * @return String
	 */
	public static String addZeroBefortText(String originalString,
			final int expectStringToLen) {
		StringBuffer stringBuffer = new StringBuffer(originalString.trim());
		while (stringBuffer.length() < expectStringToLen) {
			stringBuffer.insert(0, "0");
		}
		return stringBuffer.toString();
	}

}

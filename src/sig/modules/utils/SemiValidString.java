package sig.modules.utils;

import java.util.Arrays;

import sig.utils.TextUtils;

public class SemiValidString {
	final public static String ERROR_VALUE = "nil";
	String[] values;
	
	public SemiValidString(String[] vals) {
		this.values = vals;
	}
	
	public String getValidString() {
		for (String val : values) {
			if (passesTestConditions(val)) {
				return val;
			}
		}
		System.out.println("WARNING! Could not find valid value for SemiValidString["+values.length+"]!");
		return ERROR_VALUE;
	}

	public boolean passesTestConditions(String testval) {
		return !(testval.equalsIgnoreCase("nil") || TextUtils.isNumeric(testval));
	}
	
	public String toString() {
		return "SemiValidString "+Arrays.toString(values);
	}
}

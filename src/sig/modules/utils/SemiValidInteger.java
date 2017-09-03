package sig.modules.utils;

import java.util.Arrays;

import sig.utils.TextUtils;

public class SemiValidInteger {
	final public static int ERROR_VALUE = Integer.MIN_VALUE;
	String[] values;
	int bossHP=Integer.MAX_VALUE;
	boolean initialized=true;
	int trustedslot = -1;
	
	public SemiValidInteger(String[] vals) {
		this.values = vals;
		ConvertNegativeValues(vals);
	}
	
	public SemiValidInteger(String[] vals, Integer bossHP, boolean initialized) {
		this.bossHP=bossHP;
		this.values=vals;
		this.initialized=initialized;
		ConvertNegativeValues(vals);
	}
	
	public SemiValidInteger(String[] vals, Integer bossHP, boolean initialized, int trustedslot) {
		this.bossHP=bossHP;
		this.values=vals;
		this.initialized=initialized;
		this.trustedslot=trustedslot;
		ConvertNegativeValues(vals);
	}

	private void ConvertNegativeValues(String[] vals) {
		for (int i=0;i<vals.length;i++) {
			if (TextUtils.isNumeric(vals[i])) {
				double d = Double.parseDouble(vals[i]);
				if (d>Integer.MAX_VALUE) {
					System.out.println("Double: "+d+" Val:"+vals[i]);
					vals[i] = Integer.toString((int)(d-((double)Integer.MAX_VALUE*2))-2);
				}
			}
		}
		//System.out.print("Test values: "+Arrays.toString(vals));
	}

	public int getValidInteger() {
		if (initialized && trustedslot!=-1) {
			if (TextUtils.isNumeric(values[trustedslot]) && TextUtils.isInteger(values[trustedslot], 10) && values[trustedslot].length()<Integer.toString(Integer.MAX_VALUE).length()) {
				int testnumb = Integer.parseInt(values[trustedslot]);
				if (passesTestConditions(testnumb)) {
					return testnumb;
				}
			}
		} else {
			for (int i=0;i<values.length;i++) {
				if (TextUtils.isNumeric(values[i]) && TextUtils.isInteger(values[i], 10) && values[i].length()<Integer.toString(Integer.MAX_VALUE).length()) {
					int testnumb = Integer.parseInt(values[i]);
					if (passesTestConditions(testnumb)) {
						trustedslot = i;
						return testnumb;
					}
				}
			}
		}
		//System.out.println("WARNING! Could not find valid value for SemiValidInteger["+values.length+"]!");
		return ERROR_VALUE;
	}
	
	public int getTrustedSlot() {
		return trustedslot;
	}

	public boolean passesTestConditions(int testnumb) {
		return testnumb!=0 && testnumb<1000000 && (((!initialized && testnumb==bossHP) || (initialized && testnumb<=bossHP+7000)) || bossHP==Integer.MAX_VALUE);
	}
	
	public String toString() {
		return "SemiValidInteger "+Arrays.toString(values);
	}
}

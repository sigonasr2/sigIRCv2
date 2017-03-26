package sig.modules.utils;

import java.util.Arrays;

import sig.TextUtils;

public class SemiValidInteger {
	final public static int ERROR_VALUE = Integer.MIN_VALUE;
	String[] values;
	int bossHP=Integer.MAX_VALUE;
	boolean initialized=true;
	int trustedslot = -1;
	
	public SemiValidInteger(String[] vals) {
		this.values = vals;
	}
	
	public SemiValidInteger(String[] vals, Integer bossHP, boolean initialized) {
		this.bossHP=bossHP;
		this.values=vals;
		this.initialized=initialized;
	}
	
	public SemiValidInteger(String[] vals, Integer bossHP, boolean initialized, int trustedslot) {
		this.bossHP=bossHP;
		this.values=vals;
		this.initialized=initialized;
		this.trustedslot=trustedslot;
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
		System.out.println("WARNING! Could not find valid value for SemiValidInteger["+values.length+"]!");
		return ERROR_VALUE;
	}
	
	public int getTrustedSlot() {
		return trustedslot;
	}

	public boolean passesTestConditions(int testnumb) {
		return testnumb!=0 && testnumb<1000000 && (((!initialized && testnumb==bossHP) || (initialized && testnumb<=bossHP)) || bossHP==Integer.MAX_VALUE);
	}
	
	public String toString() {
		return "SemiValidInteger "+Arrays.toString(values);
	}
}

package sig.utils;

import java.lang.reflect.Field;

public class ReflectUtils {
	public static boolean isCloneable(Field f) {
		int mods = f.getModifiers();
		return mods<8;
	}
}

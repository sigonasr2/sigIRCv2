package sig.modules.RabiRibi;

import sig.modules.RabiRibiModule;

public class RabiUtils {
	public static RabiRibiModule module;
	
	public static boolean isGamePaused() {
		//return module.readIntFromMemory(MemoryOffset.TRANSITION_COUNTER)>=300;
		//TODO Detect when game is paused.
		return false;
	}
}

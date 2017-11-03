package sig.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;

public class MemoryUtils {
	/**
	 * Enables debug privileges for this process, required for OpenProcess() to
	 * get processes other than the current user
	 */
	public static void enableDebugPrivilege() {
	    HANDLEByReference hToken = new HANDLEByReference();
	    boolean success = Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(),
	            WinNT.TOKEN_QUERY | WinNT.TOKEN_ADJUST_PRIVILEGES, hToken);
	    if (!success) {
	        System.out.println("OpenProcessToken failed. Error: {}" + Native.getLastError());
	        return;
	    }
	    WinNT.LUID luid = new WinNT.LUID();
	    success = Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_DEBUG_NAME, luid);
	    if (!success) {
	    	System.out.println("LookupprivilegeValue failed. Error: {}" + Native.getLastError());
	        return;
	    }
	    WinNT.TOKEN_PRIVILEGES tkp = new WinNT.TOKEN_PRIVILEGES(1);
	    tkp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new DWORD(WinNT.SE_PRIVILEGE_ENABLED));
	    success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tkp, 0, null, null);
	    if (!success) {
	    	System.out.println("AdjustTokenPrivileges failed. Error: {}" + Native.getLastError());
	    }
	    Kernel32.INSTANCE.CloseHandle(hToken.getValue());
	}
}

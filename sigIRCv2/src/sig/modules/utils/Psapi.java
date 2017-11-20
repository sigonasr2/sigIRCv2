package sig.modules.utils;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Psapi extends StdCallLibrary{
    Psapi INSTANCE = (Psapi) Native.loadLibrary("Psapi", Psapi.class);

    /*
     * http://msdn.microsoft.com/en-us/library/ms682629(VS.85).aspx
     */
    boolean EnumProcesses(int[] pProcessIds, int cb, IntByReference pBytesReturned);
    
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms682631(VS.85).aspx
     */
    boolean EnumProcessModules(HANDLE hProcess, HMODULE[] lphModule, int cb, IntByReference lpcbNeededs);
    
    boolean EnumProcessModulesEx(HANDLE hProcess, HMODULE[] lphModule, int cb, IntByReference lpcbNeededs, int flags);

    
    /*
     * http://msdn.microsoft.com/en-us/library/ms683198(VS.85).aspx
     */
    int GetModuleFileNameExA(HANDLE hProcess, HMODULE hModule, byte[] lpImageFileName, int nSize);
    
    int GetModuleBaseNameA(HANDLE hProcess, HMODULE hModule, byte[] lpImageFileName, int nSize);

    
    /*
     * http://msdn.microsoft.com/en-us/library/ms684229(VS.85).aspx
     */
    public static class LPMODULEINFO extends Structure {
            public HANDLE lpBaseOfDll;
            public int  SizeOfImage;
            public HANDLE EntryPoint;
			@Override
			protected List getFieldOrder() {
				return Arrays.asList(new String[] { "lpBaseOfDll", "SizeOfImage", "EntryPoint"});
			}
}
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms683201(VS.85).aspx
     */
    boolean GetModuleInformation(HANDLE hProcess, HMODULE hModule, LPMODULEINFO lpmodinfo, int cb);

    
}

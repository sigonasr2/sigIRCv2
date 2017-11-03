package sig.modules.utils;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.ptr.IntByReference;

public interface MyKernel32 extends Kernel32 {
	final Kernel32 INSTANCE = (Kernel32) Native.loadLibrary ("kernel32", Kernel32.class);
	
//    BOOL WINAPI WriteProcessMemory(  
//    __in   HANDLE hProcess,  
//    __in   LPVOID lpBaseAddress,  
//    __in   LPCVOID lpBuffer,  
//    __in   SIZE_T nSize,  
//    __out  SIZE_T *lpNumberOfBytesWritten  
//    );  
    boolean WriteProcessMemory(HANDLE p, int address, HANDLE buffer, int size, IntByReference written);  
     
     
//    BOOL WINAPI ReadProcessMemory(  
//              __in   HANDLE hProcess,  
//              __in   LPCVOID lpBaseAddress,  
//              __out  LPVOID lpBuffer,  
//              __in   SIZE_T nSize,  
//              __out  SIZE_T *lpNumberOfBytesRead  
//            );  
    boolean ReadProcessMemory(HANDLE hProcess, int inBaseAddress, Memory outputBuffer, int nSize, IntByReference outNumberOfBytesRead);  
     
     
//    HANDLE WINAPI OpenProcess(  
//      __in  DWORD dwDesiredAccess,  
//      __in  BOOL bInheritHandle,  
//      __in  DWORD dwProcessId  
//    );  
    HANDLE OpenProcess(int desired, boolean inherit, int pid);  
     
    
//    BOOL WINAPI EnumProcessModules(
//    		  _In_   HANDLE hProcess,
//    		  _Out_  HMODULE *lphModule,
//    		  _In_   DWORD cb,
//    		  _Out_  LPDWORD lpcbNeeded
//    );
    boolean EnumProcessModules(HANDLE hProcess, HMODULE lphModule, int cb, int lpcbNeeded);
    
    
//    DWORD WINAPI GetModuleFileName(
//    		  _In_opt_  HMODULE hModule,
//    		  _Out_     LPTSTR lpFilename,
//    		  _In_      DWORD nSize
//    );

    int GetModuleFileName(HMODULE hModule, String lpFilename, int size);
    
//    DWORD WINAPI GetModuleFileNameEx(
//    		  _In_      HANDLE hProcess,
//    		  _In_opt_  HMODULE hModule,
//    		  _Out_     LPTSTR lpFilename,
//    		  _In_      DWORD nSize
//    );
    
    
//    BOOL WINAPI GetModuleHandleEx(
//    		  _In_      DWORD dwFlags,
//    		  _In_opt_  LPCTSTR lpModuleName,
//    		  _Out_     HMODULE *phModule
//    );

    int GetModuleHandleExA(int permissions, String lpFilename, HMODULE module);
    
//    BOOL WINAPI EnumProcesses(
//    		  _Out_  DWORD *pProcessIds,
//    		  _In_   DWORD cb,
//    		  _Out_  DWORD *pBytesReturned
//    );

    boolean EnumProcesses(int[] processIds, int cb, int bytesReturned);
    
    int GetLastError();  
}
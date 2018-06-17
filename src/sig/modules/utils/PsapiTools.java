package sig.modules.utils;

import java.util.LinkedList;
import java.util.List;

import sig.modules.utils.Psapi.LPMODULEINFO;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public class PsapiTools {
    private static PsapiTools  INSTANCE=null;
    private static Psapi psapi = Psapi.INSTANCE;
    private static Kernel32 k32 = MyKernel32.INSTANCE;

    private PsapiTools(){}
    
    public static PsapiTools getInstance(){
            if (INSTANCE==null)
                    INSTANCE=new PsapiTools();
            return INSTANCE;
    }       

    
    public List<Integer> enumProcesses() throws Exception{
            List<Integer> list = new LinkedList<Integer>();
            
            int[] pProcessIds = new int[1024];
            IntByReference pBytesReturned = new IntByReference(); 
            boolean success = psapi.EnumProcesses(pProcessIds, pProcessIds.length*Integer.SIZE/8, pBytesReturned); 
            if (!success){
		        int err=k32.GetLastError();
		        throw new Exception("EnumProcesses failed. Error: "+err);
		    }
    
            int size = (pBytesReturned.getValue()/(Integer.SIZE/8)); 
            for (int i=0;i<size;i++)
                    list.add(pProcessIds[i]);
            
            return list;
    }
     
    public List<Module> EnumProcessModules(HANDLE hProcess) throws Exception{
            List<Module> list = new LinkedList<Module>();
            
            HMODULE[] lphModule = new HMODULE[1024];
            IntByReference lpcbNeededs= new IntByReference();
            boolean success = psapi.EnumProcessModules(hProcess, lphModule, lphModule.length, lpcbNeededs);
            if (!success){
	            int err=k32.GetLastError();
	            if (err!=6 && err!=299) {
	            	throw new Exception("EnumProcessModules failed. Error: "+err);
	            }
            }
            for (int i = 0; i < lpcbNeededs.getValue()/4; i++) {
                    list.add(new Module(hProcess, lphModule[i]));
            }
            
            return list;
    }
    
    public List<Module> EnumProcessModulesEx(HANDLE hProcess, int flags) throws Exception{
        List<Module> list = new LinkedList<Module>();
        
        HMODULE[] lphModule = new HMODULE[1024];
        IntByReference lpcbNeededs= new IntByReference();
        boolean success = psapi.EnumProcessModulesEx(hProcess, lphModule, lphModule.length, lpcbNeededs, flags);
        if (!success){
            int err=k32.GetLastError();
            throw new Exception("EnumProcessModules failed. Error: "+err);
        }
        for (int i = 0; i < lpcbNeededs.getValue()/4; i++) {
                list.add(new Module(hProcess, lphModule[i]));
        }
        
        return list;
}
    
    public String GetModuleFileNameExA(HANDLE hProcess, HMODULE hModule){
            byte[] lpImageFileName= new byte[256];
            psapi.GetModuleFileNameExA(hProcess, hModule, lpImageFileName, 256);
            return Native.toString(lpImageFileName);
    }
    
    public String GetModuleBaseNameA(HANDLE hProcess, HMODULE hModule){
        byte[] lpImageFileName= new byte[256];
        psapi.GetModuleBaseNameA(hProcess, hModule, lpImageFileName, 256);
        return Native.toString(lpImageFileName);
}
    
    public LPMODULEINFO GetModuleInformation(HANDLE hProcess, HMODULE hModule) throws Exception{
            LPMODULEINFO lpmodinfo = new LPMODULEINFO();
            
            boolean success = psapi.GetModuleInformation(hProcess, hModule, lpmodinfo, lpmodinfo.size());
            if (!success){
            int err=k32.GetLastError();
        throw new Exception("GetModuleInformation failed. Error: "+err);
    }
            return lpmodinfo;
    }

}

package sig.modules.utils;

import sig.modules.utils.Psapi.LPMODULEINFO;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class Module {
	private HANDLE hProcess;
	private HMODULE hModule;
	private HANDLE lpBaseOfDll = null;
	private int SizeOfImage = 0;
	private HANDLE EntryPoint = null;

	private PsapiTools psapi = PsapiTools.getInstance();

	protected Module() {
	}

	public Module(HANDLE hProcess, HMODULE hModule) {
		this.hProcess = hProcess;
		this.hModule = hModule;
	}

	public HMODULE getPointer() {
		return hModule;
	}

	public String getFileName() {
		return psapi.GetModuleFileNameExA(hProcess, hModule);
	}
	
	public String getBaseName() {
		return psapi.GetModuleBaseNameA(hProcess, hModule);
	}

	private void GetModuleInformation() {
		if (lpBaseOfDll == null) {
			try {
				LPMODULEINFO x = psapi.GetModuleInformation(hProcess, hModule);
				lpBaseOfDll = x.lpBaseOfDll;
				SizeOfImage = x.SizeOfImage;
				EntryPoint = x.EntryPoint;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HANDLE getLpBaseOfDll() {
		GetModuleInformation();
		return lpBaseOfDll;
	}

	public int getSizeOfImage() {
		GetModuleInformation();
		return SizeOfImage;
	}

	public HANDLE getEntryPoint() {
		GetModuleInformation();
		return EntryPoint;
	}

}

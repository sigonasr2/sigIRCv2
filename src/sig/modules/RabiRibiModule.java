package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import sig.Module;
import sig.sigIRC;
import sig.modules.RabiRibi.MemoryOffset;
import sig.modules.utils.PsapiTools;
import sig.utils.DrawUtils;

public class RabiRibiModule extends Module{
	final int PROCESS_PERMISSIONS = WinNT.PROCESS_QUERY_INFORMATION | WinNT.PROCESS_VM_READ;
	boolean foundRabiRibi = false;
	int rabiRibiPID = -1;
	long rabiRibiMemOffset = 0;
	HANDLE rabiribiProcess = null;
	
	public RabiRibiModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		//Initialize();
		Initialize();
		
		//System.out.println("Money value is: "+readIntFromMemory(MemoryOffset.MONEY));
	}

	private void Initialize() {
		List<Integer> pids;
		try {
			pids = PsapiTools.getInstance().enumProcesses();		
			for (Integer pid : pids) {
				HANDLE process = Kernel32.INSTANCE.OpenProcess(PROCESS_PERMISSIONS, true, pid);
		        List<sig.modules.utils.Module> hModules;
				try {
					hModules = PsapiTools.getInstance().EnumProcessModules(process);
					for(sig.modules.utils.Module m: hModules){
						//System.out.println(m.getFileName()+":"+m.getEntryPoint());
						if (m.getFileName().contains("rabiribi")) {
							rabiRibiMemOffset = Pointer.nativeValue(m.getLpBaseOfDll().getPointer());
							System.out.println("Found an instance of Rabi-Ribi at 0x"+Long.toHexString(rabiRibiMemOffset));
							rabiRibiPID=pid;
							foundRabiRibi=true;
							rabiribiProcess=process;
							break;
						}
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (foundRabiRibi) {
					break;
				}
				if (process!=null) {
					Kernel32.INSTANCE.CloseHandle(process);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.rabiribimodule_X=(int)position.getX();
		sigIRC.rabiribimodule_Y=(int)position.getY();
		sigIRC.config.setInteger("RABIRIBI_module_X", sigIRC.rabiribimodule_X);
		sigIRC.config.setInteger("RABIRIBI_module_Y", sigIRC.rabiribimodule_Y);
	}
	
	public void run() {
		super.run();
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		int i=32;
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "Money: "+readIntFromMemory(MemoryOffset.MONEY));
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "H-Ups: "+readItemCountFromMemory(MemoryOffset.HEALTHUP_START,MemoryOffset.HEALTHUP_END));
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "A-Ups: "+readItemCountFromMemory(MemoryOffset.ATTACKUP_START,MemoryOffset.ATTACKUP_END));
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "M-Ups: "+readItemCountFromMemory(MemoryOffset.MANAUP_START,MemoryOffset.MANAUP_END));
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "R-Ups: "+readItemCountFromMemory(MemoryOffset.REGENUP_START,MemoryOffset.REGENUP_END));
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, position.getX(), position.getY()+(i+=24), 3, Color.BLACK, Color.WHITE, "P-Ups: "+readItemCountFromMemory(MemoryOffset.PACKUP_START,MemoryOffset.PACKUP_END));
	}
	
	int readIntFromMemory(long offset) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(rabiRibiMemOffset+offset), mem, 4, null);
		return mem.getInt(0);
	}
	
	int readIntFromMemory(MemoryOffset val) {
		Memory mem = new Memory(4);
		Kernel32.INSTANCE.ReadProcessMemory(rabiribiProcess, new Pointer(rabiRibiMemOffset+val.getOffset()), mem, 4, null);
		return mem.getInt(0);
	}
	
	int readItemCountFromMemory(MemoryOffset start_range,
			MemoryOffset end_range) {
		int count=0;
		for (long i=start_range.getOffset();i<=end_range.getOffset();i++) {
			if (readIntFromMemory(i)==1) {
				count++;
			}
		}
		return count;
	}
}

package sig.modules.TouhouMother;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import javax.imageio.ImageIO;

import sig.MyPanel;
import sig.sigIRC;
import sig.modules.TouhouMotherModule;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class SwapButton extends TouhouMotherButton{
	final static String GAMEDIR = "D:/Documents/Games/Touhou Mother/Data/";
	
	boolean controlKeyPressed;
	
	String message = "";
	
	int displaytime=0;
	
	int slotselected=0;
	
	public SwapButton(TouhouMotherModule parentmodule, File filename, int x, int y) {
		super(parentmodule,filename,x,y);
	}
	
	public void run() {
		if (displaytime>0) {
			displaytime--;
		}
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		if (displaytime>0) {
			DrawUtils.drawOutlineText(g, MyPanel.smallFont, x+buttonimg.getWidth()+4, y+buttonimg.getHeight(), 2, Color.WHITE, new Color(60,0,150), message);
		}
	}
	
	public void onClickEvent(MouseEvent ev) {
		if (ev.getX()>=x && ev.getX()<=x+buttonimg.getWidth() &&
				ev.getY()>=y && ev.getY()<=y+buttonimg.getHeight()) {
			if (controlKeyPressed) {
				ReplaceFilesInSaveFolder(slotselected);
			} else {
				ReplaceFilesInGameFolder(slotselected);
			}
		}
	}

	public void keyPressEvent(KeyEvent ev) {
		switch (ev.getKeyCode()) {
			case KeyEvent.VK_CONTROL: {
				controlKeyPressed=true;
			}break;
			case KeyEvent.VK_NUMPAD1: {
				slotselected=1;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD2: {
				slotselected=2;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD3: {
				slotselected=3;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD4: {
				slotselected=4;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD5: {
				slotselected=5;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD6: {
				slotselected=6;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD7: {
				slotselected=7;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD8: {
				slotselected=8;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
			case KeyEvent.VK_NUMPAD9: {
				slotselected=9;
				TriggerNewMessage("Selected Memory Folder slot "+slotselected+".",30*5);
			}break;
		}
		//System.out.println("Pressing "+ev.getKeyCode());
	}
	
	public void ReplaceFilesInGameFolder(int foldernumb) {
		File dir = new File(GAMEDIR+"SAVES"+foldernumb); 
		if (dir.exists()) {
			String[] contents = dir.list();
			for (String s : contents) {
				if (IsValidTouhouMotherSaveFile(s)) {
					File existingfile = new File(GAMEDIR+"SAVES"+foldernumb+"/"+s);
					File newfile = new File(GAMEDIR+"/"+s);
					try {
						System.out.println("Copying file "+existingfile.getAbsolutePath()+" to "+newfile.getAbsolutePath());
						FileUtils.copyFile(existingfile, newfile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			TriggerNewMessage("Loaded Memory Folder Slot "+foldernumb+".",30*5);
		} else {
			System.out.println("WARNING! Could not find directory "+dir.getAbsolutePath()+"!");
		}
	}

	public boolean IsValidTouhouMotherSaveFile(String s) {
		return s.contains("Save") && s.contains(".lsd");
	}
	
	public void ReplaceFilesInSaveFolder(int foldernumb) {
		File dir = new File(GAMEDIR); 
		if (dir.exists()) {
			String[] contents = dir.list();
			for (String s : contents) {
				if (IsValidTouhouMotherSaveFile(s)) {
					File newfile = new File(GAMEDIR+"SAVES"+foldernumb+"/"+s);
					File existingfile = new File(GAMEDIR+"/"+s);
					try {
						System.out.println("Copying file "+existingfile.getAbsolutePath()+" to "+newfile.getAbsolutePath());
						FileUtils.copyFile(existingfile, newfile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			TriggerNewMessage("Saved memory set into Memory Folder Slot "+foldernumb+".",30*5);
		} else {
			System.out.println("WARNING! Could not find directory "+dir.getAbsolutePath()+"!");
		}
	}

	private void TriggerNewMessage(String string, int time) {
		message = string;
		displaytime = time;
	}

	public void keyReleaseEvent(KeyEvent ev) {
		if (ev.getKeyCode()==KeyEvent.VK_CONTROL) {
			controlKeyPressed=false;
		}
		//System.out.println("Released "+ev.getKeyCode());
	}
}

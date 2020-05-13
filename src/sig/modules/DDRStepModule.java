package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import sig.Module;
import sig.sigIRC;
import sig.modules.Controller.Controller;

public class DDRStepModule extends Module{
	public static DDRStepModule step_module;
	ControllerModule controller;
	Controller cont;
	AudioInputStream songStream,metronomeStream;
	Clip audioClip,metronomeClick;
	int bpm=180;
	int lastsystemtime = 0;
	int lastnotetime = 0;
	int offset = 0;
	int framecounter = 0;
	Image arrow_img1,arrow_img2;
	byte[] lastbuttonstate = new byte[]{0,0,0,0,0,0};
	boolean[] lastpressedstate = new boolean[]{false,false,false,false,false,false};
	List<Note> notelist = new ArrayList<Note>();
	boolean notepressed=false;
	int notespd = 256;
	
	public DDRStepModule(Rectangle2D bounds, String moduleName) {
		this(bounds,moduleName,true);
	}

	public DDRStepModule(Rectangle2D bounds, String moduleName, boolean enabled) {
		super(bounds, moduleName, enabled);
		Initialize();
	}
	
	void Initialize() {
		DDRStepModule.step_module = this;
		if (sigIRC.controllermodule_enabled) {
			try {
				metronomeStream = AudioSystem.getAudioInputStream(new File(sigIRC.BASEDIR+"sigIRC/sounds/tick.wav"));
				try {
					metronomeClick = AudioSystem.getClip();
					metronomeClick.open(metronomeStream);
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			controller = ControllerModule.controller_module;
			cont = controller.getControllers().get(0);
			try {
				arrow_img1 = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/ddr_notes1.png"));
				arrow_img2 = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/ddr_notes2.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Cannot initialize DDRStepModule due to Controller Module being disabled!");
			this.enabled = false;
		}
	}
	
	public static void loadModule() {
		sigIRC.modules.add(new DDRStepModule(
				new Rectangle(sigIRC.ddrstepmodule_X,sigIRC.ddrstepmodule_Y,sigIRC.ddrstepmodule_width,sigIRC.ddrstepmodule_height),
				"DDR Step"
				));
		sigIRC.ddrstepmodule_enabled=true;
		sigIRC.config.saveProperties();
	}
	public static void unloadModule() {
		for (int i=0;i<sigIRC.modules.size();i++) {
			if (sigIRC.modules.get(i) instanceof DDRStepModule) {
				sigIRC.modules.remove(sigIRC.modules.get(i));
			}
		}
		sigIRC.ddrstepmodule_enabled=false;
		sigIRC.config.saveProperties();
	}
	
	public void run() {
		super.run();
		
		if (cont!=null) {
			//System.out.println(cont.outputButtons());
			/*	Start = 1
				Select = 0
				Up = 5
				Right = 2
				Left = 4
				Down = 3
			 */


			//System.out.println((System.currentTimeMillis()-(1000/(bpm/60d)))+"/"+lastsystemtime);
			if (audioClip!=null &&
					audioClip.isRunning() && lastsystemtime<audioClip.getFramePosition()-(44100/(bpm/60d))) {
				//System.out.print("Tick.  ");
				lastsystemtime += (44100/(bpm/60d));
				//metronomeClick.loop(Clip.LOOP_CONTINUOUSLY);
				metronomeClick.stop();
				metronomeClick.setFramePosition(0);
				metronomeClick.start();
			} //METRONOME CODE.
			if (audioClip!=null &&
					audioClip.isRunning() && lastnotetime<audioClip.getFramePosition()-(44100/((bpm*2)/60d))) {
				//System.out.print("Tick.  ");
				lastnotetime += (44100/((bpm*2)/60d));
				for (int i=0;i<6;i++) {
					lastpressedstate[i]=false;
				}
			}
			if (audioClip!=null && audioClip.isRunning()) {
				framecounter++;
				System.out.println(audioClip.getFramePosition());
				//UP
				//44100Hz = 1 second
			}
			byte[] buttons = cont.getButtons();
			HandleButton(BUTTONDIR.UP,buttons);
			HandleButton(BUTTONDIR.DOWN,buttons);
			HandleButton(BUTTONDIR.LEFT,buttons);
			HandleButton(BUTTONDIR.RIGHT,buttons);
		}
	}
	
	void HandleButton(BUTTONDIR button, byte[] buttons) {
		if (lastbuttonstate[button.getButton()]==0 && buttons[button.getButton()]==1) {
			System.out.println(button+" Pressed"/* at "+audioClip.getFramePosition()*/);
			lastbuttonstate[button.getButton()] = buttons[button.getButton()];
			if (!lastpressedstate[button.button]) {
				lastpressedstate[button.button]=true;
				notelist.add(new Note(lastnotetime,button.getNote()));
			}
		} else
		if (lastbuttonstate[button.getButton()]==1 && buttons[button.getButton()]==0) {
			System.out.println(button+" Released"/* at "+audioClip.getFramePosition()*/);
			lastbuttonstate[button.getButton()] = buttons[button.getButton()];
		}
	}
	
	class Note{
		int framePosition;
		//(44100/(bpm/60d)) = Quarter Note Snapping
		//(44100/((bpm*2)/60d)) = Eighth Note Snapping
		NOTEPROPERTIES direction;
		
		Note(int framePosition, NOTEPROPERTIES direction) {
			this.framePosition = framePosition;
			this.direction = direction;
		}
	}
	
	enum NOTEPROPERTIES{
		UP(0,2),
		DOWN(-64,1),
		LEFT(-128,0),
		RIGHT(64,3);
		
		int xoffset,subimage;
		
		NOTEPROPERTIES(int xoffset,int subimage) {
			this.xoffset = xoffset;
			this.subimage = subimage;
		}
		
		int getXOffset() {
			return xoffset;
		}
		
		int getSubimage() {
			return subimage;
		}
	}
	
	enum BUTTONDIR{
		UP(5,NOTEPROPERTIES.UP),
		DOWN(3,NOTEPROPERTIES.DOWN),
		LEFT(4,NOTEPROPERTIES.LEFT),
		RIGHT(2,NOTEPROPERTIES.RIGHT);
		
		int button;
		NOTEPROPERTIES note;
		
		BUTTONDIR(int button, NOTEPROPERTIES note) {
			this.button = button;
			this.note = note;
		}
		
		int getButton() {
			return button;
		}
		NOTEPROPERTIES getNote() {
			return note;
		}
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		Point initialP = new Point((int)this.position.getCenterX()-arrow_img1.getWidth(sigIRC.window)/2,(int)this.position.getY()+(int)this.position.getHeight());
		g.drawImage(arrow_img1, (int)this.position.getCenterX()-arrow_img1.getWidth(sigIRC.window)/2, (int)this.position.getY()+(int)this.position.getHeight(), sigIRC.window);
		
		if (audioClip!=null && audioClip.isRunning()) {
			for (int i=notelist.size()-1;i>0;i--) {
				Note n = notelist.get(i);
				if ((int)(notespd*((audioClip.getFramePosition()-n.framePosition)/44100d))>this.position.getHeight()) {
					break;
				}
				g.setColor(Color.RED);
				g.drawImage(arrow_img2, initialP.x+128+n.direction.xoffset, initialP.y-(int)(notespd*((audioClip.getFramePosition()-n.framePosition)/44100d)), initialP.x+64+128+n.direction.xoffset, initialP.y+64-(int)(notespd*((audioClip.getFramePosition()-n.framePosition)/44100d)), n.direction.subimage*64, 0, n.direction.subimage*64+64, 64, new Color(0,0,0,0), sigIRC.window);
			}
		}
	}
	
	public void keypressed(KeyEvent ev) {
		super.keypressed(ev);
		if (ev.getKeyCode() == ev.VK_OPEN_BRACKET) {
			JFileChooser song_choice = new JFileChooser();
			int val = song_choice.showOpenDialog(sigIRC.window);
			if (val == JFileChooser.APPROVE_OPTION) {
				System.out.println("Opening file "+song_choice.getSelectedFile().getAbsolutePath());
			}
			JOptionPane bpm_pane = new JOptionPane();
			String input = bpm_pane.showInputDialog(sigIRC.window,"Input Song BPM:","180");
			if (input!=null && input.length()>0) {
				bpm = Integer.parseInt(input);
				System.out.println("BPM set to "+bpm);
				try {
					songStream = AudioSystem.getAudioInputStream(new File(song_choice.getSelectedFile().getAbsolutePath()));
					try {
						if (audioClip!=null && audioClip.isOpen()) {
							audioClip.close();
						}
						audioClip = AudioSystem.getClip();
						audioClip.open(songStream);
					} catch (LineUnavailableException e) {
						e.printStackTrace();
					}
				} catch (UnsupportedAudioFileException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			bpm_pane.showMessageDialog(sigIRC.window,"Press the RIGHT BRACKET button to begin playing.");
		} else
		if (ev.getKeyCode() == ev.VK_1) {
			if (audioClip.isOpen() && !audioClip.isRunning()) {
				audioClip.start();
				framecounter = audioClip.getFramePosition();
				System.out.println("Audio Clip Started.");
				lastsystemtime = audioClip.getFramePosition();
				lastnotetime = audioClip.getFramePosition();
			}
		} else
		if (ev.getKeyCode() == ev.VK_2) {
			if (audioClip.isOpen() && audioClip.isRunning()) {
				audioClip.stop();
				audioClip.setFramePosition(offset);
				System.out.println("Audio Clip Stopped.");
			}
		} else
		if (ev.getKeyCode() == ev.VK_3) {
			if (audioClip.isOpen() && audioClip.isRunning()) {
				offset = audioClip.getFramePosition();
				//audioClip.setMicrosecondPosition(offset);
				System.out.println("Set new Offset to "+offset);
			}
		} else
		if (ev.getKeyCode() == ev.VK_4) {
			if (audioClip.isOpen() && !audioClip.isRunning()) {
				framecounter = 0;
				audioClip.setFramePosition(0);
				System.out.println("Reset offset to 0.");
			}
		}
	}
}

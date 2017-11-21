package sig.modules;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Timer;

import sig.Module;
import sig.sigIRC;
import sig.modules.TouhouMother.DataProperty;
import sig.modules.TouhouMother.IncreaseTouhouMotherClockCount;
import sig.modules.TouhouMother.KillButton;
import sig.modules.TouhouMother.SwapButton;
import sig.modules.TouhouMother.TimeRecord;
import sig.modules.TouhouMother.TouhouMotherBossData;
import sig.modules.TouhouMother.TouhouMotherButton;
import sig.modules.TouhouMother.TouhouMotherCharacterData;
import sig.modules.TouhouMother.TouhouPlayerCharacter;
import sig.modules.TouhouMother.UpdateButton;
import sig.modules.utils.SemiValidInteger;
import sig.modules.utils.SemiValidString;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class TouhouMotherModule extends Module implements ActionListener{
	Timer filereadClock = new Timer(200,this);
	IncreaseTouhouMotherClockCount countev = new IncreaseTouhouMotherClockCount(this);
	Timer secondClock = new Timer(1000,countev);
	public int secondsCount = 0;
	String[] memory = new String[0];
	SemiValidInteger bossHP;
	SemiValidInteger bossID;
	SemiValidString gameData;
	int real_bossHP=SemiValidInteger.ERROR_VALUE;
	int real_bossID=SemiValidInteger.ERROR_VALUE;
	String real_gameData=SemiValidString.ERROR_VALUE;
	TouhouMotherBossData[] monsterDatabase;
	TouhouMotherCharacterData[] characterDatabase = new TouhouMotherCharacterData[4];
	
	public List<TimeRecord> recordDatabase = new ArrayList<TimeRecord>();
	
	int bossMaxHP=SemiValidInteger.ERROR_VALUE;
	TouhouMotherBossData currentBoss = null;
	Image bossImage = null;
	int lastCharacterAttacked=0;
	int lastBossHP=0;
	final int TIME_BETWEEN_DATA_DISPLAYS = 5 * 6;
	int data_display_toggle=0;
	int data_display_id=0;
	boolean hasDied=false;
	boolean battleEnds=false;
	
	boolean diamondSparkyMsg = false;
	
	List<TouhouMotherButton> moduleButtons = new ArrayList<TouhouMotherButton>();
	
	UpdateButton updateButton;
	KillButton killButton;
	SwapButton swapButton;

	public TouhouMotherModule(Rectangle2D bounds, String moduleName) {
		super(bounds, moduleName);
		PerformModuleInitialization();
	}
	
	public TouhouMotherModule(Rectangle2D bounds, String moduleName, boolean enabled) {
		super(bounds, moduleName, enabled);
		PerformModuleInitialization();
	}
	
	public void PerformModuleInitialization() {
		DefineMonsterDatabase();
		DefineCharacterDatabase();
		DisableTouhouMotherClockCount();
		PopulateRecordDatabase();
		DefineButton();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		memory = FileUtils.readFromFile(sigIRC.BASEDIR+"memory");
		//System.out.println(Arrays.toString(memory));
		if (memory.length>=14) {
			ProcessMemoryData();
			ValidateAndControlMonsterData();
		}
		data_display_toggle++;
		if (data_display_toggle>TIME_BETWEEN_DATA_DISPLAYS) {
			data_display_id=(data_display_id+1)%DataProperty.values().length;
			data_display_toggle=0;
		}
		swapButton.run();
		updateButton.run();
		killButton.run();
	}
	
	
	public void run() {
		EnableAndDisableTimer();
	}
	
	public void ModuleDragEvent(int oldX, int oldY, int newX, int newY) {
		for (TouhouMotherButton tmb : moduleButtons) {
			tmb.updatePosition(oldX,oldY,newX,newY);
		}
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.touhoumothermodule_X=(int)position.getX();
		sigIRC.touhoumothermodule_Y=(int)position.getY();
		sigIRC.config.setInteger("TOUHOUMOTHER_module_X", sigIRC.touhoumothermodule_X);
		sigIRC.config.setInteger("TOUHOUMOTHER_module_Y", sigIRC.touhoumothermodule_Y);
	}
	
	public void draw(Graphics g) {
		if (enabled) {
			super.draw(g);
			if (currentBoss!=null) {
				DrawBossAndPlayerInfo(g);
			} else {
				DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, (int)position.getX()+4, (int)position.getY()+4+16, 1, Color.WHITE, new Color(30,0,86,255), 
						DataProperty.getDataPropertyBasedOnID(data_display_id).getDisplayName());
				DrawSortedHealthbarsBasedOnDataProperty(g, DataProperty.getDataPropertyBasedOnID(data_display_id), 0, -64);
			}
			updateButton.draw(g);
			killButton.draw(g);
			swapButton.draw(g);
		}
	}
	
	public void DrawBossAndPlayerInfo(Graphics g) {
		g.drawImage(bossImage, (int)position.getX()+4, (int)position.getY()+4, sigIRC.panel);
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, Math.min(bossImage.getWidth(sigIRC.panel)+4,160)+(int)position.getX()+4, (int)position.getY()+4+16, 1, Color.WHITE, new Color(30,0,86,255), 
				currentBoss.getName());
		DrawUtils.drawOutlineText(g, sigIRC.panel.userFont, Math.min(bossImage.getWidth(sigIRC.panel)+4,160)+(int)position.getX()+4, (int)position.getY()+4+48, 1, Color.WHITE, new Color(30,0,86,255), 
				real_bossHP+" / "+bossMaxHP +" ("+Math.round(((real_bossHP/(double)bossMaxHP)*100))+"%)");
		DrawUtils.drawHealthbar(g, new Rectangle(
				Math.min(bossImage.getWidth(sigIRC.panel)+4,160)+(int)position.getX()+4,
				(int)position.getY()+4+20,
				(int)TextUtils.calculateStringBoundsFont(bossMaxHP+" / "+bossMaxHP +" ("+Math.round((1d*100))+"%", sigIRC.panel.userFont).getWidth(),
				8
				), real_bossHP/(double)bossMaxHP, ChooseHealthbarColor(real_bossHP/(double)bossMaxHP));
		DrawUtils.drawOutlineText(g, sigIRC.panel.userFont, Math.min(bossImage.getWidth(sigIRC.panel)+4,160)+(int)position.getX()+16, (int)position.getY()+4+68, 1, Color.WHITE, new Color(30,0,86,255), 
				TextUtils.convertSecondsToTimeFormat(secondsCount));
		int record = TimeRecord.getRecord(currentBoss.getID());
		if (record!=TimeRecord.ERROR_VALUE) {
			DrawUtils.drawOutlineText(g, sigIRC.panel.smallFont, Math.min(bossImage.getWidth(sigIRC.panel)+4,160)+(int)position.getX()+
					TextUtils.calculateStringBoundsFont(TextUtils.convertSecondsToTimeFormat(secondsCount), sigIRC.panel.userFont).getWidth()+20, 
					(int)position.getY()+4+72, 1, Color.WHITE, new Color(30,0,86,255), 
					"RECORD "+TextUtils.convertSecondsToTimeFormat(record));
		}
		DrawSortedHealthbarsBasedOnDataProperty(g, DataProperty.CURRENTDAMAGE, 0, 0);
	}
	
	public void DrawSortedHealthbarsBasedOnDataProperty(Graphics g, DataProperty property, int x, int y) {
		int pos = 0;
		int[] sorteddmg = new int[4];
		sorteddmg = SortByProperty(property);
		int maxdmg = calculateDataPropertyMaxValue(property);
		int totaldmg = calculateDataPropertyTotalValue(property);
		for (int i=0;i<sorteddmg.length;i++) {
			if (sorteddmg[i]!=-1 && characterDatabase[sorteddmg[i]].getDataProperty(property)>0) {
				DrawUtils.drawOutlineText(g, sigIRC.panel.userFont, Math.min(((bossImage!=null)?bossImage.getWidth(sigIRC.panel):0)+4,160)+(int)position.getX()+4-Math.min(50, (bossImage!=null)?bossImage.getWidth(sigIRC.panel):0)+x, (int)position.getY()+4+96+pos+y, 1, Color.WHITE, new Color(30,0,86,255), 
						characterDatabase[sorteddmg[i]].getName());
				DrawUtils.drawHealthbar(g, 
						new Rectangle(
								Math.min(((bossImage!=null)?bossImage.getWidth(sigIRC.panel):0)+4,160)+(int)position.getX()+4+Math.max(0, 50-((bossImage!=null)?bossImage.getWidth(sigIRC.panel):0))+x,
								(int)position.getY()+4+86+pos+y,
								96,
								10
								)
						, (double)characterDatabase[sorteddmg[i]].getDataProperty(property)/maxdmg, characterDatabase[sorteddmg[i]].getColor());
				DecimalFormat df = new DecimalFormat("0.0");
				DrawUtils.drawOutlineText(g, sigIRC.panel.smallFont, Math.min((bossImage!=null)?bossImage.getWidth(sigIRC.panel):0+4,160)+(int)position.getX()+4+Math.max(0, 50-((bossImage!=null)?bossImage.getWidth(sigIRC.panel):0))+108+x, (int)position.getY()+4+96+pos+y, 1, Color.WHITE, new Color(30,0,86,255), 
						characterDatabase[sorteddmg[i]].getDataProperty(property)+" "+"("+df.format(((((double)characterDatabase[sorteddmg[i]].getDataProperty(property)/totaldmg))*100))+"%)");
				pos+=16;
			}
		}
	}

	private int[] SortByProperty(DataProperty property) {
		List<TouhouMotherCharacterData> needs_to_be_sorted = new ArrayList<TouhouMotherCharacterData>();
		for (int i=0;i<characterDatabase.length;i++) {
			needs_to_be_sorted.add(characterDatabase[i].clone());
		}
		int highest_val = Integer.MIN_VALUE;
		int arraypos = -1;
		int[] orderedslots = new int[4];
		int filledslots = 0;
		//System.out.println("Start of sorting algorithm:");
		while (filledslots<4) {
			for (int i=0;i<needs_to_be_sorted.size();i++) {
				if (needs_to_be_sorted.get(i).getDataProperty(property)>highest_val) {
					highest_val = needs_to_be_sorted.get(i).getDataProperty(property);
					//System.out.println("Set highest_val to "+highest_val);
					arraypos=i;
				}
			}
			orderedslots[filledslots++] = arraypos;
			if (arraypos!=-1) {
				needs_to_be_sorted.get(arraypos).setDataProperty(property, Integer.MIN_VALUE);
			}
			arraypos=-1;
			highest_val = Integer.MIN_VALUE;
		}
		//System.out.println("End result of orderedslots: "+Arrays.toString(orderedslots));
		return orderedslots;
	}
	private int calculateDataPropertyMaxValue(DataProperty property) {
		int max = Integer.MIN_VALUE;
		for (TouhouMotherCharacterData tmcd : characterDatabase) {
			if (tmcd.getDataProperty(property)>max) {
				max = tmcd.getDataProperty(property);
			}
		}
		return max;
	}
	private int calculateDataPropertyTotalValue(DataProperty property) {
		int total = 0;
		for (TouhouMotherCharacterData tmcd : characterDatabase) {
			total += tmcd.getDataProperty(property);
		}
		return total;
	}
	private void PopulateRecordDatabase() {
		TimeRecord.tmm = this;
		TimeRecord.LoadRecordDatabase();
	}
	
	private Color ChooseHealthbarColor(double pct) {
		if (pct>=0.66) {
			 return new Color(64,168,64);
		} else
		if (pct>=0.33) {
			 return new Color(240,220,0);
		} else
			 return new Color(168,0,0);
	}
	private TouhouMotherBossData GetBossData(int bossID) {
		for (TouhouMotherBossData tmbd : monsterDatabase) {
			if (tmbd.getID()==bossID) {
				return tmbd;
			}
		}
		//System.out.println("Invalid boss ID specified! Could not find boss with ID "+bossID+"!");
		return null;
	}
	
	public void ProcessMemoryData() {
		bossID = new SemiValidInteger(Arrays.copyOfRange(memory, 14, memory.length-1));
		if (GetBossData(bossID.getValidInteger())!=null) {
			bossHP = new SemiValidInteger(Arrays.copyOfRange(memory, 0, 8),GetBossData(bossID.getValidInteger()).getHP(),currentBoss!=null,(bossHP!=null)?bossHP.getTrustedSlot():-1);
			gameData = new SemiValidString(Arrays.copyOfRange(memory, 9, 13));
			//System.out.println(bossHP.toString()+";"+bossID.toString()+";"+gameData.toString());
			real_bossHP = bossHP.getValidInteger();
			real_bossID = bossID.getValidInteger();
			real_gameData = gameData.getValidString();
		}
		if (memory.length>=14) {
			gameData = new SemiValidString(Arrays.copyOfRange(memory, 9, 13));
			real_gameData = gameData.getValidString();
		}
		System.out.print(real_gameData);
		if (real_gameData!=null && real_gameData.contains("sad thing that your adventures")) {
			hasDied=true;
		}
		if (real_gameData!=null && real_gameData.contains("Your SPARKY")) {
			diamondSparkyMsg=true;
		}
		if (real_gameData!=null && (real_gameData.contains("you should see...") ||
				real_gameData.contains("KA-75 fired its") || real_gameData.contains("The battle was lost")
				 || real_gameData.contains("Yukari tried"))) {
			battleEnds=true;
		}
	}
	
	public void endBattle() {
		battleEnds=true;
	}
	
	private int GetLastAttacker(String data) {
		if (data.contains("Reimu")) {
			return TouhouPlayerCharacter.REIMU.getID();
		} else
		if (data.contains("Marisa")) {
			return TouhouPlayerCharacter.MARISA.getID();
		} else
		if (data.contains("Yuka")) {
			return TouhouPlayerCharacter.YUUKA.getID();
		} else
		if (data.contains("Mima")) {
			return TouhouPlayerCharacter.MIMA.getID();
		} else 
		if ((data.contains("Nitori") || data.contains("Sanae") ||
				data.contains("Patchouli") || data.contains("Iku")
				 || data.contains("Alice")) && !data.contains("took")) {
			return -1;
		}
		return lastCharacterAttacked;
	}
	
	public void ValidateAndControlMonsterData() {
		InitializeBossData();
		HandleBattleStats();
	}
	public void HandleBattleStats() {
		if (currentBoss!=null) {
			if (real_bossHP>bossMaxHP) {
				real_bossHP=bossMaxHP;
			}
			KillBossData();
			if (currentBoss!=null) {
				if (lastBossHP==0) {
					lastBossHP=real_bossHP;
				} else {
					if (lastBossHP>real_bossHP) {
						int diff = lastBossHP - real_bossHP;
						if (lastCharacterAttacked>=0 && diff<=100000) {
							characterDatabase[lastCharacterAttacked].addCurrentDamage(diff);
							characterDatabase[lastCharacterAttacked].addTotalDamage(diff);
							characterDatabase[lastCharacterAttacked].addDamageTurns(1);
							characterDatabase[lastCharacterAttacked].setLargestHit(diff);
							lastCharacterAttacked=-1;
						}
						lastBossHP=real_bossHP;
					} else {
						if (real_bossHP>lastBossHP) { //This boss healed somehow.
							lastBossHP = real_bossHP;
						}
					}
				}
				lastCharacterAttacked = GetLastAttacker(real_gameData);
			}
		}
	}
	
	public void InitializeBossData() {
		if (real_bossHP!=SemiValidInteger.ERROR_VALUE &&
				currentBoss==null) {
			currentBoss = GetBossData(real_bossID);
			SetupBattleInfo();
		}
	}

	public void SetupBattleInfo() {
		if (currentBoss!=null) {
			bossMaxHP = currentBoss.getHP();
			secondsCount=0;
			secondClock.start();
			for (TouhouMotherCharacterData tmcd : characterDatabase) {
				tmcd.setCurrentDamage(0);
			}
			currentBoss.getFileManager().verifyAndFetchFileFromServer();
			bossImage = sigIRC.readImage(sigIRC.BASEDIR+"Boss Sprites/"+currentBoss.getImage());
		}
	}
	
	public void KillBossData() {
		if (((real_bossHP==SemiValidInteger.ERROR_VALUE || real_bossHP<0) &&
				currentBoss!=null && (currentBoss.getID()!=121 || (diamondSparkyMsg))) || hasDied || battleEnds) {
			if (bossImage!=null) {
				bossImage.flush();
			}
			int diff = lastBossHP;
			if (!hasDied) {
				if (!battleEnds) {
					if (lastCharacterAttacked>=0 && diff<=100000) {
						characterDatabase[lastCharacterAttacked].addCurrentDamage(diff);
						characterDatabase[lastCharacterAttacked].addTotalDamage(diff);
						characterDatabase[lastCharacterAttacked].addDamageTurns(1);
						characterDatabase[lastCharacterAttacked].setLargestHit(diff);
					}
					TimeRecord.setRecord(currentBoss.getID(), secondsCount);
					TimeRecord.SaveRecordDatabase();
				}
			} else {
				for (TouhouMotherCharacterData tmcd : characterDatabase) {
					tmcd.resetAllData();
				}
				hasDied=false;
			}
			battleEnds=false;
			if (IsSparky()) { //Sparky has its own rules.
				currentBoss = GetBossData(121);
				SetupBattleInfo();
			} else {
				bossMaxHP=SemiValidInteger.ERROR_VALUE;
				currentBoss=null;
				lastBossHP=0;
				diamondSparkyMsg=false;
			}
		}
	}

	public boolean IsSparky() {
		return currentBoss.getID()==120;
	}
	
	private void DefineCharacterDatabase() {
		characterDatabase[TouhouPlayerCharacter.REIMU.getID()] = new TouhouMotherCharacterData("Reimu",new Color(255,70,70));
		characterDatabase[TouhouPlayerCharacter.MARISA.getID()] = new TouhouMotherCharacterData("Marisa",new Color(255,200,70));
		characterDatabase[TouhouPlayerCharacter.YUUKA.getID()] = new TouhouMotherCharacterData("Yuuka",new Color(35,140,35));
		characterDatabase[TouhouPlayerCharacter.MIMA.getID()] = new TouhouMotherCharacterData("Mima",new Color(110,60,250));
	}
	
	private void DefineMonsterDatabase() {
		List<TouhouMotherBossData> monsterdata = new ArrayList<TouhouMotherBossData>();
		monsterdata.add(new TouhouMotherBossData("Cirno", 1, 500, "Cirno.png"));
		monsterdata.add(new TouhouMotherBossData("Starman Jr", 3, 888, "Starman_Junior.png"));
		monsterdata.add(new TouhouMotherBossData("Masked Maid Girl", 4, 1000, "Masked_Maid_Girl.png"));
		monsterdata.add(new TouhouMotherBossData("Youmu Shall Not Lose", 11, 2111, "Youmu_Never_Loses.png"));
		monsterdata.add(new TouhouMotherBossData("Alice (Sick)", 15, 3500, "TME_15.png"));
		monsterdata.add(new TouhouMotherBossData("Galbangor", 23, 4000, "TME_23.png"));
		monsterdata.add(new TouhouMotherBossData("Miss Iku", 24, 3000, "TME_24.png"));
		monsterdata.add(new TouhouMotherBossData("Starman EX", 33, 5558, "TME_33.png"));
		monsterdata.add(new TouhouMotherBossData("V-1969", 38, 8000, "TME_38.png"));
		monsterdata.add(new TouhouMotherBossData("Severed Head", 44, 7209, "TME_44.png"));
		monsterdata.add(new TouhouMotherBossData("Lady Shinki", 53, 10000, "TME_53.png"));
		monsterdata.add(new TouhouMotherBossData("Cirno", 63, 8888, "TME_63.png"));
		monsterdata.add(new TouhouMotherBossData("Strange Patchouli", 68, 10000, "TME_68.png"));
		monsterdata.add(new TouhouMotherBossData("DEATH", 73, 10000, "TME_73.png"));
		monsterdata.add(new TouhouMotherBossData("Count Remilia", 55, 16000, "TME_55.png"));
		monsterdata.add(new TouhouMotherBossData("Miss Sanae", 74, 6000, "TME_74.png"));
		monsterdata.add(new TouhouMotherBossData("Pitch Black Rumia", -100, 8888, "TME_-100.png"));
		monsterdata.add(new TouhouMotherBossData("Rinbokusan", 83, 10000, "TME_83.png"));
		monsterdata.add(new TouhouMotherBossData("New Udonge", -101, 8888, "TME_-101.png"));
		monsterdata.add(new TouhouMotherBossData("Roboster", -84, 9999, "TME_-84.png"));
		monsterdata.add(new TouhouMotherBossData("Ancestral Starman", 103, 9999, "TME_103.png"));
		monsterdata.add(new TouhouMotherBossData("Miss Yuugi", 104, 12345, "TME_104.png"));
		monsterdata.add(new TouhouMotherBossData("Sparky", 120, 8000, "TME_120.png"));
		monsterdata.add(new TouhouMotherBossData("Diamond Sparky", 121, 8000, "ENEMYDiamondMasahirorin.png"));
		monsterdata.add(new TouhouMotherBossData("Keine", 136, 10000, "TME_136.png"));
		monsterdata.add(new TouhouMotherBossData("Miracle Udon", 137, 6000, "TME_137.png"));
		monsterdata.add(new TouhouMotherBossData("a", -99, 26000, "TME_43.png"));
		monsterdata.add(new TouhouMotherBossData("Mima", -10000, 41, "TME_-10000.png"));
		monsterdata.add(new TouhouMotherBossData("Ness", 900000, 1500, "TME_900000.png"));
		monsterdata.add(new TouhouMotherBossData("General Pigmask", 202, 15000, "TME_202.png"));
		monsterdata.add(new TouhouMotherBossData("Proto-NKC", 204, 30000, "TME_204.png"));
		monsterdata.add(new TouhouMotherBossData("Tenshi", 999997, 30000, "TME_999997.png"));
		monsterdata.add(new TouhouMotherBossData("The Devil's Machine", 999998, 14000, "DevilMachine.png"));
		monsterdata.add(new TouhouMotherBossData("R-IN", 122, 999999, "TME_122.png"));
		monsterdata.add(new TouhouMotherBossData("S-IN", 138, 999999, "TME_138.png"));
		monsterdata.add(new TouhouMotherBossData("Heavy Kisume", 200, 999999, "TME_200.png"));
		monsterdata.add(new TouhouMotherBossData("KA-75", 203, 999999, "TME_203.png"));
		monsterdata.add(new TouhouMotherBossData("Gensokyo", 999999, 900000, "TME_999999.png"));
		monsterdata.add(new TouhouMotherBossData("Miss Satori", 108, 900000, "TME_108.png"));
		monsterdata.add(new TouhouMotherBossData("Only God", 48, 4010, "TME_48.png"));
		monsterDatabase = monsterdata.toArray(new TouhouMotherBossData[monsterdata.size()]);
	}
	
	/**
	 *  Controls the timer, by enabling or disabling it based on enabled state.
	 */
	public void EnableAndDisableTimer() {
		if (enabled) {
			if (!filereadClock.isRunning()) {
				filereadClock.start();
			}
		} else {
			if (filereadClock.isRunning()) {
				filereadClock.stop();
			}
		}
	}
	
	private void DisableTouhouMotherClockCount() {
		secondClock.setInitialDelay(1000);
		secondClock.stop();
	}
	
	public void mousePressed(MouseEvent ev) {
		updateButton.onClickEvent(ev);
		killButton.onClickEvent(ev);
		swapButton.onClickEvent(ev);
	}
	public void mouseWheel(MouseWheelEvent ev) {
		updateButton.onMouseWheelEvent(ev);
	}
	
	public void keypressed(KeyEvent ev) {
		swapButton.keyPressEvent(ev);
	}
	
	public void keyreleased(KeyEvent ev) {
		swapButton.keyReleaseEvent(ev);
	}
	
	private void DefineButton() {
		updateButton = new UpdateButton(this, //56x20 pixels
				new File(sigIRC.BASEDIR+"update.png"),
				(int)position.getX()+320-56,(int)position.getY()+sigIRC.panel.getHeight()/2-20);
		killButton = new KillButton(this,
				new File(sigIRC.BASEDIR+"kill.png"),
				(int)position.getX(),(int)position.getY()+sigIRC.panel.getHeight()/2-20);
		swapButton = new SwapButton(this,
				new File(sigIRC.BASEDIR+"swap.png"),
				(int)position.getX(),(int)position.getY()+sigIRC.panel.getHeight()/2-40);
		moduleButtons.add(updateButton);
		moduleButtons.add(killButton);
		moduleButtons.add(swapButton);
	}
	
	public Rectangle2D getBounds() {
		return position;
	}
}

package sig.modules.TouhouMother;

import java.awt.Color;

public class TouhouMotherCharacterData {
	String name;
	int dmg_current;
	int dmg_total;
	int dmg_turns;
	int largest_hit;
	Color color;
	
	public TouhouMotherCharacterData(String name, Color color) {
		this.name=name;
		this.dmg_current=0;
		this.dmg_total=0;
		this.dmg_turns=0;
		this.largest_hit=0;
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public int getCurrentDamage() {
		return dmg_current;
	}

	public void setCurrentDamage(int newdmg) {
		this.dmg_current = newdmg;
	}
	
	public void addCurrentDamage(int dmg) {
		this.dmg_current += dmg;
	}

	public int getTotalDamage() {
		return dmg_total;
	}

	public void setTotalDamage(int newdmg) {
		this.dmg_total = newdmg;
	}
	
	public void addTotalDamage(int dmg) {
		this.dmg_total += dmg;
	}

	public int getDamageTurns() {
		return dmg_turns;
	}

	public void setDamageTurns(int turns) {
		this.dmg_turns = turns;
	}
	
	public void addDamageTurns(int turns) {
		this.dmg_turns += turns;
	}
	
	public int getLargestHit() {
		return largest_hit;
	}
	
	public void setLargestHit(int dmg) {
		setLargestHit(dmg,false);
	}
	
	public void setLargestHit(int dmg, boolean force) {
		if (force || largest_hit<dmg) {
			largest_hit = dmg;
		}
	}

	public String getName() {
		return name;
	}
	
	public int getDataProperty(DataProperty property) {
		switch (property) {
			case CURRENTDAMAGE:{
				return getCurrentDamage();
			}
			case TOTALDAMAGE:{
				return getTotalDamage();
			}
			case DAMAGETURNS:{
				return getDamageTurns();
			}
			case LARGESTHIT:{
				return getLargestHit();
			}
			default:{
				return getCurrentDamage();
			}
		}
	}
	
	public void setDataProperty(DataProperty property, int val) {
		switch (property) {
			case CURRENTDAMAGE:{
				setCurrentDamage(val);
			}
			case TOTALDAMAGE:{
				setTotalDamage(val);
			}
			case DAMAGETURNS:{
				setDamageTurns(val);
			}
			case LARGESTHIT:{
				setLargestHit(val,true);
			}
			default:{
				setCurrentDamage(val);
			}
		}
	}
	
	public void resetAllData() {
		setCurrentDamage(0);
		setTotalDamage(0);
		setDamageTurns(0);
		setLargestHit(0,true);
	}
	
	public TouhouMotherCharacterData clone(){
		TouhouMotherCharacterData dataClone = new TouhouMotherCharacterData(name,color);
		dataClone.setCurrentDamage(getCurrentDamage());
		dataClone.setTotalDamage(getTotalDamage());
		dataClone.setDamageTurns(getDamageTurns());
		dataClone.setLargestHit(getLargestHit());
		return dataClone;
	}
}

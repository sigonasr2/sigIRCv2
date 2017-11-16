package sig.modules.RabiRace;

import java.util.HashMap;

import sig.modules.RabiRaceModule;

public class Profile {
	public int playtime = 0;
	public int healthUps = 0;
	public int attackUps = 0;
	public int manaUps = 0;
	public int regenUps = 0;
	public int packUps = 0;
	public int rainbowEggCount = 0;
	public boolean isPaused = false;
	public int difficulty = 0;
	public int loop = 0;
	public float itempct = 0;
	public float mappct = 0;
	public HashMap<String,MemoryData> key_items = new HashMap<String,MemoryData>();
	public HashMap<String,MemoryData> badges = new HashMap<String,MemoryData>();
	RabiRaceModule parent;
	
	public Profile(RabiRaceModule module) {
		this.parent = module;
	}
	
	public void updateClientValues() {
		for (MemoryData md : RabiRaceModule.key_items_list) {
			//System.out.println("Checking "+md.getDisplayName());
			if (parent.readIntFromMemory(md.mem)!=0) {
				key_items.put(md.name, md);
				//System.out.println("Obtained "+md.getDisplayName());
			} else {
				key_items.remove(md.name);
			}
		}
		for (MemoryData md : RabiRaceModule.badges_list) {
			if (parent.readIntFromMemory(md.mem)!=0) {
				badges.put(md.name, md);
				//System.out.println("Obtained "+md.getDisplayName());
			} else {
				badges.remove(md.name);
			}
		}
	}
}

package sig.modules.RabiRibi;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sig.sigIRC;
import sig.modules.RabiRibiModule;
import sig.utils.FileUtils;

public class EntityLookupData {
	final static String RABIRIBI_DIR = sigIRC.BASEDIR+"sigIRC/rabi-ribi/";
	final static String LOOKUP_DATA_FILE = RABIRIBI_DIR+"lookupdata.txt";
	int en=-1;
	int kills=0;
	public static RabiRibiModule parent;
	
	public EntityLookupData(){
	};
	
	public EntityLookupData(int en){
		this();
		this.en=en;
	};
	
	public EntityLookupData(String[] parse_string){
		this();
		int i=1;
		if (parse_string.length>=i++) {
			this.en = Integer.parseInt(parse_string[0]);
		}
		if (parse_string.length>=i++) {
			this.kills = Integer.parseInt(parse_string[1]);
		}
	};
	
	public static int getMoney(int id, int color) {
		String hashcode = EntityLookupData.getHashCode(id, color);
		System.out.println("Hashcode is "+hashcode);
		if (parent.lookup_table.containsKey(hashcode)) {
			return parent.lookup_table.get(hashcode).getMoney();
		} else {
			return -1;
		}
	}
	
	public int getMoney() {
		return en;
	}
	
	public void setMoney(int money) {
		en = money;
	}
	
	public int getKills() {
		return kills;
	}
	
	public void increaseKills(int amt) {
		kills += amt;
	}
	
	public String getSaveString() {
		StringBuilder sb = new StringBuilder();
		sb.append(en);
		sb.append(",");
		sb.append(kills);
		sb.append(",");
		return sb.toString();
	}
	
	public static EntityLookupData getEntityLookupData(int id, int color) {
		String hashcode = EntityLookupData.getHashCode(id, color);
		if (parent.lookup_table.containsKey(hashcode)) {
			return parent.lookup_table.get(hashcode);
		} else {
			EntityLookupData data = new EntityLookupData(0);
			parent.lookup_table.put(getHashCode(id,color), data);
			return data;
		}
	}
	
	public static String getHashCode(int id, int color) {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append("_");
		sb.append(color);
		sb.append("_");
		sb.append(parent.readIntFromMemory(MemoryOffset.GAME_DIFFICULTY));
		sb.append("_");
		sb.append(parent.readIntFromMemory(MemoryOffset.GAME_LOOP));
		return sb.toString();
	}
	
	public static void saveEntityLookupData(HashMap<String,EntityLookupData> vals) {
		File dir = new File(RABIRIBI_DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		List<String> data = new ArrayList<String>();
		for (String s : vals.keySet()) {
			EntityLookupData lookup_data = vals.get(s);
			data.add(s+":"+lookup_data.getSaveString());
		}
		FileUtils.writetoFile(data.toArray(new String[data.size()]), LOOKUP_DATA_FILE);
	}
	
	public static void loadEntityLookupData(HashMap<String,EntityLookupData> map) {
		File file = new File(LOOKUP_DATA_FILE);
		map.clear();
		if (file.exists()) {
			String[] data = FileUtils.readFromFile(LOOKUP_DATA_FILE);
			for (String s : data) {
				String[] key_split = s.split(":");
				String[] split = key_split[1].split(",");
				
				EntityLookupData lookup_data = new EntityLookupData(
						split
						);
				
				map.put(key_split[0], 
						lookup_data
						);
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"(");
		boolean first=false;
		for (Field f : this.getClass().getDeclaredFields()) {
			//if (!ReflectUtils.isCloneable(f)) {
				if (!first) {
					try {
						sb.append(f.getName()+"="+f.get(this));
						first=true;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					try {
						sb.append(","+f.getName()+"="+f.get(this));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			//}
		}
		sb.append(")");
		return sb.toString();
	}
}

package sig.modules.RabiRace;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sig.modules.RabiRaceModule;
import sig.utils.ReflectUtils;

public class Session {
	long creationTime = 0;
	long updateTime = 0;
	String name = "";
	int maxPlayers = 0;
	String password = "";
	int id = 0;
	List<Profile> players = new ArrayList<Profile>();
	
	public Session(String dataString) {
		String[] split = dataString.split(",");
		
		int i=0;
		
		id = Integer.parseInt(split[i++]);
		creationTime = Long.parseLong(split[i++]);
		updateTime = Long.parseLong(split[i++]);
		name = split[i++];
		maxPlayers = Integer.parseInt(split[i++]);
		password = split[i++];
		//System.out.println(this.toString());
		if (split.length>=7) {
			String val = split[i++];
			String[] playerlist = val.split(";");
			//System.out.println(Arrays.toString(playerlist));
			if (playerlist.length>0) {
				for (String s : playerlist) {
					Profile p = new Profile(RabiRaceModule.module);
					p.username=s;
					//System.out.println("Player "+p.username);
					p.downloadProfile();
					//System.out.println("Adding Player "+p);
					players.add(p);
				}
			} else {
				Profile p = new Profile(RabiRaceModule.module);
				p.username=val;
				//System.out.println("Player "+p.username);
				p.downloadProfile();
				//System.out.println("Adding Player "+p);
				players.add(p);
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"(");
		boolean first=false;
		for (Field f : this.getClass().getDeclaredFields()) {
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
		}
		sb.append(")");
		return sb.toString();
	}
}

package sig.modules.RabiRace;

import java.util.HashMap;

import sig.modules.RabiRaceModule;

public class SessionListData {
	HashMap<Integer,Session> data = new HashMap<Integer,Session>();
	
	public SessionListData() {
		
	}
	
	public void UpdateData(String[] data) {
		//this.data.clear();
		for (String session : data) {
			if (session.length()>0) {
				//System.out.println("Adding session "+session);
				//this.data.add(new Session(session));
				int sessionID = Integer.parseInt(session.split(",")[0]);
				Session s = new Session(session);
				this.data.put(sessionID, s);
				if (RabiRaceModule.module.mySession!=null && RabiRaceModule.module.mySession.id==sessionID) {
					RabiRaceModule.module.mySession = s;
				}
			}
		}
		//System.out.println(this.data);
	}
	
	public HashMap<Integer,Session> getSessions() {
		return data;
	}
}

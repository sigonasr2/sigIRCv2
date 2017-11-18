package sig.modules.RabiRace;

import java.util.HashMap;

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
				this.data.put(sessionID, new Session(session));
			}
		}
		//System.out.println(this.data);
	}
	
	public HashMap<Integer,Session> getSessions() {
		return data;
	}
}

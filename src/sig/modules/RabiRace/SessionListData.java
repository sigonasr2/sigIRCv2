package sig.modules.RabiRace;

import java.util.ArrayList;
import java.util.List;

public class SessionListData {
	List<Session> data = new ArrayList<Session>();
	
	public SessionListData() {
		
	}
	
	public void UpdateData(String[] data) {
		this.data.clear();
		for (String session : data) {
			if (session.length()>0) {
				//System.out.println("Adding session "+session);
				this.data.add(new Session(session));
			}
		}
		//System.out.println(this.data);
	}
	
	public List<Session> getSessions() {
		return data;
	}
}

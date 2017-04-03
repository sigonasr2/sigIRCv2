package sig.modules.TouhouMother;

import sig.FileUtils;
import sig.sigIRC;
import sig.modules.TouhouMotherModule;

public class TimeRecord {
	final public static int ERROR_VALUE = Integer.MAX_VALUE;
	public static TouhouMotherModule tmm;
	private int boss_id;
	private int seconds;
	
	public TimeRecord(int id, int seconds) {
		this.boss_id=id;
		this.seconds=seconds;
	}
	
	public static void LoadRecordDatabase() {
		String[] records = FileUtils.readFromFile(sigIRC.BASEDIR+"sigIRC/records");
		for (String s : records) {
			if (s.contains(":")) {
				String[] pieces = s.split(":");
				tmm.recordDatabase.add(new TimeRecord(
						Integer.parseInt(pieces[0]),
						Integer.parseInt(pieces[1])
						));
			}
		}
	}
	
	public static void SaveRecordDatabase() {
		StringBuilder sb = new StringBuilder();
		for (TimeRecord tr : tmm.recordDatabase) {
			sb.append(tr.getID()+":"+tr.getTimeRecord()+"\n");
		}
		FileUtils.writetoFile(new String[]{sb.toString()}, sigIRC.BASEDIR+"sigIRC/records");
	}
	
	public static int getRecord(int id) {
		for (TimeRecord tr : tmm.recordDatabase) {
			if (id==tr.getID() && tr.getTimeRecord()>10) {
				return tr.getTimeRecord();
			}
		}
		System.out.println("Warning! Time record for Monster ID "+id+" does not exist!");
		return ERROR_VALUE;
	}
	
	/**
	 * Tries to set the new record for this boss ID. If it doesn't exist, it creates a new record.
	 * If the old record does not beat the currently stored record, it will not overwrite it.
	 */
	public static void setRecord(int id, int seconds) {
		for (TimeRecord tr : tmm.recordDatabase) {
			if (id==tr.getID() && tr.getTimeRecord()>10) {
				if (tr.getTimeRecord()>seconds) {
					tr.setTimeRecord(seconds);
				}
				return;
			}
		}
		//If we got out here, we did not find one. So create a new record.
		tmm.recordDatabase.add(new TimeRecord(id,seconds));
	}

	private void setTimeRecord(int seconds) {
		this.seconds = seconds;
	}

	public int getID() {
		return boss_id;
	}

	public int getTimeRecord() {
		return seconds;
	}
}

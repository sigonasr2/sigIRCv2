package sig.modules.Bandori;

import java.util.Random;

public class SongPart {
	String name;
	int trials;
	int successes;
	
	public SongPart(String partname) {
		this.name = partname;
		this.trials = this.successes = 0;
	}
	
	public void setTitle(String title) {
		this.name = title;
	}
	
	public void setSuccesses(Integer successes) {
		this.successes = successes;
	}
	
	public void setTrials(Integer trials) {
		this.trials = trials;
	}
	
	public String getTitle() {
		return this.name;
	}
	public Integer getSuccesses() {
		return this.successes;
	}
	public Integer getTotal() {
		return this.trials;
	}

	public void AddtoTrials(boolean success) {
		if (success) { 
			this.successes++;
		}
		this.trials++;
	}
}

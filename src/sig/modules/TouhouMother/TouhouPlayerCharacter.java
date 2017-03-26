package sig.modules.TouhouMother;

public enum TouhouPlayerCharacter {
	REIMU(0),
	MARISA(1),
	YUUKA(2),
	MIMA(3);
	
	private int id;
	
	TouhouPlayerCharacter(int arrayID) {
		this.id=arrayID;
	}
	
	public int getID() {
		return id;
	}
}

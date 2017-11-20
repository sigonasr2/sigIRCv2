package sig.modules.TouhouMother;

public enum DataProperty {
	CURRENTDAMAGE(0,"Last Battle"),
	TOTALDAMAGE(1,"Total Damage"),
	DAMAGETURNS(2,"Damage Turns"),
	LARGESTHIT(3,"Largest Hit");
	
	private int id;
	private String displayname;
	
	DataProperty(int id, String displayname) {
		this.id=id;
		this.displayname=displayname;
	}
	
	public int getID() {
		return id;
	}
	
	public String getDisplayName() {
		return displayname;
	}
	
	public static DataProperty getDataPropertyBasedOnID(int id) {
		for (DataProperty dp : DataProperty.values()) {
			if (dp.getID()==id) {
				return dp;
			}
		}
		System.out.println("Warning! Could not find Data Property with ID "+id+"!");
		return CURRENTDAMAGE;
	}
}

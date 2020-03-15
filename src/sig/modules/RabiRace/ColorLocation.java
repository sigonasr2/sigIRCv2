package sig.modules.RabiRace;

import java.awt.Color;

public enum ColorLocation {
	UNKNOWN(0,"Unknown",52,52,52),
	STARTING_FOREST(1,"Starting Forest",110,181,103),
	SPECTRAL_CAVE(2,"Spectral Cave",97,103,162),
	FORGOTTEN_CAVE(3,"Forgotten Cave",135,110,75),
	NATURAL_AQUARIUM(4,"Natural Aquarium",128,169,115),
	DLC2BOSSARENA(5,"Boss Arena",255,255,255),
	FORGOTTENCAVEII(6,"Forgotten Cave II",146,108,109),
	UPPERFOREST(7,"Upper Forest",0,128,84),
	BLANK(8,"???",52,52,52),
	RABIRABIBEACH(9,"Rabi Rabi Beach",98,135,193),
	GOLDENPYRAMID(10,"Golden Pyramid",198,161,75),
	RABIRABIRAVINE(11,"Rabi Rabi Ravine",110,181,103),
	RABIRABITOWN(12,"Rabi Rabi Town",217,145,126),
	RABIRABIPARK(13,"Rabi Rabi Park",181,110,103),
	UPRPRCBASE(14,"UPRPRC Base",110,110,181),
	SKYISLANDTOWN(15,"Sky Island Town",142,105,150),
	AZURESNOWLAND(16,"Azure Snowland",142,105,235),
	SYSTEMINTERIORI(17,"System Interior I",104,156,207),
	EVERNIGHTPEAK(18,"Evernight Peak",75,97,210),
	EXOTICLAB(19,"Exotic Laboratory",175,103,134),
	GOLDENRIVERBANK(20,"Golden Riverbank",206,156,105),
	FLOATINGGRAVEYARD(21,"Floating Graveyard",180,59,54),
	SYSTEMINTERIORII(22,"System Interior II",212,77,86),
	AURORAPALACE(23,"Aurora Palace",34,169,209),
	FLOATINGLIBRARY(24,"Floating Library",118,198,166),
	NATURALAQUARIUM(25,"Natural Aquarium",128,169,115),
	SKYHIGHBRIDGE(26,"Sky-High Bridge",106,195,182),
	WARPDESTINATION(27,"Warp Destination",138,178,88),
	VOLCANICCAVERNS(28,"Volcanic Caverns",186,45,42),
	PLURKWOOD(29,"Plurkwood",195,98,45),
	HALLOFMEMORIES(30,"Hall of Memories",64,99,164),
	ICYSUMMIT(31,"Icy Summit",45,104,146),
	HALLOFMEMORIESII(32,"Hall of Memories II",220,143,64),
	HALLOWEENAREA(34,"Halloween Area",121,55,53),
	HOSPITAL(50,"Hospital",97,97,136),
	RABIRABIRAVINEII(55,"Rabi Rabi Ravine II",42,184,120),
	NOAH3BOSSARENA(81,"Noah 3 Boss Arena",52,52,52),
	NOAH1BOSSARENA(83,"Noah 1 Boss Arena",52,52,52),
	RUMIBOSSARENA(87,"Rumi Boss Arena",203,125,122),
	HALLOFMEMORIES21(95,"Hall of Memories II",255,255,0),
	HALLOFMEMORIES22(96,"Hall of Memories II",255,255,0),
	HALLOFMEMORIES23(97,"Hall of Memories II",255,255,0),
	HALLOFMEMORIES24(98,"Hall of Memories II",255,255,0),
	
	;
	
	int color;
	String name;
	Color colorval;
	
	ColorLocation(int val, String name, int r, int g, int b) {
		
		color = val;
		this.name=name;
		this.colorval = new Color(r,g,b);
		
	}
	
	public static Color getColor(int id) {
		for (ColorLocation cl : ColorLocation.values()) {
			if (cl.color == id) {
				return cl.colorval;
			}
		}
		return null;
	}
	
	public static String getLocationName(int id) {
		for (ColorLocation cl : ColorLocation.values()) {
			if (cl.color == id) {
				return cl.name;
			}
		}
		return "";
	}
}

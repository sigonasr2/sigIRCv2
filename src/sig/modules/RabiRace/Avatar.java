package sig.modules.RabiRace;

import java.awt.Image;

import sig.modules.RabiRaceModule;

public enum Avatar {
	ERINA("Erina","small_erina.png",0),
	RIBBON("Ribbon","small_ribbon.png",1),
	IRISU("Irisu","small_irisu.png",2),
	GREEN_KOTRI("Kotri (Green)","small_green_kotri.png",3),
	BLUE_KOTRI("Kotri (Blue)","small_blue_kotri.png",4),
	RED_KOTRI("Kotri (Red)","small_kotri.png",5),
	ARURAUNE("Aruraune","small_aruraune.png",6),
	ASHURI("Ashuri","small_ashuri.png",7),
	CICINI("Cicini","small_cicini.png",8),
	COCOA("Cocoa","small_cocoa.png",9),
	ILLUSION_ALIUS("Illusion Alius","small_illusion_alius.png",10),
	KEKE_BUNNY("Keke Bunny","small_keke_bunny.png",11),
	LILITH("Lilith","small_lilith.png",12),
	LILLI("Lilli","small_lilli.png",13),
	MIRIAM("Miriam","small_miriam.png",14),
	MIRU("Miru","small_miru.png",15),
	NIEVE("Nieve","small_nieve.png",16),
	NIXIE("Nixie","small_nixie.png",17),
	NOAH("Noah","small_noah.png",18),
	PANDORA("Pandora","small_pandora.png",19),
	SHADOW_PANDORA("Pandora (Shadow)","small_shadow_pandora.png",20),
	PIXIE("Pixie","small_pixie.png",21),
	RITA("Rita","small_rita.png",22),
	SHADOW_RITA("Rita (Shadow)","small_shadow_rita.png",23),
	RUMI("Rumi","small_rumi.png",24),
	SAYA("Saya","small_saya.png",25),
	SEANA("Seana","small_seana.png",26),
	SYARO("Syaro","small_syaro.png",27),
	VANILLA("Vanilla","small_vanilla.png",28),
	;
	
	public String displayName;
	public String fileName;
	public int value;
	
	Avatar(String displayName, String fileName, int value) {
		this.displayName = displayName;
		this.fileName = fileName;
		this.value = value;
	}
	
	public static Avatar getAvatarFromID(int value) {
		for (Avatar a : Avatar.values()) {
			if (a.value == value) {
				return a;
			}
		}
		return ERINA;
	}
	
	public Image getAvatarImage() {
		return RabiRaceModule.image_map.get(fileName);
	}
}

package sig.modules.RabiRace;

import java.awt.Image;

import sig.modules.RabiRaceModule;
import sig.modules.RabiRibi.MemoryOffset;

public enum MemoryData {
	HAMMER(MemoryOffset.HAMMER,"Hammer","piko_hammer.png",true),
	AIR_JUMP(MemoryOffset.AIR_JUMP,"Air Jump","air_jump.png",true),
	SLIDING_POWDER(MemoryOffset.SLIDING_POWDER,"Sliding Powder","sliding_powder.png",true),
	CARROT_BOMB(MemoryOffset.CARROT_BOMB,"Carrot Bomb","carrot_bomb.png",true),
	HOURGLASS(MemoryOffset.HOURGLASS,"Hourglass","hourglass.png",true),
	SPEED_BOOST(MemoryOffset.SPEED_BOOST,"Speed Boost","speed_boost.png",true),
	AUTO_EARRINGS(MemoryOffset.AUTO_EARRINGS,"Auto Earrings","auto_earrings.png",true),
	RIBBON(MemoryOffset.RIBBON,"Ribbon","ribbon.png",true),
	SOUL_HEART(MemoryOffset.SOUL_HEART,"Soul Heart","soul_heart.png",true),
	RABI_SLIPPERS(MemoryOffset.RABI_SLIPPERS,"Rabi Slippers","rabi_slippers.png",true),
	BUNNY_WHIRL(MemoryOffset.BUNNY_WHIRL,"Bunny Whirl","bunny_whirl.png",true),
	QUICK_BARRETTE(MemoryOffset.QUICK_BARETTE,"Quick Barrette","quick_barrette.png",true),
	BOOK_OF_CARROT(MemoryOffset.BOOK_OF_CARROT,"Book of Carrot","book_of_carrot.png",true),
	CHAOS_ROD(MemoryOffset.CHAOS_ROD,"Chaos Rod","chaos_rod.png",true),
	HAMMER_WAVE(MemoryOffset.HAMMER_WAVE,"Hammer Wave","hammer_wave.png",true),
	HAMMER_ROLL(MemoryOffset.HAMMER_ROLL,"Hammer Roll","hammer_roll.png",true),
	LIGHT_ORB(MemoryOffset.LIGHT_ORB,"Light Orb","light_orb.png",true),
	WATER_ORB(MemoryOffset.WATER_ORB,"Water Orb","water_orb.png",true),
	FIRE_ORB(MemoryOffset.FIRE_ORB,"Fire Orb","fire_orb.png",true),
	NATURE_ORB(MemoryOffset.NATURE_ORB,"Nature Orb","nature_orb.png",true),
	P_HAIRPIN(MemoryOffset.P_HAIRPIN,"P. Hairpin","p_hairpin.png",true),
	SUNNY_BEAM(MemoryOffset.SUNNY_BEAM,"Sunny Beam","sunny_beam.png",true),
	PLUS_NECKLACE(MemoryOffset.PLUS_NECKLACE,"Plus Necklace","plus_necklace.png",true),
	CYBER_FLOWER(MemoryOffset.CYBER_FLOWER,"Cyber Flower","cyber_flower.png",true),
	HEALING_STAFF(MemoryOffset.HEALING_STAFF,"Healing Staff","healing_staff.png",true),
	MAX_BRACELET(MemoryOffset.MAX_BRACELET,"Max Bracelet","max_bracelet.png",true),
	EXPLODE_SHOT(MemoryOffset.EXPLODE_SHOT,"Explode Shot","explode_shot.png",true),
	AIR_DASH(MemoryOffset.AIR_DASH,"Air Dash","air_dash.png",true),
	BUNNY_STRIKE(MemoryOffset.BUNNY_STRIKE,"Bunny Strike","bunny_strike.png",true),
	STRANGE_BOX(MemoryOffset.STRANGE_BOX,"Strage Box","strange_box.png",true),
	WALL_JUMP(MemoryOffset.WALL_JUMP,"Wall Jump","wall_jump.png",true),
	SPIKE_BARRIER(MemoryOffset.SPIKE_BARRIER,"Spike Barrier","spike_barrier.png",true),
	BUNNY_AMULET(MemoryOffset.BUNNY_AMULET,"Bunny Amulet","bunny_amulet.png",true),
	CHARGE_RING(MemoryOffset.CHARGE_RING,"Charge Ring","charge_ring.png",true),
	CARROT_SHOOTER(MemoryOffset.CARROT_SHOOTER,"Carrot Shooter","carrot_shooter.png",true),
	SUPER_CARROT(MemoryOffset.SUPER_CARROT,"Super Carrot","super_carrot.png",true),
	/*DLC_ITEM1(MemoryOffset.DLC_ITEM1,"","",true),
	DLC_ITEM2(MemoryOffset.DLC_ITEM2,"","",true),
	DLC_ITEM4(MemoryOffset.DLC_ITEM4,"","",true),*/
	BUNNY_CLOVER(MemoryOffset.BUNNY_CLOVER,"Bunny Clover","bunny_clover.png",true),
	FAIRYS_FLUTE(MemoryOffset.FAIRYS_FLUTE,"Fairy's Flute","fairy_s_flute.png",true),
	BADGE_HEALTH_PLUS(MemoryOffset.BADGE_HEALTH_PLUS,"Health Plus","health_plus.png",false),
	BADGE_HEALTH_SURGE(MemoryOffset.BADGE_HEALTH_SURGE,"Health Surge","health_surge.png",false),
	BADGE_MANA_PLUS(MemoryOffset.BADGE_MANA_PLUS,"Mana Plus","mana_plus.png",false),
	BADGE_MANA_SURGE(MemoryOffset.BADGE_MANA_SURGE,"Mana Surge","mana_surge.png",false),
	BADGE_CRISIS_BOOST(MemoryOffset.BADGE_CRISIS_BOOST,"Crisis Boost","crisis_boost.png",false),
	BADGE_ATK_GROW(MemoryOffset.BADGE_ATK_GROW,"ATK Grow","atk_grow.png",false),
	BADGE_DEF_GROW(MemoryOffset.BADGE_DEF_GROW,"DEF Grow","def_grow.png",false),
	BADGE_ATK_TRADE(MemoryOffset.BADGE_ATK_TRADE,"ATK Trade","atk_trade.png",false),
	BADGE_DEF_TRADE(MemoryOffset.BADGE_DEF_TRADE,"DEF Trade","def_trade.png",false),
	BADGE_ARM_STRENGTH(MemoryOffset.BADGE_ARM_STRENGTH,"Arm Strength","arm_strength.png",false),
	BADGE_CARROT_BOOST(MemoryOffset.BADGE_CARROT_BOOST,"Carrot Boost","carrot_boost.png",false),
	BADGE_WEAKEN(MemoryOffset.BADGE_WEAKEN,"Weaken","weaken.png",false),
	BADGE_SELF_DEFENSE(MemoryOffset.BADGE_SELF_DEFENSE,"Self Defense","self_defense.png",false),
	BADGE_ARMORED(MemoryOffset.BADGE_ARMORED,"Armored","armored.png",false),
	BADGE_LUCKY_SEVEN(MemoryOffset.BADGE_LUCKY_SEVEN,"Lucky Seven","lucky_seven.png",false),
	BADGE_HEX_CANCEL(MemoryOffset.BADGE_HEX_CANCEL,"Hex Cancel","hex_cancel.png",false),
	BADGE_PURE_LOVE(MemoryOffset.BADGE_PURE_LOVE,"Pure Love","pure_love.png",false),
	BADGE_TOXIC_STRIKE(MemoryOffset.BADGE_TOXIC_STRIKE,"Toxic Strike","toxic_strike.png",false),
	BADGE_FRAME_CANCEL(MemoryOffset.BADGE_FRAME_CANCEL,"Frame Cancel","frame_cancel.png",false),
	BADGE_HEALTH_WAGER(MemoryOffset.BADGE_HEALTH_WAGER,"Health Wager","health_wager.png",false),
	BADGE_MANA_WAGER(MemoryOffset.BADGE_MANA_WAGER,"Mana Wager","mana_wager.png",false),
	BADGE_STAMINA_PLUS(MemoryOffset.BADGE_STAMINA_PLUS,"Stamina Plus","stamina_plus.png",false),
	BADGE_BLESSED(MemoryOffset.BADGE_BLESSED,"Blessed","blessed.png",false),
	BADGE_HITBOX_DOWN(MemoryOffset.BADGE_HITBOX_DOWN,"Hitbox Down","hitbox_down.png",false),
	BADGE_CASHBACK(MemoryOffset.BADGE_CASHBACK,"Cashback","cashback.png",false),
	BADGE_SURVIVAL(MemoryOffset.BADGE_SURVIVAL,"Survival","survival.png",false),
	BADGE_TOP_FORM(MemoryOffset.BADGE_TOP_FORM,"Top Form","top_form.png",false),
	BADGE_TOUGH_SKIN(MemoryOffset.BADGE_TOUGH_SKIN,"Tough Skin","tough_skin.png",false),
	BADGE_ERINA_BADGE(MemoryOffset.BADGE_ERINA_BADGE,"Erina","erina_badge.png",false),
	BADGE_RIBBON_BADGE(MemoryOffset.BADGE_RIBBON_BADGE,"Ribbon","ribbon_badge.png",false),
	BADGE_AUTO_TRIGGER(MemoryOffset.BADGE_AUTO_TRIGGER,"Auto Trigger","auto_trigger.png",false),
	BADGE_LILITHS_GIFT(MemoryOffset.BADGE_LILITHS_GIFT,"Lilith's Gift","lilith_s_gift.png",false),
	;
	
	public MemoryOffset mem;
	public String name;
	public String img_path;
	public boolean key_item; //Set to true if it's a key item. False if it's a badge.
	
	MemoryData(MemoryOffset mem, String name, String icon_name, boolean isKeyItem) {
		this.mem = mem;
		this.name = name;
		this.img_path = icon_name;
		this.key_item = isKeyItem;
	}
	
	public Image getImage() {
		return RabiRaceModule.image_map.get(img_path);
	}
	
	public String getDisplayName() {
		return (key_item)?name:name+" Badge";
	}
}

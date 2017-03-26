package sig.modules.TouhouMother;

public class TouhouMotherBossData {
	private String img;
	private String name;
	private int hp;
	private int id;
	
	public TouhouMotherBossData(String name, int id, int hp, String img) {
		this.name=name;
		this.id=id;
		this.hp=hp;
		this.img=img;
	}

	public String getImage() {
		return img;
	}

	public String getName() {
		return name;
	}

	public int getHP() {
		return hp;
	}

	public int getID() {
		return id;
	}
}

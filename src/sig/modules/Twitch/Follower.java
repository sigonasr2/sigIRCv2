package sig.modules.Twitch;

public class Follower {
	public long id;
	public String bio;
	public String created_at;
	public String display_name;
	public String logo_url;
	public String name;
	public String type;
	public String updated_at;
	Follower(Long id,
			String bio,
			String created_at,
			String display_name,
			String logo_url,
			String name,
			String type,
			String updated_at) {
		this.id=id;
		this.bio = bio;
		this.created_at = created_at;
		this.display_name = display_name;
		this.logo_url = logo_url;
		this.name = name;
		this.type = type;
		this.updated_at = updated_at;
	}
	public Follower(String id,
			String bio,
			String created_at,
			String display_name,
			String logo_url,
			String name,
			String type,
			String updated_at) {
		this(Long.parseLong(id),
				bio,
				created_at,
				display_name,
				logo_url,
				name,
				type,
				updated_at
				);
	}
}	

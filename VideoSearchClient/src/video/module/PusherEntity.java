package video.module;

public class PusherEntity {
	public String imageURL;
	public String weblink;
	public String getImageURL() {
		return imageURL;
	}

	public String getWeblink() {
		return weblink;
	}


	public int getId() {
		return id;
	}

	public int id;
	public PusherEntity(String imageURL,String weblink,int id) {
		this.imageURL=imageURL;
		this.weblink=weblink;
		this.id=id;
	}
	
	public PusherEntity() {}
}
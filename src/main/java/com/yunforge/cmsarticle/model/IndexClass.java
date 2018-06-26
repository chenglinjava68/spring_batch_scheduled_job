package com.yunforge.cmsarticle.model;

public class IndexClass {
	
    private String id;
	private String title;
	private String content;
	private String image;
	private String url;
	private String pcurl;
	private String type;
	private String typeName;
	private String createDate;
	
	public IndexClass() {
		super();
	}
	public IndexClass(String title, String content) {
		super();
		this.title = title;
		this.content = content;
	}
	public String getId() {
	    return id;
	}
	public void setId(String id) {
	    this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
    public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getPcurl() {
		return pcurl;
	}
	public void setPcurl(String pcurl) {
		this.pcurl = pcurl;
	}
	@Override
	public String toString() {
		return "IndexClass [id=" + id + ", title=" + title + ", content=" + content + ", image=" + image + ", url="
				+ url + ", pcurl=" + pcurl + ", type=" + type + ", typeName=" + typeName + ", createDate=" + createDate
				+ "]";
	}
    
    
	
	
	

}

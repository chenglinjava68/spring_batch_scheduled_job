package com.yunforge.cmsarticle.model;

import java.util.Date;


public class Article {

	private Long id;
	private String title;

	private String origin;

	private Integer artType;

	private boolean notifyPub;

	private Integer commentCount;

	private Integer readCount;

	private Integer rating;

	private Integer status;

	private Long catId;

	private String linkUrl;

	private String createdBy;

	private Date createdTime;

	private String modifiedBy;

	private Date modifiedTime;

	private String auditedBy;

	private Date auditedTime;

	private String publishedBy;

	private Date publishedTime;
	
	private String homeText;

	private String content;

	private Integer weight;
	
	private String info;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public Integer getArtType() {
		return artType;
	}

	public void setArtType(Integer artType) {
		this.artType = artType;
	}

	public boolean isNotifyPub() {
		return notifyPub;
	}

	public void setNotifyPub(boolean notifyPub) {
		this.notifyPub = notifyPub;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getReadCount() {
		return readCount;
	}

	public void setReadCount(Integer readCount) {
		this.readCount = readCount;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getCatId() {
		return catId;
	}

	public void setCatId(Long catId) {
		this.catId = catId;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getAuditedBy() {
		return auditedBy;
	}

	public void setAuditedBy(String auditedBy) {
		this.auditedBy = auditedBy;
	}

	public Date getAuditedTime() {
		return auditedTime;
	}

	public void setAuditedTime(Date auditedTime) {
		this.auditedTime = auditedTime;
	}

	public String getPublishedBy() {
		return publishedBy;
	}

	public void setPublishedBy(String publishedBy) {
		this.publishedBy = publishedBy;
	}

	public Date getPublishedTime() {
		return publishedTime;
	}

	public void setPublishedTime(Date publishedTime) {
		this.publishedTime = publishedTime;
	}

	public String getHomeText() {
		return homeText;
	}

	public void setHomeText(String homeText) {
		this.homeText = homeText;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "Article [id=" + id + ", title=" + title + ", origin=" + origin + ", artType=" + artType + ", notifyPub="
				+ notifyPub + ", commentCount=" + commentCount + ", readCount=" + readCount + ", rating=" + rating
				+ ", status=" + status + ", catId=" + catId + ", linkUrl=" + linkUrl + ", createdBy=" + createdBy
				+ ", createdTime=" + createdTime + ", modifiedBy=" + modifiedBy + ", modifiedTime=" + modifiedTime
				+ ", auditedBy=" + auditedBy + ", auditedTime=" + auditedTime + ", publishedBy=" + publishedBy
				+ ", publishedTime=" + publishedTime + ", homeText=" + homeText + ", content=" + content + ", weight="
				+ weight + ", info=" + info + "]";
	}

	
	
}

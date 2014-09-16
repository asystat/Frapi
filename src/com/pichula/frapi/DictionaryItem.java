package com.pichula.frapi;

public class DictionaryItem {
	
	public static final int STATUS_INACTIVE=0;
	public static final int STATUS_PARSING=1;
	public static final int STATUS_PARSED=2;
	public static final int STATUS_ERROR=3;
	public static final int STATUS_DOWNLOADING=4;
	
	
	String url;
	String name;
	String imgurl;
	Long _id=null;
	int status=0;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	
}

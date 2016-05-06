package com.witskies.manager.bean;

/**
 * @作者 ch
 * @描述 这是一个放适配器所需要数据来源的Bean(视频，音乐，图片，文档)
 * @时间 2015年5月5日 上午10:02:45
 */
public class AdapterItemBean {
	// 视频名称，时间，大小
	private String videoName;
	private String videoTime;
	private String videoSize;
	private String videoCurrPath;// 用于删除文件
	// 音乐名称，时间，大小
	private String musicName;
	private String musicTime;
	private String musicSize;
	private String musicCurrPath;// 用于删除文件

	// 图片名称，时间，大小
	private String imageName;
	private String imageTime;
	private String imageSize;
	private String imageCurrPath;// 用于删除文件
	// 文档名称，时间，大小
	private String documentName;
	private String documentTime;
	private String documentSize;
	private String documenCurrPath;// 用于删除文件

	// 后面改成通用的了
	private String name;
	private String time;
	private String size;
	private String currPath;// 用于删除文件
	private int id;

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getCurrPath() {
		return currPath;
	}

	public void setCurrPath(String currPath) {
		this.currPath = currPath;
	}

	public String getDocumenCurrPath() {
		return documenCurrPath;
	}

	public void setDocumenCurrPath(String documenCurrPath) {
		this.documenCurrPath = documenCurrPath;
	}

	public String getImageCurrPath() {
		return imageCurrPath;
	}

	public void setImageCurrPath(String imageCurrPath) {
		this.imageCurrPath = imageCurrPath;
	}

	public String getVideoCurrPath() {
		return videoCurrPath;
	}

	public void setVideoCurrPath(String videoCurrPath) {
		this.videoCurrPath = videoCurrPath;
	}

	public String getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(String videoSize) {
		this.videoSize = videoSize;
	}

	public String getMusicSize() {
		return musicSize;
	}

	public void setMusicSize(String musicSize) {
		this.musicSize = musicSize;
	}

	public String getImageSize() {
		return imageSize;
	}

	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}

	public String getDocumentSize() {
		return documentSize;
	}

	public void setDocumentSize(String documentSize) {
		this.documentSize = documentSize;
	}

	public String getMusicCurrPath() {
		return musicCurrPath;
	}

	public void setMusicCurrPath(String musicCurrPath) {
		this.musicCurrPath = musicCurrPath;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoTime() {
		return videoTime;
	}

	public void setVideoTime(String videoTime) {
		this.videoTime = videoTime;
	}

	public String getMusicName() {
		return musicName;
	}

	public void setMusicName(String musicName) {
		this.musicName = musicName;
	}

	public String getMusicTime() {
		return musicTime;
	}

	public void setMusicTime(String musicTime) {
		this.musicTime = musicTime;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageTime() {
		return imageTime;
	}

	public void setImageTime(String imageTime) {
		this.imageTime = imageTime;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDocumentTime() {
		return documentTime;
	}

	public void setDocumentTime(String documentTime) {
		this.documentTime = documentTime;
	}

}

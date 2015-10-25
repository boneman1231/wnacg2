package org.redcenter.wnacg.download;

public class QueueItem {
	static public final String STATUS_WAIT = "wait";
	static public final String STATUS_DONE = "done";
	static public final String STATUS_PAUSE = "pause";

	protected String saveDir;
	protected String comicName;
	protected String volumeName;
	protected String name;

	protected String link;
	protected String referLink;
	protected String status;

	protected int totalSize;
	protected int size;

	public QueueItem(String saveDir, String name, String link) {
		this(saveDir, null, null, name, link);
	}

	/**
	 * 
	 * @param saveDir
	 * @param comicName
	 * @param volumeName
	 * @param name
	 * @param link
	 *            ex:http://x1.yaodm.com/comicdata/slerz/001/001.jpg
	 */
	public QueueItem(String saveDir, String comicName, String volumeName, String name, String link) {
		this(saveDir, comicName, volumeName, name, link, QueueItem.STATUS_WAIT, "");
	}

	public QueueItem(String saveDir, String comicName, String volumeName, String name, String link, String status,
			String referLink) {
		this.saveDir = saveDir;
		this.comicName = comicName;
		this.volumeName = volumeName;
		this.name = name;
		this.link = link;
		this.status = status;
		this.referLink = referLink;
	}

	public String getSavePath() {
		if (comicName == null) {
			return saveDir + "/" + name;
		} else {
			if (volumeName == null) {
				return saveDir + "/" + comicName + "/" + name;
			} else {
				return saveDir + "/" + comicName + "/" + volumeName + "/" + name;
			}
		}
	}

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}

	public String getComicName() {
		return comicName;
	}

	public void setComicName(String comicName) {
		this.comicName = comicName;
	}

	public String getVolumeName() {
		return volumeName;
	}

	public String getName() {
		return name;
	}

	public String getLink() {
		return link;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReferLink() {
		return this.referLink;
	}

	public void setReferLink(String link) {
		referLink = link;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}

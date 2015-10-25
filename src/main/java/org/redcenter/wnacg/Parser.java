package org.redcenter.wnacg;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.redcenter.wnacg.download.QueueItem;

public interface Parser {
	static final int TIMEOUT = (int) TimeUnit.MILLISECONDS.convert(60,
            TimeUnit.SECONDS);
	
	boolean isComicWeb(String url);
	
	boolean isComicMainPage(String url);
	
	boolean isComicIndexPage(String url);
	
	boolean isComicImagePage(String url);
	
	String getComicMainPageFromIndexPage(String url);
	
	String getComicMainPageFromImagePage(String url);
	
	String getComicNameFromMainPage(String url);
	
	List<QueueItem> getImages(String mainPageUrl);
}

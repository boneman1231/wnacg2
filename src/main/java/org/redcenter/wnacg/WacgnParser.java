package org.redcenter.wnacg;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.redcenter.wnacg.download.QueueItem;

public class WacgnParser implements Parser {
	private static final String WEBSITE = "www.wnacg.com";
	
	private static final String KEY_PHOTO_INDEX = "photos-index-aid-";
	private static final String KEY_PHOTO_INDEX_PAGE = "photos-index-page-[0-9]+-aid-";
	
	private static final String KEY_PHOTO_VIEW = "photos-view-id-";
	private static final String KEY_PHOTO_VIEW2 = "?ctl=photos&act=view&id=";
	
	private static final String KEY_DOWNLOAD_BUTTON = "a[class=down_btn]";

	@Override
	public boolean isComicWeb(String url) {
		return url.contains(WEBSITE);
	}

	@Override
	public boolean isComicMainPage(String url) {
		String keyword = "http://" + WEBSITE + "/" + KEY_PHOTO_INDEX;
		return url.contains(keyword);
	}

	@Override
	public boolean isComicIndexPage(String url) {
		Pattern pattern = Pattern.compile(KEY_PHOTO_INDEX_PAGE);
		Matcher matcher = pattern.matcher(url);
		return matcher.find();
	}

	@Override
	public boolean isComicImagePage(String url) {
		String keyword = "http://" + WEBSITE + "/" + KEY_PHOTO_VIEW;
		String keyword2 = "http://" + WEBSITE + "/" + KEY_PHOTO_VIEW2;
		return url.contains(keyword) || url.contains(keyword2);
	}

	@Override
	public String getComicMainPageFromIndexPage(String indexUrl) {
		return indexUrl.replaceAll("-page-[0-9]+", "");
	}

	@Override
	public String getComicMainPageFromImagePage(String imageUrl) {
		try {
			Document doc = Jsoup.parse(new URL(imageUrl), TIMEOUT);
			Elements element = doc.select("a[href*=" + KEY_PHOTO_INDEX + "]");
			// ex: /photos-index-aid-10548.html
			String urlName = element.attr("href");

			int lastIndex = imageUrl.lastIndexOf("/");
			String parentUrl = imageUrl.substring(0, lastIndex);
			String url = parentUrl + urlName;
			System.out.println("Find " + url + " for " + imageUrl);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String getComicNameFromMainPage(String url) {
		try {
			Document doc = parseHtml(url);
			String name = doc.select("h2").text(); // get comic name
			name = name.replaceAll("[\u0001-\u001f<>:\"/\\\\|?*\u007f]+", "_");
			if (name.endsWith("_")) {
				name = name.substring(0, name.length() - 1);
			}
			return name;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public List<QueueItem> getImages(String mainPageUrl) {
		String comicName = getComicNameFromMainPage(mainPageUrl);
		List<QueueItem> list = new ArrayList<>();

		// get download page url
		String downloadPageUrl = mainPageUrl.replace("photos", "download");
		try {
			Document doc = parseHtml(downloadPageUrl);
			Elements elements = doc.select(KEY_DOWNLOAD_BUTTON);
			if (elements == null || elements.isEmpty()) {
				System.err.println("No sub page found.");
			} else {
				String url = elements.first().attr("href");
				String extension = url.substring(url.lastIndexOf("."));
				String fileName = comicName + extension;
				String saveDir = new File(".").getAbsolutePath();
				
				QueueItem item = new QueueItem(saveDir, fileName, url);
				System.out.println("To save " + item.getSavePath() + " from " + item.getLink());
				list.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private Document parseHtml(String url) throws IOException {
		return Jsoup.connect(url).timeout(TIMEOUT)
				.userAgent(
						"Mozilla/5.0 (Windows; U; WindowsNT 6.1; en-US; rv1.8.1.6) " + "Gecko/20070725 Firefox/2.0.0.6")
				.referrer("http://www.google.com").get();
	}
}

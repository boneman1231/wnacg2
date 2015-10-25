package org.redcenter.wnacg.download;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.redcenter.wnacg.Parser;

public class DownloadManager {
	public static final String QUEUE_FILE_NAME = "queue.txt";
	public static final String QUEUE_BAK_FILE_NAME = "queue.bak";

	private Parser parser;

	public DownloadManager(Parser parser) {
		this.parser = parser;
	}

	public void executeQueue(String path) throws IOException {
		File queueFile = new File(path + File.separator + QUEUE_FILE_NAME);
		System.out.println("Read queue file " + queueFile.getAbsolutePath());

		List<String> contents = FileUtils.readLines(queueFile);
		for (String line : contents) {
			if (line.isEmpty()) {
				continue;
			}

			if (parser.isComicIndexPage(line)) {
				line = parser.getComicMainPageFromIndexPage(line);
			} else if (parser.isComicImagePage(line)) {
				line = parser.getComicMainPageFromImagePage(line);
			}

			Downloader.getInstance().download(line, parser);
		}
		Downloader.getInstance().terminate();

		File bakFile = new File(path + File.separator + QUEUE_BAK_FILE_NAME);
		FileUtils.copyFile(queueFile, bakFile);
		FileUtils.writeByteArrayToFile(queueFile, new byte[] {});
	}
}

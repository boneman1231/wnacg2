package org.redcenter.wnacg;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.redcenter.wnacg.download.DownloadManager;
import org.redcenter.wnacg.util.ClipboardMonitor;

public class Wacgn {
	private static Parser parser = new WacgnParser();

	public static void main(String[] args) throws Exception {
		// String path = "M:/t/wnacg";
		String path = ".";
		if (args != null && args.length > 0) {
			path = args[0];
		}

		// monitor clipboard
//		if (args != null && args.length > 1) {
			monitorClipboard(path);
			Thread.sleep(1000*3);
//		}

		// download from queue file
		try {
			System.out.println("Download queue.");
			new DownloadManager(parser).executeQueue(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("Finish main thread.");
		System.out.println();
	}

	private static void monitorClipboard(String path) throws IOException {
		System.out.println("Start monitor clipboard.");
		final Set<String> urlSet = new HashSet<String>();
		ClipboardMonitor monitor = new ClipboardMonitor() {
			protected boolean isTarget(String s) {
				return parser.isComicWeb(s);
			}

			protected void processContent(String content) {
				System.out.println("Monitor: " + content);
				urlSet.add(content.trim());
			}
		};
		monitor.startMonitor();

		// wait input to stop monitor
		System.out.println("Press Enter to stop monitor.");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
		monitor.stopMonitor();
		System.out.println("Stop monitor clipboard.");
		scanner.close();

		// write to file
		System.out.println("---- Start to write urls to queue file -----");
		StringBuffer sb = new StringBuffer();
		for (String s : urlSet) {
			sb.append(s + "\n");
		}
		System.out.println(sb.toString());
		File queueFile = new File(path + File.separator + DownloadManager.QUEUE_FILE_NAME);
		FileUtils.writeStringToFile(queueFile, sb.toString());
		System.out.println("---- Complete to write urls to queue file -----");
	}
}
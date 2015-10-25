package org.redcenter.wnacg.util;

import java.awt.*;
import java.awt.datatransfer.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClipboardMonitor implements ClipboardOwner {
	private Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
	private boolean monitor = true;
	private Pattern pattern = null;

	public void lostOwnership(Clipboard c, Transferable t) {
		try {
			Thread.sleep(200);
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		}
		try {
			Transferable contents = c.getContents(this); // EXCEPTION
			regainOwnership(contents);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void regainOwnership(Transferable t) {
		sysClip.setContents(t, this);
		processContents(t);
	}

	public void startMonitor() {
		Thread t = new Thread() {
			public void run() {
				Transferable trans = sysClip.getContents(this);
				regainOwnership(trans);
				while (true) {
					if (!monitor) {
						break;
					}
				}
			}
		};
		t.start();
	}

	public void stopMonitor() {
		monitor = false;
	}

	private void processContents(Transferable t) {
		if (!monitor) {
			return;
		}
		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = flavors.length - 1; i >= 0; i--) {
			try {
				Object o = t.getTransferData(flavors[i]);
				if (o instanceof String) {
					String s = (String) o;
					if (isTarget(s)) {
						processContent(s);
					}
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected boolean isTarget(String s) {
		pattern = Pattern.compile("^http://");
		Matcher matcher = pattern.matcher(s);
		return matcher.find();
	}

	protected void processContent(String content) {
		System.out.println("Monitor: " + content);
	}

	public static void main(String[] args) {
		ClipboardMonitor b = new ClipboardMonitor();
		b.startMonitor();
	}
}
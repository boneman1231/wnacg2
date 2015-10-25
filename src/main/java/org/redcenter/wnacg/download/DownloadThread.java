package org.redcenter.wnacg.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;

import javax.swing.ImageIcon;

import org.redcenter.wnacg.util.Util;

public class DownloadThread extends Thread {
	private int retry = 0;
	private int bufRetry = 0;
	private int bufWaitTime = Configuration.retryWaitTime;
	private boolean isImage = true;

	private QueueItem m_item;

	public DownloadThread(QueueItem item, int index, ThreadGroup group) {
		super(group, "queue thread");
		m_item = item;

		// check if it is archive file
		if (m_item.getComicName() == null && m_item.getVolumeName() == null) {
			isImage = false;
		}
	}

	public void run() {
		String savePath = m_item.getSavePath();
		HttpURLConnection urlConnection = null;
		BufferedInputStream bi = null;
		BufferedOutputStream bo = null;

		try {
			// connect url
			urlConnection = Util.connect(m_item.getLink(), m_item.getReferLink());
			urlConnection.connect();
			final int totalsize = urlConnection.getContentLength();
			m_item.setTotalSize(totalsize / 8000);
			m_item.setSize(0);
			bi = new BufferedInputStream(urlConnection.getInputStream());

			File picFile = new File(savePath);
			if (!picFile.exists())
				picFile.createNewFile();

			// finish download
			if (picFile.length() == totalsize && retry == 0) {
				showMessage(savePath + " 已下載完成");

				// to check the existed image is legal
				if (isImage) {
					checkImage(savePath);
				}
				return;
			}

			// set buffer size
			bo = new BufferedOutputStream(new FileOutputStream(picFile));
			if (isImage) {
				writeToImageFile(bi, bo, totalsize);
			} else {
				byte[] buffer = new byte[8 * 1024]; // Or whatever
				int bytesRead;
				while ((bytesRead = bi.read(buffer)) > 0) {
					bo.write(buffer, 0, bytesRead);
				}
			}

			bo.flush();
			bo.close();

			// to sure that the image is legal
			if (isImage) {
				checkImage(savePath);
			}

			showMessage(savePath + "完成!!");
		} catch (ZeroAvailabeBufferedSizeException e) {
			if (bufRetry > Configuration.retryCount) {
				showMessage("伺服器忙碌中，重試\"" + m_item.getLink() + "\"已超過次數");
				return;
			}
			bufRetry++;
			if (urlConnection != null)
				urlConnection.disconnect();
			showMessage("伺服器忙碌中，重試\"" + m_item.getLink() + "\"中...");
			run();
		} catch (SocketTimeoutException e) {
			if (retry > Configuration.retryCount) {
				showMessage("重試已超過次數");
				return;
			}
			showMessage(savePath + "超出等待時間,重新連線中...");
			retry++;
			if (urlConnection != null)
				urlConnection.disconnect();
			run();
		} catch (ImageCheckException e) {
			if (retry > Configuration.retryCount) {
				showMessage("重試已超過次數");
				return;
			}
			showMessage(savePath + "檔案檢查失敗,重新下載中...");
			retry++;
			if (urlConnection != null)
				urlConnection.disconnect();

			File file = new File(savePath);
			while (file.exists()) {
				file.delete();
				try {
					sleep(Configuration.retryWaitTime);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					break;
				}
			}

			run();
		} catch (FileNotFoundException e) {
			showMessage(m_item.getLink() + "\n下載網址錯誤,關閉下載...");
		} catch (InterruptedException e) { // thread interrupt
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
			try {
				bi.close();
				bo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeToImageFile(BufferedInputStream bi, BufferedOutputStream bo, final int totalsize)
			throws IOException, InterruptedException, ZeroAvailabeBufferedSizeException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int bufferSize = bi.available();
		// int bufferSize=Configuration.flushBuffer; //bufferSize=1024
		byte buf[] = new byte[bufferSize];

		// TODO 若bufferSize=0,下面的for loop會無限迴圈
		for (int i = 0, actualSize = 0; actualSize < totalsize;) {
			// 避免buffer size為0,做最小限度的重試
			boolean exceptionFlag = true;
			for (int index = 0; index < 10; index++) {
				if (bufferSize != 0) {
					exceptionFlag = false;
					break;
				}
				sleep(bufWaitTime);
				bufferSize = bi.available();
			}
			if (exceptionFlag)
				throw new ZeroAvailabeBufferedSizeException();

			// stop download thread
			if (!m_item.getStatus().equals(QueueItem.STATUS_WAIT)) {
				return;
			}

			// read data
			i = bi.read(buf);
			baos.write(buf);
			actualSize += i;
			m_item.setSize(actualSize / 8000);

			// set new buffer size
			bufferSize = bi.available();
			if ((totalsize - actualSize) < bufferSize) {
				bufferSize = totalsize - actualSize;
			}
			buf = new byte[bufferSize];
			// sleep(Configuration.sleepTime);
		}

		// write to file
		baos.flush();
		bo.write(baos.toByteArray());
	}

	private void showMessage(String message) {
		System.out.println(message);
	}

	private void checkImage(String filename) throws ImageCheckException {
		ImageIcon image = new ImageIcon(filename);
		int height = image.getIconHeight();
		int width = image.getIconWidth();
		if (height < 0 || width < 0) {
			throw new ImageCheckException();
		}
	}
}

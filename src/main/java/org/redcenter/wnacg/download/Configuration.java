package org.redcenter.wnacg.download;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Configuration {
	public static String proxyAddress="";
	public static int proxyPort=3128;

	//final
	public static int retryCount = 3;
	public static int threadNum = 3; //10
	public static int connectTimeout = (int) MILLISECONDS.convert(30000, SECONDS); //10000
	public static int readTimeout = (int) MILLISECONDS.convert(30000, SECONDS); //10000
//	public static int sleepTime = (int) MILLISECONDS.convert(50, MILLISECONDS); 
	public static int flushBuffer = 256000;
	public static int retryWaitTime = (int) MILLISECONDS.convert(2, SECONDS);

	public static int totalCount = 0;
	public static int currentCount = 0;
}

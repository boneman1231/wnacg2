package org.redcenter.wnacg.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import org.redcenter.wnacg.download.Configuration;

public class Util
{
    // number of digits of the batch link
    private final static int batchDigits = 3;

    /**
     * Connect
     * 
     * @param addr
     * @param referAddr
     * @return urlConnection
     * @throws IOException
     */
    public static HttpURLConnection connect(String addr, String referAddr)
            throws IOException
    {
        HttpURLConnection urlConnection;
        URL url = new URL(addr);

        // add proxy
        Proxy proxy = Proxy.NO_PROXY;
        if (!Configuration.proxyAddress.equals(""))
        {
            InetSocketAddress proxyAddress = new InetSocketAddress(
                    Configuration.proxyAddress, Configuration.proxyPort);
            proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
        }

        urlConnection = (HttpURLConnection) url.openConnection(proxy);
        urlConnection.setConnectTimeout(Configuration.connectTimeout);
        urlConnection.setReadTimeout(Configuration.readTimeout);

        // connetion setting 
        // urlConnection.setUseCaches(false);
        // urlConnection.setDoOutput(true);
        // urlConnection.setDoInput(true);
        // urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "text/html");
        urlConnection.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
        urlConnection.setRequestProperty("Referer", referAddr);

        return urlConnection;
    }

    public static String getFormatedName(int index)
    {
        String value = String.valueOf(index);
        switch (batchDigits - value.length())
        {
        case 0:
            break;
        case 1:
            value = "0" + value;
            break;
        case 2:
            value = "00" + value;
            break;
        case 3:
            value = "000" + value;
            break;
        }
        return value;
    }

}

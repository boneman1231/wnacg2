package org.redcenter.wnacg.download;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.redcenter.wnacg.Parser;

public class Downloader
{
    public static final String THREAD_GROUP = "ComicDownloader-";
    private static Downloader instance = null;
    private ExecutorService service = null;

    private Downloader()
    {
        service = Executors.newFixedThreadPool(Configuration.threadNum);
    }

    public static Downloader getInstance()
    {
        if (instance == null)
        {
            instance = new Downloader();
        }
        return instance;
    }

    public void download(String address, Parser parser)
    {
        ThreadGroup tGroup = new ThreadGroup(THREAD_GROUP + address);
        List<QueueItem> items = parser.getImages(address);
        for (int i = 0; i < items.size(); i++)
        {
            QueueItem item = items.get(i);
            if (!item.getStatus().equals(QueueItem.STATUS_WAIT))
            {
                continue;
            }
            
            // check folder exist  
            String path = item.getSavePath();
            String dirName = path.substring(0, path.lastIndexOf("/"));
            File dir = new File(dirName);
            if (!dir.exists())
            {
                dir.mkdirs();
            }

            DownloadThread t = new DownloadThread(item, i, tGroup);
            service.submit(t);
        }
    }

    /**
     * terminate all download threads
     */
    public void terminate()
    {
        if (service != null)
        {
            service.shutdown(); // close thread pool
        }
    }
}

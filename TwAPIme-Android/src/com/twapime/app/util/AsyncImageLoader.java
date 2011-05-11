package com.twapime.app.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author ernandes@gmail.com
 */
public class AsyncImageLoader {
	
	private static final int HARD_CACHE_CAPACITY = 10;
	
    private final HashMap<String, Drawable> sHardBitmapCache =
        new LinkedHashMap<String, Drawable>(HARD_CACHE_CAPACITY / 2, 0.75f, true) {
			private static final long serialVersionUID = -4795168423098442895L;
		@Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Drawable> eldest) {
            if (size() > HARD_CACHE_CAPACITY) {
                // Entries push-out of hard reference cache are transferred to soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Drawable>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Drawable>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Drawable>>(HARD_CACHE_CAPACITY / 2);
	
    private List<Handler> queue = new ArrayList<Handler>(); 
    
    private List<String> downloading = new ArrayList<String>();
    
    private Runnable queueTask = new Runnable() {
		@Override
		public void run() {
			while (true) {
				synchronized (AsyncImageLoader.this) {
					for (Handler callback : queue) {
						String url = callback.toString();
						//
				    	Drawable drawable = getDrawableFromCache(url);
				    	if (drawable != null) {
			                Message message = callback.obtainMessage(0, drawable);
			                callback.sendMessage(message);
				    	}
					}
				}
				//
				try {
					synchronized (this) {
						wait();	
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
 
    /**
     * 
     */
    public AsyncImageLoader() {
    	new Thread(queueTask).start();
    }
    
    private void addDrawableToCache(String url, Drawable drawable) {
        if (drawable != null) {
            synchronized (sHardBitmapCache) {
                sHardBitmapCache.put(url, drawable);
            }
        }
    }
    
    private Drawable getDrawableFromCache(String url) {
        // First try the hard reference cache
        synchronized (sHardBitmapCache) {
            final Drawable drawable = sHardBitmapCache.get(url);
            if (drawable != null) {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(url);
                sHardBitmapCache.put(url, drawable);
                return drawable;
            }
        }

        // Then try the soft reference cache
        SoftReference<Drawable> drawableReference = sSoftBitmapCache.get(url);
        if (drawableReference != null) {
            final Drawable drawable = drawableReference.get();
            if (drawable != null) {
                // Bitmap found in soft cache
                return drawable;
            } else {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(url);
            }
        }

        return null;
    }
 
    /**
     * @param imageUrl
     * @param imageCallback
     * @return
     */
    public synchronized Drawable loadDrawable(final String imageUrl, 
    	final ImageLoaderCallback imageCallback) {
    	Drawable drawable = getDrawableFromCache(imageUrl);
    	if (drawable != null) {
    		return drawable;
    	}
    	//
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
            }
            
            @Override
            public String toString() {
            	return imageUrl;
            }
        };
        //
    	if (downloading.contains(imageUrl)) {
    		queue.add(handler);
    		//
    		return null;
    	} else {
    		downloading.add(imageUrl);
    	}
        //
        new Thread() {
            @Override
            public void run() {
            	Log.d("twapime", "imageUrl: " + imageUrl);
            	//
                Drawable drawable = loadImageFromUrl(imageUrl);
                synchronized (AsyncImageLoader.this) {
                	addDrawableToCache(imageUrl, drawable);
                	downloading.remove(imageUrl);
                }
                Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
                //
                synchronized (queueTask) {
                	queueTask.notifyAll();
				}
            }
        }.start();
        //
        return null;
    }
 
    /**
     * @param url
     * @return
     */
    public static Drawable loadImageFromUrl(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpGet req = new HttpGet(url);
        //
        try {
            HttpResponse res = client.execute(req);
            //
            if (res.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return null;
            }
            //
            HttpEntity entity = res.getEntity();
            //
            if (entity != null) {
                InputStream stream = null;
                //
                try {
                    stream = new FlushedInputStream(entity.getContent());
                    //
                    return Drawable.createFromStream(stream, "src");
                } finally {
                    if (stream != null) {
                        stream.close();
                    }
                    //
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            req.abort();
        }
        //
        return null;
    }
    
    /**
     * <p>
     * An InputStream that skips the exact number of bytes provided, unless it
     * reaches EOF.
     * </p>
     * @author ernandes@gmail.com
     */
    static class FlushedInputStream extends FilterInputStream {
        /**
         * @param inputStream
         */
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        /**
         * @see java.io.FilterInputStream#skip(long)
         */
        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            //
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                //
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                //
                totalBytesSkipped += bytesSkipped;
            }
            //
            return totalBytesSkipped;
        }
    }
 
    /**
     * @author ernandes@gmail.com
     */
    public interface ImageLoaderCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }
}

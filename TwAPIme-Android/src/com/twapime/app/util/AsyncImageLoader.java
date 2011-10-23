/*
 * AsyncImageLoader.java
 * 25/05/2011
 * TwAPIme for Android
 * Copyright(c) Ernandes Mourao Junior (ernandes@gmail.com)
 * All rights reserved
 * GNU General Public License (GPL) Version 2, June 1991
 */
package com.twapime.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;

/**
 * @author ernandes@gmail.com
 */
public final class AsyncImageLoader {
	/**
	 * 
	 */
	private static final int HARD_CACHE_CAPACITY = 20;
	
	/**
	 * 
	 */
	private static AsyncImageLoader singleton;
	
    /**
	 * @param context
	 * @return
	 */
	public synchronized static AsyncImageLoader getInstance(Context context) {
		if (singleton == null) {
			singleton = new AsyncImageLoader(context);
		}
		//
		return singleton;
	}
	
    /**                                               
     * 
     */
    private final ConcurrentHashMap<String, SoftReference<Drawable>> softCache;

	/**
	 * 
	 */
	private final HashMap<String, Drawable> hardCache;

	/**
	 * 
	 */
	private final HashMap<String, List<ImageLoaderCallback>> callbacks;

	/**
	 * 
	 */
	private final List<String> urlsBeingDownloaded;
	
	/**
	 * 
	 */
	private File cacheDir;
	
	/**
	 * 
	 */
	private Context context;
	
    /**
     * @param context
     */
    private AsyncImageLoader(Context context) {
    	this.context = context;
    	//
    	urlsBeingDownloaded = new Vector<String>();
    	callbacks = new HashMap<String, List<ImageLoaderCallback>>();
    	hardCache =
            new LinkedHashMap<String, Drawable>(
            	HARD_CACHE_CAPACITY, 0.75f, true) {
    			private static final long serialVersionUID =
    				-4795168423098442895L;
    		@Override
            protected boolean removeEldestEntry(LinkedHashMap.Entry<String,
            	Drawable> eldest) {
                if (size() > HARD_CACHE_CAPACITY) {
                    softCache.put(
                    	eldest.getKey(),
                    	new SoftReference<Drawable>(eldest.getValue()));
                    //
                    return true;
                } else
                    return false;
            }
        };
        softCache =
        	new ConcurrentHashMap<String, SoftReference<Drawable>>(
        		HARD_CACHE_CAPACITY / 2);
        //
        loadCacheDir();
    }
    
    /**
     * @param imageUrl
     * @param imageCallback
     * @return
     */
    public Drawable loadDrawable(final String imageUrl, 
    	ImageLoaderCallback imageCallback) {
    	Drawable drawable = getDrawableFromCache(imageUrl);
    	//
    	if (drawable != null) {
    		return drawable;
    	}
    	//
    	synchronized (callbacks) {
        	List<ImageLoaderCallback> callbacksList = callbacks.get(imageUrl);
        	//
        	if (callbacksList == null) {
        		callbacksList = new Vector<ImageLoaderCallback>();
        		callbacks.put(imageUrl, callbacksList);
        	}
        	//
        	callbacksList.add(imageCallback);
		}
    	//
        if (urlsBeingDownloaded.contains(imageUrl)) {
        	return null;
        }
        //
        urlsBeingDownloaded.add(imageUrl);
        //
        new AsyncTask<String, Void, Drawable>() {
			@Override
			protected Drawable doInBackground(String... params) {
				return loadImageFromUrl(imageUrl);
			}
			
	        @Override
	        protected void onPostExecute(Drawable drawable) {
	        	if (drawable != null) {
	            	addDrawableToCache(imageUrl, drawable);
	            	//
	            	synchronized (callbacks) {
            			for (ImageLoaderCallback callback
            					: callbacks.get(imageUrl)) {
            				callback.imageLoaded(drawable, imageUrl);
						}
            			//
            			callbacks.remove(imageUrl);
					}
	        	}
	        	//
	        	urlsBeingDownloaded.remove(imageUrl);
	        }
        }.execute();
        //
        return null;
    }
    
    /**
     * 
     */
    public void clearCache() {
        synchronized (hardCache) {
            hardCache.clear();
		}
        //
    	softCache.clear();
        //
    	for (File f : cacheDir.listFiles()) {
            f.delete();
        }
    }
 
    /**
	 * @param imageUrl
	 * @return
	 */
	private Drawable getDrawableFromCache(String imageUrl) {
	    synchronized (hardCache) {
	        Drawable drawable = hardCache.get(imageUrl);
	        //
	        if (drawable != null) {
	            // Drawable found in hard cache
	            // Move element to first position, so that it is removed last
	            hardCache.remove(imageUrl);
	            hardCache.put(imageUrl, drawable);
	            //
	            return drawable;
	        }
	    }
	    //
	    SoftReference<Drawable> reference = softCache.get(imageUrl);
	    //
	    if (reference != null) {
	        Drawable drawable = reference.get();
	        //
	        if (drawable != null) {
	            return drawable;
	        }
	    }
	    //
	    return null;
	}

	/**
	 * @param imageUrl
	 * @param drawable
	 */
	private void addDrawableToCache(String imageUrl, Drawable drawable) {
	    if (drawable != null) {
	        synchronized (hardCache) {
	            hardCache.put(imageUrl, drawable);
	        }
	    }
	}

	/**
	 * @param imageUrl
	 * @return
	 */
	private Drawable loadImageFromUrl(String imageUrl) {
	    InputStream stream = null;
	    //
	    try {
			stream = readFromFile(imageUrl);
			//
			if (stream != null) {
				return Drawable.createFromStream(stream, "src");
			}
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		//
	    HttpClient client = new DefaultHttpClient();
	    HttpGet req = new HttpGet(imageUrl);
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
	            try {
	                stream = new FlushedInputStream(entity.getContent());
	                //
	                writeToFile(imageUrl, stream);
	                stream.close();
	                stream = readFromFile(imageUrl);
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
	 * 
	 */
	private void loadCacheDir() {
	    if (Environment.getExternalStorageState().equals(
	    		Environment.MEDIA_MOUNTED)) {
	    	File sdDir = Environment.getExternalStorageDirectory();
	        cacheDir = new File(sdDir.getAbsolutePath() + "/TwAPIme/cache");
		    //
		    if (!cacheDir.exists()) {
	    		cacheDir.mkdirs();	
		    }
	    } else {
	        cacheDir = context.getCacheDir();
	    }
	}
	
	/**
	 * @param imageUrl
	 * @return
	 * @throws IOException 
	 */
	private InputStream readFromFile(String imageUrl)
		throws IOException {
		File file = getFileFromUrl(imageUrl);
		//
		if (file.exists()) {
			return new FileInputStream(file);
		} else {
			return null;
		}
	}
	
	/**
	 * @param imageUrl
	 * @param imageStream
	 * @throws IOException 
	 */
	private void writeToFile(String imageUrl, InputStream imageStream)
		throws IOException {
		File file = getFileFromUrl(imageUrl);
		//
		if (!file.exists()) {
			file.createNewFile();
		}
		//
		FileOutputStream o = new FileOutputStream(file);
		byte[] buffer = new byte[1024];  
		int bytesRead;
		//
		while ((bytesRead = imageStream.read(buffer)) != -1) {  
		  o.write(buffer, 0, bytesRead);  
		}
		//
		o.close();
	}
	
	/**
	 * @param imageUrl
	 * @return
	 */
	private File getFileFromUrl(String imageUrl) {
        return new File(cacheDir, String.valueOf(imageUrl.hashCode()));
	}

	/**
     * <p>
     * An InputStream that skips the exact number of bytes provided, unless it
     * reaches EOF.
     * </p>
     * @author ernandes@gmail.com
     */
    private static class FlushedInputStream extends FilterInputStream {
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

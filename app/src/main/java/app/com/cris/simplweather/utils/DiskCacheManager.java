package app.com.cris.simplweather.utils;

import android.content.Context;
import android.os.Environment;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Cris on 2017/6/27.
 */

public class DiskCacheManager {


    private final String WEATHER_CACHE_KEY = "city_weather";
    private final String CACHE_DIR = "weatherCache";
    private Context mContext;
    private DiskLruCache mDiskLruCache;
    private File mFileDir;
    private DiskLruCache.Editor mEditor;

private static DiskCacheManager mManager;

    private DiskCacheManager(Context context) throws IOException {
        mContext = context;
        mFileDir = getDiskCacheDir(mContext, CACHE_DIR);
        if (!mFileDir.exists()){
            mFileDir.mkdirs();
        }
        mDiskLruCache = DiskLruCache.open(mFileDir,1,1,20*1024*1024);

    }
    public static synchronized DiskCacheManager getInstance(Context context) throws IOException {
        if (mManager == null){
            mManager = new DiskCacheManager(context);
            return mManager;
        }
        return mManager;
    }

    public  void putString(String key, String value){

        try {
            mEditor = mDiskLruCache.edit(hashKeyForDisk(WEATHER_CACHE_KEY + key));
            mEditor.set(0, value);
            mEditor.commit();
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  String getString(String key){
        String mResultStr;
        try {
            DiskLruCache.Snapshot snapShot = mDiskLruCache.get(hashKeyForDisk(WEATHER_CACHE_KEY + key));
            if (snapShot != null) {

                mResultStr = snapShot.getString(0);

            }
            else {
                mResultStr = null;
            }
            return mResultStr;

        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }







    private File getDiskCacheDir(Context context, String name){
        String cachePath;
        //外部存储可用，并且是非可移除的sd卡，才将缓存设置外部存储
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return  new File(cachePath + File.separator + name);
    }


    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}

package app.com.cris.simplweather.net;

/**
 * Created by Cris on 2017/6/25.
 */

public class DownLoadException extends RuntimeException {
    public DownLoadException(String msg){
        super(msg);
    }
}

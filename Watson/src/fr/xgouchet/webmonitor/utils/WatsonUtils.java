package fr.xgouchet.webmonitor.utils;

import java.io.File;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;


public final class WatsonUtils {
    
    
    public static Bitmap getTargetIcon(final Context context, final Target target) {
        File cacheDir = context.getCacheDir();
        File faviconFile = new File(cacheDir, Long.toString(target
                .getTargetId()));
        
        Bitmap bmp = BitmapFactory.decodeFile(faviconFile.getPath());
        
        if (bmp == null) {
            Drawable def = context.getResources().getDrawable(
                    R.drawable.ic_favicon);
            if (BitmapDrawable.class.isAssignableFrom(def.getClass())) {
                bmp = ((BitmapDrawable) def).getBitmap();
            }
        } else {
            if (bmp.getHeight() < 32) {
                bmp = getResizedBitmap(bmp, 32, 32);
            }
        }
        
        return bmp;
    }
    
    public static Bitmap getResizedBitmap(final Bitmap bm, final int newHeight, final int newWidth) {
        
        int width = bm.getWidth();
        int height = bm.getHeight();
        
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        
        return resizedBitmap;
    }
    
    /**
     * @param context
     * @param timestamp
     * @return
     */
    public static String getLastUpdateTime(final Context context, final long timestamp) {
        String lastUpdate;
        if (timestamp == 0) {
            lastUpdate = "";
        } else {
            lastUpdate = context
                    .getString(R.string.ui_last_update, DateFormat.format(
                            "yyyy/MM/dd h:mmaa", new Date(timestamp)));
        }
        return lastUpdate;
    }
    
    /**
     * @param context
     * @param timestamp
     * @return
     */
    public static String getLastCheckTime(final Context context, final long timestamp) {
        String lastUpdate;
        if (timestamp == 0) {
            lastUpdate = "";
        } else {
            lastUpdate = context
                    .getString(R.string.ui_last_check, DateFormat.format(
                            "yyyy/MM/dd h:mmaa", new Date(timestamp)));
        }
        return lastUpdate;
    }
    
    /**
     * @param context
     *            the application context
     * @param status
     *            the error status code
     * @return the string corresponding
     */
    public static String getErrorMessage(final Context context, final int status) {
        int string = getErrorMessage(status);
        
        if (string == 0) {
            string = context.getResources().getIdentifier(
                    "notif_http_" + status, "string", context.getPackageName());
        }
        if (string == 0) {
            string = R.string.notif_error;
        }
        
        return context.getString(string);
    }
    
    /**
     * @param status
     * @return
     */
    public static int getErrorMessage(final int status) {
        int error;
        
        switch (status) {
            case Status.DNS_ERROR:
                error = R.string.notif_dns_error;
                break;
            case Status.URL_FORMAT:
                error = R.string.notif_url_format;
                break;
            default:
                error = 0;
                break;
        }
        return error;
    }
    
    private WatsonUtils() {
    }
}

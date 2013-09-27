package fr.xgouchet.webmonitor.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import fr.xgouchet.webmonitor.common.Settings;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;


public final class WebUtils {
    
    /**
     * @param context
     *            the current application context
     * @return if Internet is available
     */
    public static boolean isInternetAvailable(final Context context) {
        boolean available = false;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null) {
            available = networkInfo.isConnected();
            
            // test to prevent roaming
            if (!Settings.sAllowRoaming) {
                available &= (!networkInfo.isRoaming());
            }
            
            // test for Wifi only
            if (Settings.sWifiOnly) {
                available &= (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
            }
        }
        
        return available;
    }
    
    /**
     * @param target
     *            the target to update
     * @return the content of the HTML page
     */
    public static String getTargetContent(final Target target) {
        String result;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(target.getUrl());
            ResponseHandler<String> handler = new BasicResponseHandler();
            result = client.execute(request, handler);
        }
        catch (HttpResponseException e) {
            Log.w("Watson", "Http status code " + e.getStatusCode());
            result = null;
            target.setStatus(e.getStatusCode());
        }
        catch (UnknownHostException e) {
            Log.w("Watson", "Unknown host (DNS error or internet error?)");
            result = null;
            if (target.getStatus() < Status.GENERIC_ERROR) {
                target.setStatus(Status.DNS_ERROR);
            }
        }
        catch (SocketException e) {
            Log.w("Watson", "Network error, maybe connection was lost");
            result = null;
        }
        catch (IllegalArgumentException e) {
            Log.w("Watson", "Illegal character in URL ? ");
            result = null;
            target.setStatus(Status.URL_FORMAT);
        }
        catch (IllegalStateException e) {
            Log.w("Watson", "Illegal scheme ? ");
            result = null;
            target.setStatus(Status.URL_FORMAT);
        }
        catch (Exception e) {
            Log.e("Watson", "Unknown exception while updating target : "
                    + e.getClass().getName());
            Log.w("Watson", e);
            result = null;
            target.setStatus(Status.UNKNOWN_ERROR);
        }
        
        return result;
    }
    
    /**
     * @param context
     * @param target
     */
    public static void ensureFaviconExists(final Context context, final Target target) {
        File cacheDir = context.getCacheDir();
        File faviconFile = new File(cacheDir, Long.toString(target
                .getTargetId()));
        
        if (faviconFile.exists() && (target.getStatus() != Status.UNKNOWN)) {
            return;
        }
        
        String url = getFaviconUrl(target);
        if (url != null) {
            downloadFile(url, faviconFile);
        }
    }
    
    /**
     * 
     * @param url
     * @param file
     */
    public static void downloadFile(final String url, final File file) {
        try {
            URL source = new URL(url);
            
            InputStream input = new BufferedInputStream(source.openStream());
            OutputStream output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int length = 0;
            
            do {
                length = input.read(buffer, 0, 1024);
                if (length > 0) {
                    output.write(buffer, 0, length);
                }
                
            }
            while (length > 0);
            
            output.flush();
            output.close();
            input.close();
            
        }
        catch (MalformedURLException e) {
            Log.w("Watson", "Unable to download " + url + " : Malformed URL");
        }
        catch (IOException e) {
            Log.w("Watson", "Unable to download " + url + " : IO exception");
        }
    }
    
    private static String getFaviconUrl(final Target target) {
        String url = getLinkFaviconUrl(target);
        if (url == null) {
            url = getAbsoluteUrl(target, "/favicon.ico");
        } else {
            if (url.startsWith("/")) {
                url = getAbsoluteUrl(target, url);
            }
        }
        
        return url;
    }
    
    private static String getLinkFaviconUrl(final Target target) {
        String content = target.getContent();
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        
        Pattern linkPattern = Pattern.compile("(?i)<\\s*link\\s+");
        Matcher linkMatcher = linkPattern.matcher(content);
        String url = null, linkContent;
        int link, end;
        link = end = -1;
        
        do {
            link = linkMatcher.find(link + 1) ? linkMatcher.start() : -1;
            if (link >= 0) {
                end = content.indexOf('>', link + 1);
                linkContent = content.substring(link, end + 1);
                url = extractFaviconLinkUrl(linkContent);
            }
            
        }
        while ((url == null) && (link >= 0));
        
        return url;
    }
    
    /**
     * @param link
     *            a link tag from a html source code
     * @return the favicon url (if any) or null
     */
    private static String extractFaviconLinkUrl(final String link) {
        Pattern hrefPattern = Pattern
                .compile("(?i)href\\s*=\\s*(['\"])([\\w\\d\\.:/=&\\?-]*)\\1");
        Matcher hrefMatcher = hrefPattern.matcher(link);
        
        Pattern relPattern = Pattern
                .compile("(?i)rel\\s*=\\s*(['\"])((shortcut )?icon)\\1");
        Matcher relMatcher = relPattern.matcher(link);
        
        String href;
        
        if (relMatcher.find() && hrefMatcher.find()) {
            href = hrefMatcher.group(2);
        } else {
            href = null;
        }
        
        return href;
    }
    
    /**
     * Given a relative url "/favicon.ico" and a target, returns an absolute url
     * ("http://www.domain.com/favicon.ico")
     * 
     * @param target
     * @param url
     * @return
     */
    private static String getAbsoluteUrl(final Target target, final String url) {
        
        StringBuilder builder = new StringBuilder();
        
        Uri uri = Uri.parse(target.getUrl());
        builder.append(uri.getScheme());
        builder.append("://");
        builder.append(uri.getHost());
        builder.append(url);
        
        return builder.toString();
        
    }
    
    private WebUtils() {
    }
}

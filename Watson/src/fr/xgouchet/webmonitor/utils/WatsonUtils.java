package fr.xgouchet.webmonitor.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;

public final class WatsonUtils {

	private static final String LOG_TAG = "WatsonUtils";

	public static Bitmap getTargetIcon(final Context context,
			final Target target) {
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

	public static Bitmap getResizedBitmap(final Bitmap bm, final int newHeight,
			final int newWidth) {

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
	public static String getLastUpdateTime(final Context context,
			final long timestamp) {
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
	public static String getLastCheckTime(final Context context,
			final long timestamp) {
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

	/**
	 * 
	 * @param status
	 * @return
	 */
	public static int getIconForStatus(final int status) {
		int icon;
		switch (status) {
		case Status.UPDATED:
			icon = R.drawable.ic_updated;
			break;
		case Status.OK:
			icon = R.drawable.ic_ok;
			break;
		case Status.UNKNOWN:
			icon = R.drawable.ic_unknown;
			break;
		case Status.UNKNOWN_ERROR:
		default:
			icon = R.drawable.ic_error;
			break;
		}
		return icon;
	}

	private WatsonUtils() {
	}

	public static File getLogDir(Context context) {
		return context.getExternalFilesDir("logs");
	}

	/**
	 * @param path
	 *            the absolute path to the file to save
	 * @param text
	 *            the text to write
	 * @return if the file was saved successfully
	 */
	public static boolean writeTextFile(final String path, final String text,
			final String encoding) {
		final File file = new File(path);
		OutputStreamWriter writer;
		BufferedWriter out = null;
		String enc = encoding;
		if (TextUtils.isEmpty(enc)) {
			enc = "UTF-8";
		}

		boolean result;
		try {

			writer = new OutputStreamWriter(new FileOutputStream(file), enc);
			out = new BufferedWriter(writer);
			out.write(text);
			out.flush();

			result = true;
		} catch (OutOfMemoryError e) {
			Log.w(LOG_TAG, "Out of memory error", e);
			result = false;
		} catch (IOException e) {
			Log.w(LOG_TAG, "Can't write to file " + path, e);
			result = false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}

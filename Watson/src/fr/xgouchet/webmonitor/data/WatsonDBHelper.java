package fr.xgouchet.webmonitor.data;

import fr.xgouchet.webmonitor.common.DB;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 
 * @author xgouchet
 * 
 */
public class WatsonDBHelper extends SQLiteOpenHelper {
    
    /** Android Log Tag */
    private static final String LOG_TAG = "WatsonDBHelper";
    
    /**
     * Create a helper object to create, open, and manage a database. This
     * method always returns very quickly. The database is not actually created
     * or opened until one of getWritableDatabase or getReadableDatabase is
     * called.
     * 
     * @param context
     *            the current application context
     */
    public WatsonDBHelper(final Context context) {
        super(context, DB.NAME, null, DB.VERSION);
    }
    
    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.v(LOG_TAG, "onCreate");
        db.execSQL(DB.TARGET.CREATE);
    }
    
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        Log.v(LOG_TAG, "onUpgrade");
    }
    
    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        Log.v(LOG_TAG, "onDowngrade");
        
    }
}

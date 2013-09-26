package fr.xgouchet.webmonitor.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public final class TargetDAO {
    
    /** Android Log Tag */
    private static final String LOG_TAG = "TargetDAO";
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Singleton
    //////////////////////////////////////////////////////////////////////////////////////
    
    /** the singleton instance */
    private static TargetDAO sInstance;
    
    /**
     * Get the singleton instance, create it if needed
     */
    public static TargetDAO getSingleton() {
        if (sInstance == null) {
            synchronized (TargetDAO.class) {
                if (sInstance == null) {
                    sInstance = new TargetDAO();
                    
                }
            }
        }
        return sInstance;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // 
    //////////////////////////////////////////////////////////////////////////////////////
    
    private Context mContext;
    private WatsonDBHelper mHelper;
    
    /**
     * @param context
     *            the current application context
     */
    public void init(final Context context) {
        mContext = context;
        mHelper = new WatsonDBHelper(mContext);
    }
    
    /**
     * @param url
     *            the url
     * @return the target associated with the given url (if any)
     */
    public Target getTarget(final String url) {
        
        // Statement
        String where = DB.TARGET.URL + "=?";
        String[] whereArgs = new String[] {
                url
        };
        
        Target target = null;
        
        synchronized (this) {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            
            db.beginTransaction();
            
            try {
                Cursor cursor = db.query(DB.TARGET.TABLE_NAME, null, where, whereArgs, null, null,
                        null);
                
                
                if (cursor.moveToFirst()) {
                    target = buildTargetFromCursor(cursor);
                }
                
                db.setTransactionSuccessful();
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Exception on getTarget(url)", e);
            }
            finally {
                db.endTransaction();
            }
            
            db.close();
        }
        
        return target;
    }
    
    /**
     * @param cursor
     *            a cursor on a target table
     * @return the target instance corresponding to the cursor
     */
    public static Target buildTargetFromCursor(final Cursor cursor) {
        final Target target = new Target();
        int index;
        
        index = cursor.getColumnIndex(DB.TARGET.ID);
        if (index >= 0) {
            target.setTargetId(cursor.getLong(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.URL);
        if (index >= 0) {
            target.setUrl(cursor.getString(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.TITLE);
        if (index >= 0) {
            target.setTitle(cursor.getString(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.CONTENT);
        if (index >= 0) {
            target.setContent(cursor.getString(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.LAST_CHECK);
        if (index >= 0) {
            target.setLastCheck(cursor.getLong(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.LAST_UPDATE);
        if (index >= 0) {
            target.setLastUpdate(cursor.getLong(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.FREQUENCY);
        if (index >= 0) {
            target.setFrequency(cursor.getLong(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.STATUS);
        if (index >= 0) {
            target.setStatus(cursor.getInt(index));
        }
        
        index = cursor.getColumnIndex(DB.TARGET.DIFFERENCE);
        if (index >= 0) {
            target.setMinimumDifference(cursor.getInt(index));
        }
        
        return target;
    }
    
    
    private TargetDAO() {
    }
}

package fr.xgouchet.webmonitor.data;

import fr.xgouchet.webmonitor.common.DB;
import fr.xgouchet.webmonitor.provider.TargetContentProvider;
import android.content.ContentValues;
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
    public static TargetDAO getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (TargetDAO.class) {
                if (sInstance == null) {
                    sInstance = new TargetDAO(context);
                    
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
     * Inserts a target in database
     * 
     * @param target
     *            the target to add
     */
    public void insertTarget(final Target target) {
        ContentValues values = buildContentValuesFromTarget(target);
        
        synchronized (this) {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            
            db.beginTransaction();
            
            try {
                db.insert(DB.TARGET.TABLE_NAME, null, values);
                
                db.setTransactionSuccessful();
                
                notifyContentProvider();
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Exception on insertTarget(target)", e);
            }
            finally {
                db.endTransaction();
            }
            
            db.close();
        }
        
    }
    
    /**
     * Updates the data of a target
     * 
     * @param target
     *            the target content to update
     */
    public void updateTarget(final Target target) {
        
        // Statement
        ContentValues values = buildContentValuesFromTarget(target);
        
        String where = DB.TARGET.ID + "=?";
        String[] whereArgs = new String[] {
                Long.toString(target.getTargetId())
        };
        
        synchronized (this) {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            
            db.beginTransaction();
            
            try {
                db.update(DB.TARGET.TABLE_NAME, values, where, whereArgs);
                
                db.setTransactionSuccessful();
                
                notifyContentProvider();
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Exception on insertTarget(target)", e);
            }
            finally {
                db.endTransaction();
            }
            
            db.close();
        }
    }
    
    /**
     * Deletes the target from database
     * 
     * @param target
     *            the target content to delete
     */
    public void deleteTarget(final Target target) {
        
        // Statement       
        String where = DB.TARGET.ID + "=?";
        String[] whereArgs = new String[] {
                Long.toString(target.getTargetId())
        };
        
        synchronized (this) {
            SQLiteDatabase db = mHelper.getReadableDatabase();
            
            db.beginTransaction();
            
            try {
                db.delete(DB.TARGET.TABLE_NAME, where, whereArgs);
                
                db.setTransactionSuccessful();
                
                notifyContentProvider();
            }
            catch (Exception e) {
                Log.e(LOG_TAG, "Exception on insertTarget(target)", e);
            }
            finally {
                db.endTransaction();
            }
            
            db.close();
        }
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Utilities
    //////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Notifies the Content Provider that the underlying data has changed
     */
    private void notifyContentProvider() {
        mContext.getContentResolver().notifyChange(
                TargetContentProvider.BASE_URI, null);
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
    
    public static ContentValues buildContentValuesFromTarget(final Target target) {
        final ContentValues contentValues = new ContentValues();
        
        
        contentValues.put(DB.TARGET.URL, target.getUrl());
        contentValues.put(DB.TARGET.TITLE, target.getTitle());
        contentValues.put(DB.TARGET.CONTENT, target.getContent());
        contentValues.put(DB.TARGET.LAST_CHECK, target.getLastCheck());
        contentValues.put(DB.TARGET.LAST_UPDATE, target.getLastUpdate());
        contentValues.put(DB.TARGET.FREQUENCY, target.getFrequency());
        contentValues.put(DB.TARGET.STATUS, target.getStatus());
        contentValues.put(DB.TARGET.DIFFERENCE, target.getMinimumDifference());
        
        return contentValues;
    }
    
    /**
     * @param context
     *            the current application context
     */
    private TargetDAO(final Context context) {
        mContext = context;
        mHelper = new WatsonDBHelper(mContext);
    }
}

package fr.xgouchet.webmonitor.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import fr.xgouchet.webmonitor.data.DB;
import fr.xgouchet.webmonitor.data.WatsonDBHelper;


public class TargetContentProvider extends ContentProvider {
    
    
    private WatsonDBHelper mArticleDB;
    
    public static final String GENERIC_MIME = "vnd.android.cursor.item/vnd.fr.xgouchet.webmonitor.target";
    
    public static final String AUTHORITY = "fr.xgouchet.webmonitor";
    
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/target");
    
    @Override
    public String getType(final Uri uri) {
        String mime;
        
        if (AUTHORITY.equalsIgnoreCase(uri.getAuthority())) {
            mime = GENERIC_MIME;
        } else {
            mime = null;
        }
        
        return mime;
    }
    
    @Override
    public boolean onCreate() {
        mArticleDB = new WatsonDBHelper(getContext());
        return true;
    }
    
    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
            final String[] selectionArgs,
            final String sortOrder) {
        
        Cursor cursor;
        
        if (AUTHORITY.equalsIgnoreCase(uri.getAuthority())) {
            cursor = queryTarget(projection, selection, selectionArgs, sortOrder);
        } else {
            cursor = null;
        }
        
        return cursor;
    }
    
    @Override
    public int update(final Uri uri, final ContentValues values, final String selection,
            final String[] selectionArgs) {
        return 0;
    }
    
    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        return null;
    }
    
    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        return 0;
    }
    
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // 
    //////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Perform a query by combining all current settings and the information
     * passed into this method.
     * 
     * @param projectionIn
     *            A list of which columns to return. Passing null will return
     *            all columns, which is discouraged to prevent reading data from
     *            storage that isn't going to be used.
     * @param selection
     *            A filter declaring which rows to return, formatted as an SQL
     *            WHERE clause (excluding the WHERE itself). Passing null will
     *            return all rows for the given URL.
     * @param selectionArgs
     *            You may include ?s in selection, which will be replaced by the
     *            values from selectionArgs, in order that they appear in the
     *            selection. The values will be bound as Strings.
     * @param sortOrder
     *            How to order the rows, formatted as an SQL ORDER BY clause
     *            (excluding the ORDER BY itself). Passing null will use the
     *            default sort order, which may be unordered.
     * @return a cursor over the result set
     */
    private Cursor queryTarget(final String[] projection,
            final String selection, final String[] selectionArgs,
            final String sortOrder) {
        final SQLiteDatabase sqldb = mArticleDB.getReadableDatabase();
        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        
        builder.setTables(DB.TARGET.TABLE_NAME);
        
        return builder.query(sqldb, projection, selection, selectionArgs, null,
                null, sortOrder);
    }
    
}

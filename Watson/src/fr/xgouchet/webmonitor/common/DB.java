package fr.xgouchet.webmonitor.common;

import java.util.LinkedList;
import java.util.List;


public final class DB {
    
    public static final String NAME = "cache_info.db";
    public static final int VERSION = 1;
    
    public static final String INTEGER = "INTEGER";
    public static final String TEXT = "TEXT";
    public static final String NULL = "NULL";
    public static final String REAL = "REAL";
    public static final String BLOB = "BLOB";
    
    private static final String BLANK = " ";
    private static final String COMMA = ",";
    private static final String PRIMARY_KEY = "PRIMARY KEY";
    private static final String AUTOINCREMENT = "AUTOINCREMENT";
    private static final String NOT_NULL = "NOT NULL";
    
    private static final String DROP_IF_EXIST = "DROP TABLE IF EXISTS ";
    private static final String CREATE_TABLE = "CREATE TABLE";
    
    
    public static final class TARGET {
        
        public static final String TABLE_NAME = "watson_target";
        
        public static final String ID = "_id";
        public static final String TITLE = "_title";
        public static final String URL = "_url";
        public static final String CONTENT = "_content";
        public static final String LAST_CHECK = "_lastcheck";
        public static final String LAST_UPDATE = "_lastupdate";
        public static final String FREQUENCY = "_frequency";
        public static final String DIFFERENCE = "_difference";
        public static final String STATUS = "_status";
        
        private static final List<Column> COLUMNS = new LinkedList<Column>() {
            
            private static final long serialVersionUID = 1L;
            
            {
                add(new Column(ID, INTEGER, true, true, false));
                add(new Column(TITLE, TEXT, true));
                add(new Column(URL, TEXT, true));
                add(new Column(CONTENT, TEXT));
                add(new Column(LAST_CHECK, INTEGER));
                add(new Column(LAST_UPDATE, INTEGER));
                add(new Column(FREQUENCY, TEXT));
                add(new Column(DIFFERENCE, TEXT));
                add(new Column(STATUS, TEXT));
            }
        };
        
        public static final String CREATE = createStatement(TABLE_NAME, COLUMNS);
        
        public static final String DROP = DROP_IF_EXIST + TABLE_NAME;
        
        private TARGET() {
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // TABLE UTILS
    //////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * 
     */
    private static class Column {
        
        private final String mName;
        private final String mType;
        private final boolean mPrimaryKey, mAutoIncrement, mNotNull;
        
        public Column(final String name, final String type) {
            this(name, type, false, false, false);
        }
        
        public Column(final String name, final String type, final boolean notNull) {
            this(name, type, false, false, notNull);
        }
        
        public Column(final String name, final String type, final boolean primaryKey,
                final boolean autoIncrement, final boolean notNull) {
            mName = name;
            mType = type;
            mPrimaryKey = primaryKey;
            mAutoIncrement = autoIncrement;
            mNotNull = notNull;
        }
        
        /**
         * @return the SQL description of this column
         */
        public String getDescription() {
            StringBuilder builder = new StringBuilder();
            
            builder.append(mName);
            builder.append(BLANK);
            builder.append(mType);
            
            if (mPrimaryKey) {
                builder.append(BLANK);
                builder.append(PRIMARY_KEY);
            }
            
            if (mAutoIncrement) {
                builder.append(BLANK);
                builder.append(AUTOINCREMENT);
            }
            
            if (mNotNull) {
                builder.append(BLANK);
                builder.append(NOT_NULL);
            }
            
            return builder.toString();
        }
    }
    
    /**
     * @return the table create SQLite statement
     */
    private static String createStatement(final String tableName, final List<Column> columns) {
        StringBuilder builder = new StringBuilder();
        
        builder.append(CREATE_TABLE);
        builder.append(BLANK);
        builder.append(tableName);
        builder.append(" (");
        
        for (Column column : columns) {
            builder.append(column.getDescription());
            builder.append(COMMA);
        }
        
        builder.setLength(builder.length() - 1);
        
        builder.append(")");
        
        return builder.toString();
    }
    
    private DB() {
    }
}

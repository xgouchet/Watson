package fr.xgouchet.webmonitor.data;


public final class Status {
    
    public static final int UNKNOWN = -1;
    public static final int OK = 0;
    public static final int UPDATED = 42;
    
    public static final int DNS_ERROR = 601;
    public static final int URL_FORMAT = 602;
    public static final int UNKNOWN_ERROR = 666;
    
    private Status() {
    }
}

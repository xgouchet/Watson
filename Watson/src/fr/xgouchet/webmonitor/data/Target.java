package fr.xgouchet.webmonitor.data;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * A Target for Dr Watson to watch
 * 
 * @author xgouchet
 * 
 */
public class Target implements Parcelable {
    
    private String mUrl, mTitle, mContent;
    
    private long mLastCheck, mLastUpdate, mFrequency, mTargetId;
    
    private int mStatus, mMinimumDifference;
    
    /**
     * Default constructor
     */
    public Target() {
        mContent = "";
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Parcelable Implementation
    //////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructor from {@link Parcel}
     * 
     * @param source
     *            the parcel to read the data from
     * @see Parcelable
     */
    public Target(final Parcel source) {
        mUrl = source.readString();
        mTitle = source.readString();
        mContent = source.readString();
        mLastCheck = source.readLong();
        mLastUpdate = source.readLong();
        mFrequency = source.readLong();
        mStatus = source.readInt();
        mTargetId = source.readLong();
        mMinimumDifference = source.readInt();
    }
    
    
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        
        assert (mUrl != null);
        assert (mTitle != null);
        assert (mContent != null);
        
        dest.writeString(mUrl);
        dest.writeString(mTitle);
        dest.writeString(mContent);
        dest.writeLong(mLastCheck);
        dest.writeLong(mLastUpdate);
        dest.writeLong(mFrequency);
        dest.writeInt(mStatus);
        dest.writeLong(mTargetId);
        dest.writeInt(mMinimumDifference);
    }
    
    /**
     * The parcelable creator instance
     * 
     * @see Parcelable
     */
    public static final Parcelable.Creator<Target> CREATOR = new Parcelable.Creator<Target>() {
        
        @Override
        public Target createFromParcel(final Parcel in) {
            return new Target(in);
        }
        
        @Override
        public Target[] newArray(final int size) {
            return new Target[size];
        }
    };
    
    /**
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Getters
    //////////////////////////////////////////////////////////////////////////////////////
    
    public String getUrl() {
        return mUrl;
    }
    
    
    public String getTitle() {
        return mTitle;
    }
    
    
    public String getContent() {
        return mContent;
    }
    
    
    public long getLastCheck() {
        return mLastCheck;
    }
    
    
    public long getLastUpdate() {
        return mLastUpdate;
    }
    
    
    public long getFrequency() {
        return mFrequency;
    }
    
    
    public long getTargetId() {
        return mTargetId;
    }
    
    
    public int getStatus() {
        return mStatus;
    }
    
    
    public int getMinimumDifference() {
        return mMinimumDifference;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Setters
    //////////////////////////////////////////////////////////////////////////////////////
    
    public void setUrl(final String url) {
        mUrl = url;
    }
    
    
    public void setTitle(final String title) {
        mTitle = title;
    }
    
    
    public void setContent(final String content) {
        mContent = content;
    }
    
    
    public void setLastCheck(final long lastCheck) {
        mLastCheck = lastCheck;
    }
    
    
    public void setLastUpdate(final long lastUpdate) {
        mLastUpdate = lastUpdate;
    }
    
    
    public void setFrequency(final long frequency) {
        mFrequency = frequency;
    }
    
    
    public void setTargetId(final long targetId) {
        mTargetId = targetId;
    }
    
    
    public void setStatus(final int status) {
        mStatus = status;
    }
    
    
    public void setMinimumDifference(final int minimumDifference) {
        mMinimumDifference = minimumDifference;
    }
    
    //////////////////////////////////////////////////////////////////////////////////////
    // Misc
    //////////////////////////////////////////////////////////////////////////////////////
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"");
        builder.append(mTitle);
        builder.append("\" [");
        builder.append(mUrl);
        builder.append("] #" + mStatus);
        
        return builder.toString();
    }
    
}

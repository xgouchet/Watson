package fr.xgouchet.webmonitor.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import fr.xgouchet.webmonitor.R;
import fr.xgouchet.webmonitor.data.DB;
import fr.xgouchet.webmonitor.data.Status;
import fr.xgouchet.webmonitor.data.Target;
import fr.xgouchet.webmonitor.data.TargetDAO;
import fr.xgouchet.webmonitor.data.WatsonUtils;


public class TargetAdapter extends SimpleCursorAdapter {
    
    
    protected Context mAppContext;
    
    /**
     * @param context
     *            the current application context
     */
    public TargetAdapter(final Context context, final Cursor cursor) {
        
        super(context, R.layout.item_target, cursor,
                new String[] {
                        DB.TARGET.TITLE
                }, new int[] {
                        R.id.txtTitle
                }, 0);
        mAppContext = context;
    }
    
    /**
     * @see android.widget.SimpleCursorAdapter#bindView(android.view.View, android.content.Context,
     *      android.database.Cursor)
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        
        if (cursor.getColumnIndex(DB.TARGET.TITLE) < 0) {
            return;
        }
        Target target = TargetDAO.buildTargetFromCursor(cursor);
        
        // Title
        int icon = getIconForStatus(target.getStatus());
        setViewText((TextView) view.findViewById(R.id.txtTitle),
                target.getTitle());
        ((TextView) view.findViewById(R.id.txtTitle))
                .setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0);
        
        // Subtitle 1
        setViewText(
                (TextView) view.findViewById(R.id.txtSubtitle1),
                WatsonUtils.getLastCheckTime(mAppContext, target.getLastCheck()));
        
        // Subtitle 2 
        if (target.getStatus() < 300) {
            setViewText(
                    (TextView) view.findViewById(R.id.txtSubtitle2),
                    WatsonUtils.getLastUpdateTime(mAppContext,
                            target.getLastUpdate()));
        } else {
            setViewText(
                    (TextView) view.findViewById(R.id.txtSubtitle2),
                    WatsonUtils.getErrorMessage(mAppContext, target.getStatus()));
        }
        
        // icon
        Bitmap bmp = WatsonUtils.getTargetIcon(mAppContext, target);
        if (bmp == null) {
            ((ImageView) view.findViewById(R.id.imgIcon))
                    .setImageResource(R.drawable.ic_favicon);
        } else {
            ImageView image = ((ImageView) view.findViewById(R.id.imgIcon));
            LayoutParams params = image.getLayoutParams();
            params.height = bmp.getHeight();
            image.setImageBitmap(bmp);
            image.setLayoutParams(params);
        }
    }
    
    private int getIconForStatus(final int status) {
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
}

package cu.tecnomatica.android.glp.activities.localizacion;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.oscim.android.MapView;

public class NewInfoWindow
{
    protected View mView;
    protected boolean mIsVisible = false;
    protected RelativeLayout mLayout;
    private android.widget.RelativeLayout.LayoutParams mLayoutPos;

    private MapView mMap;

    /**
     * @param layoutResId the id of the view resource.
     * @param mapView     the mapview on which is hooked the view
     */
    public NewInfoWindow(int layoutResId, MapView mapView) {
        ViewGroup parent = (ViewGroup) mapView.getParent();
        Context context = mapView.getContext();
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(layoutResId, parent, false);

        RelativeLayout.LayoutParams rlp =
                new RelativeLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        mLayout = new RelativeLayout(context);
        mLayout.setWillNotDraw(true);
        mLayout.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        mLayout.setLayoutParams(rlp);
        mLayoutPos = rlp;
        mView.setDrawingCacheEnabled(true);
        mLayout.addView(mView);

        mIsVisible = false;
        mLayout.setVisibility(View.GONE);
        mMap = mapView;

        parent.addView(mLayout);
    }

    /**
     * Returns the Android view. This allows to set its content.
     *
     * @return the Android view
     */
    public View getView() {
        return (mView);
    }

    private int mHeight;

    /**
     * open the window at the specified position.
     *
     * @param item    the item on which is hooked the view
     * @param offsetX (&offsetY) the offset of the view to the position, in pixels.
     *                This allows to offset the view from the marker position.
     * @param offsetY ...
     */
    public void open(NewExtendedMarkerItem item, int offsetX, int offsetY) {

        //onOpen(item);
        //close();

        mView.buildDrawingCache();

        mHeight = mMap.getHeight();
        mLayout.setVisibility(View.VISIBLE);
        mIsVisible = true;

    }

    public void position(int x, int y) {
        RelativeLayout.LayoutParams rlp = mLayoutPos;
        rlp.leftMargin = x;
        rlp.rightMargin = -x;
        rlp.topMargin = y;
        rlp.bottomMargin = mHeight / 2 - y;
        mLayout.setLayoutParams(rlp);
        mLayout.requestLayout();
    }

    public void close() {

        if (mIsVisible) {
            mIsVisible = false;
            mLayout.setVisibility(View.GONE);
            //onClose();
        }
    }

    public boolean isOpen() {
        return mIsVisible;
    }

    // Abstract methods to implement:
    //public abstract void onOpen(NewExtendedMarkerItem item);

    //public abstract void onClose();
}

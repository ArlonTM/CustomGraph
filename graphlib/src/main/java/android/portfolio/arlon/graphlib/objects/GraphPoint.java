package android.portfolio.arlon.graphlib.objects;

import android.graphics.Point;
import android.os.Parcel;
import android.portfolio.arlon.graphlib.R;
import android.text.TextUtils;

/**
 * Created by Arlon Mukeba on 14/09/2016.
 */
public abstract class GraphPoint extends Point {

    protected int mColor;
    protected String mLabel;

    /*public GraphPoint(int x, int y) {
        this(x, y, -1);
    }*/

    public GraphPoint(int x, int y/*, int color*/) {
        this(x, y/*, color*/, null);
    }

    public GraphPoint(int x, int y/*, int color*/, String label) {
        super(x, y);

        /*if (color != (-1)) {
            color = R.color.ocean_boat_blue;
        }*/

//        setColor(color);
        setColourRule();

        if (TextUtils.isEmpty(label)) {
            label = x + "," + y;
        }

        setLabel(label);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    protected void setLabel(String label) {
        mLabel = label;
    }

    protected abstract void setColourRule();
}
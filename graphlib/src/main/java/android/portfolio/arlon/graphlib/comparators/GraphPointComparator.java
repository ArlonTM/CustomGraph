package android.portfolio.arlon.graphlib.comparators;

import android.portfolio.arlon.graphlib.objects.GraphPoint;

import java.util.Comparator;

/**
 * Created by Arlon Mukeba on 14/09/2016.
 */
public class GraphPointComparator implements Comparator<GraphPoint> {

    public enum SortType {
        HORIZONTAL, VERTICAL
    }

    private SortType mSortType = SortType.HORIZONTAL;

    public GraphPointComparator(SortType sortType) {
        mSortType = sortType;
    }

    @Override
    public int compare(GraphPoint pt1, GraphPoint pt2) {
        return (mSortType == SortType.VERTICAL) ? compare(pt1.y, pt2.y) : compare(pt1.x, pt2.x);
    }

    private int compare(double pt1, double pt2) {
        return (pt1 < pt2) ? (-1) : ((pt1 > pt2) ? 1 : 0);
    }
}
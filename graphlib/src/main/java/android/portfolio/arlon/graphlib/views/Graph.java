package android.portfolio.arlon.graphlib.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.portfolio.arlon.graphlib.R;
import android.portfolio.arlon.graphlib.comparators.GraphPointComparator;
import android.portfolio.arlon.graphlib.helpers.DialogHelper;
import android.portfolio.arlon.graphlib.interfaces.GraphPointTapped;
import android.portfolio.arlon.graphlib.objects.GraphPoint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

/**
 * Created by Arlon Mukeba on 14/09/2016.
 */
public class Graph extends View {

    public static final String SHOULD_FILL_GRAPH = "SHOULD_FILL_GRAPH";
    public static final String BORDER_TYPE = "BORDER_TYPE";
    public static final String BORDER_SIZE = "BORDER_SIZE";
    public static final String SHOULD_REVERSE_GRAPH = "SHOULD_REVERSE_GRAPH";
    public static final String JOINT_TYPE = "JOINT_TYPE";
    public static final String POINT_TYPE = "POINT_TYPE";
    public static final String POINT_SIZE = "POINT_SIZE";
    public static final String GRAPH_WIDTH = "GRAPH_WIDTH";
    public static final String GRAPH_HEIGHT = "GRAPH_HEIGHT";
    public static final String SCALE_TYPE = "SCALE_TYPE";

    public static final int BORDER_TYPE_NONE = 0;
    public static final int BORDER_TYPE_PLAIN = 1;
    public static final int BORDER_TYPE_DASHED = 2;

    public static final int JOINT_TYPE_STRAIGHT = 0;
    public static final int JOINT_TYPE_ROUND = 1;

    public static final int POINT_TYPE_CIRCLE = 0;
    public static final int POINT_TYPE_SQUARE = 1;

    public static final int SCALE_TYPE_FIT = 0;
    public static final int SCALE_TYPE_OVERFLOW = 1;

    private int mGraphWidth = -1;
    private int mGraphHeight = -1;
    private int mScaleType = -1;
    private boolean mShouldFill = false;
    private int mFillColour = -1;
    private int mBorderType = 0;
    private int mBorderSize = 2;
    private int mBorderColour = -1;
    private boolean mShouldReverse = false;
    private int mJointType = 0;
    private int mPointSize = -1;
    private int mPointType = 0;
    private int mGraphTopMargin = -1;
    private int mXSpace = -1;
    private int mYSpace = -1;
    private Canvas mCanvas = null;
    private ArrayList<GraphPoint> mGraphPoints = new ArrayList<GraphPoint>();
    private Handler mHandler = new Handler();
    private GraphPointTapped mGraphPointTapListener = null;

    public Graph(Context context) {
        super(context);
        initialize(null);
    }

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    public Graph(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(attrs);
    }

    private void initialize(AttributeSet attrs) {
        mGraphPointTapListener = new GraphPointTapListener();

        if (attrs == null) {
            return;
        }

        initDimensions();
        initColours();
        processAttributes(attrs);
        applyAttributes();
    }

    private void initDimensions() {
        mPointSize = (int) getContext().getResources().getDimension(
                R.dimen.point_circle_default_radius);

        mGraphTopMargin = mPointSize / 2; //(int) getContext().getResources().getDimension(R.dimen.graph_top_margin);

//        mGraphWidth = getScreenWidth();
        mGraphHeight = 400;
    }

    private int getScreenWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    private void initColours() {
        mFillColour = getContext().getResources().getColor(R.color.silver);
        mBorderColour = getContext().getResources().getColor(R.color.ocean_boat_blue);
    }

    private void processAttributes(AttributeSet attrs) {
        TypedArray attrsArr = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.Graph, 0, 0);

        mShouldFill = attrsArr.getBoolean(R.styleable.Graph_shouldFill, false);

        if (mShouldFill) {
            mFillColour = attrsArr.getColor(R.styleable.Graph_fillColour, mFillColour);
        }

        mBorderType = attrsArr.getInt(R.styleable.Graph_borderType, BORDER_TYPE_PLAIN);
        mBorderColour = attrsArr.getColor(R.styleable.Graph_borderColour, mBorderColour);
        mBorderSize = attrsArr.getInt(R.styleable.Graph_borderSize, mBorderSize);

        mShouldReverse = attrsArr.getBoolean(R.styleable.Graph_shouldReverse, false);

        mPointSize = attrsArr.getDimensionPixelSize(R.styleable.Graph_pointSize, mPointSize);
        mPointType = attrsArr.getInt(R.styleable.Graph_pointType, POINT_TYPE_CIRCLE);
        mJointType = attrsArr.getInt(R.styleable.Graph_jointType, JOINT_TYPE_STRAIGHT);

        mScaleType = attrsArr.getInt(R.styleable.Graph_scaleType, SCALE_TYPE_OVERFLOW);

        mGraphWidth = attrsArr.getDimensionPixelSize(R.styleable.Graph_graphWidth, getScreenWidth());
        mGraphHeight = attrsArr.getDimensionPixelSize(R.styleable.Graph_graphHeight, mGraphHeight);
    }

    private void applyAttributes() {

    }

    public void setItems(ArrayList<GraphPoint> graphPoints) {
        mGraphPoints = graphPoints;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        computeGraphDimensions(widthMeasureSpec, heightMeasureSpec);
        computeXSpace();
        computeYSpace();
    }

    private int computeDrawingHeight() {
        int graphHeight = getMeasuredHeight();
        int graphDrawingHeight = graphHeight - (mPointSize * 2);
        graphDrawingHeight -= mGraphTopMargin;
        return graphDrawingHeight;
    }

    private int computeHighestPoint() {
        if ((mGraphPoints == null) || (mGraphPoints.isEmpty())) return 1;

        ArrayList<GraphPoint> graphPoints = new ArrayList<GraphPoint>();
        graphPoints.addAll(mGraphPoints);

        Collections.sort(graphPoints, new GraphPointComparator(GraphPointComparator.SortType.VERTICAL));

        return graphPoints.get(graphPoints.size() - 1).y;
    }

    private void computeXSpace() {
        mXSpace = getMeasuredWidth() / (mGraphPoints.size() + 2);
    }

    private void computeYSpace() {
        int highestPt = computeHighestPoint();
        mYSpace = getMeasuredHeight();
        mYSpace -= mGraphTopMargin;
        mYSpace -= (2 * mPointSize);
        mYSpace /= highestPt;
    }

    private void computeGraphDimensions(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int screenWidth = getScreenWidth();

        if (mScaleType == SCALE_TYPE_FIT) {
            if (mGraphWidth < screenWidth) {
                width = mGraphWidth;
            } else {
                width = screenWidth;
            }
        } else {
            width = mGraphWidth;
        }

        setMeasuredDimension(width, mGraphHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mGraphPoints == null) || (mGraphPoints.isEmpty())) {
            return;
        }

        mCanvas = canvas;

        plotGraph();
    }

    public void renderGraph() {
        invalidate();
        requestLayout();
    }

    protected void plotGraph() {
        ArrayList<GraphPoint> graphPoints = adjustPoints();

        Path wallPath = buildPath(graphPoints);

        if (mShouldFill) {
            drawFullPolygon(wallPath);
        }

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(mBorderColour);
        paint.setStrokeWidth(mBorderSize);

        if (mBorderType != BORDER_TYPE_NONE) {
            drawPolygonPath(paint, wallPath);
        }

        drawPoints(graphPoints);
    }

    private ArrayList<GraphPoint> adjustPoints() {
        ArrayList<GraphPoint> graphPoints = new ArrayList<GraphPoint>();

        int highestPt = computeHighestPoint();
        int graphDrawingHeight = computeDrawingHeight();

        for (GraphPoint graphPoint : mGraphPoints) {
            graphPoints.add(adjustPoint(graphPoint, highestPt, graphDrawingHeight));
        }

        return graphPoints;
    }

    public GraphPoint adjustPoint(GraphPoint graphPoint, int highestPt, int graphDrawingHeight) {
        int x = (1 + graphPoint.x) * mXSpace;
        int y = adjustYCoord(graphPoint.y, highestPt, graphDrawingHeight);

        CustomPoint pt = new CustomPoint(x,y);
        pt.setColor(graphPoint.getColor());

        return pt;
    }

    private int adjustYCoord(int y, int heighestPt, int graphDrawingHeight) {
        y = (int) (((float) graphDrawingHeight) * (((float) y) / ((float) heighestPt)));

        if (mShouldReverse) {
            y = graphDrawingHeight - y;
        }

        y += mPointSize;
        y += mGraphTopMargin;
        y += (mPointSize / 2);

        return y;
    }

    private Path buildPath(ArrayList<GraphPoint> graphPoints/*, boolean lineDrawing*/) {
        Path path = new Path();
        path.reset();

        int bottomY = getMeasuredHeight();

        path.moveTo(0, bottomY);

        int x = 0;
        int y = 0;

        for (GraphPoint point : graphPoints) {
            if (point.getColor() != (-1)) {
                x = point.x;
                y = point.y;

                path.lineTo(x, y);
            }
        }

        if (mJointType == 1) {
            bottomY += 10;
        }

        path.lineTo(getMeasuredWidth(), bottomY);

        return path;
    }

    private void drawFullPolygon(Path polygonContour) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(mFillColour);

        if (mJointType == JOINT_TYPE_ROUND) {
            paint.setDither(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setPathEffect(new CornerPathEffect(10));
        }

        mCanvas.drawPath(polygonContour, paint);
    }

    private void drawPolygonPath(Paint paint, Path polygonContour) {
        if (mJointType == JOINT_TYPE_ROUND) {
            paint.setDither(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setPathEffect(new CornerPathEffect(10));
        }

        if (mBorderType == BORDER_TYPE_DASHED) {
            paint.setPathEffect(new DashPathEffect(new float[] {10, 10}, 0));
        }

        mCanvas.drawPath(polygonContour, paint);
    }

    private void drawPoints(ArrayList<GraphPoint> graphPoints) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(mFillColour);
        paint.setStrokeWidth(2);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        drawPoints(graphPoints, paint);
    }

    private void drawPoints(ArrayList<GraphPoint> graphPoints, Paint paint) {
        for (GraphPoint point : graphPoints) {
            if (point.getColor() == (-1)) {
                continue;
            }

            drawSinglePoint(paint, point);
        }
    }

    private void drawSinglePoint(Paint paint, GraphPoint point) {
        int x = point.x;
        int y = point.y;

        paint.setColor(getResources().getColor(point.getColor()));

        int radius = mPointSize / 2;

        if (mPointType == POINT_TYPE_CIRCLE) {
            mCanvas.drawCircle(x, y, radius, paint);
        } else {
            int rectLeft = x - radius;
            int rectRight = x + radius;
            int rectTop = y - radius;
            int rectBottom = y + radius;

            Rect rect = new Rect(rectLeft, rectTop, rectRight, rectBottom);
            mCanvas.drawRect(rect, paint);
        }
    }

    public void setAttributes(Hashtable<String, Object> attributes) {
        if (attributes.containsKey(SHOULD_FILL_GRAPH)) {
            mShouldFill = Boolean.parseBoolean("" + attributes.get(SHOULD_FILL_GRAPH));
            Log.d(SHOULD_FILL_GRAPH, "" + mShouldFill);
        }

        if (attributes.containsKey(BORDER_TYPE)) {
            mBorderType = Integer.parseInt("" + attributes.get(BORDER_TYPE));
            Log.d(BORDER_TYPE, "" + mBorderType);
        }

        if (attributes.containsKey(BORDER_SIZE)) {
            mBorderSize = Integer.parseInt("" + attributes.get(BORDER_SIZE));
            Log.d(BORDER_SIZE, "" + mBorderSize);
        }

        if (attributes.containsKey(GRAPH_WIDTH)) {
            mGraphWidth = Integer.parseInt("" + attributes.get(GRAPH_WIDTH));
            Log.d(GRAPH_WIDTH, "" + mGraphWidth);
        }

        if (attributes.containsKey(GRAPH_HEIGHT)) {
            mGraphHeight = Integer.parseInt("" + attributes.get(GRAPH_HEIGHT));
            Log.d(GRAPH_HEIGHT, "" + mGraphHeight);
        }

        if (attributes.containsKey(SCALE_TYPE)) {
            mScaleType = Integer.parseInt("" + attributes.get(SCALE_TYPE));
            Log.d(SCALE_TYPE, "" + mScaleType);
        }

        if (attributes.containsKey(SHOULD_REVERSE_GRAPH)) {
            mShouldReverse = Boolean.parseBoolean("" + attributes.get(SHOULD_REVERSE_GRAPH));
            Log.d(SHOULD_REVERSE_GRAPH, "" + mShouldReverse);
        }

        if (attributes.containsKey(JOINT_TYPE)) {
            mJointType = Integer.parseInt("" + attributes.get(JOINT_TYPE));
            Log.d(JOINT_TYPE, "" + mJointType);
        }

        if (attributes.containsKey(POINT_TYPE)) {
            mPointType = Integer.parseInt("" + attributes.get(POINT_TYPE));
            Log.d(POINT_TYPE, "" + mPointType);
        }

        if (attributes.containsKey(POINT_SIZE)) {
            mPointSize = Integer.parseInt("" + attributes.get(POINT_SIZE));
            Log.d(POINT_SIZE, "" + mPointSize);
        }
    }

    public void setShouldFill(boolean shouldFill) {
        mShouldFill = shouldFill;
    }

    public void setFillColour(int fillColour) {
        mFillColour = fillColour;
    }

    public void setBorderColour(int borderColour) {
        mBorderColour = borderColour;
    }

    public void setBorderType(int borderType) {
        mBorderType = borderType;
    }

    public void setShouldReverse(boolean shouldReverse) {
        mShouldReverse = shouldReverse;
    }

    public void setJointType(int jointType) {
        mJointType = jointType;
    }

    public void setPointType(int pointType) {
        mPointType = pointType;
    }

    public void setPointSize(int pointSize) {
        mPointSize = pointSize;
    }

    public void setOnPointClick(GraphPointTapped graphPointTapped) {
        mGraphPointTapListener = graphPointTapped;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                computeTapPosition(Math.round(event.getX()), Math.round(event.getY()));

                break;
        }

        return true;
    }

    private void computeTapPosition(int x, int y) {
        CustomPoint point = new CustomPoint(x, y);

        ArrayList<GraphPoint> graphPoints = adjustPoints();

        int selectedPoint = graphPoints.indexOf(point);

        if (selectedPoint == (-1)) {
            return;
        }

        mGraphPointTapListener.onGraphPointTapped(mGraphPoints.get(selectedPoint));
    }

    private class CustomPoint extends GraphPoint {

        public CustomPoint(int x, int y) {
            super(x, y);
        }

        @Override
        protected void setColourRule() {

        }

        public boolean equals(Object point) {
            int x = ((GraphPoint) point).x;
            int y = ((GraphPoint) point).y;

            return compare(this.x, x) && compare(this.y, y);
        }

        private boolean compare(int a, int b) {
            int radius = mPointSize / 2;
            return ((b >= (a - radius)) && (b <= (a + radius)));
        }
    }

    private class GraphPointTapListener implements GraphPointTapped {

        @Override
        public void onGraphPointTapped(GraphPoint graphPoint) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                    R.layout.point_details_layout, null, false);

            TextView tvXCoord = ((TextView) layout.findViewById(R.id.tvXCoord));
            tvXCoord.setText("" + graphPoint.x);

            TextView tvYCoord = ((TextView) layout.findViewById(R.id.tvYCoord));
            tvYCoord.setText("" + graphPoint.y);

            MaterialDialog.Builder builder = DialogHelper.buildCustomDialog(getContext(), layout,
                    R.string.pt_dialog_title, R.string.pt_dialog_ok_btn, null);

            builder.titleColorRes(graphPoint.getColor());
            builder.positiveColorRes(graphPoint.getColor());
            builder.show();
        }
    }
}
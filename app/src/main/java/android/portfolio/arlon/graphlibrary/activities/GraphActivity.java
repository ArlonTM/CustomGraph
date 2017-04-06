package android.portfolio.arlon.graphlibrary.activities;

import android.os.Parcel;
import android.os.Parcelable;
import android.portfolio.arlon.graphlibrary.R;
import android.portfolio.arlon.graphlib.objects.GraphPoint;
import android.portfolio.arlon.graphlib.views.Graph;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class GraphActivity extends AppCompatActivity {

    Graph mCustomGraph = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        setupViews();
    }

    private void setupViews() {
        mCustomGraph = ((Graph) findViewById(R.id.graph));
        reshuffle();
        apply(null);
    }

    private ArrayList<GraphPoint> buildGraphPointArray() {
        return new ArrayList<GraphPoint>() {
            {
                int j = 0;
//                int color = -1;
//                int colors[] = {R.color.candy_pink, R.color.pastel_orange, R.color.eton_blue};

                for (int i = 0; i < 20; i++) {
                    j = new Random().nextInt(100) + ((i + 1) * (1 + new Random().nextInt(20)));
//                    color = getResources().getColor(colors[new Random().nextInt(3)]);
                    add(new XGraphPt(i, j));
                }
            }
        };
    }

    private void renderGraph() {
        mCustomGraph.renderGraph();
        ((ScrollView) findViewById(R.id.svGraph)).fullScroll(View.FOCUS_UP);
    }

    public void apply(View view) {
        Log.d("EVENTFIRED", "...");
        /*mCustomGraph.setShouldFill(true);
        mCustomGraph.setPointType(Graph.POINT_TYPE_CIRCLE);
        mCustomGraph.setBorderType(Graph.BORDER_TYPE_PLAIN);
        mCustomGraph.renderGraph();*/

        Hashtable<String, Object> attributes = new Hashtable<String, Object>() {
            {
                put(Graph.SHOULD_FILL_GRAPH, getSwitchInput(R.id.switchFillGraph));
                put(Graph.BORDER_TYPE, getChoiceInput(R.id.spinBorderType));
                put(Graph.BORDER_SIZE, getIntegerInput(R.id.edtBorderSize));
                put(Graph.GRAPH_WIDTH, getIntegerInput(R.id.edtWidth));
                put(Graph.GRAPH_HEIGHT, getIntegerInput(R.id.edtHeight));
                put(Graph.SCALE_TYPE, getChoiceInput(R.id.spinScaleType));
                put(Graph.SHOULD_REVERSE_GRAPH, getSwitchInput(R.id.switchFillGraph));
                put(Graph.JOINT_TYPE, getChoiceInput(R.id.spinJointType));
                put(Graph.POINT_TYPE, getChoiceInput(R.id.spinPointType));
                put(Graph.POINT_SIZE, getIntegerInput(R.id.edtPtSize));
            }
        };

        mCustomGraph.setAttributes(attributes);

        renderGraph();
    }

    private int getIntegerInput(int fieldId) {
        int input = 0;

        try {
            EditText editTxt = (EditText) findViewById(fieldId);
            input = Integer.parseInt(editTxt.getText().toString());
            input = View.MeasureSpec.getSize(input);
        } catch (Exception e) {

        }

        return input;
    }

    private int getChoiceInput(int fieldId) {
        Spinner spinner = (Spinner) findViewById(fieldId);
        return spinner.getSelectedItemPosition();
    }

    private boolean getSwitchInput(int fieldId) {
        return ((Switch) findViewById(fieldId)).isChecked();
    }

    private void reshuffle() {
        mCustomGraph.setItems(buildGraphPointArray());
    }

    public void reshuffle(View view) {
        reshuffle();
        renderGraph();
    }
}

class XGraphPt extends GraphPoint implements Parcelable {

    public XGraphPt(int x, int y/*, int color*/) {
        this(x, y/*, color*/, null);
    }

    public XGraphPt(int x, int y/*, int color*/, String label) {
        super(x, y, label);
    }

    public XGraphPt(Parcel in) {
        super(in.readInt(), in.readInt());
//        setColor(in.readInt());
            setLabel(in.readString());
    }

    public static final Creator<XGraphPt> CREATOR = new Creator<XGraphPt>() {

        @Override
        public XGraphPt createFromParcel(Parcel source) {
            return new XGraphPt(source);
        }

        @Override
        public XGraphPt[] newArray(int size) {
            return new XGraphPt[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(mColor);
        dest.writeString(mLabel);
    }

    protected void setColourRule() {
        Log.d("COORD", "Y: " + y);

        if (y < 75) {
            mColor = android.portfolio.arlon.graphlib.R.color.eton_blue;
        } else if ((y >= 75) && (y < 150)) {
            mColor = android.portfolio.arlon.graphlib.R.color.ocean_boat_blue;
        } else if ((y >= 150) && (y < 225)) {
            mColor = android.portfolio.arlon.graphlib.R.color.pastel_orange;
        } else if (y >= 225) {
            mColor = android.portfolio.arlon.graphlib.R.color.candy_pink;
        }
    }
}
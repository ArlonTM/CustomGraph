package android.portfolio.arlon.graphlib.helpers;

import android.content.Context;
import android.portfolio.arlon.graphlib.R;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Arlon Mukeba on 3/31/2017.
 */

public class DialogHelper {

    private static String getString(Context context, int resId) {
        return context.getString(resId);
    }

    private static MaterialDialog.Builder getBase(Context context) {
        return new MaterialDialog.Builder(context).cancelable(false);
    }

    public static MaterialDialog.Builder buildCustomDialog(Context context,
                                                           ViewGroup layout, int titleResId,
                                                           int yesBtnResId,
                                                           MaterialDialog.SingleButtonCallback callback) {

        return getBase(context).title(titleResId)
                .titleColorRes(R.color.ocean_boat_blue).positiveText(yesBtnResId)
                .positiveColorRes(R.color.ocean_boat_blue).customView(layout, false)
                .onAny(callback);
    }
}
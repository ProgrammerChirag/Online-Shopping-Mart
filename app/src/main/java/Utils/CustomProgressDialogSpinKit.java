package Utils;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.style.FadingCircle;
import com.selflearn.rpsstationary.R;


public class CustomProgressDialogSpinKit {

    Activity activity;
    ProgressBar progressBar;

    public CustomProgressDialogSpinKit(Activity activity)
    {
        this.activity = activity;
        progressBar = (ProgressBar) activity.findViewById(R.id.spin_kit);
    }

    public void startLoadingDialog() {

        FadingCircle doubleBounce = new FadingCircle();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

    }

    public void dismissDialog(){
        progressBar.setVisibility(View.INVISIBLE);
    }


}

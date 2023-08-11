package mobi.omegacentauri.whitescreen;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class Main extends Activity {

    private View.OnTouchListener gestureListener;
    private SharedPreferences options;
    private CheckBox keepScreenOnCB;
    private CheckBox maxBrightnessCB;
    Boolean keepScreenOn = null;
    Boolean maxBrightness = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        options = PreferenceManager.getDefaultSharedPreferences(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window w = getWindow();
        WindowManager.LayoutParams attrs = w.getAttributes();

        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        attrs.flags |= WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;

        getWindow().setAttributes(attrs);

        View dv = w.getDecorView();

        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            dv.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            int flags = dv.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            dv.setSystemUiVisibility(flags);
        }

        setContentView(R.layout.main);
        keepScreenOnCB = findViewById(R.id.keepScreenOn);
        maxBrightnessCB = findViewById(R.id.maxBrightness);

        updateOptions(true);

        keepScreenOnCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateOptions(false);
            }
        });
        maxBrightnessCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateOptions(false);
            }
        });
    }

    private void updateOptions(boolean fromSaved) {
        Boolean newKeepScreenOn;
        Boolean newMaxBrightness;
        if (fromSaved) {
            newKeepScreenOn = options.getBoolean("keepScreenOn", false);
            newMaxBrightness = options.getBoolean("maxBrightness", false);
            if (newKeepScreenOn == keepScreenOn && newMaxBrightness == maxBrightness)
                return;
            keepScreenOn = newKeepScreenOn;
            maxBrightness = newMaxBrightness;
            keepScreenOnCB.setChecked(keepScreenOn);
            maxBrightnessCB.setChecked(maxBrightness);
        }
        else {
            newKeepScreenOn = keepScreenOnCB.isChecked();
            newMaxBrightness = maxBrightnessCB.isChecked();
            if (newKeepScreenOn == keepScreenOn && newMaxBrightness == maxBrightness)
                return;
            keepScreenOn = newKeepScreenOn;
            maxBrightness = newMaxBrightness;
            SharedPreferences.Editor ed = options.edit();
            ed.putBoolean("keepScreenOn", keepScreenOn);
            ed.putBoolean("maxBrightness", maxBrightness);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                ed.apply();
            }
            else {
                ed.commit();
            }
        }
        Window w = getWindow();
        if (keepScreenOn)
            w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        else
            w.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        WindowManager.LayoutParams attrs = w.getAttributes();
        if (maxBrightness)
            attrs.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        else
            attrs.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        w.setAttributes(attrs);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View options = findViewById(R.id.options);

            boolean visible = options.getVisibility()==View.VISIBLE;
            options.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
            return true;

        }
        return super.onTouchEvent(event);
    }
}

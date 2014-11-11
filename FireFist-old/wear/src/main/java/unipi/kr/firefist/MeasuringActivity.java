package unipi.kr.firefist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;
import static java.lang.Math.*;

public class MeasuringActivity extends Activity implements SensorEventListener, View.OnClickListener{

    private TextView mTextScore;
    SensorManager mSensorManager;
    Sensor mSensorLinear;
    Vibrator mVibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measuring);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextScore = (TextView) findViewById(R.id.measuring_score);
                findViewById(R.id.measuring_exit).setOnClickListener(MeasuringActivity.this);
            }
        });

        try {
            mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mSensorLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            if (!mSensorManager.registerListener(this, mSensorLinear, SensorManager.SENSOR_DELAY_GAME))
                throw new Exception("센서를 사용할 수 없습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.alert(this, "센서를 사용할 수 없습니다.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSensorManager != null)
            mSensorManager.unregisterListener(this, mSensorLinear);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.measuring_exit:
                finish();
                break;
        }
    }

    Vector3 acc = new Vector3();
    Vector3 max = new Vector3();


    @Override
    public void onSensorChanged(SensorEvent e) {
        acc.set(e.values[0], e.values[1], e.values[2]);

        float accLen = acc.length();
        float maxLen = max.length();

        if((accLen - 20) > maxLen)
        {
            max.set(acc);

            mTextScore.setText((int)accLen + "점");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}

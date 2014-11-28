package unipi.kr.firefist;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import unipi.kr.firefist.gaming.NotificationClearTask;
import unipi.kr.firefist.gaming.ScoreAgent;

public class GameActivity extends Activity
        implements SensorEventListener, View.OnClickListener
        , GoogleApiClient.OnConnectionFailedListener
        , GoogleApiClient.ConnectionCallbacks
{

	public static final String
			PREFERENCE_NAME = "LocalScoreBoard",
			KEY_BEST_SCORE = "best-score",
			KEY_CAMULATED_SCORE = "camulated-score",
			KEY_EXPERIENCE = "experience",
			KEY_COINS = "coins"
					;

	public static final String PATH_WATCH_METER = "watch-meter";





    private TextView textScore, textNotification;
    private Button btnReset;

    GoogleApiClient client;
    DataMapItem playerDataMap;
    boolean connected = false;

    SensorManager mSensorManager;
    Sensor mSensorLinear;
    Vibrator mVibrator;

    Handler handler;
    ScoreAgent score;
    NotificationClearTask lastTask;


    float maxScore = 0,
            totalScore = 0,
            exp = 0;
    int level = 1, coin = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // 처음에 게임 액티비티를 나타내고
        // 이후 스플래시 액티비티를 위에 올린다.
        // 스플래시 액티비티는 2초뒤에 자동으로 종료된다.
        // 그러면 게임 액티비티가 나타난다.
        Intent it = new Intent(this, SplashActivity.class);
        startActivity(it);

        // 객체 생성
        client = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Wearable.API)
                .build();
        handler = new Handler();
        score = new ScoreAgent(0);
        maxScore = 0;

        // UI 초기화
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                initUI(stub);
            }
        });
    }

    // UI 초기화를 하는 메서드
    private void initUI(WatchViewStub stub)
    {
        textScore = (TextView) stub.findViewById(R.id.game_text_score);
        textNotification = (TextView) stub.findViewById(R.id.game_text_notification);
        btnReset = (Button) stub.findViewById(R.id.game_button_clear);
        btnReset.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();

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
    protected void onStop() {
        super.onStop();
        if(mVibrator != null)
        {
            mSensorManager.unregisterListener(this);
        }
        if(client != null && client.isConnected())
        {
            client.disconnect();
        }
    }

    // 밀리초만큼 진동함
    private void vibrate(long milli)
    {
        if(mVibrator != null)
        {
            mVibrator.vibrate(milli);
        }
    }

    // 현재 점수를 변경함
    private void setScoreText(float score)
    {
        String text = String.format("%.1f점", score);
        if(textScore != null)
	        textScore.setText(text);
    }

    // 최고 점수를 변경하는 메서드
    private void setMaxScore(float maxScore)
    {
        this.maxScore = maxScore;

        if(textNotification == null || maxScore < 25)
	        return;

	    textNotification.setText("최고점수 갱신!");

        if(lastTask != null && !lastTask.isFinished())
        {
            lastTask.SetActivation(false);
        }

        lastTask = new NotificationClearTask(textNotification);
        handler.postDelayed(
                lastTask,
                3000
        );
        vibrate(300);
    }

    private void syncData()
    {
        PendingResult<DataItemBuffer> buffer = Wearable.DataApi.getDataItems(client);
        buffer.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                int count = dataItems.getCount();
                boolean put = false;

                for(int i = 0; i < count; ++i)
                {
                    DataItem item = dataItems.get(i);
                    if(isPlayerUri(item.getUri()))
                    {
                        Log.d("게임", "기존의 데이터 맵 요청에 추가됨. " + item.getUri());
                        PutDataMapRequest request = PutDataMapRequest.createFromDataMapItem(
                                DataMapItem.fromDataItem(item)
                        );
                        putData(request.getDataMap());
                        Wearable.DataApi.putDataItem(client, request.asPutDataRequest());
                        put = true;
                    }
                }
                if(!put)
                {
                    Log.d("게임", "새로운 데이터 맵 요청이 추가됨.");
                    PutDataMapRequest request = PutDataMapRequest.create("/" + PATH_WATCH_METER);
                    putData(request.getDataMap());
                    Wearable.DataApi.putDataItem(client, request.asPutDataRequest());
                }
            }
        });
    }

    private void putData(DataMap map)
    {
        float syncBest = map.getFloat(KEY_BEST_SCORE, 0);
        if(syncBest < maxScore)
            map.putFloat(KEY_BEST_SCORE, maxScore);

        float syncTotal = map.getFloat(KEY_CAMULATED_SCORE, 0);
        syncTotal += score.getScore();
        map.putFloat(KEY_CAMULATED_SCORE, syncTotal);

	    if(score.getScore() > 25) {
		    float exp = map.getFloat(KEY_EXPERIENCE, 0);
		    exp += 1;
		    map.putFloat(KEY_EXPERIENCE, exp);
	    }
    }

    private boolean isPlayerUri(Uri uri)
    {
        return uri.getPath().equals(PATH_WATCH_METER);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.game_button_clear:
                setScoreText(0);
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float []values = sensorEvent.values;

        // 표본 추가
        score.addSample(new Vector3(values[0], values[1], values[2]));

        // 점수가 바뀌었다면?
        if(score.isScoreChanged())
        {
            float currScore = score.getScore();

            // 최고점수 갱신
            if(currScore > maxScore)
            {
                setMaxScore(currScore);
            }

	        if(currScore > 10) {
		        //
		        setScoreText(currScore);
		        score.notifyChanging();

		        exp += currScore / 10.0f;
		        totalScore += currScore;

		        if (connected) {
			        syncData();
		        }
	        }
        }
    }


    // Unused
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}


    @Override
    public void onConnected(Bundle bundle) {
        connected = true;

    }

    @Override
    public void onConnectionSuspended(int i) {
        connected = false;
        Log.d("FIREFIST", "Connection has been Suspended");
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        connected = false;
        Log.d("FIREFIST", "Connection has been failed");
    }
}

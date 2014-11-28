package unipi.kr.firefist;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import unipi.kr.firefist.gaming.IScoreBoard;
import unipi.kr.firefist.gaming.LocalScoreBoard;
import unipi.kr.firefist.gaming.NotificationClearTask;
import unipi.kr.firefist.gaming.ScoreMeter;
import unipi.kr.firefist.utils.ActivityPlus;
import unipi.kr.firefist.utils.Vector3;


public class MainActivity
        extends ActivityPlus
    implements SensorEventListener,
		ScoreMeter.Handler,
		View.OnClickListener
{

	public static final int NOTIFICATION_DURATION = 2000,
							VIBRATING_DURATION = 300;

	SensorManager mSensorManager;
	Sensor mSensorLinear;
	Vibrator mVibrator;

	Handler handler;
	ScoreMeter meter;
	IScoreBoard board;
	NotificationClearTask lastTask;



	//
	// UI 초기화 루틴
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new Handler();
		meter = new ScoreMeter();
		meter.setHandler(this);
		board = new LocalScoreBoard(this);

		onClick(R.id.button_cracking_building, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent it = new Intent(this, ProfileActivity.class);
			startActivity(it);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view)
	{
		switch(view.getId())
		{
			case R.id.button_cracking_building:
			{
				Intent it = new Intent(this, CrackingActivity.class);
				startActivity(it);
				break;
			}
		}
	}



	// START / STOP 에서는 센서 등을 얻고 파기함.
	@Override
	protected void onStart() {
		super.onStart();

		try {
			mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
			mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
			mSensorLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			if (!mSensorManager.registerListener(this, mSensorLinear, SensorManager.SENSOR_DELAY_GAME))
				throw new Exception("센서를 사용할 수 없습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			alert("센서를 사용할 수 없습니다.", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mSensorLinear != null)
		{
			mSensorManager.unregisterListener(this);
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







	// 점수가 측정됨!
	@Override
	public void onScoreMeasured(float score, ScoreMeter meter)
	{
		text_f(R.id.text_firefist_power,
				"%.1f %s",
				score,
				string(R.string.score_unit)
				);
		board.addScore(score);
		log_d("측정점수", score + "");
	}

	@Override
	public void onBestScoreChanged(float bestScore, ScoreMeter meter)
	{
		log_d("새로운 최고 점수", bestScore + "");
		if(lastTask != null)
		{
			lastTask.setActivation(false);
		}

		TextView viewNotify = textView_(R.id.text_notification);
		lastTask = new NotificationClearTask(viewNotify);


		board.setBestScore(bestScore);
		viewNotify.setText(getString(R.string.new_best_score));


		// 알림 텍스트는 NOTIFICATION_DURATION ms 뒤에 사라짐.
		handler.postDelayed(lastTask, NOTIFICATION_DURATION);
		vibrate(VIBRATING_DURATION);
	}






	// 센서 이벤트 수신

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		float []values = sensorEvent.values;

		// 표본 추가
		meter.addSample(new Vector3(values[0], values[1], values[2]));
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
		// 안써요
	}
}

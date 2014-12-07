package unipi.kr.firefist.gaming;

import unipi.kr.firefist.api.IFeature;
import unipi.kr.firefist.utils.Vector3;

/**
 * Created by KimHeeKue on 2014-11-26.
 */
public class ScoreMeter {


	public static interface Handler
	{

		public void onScoreMeasured(float score, ScoreMeter meter);


		public void onBestScoreChanged(float bestScore, ScoreMeter meter);

	}




	public static final long INTERVAL = 1000;

	Handler handler;
	PlayerAttributes attributes;
	float deviceAdjustment = 1.0f;
	long lastChangedTime = 0, interval = INTERVAL;


	public ScoreMeter()
	{
		attributes = new PlayerAttributes();

	}




	/*
		샘플을 추가하여 계산을 진행함.
	*/
	public void addSample(Vector3 acc)
	{
		float length = acc.length() * deviceAdjustment;
		if(length < 20)
			return;


		if( !isTimeIntervalExceeded() )
			return;


		float score = length;
		attributes.exp += 0.1;

		if(handler != null)
			handler.onScoreMeasured(score, this);


		// 최고 점수 기록이 바뀌었다!
		if(attributes.bestScore < score)
		{
			attributes.bestScore = score;
			attributes.exp += 0.5;
			attributes.notifyChanged();

			if(handler != null)
				handler.onBestScoreChanged(score, this);
		}
	}

	// 측정 간격 시간이 지났는가?
	private boolean isTimeIntervalExceeded()
	{
		long currentTime = System.currentTimeMillis();
		long delta = currentTime - lastChangedTime;

		if(delta > interval)
		{
			lastChangedTime = currentTime;
			return true;
		}
		else
			return false;
	}









	//
	//
	//
	//
	//
	// 게터랑 세터들
	//
	///
	////
	///
	/////



	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public PlayerAttributes getAttributes() {
		return attributes;
	}

	public void setAttributes(PlayerAttributes attributes) {
		this.attributes = attributes;
	}

	public float getDeviceAdjustment() {
		return deviceAdjustment;
	}

	public void setDeviceAdjustment(float deviceAdjustment) {
		this.deviceAdjustment = deviceAdjustment;
	}

	public long getLastChangedTime() {
		return lastChangedTime;
	}

	public void setLastChangedTime(long lastChangedTime) {
		this.lastChangedTime = lastChangedTime;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}




}

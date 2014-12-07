package unipi.kr.firefist.api;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import unipi.kr.firefist.gaming.ScoreMeter;
import unipi.kr.firefist.utils.Vector3;

/**
 * Created by KimHeeKue on 2014-12-01.
 */
public class ScoreSensor
	implements IFeature,
		SensorEventListener
{
	Vector3 acc;
	ScoreMeter meter;
	SensorManager manager;
	Sensor linearSensor;


	public ScoreSensor(Context context, Container container)
			throws Exception
	{
		container.featureList.add(this);


		acc = new Vector3();
		manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		linearSensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		meter = new ScoreMeter();

		if(manager == null)
			throw new Exception("Could not get sensor manager.");
		if(linearSensor == null)
			throw new Exception("Could not get Linear Acceleration sensor.");
	}


	@Override
	public void onSensorChanged(SensorEvent sensorEvent)
	{
		float[] values = sensorEvent.values;
		acc.set(values[0], values[1], values[2]);
		meter.addSample(acc);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {

	}



	private void register()
	{
		manager.registerListener(this, linearSensor, SensorManager.SENSOR_DELAY_GAME);
	}

	private void unregister()
	{
		manager.unregisterListener(this, linearSensor);
	}



	public ScoreMeter getMeter() {
		return meter;
	}

	public SensorManager getManager() {
		return manager;
	}

	public Sensor getLinearSensor() {
		return linearSensor;
	}



	boolean enabled = false;

	@Override
	public void setEnable(boolean enabled)
	{
		if(this.enabled == enabled) return;

		this.enabled = enabled;
		if(enabled)
			register();
		else
			unregister();
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}

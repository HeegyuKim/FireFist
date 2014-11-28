package unipi.kr.firefist.gui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by KimHeeKue on 2014-11-28.
 */
public class DamageRatePainter implements SurfaceHolder.Callback
{
	SurfaceView surfaceView;
	SurfaceHolder holder;
	Paint paint;
	int width = 0, height = 0;
	float lastRate = 0;

	public DamageRatePainter(SurfaceView surfaceView)
	{
		surfaceView.getHolder().addCallback(this);
		paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
	}

	@Override
	public void surfaceChanged(
			SurfaceHolder surfaceHolder,
			int format,
			int width,
			int height
	)
	{
		this.holder = surfaceHolder;
		this.width = width;
		this.height = height;

		Log.d("SurfaceView WIDTH", width + "");
		Log.d("SurfaceView HEIGHT", height + "");

		paint(lastRate);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		this.holder = surfaceHolder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

	}


	public void paint(float rate)
	{
		lastRate = rate;
		Canvas canvas = holder.lockCanvas();
		if(canvas == null)
		{
			Log.d("파괴율 그리기", "뷁");
			return;
		}
		Log.d("파괴율 그리기", "" + rate);

		paint.setARGB(255, 215, 215, 215);
		canvas.drawRect(0, 0, width, height, paint);

		//#E56717, 파파야 오렌지 색
		paint.setARGB(255, 0xE5, 0x67, 0x17);
		canvas.drawRect(0, 0, width * rate, height, paint);

		holder.unlockCanvasAndPost(canvas);
	}
}

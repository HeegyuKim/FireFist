package unipi.kr.firefist;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceView;

import com.android.volley.toolbox.NetworkImageView;

import unipi.kr.firefist.api.AccessibleBuildingHandler;
import unipi.kr.firefist.api.Building;
import unipi.kr.firefist.api.Cracker;
import unipi.kr.firefist.gui.DamageRatePainter;
import unipi.kr.firefist.utils.ActivityPlus;
import unipi.kr.firefist.utils.VolleySingleton;

public class CrackingActivity extends ActivityPlus {

	Cracker cracker;
	DamageRatePainter painter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cracking);

	    cracker = new Cracker();

	    SurfaceView surfaceView = (SurfaceView)view_(R.id.surface_damage_rate);
	    painter = new DamageRatePainter(surfaceView);

	    findBuilding();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cracking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	private void findBuilding()
	{
		cracker.getAccessibleBuilding(37.5172, 127.047).asyncPost(new AccessibleBuildingHandler() {
			@Override
			public void onSucceeded(Building building) {
				onFindBuilding(building);
			}

			@Override
			public void onFailed(String message) {
				Log.d("에러", message);
				text_(R.id.text_building, string(R.string.none));
			}
		});
	}


	private void onFindBuilding(Building building)
	{
		text_(R.id.text_building, building.name);
		text_f(R.id.text_damage_rate, "%.2f %%", building.damageRate * 100);
		painter.paint((float)building.damageRate);
		NetworkImageView imageView = (NetworkImageView)view_(R.id.image_building);
		imageView.setImageUrl(
				cracker.getBuildingImageUrl(building.id, false),
				VolleySingleton.getInstance(this).getImageLoader()
		);
	}
}

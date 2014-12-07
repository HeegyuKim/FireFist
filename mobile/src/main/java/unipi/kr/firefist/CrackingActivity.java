package unipi.kr.firefist;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONObject;

import unipi.kr.firefist.api.Container;
import unipi.kr.firefist.api.GPSLocation;
import unipi.kr.firefist.api.GooglePlus;
import unipi.kr.firefist.api.ScoreSensor;
import unipi.kr.firefist.api.Watch;
import unipi.kr.firefist.api.rest.AccessibleBuildingHandler;
import unipi.kr.firefist.api.Building;
import unipi.kr.firefist.api.rest.Cracker;
import unipi.kr.firefist.api.rest.JSONHandler;
import unipi.kr.firefist.gaming.PlayerAttributes;
import unipi.kr.firefist.gaming.ScoreMeter;
import unipi.kr.firefist.gui.DamageRatePainter;
import unipi.kr.firefist.utils.ActivityPlus;
import unipi.kr.firefist.utils.VolleySingleton;

public class CrackingActivity
	extends ActivityPlus
	implements Watch.Handler,
		ScoreMeter.Handler,
		com.google.android.gms.location.LocationListener
{
	Cracker cracker;
	Building building;
	DamageRatePainter painter;


	Container container;
	Watch watch;
	ScoreSensor scoreSensor;
	GooglePlus plus;
	GPSLocation gps;
	Vibrator vibrator;

	double myDamage = 0;
	int myRank = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cracking);

	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    initFeatures();
	    // findBuilding(37.5172, 127.047);
    }

	private void initFeatures()
	{
		cracker = new Cracker();
		SurfaceView surfaceView = (SurfaceView)view_(R.id.surface_damage_rate);
		painter = new DamageRatePainter(surfaceView);


		container = new Container();
		plus = new GooglePlus(this, container);
		plus.client.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult connectionResult) {
				toast(string(R.string.not_authorized_so_you_are_anonymous), Toast.LENGTH_LONG);
			}
		});

		try
		{
			watch = new Watch(this, container);
			watch.setHandler(this);
			scoreSensor = new ScoreSensor(this, container);
			scoreSensor.getMeter().setHandler(this);

			gps = new GPSLocation(this, container);
			gps.client.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
				@Override
				public void onConnected(Bundle bundle) {
					Log.d("GPS", "Connected, requesting location...");
					LocationRequest request = LocationRequest.create();
					request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
					request.setInterval(30000);
					LocationServices.FusedLocationApi.requestLocationUpdates(
							gps.client,
							request,
							CrackingActivity.this
					);
				}

				@Override
				public void onConnectionSuspended(int i) {
					Log.d("GPS", "Connected, suspended");
					LocationServices.FusedLocationApi.removeLocationUpdates(
							gps.client,
							CrackingActivity.this
					);
				}
			});
			gps.client.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
				@Override
				public void onConnectionFailed(ConnectionResult connectionResult) {
					Log.d("GPS", "Connection is failed");
					text_(R.id.text_building, string(R.string.gps_is_disabled));
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			alert(string(R.string.not_found_sensors), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					dialogInterface.dismiss();
				}
			});
		}
	}


	@Override
	protected void onStart() {
		super.onStart();

		container.setEnable(true);
	}

	@Override
	protected void onStop() {
		super.onStop();

		container.setEnable(false);
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

	    if(id == android.R.id.home)
        {
	        onBackPressed();
	        return true;
        }
        return super.onOptionsItemSelected(item);
    }




	@Override
	public void onLocationChanged(Location location)
	{
		double lat = location.getLatitude(),
				lng = location.getLongitude();

		if(building != null)
		{
			Location target = new Location("Target Location");
			target.setLatitude(building.latitude);
			target.setLongitude(building.longitude);

			float distance = location.distanceTo(
					target
			);
			if(distance <= building.radius)
				return;
		}

		log_df("Location Changed", "%.5f, %.5f", lat, lng);
		findBuilding(lat, lng);
	}

	private void findBuilding(double lat, double lng)
	{
		text_id(R.id.text_building, R.string.finding);

		Person person = Plus.PeopleApi.getCurrentPerson(plus.client);
		String playerId = "anonymous";
		if(person != null)
			playerId = person.getId();


		cracker.getAccessibleBuilding(playerId, lat, lng)
		.asyncPost(new AccessibleBuildingHandler() {
			@Override
			public void onSucceeded(Building building) {
				onFindBuilding(building);
			}

			@Override
			public void onFailed(String message) {
				building = null;
				Log.d("GetAccessibleBuilding", message);
				//text_(R.id.text_building, string(R.string.none));
			}
		});
	}


	private void onFindBuilding(Building building)
	{
		this.building = building;

		text_(R.id.text_building, building.name);
		text_f(R.id.text_damage_rate, "%.2f %%", building.damageRate * 100);

		text_idf(R.id.text_my_damage_number, R.string.format_score, building.myDamage);
		text_idf(R.id.text_my_ranking, R.string.format_rank, building.myRank);

		painter.paint((float)building.damageRate);


		NetworkImageView imageView = (NetworkImageView)view_(R.id.image_building);
		imageView.setImageUrl(
				building.getBefittingUrl(),
				VolleySingleton.getInstance(this).getImageLoader()
		);
	}






	public void takeDamage(double score)
	{
		if(building == null)
		{
			log_d("TakeDamage", "건물 없음");
			return;
		}
		if(!plus.isConnected())
		{
			toast(string(R.string.not_authorized), Toast.LENGTH_LONG);
			return;
		}
		if(building.destroyed) return;

		Person person = Plus.PeopleApi.getCurrentPerson(plus.client);
		if(person == null)
		{
			toast(string(R.string.could_not_get_id_for_cracking), Toast.LENGTH_LONG);
			return;
		}

		log_d("TakeDamage", "데미지 " + score);

		plus.leaderBoard.addScore(score);
		plus.leaderBoard.addExp(0.5f);

		cracker.damage(person.getId(), building.id, score).asyncPost(new JSONHandler() {
		@Override
		public void onSucceeded(JSONObject data) {
			try
			{
				boolean destroy = data.getBoolean("destroy");
				double damageRate = data.getDouble("damage_rate");
				double myDamage = data.getDouble("my_damage");
				int myRank = data.getInt("my_rank");

				text_(R.id.text_damage_rate, String.format("%.2f", damageRate * 100));
				text_idf(R.id.text_my_damage_number, R.string.format_score, myDamage);
				text_idf(R.id.text_my_ranking, R.string.format_rank, myRank);

				painter.paint((float)damageRate);

				if(destroy)
				{
					NetworkImageView imageView = (NetworkImageView)view_(R.id.image_building);
					imageView.setImageUrl(
							building.destroyedPhotoUrl,
							VolleySingleton.getInstance(CrackingActivity.this).getImageLoader()
					);

					alert(string(R.string.destroy_building), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					});

					Vibrator v = (Vibrator)getSystemService(VIBRATOR_SERVICE);
					if(v != null)
						v.vibrate(1000);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void onFailed(String message) {
			super.onFailed(message);
			log_d("데미지 입히기 실패", message);
			toast(string(R.string.could_not_send_data_to_server), Toast.LENGTH_LONG);
		}
		});
	}

	@Override
	public void onScoreMeasured(float score, ScoreMeter meter) {
		Log.d("Phone Score", score + "");
		takeDamage(score);
	}

	@Override
	public void onBestScoreChanged(float bestScore, ScoreMeter meter) {

	}

	@Override
	public void onWatchMeasured(PlayerAttributes attr, Watch watch) {
		double score = attr.camulScore;
		takeDamage(score);
		Log.d("Watch Score", score + "");
		watch.clearData();
	}

}

package unipi.kr.firefist.api;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by KimHeeKue on 2014-12-06.
 */
public class GPSLocation implements IFeature, GoogleApiClient.ConnectionCallbacks
{
	public static final int REQUEST_INTERVAL = 10000;


	public final GoogleApiClient client;

	boolean enabled = false;


	public GPSLocation(Context context, Container container)
	{
		if(container != null)
			container.featureList.add(this);
		client = new GoogleApiClient.Builder(context)
				.addApi(LocationServices.API)
				.addConnectionCallbacks(this)
				.build();
	}


	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}


	public boolean request(LocationListener listener)
	{
		if(!client.isConnected()) return false;

		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		request.setInterval(REQUEST_INTERVAL);

		LocationServices.FusedLocationApi.requestLocationUpdates(client, request, listener);

		return true;
	}

	public void remove(LocationListener listener)
	{
		LocationServices.FusedLocationApi.removeLocationUpdates(client, listener);
	}





	@Override
	public void setEnable(boolean enabled) {
		if(this.enabled == enabled)
			return;

		this.enabled = enabled;
		if(enabled)
		{
			if(!client.isConnected() && !client.isConnecting())
				client.connect();
		}
		else
			client.disconnect();
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}

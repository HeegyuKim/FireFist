package unipi.kr.firefist.game;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;

/**
 * Created by KimHeeKue on 2014-11-23.
 */
public class GooglePlusPlayer
implements GooglePlayServicesClient.ConnectionCallbacks
	, GooglePlayServicesClient.OnConnectionFailedListener

{
	PlusClient plusClient;
	ConnectionResult result;


	public GooglePlusPlayer(Context context)
	{
		plusClient = new PlusClient.Builder(context, this, this)
				.build();

	}


	public PlusClient getPlusClient()
	{
		return plusClient;
	}



	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onDisconnected() {

	}
}

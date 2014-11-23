package unipi.kr.firefist.game;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.plus.PlusClient;

/**
 * Created by KimHeeKue on 2014-11-23.
 */
public class PlusAdapter
		implements GooglePlayServicesClient.ConnectionCallbacks
		, GooglePlayServicesClient.OnConnectionFailedListener {


	FireFist firefist;

	public PlusAdapter(FireFist firefist)
	{
		this.firefist = firefist;
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

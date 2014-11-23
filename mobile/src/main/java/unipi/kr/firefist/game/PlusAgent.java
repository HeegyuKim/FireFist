package unipi.kr.firefist.game;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * Created by KimHeeKue on 2014-11-23.
 */
public class PlusAgent
	implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener
{
	GoogleApiClient client;

	public PlusAgent(Context context)
	{
		client = new GoogleApiClient.Builder(context)
				.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN).addScope(Plus.SCOPE_PLUS_PROFILE)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.build();
	}


	public GoogleApiClient getClient()
	{
		return client;
	}


	@Override
	public void onConnectionSuspended(int i) {
		Log.d("PlusAgent", "연결 중단됨...");
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("PlusAgent", "연결 성공!");

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("PlusAgent", "연결 실패,,,");

	}
}

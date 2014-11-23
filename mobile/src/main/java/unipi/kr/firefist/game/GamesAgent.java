package unipi.kr.firefist.game;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

/**
 * Created by KimHeeKue on 2014-11-23.
 */
public class GamesAgent
		implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener
{
	GoogleApiClient client;

	public GamesAgent(Context context)
	{
		client = new GoogleApiClient.Builder(context)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)
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
		Log.d("GamesAgent", "연결 중단됨...");
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("GamesAgent", "연결 성공!");

	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("GamesAgent", "연결 실패,,,");

	}
}

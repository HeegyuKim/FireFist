package unipi.kr.firefist.api;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.wearable.Wearable;
import com.google.example.games.basegameutils.BaseGameUtils;

import unipi.kr.firefist.gaming.PlayerAttributes;
import unipi.kr.firefist.gaming.PlayerHandler;

/**
 * Created by KimHeeKue on 2014-12-06.
 */
public class GooglePlus
	implements IFeature,
		GoogleApiClient.OnConnectionFailedListener,
		GoogleApiClient.ConnectionCallbacks
{
	public static final int REQUEST_RESOLVE_FAILED = 1230330;
	public final GoogleApiClient client;
	public final LeaderBoard leaderBoard;
	public final PlayerAttributes attributes = new PlayerAttributes();


	public GooglePlus(Context context, Container container)
	{
		if(container != null)
		{
			container.featureList.add(this);
		}

		client = new GoogleApiClient.Builder(context)
			.addApi(Plus.API)
			.addScope(Plus.SCOPE_PLUS_LOGIN)
			.addScope(Plus.SCOPE_PLUS_PROFILE)
			.addApi(Games.API)
			.addScope(Games.SCOPE_GAMES)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.build();

		leaderBoard = new LeaderBoard(context, client);
	}


	public void connect()
	{
		if(!client.isConnected() && !client.isConnecting())
		{
			Log.d("GooglePlus", "let's connect");
			client.connect();
		}
		else
			Log.d("GooglePlus", "still connecting or connected");
	}


	public void disconnect()
	{
		if(client.isConnected())
			client.disconnect();
	}

	public boolean isConnected()
	{
		return client.isConnected();
	}



	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("GooglePlus", "onConnectionFailed");
		if(resolveFailed && connectionResult.hasResolution())
		{
			try {
				Log.d("GooglePlus", "trying resolution for result.");
				connectionResult.startResolutionForResult(
						resolveActivity,
						REQUEST_RESOLVE_FAILED
				);
				/*
				BaseGameUtils.resolveConnectionFailure(
						resolveActivity,
						client,
						connectionResult,
						REQUEST_RESOLVE_FAILED,
						""
				);
				*/
			}
			catch(IntentSender.SendIntentException e)
			{
				Log.d("GooglePlus", "SendIntentException occurred.");
				connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d("GooglePlus", "Connection succeeded");
		leaderBoard.loadAttributesTo(new PlayerHandler() {
			@Override
			public void onPlayerChanged(PlayerAttributes attr) {
				attributes.set(attr);
			}
		});
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("GooglePlus", "Connection suspended");
	}



	Activity resolveActivity;
	boolean resolveFailed = false;

	public boolean isResolveFailed() {
		return resolveFailed;
	}

	public void setResolveFailed(Activity activity, boolean resolveFailed) {
		this.resolveActivity = activity;
		this.resolveFailed = resolveFailed;
	}




	boolean enabled = false;

	@Override
	public void setEnable(boolean enabled)
	{
		if(this.enabled == enabled) return;

		this.enabled = enabled;
		if(enabled)
			connect();
		else
			disconnect();
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}



}

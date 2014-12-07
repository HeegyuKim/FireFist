package unipi.kr.firefist.api;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import unipi.kr.firefist.gaming.PlayerAttributes;

import static unipi.kr.firefist.api.IScoreBoard.*;

/**
 * Created by KimHeeKue on 2014-11-26.
 */
public class Watch
implements GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		DataApi.DataListener,
		IFeature
{

	public static interface Handler
	{
		public void onWatchMeasured(PlayerAttributes attributes, Watch watch);
	}


	public static final String PATH_WATCH_METER = "/watch-meter";

	Context context;
	GoogleApiClient client;
	Handler handler;
	PlayerAttributes attr;
	Uri uri;


	public Watch(Context context, Container container)
	{
		this.context = context;
		if(container != null)
			container.featureList.add(this);

		client = new GoogleApiClient.Builder(context)
				.addApi(Wearable.API)
				.addOnConnectionFailedListener(this)
				.addConnectionCallbacks(this)
				.build();

		attr = new PlayerAttributes();
	}


	public void connect()
	{
		if(!client.isConnected() && !client.isConnecting())
			client.connect();

		Wearable.DataApi.addListener(client, this);
	}


	public void disconnect()
	{
		if(client.isConnected())
			client.disconnect();

		Wearable.DataApi.removeListener(client, this);
	}




	public void clearData()
	{
		if(uri != null && client.isConnected())
			Wearable.DataApi.deleteDataItems(client, uri);
	}



	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

	}

	@Override
	public void onConnected(Bundle bundle) {

	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {

		if(!dataEvents.getStatus().isSuccess())
			return;

		int count = dataEvents.getCount();
		for(int i = 0; i < count; ++i)
		{
			DataEvent event = dataEvents.get(i);
			DataMapItem mapItem = DataMapItem.fromDataItem(event.getDataItem());

			Uri uri = mapItem.getUri();
			if(isWatchMeterUri(uri))
			{
				this.uri = uri;
				handleDataMap(mapItem.getDataMap());
				return;
			}
		}
	}


	private boolean isWatchMeterUri(Uri uri)
	{
		String path = uri.getPath();
		return path.equals(PATH_WATCH_METER);
	}



	private void handleDataMap(DataMap map)
	{
		if(handler != null)
		{
			attr.bestScore = map.getFloat(KEY_BEST_SCORE, 0);
			attr.camulScore = map.getFloat(KEY_CAMULATED_SCORE, 0);
			attr.exp = map.getFloat(KEY_EXPERIENCE, 0);
			attr.coins = map.getInt(KEY_COINS, 0);

			AsyncTask<Void,Void,Void> task = new AsyncTask<Void, Void, Void>(){
				@Override
				protected Void doInBackground(Void... voids) {return null;}

				@Override
				protected void onPostExecute(Void aVoid) {
					handler.onWatchMeasured(attr, Watch.this);
				}
			};
			task.execute();

		}
	}




	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
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

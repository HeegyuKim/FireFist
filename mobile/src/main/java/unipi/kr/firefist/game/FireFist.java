package unipi.kr.firefist.game;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.TreeMap;

import unipi.kr.firefist.R;

/**
 * Created by KimHeekue on 2014-11-12.
 */
public class FireFist
implements GoogleApiClient.ConnectionCallbacks
    , GoogleApiClient.OnConnectionFailedListener
    , DataApi.DataListener
{
    Context context;
	GoogleApiClient client;
	PlayerData data;
    PlayerListener listener;


    public FireFist(Context context, PlayerListener listener)
    {
        this.context = context;

	    //
	    // API 객체들 생성

	    client = new GoogleApiClient.Builder(context)
			    .addApi(Wearable.API)
			    .addConnectionCallbacks(this)
			    .addOnConnectionFailedListener(this)
			    .build();

	    //
	    // 플레이어 데이터 생성
	    TreeMap<Float, String> cutlines = new TreeMap<Float, String>();
	    cutlines.put(0.0f, context.getString(R.string.level_1));
	    cutlines.put(100.0f, context.getString(R.string.level_2));
	    cutlines.put(1000.0f, context.getString(R.string.level_3));
	    cutlines.put(10000.0f, context.getString(R.string.level_4));


        // set player data to default
        data = new PlayerData(cutlines);
        data.bestScore = 0;
        data.camulScore = 0;
        data.exp = 0;
        data.coins = 0;
        data.level = context.getString(R.string.level_1);
        data.name = context.getString(R.string.player_name_default);

        this.listener = listener;
    }



	public PlayerData getPlayerData()
	{
		return data;
	}
	public GoogleApiClient getGoogleApiClient() { return client; }



    public void connect()
    {
        if(client != null)
            client.connect();
    }

    public void disconnect()
    {
        if(client != null)
        {
            client.disconnect();
        }
    }





    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        int count = dataEvents.getCount();
        Log.d("Hey~", "onDataChanged 이벤트가 " + count + "개 발생!");

        for(int i = 0; i < count; ++i)
        {
            DataEvent event = dataEvents.get(i);
            DataItem item = event.getDataItem();
            Log.d("DataItem URI", item.getUri().toString());
            if(isPlayerUri(item.getUri()))
            {
                getDataFromMap(DataMapItem.fromDataItem(item).getDataMap());
            }
        }
    }




    private void findPlayerInBuffer(DataItemBuffer buffer)
    {
        int count = buffer.getCount();
        Log.d("Hey~", "onConnected에서 데이터 아이템을들 " + count + "개 받아오겠스!");

        for(int i = 0; i < count; ++i)
        {
            DataItem item = buffer.get(i);

            Log.d("DataItem URI", item.getUri().toString());

            if(!isPlayerUri(item.getUri()))
            {
                continue;
            }

            DataMapItem mapItem = DataMapItem.fromDataItem(item);
            DataMap map = mapItem.getDataMap();

            getDataFromMap(map);
        }
    }

    private boolean isPlayerUri(Uri uri)
    {
        return uri.getPath().equals("/player");
    }

    private void getDataFromMap(DataMap map)
    {
        data.bestScore = map.getFloat("best_score", 0);
        data.camulScore = map.getFloat("camul_score", 0);
        data.coins = map.getInt("coins", 0);
        data.setExp(map.getFloat("exp", 0));
        data.name = map.getString("name", "이름없는 파이터");

        if(listener != null)
            listener.onPlayerDataChanged(data);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Wearable.DataApi.removeListener(client, this);
	    Log.d("FireFist", "연결 실패~");
    }

    @Override
    public void onConnected(Bundle bundle) {
	    Log.d("FireFist", "연결 성공");
        Wearable.DataApi.addListener(client, this);

        PendingResult<DataItemBuffer> result =
                Wearable.DataApi.getDataItems(client);
        result.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                findPlayerInBuffer(dataItems);
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
	    Log.d("FireFist", "연결 중단!");
	    Wearable.DataApi.removeListener(client, this);
    }


	public static PlusClient createPlusClient(
			Context context,
			GooglePlayServicesClient.ConnectionCallbacks callbacks,
			GooglePlayServicesClient.OnConnectionFailedListener lis
	)
	{
		return new PlusClient.Builder(context, callbacks, lis)
				.setScopes( TextUtils.join(
								" ",
								new String[]{
										"https://www.googleapis.com/auth/plus.login",
										"https://www.googleapis.com/auth/plus.me",
										"https://www.googleapis.com/auth/userinfo.email"
								}
						)
				)
				.setActions(
						"http://schemas.google.com/AddActivity",
						"http://schemas.google.com/BuyActivity"
				)
				.build();
	}
}

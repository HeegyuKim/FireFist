package unipi.kr.firefist.game;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

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
    boolean connected = false;
    PlayerListener listener;


    public FireFist(Context context, PlayerListener listener)
    {
        this.context = context;
        client = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // set player data to default
        data = new PlayerData();
        data.bestScore = 0;
        data.camulScore = 0;
        data.exp = 0;
        data.coins = 0;
        data.level = "물주먹";
        data.name = "멋쟁이 파이터";

        this.listener = listener;
    }

    public PlayerData getPlayerData()
    {
        return data;
    }



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
            connected = false;
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
        connected = false;
        Wearable.DataApi.removeListener(client, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        connected = true;

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
        connected = false;
        Wearable.DataApi.removeListener(client, this);
    }
}

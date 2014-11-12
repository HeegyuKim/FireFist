package unipi.kr.firefist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.concurrent.TimeUnit;

/**
 * Created by KimHeekue on 2014-11-12.
 */
public class ResetTask extends AsyncTask<Void, Void, Void>
    implements GoogleApiClient.OnConnectionFailedListener
                , GoogleApiClient.ConnectionCallbacks
{
    Context context;
    String name;
    public ResetTask(Context context, String name)
    {
        this.context = context;
        this.name = name;
    }


    @Override
    protected Void doInBackground(Void ...params)
    {
        final GoogleApiClient client = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
        ConnectionResult result = client.blockingConnect(5, TimeUnit.SECONDS);
        if(!result.isSuccess())
        {
            alert("에러", "연결된 웨어러블 기기가 없습니다. 에러 코드: "
                    + result.getResolution());
            return null;
        }

        PendingResult<DataItemBuffer> buffer = Wearable.DataApi.getDataItems(client);
        DataItemBuffer dataItems =  buffer.await();

        for(int i = 0; i < dataItems.getCount(); ++i)
        {
            DataMapItem item = DataMapItem.fromDataItem(dataItems.get(i));
            if(item.getUri().getPath().equals("/player"))
            {
                Log.d("리셋!", item.getUri() + " 에다가 " + name + " 으로 지정!");
                PutDataMapRequest req = PutDataMapRequest.createFromDataMapItem(item);
                req.getDataMap().clear();
                req.getDataMap().putString("name", name);
                Wearable.DataApi.putDataItem(client, req.asPutDataRequest());
            }
        }

        alert("초기화 성공", name + " 님 반갑습니다!");
        client.disconnect();

        return null;
    }

    private void alert(String title, String message)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

package unipi.kr.firefist.gaming;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import unipi.kr.firefist.Vector3;

/**
 * Created by KimHeekue on 2014-11-04.
 */
public class ScoreAgent
{

    float score;
    long lastChangedTime;
    boolean changed;

    public ScoreAgent(float score)
    {
        this.score = score;
        this.lastChangedTime = 0L;
        this.changed = false;
    }


    public void addSample(Vector3 acc)
    {
        float accLen = acc.length() - 20;
        if(accLen <= 0) return;

        long currTime = System.currentTimeMillis();
        long delta = currTime - lastChangedTime;

        if(accLen > score || delta > 1000)
        {
            this.lastChangedTime = currTime;
            this.score = accLen;
            this.changed = true;
        }
    }

    public float getScore()
    {
        return score;
    }
    public boolean isScoreChanged()
    {
        return changed;
    }
    public void notifyChanging()
    {
        changed = false;
    }
}

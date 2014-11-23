package unipi.kr.firefist.game;

import unipi.kr.firefist.utils.Vector3;

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

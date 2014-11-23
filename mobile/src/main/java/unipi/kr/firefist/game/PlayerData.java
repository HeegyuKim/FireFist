package unipi.kr.firefist.game;

import android.util.Log;

import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by KimHeekue on 2014-11-12.
 */
public class PlayerData {

    TreeMap<Float, String> cutlines;

    public String name, level;
    public float exp, bestScore, camulScore;
    public int coins;

    public PlayerData(TreeMap<Float, String> cutlines) {
	    this.cutlines = cutlines;
    }

    public void setExp(float exp)
    {
        this.exp = exp;

        for(Map.Entry<Float, String> entry : cutlines.entrySet())
        {
            if(this.exp >= entry.getKey())
            {
                this.level = entry.getValue();
            }
        }
    }
}

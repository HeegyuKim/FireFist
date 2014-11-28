package unipi.kr.firefist.gaming;

import android.content.Context;

import java.util.TreeMap;

import unipi.kr.firefist.R;

/**
 * Created by KimHeeKue on 2014-11-26.
 */
public class LevelTable {

	Context context;

	public LevelTable(Context context)
	{
		this.context = context;
	}


	public String findLevel(double exp)
	{
		if(exp < 100)
			return level(R.string.level_1);

		else if(isInside(exp, 100, 1000))
			return level(R.string.level_2);

		else if(isInside(exp, 1000, 10000))
			return level(R.string.level_3);

		else // if(exp >= 10000)
			return level(R.string.level_4);


	}

	private boolean isInside(double exp, double min, double max)
	{
		return exp >= min && exp < max;
	}

	private String level(int stringId)
	{
		return context.getString(stringId);
	}
}

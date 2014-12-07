package unipi.kr.firefist.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KimHeeKue on 2014-12-01.
 */
public class Container implements IFeature
{
	public final List<IFeature> featureList = new ArrayList<IFeature>();
	private boolean enabled = false;

	public Container()
	{
	}


	@Override
	public void setEnable(boolean enabled)
	{
		if(this.enabled == enabled) return;

		this.enabled = enabled;
		for(IFeature feature : featureList)
			feature.setEnable(enabled);
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}
}

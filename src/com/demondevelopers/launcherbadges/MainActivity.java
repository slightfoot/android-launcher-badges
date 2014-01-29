package com.demondevelopers.launcherbadges;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;


public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.increment).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainActivity activity = (MainActivity)v.getContext();
				if(AppBadge.incrementBadgeCount(activity)){
					AppBadge.updateActivityIcon(activity);
					activity.updateIcon(AppBadge.getCurrentBadge(activity));
				}
			}
		});
		
		findViewById(R.id.decrement).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainActivity activity = (MainActivity)v.getContext();
				if(AppBadge.decrementBadgeCount(activity)){
					AppBadge.updateActivityIcon(activity);
					activity.updateIcon(AppBadge.getCurrentBadge(activity));
				}
			}
		});
		
		updateIcon(AppBadge.getCurrentBadge(this));
	}
	
	public void updateIcon(Drawable drawable)
	{
		ImageView iconView = (ImageView)findViewById(R.id.icon);
		iconView.setImageDrawable(drawable);
		iconView.setScaleX(0.15f);
		iconView.setScaleY(0.15f);
		iconView.animate().setDuration(150)
			.scaleX(1.0f).scaleY(1.0f);
		iconView.invalidate();
	}
}

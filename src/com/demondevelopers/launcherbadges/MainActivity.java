package com.demondevelopers.launcherbadges;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;


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
				AppBadge.incrementBadgeCount(v.getContext());
				AppBadge.updateActivityIcon(MainActivity.this);
			}
		});
		
		findViewById(R.id.decrement).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AppBadge.decrementBadgeCount(v.getContext());
				AppBadge.updateActivityIcon(MainActivity.this);
			}
		});
	}
}

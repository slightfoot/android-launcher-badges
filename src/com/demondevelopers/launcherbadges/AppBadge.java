/*
	Copyright 2014 Simon Lightfoot / Demon Developers Ltd
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
		http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.demondevelopers.launcherbadges;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;


public final class AppBadge
{
	private static final String TAG                 = AppBadge.class.getSimpleName();
	public  static final String ACTIVITY_BADGE_META = "com.demondevelopers.launcherbadge";
	private static final String KEY_BADGE_COUNT     = "badge_count";
	
	private static AppBadge sInstance;
	
	private ArrayList<ComponentName> mBadges = new ArrayList<ComponentName>();
	private SharedPreferences        mSharedPrefs;
	private Context                  mAppContext;
	
	
	private AppBadge(Context context)
	{
		mAppContext  = context.getApplicationContext();
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(mAppContext);
		PackageManager pkgManager = mAppContext.getPackageManager();
		try{
			String packageName  = mAppContext.getPackageName();
			PackageInfo pkgInfo = pkgManager.getPackageInfo(packageName, 
				PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA | 
				PackageManager.GET_DISABLED_COMPONENTS);
			for(ActivityInfo activityInfo : pkgInfo.activities){
				if(activityInfo.metaData != null && activityInfo.metaData.containsKey(ACTIVITY_BADGE_META)){
					ComponentName componentName = new ComponentName(packageName, activityInfo.name);
					int badge = activityInfo.metaData.getInt(ACTIVITY_BADGE_META);
					Log.d(TAG, "Found Badge " + badge + " is " + componentName.toShortString());
					mBadges.add(badge, componentName);
				}
			}
		}
		catch(NameNotFoundException e){
			throw new RuntimeException("This should never occur because the package is installed.", e);
		}
	}
	
	public static synchronized AppBadge getInstance(Context context)
	{
		if(sInstance == null){
			sInstance = new AppBadge(context);
		}
		return sInstance;
	}
	
	public static boolean incrementBadgeCount(Context context)
	{
		return getInstance(context).incrementBadgeCount();
	}
	
	public static boolean decrementBadgeCount(Context context)
	{
		return getInstance(context).decrementBadgeCount();
	}
	
	public static Drawable getCurrentBadge(Context context)
	{
		return getInstance(context).getCurrentBadge();
	}
	
	@SuppressLint("NewApi")
	public static void updateActivityIcon(Activity activity)
	{
		Drawable icon = getInstance(activity).getCurrentBadge();
		if(activity.getWindow().hasFeature(Window.FEATURE_LEFT_ICON)){
			activity.setFeatureDrawable(Window.FEATURE_LEFT_ICON, icon);
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			activity.getActionBar().setIcon(icon);
		}
	}
	
	public boolean incrementBadgeCount()
	{
		return updateBadgeCount(+1);
	}
	
	public boolean decrementBadgeCount()
	{
		return updateBadgeCount(-1);
	}
	
	private int getCurrentBadgeCount()
	{
		return mSharedPrefs.getInt(KEY_BADGE_COUNT, 0);
	}
	
	public synchronized Drawable getCurrentBadge()
	{
		try{
			return mAppContext.getPackageManager()
				.getActivityIcon(mBadges.get(getCurrentBadgeCount()));
		}
		catch(NameNotFoundException e){
			throw new RuntimeException(e);
		}
	}
	
	private synchronized boolean updateBadgeCount(int delta)
	{
		int prevCount = getCurrentBadgeCount();
		int nextCount = prevCount + delta;
		if(nextCount < 0){
			nextCount = 0;
		}
		else if(nextCount >= mBadges.size()){
			nextCount = mBadges.size() - 1;
		}
		if(prevCount == nextCount){
			return false;
		}
		
		if(!mSharedPrefs.edit().putInt(KEY_BADGE_COUNT, nextCount).commit()){
			return false;
		}
		
		ComponentName prevBadge = mBadges.get(prevCount);
		ComponentName nextBadge = mBadges.get(nextCount);
		
		PackageManager pkgManager = mAppContext.getPackageManager();
		
		Log.d(TAG, "Disabling Badge " + prevCount + " " + prevBadge.toShortString());
		pkgManager.setComponentEnabledSetting(prevBadge, 
			PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			PackageManager.DONT_KILL_APP);
		
		Log.d(TAG, "Enabling  Badge " + nextCount + " " + nextBadge.toShortString());
		pkgManager.setComponentEnabledSetting(nextBadge, 
			PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
			PackageManager.DONT_KILL_APP);
		
		return true;
	}
}

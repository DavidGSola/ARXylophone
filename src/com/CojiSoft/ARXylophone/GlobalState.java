package com.CojiSoft.ARXylophone;

import android.app.Application;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;

public class GlobalState extends Application
{
	public String PROPERTY_ID = "UA-37070365-4";
	
	public synchronized Tracker getTracker()
    {
    	GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker t = analytics.getTracker(PROPERTY_ID);
    
    	return t;
    }
}

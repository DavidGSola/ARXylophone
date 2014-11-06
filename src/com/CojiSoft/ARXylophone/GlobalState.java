/**
 * Copyright 2014 David González Sola (hispalos@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

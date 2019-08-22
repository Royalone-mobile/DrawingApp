package com.songu.shadow.drawing;



import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;


@ReportsCrashes(
        mailTo = "pgyhw718@hotmail.com", // my email here
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class MainApplication  extends Application
{	
	@Override
    public void onCreate() 
	{
        super.onCreate();
        ACRA.init(this);
    }

    @Override
    public void onTerminate() 
    {
        super.onTerminate();
    }
}

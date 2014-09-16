package com.pichula.frapi;

import android.app.Application;
import android.content.Context;

public class MainApplication extends Application{

	
	static Context mContext;
	
	@Override
	public void onCreate() {
		mContext=this;
		super.onCreate();
	}
	
	public static Context getContext(){
		return mContext;
	}
}

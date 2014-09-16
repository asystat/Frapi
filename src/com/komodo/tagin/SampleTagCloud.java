package com.komodo.tagin;
/**
 * Komodo Lab: Tagin! Project: 3D Tag Cloud
 * Google Summer of Code 2011
 * @authors Reza Shiftehfar, Sara Khosravinasr and Jorge Silva
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.pichula.frapi.DataController;
import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.MutableFloat;

/**
 * SampleTagCloud class:
 * this is a sample program to show how the 3D Tag Cloud can be used.
 * It Creates the activity and sets the ContentView to our TagCloudView class
 */
public class SampleTagCloud extends Activity {

	Dictionary dic;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Step0: to get a full-screen View:
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		long dict_id = getIntent().getLongExtra("id", 0);
		dic = DataController.instance.getDictionary(dict_id);
		if (dic == null){
			finish();
			return;
		}
		
		//Step1: get screen resolution:
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();
		
		//Step2: create the required TagList:
		//notice: All tags must have unique text field
		//if not, only the first occurrence will be added and the rest will be ignored
		List<Tag> myTagList= createTags();
		
		//Step3: create our TagCloudview and set it as the content of our MainActivity
		mTagCloudView = new TagCloudView(this, width, height, myTagList ); //passing current context 
		setContentView(mTagCloudView);
		mTagCloudView.requestFocus();
		mTagCloudView.setFocusableInTouchMode(true);
		
		//Step4: (Optional) adding a new tag and resetting the whole 3D TagCloud
		//you can also add individual tags later:
		//mTagCloudView.addTag(new Tag("AAA", 5, "http://www.aaa.com"));
		// .... (several other tasg can be added similarly )
		//indivual tags will be placed along with the previous tags without moving 
		//old ones around. Thus, after adding many individual tags, the TagCloud 
		//might not be evenly distributed anymore. reset() re-positions all the tags:
		//mTagCloudView.reset();
		
		//Step5: (Optional) Replacing one of the previous tags with a new tag
		//you have to create a newTag and pass it in together 
		//with the Text of the existing Tag that you want to replace
		//Tag newTag=new Tag("Illinois", 9, "http://www.illinois.com");
		//in order to replace previous tag with text "Google" with this new one:
		//boolean result=mTagCloudView.Replace(newTag, "google");
		//result will be true if "google" was found and replaced. else result is false
	}

	protected void onResume() {
		super.onResume();
	}
	
	protected void onPause() {
		super.onPause();
	}
	
	private List<Tag> createTags(){
		//create the list of tags with popularity values and related url

		dic.sort(Dictionary.P_APPEARANCE_DESC);
		Vector mData=new Vector();
		mData.addAll(dic.getWords().entrySet());
		List<Tag> tempList = new ArrayList<Tag>();
		
		for(int i=0;i<20 && i<mData.size();i++){
			Map.Entry<String, MutableFloat> item=(Entry<String, MutableFloat>) mData.get(i);
			tempList.add(new Tag(item.getKey(), (int)item.getValue().get(), "https://www.google.es/search?q="+item.getKey()));
		}
		return tempList;
	}
	
	private TagCloudView mTagCloudView;
}
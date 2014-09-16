package com.pichula.frapi.api;

import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.UserDictionary;
import android.util.Log;

public class Frapi {
	
	public static Dictionary create(String url,int n,long id, DictionaryHandler h) {
		return Dictionary.create(url,n, id, h);
	}

	public static void no_numbers(Dictionary d, int num_digits) {
		d.no_numbers(num_digits);
	}

	public static void to_lower(Dictionary d) {
		d.toLower();
	}

	public static void to_capital(Dictionary d) {
		d.toLUpper();
	}

	public static void plurals(Dictionary d, String plurals) {
		d.plurals(plurals);
	}

	public static Dictionary relevant(Dictionary d, String relevants) {
		return d.relevant(relevants);
	}

	public static void sort(Dictionary d, int pattern) {
		d.sort(pattern);
	}

	public static void set_language(Dictionary d, String language) {
		d.setLanguage(language);
	}

	public static String get_language(Dictionary d) {
		return d.getLanguage(); 
	}

	public static void set_id(Dictionary d, int id) {
		d.id = id;
	}

	public static long get_id(Dictionary d) {
		return d.id;
	}

	public static Dictionary common(Dictionary d) {
		//TODO: caca
		return null;
	}

	public static Dictionary exceptional(Dictionary d) {
		//TODO: caca
		return null;
	}

	public static float find_word(Dictionary d, String s) {
		return d.frequency(s);
	}

	public static void add_word(Dictionary d, String w, int f) {
		d.add_word(w,f);
	}

	public static void kill_word(Dictionary d, String words) {
		d.kill_word(words);
	}

	public static Dictionary probabilities(Dictionary d) {
		return d.probabilities();
	}

	public static float entropy(Dictionary d) {
		return d.entropy();
	}

	public static Dictionary sum(Dictionary d1, Dictionary d2) {
		return d1.sum(d2);
	}

	public static Dictionary substract(Dictionary d1, Dictionary d2) {
		return d1.substract(d2);
	}

	//TODO: WTF???
	public static float distances(Dictionary d1, Dictionary d2) {
		return 0f;
	}

	// Library

	public static Library createLibrary(){
		Library l = new Library();
		return l;
	}
	
	public static Library createLibrary(Dictionary d1, Dictionary d2) {
		Library l = new Library();
		l.dics.add(d1);
		l.dics.add(d2);
		return null;
	}

	public static void add(Dictionary d, Library l) {
		l.dics.add(d);
	}

	public static void purge(Dictionary d, Library l) {
		for (Dictionary dic : l.dics) {
			if (dic.id == d.id) {
				l.dics.remove(dic);
				break;
			}
		}
	}

	public static Dictionary total(Library l) {
		if(l.dics==null)
			return null;
		if(l.dics.size()==1)
			return l.dics.get(0).clone();
		Dictionary d=null;
		for (Dictionary dic : l.dics) {
			if(d==null){
				d=dic.clone();
			}
			else{
				d=d.sum(dic);
			}
		}
		return d;
	}

	public static float total_sigma(Library l) {
		return 0f;
	}

	public static void getFromDictionary(Activity a) {
		ContentResolver resolver = a
				.getContentResolver();
		String[] projection = new String[] { UserDictionary.Words._ID,
				UserDictionary.Words.WORD, UserDictionary.Words.FREQUENCY };
		Cursor cursor = resolver.query(UserDictionary.Words.CONTENT_URI,
				projection, null, null, null);
		while (cursor.moveToNext()) {
			String word = cursor.getString(1);
			Log.e("DICCIONARIO", word);

		}
	}
	
	public static Map<Long, Float> distances(Library l, long dic_id){
		
		
		
		Dictionary originalDic=null;
		Dictionary theDic=null;
		for (Dictionary dic : l.dics) {
			if(dic.id==dic_id){
				originalDic=dic;
				theDic=dic.probabilities();
				break;
			}
		}
		if(theDic==null)
			return null;
		
		for (Dictionary dic : l.dics) {
			Log.e("Calculando distancia", "...");
			if(dic.id==dic_id){
				continue;
			}
			float h=theDic.distances(dic.probabilities());
			dic.setDistance(theDic, h);
			originalDic.setDistance(dic, h);
			Log.e("Listo", "...");
		}
		
		return originalDic.distances;
		
	}
	
	public static void far(Library l, long dic_id){
		//TODO:WTF!!
	}
	
	public static void near(Library l, long dic_id){
		//TODO:WTF!!
	}
	
	public static boolean find_word(Library l, String word){
		for (Dictionary dic : l.dics) {
			if(dic.frequency(word)>0)
				return true;
		}
		return false;
	}
	
	public static void add_word(Library l, String s, float f){
		for (Dictionary dic : l.dics) {
			dic.add_word(s, f);
		}
	}
	
	public static void kill_word(Library l, String s){
		for (Dictionary dic : l.dics) {
			dic.kill_word(s);
		}
	}
	
	public static Dictionary shared_words(Library l){
		
		Dictionary shared=null;
		
		for (Dictionary dic : l.dics) {
			if(shared==null){
				shared=dic.clone();
				continue;
			}
			if(shared.getSize()==0)
				return shared;
			shared=dic.commons(shared);
		}
		return shared;
	}
	
	public static void exceptional_words(Library l){
		Dictionary shared= shared_words(l);
		for (Dictionary dic : l.dics) {
			dic=dic.substract(shared);
		}
	}
	
	public static Dictionary over_the_mean(Library l){
		//TODO: WTF!??
		return null;
	}
	
	public static Dictionary under(Library l){
		//TODO: WTF!??
		return null;
	}
	
	public static float strategies(Library l, String words){
		
		float sum=0;
		
		for (Dictionary dic : l.dics) {
			if(!dic.isProbability())
				dic.probabilities();
			sum+=dic.frequency(words);
		}
		return sum;
	}

}

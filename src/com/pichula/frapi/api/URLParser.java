package com.pichula.frapi.api;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class URLParser {

	public static String parseUrl(String url){
		//String url = "http://stackoverflow.com/questions/2835505";
        Document document=null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return document.text();        
	}
	
}


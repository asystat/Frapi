package com.pichula.frapi;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import java.util.Vector;

import nl.qbusict.cupboard.DatabaseCompartment;
import nl.qbusict.cupboard.QueryResultIterable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CupboardSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "frapi.db";
	private static final int DATABASE_VERSION = 1;
	private static CupboardSQLiteOpenHelper instance = new CupboardSQLiteOpenHelper(
			MainApplication.getContext());

	static {
		// register our models
		cupboard().register(DictionaryItem.class);
	}

	private CupboardSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// this will ensure that all tables are created
		cupboard().withDatabase(db).createTables();
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// this will upgrade tables, adding columns and new tables.
		// Note that existing columns will not be converted
		cupboard().withDatabase(db).upgradeTables();
		// do migration work
	}

	/* Creator */

	public static Vector<DictionaryItem> getDictionaryItems() {
		
		Vector<DictionaryItem> v=new Vector<DictionaryItem>();
		Cursor cursor=cupboard().withDatabase(instance.getReadableDatabase()).query(DictionaryItem.class).getCursor();
		
		QueryResultIterable<DictionaryItem> itr = cupboard().withCursor(cursor).iterate(DictionaryItem.class);
		  for (DictionaryItem di : itr) {
			di.status=DictionaryItem.STATUS_INACTIVE;
		    v.add(di);
		  }
		  itr.close();
		  return v;
	}

	public static void deleteDictionaryItem(DictionaryItem di) {
		cupboard().withDatabase(instance.getWritableDatabase()).delete(DictionaryItem.class, di._id);
	}

	public static long addDictionaryItem(DictionaryItem di) {
		return cupboard().withDatabase(instance.getWritableDatabase()).put(di);
	}
	
	
	public static void addDefaults(){
		DictionaryItem di=new DictionaryItem();
		di.imgurl="";
		di.url="http://elpais.com/";
		di.name="El Pais";
		di._id=1L;
		addDictionaryItem(di);
		
		DictionaryItem doo=new DictionaryItem();
		doo.imgurl="";
		doo.url="http://www.abc.es/";
		doo.name="ABC";
		doo._id=2L;
		addDictionaryItem(doo);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.elmundo.es/";
		di.name="El Mundo";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.heraldo.es/";
		di.name="Heraldo";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.gaceta.es/";
		di.name="La Gaceta";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.larazon.es/";
		di.name="La Razon";
		di._id=null;
		addDictionaryItem(di);
		
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://as.com/";
		di.name="as";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.marca.com/";
		di.name="Marca";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.expansion.com/";
		di.name="Expansi—n";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.eleconomista.es/";
		di.name="El Economista";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://cincodias.com/";
		di.name="Cinco Dias";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.elperiodico.com/es/";
		di.name="El Periodico";
		di._id=null;
		addDictionaryItem(di);
		
		di=new DictionaryItem();
		di.imgurl="";
		di.url="http://www.lavozdegalicia.es/";
		di.name="La Voz de Galicia";
		di._id=null;
		addDictionaryItem(di);
	}

}
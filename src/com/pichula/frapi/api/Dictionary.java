package com.pichula.frapi.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.os.AsyncTask;
import android.util.Log;

public class Dictionary {

	private CreateFromUrlTask m_createFromUrlTask;
	private CreateFromInputStreamTask m_createFromInputStreamTask;
	private DictionaryHandler m_Handler;

	Map<String, MutableFloat> words;
	public long id;
	private String language;
	int n;
	public float Fi;
	public boolean ready = false;
	public float maxCount = 1;

	public String name;
	public String url;

	public Map<Long, Float> distances = null;

	private Dictionary() {

	}

	public static Dictionary create(String url, int n, long id,
			DictionaryHandler handler) {
		Dictionary d = new Dictionary();
		d.url = url;
		d.n = n;
		d.Fi = 0;
		d.id = id;
		d.m_Handler = handler;
		d.m_createFromUrlTask = d.new CreateFromUrlTask();
		d.m_createFromUrlTask.execute(url);

		return d;
	}

	public static Dictionary create(InputStream is, int n,
			DictionaryHandler handler) {
		Dictionary d = new Dictionary();
		d.n = n;
		d.Fi = 0;
		d.id = 0;
		d.m_Handler = handler;
		d.m_createFromInputStreamTask = d.new CreateFromInputStreamTask();
		d.m_createFromInputStreamTask.execute(is);
		return d;
	}

	public void setLanguage(String lan) {
		language = lan.toLowerCase();
	}

	public Map<String, MutableFloat> getWords() {
		return words;
	}

	public String getLanguage() {
		return language;
	}

	private void populateFromString(String raw_words, boolean filter) {
		Fi = 0;
		String[] wordsArray = raw_words.split("\\s+");
		words = new HashMap<String, MutableFloat>();
		String tempString = "";
		int ncount = 0;
		for (String s : wordsArray) {
			ncount++;
			tempString += s + " ";
			if (ncount % n == 0) {

				tempString = tempString.toLowerCase().trim();
				if (filter && filterString(tempString)) {
					ncount = 0;
					tempString = "";
					continue;
				}
				MutableFloat count = words.get(tempString);
				if (count == null) {
					words.put(tempString, new MutableFloat());
				} else {
					count.increment();
					if (count.value > maxCount)
						maxCount = count.value;
				}
				Fi += 1;
				ncount = 0;
				tempString = "";
			}
		}
		ready = true;
	}

	private boolean filterString(String s) {
		if (s.length() <= 3)
			return true;
		if (s.matches(".*\\d.*"))
			return true;

		return false;
	}

	public boolean isEmpty() {
		if (words == null || words.size() == 0)
			return true;
		return false;
	}

	public int getSize() {
		if (words == null)
			return 0;
		return words.size();
	}

	private class CreateFromUrlTask extends AsyncTask<String, String, Void> {

		private long startMillis;

		@Override
		protected void onPreExecute() {
			startMillis = System.currentTimeMillis();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... arg0) {
			try {
				publishProgress("Descargando...");
				String raw_words = URLParser.parseUrl(arg0[0]);
				publishProgress("Parseando...");
				populateFromString(raw_words, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (words != null && words.size() > 0)
				m_Handler.onSuccess("Se ha parseado la url con Žxito",
						Dictionary.this);
			else
				m_Handler.onError("Ha ocurrido un error al parsear!");
			long totalMillis = System.currentTimeMillis() - startMillis;
			Log.e("Parsing+processing time (millis) ", totalMillis + " ");
		}

		@Override
		protected void onProgressUpdate(String... values) {
			m_Handler.onProgress(values[0]);
		}

	}

	private String getStringFromIs(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line + " ");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	private class CreateFromInputStreamTask extends
			AsyncTask<InputStream, String, Void> {

		private long startMillis;

		@Override
		protected void onPreExecute() {
			startMillis = System.currentTimeMillis();
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(InputStream... arg0) {
			try {
				publishProgress("Abriendo fichero...\nEste proceso se hace solo una vez");
				String raw_words = getStringFromIs(arg0[0]);
				publishProgress("Creando diccionario espa–ol...");
				populateFromString(raw_words, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (words != null && words.size() > 0)
				m_Handler.onSuccess("Se ha parseado la url con Žxito",
						Dictionary.this);
			else
				m_Handler.onError("Ha ocurrido un error al parsear!");
			long totalMillis = System.currentTimeMillis() - startMillis;
			Log.e("Parsing+processing time (millis) ", totalMillis + " ");
		}

		@Override
		protected void onProgressUpdate(String... values) {
			m_Handler.onProgress(values[0]);
		}

	}

	private void sortByValue(boolean desc) {
		if (isEmpty())
			return;
		words = MapUtil.sortByValue(words, desc);
	}

	private void sortByKey(boolean desc) {
		if (isEmpty())
			return;
		words = MapUtil.sortByKey(words, desc);
	}

	public static final int P_ALPHABETICALLY = 0;
	public static final int P_ALPHABETICALLY_DESC = 1;
	public static final int P_APPEARANCE = 2;
	public static final int P_APPEARANCE_DESC = 3;

	public void sort(int pattern) {

		switch (pattern) {
		case P_ALPHABETICALLY:
			sortByKey(false);
			break;
		case P_ALPHABETICALLY_DESC:
			sortByKey(true);
			break;
		case P_APPEARANCE:
			sortByValue(false);
			break;
		case P_APPEARANCE_DESC:
			sortByValue(true);
			break;

		default:
			break;
		}

	}

	public void showLog() {
		Iterator it = words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Log.e((String) pairs.getKey(),
					" = " + ((MutableFloat) pairs.getValue()).value);
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	public void no_numbers(int num_digits) {
		Iterator it = words.entrySet().iterator();
		String stringsToKill = "";
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();

			if (android.text.TextUtils.isDigitsOnly((String) pairs.getKey())) {
				if (((String) pairs.getKey()).length() <= num_digits) {
					/*
					 * MutableFloat mf = words.remove(pairs.getKey()); Fi -=
					 * mf.value;
					 */
					stringsToKill += pairs.getKey() + " ";
				}
			}
		}

		kill_word(stringsToKill);

	}

	public void toLower() {

		HashMap<String, MutableFloat> temp = new HashMap<String, MutableFloat>();

		Iterator it = words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			temp.put(((String) pairs.getKey()).toLowerCase(),
					(MutableFloat) pairs.getValue());
		}
		words.clear();
		words = temp;
	}

	public void toLUpper() {

		HashMap<String, MutableFloat> temp = new HashMap<String, MutableFloat>();

		Iterator it = words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			temp.put(((String) pairs.getKey()).toUpperCase(),
					(MutableFloat) pairs.getValue());
		}
		words.clear();
		words = temp;
	}

	private void unifyWords(String[] w) {
		if (n == 1) {
			if (words.containsKey(w[0]) && words.containsKey(w[1])) {
				float nPlurals = words.get(w[1]).value;
				words.get(w[0]).increment(nPlurals);
				words.remove(w[1]);
			}
		} else {
			String s1 = "";
			String s2 = "";
			for (int i = 0; i < n; i++)
				s1 += w[i] + " ";
			for (int i = n; i < n * 2; n++) {
				s2 += w[i] + " ";
			}
			s1 = s1.trim();
			s2 = s2.trim();
			if (words.containsKey(s1) && words.containsKey(s2)) {
				float nPlurals = words.get(s2).value;
				words.get(s1).increment(nPlurals);
				words.remove(s2);
			}
		}
	}

	public void plurals(String plurals) {
		BufferedReader bufReader = new BufferedReader(new StringReader(plurals));
		String line = null;
		try {
			while ((line = bufReader.readLine()) != null) {
				String[] wordsArray = line.split("\\s+");
				if ((wordsArray.length / n) != 2) {
					Log.e("Warning",
							"String de plurales no bien formado para este diccionario");
					continue;
				}
				unifyWords(wordsArray);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Dictionary relevant(String relevants) {
		String[] wordsArray = relevants.split("\\s+");
		HashMap<String, MutableFloat> relevantDictionary = new HashMap<String, MutableFloat>();

		float newF1 = 0;
		int ncount = 0;
		String tempStr = "";
		for (String s : wordsArray) {
			ncount++;
			tempStr += s + " ";
			if (ncount % n == 0) {
				tempStr = tempStr.toLowerCase().trim();
				if (words.containsKey(tempStr)
						&& !relevantDictionary.containsKey(tempStr)) {
					relevantDictionary.put(tempStr, words.get(tempStr));
					newF1 += words.get(s).value;
				}
				ncount = 0;
				tempStr = "";
			}

		}
		Dictionary d = new Dictionary();
		d.words = relevantDictionary;
		d.language = language;
		d.n = n;
		d.Fi = newF1;
		d.ready = true;
		return d;
	}

	public float frequency(String s) {
		MutableFloat theInt = words.get(s);
		if (theInt == null)
			return 0;
		return theInt.value;
	}

	public float frequencySum(String swords) {
		String[] wordsArray = swords.split("\\s+");

		float probSum = 0;

		int ncount = 0;
		String tempStr = "";
		for (String s : wordsArray) {
			ncount++;
			tempStr += s + " ";
			if (ncount % n == 0) {
				tempStr = tempStr.toLowerCase().trim();
				if (words.containsKey(tempStr)) {
					probSum += words.get(s).value;
				}
				ncount = 0;
				tempStr = "";
			}
		}
		return probSum;
	}

	public void add_word(String w, float f) {
		if (!words.containsKey(w)) {
			MutableFloat mi = new MutableFloat();
			mi.value = f;
			Fi += f;
			words.put(w, mi);
		}
	}

	public void kill_word(String w) {
		String[] wordsArray = w.split("\\s+");

		int ncount = 0;
		String tempStr = "";
		for (String s : wordsArray) {
			ncount++;
			tempStr += s + " ";
			if (ncount % n == 0) {
				tempStr = tempStr.toLowerCase().trim();
				if (words.containsKey(tempStr)) {
					MutableFloat mf = words.remove(tempStr);
					Fi -= mf.value;
				}
				ncount = 0;
				tempStr = "";
			}

		}
	}

	private void calculateTotalAppareances() {
		Iterator it = words.entrySet().iterator();
		Fi = 0;
		maxCount = 1;
		while (it.hasNext()) {

			Map.Entry pairs = (Map.Entry) it.next();
			float value = ((MutableFloat) pairs.getValue()).value;
			if (value > maxCount)
				maxCount = value;
			Fi += value;
		}
	}

	public Dictionary probabilities() {
		// calculamos sumatorio Fj
		if (Fi == 0)
			calculateTotalAppareances();

		HashMap<String, MutableFloat> probabilitiesDictionary = new HashMap<String, MutableFloat>();

		Iterator it = words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			float val = ((MutableFloat) pairs.getValue()).value / Fi;
			probabilitiesDictionary.put((String) pairs.getKey(),
					new MutableFloat(val));
		}

		Dictionary d = new Dictionary();
		d.words = probabilitiesDictionary;
		d.language = language;
		d.n = n;
		d.ready = true;
		d.id = id;
		return d;

	}

	public float distances(Dictionary d) {
		if (distances != null && distances.containsKey(d.id))
			return distances.get(d.id);
		if (!isProbability() || !d.isProbability())
			return 0f;
		if (n != d.n)
			return 0f;

		Dictionary comunes = commons(d);
		float h = 0f;

		Iterator it = comunes.words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			h += Math.abs(words.get(pairs.getKey()).value
					+ ((MutableFloat) pairs.getValue()).value);
		}
		return h;
	}

	public void setDistance(Dictionary d, float distance) {
		if (distances == null)
			distances = new HashMap<Long, Float>();
		if (distances.containsKey(d.id))
			distances.remove(d.id);
		distances.put(d.id, distance);
	}

	public boolean isProbability() {
		if (words != null) {
			Iterator it = words.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pairs = (Map.Entry) it.next();
				float val = ((MutableFloat) pairs.getValue()).value;
				if (val < 1f)
					return true;
				return false;
			}
		}
		Log.e("Error", "Dictionary is empty!");
		return false;
	}

	// tiene que recibir un diccionario con probabilidades!
	public float entropy() {
		Iterator it = words.entrySet().iterator();
		float total = 0;
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			float val = ((MutableFloat) pairs.getValue()).value;
			total += val * Math.log10(val);
		}
		return -total;
	}

	public Dictionary sum(Dictionary d2) {

		if (n != d2.n || !language.equalsIgnoreCase(d2.language)) {
			Log.e("Cannot merge dictionaries!",
					"n is not equal or different language");
			return null;
		}

		HashMap<String, MutableFloat> mergedDictionary = new HashMap<String, MutableFloat>(
				words);

		Iterator it = d2.words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			MutableFloat count = mergedDictionary.get(pairs.getKey());
			if (count == null) {
				mergedDictionary.put((String) pairs.getKey(),
						(MutableFloat) pairs.getValue());
			} else {
				count.value += ((MutableFloat) pairs.getValue()).value;
			}
		}
		Dictionary d = new Dictionary();
		d.words = mergedDictionary;
		d.language = language;
		d.n = n;
		d.Fi = 0f;
		d.ready = true;
		return d;
	}

	public void sum_save(Dictionary d2) {

		if (n != d2.n) {
			Log.e("Cannot merge dictionaries!",
					"n is not equal or different language");
			return;
		}

		Iterator it = d2.words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			MutableFloat count = words.get(pairs.getKey());
			if (count == null) {
				words.put((String) pairs.getKey(),
						(MutableFloat) pairs.getValue());
			} else {
				count.value += ((MutableFloat) pairs.getValue()).value;
			}
		}
		Fi = 0f;
		calculateTotalAppareances();
		ready = true;
	}

	public void substract_save(Dictionary d2) {
		if (n != d2.n) {
			Log.e("Cannot merge dictionaries!", "n is not equal");
			return;
		}

		Iterator it = d2.words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (words.containsKey(pairs.getKey())) {
				words.remove(pairs.getKey());
			}
		}
		Fi = 0f;
		calculateTotalAppareances();
		ready = true;
	}

	public Dictionary substract(Dictionary d2) {

		if (n != d2.n) {
			Log.e("Cannot merge dictionaries!", "n is not equal");
			return null;
		}

		HashMap<String, MutableFloat> mergedDictionary = new HashMap<String, MutableFloat>(
				words);

		Iterator it = d2.words.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (mergedDictionary.containsKey(pairs.getKey())) {
				mergedDictionary.remove(pairs.getKey());
			}
		}
		Dictionary d = new Dictionary();
		d.words = mergedDictionary;
		d.language = language;
		d.n = n;
		d.Fi = 0f;
		d.calculateTotalAppareances();
		d.ready = true;
		return d;
	}

	public Dictionary clone() {
		HashMap<String, MutableFloat> clonedDictionary = new HashMap<String, MutableFloat>(
				words);
		Dictionary d = new Dictionary();
		d.words = clonedDictionary;
		d.language = language;
		d.n = n;
		d.Fi = Fi;
		d.ready = true;
		return d;
	}

	// crea un diccionario nuevo solo con las palabras compartidas (deja las
	// frecuencias del que se pasa por parametro )
	public Dictionary commons(Dictionary shared) {

		String stringsToKill = "";

		HashMap<String, MutableFloat> clonedDictionary = new HashMap<String, MutableFloat>(
				shared.words);
		Iterator it = clonedDictionary.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			if (!words.containsKey(pairs.getKey())) {
				stringsToKill += pairs.getKey() + " ";
			}
		}

		Dictionary d = new Dictionary();
		d.words = clonedDictionary;
		d.language = language;
		d.n = n;
		d.Fi = 0;
		d.kill_word(stringsToKill);
		d.ready = true;
		return d;
	}

}

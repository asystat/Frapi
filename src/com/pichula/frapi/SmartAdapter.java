package com.pichula.frapi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pichula.frapi.api.Dictionary;
import com.pichula.frapi.api.Library;
import com.pichula.frapi.api.MapUtil;
import com.pichula.frapi.api.MutableFloat;

public class SmartAdapter extends BaseAdapter {

	Activity m_context;

	Library l;

	public SmartAdapter(Activity a) {
		m_context = a;
		l = DataController.instance.getLibrary();
	}

	@Override
	public int getCount() {
		return l.dics.size();
	}

	@Override
	public Object getItem(int position) {
		return l.dics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return l.dics.get(position).id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = m_context.getLayoutInflater().inflate(
					R.layout.smart_item, null);

		final Dictionary d = (Dictionary) getItem(position);
		String[] topFive = getTopFive(d);
		String[] near = getNear(d);

		TextView s_name = (TextView) convertView.findViewById(R.id.s_name);
		TextView s_url = (TextView) convertView.findViewById(R.id.s_url);
		TextView s_topfive = (TextView) convertView
				.findViewById(R.id.s_topfive);
		TextView s_topfive_stats = (TextView) convertView
				.findViewById(R.id.s_topfive_stats);
		TextView s_near = (TextView) convertView.findViewById(R.id.s_near);
		TextView s_near_stats = (TextView) convertView
				.findViewById(R.id.s_near_stats);

		final View shareView = convertView.findViewById(R.id.s_sharelayout);

		ImageView share = (ImageView) convertView.findViewById(R.id.s_share);
		share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				captureAndShare(shareView);

			}
		});

		Button detalles = (Button) convertView.findViewById(R.id.s_more);
		detalles.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(m_context, CloudActivity.class);
				i.putExtra("id", d.id);
				m_context.startActivity(i);

			}
		});

		s_name.setText(d.name);
		s_url.setText(d.url);
		s_topfive.setText(topFive[0]);
		s_topfive_stats.setText(topFive[1]);
		s_near.setText(near[0]);
		s_near_stats.setText(near[1]);

		return convertView;
	}

	private void captureAndShare(View v) {
		try {
			v.setDrawingCacheEnabled(true);
			Bitmap b = v.getDrawingCache();
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("image/jpeg");
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			b.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
			File f = new File(Environment.getExternalStorageDirectory()
					+ File.separator + "temporary_file.jpg");
			try {
				f.createNewFile();
				FileOutputStream fo = new FileOutputStream(f);
				fo.write(bytes.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
			share.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file:///sdcard/temporary_file.jpg"));
			m_context.startActivity(Intent.createChooser(share, "Share Image"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String[] getTopFive(Dictionary d) {
		Vector mData = new Vector();
		mData.addAll(d.getWords().entrySet());
		String topfive = "";
		String topfive_stats = "";

		for (int i = 0; i < 5; i++) {
			if (mData.size() <= i)
				break;
			Map.Entry<String, MutableFloat> e = (Map.Entry) mData.get(i);
			topfive += "- " + e.getKey();
			topfive_stats += "x" + ((int) e.getValue().get());

			if (i < 4) {
				topfive += "\n";
				topfive_stats += "\n";
			}
		}
		return new String[] { topfive, topfive_stats };
	}

	private String[] getNear(Dictionary d) {
		String near = "";
		String near_stats = "";

		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMinimumFractionDigits(2);
		formatter.setMaximumFractionDigits(2);

		Map<Long, Float> distances = MapUtil.sortByValue(d.distances, true);
		Vector mData = new Vector();
		mData.addAll(distances.entrySet());
		for (int i = 0; i < mData.size(); i++) {
			Map.Entry<Long, Float> e = (Map.Entry) mData.get(i);

			near += getDictionaryGame(e.getKey());
			near_stats += formatter.format(e.getValue());
			if (i < mData.size() - 1) {
				near += "\n";
				near_stats += "\n";
			}
		}
		return new String[] { near, near_stats };

	}

	public String getDictionaryGame(Long l) {
		for (Dictionary d : this.l.dics) {
			if (d.id == l)
				return d.name;
		}
		return "";
	}

}

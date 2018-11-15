package de.streblow.matesolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.WindowManager;
import android.widget.TextView;

public class HelpDialog extends Dialog {

	private static Context mContext = null;

	public HelpDialog(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		TextView tv;
		setContentView(R.layout.activity_help);
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
		tv = (TextView)findViewById(R.id.title_text);
		tv.setText(Html.fromHtml(readRawTextFile(R.raw.title)));
		tv = (TextView)findViewById(R.id.help_text);
		tv.setText(Html.fromHtml(readRawTextFile(R.raw.help)));
		tv.setLinkTextColor(Color.rgb(0x88, 0x88, 0xcc));
		//Linkify.addLinks(tv, Linkify.EMAIL_ADDRESSES + Linkify.WEB_URLS);
	}

	public static String readRawTextFile(int id) {
		InputStream inputStream = mContext.getResources().openRawResource(id);
		InputStreamReader in = new InputStreamReader(inputStream);
		BufferedReader buf = new BufferedReader(in);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while (( line = buf.readLine()) != null) text.append(line);
		}
		catch (IOException e) {
			return null;
		}
		return text.toString();
	}
}

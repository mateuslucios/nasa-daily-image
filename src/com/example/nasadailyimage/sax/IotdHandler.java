package com.example.nasadailyimage.sax;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class IotdHandler extends DefaultHandler {

	private static final String TAG = "IotdHandler";
	
	private String url = "http://www.nasa.gov/rss/image_of_the_day.rss";

	private boolean inTitle;
	private boolean inDescription;
	private boolean inItem;
	private boolean inDate;

	private Bitmap image;

	private String title;
	private String description;
	private String date;

	private boolean inEnclosure;
	
	public void processFeedAndDispatch(Handler activityHandler) {

		processFeedAndCache();

		Message message = new Message();
		Bundle bundle = new Bundle(4);
		bundle.putString("title", title);
		bundle.putString("description", description);
		bundle.putString("date", date);
		bundle.putParcelable("image", image);
		message.setData(bundle);
		activityHandler.sendMessage(message);
	}

	public void processFeedAndCache() {

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler(this);
			InputStream is = new URL(url).openStream();
			reader.parse(new InputSource(is));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Bitmap getBitmap(String url) {
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		InputStream inputStream = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoInput(true);
			conn.connect();
			inputStream = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(inputStream);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if(localName.startsWith("item")){
			inItem = true;
		} else if(inItem) {
			inTitle = "title".equals(localName);
			inDescription = "description".equals(localName);
			inDate = "pubDate".equals(localName);
			inEnclosure = "enclosure".equals(localName);
			if (inEnclosure && image == null){
				Log.d(TAG, "image-url: " + attributes.getValue("url"));
				image = getBitmap(attributes.getValue("url"));
			}
		}
		
		
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		String chars = new String(ch).substring(start, start + length);

		if (inTitle && title == null) {
			title = chars;
			Log.d(TAG, "got title: " + title);
		}
		if (inDescription && description == null) {
			description = chars;
			Log.d(TAG, "got description: " + description);
		}
		if (inDate && date == null) {
			date = chars;
			Log.d(TAG, "got date: " + date);
		}
	}

	public Bitmap getImage() {
		return image;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}
}

package com.aod.clubapp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class HttpClient {
	private static final String TAG = "HttpClient";
	public static JSONObject SendHttpPost(String URL, Object jsonObjSend) throws IOException, JSONException {
		return SendHttpPost(URL, jsonObjSend, true);
	}
	
	public static JSONObject SendHttpPost(String URL, Object jsonObjSend, boolean jsonType) throws IOException, JSONException {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPostRequest = new HttpPost(URL);

		StringEntity se;
		se = new StringEntity(jsonObjSend.toString());
		httpPostRequest.setEntity(se);
		
		// Set HTTP parameters
		if(jsonType) {
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
		} else {
			httpPostRequest.setHeader("Accept", "*/*");
			httpPostRequest.setHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
			

		}
		// only set this parameter if you would like to use gzip compression		
		httpPostRequest.setHeader("Accept-Encoding", "gzip"); 

		long t = System.currentTimeMillis();
		HttpResponse response = (HttpResponse) httpclient
				.execute(httpPostRequest);
		Log.i(TAG, "HTTPResponse received in ["
				+ (System.currentTimeMillis() - t) + "ms]");

		// Get hold of the response entity (-> the data):
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			// Read the content stream
			InputStream instream = entity.getContent();
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			// convert content stream to a String
			String resultString = convertStreamToString(instream);
			instream.close();
			// resultString = resultString.substring(1,resultString.length()-1);
			// // remove wrapping "[" and "]"
			Log.d(TAG, "resultString == " + resultString);
			// Transform the String into a JSONObject
			JSONObject jsonObjRecv = new JSONObject(resultString);
			// Raw DEBUG output of our received JSON object:
			Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString()
					+ "\n</JSONObject>");

			return jsonObjRecv;
		}

		return null;
	}

	public static String sendHttpPost(String URL, String postData) throws IOException {

		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPostRequest = new HttpPost(URL);

		StringEntity se;
		se = new StringEntity(postData);
		httpPostRequest.setEntity(se);
		

		httpPostRequest.setHeader("Accept", "*/*");
		httpPostRequest.setHeader("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
		// only set this parameter if you would like to use gzip compression		
		httpPostRequest.setHeader("Accept-Encoding", "gzip"); 

		long t = System.currentTimeMillis();
		HttpResponse response = (HttpResponse) httpclient
				.execute(httpPostRequest);
		Log.i(TAG, "HTTPResponse received in ["
				+ (System.currentTimeMillis() - t) + "ms]");

		// Get hold of the response entity (-> the data):
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			// Read the content stream
			InputStream instream = entity.getContent();
			Header contentEncoding = response
					.getFirstHeader("Content-Encoding");
			if (contentEncoding != null
					&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				instream = new GZIPInputStream(instream);
			}

			// convert content stream to a String
			String resultString = convertStreamToString(instream);
			instream.close();
			// resultString = resultString.substring(1,resultString.length()-1);
			// // remove wrapping "[" and "]"
			Log.d(TAG, "resultString == " + resultString);
			return resultString;
		}
		return "";
	}

	public static String sendHttpGet(String url) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		get.addHeader("Cache-Control", "no-cache");
		HttpResponse response;

		response = client.execute(get);

		HttpEntity entity = response.getEntity();

		InputStream in = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		in.close();
		return sb.toString();
	}

	public static String SendHttpDelete(String url) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpDelete delReq = new HttpDelete(url);
		HttpResponse response;

		response = client.execute(delReq);

		HttpEntity entity = response.getEntity();
		if(entity != null) {
			InputStream in = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			return sb.toString();
		}
		return "";
	}

	public static String sendSimpleHttpPut(String url, String content, String contentType) throws IOException {
		
		DefaultHttpClient client = new DefaultHttpClient();
	    HttpPut post = new HttpPut(url);
	    StringEntity se = new StringEntity(content);  
	    //se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
	    post.setHeader("Accept", "*/*");
	    post.setHeader("Accept-Encoding", "gzip, deflate");
	    post.setHeader("User-Agent", "Clubs/1.1 (iPhone; iOS 7.0.3; Scale/2.00)");
	    
	    post.setHeader("Content-type", contentType);
	    
	    post.setEntity(se);
	    client.execute(post);				
		return "";
	}
	
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 * 
		 * (c) public domain:
		 * http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/
		 * 11/a-simple-restful-client-at-android/
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

}
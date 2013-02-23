// Loup NetworkManager;

package helloworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkManager {

	private static NetworkManager NetworkManager;

	public static NetworkManager getInstance(){
		if(NetworkManager == null){
			synchronized(NetworkManager.class) {
				if (NetworkManager == null) {
					NetworkManager = new NetworkManager();
				}
			}
		}
		return NetworkManager;
	}

	public String POST(String addr, String post){
		StringBuffer sb = null;
		String type = "application/x-www-form-urlencoded";
		HttpURLConnection urlCon = null;
		BufferedReader br = null;
		OutputStreamWriter wr = null;
		String line = null;
		try {
			sb = new StringBuffer();
			URL url = new URL(addr);
			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setRequestProperty("Content-Type", type);
			urlCon.setRequestMethod("POST");
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.connect();
			wr = new OutputStreamWriter(urlCon.getOutputStream(),"utf8");
			wr.write(post);
			wr.flush();
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(), "utf8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(urlCon != null){
				urlCon.disconnect();
			}
			if(wr != null){
				try {
					wr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public String GET(String addr){
		StringBuffer sb = null;
		BufferedReader br = null;
		HttpURLConnection urlCon = null;
		String line = null; 
		try {
			sb = new StringBuffer();
			URL url = new URL(addr);
			urlCon = (HttpURLConnection) url.openConnection();  
			urlCon.setRequestMethod("GET");
			urlCon.setDoInput(true); 
			urlCon.connect(); 
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream(),"utf-8"));
			while ((line = br.readLine()) != null) {
				sb.append(line);	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(urlCon != null){
				urlCon.disconnect();
			}
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

}
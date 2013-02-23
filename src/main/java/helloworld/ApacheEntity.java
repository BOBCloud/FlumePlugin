package helloworld;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class ApacheEntity{

	private String ip, date, method, url, protocol;
	private int statucCode = 0;
	
	public ApacheEntity(String log)
	{
		String[] items = log.split(" ");
		this.ip = items[0];
		SimpleDateFormat inFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		try {
			this.date = outFormat.format(inFormat.parse(items[3] + " " + items[4]));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.method = items[5].replace("\"", "");
		this.url = items[6];
		this.protocol = items[7].replace("\"", "");
		this.statucCode = Integer.parseInt(items[8]);
	}
	
	private void printItems(){
		System.out.println("IP : " + this.ip);
		System.out.println("DATE : " + this.date);
		System.out.println("METHOD : " + this.method);
		System.out.println("URL : " + this.url);
		System.out.println("PROTOCOL : " + this.protocol);
		System.out.println("STATUS CODE : " + this.statucCode);
	}

	public String getIp()
	{
		return ip;
	}

	public String getDate()
	{
		return date;
	}

	public String getMethod()
	{
		return method;
	}

	public String getUrl()
	{
		return url;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public int getStatucCode()
	{
		return statucCode;
	}
}

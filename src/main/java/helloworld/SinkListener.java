package helloworld;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class SinkListener implements UpdateListener {

	private SendMail mail = new SendMail();
	private NetworkManager networkManager = NetworkManager.getInstance();
	
	
	@Override
	public void update(EventBean[] newBeans, EventBean[] oldBeans) {
		// TODO Auto-generated method stub

		String message = "";
		int num = 0;

		Enumeration<NetworkInterface> nienum = null;
		try {
			nienum = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			e.printStackTrace();
		}

		while (nienum.hasMoreElements()) {
			NetworkInterface ni = nienum.nextElement();
			Enumeration<InetAddress> kk = ni.getInetAddresses();
			message = message + "Network Interface Card" + num + "\n";
			while (kk.hasMoreElements()) {
				InetAddress inetAddress = (InetAddress) kk.nextElement();
				message = message + "Name : " + inetAddress.getHostName()
						+ "\n" + "Address : " + inetAddress.getHostAddress()
						+ "\n";
			}
			// message = message + "\n";
			num++;
		}

		Calendar calendar = Calendar.getInstance();
		java.util.Date date = calendar.getTime();
		String today = (new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss")
				.format(date));

		for (EventBean bean : newBeans) {
			if (bean != null) {
				// System.out.println("Apache Entity : " + ((Map<String,
				// Double>) bean.getUnderlying()) );
				message = message + "\n" + "[" + today + "]   "
						+ bean.getUnderlying();
				
				StringBuffer sb = new StringBuffer();
				sb.append("type=").append("update").append("&");
				String line = bean.getUnderlying().toString().replace("{","").replace("}", "");
				String[] items;
				items = line.split(",");
				for(int i=0; i<items.length; i++){
					String item = items[i];
					String[] data = item.split("=");
					String key = data[0].replace(" ", "");
					String value = "";
					if(data.length > 2){
						for(int k=1; k<data.length; k++){
							value += data[k];
						}
					}else{
						value = data[1];
					}
					sb.append(key).append("=").append(value);
					if(i != (items.length-1)){
						sb.append("&");
					}
				}
				System.out.println(sb.toString());
				networkManager.POST("http://localhost/upload.php", sb.toString());
			}

		}
		System.out.println("Send Mail!!!!!!!!!!!!!!!!!");
		mail.setMessage(message);
		mail.MailSend();
	}

}

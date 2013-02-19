package helloworld;

import java.util.Map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;


public class SinkListener implements UpdateListener{

	@Override
	public void update(EventBean[] newBeans, EventBean[] oldBeans) {
		// TODO Auto-generated method stub
		EventBean bean = newBeans[0];
		
		System.out.println("Apache Entity : " + ((Map<String, Double>)bean.getUnderlying()));
		
		// Alert Handling
		
	}

}

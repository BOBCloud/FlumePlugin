/**
 * Licensed to Cloudera, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Cloudera, Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package helloworld;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configured;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloudera.flume.conf.Context;
import com.cloudera.flume.conf.SinkFactory.SinkBuilder;
import com.cloudera.flume.core.Event;
import com.cloudera.flume.core.EventSink;
import com.cloudera.util.Pair;
import com.google.common.base.Preconditions;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;



/**
 * Simple Sink that writes to a "helloworld.txt" file.
 */
public class HelloWorldSink extends EventSink.Base {
  static final Logger LOG = LoggerFactory.getLogger(HelloWorldSink.class);
  private PrintWriter pw;
  private EPServiceProvider epService ;
  private String queryExpression= "";
  private EPStatement statement = null;
  private ApacheEntity entity;
  private SinkListener listener;
  private Configuration conf;
  private final String FILE_NAME = "rule.json";
  private NetworkManager networkManager;
  
  @Override
  public void open() throws IOException {
    // Initialized the sink
    pw = new PrintWriter(new FileWriter("helloworld.txt"));
    networkManager = NetworkManager.getInstance();
    conf = new Configuration();
    conf.addEventType("ApacheEntity",ApacheEntity.class.getName());
    conf.addImport("helloworld.ApacheEntity");
    epService = EPServiceProviderManager.getDefaultProvider(conf);
    
    queryExpression = getQuery();
    statement = epService.getEPAdministrator().createEPL(queryExpression);
    listener = new SinkListener();
    statement.addListener(listener);
    
  }

  private String getQuery(){
		String queryBase = "select ip,date,method,url,protocol from ApacheEntity.win:time_batch(10 sec) where";
		StringBuilder query = new StringBuilder();
		try {
			JSONObject roles = new JSONObject(getRoleFileData());
			Iterator<?> keys = roles.keys();
			while(keys.hasNext()){
				String key = keys.next().toString();
				JSONArray role = roles.getJSONArray(key);
				for(int i = 0; i < role.length(); i++){
					query.append(" (" + key + " like '%" + role.getString(i) + "%') ");
					if((role.length()-1) != i){
						query.append("or");
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		queryBase  = queryBase + query.toString();
		return queryBase;
	}

	private String getRoleFileData() {
		String data = null;
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(FILE_NAME);
			br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
			data = fileReader(br);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return data;
	}

	private String fileReader(BufferedReader br) {
		StringBuffer resultData = new StringBuffer();
		try {
			String data = null;
			while ((data = br.readLine()) != null) {
				resultData.append(data);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultData.toString();
	}
	
  @Override
  public void append(Event e) throws IOException {
    // append the event to the output
	  
    entity = new ApacheEntity(new String(e.getBody()));
    
    StringBuffer sb = new StringBuffer();
    
    sb.append("type=").append("append").append("&");
    sb.append("url=").append(entity.getUrl()).append("&");
    sb.append("method=").append(entity.getMethod()).append("&");
    sb.append("status_code=").append(entity.getStatucCode()).append("&");
    sb.append("protocol=").append(entity.getProtocol()).append("&");
    sb.append("date=").append(entity.getDate()).append("&");
    sb.append("ip=").append(entity.getIp()); 
    networkManager.POST("http://localhost/upload.php", sb.toString());
	epService.getEPRuntime().sendEvent(entity);
	  
    // here we are assuming the body is a string
    pw.println(new String(e.getBody()));
    pw.flush(); // so we can see it in the file right away
  }

  @Override
  public void close() throws IOException {
    // Cleanup
    pw.flush();
    pw.close();
  }

  public static SinkBuilder builder() {
    return new SinkBuilder() {
      // construct a new parameterized sink
      @Override
      public EventSink build(Context context, String... argv) {
        Preconditions.checkArgument(argv.length == 0,
            "usage: helloWorldSink");

        return new HelloWorldSink();
      }
    };
  }

  /**
   * This is a special function used by the SourceFactory to pull in this class
   * as a plugin sink.
   */
  public static List<Pair<String, SinkBuilder>> getSinkBuilders() {
    List<Pair<String, SinkBuilder>> builders =
      new ArrayList<Pair<String, SinkBuilder>>();
    builders.add(new Pair<String, SinkBuilder>("helloWorldSink", builder()));
    return builders;
  }
}

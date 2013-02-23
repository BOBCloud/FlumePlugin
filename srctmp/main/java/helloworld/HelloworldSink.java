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
import java.util.Properties;

import org.apache.hadoop.conf.Configured;
import org.codehaus.jettison.json.JSONArray;
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
public class HelloworldSink extends EventSink.Base {
	static final Logger LOG = LoggerFactory.getLogger(HelloworldSink.class);
	private PrintWriter pw;
	private EPServiceProvider epService;
	private String queryExpression = "";
	private EPStatement statement = null;
	private ApacheEntity entity;
	private SinkListener listener;
	private Configuration conf;
	private final String FILE_NAME = "conf/role.json";

	@Override
	public void open() throws IOException {
		// Initialized the sink
		pw = new PrintWriter(new FileWriter("helloworld.txt"));

		conf = new Configuration();
		conf.addEventType("ApacheEntity", ApacheEntity.class.getName());
		conf.addImport("DetectLog.ApacheEntity");
		epService = EPServiceProviderManager.getDefaultProvider(conf);

		queryExpression = "select ip,date,method,url,protocol from ApacheEntity.win:time_batch(10 sec) "
				+ "where (url = \"/cgi-bin/shop.cgi?page=../../../../../../../etc/passwd\") "
				+ "or (url =\"/bin/admin.pl\") "
				+ "or (url =\"/mobileadmin/bin/\") "
				+ "or (url =\"/htdocs/../../../../../../../../../../../etc/passwd\") ";

		statement = epService.getEPAdministrator().createEPL(queryExpression);
		listener = new SinkListener();
		statement.addListener(listener);

	}

	@Override
	public void append(Event e) throws IOException {
		// append the event to the output

		entity = new ApacheEntity(new String(e.getBody()));

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
						"usage: AbnormalLogDetect");

				return new HelloworldSink();
			}
		};
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
  /**
   * This is a special function used by the SourceFactory to pull in this class
   * as a plugin sink.
   */
  public static List<Pair<String, SinkBuilder>> getSinkBuilders() {
    List<Pair<String, SinkBuilder>> builders =
      new ArrayList<Pair<String, SinkBuilder>>();
    builders.add(new Pair<String, SinkBuilder>("HelloworldSink", builder()));
    return builders;
  }
  
  
	private final String FILENAME = "rule.properties";
	private Properties properties = null;

	public void PropertiesManager() {
		FileInputStream propFile = null;
		try {
			propFile = new FileInputStream(FILENAME);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		properties = new Properties(System.getProperties());
		try {
			properties.load(propFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.setProperties(properties);
	}

	public String getProperty(String key) {
		return System.getProperties().getProperty(key);
	}
}



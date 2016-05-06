package com.witskies.manager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
/**
 * 
 * @author ZZ
 */
@SuppressWarnings("deprecation")
public class HttpClient {
	
	public static enum HttpMethod {GET, POST}
	
	private AtomicLong id = new  AtomicLong(0);

	public static final int WHAT_SUCCESS = 0x11;
	public static final int WHAT_FAIL = 0x12;
	public static final int WHAT_FAIL_TIMEOUT = 0x15;
	public static final int WHAT_CANCEL = 0x13;
	private static final int WHAT_FIXED = 0x14;
	private static final int REQUEST_TIMEOUT = 20*1000;//设置请求超时20秒钟
	private static final int SO_TIMEOUT = 20*1000;  //设置等待数据超时时间20秒钟
	BasicHttpParams httpParams = new BasicHttpParams();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private LRULinkedHashMap<Long, _Data> map = new LRULinkedHashMap(Runtime.getRuntime().availableProcessors()*20);
	
	private ExecutorService executorService;	//线程池
	
	private final String Tag = HttpClient.class.getSimpleName();
	
	private HttpClient(){
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*20);
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
	}
	
	public static HttpClient getInstance(){
		return Holder.client;
	}
	
	public long getId(){
		return id.getAndIncrement();
	}

	static class Holder{
		static HttpClient client = new HttpClient();
	}
	
	class _Data{
		public _Data(String url,int status){
			this.url = url;
			this.status = status;
		}
		public String url;
		public int status;
	}
	
	/**
	 * 根据资源id取消请求
	 * @param id
	 */
	public void recallRequest(long id){
		if(map.containsKey(id)){
			map.get(id).status = WHAT_CANCEL;
		}
	}
	
	/**
	 * 根据请求地址 取消请求
	 * @param id
	 */
	public void recallRequest(String url){
		Iterator<Entry<Long,_Data>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Long,_Data> entry = iter.next();
			if(entry.getValue().url.equals(url)){
				entry.getValue().status = WHAT_CANCEL;
			}
		}
	}
	
	/**
	 * 取消所有请求
	 */
	public void recallAllRequest(){
		Iterator<Entry<Long,_Data>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Long,_Data> entry = iter.next();
			if(entry.getValue().status != WHAT_FIXED ){
				entry.getValue().status = WHAT_CANCEL;
			}
		}
	}
	
	
	public long sendAsynRequest(String url,Handler handler){
		long id = getId();
		executorService.execute(new RequestRunnable(url,handler,id));
		return id;
	}
	
	public void setNoCancel(long id){
		if(map.containsKey(id)){
			map.get(id).status = WHAT_FIXED;
		}
	}
	
	public long sendAsynRequest(String url,Handler handler,boolean flag ){
		long id = getId();
		executorService.execute(new RequestRunnable(url,handler,flag,id));
		return id;
	}
	
	public long sendAsynRequest(String url,HttpMethod method,Map<String, Object> params,Handler handler){
		long id = getId();
		executorService.execute(new RequestRunnable(url,method, params,handler,id));
		return id;
	}
	
	public long sendAsynRequest(String url,Map<String, Object> params,Handler handler){
		long id = getId();
		executorService.execute(new RequestRunnable(url,params,handler,id));
		return id;
	}
	
	public long sendAsynRequest(String url,int what,Map<String, Object> params,Handler handler){
		long id = getId();
		executorService.execute(new RequestRunnable(url,params,what,handler,id));
		return id;
	}
	
	public void addHeader(String name,String value){
		
	}
	
	class RequestRunnable implements Runnable{

		Handler handler;
		String url;
		Map<String, Object> params ;
		int what = 0;
		HttpMethod method = HttpMethod.GET;
		
		int arg;
		
		boolean flag = false;
		
		long id = 0;
		
		void init(){
			map.put(id, new _Data(this.url,0));
		}
		
		RequestRunnable(String url,Handler handler,long id){
			this.handler = handler;
			this.url = url;
			this.id = id;
			this.init();
		}
		
		RequestRunnable(String url,Handler handler,boolean flag,long id){
			this.handler = handler;
			this.url = url;
			this.flag = flag;
			this.id = id;
			this.init();
		}
		
		RequestRunnable(String url,HttpMethod method,Map<String, Object> params,Handler handler,long id){
			this.handler = handler;
			this.url = url;
			this.method = method;
			this.params = params;
			this.id = id;
			this.init();
		}
		
		RequestRunnable(String url,Map<String, Object> params,Handler handler,long id){
			this.handler = handler;
			this.url = url;
			this.params = params;
			this.id = id;
		}
		
		RequestRunnable(String url,Map<String, Object> params,int what,Handler handler,long id){
			this.handler = handler;
			this.url = url;
			this.params = params;
			this.what = what;
			this.id = id;
			this.init();
		}
		
		public boolean isRecall(long id){
			_Data data = map.get(id);
			return (data != null && data.status == WHAT_CANCEL);
		}
		
		@Override
		public void run() {
			
			final DefaultHttpClient client =  new DefaultHttpClient(httpParams);
			Log.i(Tag, "" + url);
	        try {
	        	HttpResponse response = null;
	        	if(this.method.equals(HttpMethod.GET)){
	        		final HttpGet getRequest = new HttpGet(url);
		            response = client.execute(getRequest);
				}else{
					HttpPost post = new HttpPost(url);
					if(params!=null && !params.isEmpty()){
						HttpEntity entity = null;
						if(params.get(null) != null){
							entity = new StringEntity((String)params.get(null), HTTP.UTF_8);
							post.setHeader("Content-Type", "application/json");
						}else{
							List<NameValuePair> paramPairs = new ArrayList<NameValuePair>();  
							for(Map.Entry<String, Object> entry : params.entrySet()){  
				                BasicNameValuePair param = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());  
				                paramPairs.add(param);  
				            }
							entity = new UrlEncodedFormEntity(paramPairs, HTTP.UTF_8);
						}
						post.setEntity(entity); 
					}
					response = client.execute(post);
				}
	        	
	            final int statusCode = response.getStatusLine().getStatusCode();
	            if (statusCode != HttpStatus.SC_OK) {
	                handler.sendEmptyMessage(WHAT_FAIL);
	            }

	            final HttpEntity entity = response.getEntity();
	            
	            if (entity != null) {
	                InputStream inputStream = null;
	                try {
	                	
	                	if(flag){
	                		if(handler != null){
								Message msg = handler.obtainMessage(isRecall(this.id)?WHAT_CANCEL:WHAT_SUCCESS, entity.getContent());
								handler.sendMessage(msg);
							}
	                	}else{
	                		BufferedReader br = new BufferedReader(
		                            new InputStreamReader(entity.getContent()));  
		                    StringBuffer sb = new StringBuffer();  
		                    String result = br.readLine();  
		                    while (result != null) {  
		                        sb.append(result);  
		                        result = br.readLine();  
		                    }  
							if(handler != null){
								Message msg = handler.obtainMessage(isRecall(this.id)?WHAT_CANCEL:WHAT_SUCCESS, sb.toString());
								handler.sendMessage(msg);
							}
	                	}
	                } finally {
	                    if (inputStream != null) {
	                        inputStream.close();
	                    }
	                    entity.consumeContent();
	                }
	            }
	        }catch (ClientProtocolException e1) {
	        	handler.sendEmptyMessage(WHAT_FAIL_TIMEOUT);
			} catch (IOException e) {
	            //getRequest.abort();
//	            Log.w(TAG, "I/O error while retrieving bitmap from " + url, e);
	            handler.sendEmptyMessage(WHAT_FAIL);
	        } catch (IllegalStateException e) {
	            //getRequest.abort();
//	            Log.w(TAG, "Incorrect URL: " + url);
	            handler.sendEmptyMessage(WHAT_FAIL);
	        }catch (Exception e) {
	            //getRequest.abort();
//	            Log.w(TAG, "Error while retrieving bitmap from " + url, e);
	            handler.sendEmptyMessage(WHAT_FAIL);
	        } finally {
	        	
	        }
		}
		
	}
	
}

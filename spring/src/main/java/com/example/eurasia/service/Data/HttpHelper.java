/**
 * 版权申明: 苏州朗动科技有限公司<br>
 * 项目描述: 企查查-接口平台<br>
 * 该接口调用demo仅供学习参考
 */
package com.example.eurasia.service.Data;

import com.example.eurasia.service.Util.Slf4jLogUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * 
 * 描述 http请求处理模块<br>
 * 
 * @author szld<br>
 * @version 1.0 <br>
 *          日期：2019年7月31日 下午2:23:41
 */
public class HttpHelper {

	/**
	 * get 请求
	 *
	 * @param
	 */
	public static String httpGet(String url, Header[] headers) throws Exception {
		HttpUriRequest uriRequest = new HttpGet(url);
		if (null != headers) {
			uriRequest.setHeaders(headers);
		}
		CloseableHttpClient httpClient = null;
		try {
			httpClient = declareHttpClientSSL(url);
			CloseableHttpResponse httpResponse = httpClient.execute(uriRequest);
			HttpEntity httpEntity = httpResponse.getEntity();
			String result = EntityUtils.toString(httpEntity, REQ_ENCODING_UTF8);
			return result;
		} catch (ClientProtocolException e) {
			Slf4jLogUtil.get().error(String.format("http请求失败，uri{%s},exception{%s}", new Object[] { url, e }));
		} catch (IOException e) {
			Slf4jLogUtil.get().error(String.format("IO Exception，uri{%s},exception{%s}", new Object[] { url, e }));
		} finally {
			if (null != httpClient) {
				httpClient.close();
			}
		}
		return null;
	}

	/**
	 * post 请求
	 *
	 * @param
	 */
	public static String httpPost(String url, String params) throws Exception {
		HttpPost post = new HttpPost(url);
		post.addHeader("Content-Type", "application/json;charset=" + REQ_ENCODING_UTF8);
		// 设置传输编码格式
		StringEntity stringEntity = new StringEntity(params, REQ_ENCODING_UTF8);
		stringEntity.setContentEncoding(REQ_ENCODING_UTF8);
		post.setEntity(stringEntity);
		HttpResponse httpresponse = null;
		CloseableHttpClient httpClient = null;
		try {
			httpClient = declareHttpClientSSL(url);
			httpresponse = httpClient.execute(post);
			HttpEntity httpEntity = httpresponse.getEntity();
			String result = EntityUtils.toString(httpEntity, REQ_ENCODING_UTF8);
			return result;
		} catch (ClientProtocolException e) {
			Slf4jLogUtil.get().error(String.format("http请求失败，uri{%s},exception{%s}", new Object[] { url, e }));
		} catch (IOException e) {
			Slf4jLogUtil.get().error(String.format("IO Exception，uri{%s},exception{%s}", new Object[] { url, e }));
		} finally {
			if (null != httpClient) {
				httpClient.close();
			}
		}
		return null;
	}

	/**
	 *
	 *
	 * @param
	 */
	private static CloseableHttpClient declareHttpClientSSL(String url) {
		if (url.startsWith("https://")) {
			return sslClient();
		} else {
			return HttpClientBuilder.create().setConnectionManager(httpClientConnectionManager).build();
		}
	}

	/**
	 * 设置SSL请求处理
	 * 
	 * @param
	 */
	private static CloseableHttpClient sslClient() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] xcs, String str) {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] xcs, String str) {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLConnectionSocketFactory sslConnectionSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
			return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}

	private static final String REQ_ENCODING_UTF8 = "utf-8";
	private static PoolingHttpClientConnectionManager httpClientConnectionManager;

	public HttpHelper() {
		httpClientConnectionManager = new PoolingHttpClientConnectionManager();
		httpClientConnectionManager.setMaxTotal(100);
		httpClientConnectionManager.setDefaultMaxPerRoute(20);
	}

	/**
	 * get 请求
	 *
	 * @param
	 */
	public static String httpGet(String url) throws Exception {
		return httpGet(url, null);
	}
}

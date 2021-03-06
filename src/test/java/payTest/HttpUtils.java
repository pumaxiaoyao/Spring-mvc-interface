package payTest;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpUtils {
    
    public static String post(String url, String parmas) {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            
            HttpPost httppost = new HttpPost(url);
            // 设置包体参数。 
            StringEntity se = new StringEntity(parmas);
            se.setContentEncoding("UTF-8");
            se.setContentType("application/x-www-form-urlencoded");
            httppost.setEntity(se);
            CloseableHttpResponse response = httpclient.execute(httppost);
            
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                
                response.close();
                return sb.toString();
                
            }else{
                return null;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(httpclient != null){
                try {
                    httpclient.close();
                } catch (IOException e) {}
            }
        }
    }

    public static String doPost(String url,Map<String,String> propertyValue){
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
            
            HttpPost httppost = new HttpPost(url);
            
            if(propertyValue != null){
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                for(Entry<String, String> entry : propertyValue.entrySet()){
                    NameValuePair v = new BasicNameValuePair(entry.getKey(),entry.getValue());
                    nvps.add(v);
                }
                httppost.setEntity(new UrlEncodedFormEntity(nvps,Consts.UTF_8));
                httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            }
            
            CloseableHttpResponse response = httpclient.execute(httppost);
            
            if(response.getStatusLine().getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),Consts.UTF_8));
                StringBuffer sb = new StringBuffer();
                
                String content = null;
                while((content = reader.readLine()) != null){
                    sb.append(content);
                }
                
                response.close();
                return sb.toString();
                
            }else{
                return null;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(httpclient != null){
                try {
                    httpclient.close();
                } catch (IOException e) {}
            }
        }
        
    }
    
    public static String post(String url, byte[] bytes, String contentType) throws IOException {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.custom()
                    .setConnectionManager(createConnectionManager())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new ByteArrayEntity(bytes));
        if (contentType != null)
            httpPost.setHeader("Content-type", contentType);
        CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
        try {
            HttpEntity entityResponse = httpResponse.getEntity();
            int contentLength = (int) entityResponse.getContentLength();
            if (contentLength <= 0)
                throw new IOException("No response");
            byte[] respBuffer = new byte[contentLength];
            if (entityResponse.getContent().read(respBuffer) != respBuffer.length)
                throw new IOException("Read response buffer error");
            return new String(respBuffer);
        } finally {
            httpResponse.close();
        }
    }

    
    private static PoolingHttpClientConnectionManager createConnectionManager() throws Exception {
        TrustManager tm = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

            }

        };

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { tm }, null);

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                socketFactoryRegistry);

        return connectionManager;
    }

}

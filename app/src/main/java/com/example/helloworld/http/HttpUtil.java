package com.example.helloworld.http;

import com.example.helloworld.data.UserAccount;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class HttpUtil {

    private Map<String, String> headers = new HashMap<>();

    private Map<String, String> params = new HashMap<>();

    private Object body;

    private RequestMethod requestMethod;

    private String url;

    private int connectTimeout = 3000;

    private int readTimeout = 3000;


    public static HttpUtil init() {
        return new HttpUtil();
    }

    public static HttpUtil initJson() {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.addHeader("content-type", "application/json;charset=utf8");
        return httpUtil;
    }

    public String get() {
        this.method(RequestMethod.GET);
        return invoke();
    }

    public String post() {
        this.method(RequestMethod.POST);
        return invoke();
    }

    public String invoke() {
        try {
            URL uUrl = getUrl();
            HttpURLConnection conn = (HttpURLConnection) uUrl.openConnection();
            conn.setRequestMethod(requestMethod.method);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.addRequestProperty(header.getKey(), header.getValue());
            }

            StringBuilder result = new StringBuilder();
            if (RequestMethod.POST.equals(requestMethod) && body != null) {
//                conn.setDoOutput(true);
//                conn.setDoInput(true);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
                out.write(body.toString());
                out.flush();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <T> T invokeClass(Class<T> tClass) {
        String s = this.invoke();
        Gson gson = new Gson();
        return (T) gson.fromJson(s, tClass);
    }

    private URL getUrl() throws IOException {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?");
        for (Map.Entry entry : params.entrySet()) {
            urlBuilder.append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(String.valueOf(entry.getValue()), "utf8"))
                    .append("&");
        }
        url = urlBuilder.toString();
        url = url.substring(0, url.length() - 1);
        return new URL(url);
    }

    public HttpUtil url(String url) {
        this.url = url;
        return this;
    }

    public HttpUtil body(Object body) {
        this.body = body;
        return this;
    }

    public HttpUtil connectionTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public HttpUtil readTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public HttpUtil method(RequestMethod requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public HttpUtil headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public HttpUtil params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public HttpUtil addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public HttpUtil addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpUtil addParam(String key, String value) {
        params.put(key, value);
        return this;
    }

    public HttpUtil addParams(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    public enum RequestMethod {
        GET("GET"),
        POST("POST");

        public String method;

        RequestMethod(String method) {
            this.method = method;
            System.out.println(method);
        }
    }
}
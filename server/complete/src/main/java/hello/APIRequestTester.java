/* Referenced from: https://www.baeldung.com/java-http-request */


package hello;

import java.net.*;
import java.util.*;
import java.io.*;

public class APIRequestTester {
    private String url;

    public APIRequestTester(String url){
        this.url = url;
    };

    public void run(){
        try {
            URL url = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            Map<String, String> parameters = new HashMap<>();
            parameters.put("param1", "val");

            con.setDoOutput(true);
//            DataOutputStream out = new DataOutputStream(con.getOutputStream());
//            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
//            out.flush();
//            out.close();

            con.setRequestProperty("Content-Type", "application/json");
            String contentType = con.getHeaderField("Content-Type");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);

            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            };
            System.out.println("Result:");
            System.out.println(content);
            in.close();
            con.disconnect();
        } catch (Exception e){
            e.printStackTrace();
        }

    };

    static class ParameterStringBuilder {
        public static String getParamsString(Map<String, String> params) throws UnsupportedEncodingException{
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
        };
    };


};


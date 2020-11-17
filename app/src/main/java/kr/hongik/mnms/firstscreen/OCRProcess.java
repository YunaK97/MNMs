package kr.hongik.mnms.firstscreen;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class OCRProcess {

    /*
    * https://00165bd382f14d2d9ace00a03b79dd9f.apigw.ntruss.com/custom/v1/5120/4c84d36eb30d9530dfdee50b099cb9445b9b63285ca2ffa316663760b7567649/infer
김윤아 님이 모두에게:    오후 5:14
https://apidocs.ncloud.com/ko/ai-application-service/ocr/ocr/
김윤아 님이 모두에게:    오후 5:15
X-OCR-SECRET : UVlTYlVUVFpkYndNd0dmdkxBdFhsQWhHcGdZTGVpQmk=
김윤아 님이 모두에게:    오후 5:15
https://firebasestorage.googleapis.com/v0/b/webtoonstorage.appspot.com/o/KakaoTalk_20201115_161148954.jpg?alt=media&token=09bd1369-7bd3-48cc-9748-74daeada416f
    *
    * */

    public static String main(String firebaseURL) {

        String ocrMessage = "";

        try {

            String apiURL = "https://00165bd382f14d2d9ace00a03b79dd9f.apigw.ntruss.com/custom/v1/5120/4c84d36eb30d9530dfdee50b099cb9445b9b63285ca2ffa316663760b7567649/infer";
            String secretKey = "UVlTYlVUVFpkYndNd0dmdkxBdFhsQWhHcGdZTGVpQmk=";

            String objectStorageURL = firebaseURL;

            URL url = new URL(apiURL);

            String message = getReqMessage(objectStorageURL);
            System.out.println("##" + message);

            long timestamp = new Date().getTime();

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;UTF-8");
            con.setRequestProperty("X-OCR-SECRET", secretKey);

            // post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(message.getBytes("UTF-8"));
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            if (responseCode == 200) { // 정상 호출
                System.out.println(con.getResponseMessage());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                con.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    ocrMessage = decodedString;
                }
                //chatbotMessage = decodedString;
                in.close();

            } else {  // 에러 발생
                ocrMessage = con.getResponseMessage();

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println(">>>>>>>>>>" + ocrMessage);
        return ocrMessage;
    }

    public static String getReqMessage(String objectStorageURL) {

        String requestBody = "";

        try {

            long timestamp = new Date().getTime();

            JSONObject json = new JSONObject();
            json.put("version", "V2");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", Long.toString(timestamp));
            JSONObject image = new JSONObject();
            image.put("format", "jpg");
            image.put("url", objectStorageURL);

            image.put("name", "demo");
            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);

            requestBody = json.toString();

        } catch (Exception e) {
            System.out.println("## Exception : " + e);
        }

        return requestBody;

    }

}
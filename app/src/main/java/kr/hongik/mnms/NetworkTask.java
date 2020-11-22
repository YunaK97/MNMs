package kr.hongik.mnms;

import android.os.AsyncTask;
import android.util.Log;

import java.util.Map;

public class NetworkTask extends AsyncTask<Map<String, String>, Integer, String> {
    protected String url, TAG;
    private int statusCode;
    private String response;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public void setTAG(String TAG) {
        this.TAG = TAG;
    }

    public String getResponse() {
        return response;
    }


    @Override
    protected String doInBackground(Map<String, String>... maps) { // 내가 전송하고 싶은 파라미터

        // Http 요청 준비 작업
        this.statusCode=0;
        HttpClient.Builder http = new HttpClient.Builder("POST", url);

        // Parameter 를 전송한다.
        http.addAllParameters(maps[0]);

        //Http 요청 전송
        HttpClient post = http.create();
        post.request();
        // 응답 상태코드 가져오기
        int statusCode = post.getHttpStatusCode();
        this.statusCode=statusCode;
        // 응답 본문 가져오기

        return post.getBody();
    }

    @Override
    protected void onPostExecute(String response) {
        //Log.d(TAG+"~~~~~~~~~~~~~~~~", response);
        this.response=response;
    }
}
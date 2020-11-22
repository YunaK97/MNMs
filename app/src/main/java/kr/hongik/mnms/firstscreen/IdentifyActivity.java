package kr.hongik.mnms.firstscreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.hongik.mnms.ProgressDialog;
import kr.hongik.mnms.R;

public class IdentifyActivity extends AppCompatActivity {

    //layouts
    private ImageView imageView;
    private LinearLayout LLIdentifySSN;
    private EditText etIdentifySSN, etIdentifySSN2;
    private Button btnIdentifyConfirm;

    //variables
    private String mCurrentPhotoPath;
    final static int REQUEST_TAKE_PHOTO = 1;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify);

        imageView = findViewById(R.id.ivIdentifyPhoto);
        LLIdentifySSN = findViewById(R.id.LLIdentifySSN);
        btnIdentifyConfirm = findViewById(R.id.btnIdentifyConfirm);

        progressDialog = new ProgressDialog(this);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 6.0 마쉬멜로우 이상일 경우에는 권한 체크 후 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //Log.d(TAG, "권한 설정 완료");
            } else {
                //Log.d(TAG, "권한 설정 요청");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        dispatchTakePicturIntent();

        btnIdentifyConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSSN();
            }
        });
    }

    private void getSSN() {
        etIdentifySSN = findViewById(R.id.etIdentifySSN);
        etIdentifySSN2 = findViewById(R.id.etIdentifySSN2);
        String ssn1 = etIdentifySSN.getText().toString();
        String ssn2 = etIdentifySSN2.getText().toString();

        int sex=Integer.parseInt(ssn2);
        if(sex>=5){
            showToast("잘못된 입력입니다.");
            return;
        }
        int year = Integer.parseInt(ssn1.substring(0, 2));
        int month = Integer.parseInt(ssn1.substring(2, 4));
        int day = Integer.parseInt(ssn1.substring(4));
        if (year <= 0 || year >= 100 || month <= 0 || month >= 13 || day <= 0 || day >= 32) {
            showToast("잘못된 입력입니다.");
            return;
        }
        if (month == 2 || month == 4 || month == 6 || month == 9 || month == 11) {
            if (day == 31) {
                showToast("잘못된 입력입니다.");
            }
        } else if (month == 2) {
            if (day >= 28) {
                showToast("잘못된 입력입니다.");
            }
        }

        String SSN=ssn1+ssn2+"000000";

        Intent intent=new Intent();
        intent.putExtra("userSSN",SSN);
        setResult(RESULT_OK,intent);

        finish();
    }

    //권한 요청
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Log.d("카메라 권한", "onRequestPermissionsResult");
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //Log.d(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            progressDialog.show();
            File file = new File(mCurrentPhotoPath);
            Bitmap bitmap;
            if (Build.VERSION.SDK_INT >= 29) {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.fromFile(file));
                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                }
            } else {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            IdentifyActivity.PapagoNmtTask nmtTask = new PapagoNmtTask();
            Log.d("테스트", "과연");
            nmtTask.execute("https://firebasestorage.googleapis.com/v0/b/webtoonstorage.appspot.com/o/KakaoTalk_20201115_161148954.jpg?alt=media&token=09bd1369-7bd3-48cc-9748-74daeada416f", "test2");
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePicturIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "kr.hongik.mnms.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    protected void showToast(String data) {
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }

    public class PapagoNmtTask extends AsyncTask<String, String, String> {

        @Override
        public String doInBackground(String... strings) {

            return OCRProcess.main(strings[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            ReturnThreadResult(result);
        }
    }

    public void ReturnThreadResult(String result) {

        String translateText = "";

        String rlt = result;
        try {
            JSONObject jsonObject = new JSONObject(rlt);

            JSONArray jsonArray = jsonObject.getJSONArray("images");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONArray jsonArray_fields = jsonArray.getJSONObject(i).getJSONArray("fields");

                for (int j = 0; j < jsonArray_fields.length(); j++) {

                    String inferText = jsonArray_fields.getJSONObject(j).getString("inferText");
                    translateText += inferText;
                    translateText += " ";
                }
            }

            String str = translateText.substring(0, 11);
            str += "**********";

            TextView IdentifytextView = findViewById(R.id.tvIdentifySSN);
            IdentifytextView.setText(str);

            progressDialog.dismiss();
            IdentifytextView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            LLIdentifySSN.setVisibility(View.VISIBLE);
            btnIdentifyConfirm.setVisibility(View.VISIBLE);

        } catch (Exception e) {

        }
    }
}
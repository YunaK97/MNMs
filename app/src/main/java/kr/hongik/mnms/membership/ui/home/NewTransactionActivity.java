package kr.hongik.mnms.membership.ui.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kr.hongik.mnms.R;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NewTransactionActivity extends AppCompatActivity {
    String plus_money,plus_history,plus_date;

    //URLs
    String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_trans_confirm, menu) ;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.new_confirm){
            plusTransaction();
            return true;
        }
        if(item.getItemId()==R.id.new_qr){
            showToast("qr찍기 구현중!");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void plusTransaction(){
        plus_date=((EditText)findViewById(R.id.plus_date)).getText().toString();
        plus_history=((EditText)findViewById(R.id.plus_history)).getText().toString();
        plus_money=((EditText)findViewById(R.id.plus_money)).getText().toString();

        if(plus_money.isEmpty() || plus_history.isEmpty() || plus_date.isEmpty()){
            showToast("빈칸 노노");
            return;
        }else{
            //실시간으로 팀원들이 돈 사용을 허락해야함?!?
        }
    }

    protected void showToast(String data){
        Toast.makeText(this, data, Toast.LENGTH_LONG).show();
    }
}

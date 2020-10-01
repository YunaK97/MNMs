package kr.hongik.mnms.membership;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import kr.hongik.mnms.R;

public class CustomDialogDuration {
    private Context context;

    public CustomDialogDuration(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void callFunction(final TextView main_label) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dialog.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final TextView tv_customWeek,tv_customMon,tv_customYear;
        final LinearLayout LL_dialog_week,LL_dialog_month,LL_dialog_year;
        final EditText et_dialog_day;
        tv_customMon=dialog.findViewById(R.id.tv_customMon);
        tv_customWeek=dialog.findViewById(R.id.tv_customWeek);
        tv_customYear=dialog.findViewById(R.id.tv_customYear);

        LL_dialog_month=dialog.findViewById(R.id.LL_dialog_month);
        LL_dialog_week=dialog.findViewById(R.id.LL_dialog_week);
        LL_dialog_year=dialog.findViewById(R.id.LL_dialog_year);

        et_dialog_day=dialog.findViewById(R.id.et_dialog_day);

        tv_customMon.setDefaultFocusHighlightEnabled(true);
        tv_customMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LL_dialog_month.setVisibility(View.VISIBLE);
                LL_dialog_week.setVisibility(View.GONE);
                LL_dialog_year.setVisibility(View.GONE);
                et_dialog_day.setText("1");
            }
        });

        final Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.

                //main_label.setText(message.getText().toString());
                //Toast.makeText(context, "\"" +  message.getText().toString() + "\" 을 입력하였습니다.", Toast.LENGTH_SHORT).show();

                main_label.setText("매월"+" : "+et_dialog_day.getText().toString());
                // 커스텀 다이얼로그를 종료한다.
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dialog.dismiss();
            }
        });
    }
}

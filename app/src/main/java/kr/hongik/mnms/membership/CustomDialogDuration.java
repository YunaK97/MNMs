package kr.hongik.mnms.membership;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import kr.hongik.mnms.R;

public class CustomDialogDuration implements View.OnClickListener {
    private Context context;
    private TextView tv_custom_mon,tv_custom_tue,tv_custom_wed,tv_custom_thu,tv_custom_fri,tv_custom_sat,tv_custom_sun;
    private int payType=1,type1=1;
    public CustomDialogDuration(Context context) {
        this.context = context;
    }
    private ArrayAdapter monType,year_monType,year_dayType;
    private Spinner monSpinner,yearMonSpinner,yearDaySpinner;
    private int colorWhite=Color.WHITE,colorTextPink,colorBackPink;

    // 호출할 다이얼로그 함수를 정의한다.
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void callFunction(final TextView tvPayType, final TextView tvPayTypeNum) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dialog = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dialog.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dialog.show();

        //variables
        final TextView tv_customWeek,tv_customMon,tv_customYear;
        final LinearLayout LL_dialog_week,LL_dialog_month,LL_dialog_year;

        colorTextPink=dialog.getContext().getColor(R.color.colorTitle);
        colorBackPink=dialog.getContext().getColor(R.color.colorContent);

        monSpinner=dialog.findViewById(R.id.mon_spinner);
        monType=ArrayAdapter.createFromResource(dialog.getContext(),R.array.date_day,R.layout.support_simple_spinner_dropdown_item);
        monType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        monSpinner.setAdapter(monType);
        monSpinner.setSelection(0);

        yearMonSpinner=dialog.findViewById(R.id.year_monSpinner);
        year_monType=ArrayAdapter.createFromResource(dialog.getContext(),R.array.date_month,R.layout.support_simple_spinner_dropdown_item);
        year_monType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        yearMonSpinner.setAdapter(year_monType);
        yearMonSpinner.setSelection(0);

        yearDaySpinner=dialog.findViewById(R.id.year_daySpinner);
        year_dayType=ArrayAdapter.createFromResource(dialog.getContext(),R.array.date_day,R.layout.support_simple_spinner_dropdown_item);
        year_dayType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        yearDaySpinner.setAdapter(year_dayType);
        yearDaySpinner.setSelection(0);

        //id 찾기
        tv_customMon=dialog.findViewById(R.id.tv_customMon);
        tv_customWeek=dialog.findViewById(R.id.tv_customWeek);
        tv_customYear=dialog.findViewById(R.id.tv_customYear);

        tv_custom_mon=dialog.findViewById(R.id.tv_custom_mon);
        tv_custom_mon.setOnClickListener(this);
        tv_custom_tue=dialog.findViewById(R.id.tv_custom_tue);
        tv_custom_tue.setOnClickListener(this);
        tv_custom_wed=dialog.findViewById(R.id.tv_custom_wed);
        tv_custom_wed.setOnClickListener(this);
        tv_custom_thu=dialog.findViewById(R.id.tv_custom_thu);
        tv_custom_thu.setOnClickListener(this);
        tv_custom_fri=dialog.findViewById(R.id.tv_custom_fri);
        tv_custom_fri.setOnClickListener(this);
        tv_custom_sat=dialog.findViewById(R.id.tv_custom_sat);
        tv_custom_sat.setOnClickListener(this);
        tv_custom_sun=dialog.findViewById(R.id.tv_custom_sun);
        tv_custom_sun.setOnClickListener(this);

        LL_dialog_month=dialog.findViewById(R.id.LL_dialog_month);
        LL_dialog_week=dialog.findViewById(R.id.LL_dialog_week);
        LL_dialog_year=dialog.findViewById(R.id.LL_dialog_year);

        //tv_customMon.setDefaultFocusHighlightEnabled(true);
        tv_customMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //매달 낼 날짜 하루 지정 1~12
                tv_customMon.setTextColor(colorWhite);
                tv_customMon.setBackgroundColor(colorBackPink);
                payType=2;

                tv_customWeek.setTextColor(colorTextPink);
                tv_customWeek.setBackgroundColor(colorWhite);
                tv_customYear.setTextColor(colorTextPink);
                tv_customYear.setBackgroundColor(colorWhite);

                LL_dialog_month.setVisibility(View.VISIBLE);
                LL_dialog_week.setVisibility(View.GONE);
                LL_dialog_year.setVisibility(View.GONE);
            }
        });

        //tv_customWeek.setDefaultFocusHighlightEnabled(true);
        tv_customWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_customWeek.setTextColor(colorWhite);
                tv_customWeek.setBackgroundColor(colorBackPink);
                payType=1;

                tv_customMon.setTextColor(colorTextPink);
                tv_customMon.setBackgroundColor(colorWhite);
                tv_customYear.setTextColor(colorTextPink);
                tv_customYear.setBackgroundColor(colorWhite);

                LL_dialog_month.setVisibility(View.GONE);
                LL_dialog_week.setVisibility(View.VISIBLE);
                LL_dialog_year.setVisibility(View.GONE);
            }
        });

        //tv_customYear.setDefaultFocusHighlightEnabled(true);
        tv_customYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_customYear.setTextColor(colorWhite);
                tv_customYear.setBackgroundColor(colorBackPink);
                payType=3;

                tv_customWeek.setTextColor(colorTextPink);
                tv_customWeek.setBackgroundColor(colorWhite);
                tv_customMon.setTextColor(colorTextPink);
                tv_customMon.setBackgroundColor(colorWhite);

                LL_dialog_month.setVisibility(View.GONE);
                LL_dialog_week.setVisibility(View.GONE);
                LL_dialog_year.setVisibility(View.VISIBLE);

            }
        });

        final Button btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (payType){
                    case 1:
                        //주 - 일~월 선택
                        tvPayType.setText("매주");
                        switch (type1){
                            case 1:
                                tvPayTypeNum.setText("일");
                                break;
                            case 2:
                                tvPayTypeNum.setText("월");
                                break;
                            case 3:
                                tvPayTypeNum.setText("화");
                                break;
                            case 4:
                                tvPayTypeNum.setText("수");
                                break;
                            case 5:
                                tvPayTypeNum.setText("목");
                                break;
                            case 6:
                                tvPayTypeNum.setText("금");
                                break;
                            case 7:
                                tvPayTypeNum.setText("토");
                                break;
                        }
                        break;
                    case 2:
                        //월 - 01~12선택
                        tvPayType.setText("매월");
                        tvPayTypeNum.setText(monSpinner.getSelectedItem().toString());
                        break;
                    case 3:
                        //년 - 월,일 선택
                        tvPayType.setText("매년");
                        String day=yearMonSpinner.getSelectedItem().toString()+"-"+yearDaySpinner.getSelectedItem().toString();
                        tvPayTypeNum.setText(day);
                        break;
                }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_custom_mon:
                tv_custom_mon.setTextColor(colorWhite);
                tv_custom_mon.setBackgroundColor(colorBackPink);
                type1=2;

                tv_custom_tue.setBackgroundColor(colorWhite);
                tv_custom_tue.setTextColor(colorTextPink);
                tv_custom_wed.setBackgroundColor(colorWhite);
                tv_custom_wed.setTextColor(colorTextPink);
                tv_custom_thu.setBackgroundColor(colorWhite);
                tv_custom_thu.setTextColor(colorTextPink);
                tv_custom_fri.setBackgroundColor(colorWhite);
                tv_custom_fri.setTextColor(colorTextPink);
                tv_custom_sat.setBackgroundColor(colorWhite);
                tv_custom_sat.setTextColor(colorTextPink);
                tv_custom_sun.setBackgroundColor(colorWhite);
                tv_custom_sun.setTextColor(colorTextPink);
                break;
            case R.id.tv_custom_tue:
                tv_custom_tue.setTextColor(colorWhite);
                tv_custom_tue.setBackgroundColor(colorBackPink);
                type1=3;

                tv_custom_mon.setBackgroundColor(colorWhite);
                tv_custom_mon.setTextColor(colorTextPink);
                tv_custom_wed.setBackgroundColor(colorWhite);
                tv_custom_wed.setTextColor(colorTextPink);
                tv_custom_thu.setBackgroundColor(colorWhite);
                tv_custom_thu.setTextColor(colorTextPink);
                tv_custom_fri.setBackgroundColor(colorWhite);
                tv_custom_fri.setTextColor(colorTextPink);
                tv_custom_sat.setBackgroundColor(colorWhite);
                tv_custom_sat.setTextColor(colorTextPink);
                tv_custom_sun.setBackgroundColor(colorWhite);
                tv_custom_sun.setTextColor(colorTextPink);

                break;
            case R.id.tv_custom_wed:
                tv_custom_wed.setTextColor(colorWhite);
                tv_custom_wed.setBackgroundColor(colorBackPink);
                type1=4;

                tv_custom_mon.setBackgroundColor(colorWhite);
                tv_custom_mon.setTextColor(colorTextPink);
                tv_custom_tue.setBackgroundColor(colorWhite);
                tv_custom_tue.setTextColor(colorTextPink);
                tv_custom_thu.setBackgroundColor(colorWhite);
                tv_custom_thu.setTextColor(colorTextPink);
                tv_custom_fri.setBackgroundColor(colorWhite);
                tv_custom_fri.setTextColor(colorTextPink);
                tv_custom_sat.setBackgroundColor(colorWhite);
                tv_custom_sat.setTextColor(colorTextPink);
                tv_custom_sun.setBackgroundColor(colorWhite);
                tv_custom_sun.setTextColor(colorTextPink);

                break;
            case R.id.tv_custom_thu:
                tv_custom_thu.setTextColor(colorWhite);
                tv_custom_thu.setBackgroundColor(colorBackPink);
                type1=5;

                tv_custom_mon.setBackgroundColor(colorWhite);
                tv_custom_mon.setTextColor(colorTextPink);
                tv_custom_wed.setBackgroundColor(colorWhite);
                tv_custom_wed.setTextColor(colorTextPink);
                tv_custom_tue.setBackgroundColor(colorWhite);
                tv_custom_tue.setTextColor(colorTextPink);
                tv_custom_fri.setBackgroundColor(colorWhite);
                tv_custom_fri.setTextColor(colorTextPink);
                tv_custom_sat.setBackgroundColor(colorWhite);
                tv_custom_sat.setTextColor(colorTextPink);
                tv_custom_sun.setBackgroundColor(colorWhite);
                tv_custom_sun.setTextColor(colorTextPink);

                break;
            case R.id.tv_custom_fri:
                tv_custom_fri.setTextColor(colorWhite);
                tv_custom_fri.setBackgroundColor(colorBackPink);
                type1=6;

                tv_custom_mon.setBackgroundColor(colorWhite);
                tv_custom_mon.setTextColor(colorTextPink);
                tv_custom_wed.setBackgroundColor(colorWhite);
                tv_custom_wed.setTextColor(colorTextPink);
                tv_custom_thu.setBackgroundColor(colorWhite);
                tv_custom_thu.setTextColor(colorTextPink);
                tv_custom_tue.setBackgroundColor(colorWhite);
                tv_custom_tue.setTextColor(colorTextPink);
                tv_custom_sat.setBackgroundColor(colorWhite);
                tv_custom_sat.setTextColor(colorTextPink);
                tv_custom_sun.setBackgroundColor(colorWhite);
                tv_custom_sun.setTextColor(colorTextPink);

                break;
            case R.id.tv_custom_sat:
                tv_custom_sat.setTextColor(colorWhite);
                tv_custom_sat.setBackgroundColor(colorBackPink);
                type1=7;

                tv_custom_mon.setBackgroundColor(colorWhite);
                tv_custom_mon.setTextColor(colorTextPink);
                tv_custom_wed.setBackgroundColor(colorWhite);
                tv_custom_wed.setTextColor(colorTextPink);
                tv_custom_thu.setBackgroundColor(colorWhite);
                tv_custom_thu.setTextColor(colorTextPink);
                tv_custom_fri.setBackgroundColor(colorWhite);
                tv_custom_fri.setTextColor(colorTextPink);
                tv_custom_tue.setBackgroundColor(colorWhite);
                tv_custom_tue.setTextColor(colorTextPink);
                tv_custom_sun.setBackgroundColor(colorWhite);
                tv_custom_sun.setTextColor(colorTextPink);

                break;
            case R.id.tv_custom_sun:
                tv_custom_sun.setTextColor(colorWhite);
                tv_custom_sun.setBackgroundColor(colorBackPink);
                type1=1;

                tv_custom_mon.setBackgroundColor(colorWhite);
                tv_custom_mon.setTextColor(colorTextPink);
                tv_custom_wed.setBackgroundColor(colorWhite);
                tv_custom_wed.setTextColor(colorTextPink);
                tv_custom_thu.setBackgroundColor(colorWhite);
                tv_custom_thu.setTextColor(colorTextPink);
                tv_custom_fri.setBackgroundColor(colorWhite);
                tv_custom_fri.setTextColor(colorTextPink);
                tv_custom_sat.setBackgroundColor(colorWhite);
                tv_custom_sat.setTextColor(colorTextPink);
                tv_custom_tue.setBackgroundColor(colorWhite);
                tv_custom_tue.setTextColor(colorTextPink);

                break;
            default:

                break;
        }
    }
}

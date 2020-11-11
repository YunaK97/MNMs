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
    private TextView tvCustomMon,tvCustomTue,tvCustomWed,tvCustomThu,tvCustomFri,tvCustomSat,tvCustomSun;
    private int payType=1,type1=1;
    public CustomDialogDuration(Context context) {
        this.context = context;
    }
    private ArrayAdapter monType,year_monType,year_dayType;
    private Spinner spinnerMon,spinnerYearMonth,spinnerYearDay;
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
        final TextView tvCustomWeek,tvCustomMonth,tvCustomYear;
        final LinearLayout LL_dialog_week,LL_dialog_month,LL_dialog_year;

        colorTextPink=dialog.getContext().getColor(R.color.colorTitle);
        colorBackPink=dialog.getContext().getColor(R.color.colorContent);

        spinnerMon=dialog.findViewById(R.id.spinnerMon);
        monType=ArrayAdapter.createFromResource(dialog.getContext(),R.array.date_day,R.layout.support_simple_spinner_dropdown_item);
        monType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerMon.setAdapter(monType);
        spinnerMon.setSelection(0);

        spinnerYearMonth=dialog.findViewById(R.id.spinnerYearMonth);
        year_monType=ArrayAdapter.createFromResource(dialog.getContext(),R.array.date_month,R.layout.support_simple_spinner_dropdown_item);
        year_monType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerYearMonth.setAdapter(year_monType);
        spinnerYearMonth.setSelection(0);

        spinnerYearDay=dialog.findViewById(R.id.spinnerYearDay);
        year_dayType=ArrayAdapter.createFromResource(dialog.getContext(),R.array.date_day,R.layout.support_simple_spinner_dropdown_item);
        year_dayType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerYearDay.setAdapter(year_dayType);
        spinnerYearDay.setSelection(0);

        //id 찾기
        tvCustomMonth=dialog.findViewById(R.id.tvCustomMonth);
        tvCustomWeek=dialog.findViewById(R.id.tvCustomWeek);
        tvCustomYear=dialog.findViewById(R.id.tvCustomYear);

        tvCustomMon=dialog.findViewById(R.id.tvCustomMon);
        tvCustomMon.setOnClickListener(this);
        tvCustomTue=dialog.findViewById(R.id.tvCustomTue);
        tvCustomTue.setOnClickListener(this);
        tvCustomWed=dialog.findViewById(R.id.tvCustomWed);
        tvCustomWed.setOnClickListener(this);
        tvCustomThu=dialog.findViewById(R.id.tvCustomThu);
        tvCustomThu.setOnClickListener(this);
        tvCustomFri=dialog.findViewById(R.id.tvCustomFri);
        tvCustomFri.setOnClickListener(this);
        tvCustomSat=dialog.findViewById(R.id.tvCustomSat);
        tvCustomSat.setOnClickListener(this);
        tvCustomSun=dialog.findViewById(R.id.tvCustomSun);
        tvCustomSun.setOnClickListener(this);

        LL_dialog_month=dialog.findViewById(R.id.LLDialogMonth);
        LL_dialog_week=dialog.findViewById(R.id.LLDialogWeek);
        LL_dialog_year=dialog.findViewById(R.id.LLDialogYear);

        //tvCustomMonth.setDefaultFocusHighlightEnabled(true);
        tvCustomMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //매달 낼 날짜 하루 지정 1~12
                tvCustomMonth.setTextColor(colorWhite);
                tvCustomMonth.setBackgroundColor(colorBackPink);
                payType=2;

                tvCustomWeek.setTextColor(colorTextPink);
                tvCustomWeek.setBackgroundColor(colorWhite);
                tvCustomYear.setTextColor(colorTextPink);
                tvCustomYear.setBackgroundColor(colorWhite);

                LL_dialog_month.setVisibility(View.VISIBLE);
                LL_dialog_week.setVisibility(View.GONE);
                LL_dialog_year.setVisibility(View.GONE);
            }
        });

        //tvCustomWeek.setDefaultFocusHighlightEnabled(true);
        tvCustomWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCustomWeek.setTextColor(colorWhite);
                tvCustomWeek.setBackgroundColor(colorBackPink);
                payType=1;

                tvCustomMonth.setTextColor(colorTextPink);
                tvCustomMonth.setBackgroundColor(colorWhite);
                tvCustomYear.setTextColor(colorTextPink);
                tvCustomYear.setBackgroundColor(colorWhite);

                LL_dialog_month.setVisibility(View.GONE);
                LL_dialog_week.setVisibility(View.VISIBLE);
                LL_dialog_year.setVisibility(View.GONE);
            }
        });

        //tvCustomYear.setDefaultFocusHighlightEnabled(true);
        tvCustomYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvCustomYear.setTextColor(colorWhite);
                tvCustomYear.setBackgroundColor(colorBackPink);
                payType=3;

                tvCustomWeek.setTextColor(colorTextPink);
                tvCustomWeek.setBackgroundColor(colorWhite);
                tvCustomMonth.setTextColor(colorTextPink);
                tvCustomMonth.setBackgroundColor(colorWhite);

                LL_dialog_month.setVisibility(View.GONE);
                LL_dialog_week.setVisibility(View.GONE);
                LL_dialog_year.setVisibility(View.VISIBLE);

            }
        });

        final Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
        final Button btnCancel = dialog.findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
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
                        tvPayTypeNum.setText(spinnerMon.getSelectedItem().toString());
                        break;
                    case 3:
                        //년 - 월,일 선택
                        tvPayType.setText("매년");
                        String day=spinnerYearMonth.getSelectedItem().toString()+"-"+spinnerYearDay.getSelectedItem().toString();
                        tvPayTypeNum.setText(day);
                        break;
                }

                // 커스텀 다이얼로그를 종료한다.
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
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
            case R.id.tvCustomMon:
                tvCustomMon.setTextColor(colorWhite);
                tvCustomMon.setBackgroundColor(colorBackPink);
                type1=2;

                tvCustomTue.setBackgroundColor(colorWhite);
                tvCustomTue.setTextColor(colorTextPink);
                tvCustomWed.setBackgroundColor(colorWhite);
                tvCustomWed.setTextColor(colorTextPink);
                tvCustomThu.setBackgroundColor(colorWhite);
                tvCustomThu.setTextColor(colorTextPink);
                tvCustomFri.setBackgroundColor(colorWhite);
                tvCustomFri.setTextColor(colorTextPink);
                tvCustomSat.setBackgroundColor(colorWhite);
                tvCustomSat.setTextColor(colorTextPink);
                tvCustomSun.setBackgroundColor(colorWhite);
                tvCustomSun.setTextColor(colorTextPink);
                break;
            case R.id.tvCustomTue:
                tvCustomTue.setTextColor(colorWhite);
                tvCustomTue.setBackgroundColor(colorBackPink);
                type1=3;

                tvCustomMon.setBackgroundColor(colorWhite);
                tvCustomMon.setTextColor(colorTextPink);
                tvCustomWed.setBackgroundColor(colorWhite);
                tvCustomWed.setTextColor(colorTextPink);
                tvCustomThu.setBackgroundColor(colorWhite);
                tvCustomThu.setTextColor(colorTextPink);
                tvCustomFri.setBackgroundColor(colorWhite);
                tvCustomFri.setTextColor(colorTextPink);
                tvCustomSat.setBackgroundColor(colorWhite);
                tvCustomSat.setTextColor(colorTextPink);
                tvCustomSun.setBackgroundColor(colorWhite);
                tvCustomSun.setTextColor(colorTextPink);

                break;
            case R.id.tvCustomWed:
                tvCustomWed.setTextColor(colorWhite);
                tvCustomWed.setBackgroundColor(colorBackPink);
                type1=4;

                tvCustomMon.setBackgroundColor(colorWhite);
                tvCustomMon.setTextColor(colorTextPink);
                tvCustomTue.setBackgroundColor(colorWhite);
                tvCustomTue.setTextColor(colorTextPink);
                tvCustomThu.setBackgroundColor(colorWhite);
                tvCustomThu.setTextColor(colorTextPink);
                tvCustomFri.setBackgroundColor(colorWhite);
                tvCustomFri.setTextColor(colorTextPink);
                tvCustomSat.setBackgroundColor(colorWhite);
                tvCustomSat.setTextColor(colorTextPink);
                tvCustomSun.setBackgroundColor(colorWhite);
                tvCustomSun.setTextColor(colorTextPink);

                break;
            case R.id.tvCustomThu:
                tvCustomThu.setTextColor(colorWhite);
                tvCustomThu.setBackgroundColor(colorBackPink);
                type1=5;

                tvCustomMon.setBackgroundColor(colorWhite);
                tvCustomMon.setTextColor(colorTextPink);
                tvCustomTue.setBackgroundColor(colorWhite);
                tvCustomTue.setTextColor(colorTextPink);
                tvCustomWed.setBackgroundColor(colorWhite);
                tvCustomWed.setTextColor(colorTextPink);
                tvCustomFri.setBackgroundColor(colorWhite);
                tvCustomFri.setTextColor(colorTextPink);
                tvCustomSat.setBackgroundColor(colorWhite);
                tvCustomSat.setTextColor(colorTextPink);
                tvCustomSun.setBackgroundColor(colorWhite);
                tvCustomSun.setTextColor(colorTextPink);

                break;
            case R.id.tvCustomFri:
                tvCustomFri.setTextColor(colorWhite);
                tvCustomFri.setBackgroundColor(colorBackPink);
                type1=6;

                tvCustomMon.setBackgroundColor(colorWhite);
                tvCustomMon.setTextColor(colorTextPink);
                tvCustomWed.setBackgroundColor(colorWhite);
                tvCustomWed.setTextColor(colorTextPink);
                tvCustomThu.setBackgroundColor(colorWhite);
                tvCustomThu.setTextColor(colorTextPink);
                tvCustomTue.setBackgroundColor(colorWhite);
                tvCustomTue.setTextColor(colorTextPink);
                tvCustomSat.setBackgroundColor(colorWhite);
                tvCustomSat.setTextColor(colorTextPink);
                tvCustomSun.setBackgroundColor(colorWhite);
                tvCustomSun.setTextColor(colorTextPink);

                break;
            case R.id.tvCustomSat:
                tvCustomSat.setTextColor(colorWhite);
                tvCustomSat.setBackgroundColor(colorBackPink);
                type1=7;

                tvCustomMon.setBackgroundColor(colorWhite);
                tvCustomMon.setTextColor(colorTextPink);
                tvCustomWed.setBackgroundColor(colorWhite);
                tvCustomWed.setTextColor(colorTextPink);
                tvCustomThu.setBackgroundColor(colorWhite);
                tvCustomThu.setTextColor(colorTextPink);
                tvCustomFri.setBackgroundColor(colorWhite);
                tvCustomFri.setTextColor(colorTextPink);
                tvCustomTue.setBackgroundColor(colorWhite);
                tvCustomTue.setTextColor(colorTextPink);
                tvCustomSun.setBackgroundColor(colorWhite);
                tvCustomSun.setTextColor(colorTextPink);

                break;
            case R.id.tvCustomSun:
                tvCustomSun.setTextColor(colorWhite);
                tvCustomSun.setBackgroundColor(colorBackPink);
                type1=1;

                tvCustomMon.setBackgroundColor(colorWhite);
                tvCustomMon.setTextColor(colorTextPink);
                tvCustomWed.setBackgroundColor(colorWhite);
                tvCustomWed.setTextColor(colorTextPink);
                tvCustomThu.setBackgroundColor(colorWhite);
                tvCustomThu.setTextColor(colorTextPink);
                tvCustomFri.setBackgroundColor(colorWhite);
                tvCustomFri.setTextColor(colorTextPink);
                tvCustomSat.setBackgroundColor(colorWhite);
                tvCustomSat.setTextColor(colorTextPink);
                tvCustomTue.setBackgroundColor(colorWhite);
                tvCustomTue.setTextColor(colorTextPink);

                break;
            default:

                break;
        }
    }
}

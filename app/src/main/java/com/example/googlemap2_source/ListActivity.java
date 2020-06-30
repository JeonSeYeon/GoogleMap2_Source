package com.example.googlemap2_source;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    public static int SelectedBtn = 0;

    public static ArrayList<ApiList> SortingList = new ArrayList<ApiList>();


    public double DistanceMath(String latitude, String longitude){

        double x1 = MapsActivity.mMap.getCameraPosition().target.latitude;
        double y1 = MapsActivity.mMap.getCameraPosition().target.longitude;

        double x2 = Double.parseDouble(latitude);
        double y2 = Double.parseDouble(longitude);

        // 도 분 초
        int ld1 = (int) x1;
        int lb1 = (int) ((x1 - ld1) * 60);
        double lc1 = (((x1 - ld1)*60) - lb1) * 60;

        int lld1 = (int) y1;
        int llb1 = (int) ((y1 - lld1) * 60);
        double llc1 = (((y1 - lld1)*60) - llb1) * 60;

        int ld2 = (int) x2;
        int lb2 = (int) ((x2 - ld2) * 60);
        double lc2 = (((x2 - ld2)*60) - lb2) * 60;

        int lld2 = (int) y2;
        int llb2 = (int) ((y2 - lld2) * 60);
        double llc2 = (((y2 - lld2)*60) - llb2) * 60;

        //Log.d("!!!!!", "ld2, lb2, lc2 = "+ld2+" , "+lb2+" , "+lc2);

        int _ld = (int)Math.sqrt(Math.pow((ld1-ld2), 2));
        int _lb = (int)Math.sqrt(Math.pow((lb1-lb2), 2));
        int _lc = (int)Math.sqrt(Math.pow((lc1-lc2), 2));

        int _lld = (int)Math.sqrt(Math.pow((lld1-lld2), 2));
        int _llb = (int)Math.sqrt(Math.pow((llb1-llb2), 2));
        int _llc = (int)Math.sqrt(Math.pow((llc1-llc2), 2));

        double d1 = (_ld*88804 + _lb*1480 + _lc*25)/1000;
        double d2 = (_lld*88804 + _llb*1480 + _llc*25)/1000;

        return Math.sqrt(Math.pow(d1, 2) + Math.pow(d2, 2));
    }


    public void SortingMenuList(){
        try {
            //리스트 초기화
            for(int i=0; i<SortingList.size(); i++){
                SortingList.remove(i);
                i--;
            }

            int[] tempI = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

            for(int i=0 ;i<LoadingActivity.SearchList2.size();i++){

                int _i = i;

                for(int j=0; j<tempI.length;j++) {
                    //우선 처음 10개 다집어 넣음.
                    if (tempI[j] == -1) {
                        tempI[j] = i;
                        break;
                    }

                    //LoadingActivity.SearchList2.get(_i) , LoadingActivity.SearchList2.get(tempI[j])
                    if ((int) Double.parseDouble(LoadingActivity.SearchList2.get(tempI[j]).nowDistance)
                            > (int) Double.parseDouble(LoadingActivity.SearchList2.get(_i).nowDistance)) {
                        int tempIj = tempI[j];
                        tempI[j] = _i;
                        _i = tempIj;
                    }
                }
            }

            for(int i=0; i<tempI.length;i++){
                /*Log.d("sortI", " - "+tempI[i]+
                        " d : "+LoadingActivity.SearchList2.get(tempI[i]).nowDistance +
                        "\n 위치 : "+LoadingActivity.SearchList2.get(tempI[i]).latitude+" , "+LoadingActivity.SearchList2.get(tempI[i]).longitude);*/
                SortingList.add(LoadingActivity.SearchList2.get(tempI[i]));
            }

        }catch (Exception e){
            Log.d("ex", e.toString());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout mainLayout = new LinearLayout(this);

        mainLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        //우선 거리계산.
        for(int i=0 ;i<LoadingActivity.SearchList2.size();i++) {

            DecimalFormat df = new DecimalFormat("#.###");

            String nowDist = df.format(DistanceMath(LoadingActivity.SearchList2.get(i).latitude,
                    LoadingActivity.SearchList2.get(i).longitude));

            LoadingActivity.SearchList2.get(i).setNowDistance(nowDist);
        }


        // 소팅작업 (근처 10개)
        SortingMenuList();

        for(int i=0 ;i<SortingList.size();i++){

            /*Log.d("Distance"
                    , LoadingActivity.SearchList2.get(i).ccName +
                            " : " + LoadingActivity.SearchList2.get(i).nowDistance +" km");*/

             /*RadioButton radioButton = new RadioButton(this);

            radioButton.setText(LoadingActivity.SearchList.get(i).toString());

            radioButton.setId(i);


            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);


            if( radioButton != null){
                mainLayout.addView(radioButton, layoutParams);
            }*/

            //라디오버튼식 구현 널익셉션 오류로 실패 포기.

            Button button = new Button(this);

            button.getBackground().setColorFilter(0x0F7B0395, PorterDuff.Mode.MULTIPLY);

            //button.setText(LoadingActivity.SearchList.get(i).toString());
            button.setText(SortingList.get(i).ccName
                    +" - 약 "+SortingList.get(i).nowDistance+" km");

            button.setLayoutParams(params);


            button.setTag(i);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent2 = new Intent(getApplicationContext(),ListClickActivity.class);
                    startActivity(intent2);

                    Object tag = v.getTag();
                    SelectedBtn = Integer.parseInt(tag.toString());
                    Log.d("test", "버튼" + tag + " : 클릭됨");
                }
            });

            mainLayout.addView(button);
        }

        setContentView(mainLayout);

    }

}

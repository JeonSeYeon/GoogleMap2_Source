package com.example.googlemap2_source;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

import java.util.List;

public class ListActivity extends AppCompatActivity {

    public static List params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout mainLayout = new LinearLayout(this);

        mainLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        for(int i=0 ;i<LoadingActivity.SearchList.size();i++){

            /*RadioGroup radioGroup = new RadioGroup(this);

            RadioButton radioButton = new RadioButton()*/

            Button button = new Button(this);

            button.setText(LoadingActivity.SearchList.get(i).toString());

            button.setLayoutParams(params);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent2 = new Intent(getApplicationContext(),ListClickActivity.class);
                    startActivity(intent2);
                }
            });

            mainLayout.addView(button);
        }

        setContentView(mainLayout);

    }

}

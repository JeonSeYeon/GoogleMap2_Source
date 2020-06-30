package com.example.googlemap2_source;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ListClickActivity extends AppCompatActivity {
    private static final String TAG = "API_img";
    public static List params;
    //API
    private String strServiceUrl;
    TextView txtContent;
    TextView txtName;
    TextView txtNum;
    TextView txtAddress;
    ImageView imageView;

    String strAddress = "";
    String strContent = "";
    String strImg = "";

    URL url;
    InputStream is;
    Bitmap bm;

    ArrayList<String> arrN = new ArrayList<>();
    ArrayList<String> arrAddress = new ArrayList<>();
    ArrayList<String> arrContent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Log.d("ListClickActivity", "버튼" + ListActivity.SelectedBtn + " : 클릭됨");
        Log.d("SearchList", "SearchList - " + ListActivity.SelectedBtn
                + " : "+ListActivity.SortingList.get(ListActivity.SelectedBtn).ccName
                + " , "+ListActivity.SortingList.get(ListActivity.SelectedBtn).ccbaAsno
                + " , "+ListActivity.SortingList.get(ListActivity.SelectedBtn).ccbaCtcd
                + " , "+ListActivity.SortingList.get(ListActivity.SelectedBtn).ccbaKdcd);



        txtContent = (TextView) findViewById(R.id.imgText);
        txtName = (TextView) findViewById(R.id.imgTitle);
        txtNum = (TextView) findViewById(R.id.imgHo);
        txtAddress = (TextView) findViewById(R.id.imgAdd);

        imageView = (ImageView) findViewById(R.id.imageView3);

        strServiceUrl = "https://www.cha.go.kr/cha/SearchKindOpenapiDt.do?" +
                "ccbaKdcd=" + ListActivity.SortingList.get(ListActivity.SelectedBtn).ccbaKdcd
                + "&ccbaAsno=" + ListActivity.SortingList.get(ListActivity.SelectedBtn).ccbaAsno
                +"&ccbaCtcd=" + ListActivity.SortingList.get(ListActivity.SelectedBtn).ccbaCtcd;
        //strServiceKey = "ya1BKq8iExZZZGk0EFE%2FFBzsuvW7zg3UxJ%2B2urlfmuw%2FsKlgCy%2BOt5kwNhwJbeTFoNkk26k0TcuCMjrVC4HX8Q%3D%3D";
        //strUrl = strServiceUrl + "?serviceKey=" + strServiceKey;

        //문화재 상세 내용조회
        //http://www.cha.go.kr/cha/SearchKindOpenapiDt.do?ccbaKdcd=11&ccbaAsno=00010000&ccbaCtcd=11
        ListClickActivity.DownloadWebpageTask1 objTask1 = new ListClickActivity.DownloadWebpageTask1(this);
        objTask1.execute(strServiceUrl);

    }

    class DownloadWebpageTask1 extends AsyncTask<String, Void, String> {


        boolean bSet_Num = false;
        boolean bSet_Img = false;
        boolean bSet_Name = false;
        boolean bset_Address = false;
        boolean bset_Content = false;

        String strNum = "";
        String strName = "";
        String result;
        Context context;

        public DownloadWebpageTask1(Context context) {
            this.context = context;
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection urlConn = null;

            try {
                URL url = new URL(myurl);
                urlConn = (HttpURLConnection) url.openConnection();
                BufferedInputStream inBuf = new BufferedInputStream(urlConn.getInputStream());
                BufferedReader bufReader = new BufferedReader(new InputStreamReader(inBuf, "utf-8"));

                String strLine = null;
                String strPage = "";
                while ((strLine = bufReader.readLine()) != null) {
                    strPage += strLine;
                }
                return strPage;
            } finally {
                urlConn.disconnect();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                result = (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "Fail download ! ";
            }

            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                //Log.d("123123123", xpp.);
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("crltsnoNm")) bSet_Num = true;
                        if (tag_name.equals("imageUrl")) bSet_Img = true;
                        if (tag_name.equals("ccbaMnm1")) bSet_Name = true;
                        if (tag_name.equals("ccbaLcad")) bset_Address = true;
                        if (tag_name.equals("content")) bset_Content = true;
                    } else if (eventType == XmlPullParser.TEXT) {

                        if (bSet_Num) {
                            strNum += xpp.getText();
                            bSet_Num = false;

                        }
                        if (bSet_Img) {
                            strImg += xpp.getText();
                            bSet_Img = false;

                        }
                        if (bSet_Name) {
                            strName += xpp.getText();
                            bSet_Name = false;

                        }
                        if (bset_Address) {
                            strAddress += xpp.getText();
                            bset_Address = false;
                        }
                        if (bset_Content) {
                            strContent += xpp.getText();
                            bset_Content = false;
                        }


                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }


                url = new URL(strImg);
                //Log.d(TAG, "이미지당 " + strImg);

                is = url.openStream();
                bm = BitmapFactory.decodeStream(is);

                txtName.setText(strName.toString());
                txtNum.setText("국보"+strNum.toString()+"호"+"\n거리 : 약"
                        +ListActivity.SortingList.get(ListActivity.SelectedBtn).nowDistance+" km");
                txtAddress.setText(strAddress.toString());
                txtContent.setText(strContent.toString());



                imageView.setImageBitmap(bm); //비트맵 객체로 보여주기

            } catch (Exception e) {
            }

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

    }

}

package com.example.googlemap2_source;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.SupportMapFragment;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LoadingActivity extends Activity {

    public static  ArrayList<ApiList> SearchList2 = new ArrayList<ApiList>();

    private static final String TAG = "API_example";

    //API
    private String strServiceUrl, strServiceKey, numOfRows, pageNo, strUrl;
    TextView t;
    String Long = "";
    String Lat = "";

    ArrayList<Double> arrX = new ArrayList<>();
    ArrayList<Double> arrY = new ArrayList<>();
    ArrayList<String> arrN = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        //strServiceUrl = "https://www.cha.go.kr/cha/SearchKindOpenapiList.do";
        strServiceUrl = "https://www.cha.go.kr/cha/SearchKindOpenapiList.do?pageUnit=500";
        //strServiceKey = "ya1BKq8iExZZZGk0EFE%2FFBzsuvW7zg3UxJ%2B2urlfmuw%2FsKlgCy%2BOt5kwNhwJbeTFoNkk26k0TcuCMjrVC4HX8Q%3D%3D";
        //strUrl = strServiceUrl + "?serviceKey=" + strServiceKey;

        //문화재 상세 내용조회
        //http://www.cha.go.kr/cha/SearchKindOpenapiDt.do?ccbaKdcd=11&ccbaAsno=00010000&ccbaCtcd=11
        LoadingActivity.DownloadWebpageTask1 objTask1 = new LoadingActivity.DownloadWebpageTask1(this);
        objTask1.execute(strServiceUrl);

    }

    class DownloadWebpageTask1 extends AsyncTask<String, Void, String> {

        String strName = "";
        String param = "";
        boolean bSet_Name = false;
        boolean bset_Long = false;
        boolean bset_Lat = false;
        boolean bset_Kdcd = false;
        boolean bset_Ctcd = false;
        boolean bset_Asno = false;

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

                ApiList tempList = new ApiList();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                //Log.d("123123123", xpp.);
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("ccbaMnm1")) bSet_Name = true;
                        if (tag_name.equals("longitude")) bset_Long = true;
                        if (tag_name.equals("latitude")) bset_Lat = true;
                        if (tag_name.equals("ccbaKdcd")) bset_Kdcd = true;
                        if (tag_name.equals("ccbaCtcd")) bset_Ctcd = true;
                        if (tag_name.equals("ccbaAsno")) bset_Asno = true;
                    } else if (eventType == XmlPullParser.TEXT) {

                        if (bSet_Name) {
                            strName = xpp.getText();
                            Log.d(TAG, "이름 : " + strName);
                            //t.append("Name: " + strName + "\n");
                            arrN.add(strName);

                            tempList = new ApiList();
                            tempList.setCcName(strName);

                            bSet_Name = false;
                        }
                        if (bset_Kdcd) {
                            param = xpp.getText();
                            tempList.setCcbaKdcd(param);

                            bset_Kdcd = false;
                        }
                        if (bset_Ctcd) {
                            param = xpp.getText();
                            tempList.setCcbaCtcd(param);

                            bset_Ctcd = false;
                        }
                        if (bset_Asno) {
                            param = xpp.getText();
                            tempList.setCcbaAsno(param);

                            SearchList2.add(tempList);

                            bset_Asno = false;
                        }
                        if (bset_Long) {
                            Long = xpp.getText();
                            tempList.setLongitude(Long.toString());
                            Log.d(TAG, "경도" + Long);
                            //t.append("Long: " + Long + "\n");
                            arrY.add(new Double(Long));
                            bset_Long = false;
                        }
                        if (bset_Lat) {
                            Lat = xpp.getText();
                            tempList.setLatitude(Lat.toString());
                            Log.d(TAG, "위도" + Lat);
                            arrX.add(new Double(Lat));
                            //t.append("Lat: " + Lat + "\n");
                            bset_Lat = false;
                        }



                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                }
            } catch (Exception e) {
                t.setText(e.getMessage());
            }

            try {
                Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayList<GetterSetter2> arrayList = new ArrayList<>();

            for(int i=0; i< arrN.size(); i++){
                arrayList.add(new GetterSetter2(arrN.get(i),arrX.get(i), arrY.get(i)));
                Log.d(TAG, "send DATA " + arrayList.get(i).getName());
            }
            Intent intents = new Intent(context, MapsActivity.class);
            intents.putExtra("arrayList",arrayList);
            startActivity(intents);
            finish();
        }
    }
}

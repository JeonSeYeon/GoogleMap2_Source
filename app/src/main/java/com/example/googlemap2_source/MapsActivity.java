package com.example.googlemap2_source;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemap2_source.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,GoogleMap.OnCameraIdleListener{
    public static GoogleMap mMap;

    private Geocoder geocoder;
    private Button button;
    private EditText editText;

    private static final String TAG = "googlemap_example";

    ArrayList<GetterSetter2> arrayList = new ArrayList<>();
    ArrayList<GetterSetter> list = new ArrayList<>();

    private ClusterManager<GetterSetter> manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //API
        setContentView(R.layout.activity_maps);


        arrayList = (ArrayList<GetterSetter2>) getIntent().getSerializableExtra("arrayList");

        editText = (EditText) findViewById(R.id.editText);
        button=(Button)findViewById(R.id.button);

        for(int i =0; i< arrayList.size(); i++){
            list.add(new GetterSetter(arrayList.get(i).getName(), arrayList.get(i).getLatitude(), arrayList.get(i).getLongtitude()));
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        geocoder = new Geocoder(this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.559975221378, 126.975312652739), mMap.getCameraPosition().zoom+6));
        mMap.setOnCameraIdleListener(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        manager = new ClusterManager<>(this, mMap);
        manager.setRenderer(new MapCluster(this, mMap, manager));
        manager.setAnimation(true);

        for(int i =0; i< arrayList.size(); i++){
            Log.d(TAG, "Receive DATA " + arrayList.get(i).getName());
        }

        manager.addItems(list);
        manager.cluster();


        manager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<GetterSetter>() {
            @Override
            public boolean onClusterClick(Cluster<GetterSetter> cluster) {

                //클러스트가 되어있는 아이템을 클릭했을때
                if (cluster.getSize() > 9)
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), mMap.getCameraPosition().zoom + 3));
                }
                return false;
            }
        });

        manager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<GetterSetter>() {
            @Override
            public boolean onClusterItemClick(GetterSetter item) {

                //아이템 1개 클릭할때
                return false;
            }
        });


        // 검색 버튼 이벤트
        button.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){

                if(editText != null){ //에러 고쳐야함.!~!!!!!!!!!!!!!!!!!!!!!!!!!

                    String str=editText.getText().toString();

                    List<Address> addressList = null;
                    try {
                        // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                        addressList = geocoder.getFromLocationName(
                                str, // 주소
                                10); // 최대 검색 결과 개수
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 콤마를 기준으로 split
                    String []splitStr = addressList.get(0).toString().split("=");

                    String address = splitStr[1].substring(splitStr[1].indexOf("\"") + 1,splitStr[1].indexOf("]") - 1);
                    address = address.replace(",",""); // 주소

                    String latitude = splitStr[11].substring(0, splitStr[11].indexOf(",")); // 위도
                    String longitude = splitStr[13].substring(0, splitStr[13].indexOf(",")); // 경도

                    // 좌표(위도, 경도) 생성
                    LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                    // 마커 생성
                    MarkerOptions mOptions2 = new MarkerOptions();
                    mOptions2.title("search result");
                    mOptions2.snippet(address);
                    mOptions2.position(point);
                    // 마커 추가
                    //mMap.addMarker(mOptions2);
                    // 해당 좌표로 화면 줌
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
                }else{
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMap.getCameraPosition().target, mMap.getCameraPosition().zoom));
                }
            }
        });

    }

    @Override
    public void onCameraIdle() {

        //맵을 손으로 움직일때, 제일마지막에 맵이 멈췄을때 작동하는 공간
        Log.d("Map : ", "is Moved");

        if (manager!=null)
            manager.cluster();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.list) {
            Intent Intent = new Intent(this, ListActivity.class);
            startActivity(Intent);
            return true;
        }
        if (id == R.id.setting) {

            Intent intent= new Intent(Settings.ACTION_SETTINGS);
            startActivityForResult(intent,0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
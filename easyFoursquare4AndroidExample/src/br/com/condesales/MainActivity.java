package br.com.condesales;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Random;

import br.com.condesales.criterias.CheckInCriteria;
import br.com.condesales.criterias.TipsCriteria;
import br.com.condesales.criterias.VenuesCriteria;
import br.com.condesales.listeners.AccessTokenRequestListener;
import br.com.condesales.listeners.CheckInListener;
import br.com.condesales.listeners.FoursquareVenuesRequestListener;
import br.com.condesales.listeners.ImageRequestListener;
import br.com.condesales.listeners.TipsRequestListener;
import br.com.condesales.listeners.UserInfoRequestListener;
import br.com.condesales.models.Checkin;
import br.com.condesales.models.Tip;
import br.com.condesales.models.User;
import br.com.condesales.models.Venue;
import br.com.condesales.tasks.users.UserImageRequest;

public class MainActivity extends Activity implements
        AccessTokenRequestListener, ImageRequestListener, LocationListener ,OnClickListener, OnCheckedChangeListener{

    private EasyFoursquareAsync async;
    private ImageView userImage;
    private ViewSwitcher viewSwitcher;
    private TextView userName;
    private LocationManager locationManager;
    private String catid="4d4b7105d754a06374d81259";
    private RadioGroup mRadioGroup;
    private RadioButton set, rahmen, cafe, restaurant;
    
    @Override
    
    public void onStart() {
     
     super.onStart();
     
     locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
     locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // プロバイダ
     0, // 通知のための最小時間間隔
     0, // 通知のための最小距離間隔
     this); // 位置情報リスナー
    }
     
    @Override
     
    public void onStop() {
     
    super.onStop();
     locationManager.removeUpdates(this);
     
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        setTitle("食え、これを。");
//        userImage = (ImageView) findViewById(R.id.imageView1);
//        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
//        userName = (TextView) findViewById(R.id.textView1);
        //ask for access
        async = new EasyFoursquareAsync(this);
        async.requestAccess(this);
        
       LocationManager mLocationManager =
            (LocationManager) getSystemService(Context.LOCATION_SERVICE);

       Criteria criteria = new Criteria();
       criteria.setAccuracy(Criteria.ACCURACY_COARSE);
       criteria.setPowerRequirement(Criteria.POWER_LOW);
       String provider = mLocationManager.getBestProvider(criteria, true);

       ImageButton iybtn=(ImageButton)findViewById(R.id.iyadabtn);
       iybtn.setOnClickListener(this);
   
       mRadioGroup = (RadioGroup)findViewById(R.id.radiogroup);
       mRadioGroup.setOnCheckedChangeListener(this);
  
    }
    
    public void onCheckedChanged(RadioGroup group, int buttonId){
        
        set=(RadioButton)findViewById(R.id.radiobutton_set);
        cafe=(RadioButton)findViewById(R.id.radiobutton_cafe);
        rahmen=(RadioButton)findViewById(R.id.radiobutton_rahmen);
        restaurant=(RadioButton)findViewById(R.id.radiobutton_restaurant);
         
        if(set.isChecked() == true) {
        	catid = "4bf58dd8d48988d147941735";
        }else if(cafe.isChecked() == true){
        	catid = "4bf58dd8d48988d16d941735";
        }else if(rahmen.isChecked() == true){
        	catid = "4bf58dd8d48988d1d1941735";
        }else{
        	catid = "4bf58dd8d48988d1c4941735";
        }
    }

    @Override
    public void onError(String errorMsg) {
        // Do something with the error message
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAccessGrant(String accessToken) {
        // with the access token you can perform any request to foursquare.
        // example:
//        async.getUserInfo(new UserInfoRequestListener() {
//
//            @Override
//            public void onError(String errorMsg) {
//                // Some error getting user info
//                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG)
//                        .show();
//            }
//
////            @Override
////            public void onUserInfoFetched(User user) {
////                // OWww. did i already got user!?
////                if (user.getBitmapPhoto() == null) {
////                    UserImageRequest request = new UserImageRequest(
////                            MainActivity.this, MainActivity.this);
////                    request.execute(user.getPhoto());
////                } else {
////                    userImage.setImageBitmap(user.getBitmapPhoto());
////                }
////                userName.setText(user.getFirstName() + " " + user.getLastName());
////                viewSwitcher.showNext();
////                Toast.makeText(MainActivity.this, "Got it!", Toast.LENGTH_LONG)
////                        .show();
////            }
//        });

        
        //requestVenuesNearby();
        
        //for another examples uncomment lines below:
        //requestTipsNearby();
//        checkin();
    }

    @Override
    public void onImageFetched(Bitmap bmp) {
        userImage.setImageBitmap(bmp);
    }
    
    private void requestVenuesNearby(String categoryID){
        Location loc = null;
        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
//        loc.setLatitude(36.11);
//        loc.setLongitude(140.10);

        VenuesCriteria criteria = new VenuesCriteria();

        if(loc == null){
        	loc = new Location("");
        	loc.setLatitude(36.3418112);
        	loc.setLongitude(140.4467935);
        }
        
        criteria.setLocation(loc);
        criteria.setQuantity(10);
        criteria.setCategory(categoryID);
   
        async.getVenuesNearby(new FoursquareVenuesRequestListener() {
			
			@Override
			public void onError(String errorMsg) {
				// TODO Auto-generated method stub
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
				
			}
			
			@Override
			public void onVenuesFetched(ArrayList<Venue> venues) {
				// TODO Auto-generated method stub
				TextView text = (TextView)findViewById(R.id.venue);
				String str = "";
//				for(Venue v: venues){
//					str += v.getName() + "（ここから"+ v.getLocation().getDistance() + "m）\n";
//				}
//				text.setText(str);
				Random rand=new Random();
				int idx=rand.nextInt(venues.size());
				text.setText(venues.get(idx).getName() + "\n（ここから"+ venues.get(idx).getLocation().getDistance() + "m）");
			
			}
		}, criteria);
        
    	
    }
    
    public void onClick(View v){
		requestVenuesNearby(catid);
    	
//    	TextView txt=(TextView)findViewById(R.id.venue);
//    	txt.setText("Hello");
    }
    
   
    private void requestTipsNearby() {
        Location loc = new Location("");
        loc.setLatitude(40.4363483);
        loc.setLongitude(-3.6815703);

        TipsCriteria criteria = new TipsCriteria();
        criteria.setLocation(loc);
        async.getTipsNearby(new TipsRequestListener() {

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTipsFetched(ArrayList<Tip> tips) {
                Toast.makeText(MainActivity.this, tips.toString(), Toast.LENGTH_LONG).show();
            }
        }, criteria);
    }

    private void checkin() {

        CheckInCriteria criteria = new CheckInCriteria();
        criteria.setBroadcast(CheckInCriteria.BroadCastType.PUBLIC);
        criteria.setVenueId("4c7063da9c6d6dcb9798d27a");

        async.checkIn(new CheckInListener() {
            @Override
            public void onCheckInDone(Checkin checkin) {
                Toast.makeText(MainActivity.this, checkin.getId(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(String errorMsg) {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
            }
        }, criteria);
    }


	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
//        TextView tv_lat = (TextView) findViewById(R.id.Latitude);
//        tv_lat.setText("Latitude:"+location.getLatitude());
//        TextView tv_lng = (TextView) findViewById(R.id.Longitude);
//        tv_lng.setText("Longitude:"+location.getLongitude());
//		
        locationManager.removeUpdates(this);
		requestVenuesNearby("4d4b7105d754a06374d81259");
//		TextView text = (TextView)findViewById(R.id.venue);
//		text.setText("Hello");
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


}

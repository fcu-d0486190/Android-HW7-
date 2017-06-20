package com.bobo.hw7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private HotelArrayAdapter adapter = null;

    private static final int LIST_HOTELS = 1;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case LIST_HOTELS:{
                    List<hotel> hotels = (List<hotel>)msg.obj;
                    refreshHotelList(hotels);
                    break;
                }
            }
        }
    };

    private void refreshHotelList(List<hotel> hotels) {
        adapter.clear();
        adapter.addAll(hotels);
    }

    private Bitmap getImgBitmap(String imgUrl) {
        try {
            URL url = new URL(imgUrl);
            Bitmap bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bm;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvHotels = (ListView)findViewById(android.R.id.list);

        adapter = new HotelArrayAdapter(this, new ArrayList<hotel>());
        lvHotels.setAdapter(adapter);

        getPetsFromFirebase();
    }

    class FirebaseThread extends Thread{

        private DataSnapshot dataSnapshot;

        public FirebaseThread(DataSnapshot dataSnapshot){
            this.dataSnapshot = dataSnapshot;
        }

        @Override
        public void run(){
            List<hotel> lsHotel = new ArrayList<>();
            for (DataSnapshot ds : dataSnapshot.getChildren()){

                DataSnapshot dsName = ds.child("Name");
                DataSnapshot dsplace = ds.child("Add");

                String HotelName = (String)dsName.getValue();
                String HotelPlace = (String)dsplace.getValue();

                DataSnapshot dsImg = ds.child("Picture1");
                String HotelImg = (String) dsImg.getValue();

                Bitmap Img = getImgBitmap(HotelImg);

                hotel ahotel = new hotel();
                ahotel.setHotelname(HotelName);
                ahotel.setHoteladdress(HotelPlace);
                ahotel.setImpicture(Img);
                lsHotel.add(ahotel);
                Log.v("hw7", HotelName + "," + HotelPlace);
            }

            Message msg = new Message();
            msg.what = LIST_HOTELS;
            msg.obj = lsHotel;
            handler.sendMessage(msg);

        }

    }

    private void getPetsFromFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/Infos/Info");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                new FirebaseThread(dataSnapshot).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("hw7", databaseError.getMessage());
            }
        });
    }

    class HotelArrayAdapter extends ArrayAdapter<hotel> {
        Context context;

        public HotelArrayAdapter(Context context, List<hotel> items) {
            super(context, 0, items);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            LinearLayout itemlayout = null;
            if (convertView == null) {
                itemlayout = (LinearLayout) inflater.inflate(R.layout.hotel_item, null);
            } else {
                itemlayout = (LinearLayout) convertView;
            }
            hotel item = (hotel) getItem(position);
            TextView tvShelter = (TextView) itemlayout.findViewById(R.id.tv_name);
            tvShelter.setText(item.getHotelname());
            TextView tvKind = (TextView) itemlayout.findViewById(R.id.tv_address);
            tvKind.setText(item.getHoteladdress());
            ImageView ivPet = (ImageView) itemlayout.findViewById(R.id.im_self);
            ivPet.setImageBitmap(item.getImpicture());
            return itemlayout;
        }
    }

}
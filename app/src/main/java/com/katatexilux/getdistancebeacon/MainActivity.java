package com.katatexilux.getdistancebeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.List;
import java.util.UUID;

                    //PRENDE IL BEACON PIU' VICINO E STAMPA UUID, MAJOR MINOR E DISTANZA

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private BeaconRegion region;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beaconManager = new BeaconManager(this);
        region = new BeaconRegion("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
    }



    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion region, List<Beacon> list) {
                //Creo linearLayout come contenitore delle TextView
                LinearLayout myLinearLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
                String nomeBeacon = new String();
                if (!list.isEmpty()) {
                    //Cancello tutti i figli del LinearLayout
                    if(((LinearLayout) myLinearLayout).getChildCount() > 0)
                        ((LinearLayout) myLinearLayout).removeAllViews();
                    final TextView[] myTextViews = new TextView[list.size()];
                    //Determino nome beacon in base al major
                    for(int i = 0; i<list.size();i++) {
                        if(list.get(i).getMajor() == 30)
                            nomeBeacon = "Ice";
                        else if(list.get(i).getMajor() == 20)
                            nomeBeacon = "Mint";
                        else
                            nomeBeacon = "Blueberry";
                        //creo una TextView
                        final TextView rowTextView = new TextView(getApplicationContext());
                        //Prendo il beacon e mi salvo tutti i suoi dati
                        Beacon uniqueBeacon = list.get(i);
                        String uuid = uniqueBeacon.getProximityUUID().toString();
                        int major = uniqueBeacon.getMajor();
                        int minor = uniqueBeacon.getMinor();
                        double distance = RegionUtils.computeAccuracy(uniqueBeacon);

                        //TextView textView_Descr = (TextView) findViewById(R.id.textView);
                        rowTextView.setText("\nNome:" + nomeBeacon + "\nUUID: " + uuid + "\nMAJOR: " + major + "\nMINOR: " + minor + "\nDISTANZA: " + distance);
                        //Aggiungo la TextView al LinearLayout
                        myLinearLayout.addView(rowTextView);
                        myTextViews[i] = rowTextView;
                        //textView_Descr.setText("UUID: " + uuid + " MAJOR: " + major + " MINOR: " + minor + " DISTANZA; " + distance);
                    }
                }
                //Svuoto la lista
                list.removeAll(list);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }
    }


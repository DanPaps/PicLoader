package com.example.danielpappoe.picloader;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

//import io.smooch.core.Smooch;

/**
 * Created by Daniel Pappoe on 6/26/2017.
 */

public class AppLife extends Application {
    public AppLife() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Smooch.init(this, getString(R.string.smooch));
        if (FirebaseApp.getInstance() != null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
        //Initialize Firebase for the application
        //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        /*Picasso.Builder builder= new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built =builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/






    }
}

package com.example.danielpappoe.picloader;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class Rate extends Activity {
    TextView sendButton ;
    RatingBar ratingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        //the rating bar
        ratingBar =(RatingBar)findViewById(R.id.ratingBar);


        //the send button for the rating
        sendButton =(TextView)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        float ratingValue =ratingBar.getRating();
                        Toast.makeText(getApplication(),"Rating is: "+ratingValue,Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

}

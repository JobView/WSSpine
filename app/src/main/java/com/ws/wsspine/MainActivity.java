package com.ws.wsspine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ws.wsspine.activity.MultipleSpineActivity;
import com.ws.wsspine.activity.MumuHuanActivity;
import com.ws.wsspine.activity.SpinebodyActivity;
import com.ws.wsspine.dialog.ShowSpineDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View view) {
       if(view.getId() == R.id.button_mumuhuan){
            startActivity(new Intent(this, MumuHuanActivity.class));
        }else  if(view.getId() == R.id.button_spinebody){
           startActivity(new Intent(this, SpinebodyActivity.class));
       }else  if(view.getId() == R.id.button_multiple){
           startActivity(new Intent(this, MultipleSpineActivity.class));
       }else  if(view.getId() == R.id.button_dialog){
           new ShowSpineDialog(this).show();
       }
    }
}
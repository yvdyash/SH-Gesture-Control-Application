package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    String[] items = new String[]{"Turn on lights", "Turn off lights", "Turn on fan", "Turn off fan", "Increase fanspeed", "decrease fan speed", "Set Thermostat to specified temperature", "0","1","2","3","4","5","6","7","8","9"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectGesture = findViewById(R.id.button);

        Spinner dropdown = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        selectGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainActivity2Intent = new Intent(view.getContext(), MainActivity2.class);
                Bundle bundle = new Bundle();
                bundle.putInt("GestureInd", dropdown.getSelectedItemPosition());
                mainActivity2Intent.putExtras(bundle);
                startActivity(mainActivity2Intent);
            }
        });
    }
}
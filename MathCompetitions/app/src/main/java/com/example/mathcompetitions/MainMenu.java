package com.example.mathcompetitions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void AMC10(View view){
        Intent intent = new Intent(getApplicationContext(), AMCquestions.class);
        intent.putExtra("Grade", 10);
        startActivity(intent);
    }

    public void AMC12(View view){
        Intent intent = new Intent(getApplicationContext(), AMCquestions.class);
        intent.putExtra("Grade", 12);
        startActivity(intent);
    }
    public void AIME(View view){
        Intent intent = new Intent(getApplicationContext(), AIMEquestions.class);
        startActivity(intent);
    }

}

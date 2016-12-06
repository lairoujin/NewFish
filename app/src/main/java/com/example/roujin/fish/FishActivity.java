package com.example.roujin.fish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FishActivity extends AppCompatActivity {

    private GameView GameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fish);

        GameView = (GameView) findViewById(R.id.fish_game_view);
        int[] bitmaps =  new int[]{
                R.drawable.playerfish,
                R.drawable.enemyfishes
        };
        GameView.start(bitmaps);
    }
}

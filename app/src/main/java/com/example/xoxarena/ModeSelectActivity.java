package com.example.xoxarena;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ModeSelectActivity extends AppCompatActivity {

    private Button btnVsBot, btnMultiplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_select);

        // Linking buttons to the layout IDs you specified
        btnVsBot = findViewById(R.id.btnVsBot);
        btnMultiplayer = findViewById(R.id.btnMultiplayer);

        // Bot mode (VS Bot)
        btnVsBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelectActivity.this, ChooseSymbolActivity.class);
                startActivity(intent);
            }
        });

        // Multiplayer mode
        btnMultiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelectActivity.this, MainActivity.class);
                intent.putExtra("mode", "player");
                intent.putExtra("symbol", "X"); // assume Player 1 is X
                startActivity(intent);
                finish();
            }
        });
    }
}

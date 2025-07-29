package com.example.xoxarena;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseSymbolActivity extends AppCompatActivity {

    private Button xBtn, oBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_symbol);

        xBtn = findViewById(R.id.xBtn);
        oBtn = findViewById(R.id.oBtn);

        xBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGame("X");
            }
        });

        oBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGame("O");
            }
        });
    }

    private void launchGame(String symbol) {
        Intent intent = new Intent(ChooseSymbolActivity.this, MainActivity.class);
        intent.putExtra("mode", "bot");
        intent.putExtra("symbol", symbol);
        startActivity(intent);
        finish(); // Prevent going back to symbol select
    }
}

package com.example.xoxarena;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class BotActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean playerTurn = true;
    private String playerSymbol = "X";
    private String botSymbol = "O";
    private boolean gameActive = true;
    private GridLayout gridLayout;
    private ImageButton pauseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        gridLayout = findViewById(R.id.gridLayout);
        pauseBtn = findViewById(R.id.pauseButton);


        // Get intent symbol from ChooseSymbolActivity
        Intent intent = getIntent();
        playerSymbol = intent.getStringExtra("playerSymbol");
        botSymbol = playerSymbol.equals("X") ? "O" : "X";
        playerTurn = playerSymbol.equals("X");

        setupGrid();
        setupPauseButton();
    }

    private void setupGrid() {
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = (Button) gridLayout.getChildAt(index++);
                buttons[i][j] = btn;
                final int row = i, col = j;
                btn.setOnClickListener(v -> {
                    if (!gameActive || !btn.getText().toString().equals("") || !playerTurn) return;
                    btn.setText(playerSymbol);
                    playerTurn = false;
                    if (checkWin(playerSymbol)) {
                        Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show();
                        gameActive = false;
                    } else if (isBoardFull()) {
                        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
                        gameActive = false;
                    } else {
                        botMove();
                    }
                });
            }
        }
    }

    private void botMove() {
        if (!gameActive) return;

        new Handler().postDelayed(() -> {
            int row, col;
            Random rand = new Random();
            do {
                row = rand.nextInt(3);
                col = rand.nextInt(3);
            } while (!buttons[row][col].getText().toString().equals(""));

            buttons[row][col].setText(botSymbol);

            if (checkWin(botSymbol)) {
                Toast.makeText(this, "Bot Wins!", Toast.LENGTH_SHORT).show();
                gameActive = false;
            } else if (isBoardFull()) {
                Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
                gameActive = false;
            } else {
                playerTurn = true;
            }
        }, 500);
    }

    private boolean checkWin(String symbol) {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().toString().equals(symbol) &&
                    buttons[i][1].getText().toString().equals(symbol) &&
                    buttons[i][2].getText().toString().equals(symbol)) return true;

            if (buttons[0][i].getText().toString().equals(symbol) &&
                    buttons[1][i].getText().toString().equals(symbol) &&
                    buttons[2][i].getText().toString().equals(symbol)) return true;
        }

        return (buttons[0][0].getText().toString().equals(symbol) &&
                buttons[1][1].getText().toString().equals(symbol) &&
                buttons[2][2].getText().toString().equals(symbol)) ||

                (buttons[0][2].getText().toString().equals(symbol) &&
                        buttons[1][1].getText().toString().equals(symbol) &&
                        buttons[2][0].getText().toString().equals(symbol));
    }

    private boolean isBoardFull() {
        for (Button[] row : buttons) {
            for (Button btn : row) {
                if (btn.getText().toString().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setupPauseButton() {
        pauseBtn.setOnClickListener(view -> {
            PauseDialog dialog = new PauseDialog(this,
                    this::resetGame,
                    this::finish);
            dialog.show();
        });
    }

    private void resetGame() {
        for (Button[] row : buttons) {
            for (Button btn : row) {
                btn.setText("");
                btn.setEnabled(true);
            }
        }
        gameActive = true;
        playerTurn = playerSymbol.equals("X");
    }
}

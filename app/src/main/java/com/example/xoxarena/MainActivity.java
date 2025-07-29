package com.example.xoxarena;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean playerTurn = true; // true = X, false = O
    private int roundCount;
    private TextView status;
    private Button resetButton, pauseButton;

    private String mode = "bot";    // default mode
    private String playerSymbol = "X";
    private String opponentSymbol = "O";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.statusText);
        resetButton = findViewById(R.id.resetButton);
        pauseButton = findViewById(R.id.pauseButton);

        // Get intent extras
        if (getIntent() != null) {
            mode = getIntent().getStringExtra("mode");
            playerSymbol = getIntent().getStringExtra("symbol");
            if (playerSymbol != null && playerSymbol.equals("O")) {
                opponentSymbol = "X";
                playerTurn = false; // let bot or second player start if O
            }
        }

        // Set status
        if (mode.equals("player")) {
            status.setText("Multiplayer Mode");
        } else {
            status.setText("VS Bot Mode");
        }

        // Initialize buttons
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);

                int finalI = i;
                int finalJ = j;
                buttons[i][j].setOnClickListener(view -> {
                    if (!((Button) view).getText().toString().equals("")) return;

                    ((Button) view).setText(playerTurn ? playerSymbol : opponentSymbol);
                    ((Button) view).setTextColor(getResources().getColor(
                            playerTurn ? android.R.color.holo_red_light : android.R.color.white));
                    roundCount++;

                    if (checkWin()) {
                        status.setText((playerTurn ? playerSymbol : opponentSymbol) + " Wins!");
                        disableAllButtons();
                    } else if (roundCount == 9) {
                        status.setText("Draw!");
                    } else {
                        playerTurn = !playerTurn;
                        if (mode.equals("bot") && !playerTurn) {
                            botMove();
                        }
                    }
                });
            }
        }

        resetButton.setOnClickListener(view -> resetGame());
        pauseButton.setOnClickListener(view -> status.setText("Game Paused"));

        // Bot auto-move if player chose O
        if (mode.equals("bot") && !playerTurn) {
            botMove();
        }
    }

    private void botMove() {
        // Very simple bot: first empty cell
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    buttons[i][j].setText(opponentSymbol);
                    buttons[i][j].setTextColor(getResources().getColor(android.R.color.white));
                    roundCount++;
                    if (checkWin()) {
                        status.setText("Bot Wins!");
                        disableAllButtons();
                    } else if (roundCount == 9) {
                        status.setText("Draw!");
                    } else {
                        playerTurn = true;
                    }
                    return;
                }
            }
        }
    }

    private boolean checkWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                field[i][j] = buttons[i][j].getText().toString();

        // Rows & Columns
        for (int i = 0; i < 3; i++) {
            if (!field[i][0].equals("") &&
                    field[i][0].equals(field[i][1]) &&
                    field[i][0].equals(field[i][2])) return true;

            if (!field[0][i].equals("") &&
                    field[0][i].equals(field[1][i]) &&
                    field[0][i].equals(field[2][i])) return true;
        }

        // Diagonals
        if (!field[0][0].equals("") &&
                field[0][0].equals(field[1][1]) &&
                field[0][0].equals(field[2][2])) return true;

        if (!field[0][2].equals("") &&
                field[0][2].equals(field[1][1]) &&
                field[0][2].equals(field[2][0])) return true;

        return false;
    }

    private void disableAllButtons() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setEnabled(false);
    }

    private void resetGame() {
        roundCount = 0;
        playerTurn = playerSymbol.equals("X");
        status.setText(mode.equals("player") ? "Multiplayer Mode" : "VS Bot Mode");

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }

        if (mode.equals("bot") && !playerTurn) {
            botMove();
        }
    }
}

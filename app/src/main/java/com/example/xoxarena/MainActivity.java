package com.example.xoxarena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private char[][] board = new char[3][3]; // '\0' = empty, 'X' or 'O'
    private boolean playerTurn = true; // true = player, false = bot
    private int roundCount;

    private TextView status;
    private Button resetButton, pauseButton;

    private String mode = "bot";    // default mode
    private char playerSymbol = 'X';
    private char opponentSymbol = 'O';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.statusText);
        resetButton = findViewById(R.id.resetButton);
        pauseButton = findViewById(R.id.pauseButton);

        // Pause button shows confirmation dialog instead of closing immediately
        pauseButton.setOnClickListener(view -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Pause")
                    .setMessage("Do you want to exit to mode selection?")
                    .setPositiveButton("Exit", (dialog, which) -> {
                        startActivity(new Intent(MainActivity.this, ModeSelectActivity.class));
                        finish();
                    })
                    .setNegativeButton("Resume", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Get intent extras if any
        Intent intent = getIntent();
        if (intent != null) {
            String modeExtra = intent.getStringExtra("mode");
            String symbolExtra = intent.getStringExtra("symbol");

            if (modeExtra != null) mode = modeExtra;
            if (symbolExtra != null && symbolExtra.length() > 0) {
                playerSymbol = symbolExtra.charAt(0);
                opponentSymbol = (playerSymbol == 'X') ? 'O' : 'X';
                playerTurn = (playerSymbol == 'X'); // X starts first
            }
        }

        if (mode.equals("player")) {
            status.setText("Multiplayer Mode");
        } else {
            status.setText("VS Bot Mode");
        }

        // Initialize board and buttons
        resetBoard();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("button" + i + j, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                final int row = i;
                final int col = j;

                buttons[i][j].setOnClickListener(view -> {
                    if (!buttons[row][col].getText().toString().equals("")) return;
                    if (mode.equals("bot") && !playerTurn) return; // wait for bot

                    char currentSymbol = playerTurn ? playerSymbol : opponentSymbol;

                    buttons[row][col].setText(String.valueOf(currentSymbol));
                    buttons[row][col].setTextColor(ContextCompat.getColor(this,
                            playerTurn ? android.R.color.holo_red_light : android.R.color.white));
                    board[row][col] = currentSymbol;

                    roundCount++;

                    if (checkWin(currentSymbol)) {
                        status.setText(currentSymbol + " Wins!");
                        disableAllButtons();
                        return;
                    }

                    if (roundCount == 9) {
                        status.setText("Draw!");
                        return;
                    }

                    playerTurn = !playerTurn;

                    if (mode.equals("bot") && !playerTurn) {
                        botMove();
                    }
                });
            }
        }

        resetButton.setOnClickListener(view -> resetGame());

        // Bot starts immediately if player is 'O'
        if (mode.equals("bot") && !playerTurn) {
            botMove();
        }
    }

    private void botMove() {
        int[] bestMove = findBestMove();
        if (bestMove[0] == -1) {
            status.setText("Draw!");
            return;
        }

        buttons[bestMove[0]][bestMove[1]].setText(String.valueOf(opponentSymbol));
        buttons[bestMove[0]][bestMove[1]].setTextColor(ContextCompat.getColor(this, android.R.color.white));
        board[bestMove[0]][bestMove[1]] = opponentSymbol;
        roundCount++;

        if (checkWin(opponentSymbol)) {
            status.setText("Bot Wins!");
            disableAllButtons();
            return;
        }

        if (roundCount == 9) {
            status.setText("Draw!");
            return;
        }

        playerTurn = true;
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    board[i][j] = opponentSymbol;
                    int score = minimax(0, false);
                    board[i][j] = '\0';
                    if (score > bestScore) {
                        bestScore = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        return move;
    }

    private int minimax(int depth, boolean isMaximizing) {
        if (checkWin(opponentSymbol)) return 10 - depth;
        if (checkWin(playerSymbol)) return depth - 10;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = opponentSymbol;
                        best = Math.max(best, minimax(depth + 1, false));
                        board[i][j] = '\0';
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = playerSymbol;
                        best = Math.min(best, minimax(depth + 1, true));
                        board[i][j] = '\0';
                    }
                }
            }
            return best;
        }
    }

    private boolean checkWin(char symbol) {
        // rows and columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol)
                return true;
            if (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol)
                return true;
        }
        // diagonals
        if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol)
            return true;
        if (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol)
            return true;

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == '\0')
                    return false;
        return true;
    }

    private void disableAllButtons() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setEnabled(false);
    }

    private void resetBoard() {
        roundCount = 0;
        playerTurn = (playerSymbol == 'X');
        status.setText(mode.equals("player") ? "Multiplayer Mode" : "VS Bot Mode");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '\0';
                if (buttons[i][j] != null) {
                    buttons[i][j].setText("");
                    buttons[i][j].setEnabled(true);
                }
            }
        }
    }

    private void resetGame() {
        resetBoard();

        if (mode.equals("bot") && !playerTurn) {
            botMove();
        }
    }
}
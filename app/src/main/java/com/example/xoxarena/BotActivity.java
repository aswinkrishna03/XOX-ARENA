package com.example.xoxarena;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class BotActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private char[][] board = new char[3][3]; // 'X', 'O' or '\0' for empty
    private boolean playerTurn = true;
    private char playerSymbol = 'X';
    private char botSymbol = 'O';
    private TextView textViewTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        textViewTurn = findViewById(R.id.text_view_turn);

        Intent intent = getIntent();
        playerSymbol = intent.getStringExtra("playerSymbol").charAt(0);
        botSymbol = (playerSymbol == 'X') ? 'O' : 'X';

        textViewTurn.setText("Turn: " + playerSymbol);

        // Initialize board to empty
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                board[i][j] = '\0';

        // Setup buttons and listeners
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int resID = getResources().getIdentifier("button_" + i + j, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                final int row = i;
                final int col = j;
                buttons[i][j].setOnClickListener(view -> {
                    if (board[row][col] != '\0' || !playerTurn) return;

                    playerMove(row, col);
                });
            }
        }

        if (playerSymbol == 'O') {
            playerTurn = false;
            botMove();
        }
    }

    private void playerMove(int row, int col) {
        board[row][col] = playerSymbol;
        buttons[row][col].setText(String.valueOf(playerSymbol));
        textViewTurn.setText("Turn: " + botSymbol);

        if (checkWin(playerSymbol)) {
            showResult("You Win!");
            return;
        }
        if (isBoardFull()) {
            showResult("Draw!");
            return;
        }

        playerTurn = false;
        botMove();
    }

    private void botMove() {
        int[] bestMove = findBestMove();
        if (bestMove[0] == -1) {
            showResult("Draw!");
            return;
        }

        board[bestMove[0]][bestMove[1]] = botSymbol;
        buttons[bestMove[0]][bestMove[1]].setText(String.valueOf(botSymbol));
        textViewTurn.setText("Turn: " + playerSymbol);

        if (checkWin(botSymbol)) {
            showResult("Bot Wins!");
            return;
        }
        if (isBoardFull()) {
            showResult("Draw!");
            return;
        }

        playerTurn = true;
    }

    private boolean checkWin(char symbol) {
        // rows and columns
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) return true;
            if (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol) return true;
        }
        // diagonals
        if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) return true;
        if (board[0][2] == symbol && board[1][1] == symbol && board[2][0] == symbol) return true;

        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == '\0')
                    return false;
        return true;
    }

    private void showResult(String message) {
        new AlertDialog.Builder(this)
                .setTitle(message)
                .setMessage("Play again?")
                .setPositiveButton("Yes", (dialog, which) -> resetGame())
                .setNegativeButton("No", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '\0';
                buttons[i][j].setText("");
            }
        }
        playerTurn = (playerSymbol == 'X');
        textViewTurn.setText("Turn: " + (playerTurn ? playerSymbol : botSymbol));

        if (!playerTurn) botMove();
    }

    private int[] findBestMove() {
        int bestScore = Integer.MIN_VALUE;
        int[] move = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') {
                    board[i][j] = botSymbol;
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
        if (checkWin(botSymbol)) return 10 - depth;
        if (checkWin(playerSymbol)) return depth - 10;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = botSymbol;
                        int eval = minimax(depth + 1, false);
                        board[i][j] = '\0';
                        maxEval = Math.max(maxEval, eval);
                    }
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '\0') {
                        board[i][j] = playerSymbol;
                        int eval = minimax(depth + 1, true);
                        board[i][j] = '\0';
                        minEval = Math.min(minEval, eval);
                    }
                }
            }
            return minEval;
        }
    }
}

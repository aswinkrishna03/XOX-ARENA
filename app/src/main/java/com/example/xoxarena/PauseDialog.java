package com.example.xoxarena;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;

public class PauseDialog extends Dialog {
    public PauseDialog(Context context, Runnable onRestart, Runnable onExit) {
        super(context);
        setContentView(R.layout.pausedialog);
        setCancelable(true);

        Button resume = findViewById(R.id.resume);
        Button restart = findViewById(R.id.restart);
        Button exit = findViewById(R.id.exit);

        resume.setOnClickListener(v -> dismiss());
        restart.setOnClickListener(v -> {
            dismiss();
            onRestart.run();
        });
        exit.setOnClickListener(v -> {
            dismiss();
            onExit.run();
        });
    }
}

package com.llg.pingtu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.llg.pingtu.view.GameLayout;

public class MainActivity extends AppCompatActivity {

    private GameLayout mGameLayout;
    private TextView levelTv;
    private TextView timeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameLayout = (GameLayout) findViewById(R.id.game_view);
        levelTv = (TextView) findViewById(R.id.level_view);
        timeTv = (TextView) findViewById(R.id.time_view);

        mGameLayout.setPlayGameListener(new GameLayout.PlayGameListener() {
            @Override
            public void nextLevel() {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Game Info")
                        .setMessage("LEVEL UP!!!")
                        .setPositiveButton("Next Level", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGameLayout.nextLevel();
                            }
                        }).setCancelable(false).show();
            }

            @Override
            public void UIChange(int currentTime,int level ) {
                timeTv.setText("time:"+currentTime);
                levelTv.setText("level:"+level);
            }

            @Override
            public void gameOver() {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Game Info")
                        .setMessage("GAME OVER!!!")
                        .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               mGameLayout.restartGame();
                            }
                        }).setNegativeButton("QUIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                }).show();

            }

        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameLayout.pauseGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameLayout.resumeGame();
    }
}

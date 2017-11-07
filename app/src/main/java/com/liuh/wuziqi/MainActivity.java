package com.liuh.wuziqi;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private WuziqiPanel wuziqiPanel;
    private Button restartGame;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wuziqiPanel = (WuziqiPanel) findViewById(R.id.id_wuzipanel);
        restartGame = (Button) findViewById(R.id.btn_restartgame);


        wuziqiPanel.setGameOverListener(new GameOverListener() {
            @Override
            public void gameover(String text) {
                alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage(text)
                        .setNegativeButton("再来一局", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                wuziqiPanel.restartGame();
                            }
                        })
                        .setPositiveButton("不玩了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        });

        restartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wuziqiPanel.restartGame();
            }
        });
    }
}

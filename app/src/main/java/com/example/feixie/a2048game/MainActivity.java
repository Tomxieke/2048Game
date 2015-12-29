package com.example.feixie.a2048game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity implements GameView.ScoreInter{
    private TextView scoreTxt, maxScoreText;
    private GameView mGameView;
    private int currentScore;  //当前得分
    private int maxScore = 0;  //最高得分
    private SharedPreferences maxPreferences;  //保存最高得分




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        maxPreferences = getSharedPreferences("2048game.SharedPreferences", Context.MODE_PRIVATE);
        scoreTxt = (TextView) findViewById(R.id.score_text);
        maxScoreText = (TextView) findViewById(R.id.max_scores_text);
        mGameView = (GameView) findViewById(R.id.gameView);
        mGameView.regasitScoreInter(this);
        clearScore();

    }


    public void clearScore(){
        currentScore = 0;
        showScore();
    }

    public void showScore(){
        scoreTxt.setText("当前得分:  "+ currentScore);
        compareScore();  //记录最高得分
    }

    public void addScore(int s){
        currentScore += s;
        showScore();
    }


    /**
     * 比较得分得出最高得分并保存至shareparence
     */
    private void compareScore(){
        maxScore = maxPreferences.getInt("MAX_SCORE",0);
        SharedPreferences.Editor editor = maxPreferences.edit();
        if (currentScore >= maxScore){
            maxScore = currentScore;
            editor.putInt("MAX_SCORE",maxScore);
            editor.commit();
        }
        maxScoreText.setText("最高得分:  " + maxScore);

    }

    @Override
    public void getScore(int s) {
        if (s == -1){ //表示游戏结束，该重来了。将分数清零
            clearScore();
        }else{
            addScore(s);
        }
    }

    @Override
    public void exitGame() {
        finish();
    }
}

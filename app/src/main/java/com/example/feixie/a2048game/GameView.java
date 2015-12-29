package com.example.feixie.a2048game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xieke on 2015/12/13.
 */
public class GameView extends GridLayout{

    public GameView(Context context) {
        super(context);
        initView();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView(){
        setColumnCount(4);  //设置多少列
        setBackgroundColor(getResources().getColor(R.color.gameview_bg));
        setOnTouchListener(new OnTouchListener() {
            private float startX,startY,offsetX,offsetY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        offsetX = event.getX()-startX;
                        offsetY = event.getY()-startY;

                        if (Math.abs(offsetX) > Math.abs(offsetY)){  //如果X轴上的变量大于Y  则是左右滑动。i
                            if(offsetX < -5){
                            //    Toast.makeText(getContext(),"向左",Toast.LENGTH_SHORT).show();
                                swipeLeft();
                            }else if (offsetX > 5){
                             //   Toast.makeText(getContext(), "向右", Toast.LENGTH_SHORT).show();
                                swipeRight();
                            }

                        }else{
                            if (offsetY < -5){
                             //   Toast.makeText(getContext(), "向上", Toast.LENGTH_SHORT).show();
                                swipeUp();
                            }else if (offsetY > 5){
                             //   Toast.makeText(getContext(), "向下", Toast.LENGTH_SHORT).show();
                                swipeDown();
                            }
                        }

                        break;
                }

                return true;
            }
        });
    }


    @Override  //当布局改变时，返回宽高。
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int cardWegth = (Math.min(w,h)-30)/4;
        addCard(cardWegth,cardWegth);

        startGame();  //游戏开始
    }

    private void addCard(int cardW,int cardH){
        Card c;
        for (int y = 0;y < 4;y++){
            for (int x = 0;x<4;x++){
               c = new Card(getContext());
                c.setNum(0);
                addView(c,cardH,cardW);
                cards[x][y] = c;

            }
        }
    }

    private List<Point> emptyPaints = new ArrayList<>();  //记录空点的位置
    private Card[][] cards = new Card[4][4];  //保存每个位置上的数字



    /**
     * 随机产生2或者4
     */
    private void addRandomNum(){
        emptyPaints.clear();
        for (int y = 0;y<4;y++){
            for (int x=0;x<4;x++){
                if (cards[x][y].getNum() <= 0){
                        emptyPaints.add(new Point(x,y));
                }
            }
        }

        Point p = emptyPaints.remove((int)(Math.random()*emptyPaints.size()));
        cards[p.x][p.y].setNum(Math.random()>0?2:4); //按9:1的比例返回4或者是2
    }

    /**
     * 开始游戏
     */
    private void startGame(){
        scoreInter.getScore(-1);
        for (int y = 0;y<4;y++){
            for (int x = 0;x <4;x++){
                cards[x][y].setNum(0);  //开始时将所有空格设置为0
            }
        }

        //然后再添加两个数字
        addRandomNum();
        addRandomNum();

    }

    /**
     * 游戏结束逻辑
     */
    private void gameOver(){
        boolean over = true;
        ALL:
        for (int y = 0;y<4;y++){
            for (int x = 0;x<4;x++){
                if (cards[x][y].getNum()==0 ||    //有空位子或者是周围存在相同的数字时。游戏都没有结束
                        (x>0&&cards[x][y].equals(cards[x-1][y]))||
                        (x<3&&cards[x][y].equals(cards[x+1][y]))||
                        (y>0&&cards[x][y].equals(cards[x][y-1]))||
                        (y<3&&cards[x][y].equals(cards[x][y+1]))){
                    over = false;
                    break ALL;
                }
            }
        }
        if (over){
            new AlertDialog.Builder(getContext()).setTitle("你好").setMessage("游戏结束")
                  .setPositiveButton("重来开始", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          startGame();
                      }
                  }).setNegativeButton("退出游戏", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            scoreInter.exitGame();  //结束游戏
                        }
            }).show();
        }
    }

    private void swipeLeft(){

        boolean falg = false;

        for (int y=0;y<4;y++){
            for (int x=0;x<4;x++){

                for (int x1=x+1;x1<4;x1++){
                    if (cards[x1][y].getNum() >0){  //说明当前位置右边是有数字的

                        if (cards[x][y].getNum()<=0){  //当前位置无数字时
                            cards[x][y].setNum(cards[x1][y].getNum());  //把右边的数字设置给他
                            cards[x1][y].setNum(0);
                            x--;  //多循环一次，防止当前位置右边有两个相等数组时滑动后不会加在一起
                            falg = true;
                        }else if (cards[x][y].equals(cards[x1][y])) {
                            cards[x][y].setNum(cards[x][y].getNum() * 2);
                            cards[x1][y].setNum(0);
                            scoreInter.getScore(cards[x][y].getNum());  //将得分传给Activity显示
                            falg = true;
                        }
                        break;
                    }
                }
            }
        }

        if (falg ){
            addRandomNum(); //每次随机添加数字之后都去判断是否游戏结束
            gameOver();//判断游戏是否结束
        }

    }

    private void swipeRight(){
        boolean falg = false;
        for (int y=0;y<4;y++){
            for (int x=3;x>=0;x--){

                for (int x1=x-1;x1>=0;x1--){
                    if (cards[x1][y].getNum() >0){  //说明当前位置右边是有数字的

                        if (cards[x][y].getNum()<=0){  //当前位置无数字时
                            cards[x][y].setNum(cards[x1][y].getNum());  //把右边的数字设置给他
                            cards[x1][y].setNum(0);
                            x++;  //多循环一次，防止当前位置右边有两个相等数组时滑动后不会加在一起
                            falg = true;
                        }else if (cards[x][y].equals(cards[x1][y])){
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x1][y].setNum(0);
                            scoreInter.getScore(cards[x][y].getNum());
                            falg = true;
                        }
                        break;
                    }
                }
            }
        }
        if (falg){
            addRandomNum();
            gameOver();
        }

    }

    private  void swipeUp(){
        boolean flag = false;
        for (int x=0;x<4;x++){
            for (int y=0;y<4;y++){

                for (int y1=y+1;y1<4;y1++){
                    if (cards[x][y1].getNum() >0){  //说明当前位置右边是有数字的

                        if (cards[x][y].getNum()<=0){  //当前位置无数字时
                            cards[x][y].setNum(cards[x][y1].getNum());  //把右边的数字设置给他
                            cards[x][y1].setNum(0);
                            y--;  //多循环一次，防止当前位置右边有两个相等数组时滑动后不会加在一起
                            flag= true;
                        }else if (cards[x][y].equals(cards[x][y1])){
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x][y1].setNum(0);
                            scoreInter.getScore(cards[x][y].getNum());
                            flag = true;
                        }
                        break;
                    }
                }
            }
        }
        if (flag){
            addRandomNum();
            gameOver();
        }

    }

    private void swipeDown(){
        boolean flag = false;
        for (int x=0;x<4;x++){
            for (int y=3;y>=0;y--){

                for (int y1=y-1;y1>=0;y1--){
                    if (cards[x][y1].getNum() >0){  //说明当前位置右边是有数字的

                        if (cards[x][y].getNum()<=0){  //当前位置无数字时
                            cards[x][y].setNum(cards[x][y1].getNum());  //把右边的数字设置给他
                            cards[x][y1].setNum(0);
                            y++;  //多循环一次，防止当前位置右边有两个相等数组时滑动后不会加在一起
                            flag = true;
                        }else if (cards[x][y].equals(cards[x][y1])){
                            cards[x][y].setNum(cards[x][y].getNum()*2);
                            cards[x][y1].setNum(0);
                            scoreInter.getScore(cards[x][y].getNum());
                            flag = true;
                        }
                        break;
                    }
                }
            }
        }

        if (flag){
            addRandomNum();
            gameOver();
        }

    }

    private ScoreInter scoreInter;

    public void regasitScoreInter(ScoreInter s){
        this.scoreInter = s;
    }
    /**
     * 传递分数过来的接口
     */
    interface ScoreInter{
        public void getScore(int s);
        public void exitGame();
    }
}

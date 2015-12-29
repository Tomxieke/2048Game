package com.example.feixie.a2048game;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by xieke on 2015/12/13.
 */
public class Card extends FrameLayout {
    private int num = 0;
    private TextView lable;
    public Card(Context context) {
        super(context);
        //setNum(0);
        lable = new TextView(context);
        lable.setTextSize(40);
        lable.setGravity(Gravity.CENTER);
        lable.setBackgroundColor(getResources().getColor(R.color.card_bg));
        LayoutParams params = new LayoutParams(-1,-1);  // 决定布局文件的某些参数，是个属性集合
        params.setMargins(30,30,0,0);  //设置间隙

        addView(lable,params);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        if (num <= 0){
            lable.setText("");
        }else {
            lable.setText(num+"");
        }
    }

    public boolean equals(Card o) {
        return getNum() == o .getNum();
    }


}

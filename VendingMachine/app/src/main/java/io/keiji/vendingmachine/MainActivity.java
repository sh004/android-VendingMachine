package io.keiji.vendingmachine;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private TextView en, cash, lamp;
    private ImageView lever, coin, returnCoin, outputItem;
    private Button button1, button2, button3;
    private DragViewListener listener;
    private ImageView[] dragImage = new ImageView[4];
    private int[] dragImageId = new int[]{R.id.coin500, R.id.coin100, R.id.coin50, R.id.coin10};
    private GridLayout MainLayout;
//    private int dragLocalX;
//    private int dragLocalY;
//    private int screenX;
//    private int screenY;


    Timer timer = null;
    Handler handle = new Handler();
    String itemName;                        //商品名
    Integer pay, change, itemPrice;         //支払い金額, おつり
    boolean hasPut, hasBought, makeItem;    //判定：お金を投入したか, 購入したか, 作成中か

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        en = (TextView) findViewById(R.id.en);                      //円マーク
        cash = (TextView) findViewById(R.id.cash);                  //金額表示
        lever = (ImageView) findViewById(R.id.lever);               //レバー
        coin = (ImageView) findViewById(R.id.coin);                 //硬貨投入口
        returnCoin = (ImageView) findViewById(R.id.returnCoin);     //硬貨返却口
        outputItem = (ImageView) findViewById(R.id.outputItem);     //商品取出口
        lamp = (TextView) findViewById(R.id.lamp);                  //ランプ
        button1 = (Button) findViewById(R.id.button1);              //ハンバーガー
        button2 = (Button) findViewById(R.id.button2);              //チーズバーガー
        button3 = (Button) findViewById(R.id.button3);              //ダブルチーズバーガー

        change = 0;                                                 //おつり
        makeItem = true;                                            //作成中かどうか
        timer = new Timer();                                        //作成中のタイマー

//        for(int i = 0; i < 4; i++) {
//            dragImage[i].setOnTouchListener(this);
//        }
//        coin = (ImageView)findViewById(R.id.coin);
//        coin.setOnTouchListener(this);

        //仮設定
        hasPut = true;
        pay = 250;
        cash.setText(Integer.toString(pay));


        //硬貨投入
        for (int i = 0; i < 4; i++) {
            dragImage[i] = (ImageView)this.findViewById(dragImageId[i]);
            ImageView dragView = dragImage[i];
            listener = new DragViewListener(dragView);
            dragView.setOnTouchListener(listener);
        }
        en.setTextColor(Color.rgb(203, 64, 66));

        //レバー
        lever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasPut = false;
                change += pay;
                pay = 0;
                cash.setText("");
                en.setTextColor(Color.rgb(28, 28, 28));
                Toast.makeText(MainActivity.this, "返却レバーを引きました。", Toast.LENGTH_SHORT).show();
            }
        });

        //硬貨返却口
        returnCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (change > 0) {
                    Toast.makeText(MainActivity.this, change + "円が返却されました。", Toast.LENGTH_SHORT).show();
                    change = 0;
                } else {
                    Toast.makeText(MainActivity.this, "おつりはありません。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //商品取出口
        outputItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasBought == true) {
                    if (makeItem == true) {
                        Toast.makeText(MainActivity.this, itemName + "を購入しました。", Toast.LENGTH_SHORT).show();
                        hasBought = false;
                    } else {
                        Toast.makeText(MainActivity.this, "しばらくお待ちください。", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "商品を購入してください。", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //ハンバーガー
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPut == true) {
                    itemName = "ハンバーガー";
                    itemPrice = 200;
                    buy();
                } else {
                    Toast.makeText(MainActivity.this, "お金を入れてください。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //チーズバーガー
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPut == true) {
                    itemName = "チーズバーガー";
                    itemPrice = 250;
                    buy();
                } else {
                    Toast.makeText(MainActivity.this, "お金を入れてください。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //ダブルチーズバーガー
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPut == true) {
                    itemName = "ダブルチーズバーガー";
                    itemPrice = 370;
                    buy();
                } else {
                    Toast.makeText(MainActivity.this, "お金を入れてください。", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //購入処理
    private void buy() {
        hasBought = true;

        if (makeItem == true) {
            if (pay >= itemPrice) {
                makeItem = false;
                lamp.setTextColor(Color.rgb(203, 64, 66));
                timer.schedule(new MakeTimer(), 5000);        //5秒

                change = pay - itemPrice;
                pay = 0;
                cash.setText("");
                en.setTextColor(Color.rgb(28, 28, 28));
                hasPut = false;
            } else {
                Toast.makeText(MainActivity.this, itemPrice - pay + "円足りません。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //調理完了処理
    class MakeTimer extends TimerTask {
        @Override
        public void run() {
            handle.post(new Runnable() {
                @Override
                public void run() {
                    lamp.setTextColor(Color.rgb(120, 120, 120));
                    makeItem = true;
                }
            });
        }
    }

    //硬貨のドラッグ
    public class DragViewListener implements OnTouchListener {
        private ImageView COIN;

        //ドラッグ中に移動量を取得するための変数
        private int oldx;
        private int oldy;

        public DragViewListener(ImageView dragImage) {
            this.COIN = dragImage;
        }

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            MainLayout = (GridLayout)findViewById(R.id.MainLayout);
            int left = 0, top = 0;

            // タッチしている位置取得
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    left = COIN.getLeft() + (x - oldx);
                    top = COIN.getTop() + (y - oldy);

                    COIN.layout(left, top, left + COIN.getWidth(), top + COIN.getHeight());
                    break;

                case MotionEvent.ACTION_UP:
                    int coinPlusLeft = coin.getLeft() + coin.getWidth()/2;
                    int coinPlusTop = coin.getTop()  + coin.getHeight()/2;
                    int targetRight = coin.getLeft() + coin.getWidth();
                    int targetBottom = coin.getTop() + coin.getHeight();

                    if (targetRight > coinPlusLeft && targetBottom > coinPlusTop) {
                        MainLayout.removeView(coin);
                    }
                    break;
            }

            //今回のタッチ位置を保持
            oldx = x;
            oldy = y;

            return true;


//            int x = (int)event.getRawX();
//            int y = (int)event.getRawY();
//
//            switch(event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//
//                    dragLocalX = COIN.getLeft();
//                    dragLocalY = COIN.getTop();
//
//                    screenX = x;
//                    screenY = y;
//
//                    break;
//
//                case MotionEvent.ACTION_MOVE:
//
//                    int diffX = screenX - x;
//                    int diffY = screenY - y;
//
//                    dragLocalX -= diffX;
//                    dragLocalY -= diffY;
//
//                    COIN.layout(dragLocalX,
//                            dragLocalY,
//                            dragLocalX + COIN.getWidth(),
//                            dragLocalY + COIN.getHeight());
//
//                    screenX = x;
//                    screenY = y;
//
//                    break;
//
//                case MotionEvent.ACTION_UP:
//
//                    int coinLeft    = coin.getLeft() + coin.getWidth()/2;
//                    int dragRight  = coin.getLeft() + coin.getWidth();
//                    int coinTop     = coin.getTop()  + coin.getHeight()/2;
//                    int dragBottom = coin.getTop() + coin.getHeight();
//
//                    //ドロップ時の処理
//                    if (dragRight > coinLeft && dragBottom > coinTop) {
//                        MainLayout.removeView(COIN);
//                    }
//                    break;
//            }
//            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}

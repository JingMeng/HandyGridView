package com.handygridview.example;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huxq17.handygridview.HandyGridView;
import com.huxq17.handygridview.listener.IDrawer;
import com.huxq17.handygridview.listener.OnItemCapturedListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    private HandyGridView mGridView;
    private List<String> strList;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextView enableSelectorTv, changeModeTv, addTagTv;
    private Button recoveryTagTv;
    private GridViewAdapter adapter;
    private ViewGroup outLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void initView() {
        enableSelectorTv = findViewById(R.id.text_enable_selector);
        changeModeTv = findViewById(R.id.text_change_mode);
        addTagTv = findViewById(R.id.text_add_tag);
        recoveryTagTv = findViewById(R.id.text_recovery_tag);
        outLayout = findViewById(R.id.out_layout);

        mGridView = findViewById(R.id.grid_tips);
        adapter = new GridViewAdapter(this, strList);
        mGridView.setAdapter(adapter);

        setMode(HandyGridView.MODE.LONG_PRESS);
        mGridView.setAutoOptimize(false);
        //褰揼ridview鍙互婊氬姩骞朵笖琚嫋鍔ㄧ殑item浣嶄簬gridview鐨勯《閮ㄦ垨鑰呭簳閮ㄦ椂锛岃缃甮ridview婊氬睆鐨勯€熷害锛?
        // 姣忕绉诲姩鐨勫儚绱犵偣涓暟锛岄粯璁?50锛屽彲涓嶈缃€?
        mGridView.setScrollSpeed(750);
//        adapter.notifyDataSetChanged();
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mGridView.isTouchMode()&&!mGridView.isNoneMode() && !adapter.isFixed(position)) {//long press enter edit mode.
                    setMode(HandyGridView.MODE.TOUCH);
                    return true;
                }
                return false;
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "click item at " + position, Toast.LENGTH_SHORT).show();
            }
        });
        mGridView.setOnItemCapturedListener(new OnItemCapturedListener() {
            @Override
            public void onItemCaptured(View v, int position) {
                v.setScaleX(1.2f);
                v.setScaleY(1.2f);
            }

            @Override
            public void onItemReleased(View v, int position) {
                v.setScaleX(1f);
                v.setScaleY(1f);
            }

        });
        mGridView.setDrawer(new IDrawer() {
            @Override
            public void onDraw(Canvas canvas, int width, int height) {
                if (!mGridView.isNoneMode()) {
                    int offsetX = -DensityUtil.dip2px(MainActivity.this, 10);
                    int offsetY = -DensityUtil.dip2px(MainActivity.this, 10);
                    //鏂囧瓧缁樺埗浜巊ridview鐨勫彸涓嬭锛屽苟鍚戝乏锛屽悜涓婂亸绉?0dp銆?
                    drawTips(canvas, width + offsetX, height + offsetY);
                }
            }
        },false);
        enableSelectorTv.setOnClickListener(this);
//        enableSelectorTv.performClick();
        changeModeTv.setOnClickListener(this);
        addTagTv.setOnClickListener(this);
        recoveryTagTv.setOnClickListener(this);
    }

    String paintText = "闀挎寜鎺掑簭鎴栧垹闄?;
    int textWidth;
    int textHeight;
    StaticLayout tipsLayout;
    private TextPaint mTextPaint;

    private void drawTips(Canvas canvas, int width, int height) {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint();
            mTextPaint.setColor(Color.parseColor("#cccccc"));
            mTextPaint.setTextSize(DensityUtil.dip2px(MainActivity.this, 12));
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            textHeight = (int) (fontMetrics.bottom - fontMetrics.top) + 1;
            textWidth = (int) mTextPaint.measureText(paintText) + 1;
        }
        width = width - textWidth;
        height = height - textHeight;
        if (tipsLayout == null) {
            tipsLayout = new StaticLayout(paintText, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.5f, 0f, false);
        }
        canvas.translate(width, height);
        tipsLayout.draw(canvas);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.text_enable_selector:
                boolean isSelectorEnabled = mGridView.isSelectorEnabled();
                //寤鸿浼犲叆false,涓嶈寮€鍚痵elector,涓嶇劧item绉诲姩浠ュ悗锛屽湪item鍘熸潵鐨勪綅缃細鏈夋畫褰遍€愭笎娑堝け鐨勬晥鏋溿€?
                //榛樿鏄痜alse銆?
                mGridView.setSelectorEnabled(!isSelectorEnabled);
                enableSelectorTv.setText(!isSelectorEnabled ? "selector宸插紑鍚? : "selector宸插叧闂?);
                break;
            case R.id.text_change_mode:
                HandyGridView.MODE curMode = mGridView.getMode();
                int index = HandyGridView.MODE.indexOf(curMode);
                index = (index + 1) % HandyGridView.MODE.values().length;
                //鏈変笁绉嶆ā寮忓垎鍒槸鐐瑰嚮鎷栧姩锛岄暱鎸夋嫋鍔ㄥ拰涓嶆嫋鍔紝鍒嗗埆瀵瑰簲鐨勬槸TOUCH,LONG_PRESS鍜孨ONE
                setMode(HandyGridView.MODE.get(index));
                break;
            case R.id.text_add_tag:
                addTag();
                break;
            case R.id.text_recovery_tag:
//                moveTaskToBack(true);
                recoveryTag();
                break;
        }
    }

    private void recoveryTag() {
        for (Iterator<String> iter = strList.iterator(); iter.hasNext(); ) {
            String str = iter.next();
            if (str.contains("鏇村")) {
                iter.remove();
            }
        }
        setMode(HandyGridView.MODE.LONG_PRESS);
       adapter.setData(strList);
        outLayout.setClipChildren(false);
    }

    private void addTag() {
        for (int i = 0; i < 4; i++) {
            strList.add("鏇村 "+(strList.size()-1));
        }
        adapter.setData(strList);
        outLayout.setClipChildren(true);
        mGridView.smoothScrollToPosition(adapter.getCount() - 1);
    }

    private void setMode(HandyGridView.MODE mode) {
        mGridView.setMode(mode);
        changeModeTv.setText(mode.toString());
        adapter.setInEditMode(mode == HandyGridView.MODE.TOUCH);
    }

    private void log(String msg) {
        Log.e(getClass().getCanonicalName(), msg);
    }

    public void initData() {
        strList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            strList.add("ITEM " + i);
//        }
        strList.add("澶存潯");
        strList.add("杞绘澗涓€鍒?);
        strList.add("瑙嗛");
        strList.add("濞变箰");
        strList.add("娈靛瓙");
        strList.add("璺熷笘");
        strList.add("娲诲姏瀛﹂櫌");
        strList.add("鍖椾含");
        strList.add("绀句細");
        strList.add("鍐涗簨");
        strList.add("鐑偣");
        strList.add("鐩存挱");
        strList.add("缃戞槗鍙?);
        strList.add("鎴夸骇");
    }

}


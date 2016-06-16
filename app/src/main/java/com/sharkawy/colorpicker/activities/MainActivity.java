package com.sharkawy.colorpicker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sharkawy.colorpicker.R;
import com.sharkawy.colorpicker.dataModel.Image;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    ImageView imageView ,colorEx ;
//    TextView textcolor ;
    Bitmap GlideBitmap ;
    TextView _view ;
    ViewGroup _root;
    private int _xDelta;
    private int _yDelta;
    RelativeLayout Box ;
    float dX, dY;
    EditText RValue,GValue,BValue ,HexaValue ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setWidgetReference();
        bindEventHandlers();

        if(getIntent()!=null){
            String url =getIntent().getStringExtra("Image");

            Glide.with(getApplicationContext())
                    .load(url)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>(100,100) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                            imageView.setImageBitmap(resource); // Possibly runOnUiThread()
                        }
                    });

        }
        imageView.setImageResource(R.drawable.joy);
//        imageView.setImageBitmap(GlideBitmap);

        _root = (ViewGroup)findViewById(R.id.root);
        Box = (RelativeLayout) findViewById(R.id.BOX);

        Box.setOnTouchListener(this);
//        _root.addView(Box);

    }

    private void PickColor( View view, MotionEvent event) {

//        ImageView tar = (ImageView) v ;
        float eventX = event.getX();
        float eventY = event.getY();
        float[] eventXY = new float[] {eventX, eventY};

//        Matrix invertMatrix = new Matrix();
//        tar.getImageMatrix().invert(invertMatrix);

//        invertMatrix.mapPoints(eventXY);
        int x = Integer.valueOf((int)eventXY[0]);
        int y = Integer.valueOf((int)eventXY[1]);

//        Drawable imgDrawable = tar.getDrawable();
//        Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

//        Limit x, y range within bitmap

//

        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if(x < 0){
            x = 0;
        }else if(x > bitmap.getWidth()-1){
            x = bitmap.getWidth()-1;
        }

        if(y < 0){
            y = 0;
        }else if(y > bitmap.getHeight()-1){
            y = bitmap.getHeight()-1;
        }
        int touchedRGB = bitmap.getPixel(x, y);
        int redValue = Color.red(touchedRGB);
        int blueValue = Color.blue(touchedRGB);
        int greenValue = Color.green(touchedRGB);
//        view.setDrawingCacheEnabled(false);

        RValue.setText(redValue+"");
        GValue.setText(greenValue+"");
        BValue.setText(blueValue+"");
        HexaValue.setText("#" + Integer.toHexString(touchedRGB));
        HexaValue.setTextColor(touchedRGB);

//        Toast.makeText(MainActivity.this,"RED : "+redValue+", BLUE : "+blueValue+" , GREEN : "+greenValue, Toast.LENGTH_SHORT).show();

    }

    private void bindEventHandlers() {
        imageView.setOnTouchListener(this);
    }

    private void setWidgetReference() {
        imageView = (ImageView) findViewById(R.id.imageView);
        colorEx = (ImageView) findViewById(R.id.colorEx);
        RValue = (EditText) findViewById(R.id.rValue);
        GValue = (EditText) findViewById(R.id.gValue);
        BValue = (EditText) findViewById(R.id.bValue);
        HexaValue = (EditText) findViewById(R.id.HexaValue);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v==imageView){
            UpdateColor(v, event);
        }else if(v==Box){
            MoveTarget(v, event);
        }
        return true;
    }

    private void MoveTarget(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                dX = v.getX() - event.getRawX();
                dY = v.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                v.animate()
                        .x(event.getRawX() + dX)
                        .y(event.getRawY() + dY)
                        .setDuration(0)
                        .start();
                break;
            default:
                UpdateColor(v,event);
                break;
        }
    }

    private void UpdateColor(View v, MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        float[] eventXY = new float[] {eventX, eventY};

        int x = Integer.valueOf((int)eventXY[0]);
        int y = Integer.valueOf((int)eventXY[1]);

        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        if(x < 0){
            x = 0;
        }else if(x > bitmap.getWidth()-1){
            x = bitmap.getWidth()-1;
        }

        if(y < 0){
            y = 0;
        }else if(y > bitmap.getHeight()-1){
            y = bitmap.getHeight()-1;
        }
        int touchedRGB = bitmap.getPixel(x, y);
        int redValue = Color.red(touchedRGB);
        int blueValue = Color.blue(touchedRGB);
        int greenValue = Color.green(touchedRGB);
//        view.setDrawingCacheEnabled(false);

        RValue.setText(redValue+"");
        GValue.setText(greenValue+"");
        BValue.setText(blueValue+"");
        HexaValue.setText("#" + Integer.toHexString(touchedRGB));
        HexaValue.setTextColor(touchedRGB);
        colorEx.setBackgroundColor(touchedRGB);
    }
}

package com.sharkawy.colorpicker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    ImageView imageView ;
    TextView textcolor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.joy);
        textcolor = (TextView) findViewById(R.id.textColor);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                float eventX = event.getX();
                float eventY = event.getY();
                float[] eventXY = new float[] {eventX, eventY};

                Matrix invertMatrix = new Matrix();
                ((ImageView)v).getImageMatrix().invert(invertMatrix);

                invertMatrix.mapPoints(eventXY);
                int x = Integer.valueOf((int)eventXY[0]);
                int y = Integer.valueOf((int)eventXY[1]);

                Drawable imgDrawable = ((ImageView)v).getDrawable();
                Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

                //Limit x, y range within bitmap
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

//                Toast.makeText(MainActivity.this,"RED : "+redValue+", BLUE : "+blueValue+" , GREEN : "+greenValue,Toast.LENGTH_SHORT).show();
                textcolor.setText("touched color: " + "#" + Integer.toHexString(touchedRGB));
                textcolor.setTextColor(touchedRGB);

                showDilaog(x,y);

                return true;
            }


        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDilaog(int x , int y) {

        CharSequence[] items = {"Color"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

        wmlp.gravity = Gravity.NO_GRAVITY ;
        wmlp.x = x;   //x position
        wmlp.y = y;   //y position

        dialog.show();
    }
}

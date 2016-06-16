package com.sharkawy.colorpicker.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sharkawy.colorpicker.utilities.AppConstant;
import com.sharkawy.colorpicker.Instagram.InstagramApp;
import com.sharkawy.colorpicker.Instagram.InstagramSession;
import com.sharkawy.colorpicker.R;
import com.sharkawy.colorpicker.RecyclerItemClickListener;
import com.sharkawy.colorpicker.adapters.GalleryAdapter;
import com.sharkawy.colorpicker.dataModel.Image;
import com.sharkawy.colorpicker.utilities.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GalleryActivity extends AppCompatActivity {

    public static final String TAG = GalleryActivity.class.getSimpleName();
    private HashMap<String, String> userInfo;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "thumbnail";
    public static final String TAG_URL = "url";
    InstagramSession instagramSession ;
    RecyclerView recyclerView ;
    GalleryAdapter mAdapter ;
    ArrayList images ;
    ProgressDialog pDialog ;
    private InstagramApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        instagramSession = new InstagramSession(GalleryActivity.this);

//
        mApp = new InstagramApp(this, AppConstant.CLIENT_ID,
                AppConstant.CLIENT_SECRET, AppConstant.CALLBACK_URL);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading your pics..");

        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
//        RecyclerView.LayoutManager mLayoutManagerLinear = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        userInfo = (HashMap<String, String>) getIntent().getSerializableExtra(
                "userInfo");
        getAllImageRequest();

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplication(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(GalleryActivity.this,MainActivity.class);
                intent.putExtra("Image",((Image) images.get(position)).getImageURL());
                startActivity(intent);
            }
        }));

    }
    private void getAllImageRequest() {
        showpDialog();
        String new_URL ="https://api.instagram.com/v1/users/"+userInfo.get(InstagramApp.TAG_ID)+"/media/recent/?access_token="+instagramSession.getAccessToken();

        String URL = "https://api.instagram.com/v1/users/"
                + userInfo.get(InstagramApp.TAG_ID)
                + "/media/recent/?client_id="
                + AppConstant.CLIENT_ID
                + "&count="
                + userInfo.get(InstagramApp.TAG_COUNTS);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,new_URL
                , null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                // Parsing json object response
                hidepDialog();
//                Toast.makeText(MainActivity.this,response.toString(), Toast.LENGTH_LONG).show();
                try {
                    JSONArray data = response.getJSONArray(TAG_DATA);
                    Log.d(TAG, data.toString());
                    Log.d(TAG, data.length()+" size");

                    for (int data_i = 0; data_i < data.length(); data_i++) {

                        JSONObject data_obj = null;

                        data_obj = data.getJSONObject(data_i);
                        Log.d(TAG, data_obj.toString());

                        JSONObject images_obj = data_obj
                                .getJSONObject(TAG_IMAGES);

                        Log.d(TAG, images_obj.toString());

                        JSONObject thumbnail_obj = images_obj
                                .getJSONObject(TAG_THUMBNAIL);

                        Log.d(TAG, thumbnail_obj.toString());

                        // String str_height =
                        // thumbnail_obj.getString(TAG_HEIGHT);
                        //
                        // String str_width =
                        // thumbnail_obj.getString(TAG_WIDTH);

                        String str_url = thumbnail_obj.getString(TAG_URL);
                        Log.d(TAG, str_url.toString());

                        images.add(new Image(str_url));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                hidepDialog();

                String responseBody = null;
                JSONObject jsonObject = null;
                try {

                    responseBody = new String(error.networkResponse.data, "utf-8");
                    jsonObject = new JSONObject(responseBody);
                    Toast.makeText(GalleryActivity.this,jsonObject.toString(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(GalleryActivity.this, "Connection Error.", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(GalleryActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);

    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(
                    GalleryActivity.this);
            builder.setMessage("Disconnect from Instagram?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new Dialog.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    mApp.resetAccessToken();
                                    startActivity(new Intent(GalleryActivity.this, LoginActivity.class));
                                    finish();
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            final AlertDialog alert = builder.create();
            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

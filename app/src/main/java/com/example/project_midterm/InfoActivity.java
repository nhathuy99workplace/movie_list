package com.example.project_midterm;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoActivity extends AppCompatActivity {

    String imgUrl = "";
    TextView txt;
    ImageView poster;

    public static final String urlMovie = "https://api.themoviedb.org/3/movie/";
    public static final String apiKey = "302982cbe7568adaa7646323725a7a4b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        setup();
    }

    public void setup() {
        txt = findViewById(R.id.mName);
        poster = findViewById(R.id.posterImg);

        int id = getIntent().getIntExtra("id",1726);
        String name = getIntent().getStringExtra("title");

        getImgURI(id);

        //Toast.makeText(InfoActivity.this, url, Toast.LENGTH_SHORT).show();


        txt.setText(name);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(InfoActivity.this,MainActivity.class);
            startActivity(intent);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

    public void getImgURI(int id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        String url = urlMovie + id + "?api_key=" + apiKey;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            imgUrl = response.getString("backdrop_path");
                            String url = "https://image.tmdb.org/t/p/original" + imgUrl;

                            new LoadImageInternet().execute(url);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imgUrl = "";
                        Toast.makeText(InfoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }

        );
        requestQueue.add(jsonObjectRequest);

    }

    private class LoadImageInternet extends AsyncTask<String,Void, Bitmap> {

        Bitmap bitmapPoster = null;
        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                InputStream inputStream = url.openConnection().getInputStream();

                bitmapPoster = BitmapFactory.decodeStream(inputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmapPoster;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            poster.setImageBitmap(bitmap);
        }
    }
}

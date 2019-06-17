package com.example.project_midterm;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListAdapter listAdapter;
    RecyclerView recyclerView;
    ArrayList<DataItem> listMovie;
    String imgUrl = "";
    ImageView poster;

    public static final String urlMovie = "https://api.themoviedb.org/3/movie/";
    public static final String apiKey = "302982cbe7568adaa7646323725a7a4b";

    private Button buttonInsert;
    private Button buttonRemove;
    private EditText editTextInsert;
    private EditText editTextRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            setContentView(R.layout.activity_main_land);
            setup();
            buildRecyclerView_land();
        } else {
            // In portrait
            setContentView(R.layout.activity_main);
            setup();
            buildRecyclerView();
        }

    }

    public void setup() {
        createDataItems();
        buttonInsert = findViewById(R.id.button_insert);
        buttonRemove = findViewById(R.id.button_remove);
        editTextInsert = findViewById(R.id.edittext_insert);
        editTextRemove = findViewById(R.id.edittext_remove);

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextInsert.getText().toString());
                insertItem(position);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(editTextRemove.getText().toString());
                removeItem(position);
            }
        });
    }

    public void insertItem(int position) {
        listMovie.add(position, new DataItem("New Item At Position" + position, R.drawable.ic_local_movies, 2000));
        listAdapter.notifyItemInserted(position);
    }

    public void removeItem(int position) {
        listMovie.remove(position);
        listAdapter.notifyItemRemoved(position);
    }

    public void createDataItems() {
        listMovie = new ArrayList<>();
        listMovie.add(new DataItem("Iron Man", R.drawable.ic_local_movies, 1726));
        listMovie.add(new DataItem("Captain America", R.drawable.ic_local_movies, 1771));
        listMovie.add(new DataItem("Thor", R.drawable.ic_local_movies, 10195));
        listMovie.add(new DataItem("The Avengers", R.drawable.ic_local_movies, 24428));
        listMovie.add(new DataItem("Avengers: Age of Ultron", R.drawable.ic_local_movies, 99861));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main_land);
            setup();
            buildRecyclerView_land();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            setup();
            buildRecyclerView();
        }
    }

    public void buildRecyclerView() {
        listAdapter = new ListAdapter(listMovie,this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        listAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String name = listMovie.get(position).getName();
                int id = listMovie.get(position).getId();

                Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                intent.putExtra("title", name);
                intent.putExtra("id",id);

                startActivity(intent);
                listAdapter.notifyItemChanged(position);
            }
        });
    }

    public void buildRecyclerView_land() {
        listAdapter = new ListAdapter(listMovie,this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        listAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String name = listMovie.get(position).getName();
                int id = listMovie.get(position).getId();

                TextView txt = findViewById(R.id.mName);
                poster = findViewById(R.id.posterImg);
                getImgURI(id);

                txt.setText(name);
                listAdapter.notifyItemChanged(position);
            }
        });
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
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

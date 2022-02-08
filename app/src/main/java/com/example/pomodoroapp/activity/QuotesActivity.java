package com.example.pomodoroapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.pomodoroapp.R;
import com.example.pomodoroapp.databinding.ActivityQuotesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuotesActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    ActivityQuotesBinding binding;
    GestureDetector gestureDetector;
    private float x1,y1,x2,y2;
    private final int MIN_DISTANCE = 150;
    RequestQueue mRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mRequestQueue= Volley.newRequestQueue(this);

        sendRequest();
        this.gestureDetector = new GestureDetector(QuotesActivity.this,this);

        Toast.makeText(QuotesActivity.this, "Swipe Left to See More", Toast.LENGTH_LONG).show();
    }

    private void parseJSON(String text) {
        String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&formatversion=2&prop=pageimages|pageterms&piprop=thumbnail&pithumbsize=600&titles="+ text;
        binding.progressBar3.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response){
                        try {

                            JSONObject object = response.getJSONObject("query");
                            JSONArray array = object.getJSONArray("pages");
                            JSONObject page = array.getJSONObject(0);
                            JSONObject thumbnail = page.getJSONObject("thumbnail");
                            String req_url = thumbnail.getString("source");
                            loadImage(req_url);
                            binding.progressBar3.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            binding.progressBar3.setVisibility(View.GONE);
                            binding.imageView.setImageResource(R.drawable.default_profile_image);
                            Toast.makeText(QuotesActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.progressBar3.setVisibility(View.GONE);
                Toast.makeText(QuotesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        mRequestQueue.add(request);
    }

    private void loadImage(String imageUrl) {
        binding.progressBar3.setVisibility(View.VISIBLE);
        Glide.with(QuotesActivity.this)
                .load(imageUrl)
                .centerCrop()
                .placeholder(R.drawable.default_profile_image)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Toast.makeText(QuotesActivity.this, "Image loading failed", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        binding.progressBar3.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(binding.imageView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();

            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();

                float moveX = x2-x1;
                float moveY = y2-y1;

                if(Math.abs(moveX)>MIN_DISTANCE){
                    if(x1>x2) {
                        sendRequest();
                    }
                }
        }
        return super.onTouchEvent(event);
    }

    private void sendRequest() {
        binding.progressBar3.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://quotes15.p.rapidapi.com/quotes/random/")
                .get()
                .addHeader("x-rapidapi-host", "quotes15.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "3cf5979632mshd692a92023658c9p1bf1edjsn883990c32d96")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(QuotesActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String resp = response.body().string();
                Log.e("full",resp);
                QuotesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(resp);
                            String quote = object.getString("content");
                            JSONObject object1 = object.optJSONObject("originator");
                            String name  = object1.getString("name");

                            binding.name.setText(name);
                            binding.quote.setText(quote);

                            parseJSON(name);

                            binding.progressBar3.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            Toast.makeText(QuotesActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("okhhtp",e.getLocalizedMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
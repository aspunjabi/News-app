package com.example.android.newsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public TextView emptyView;

    public String newsUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.search_button);

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        emptyView = (TextView) findViewById(R.id.empty_list_view);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchString = (EditText) findViewById(R.id.search_view);
                String searchQuery = searchString.getText().toString();
                searchQuery = searchQuery.replace(" ", "+");

                newsUrl = "http://content.guardianapis.com/search?q="
                        + searchQuery + "&api-key=test&show-tags=contributor";

                Log.i("MainActivity_BOOKURL", newsUrl);

                NewsAsyncTask task = new NewsAsyncTask();
                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                if (!isConnected) {
                    Toast.makeText(MainActivity.this, "Not connected to internet!", Toast.LENGTH_SHORT).show();
                } else {
                    task.execute(newsUrl);
                }
            }

        });

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                Log.d("Swipe", "Refreshing Number");
                ( new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        NewsAsyncTask task = new NewsAsyncTask();
                        ConnectivityManager cm =
                                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                        boolean isConnected = activeNetwork != null &&
                                activeNetwork.isConnectedOrConnecting();

                        if (!isConnected) {
                            Toast.makeText(MainActivity.this, "Not connected to internet!", Toast.LENGTH_SHORT).show();
                        } else {
                            task.execute(newsUrl);
                        }
                    }
                }, 3000);
            }
        });

    }

    private class NewsAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(MainActivity.this, "Loading...Please wait", Toast.LENGTH_SHORT).show();
            ProgressDialog progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
            progDailog.cancel();
        }

        protected String doInBackground(String... url) {
            String newsStream = null;
            String urlString = url[0];

            HTTPDataRequest newsData = new HTTPDataRequest();
            newsStream = newsData.getHTTPData(urlString);

            return newsStream;
        }

        protected void onPostExecute(String stream) {
            /* Parse the stream received from the API and update the UI accordingly */
            /* Note to self: Consider writing a new function called updateUI to handle only UI aspects.
            * Maybe it takes in ArrayList<News> as input once parsed in a separate function. (getGuardianAPIData) */
            final ArrayList<News> newsResponse = new ArrayList<>();
            final ListView newsList = (ListView) findViewById(R.id.list_view);

            Log.i("RESPONSE_FROM_API", stream);

            if (stream != null) {
                try {
                    // Get the full HTTP Data as JSONObject
                    JSONObject reader = new JSONObject(stream);

                    JSONObject response = reader.getJSONObject("response");

                    int total = response.getInt("total");

                    Log.i("FIRST OBJECT IN JSON", reader.toString());
                    Log.i("EMBEDDED OBJECT IN JSON", response.toString());

                    if (total == 0) {
                        newsList.setVisibility(View.INVISIBLE);
                        emptyView.setText("No results found. Please search again.");
                        Toast.makeText(MainActivity.this, "No Results Found. Please search again.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Get the JSONArray results
                        emptyView.setText("");
                        JSONArray newsArray = response.getJSONArray("results");
                        // Get the "results" array from the JSONstream
                        for (int i = 0; i < newsArray.length(); i++) {

                            JSONObject NewsObject = newsArray.getJSONObject(i);
                            String title, sectionName, date, webUrl, author = "";

                            title = NewsObject.getString("webTitle");
                            sectionName = NewsObject.getString("sectionName");
                            date = NewsObject.getString("webPublicationDate");
                            webUrl = NewsObject.getString("webUrl");

                            Log.v("TAG_TITLE", title);
                            Log.v("TAG_SECTION", sectionName);
                            Log.v("TAG_DATE", date);
                            Log.v("TAG_URL", webUrl);
                            Log.v("TAG_AUTHOR", author);

                            Log.v("JSONARRAY", NewsObject.toString());

                            JSONArray tags = NewsObject.getJSONArray("tags");
                            for (int j = 0; j < tags.length(); j++) {
                                    JSONObject authorObject = tags.getJSONObject(j);
                                    author += authorObject.getString("webTitle") + "\t";
                                }

                            Log.v("TAG_TITLE", title);
                            Log.v("TAG_SECTION", sectionName);
                            Log.v("TAG_DATE", date);
                            Log.v("TAG_URL", webUrl);
                            Log.v("TAG_AUTHOR", author);

                            newsResponse.add(new News(title, sectionName, date, webUrl, author));
                        }

                        NewsAdapter itemsAdapter = new NewsAdapter(MainActivity.this, newsResponse, R.color.colorAccent);
                        newsList.setAdapter(itemsAdapter);

                        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String url = newsResponse.get(position).getWebUrl();
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Check the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
        }
    }

}



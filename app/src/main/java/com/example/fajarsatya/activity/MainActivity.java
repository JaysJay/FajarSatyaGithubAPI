package com.example.fajarsatya.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fajarsatya.ApiClient;
import com.example.fajarsatya.R;
import com.example.fajarsatya.adapter.MainAdapter;
import com.example.fajarsatya.object.Response;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private final int FIRST_TIME = 1;
    private RecyclerView recyclerView;
    private MainAdapter adapter;
    private List<Response.Item> data;
    private TextView textView;
    private ImageView img;
    private LinearLayoutManager layoutManager;

    private ProgressBar load;

    private boolean isLoading = true;
    private int pastVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private int page_number= 0;
    private int viewThreshold = 10;
    private String searchKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerview);
        data = new ArrayList<>();
        load = findViewById(R.id.load);
        textView = findViewById(R.id.tv1);
        img = findViewById(R.id.img1);

        checkInternet(MainActivity.this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull @NotNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if(dy>0){
                    if(isLoading){
                        if(totalItemCount>previousTotal){
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }

                    if(!isLoading && (totalItemCount-visibleItemCount)<=(pastVisibleItem+viewThreshold)){
                         page_number++;
                         pagination(searchKey, page_number);
                         isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchbar, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)item.getActionView();

        searchView.setQueryHint("Type here to search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getDataVerifikasiById(query, FIRST_TIME);
                searchKey = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void resetPagination(){
        visibleItemCount = 0;
        totalItemCount = 0;
        pastVisibleItem = 0;
        previousTotal = 0;
        page_number = 0;

    }

    public void getDataVerifikasiById(String id, int page){
        img.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        load.setVisibility(View.VISIBLE);
        textView.setText("Loading...");
        textView.setVisibility(View.VISIBLE);
        resetPagination();
        //user?q=pika+in:login

        String ids = id+"+in:login";

//        Map<String, String> params = new HashMap<>();
//        params.put("q", ids);

        Call<Response> call = ApiClient.getuserservice().getData(ids, page);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()){

                    // if data query 0 maka empty


                    Response history = response.body();
                    data = history.getItems();
                    if(data.isEmpty()){
                        load.setVisibility(View.GONE);
                        // ketika data kosong
                        textView.setVisibility(View.VISIBLE);
                        img.setVisibility(View.VISIBLE);
                        img.setImageResource(R.drawable.ic_no_profile);
                        textView.setText("No similiar User Found");
                    }else{
                        setAdapter();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Get Data Failed", Toast.LENGTH_SHORT).show();
                    load.setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_wait);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("You Searching too fast, Please kinda wait for a moment");
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "get Data Failure", Toast.LENGTH_SHORT).show();
                Log.e("Get Data", "onFailure: "+t );
            }
        });
    }

    public void pagination(String id, int page){
        String ids = id+"+in:login";

//        Map<String, String> params = new HashMap<>();
//        params.put("q", ids);

        Call<Response> call = ApiClient.getuserservice().getData(ids, page);
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()){

                    // if data query 0 maka empty
                    List<Response.Item> newData;
                    newData = new ArrayList<>();
                    Response history = response.body();
                    newData = history.getItems();
                    if(newData.isEmpty()){
                        // ketika data kosong tidak usah di refresh kembali
                        Toast.makeText(MainActivity.this, "No more data can be loaded...", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(MainActivity.this, "Page "+page_number, Toast.LENGTH_SHORT).show();
                        adapter.addMoreData(newData);
                    }
                }else{
                    recyclerView.setVisibility(View.GONE);
                    img.setVisibility(View.VISIBLE);
                    img.setImageResource(R.drawable.ic_wait);
                    textView.setText("You Scrolling too fast, please kinda wait\nbecause this API limit only 10 Request per minute");
                    textView.setVisibility(View.VISIBLE);

                }
                load.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "get Data Failure", Toast.LENGTH_SHORT).show();
                Log.e("Get Data", "onFailure: "+t );
            }
        });
    }

    public void setAdapter(){
        //Collections.reverse(dataHistory);
        textView.setVisibility(View.GONE);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainAdapter(data, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
        load.setVisibility(View.GONE);
    }

    public static void checkInternet(Context context){
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = false;
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!network_enabled) {
            // notify user
            new AlertDialog.Builder(context)
                    .setMessage(R.string.gps_network_not_enabled)
                    .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(context)
                                    .setMessage("Tidak Dapat Melakukan Request")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                            Intent intent = new Intent(context, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            context.startActivity(intent);
                                        }
                                    })
                                    .show();
                        }
                    })
                    .show();
        }
    }
}
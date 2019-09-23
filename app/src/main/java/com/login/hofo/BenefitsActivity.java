package com.login.hofo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BenefitsActivity extends ConstructNavigationBar{

    String api_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefits);

        startNavigationBar();
    }

    public void goToMenusByChef(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        this.startActivity(intent);
    }

    public void goToFaq(View view)
    {
        Intent intent = new Intent(getApplicationContext(), FaqListActivity.class);
        this.startActivity(intent);
    }



}

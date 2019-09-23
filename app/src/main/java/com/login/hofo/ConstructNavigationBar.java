package com.login.hofo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ConstructNavigationBar extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String api_token;

    private Menu menu;

    TextView correoMenu, nombreMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefits);

        startNavigationBar();
    }

    public void startNavigationBar()
    {
        //Para barra de navegacion
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        //Cambiando elemento de menú
        this.menu = navigationView.getMenu();
        updateMenuTitles();


        navigationView.setNavigationItemSelectedListener(this);


        //data de session
        SharedPreferences sp = getSharedPreferences("your_prefs", PerfilActivity.MODE_PRIVATE);
        api_token = sp.getString("api_token", null);
        String nombre = sp.getString("username",null);
        String correo = sp.getString("email",null);

        //cambiando dinamicamente elementos
        View hView =  navigationView.getHeaderView(0);
        correoMenu = (TextView) hView.findViewById(R.id.correoMenu);
        nombreMenu = (TextView) hView.findViewById(R.id.nombreMenu);

        correoMenu.setText((correo != null)?correo:"--");
        nombreMenu.setText((nombre != null)?nombre:"--");

    }

    //Metodos para menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.perfil, menu);

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {

            goToMenuCRUD();

        } else if (id == R.id.nav_solicitud) {

            goToSolicitudes();

        } else if (id == R.id.nav_logout) {

            logout();

        } else if(id == R.id.nav_faq) {
            goToFaq();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout()
    {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Cerrando sesión");
        dialog.setMessage("Cargando...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    JSONObject jsonResponse = new JSONObject(response);

                    String msg = jsonResponse.getString("data");

                    if (msg.equals("User logged out."))
                    {
                        //Borrando datos en session

                        SharedPreferences sp = getSharedPreferences("your_prefs", MainActivity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("username");
                        editor.remove("email");
                        editor.remove("api_token");
                        editor.remove("id");
                        editor.remove("address");
                        editor.remove("phone");
                        editor.remove("roleId");
                        editor.commit();

                        dialog.dismiss();


                        // Redirigiendo a pagina inicial de login
                        goToLoginDiner();
                    }
                    else
                    {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());

                        builder.setMessage("Error al hacer logout del pregonero")
                                .setNegativeButton("Retry", null)
                                .create().show();
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
        };

        LogoutRequest logoutrequest = new LogoutRequest(api_token, responseListener);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        queue.add(logoutrequest);
    }

    public void goToMenuCRUD ()
    {
        Intent intentMenuCRUD = new Intent(getApplicationContext(), MenuActivity.class);
        this.startActivity(intentMenuCRUD);
    }


    private void goToSolicitudes()
    {
        Intent intentSolicitudes = new Intent(getApplicationContext(), SolicitudesActivity.class);
        this.startActivity(intentSolicitudes);
    }

    private void goToLoginDiner()
    {
        Intent intentReturns = new Intent(getApplicationContext(), MainActivity.class);
        this.startActivity(intentReturns);
    }

    private void updateMenuTitles() {
//
        MenuItem nav_menu = menu.findItem(R.id.nav_menu);
        MenuItem nav_solicitud = menu.findItem(R.id.nav_solicitud);

        if (User.isChef())
        {
            nav_menu.setTitle("Mis Menús");
            nav_solicitud.setTitle("Solicitudes");
        }
        else
        {
            nav_menu.setTitle("Menús");
            nav_solicitud.setTitle("Mis pedidos");
        }

    }

    private void goToFaq()
    {
        Intent intentFaq = new Intent(getApplicationContext(), FaqListActivity.class);
        this.startActivity(intentFaq);
    }
    //Fin de metodos para menu lateral

}

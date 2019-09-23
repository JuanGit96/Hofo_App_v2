package com.login.hofo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends ConstructNavigationBar {


    //a List of type hero for holding list items
    List<Menu> menuList;

    //the listview
    ListView listViewMenu;

    String api_token;

    int idUser;

    TextView nothingMenu, titleActivity;

    LinearLayout agendarServicio;

    Button crearMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        startNavigationBar();

        agendarServicio = (LinearLayout) findViewById(R.id.agendarServicio);
        crearMenu = (Button) findViewById(R.id.crearMenu);

        //Cargar datos de session
        SharedPreferences sp = getSharedPreferences("your_prefs", MenuActivity.MODE_PRIVATE);
        api_token = sp.getString("api_token", null);
        idUser = sp.getInt("id", -1);

        nothingMenu = (TextView) findViewById(R.id.nothingMenu);
        titleActivity = (TextView) findViewById(R.id.titleActivity);

        if (User.isChef())
        {
            listMenusByChef();
            //ocultando campos
            agendarServicio.setVisibility(View.GONE);
            titleActivity.setText("MIS MENÚS");
        }
        else
        {
            listAllMenus();
            crearMenu.setVisibility(View.GONE);
            titleActivity.setText("MENÚS");
        }


    }

    public void scheduleService(View view)
    {
        Intent intentMenuAction = new Intent(MenuActivity.this, ScheduleServiceActivity.class);
        MenuActivity.this.startActivity(intentMenuAction);
    }

    public void goToAction(View view) {

        Intent intentMenuAction = new Intent(MenuActivity.this, CreateMenuActivity.class);
        MenuActivity.this.startActivity(intentMenuAction);
    }

    public void listMenusByChef ()
    {
        menuList = new ArrayList<>();
        listViewMenu = (ListView) findViewById(R.id.listViewMenu);

        //consumiento servicio
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {

                    JSONObject jsonResponse = new JSONObject(response);


                    String code = jsonResponse.getString("code");

                    if (code.equals("200"))
                    {
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");


                        if (jsonArray.length() == 0)
                        {
                            if (!User.isChef())
                                nothingMenu.setText("No hay menús disponibles");

                            nothingMenu.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            try {
                                JSONObject jsonObjectPregon = jsonArray.getJSONObject(i);

                                int id = jsonObjectPregon.getInt("id");
                                String nombre = jsonObjectPregon.getString("nombre");
                                String descripcion = jsonObjectPregon.getString("descripcion");
                                String precio = jsonObjectPregon.getString("precio");
                                String precioVenta = jsonObjectPregon.getString("precio_venta");
                                String foto = jsonObjectPregon.getString("foto");
                                String type_menu = jsonObjectPregon.getString("type_menu");
                                int user_id = jsonObjectPregon.getInt("user_id");

                                //adding some values to our list
                                menuList.add(new Menu(id, nombre, descripcion, precio,precioVenta, foto,type_menu,user_id));

                            } catch (JSONException e) {

                            }
                        }

                        //creating the adapter
                        ListMenusAdapter adapter = new ListMenusAdapter(MenuActivity.this, R.layout.custom_list_menus_cocinero, menuList);

                        //attaching adapter to the listview
                        listViewMenu.setAdapter(adapter);
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);

                        builder.setMessage("Error al listar menus...")
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

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                Toast.makeText(getApplicationContext(), "Error en el servidor al listar menus por chef", Toast.LENGTH_LONG).show();
            }
        };

        MenusByChefRequest menusByChefRequest = new MenusByChefRequest(api_token, Integer.toString(idUser),responseListener,errorListener);

        RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);

        queue.add(menusByChefRequest);
    }

    private void listAllMenus ()
    {
        menuList = new ArrayList<>();
        listViewMenu = (ListView) findViewById(R.id.listViewMenu);

        //consumiento servicio
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {

                    JSONObject jsonResponse = new JSONObject(response);


                    String code = jsonResponse.getString("code");

                    if (code.equals("200"))
                    {
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");


                        if (jsonArray.length() == 0)
                        {
                            if (!User.isChef())
                                nothingMenu.setText("No hay menús disponibles");

                            nothingMenu.setVisibility(View.VISIBLE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            try {
                                JSONObject jsonObjectPregon = jsonArray.getJSONObject(i);

                                int id = jsonObjectPregon.getInt("id");
                                String nombre = jsonObjectPregon.getString("nombre");
                                String descripcion = jsonObjectPregon.getString("descripcion");
                                String precio = jsonObjectPregon.getString("precio");
                                String precioVenta = jsonObjectPregon.getString("precio_venta");
                                String foto = jsonObjectPregon.getString("foto");
                                String type_menu = jsonObjectPregon.getString("type_menu");
                                int user_id = jsonObjectPregon.getInt("user_id");

                                //adding some values to our list
                                menuList.add(new Menu(id, nombre, descripcion, precio,precioVenta, foto,type_menu,user_id));

                            } catch (JSONException e) {

                            }
                        }

                        //creating the adapter
                        ListMenusAdapter adapter = new ListMenusAdapter(MenuActivity.this, R.layout.custom_list_menus_cocinero, menuList);

                        //attaching adapter to the listview
                        listViewMenu.setAdapter(adapter);
                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);

                        builder.setMessage("Error al listar menus...")
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

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                Toast.makeText(getApplicationContext(), "Error en el servidor al listar menus...", Toast.LENGTH_LONG).show();
            }
        };

        ListMenusRequest listMenusRequest = new ListMenusRequest(api_token,responseListener,errorListener);

        RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);

        queue.add(listMenusRequest);
    }

}

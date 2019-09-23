package com.login.hofo;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SolicitudesActivity extends ConstructNavigationBar {


    //a List of type hero for holding list items
    List<Solicitud> solicitudList;

    //the listview
    ListView listViewOrder;

    String api_token;

    int idUser;

    TextView no_solicitudes;

    Button goToMenus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes);

        startNavigationBar();

        //Cargar datos de session
        SharedPreferences sp = getSharedPreferences("your_prefs", MenuActivity.MODE_PRIVATE);
        api_token = sp.getString("api_token", null);
        idUser = sp.getInt("id", -1);

        no_solicitudes = (TextView) findViewById(R.id.no_solicitudes);

        goToMenus = (Button) findViewById(R.id.goToMenus);

        if (User.isChef())
        {
            listOrdersByChef();
            goToMenus.setText("MIS MENÚS");
        }
        else
        {
            listOrdersByDiner();
            goToMenus.setText("MENÚS");
        }


    }

    private void listOrdersByChef()
    {
        solicitudList = new ArrayList<>();
        listViewOrder = (ListView) findViewById(R.id.listViewOrder);

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
                        JSONObject jsonObject = jsonResponse.getJSONObject("data");

                        Iterator<String> keys = jsonObject.keys();

                        while(keys.hasNext()) {

                            String key = keys.next(); // se guarda el nombre de plato

                            JSONArray jsonArray = jsonObject.getJSONArray(key); // obtenemos las solicitudes de los platos

                            if (jsonArray.length() > 0)
                            {
                                no_solicitudes.setVisibility(View.GONE);
                            }

                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject jsonObjectOrder = jsonArray.getJSONObject(i);

                                    // Elementos del objeto Json
                                    int id = jsonObjectOrder.getInt("id");
                                    String hour = jsonObjectOrder.getString("hour");
                                    String address = jsonObjectOrder.getString("address");
                                    int amount_people = jsonObjectOrder.getInt("amount_people");
                                    String additional_comments = jsonObjectOrder.getString("additional_comments");

                                    int menu_id = 0;

                                    if (!jsonObjectOrder.getString("menu_id").equals("null"))
                                    {
                                        menu_id = jsonObjectOrder.getInt("menu_id");
                                    }

                                    int diner_id = 0;

                                    if (!jsonObjectOrder.getString("diner_id").equals("null"))
                                    {
                                        diner_id = jsonObjectOrder.getInt("diner_id");
                                    }

                                    int total_charge = 0;
                                    if (!jsonObjectOrder.getString("total_charge").equals("null")) {
                                        total_charge = jsonObjectOrder.getInt("total_charge");
                                    }

                                    int type_order = 0;
                                    if (!jsonObjectOrder.getString("type_order").equals("null")) {
                                        type_order = jsonObjectOrder.getInt("type_order");
                                    }

                                    int type_pay = 0;
                                    if (!jsonObjectOrder.getString("type_pay").equals("null")) {
                                        type_pay = jsonObjectOrder.getInt("type_pay");
                                    }

                                    boolean isSchedule = false;
                                    if (!jsonObjectOrder.getString("isSchedule").equals("1"))
                                    {
                                        isSchedule = true;
                                    }

                                    int modality = 0;
                                    if (!jsonObjectOrder.getString("modality").equals("null"))
                                    {
                                        modality = jsonObjectOrder.getInt("modality");
                                    }

                                    int status = 0;
                                    if (!jsonObjectOrder.getString("status").equals("null"))
                                    {
                                        status = jsonObjectOrder.getInt("status");
                                    }

                                    String chance = jsonObjectOrder.getString("chance");
                                    String food_type = jsonObjectOrder.getString("food_type");

                                    String fotoMenu = jsonObjectOrder.getString("fotoMenu");


                                    Solicitud solicitud = new Solicitud(key, fotoMenu, hour, address, additional_comments,
                                            amount_people,total_charge,type_order,type_pay,menu_id,
                                            diner_id,isSchedule, modality, status, chance, food_type,id);

                                    //Agregando valores al objeto y a la lista
                                     solicitudList.add(solicitud);

                                } catch (JSONException e) {
                                    Toast.makeText(SolicitudesActivity.this, e.toString(),Toast.LENGTH_LONG).show();
                                }
                            }


                        }

                        //creating the adapter
                        ListOrdersAdapter adapter = new ListOrdersAdapter(SolicitudesActivity.this, R.layout.custom_list_solicitudes, solicitudList);

                        //attaching adapter to the listview
                        listViewOrder.setAdapter(adapter);
                        //Toast.makeText(SolicitudesActivity.this,solicitudList.toString(),Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SolicitudesActivity.this);

                        builder.setMessage("Error al listar solicitudes...")
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

        SolicitudesRequest solicitudesRequest = new SolicitudesRequest(api_token, idUser,responseListener,errorListener);

        RequestQueue queue = Volley.newRequestQueue(SolicitudesActivity.this);

        queue.add(solicitudesRequest);
    }


    private void listOrdersByDiner ()
    {;
        solicitudList = new ArrayList<>();
        listViewOrder = (ListView) findViewById(R.id.listViewOrder);

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

                        if (jsonArray.length() > 0)
                        {
                            no_solicitudes.setVisibility(View.GONE);
                        }

                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            try {
                                JSONObject jsonObjectOrder = jsonArray.getJSONObject(i);

                                // Elementos del objeto Json
                                int id = jsonObjectOrder.getInt("id");
                                String hour = jsonObjectOrder.getString("hour");
                                String address = jsonObjectOrder.getString("address");
                                int amount_people = jsonObjectOrder.getInt("amount_people");
                                String additional_comments = jsonObjectOrder.getString("additional_comments");
                                String name_menu = jsonObjectOrder.getString("nameMenu");

                                int menu_id = 0;

                                if (!jsonObjectOrder.getString("menu_id").equals("null"))
                                {
                                    menu_id = jsonObjectOrder.getInt("menu_id");
                                }

                                int diner_id = 0;

                                if (!jsonObjectOrder.getString("diner_id").equals("null"))
                                {
                                    diner_id = jsonObjectOrder.getInt("diner_id");
                                }

                                int total_charge = 0;
                                if (!jsonObjectOrder.getString("total_charge").equals("null")) {
                                    total_charge = jsonObjectOrder.getInt("total_charge");
                                }

                                int type_order = 0;

                                if (!jsonObjectOrder.getString("type_order").equals("null")) {
                                    type_order = jsonObjectOrder.getInt("type_order");
                                }

                                int type_pay = 0;

                                if (!jsonObjectOrder.getString("type_pay").equals("null")) {
                                    type_pay = jsonObjectOrder.getInt("type_pay");
                                }

                                boolean isSchedule = false;
                                if (!jsonObjectOrder.getString("isSchedule").equals("1"))
                                {
                                    isSchedule = true;
                                }

                                int modality = 0;
                                if (!jsonObjectOrder.getString("modality").equals("null"))
                                {
                                    modality = jsonObjectOrder.getInt("modality");
                                }

                                int status = 0;
                                if (!jsonObjectOrder.getString("status").equals("null"))
                                {
                                    status = jsonObjectOrder.getInt("status");
                                }

                                String chance = jsonObjectOrder.getString("chance");
                                String food_type = jsonObjectOrder.getString("food_type");

                                String fotoMenu = jsonObjectOrder.getString("fotoMenu");

                                Solicitud solicitud = new Solicitud(name_menu, fotoMenu, hour, address, additional_comments,
                                        amount_people,total_charge,type_order,type_pay,menu_id,
                                        diner_id,isSchedule, modality, status, chance, food_type,id);

                                //Agregando valores al objeto y a la lista
                                solicitudList.add(solicitud);

                            } catch (JSONException e) {
                                Toast.makeText(SolicitudesActivity.this, e.toString(),Toast.LENGTH_LONG).show();
                            }
                        }

                        //creating the adapter
                        ListOrdersAdapter adapter = new ListOrdersAdapter(SolicitudesActivity.this, R.layout.custom_list_solicitudes, solicitudList);

                        //attaching adapter to the listview
                        listViewOrder.setAdapter(adapter);
                        //Toast.makeText(SolicitudesActivity.this,solicitudList.toString(),Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SolicitudesActivity.this);

                        builder.setMessage("Error al listar solicitudes...")
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

        PedidosRequest pedidosRequest = new PedidosRequest(api_token, idUser,responseListener,errorListener);

        RequestQueue queue = Volley.newRequestQueue(SolicitudesActivity.this);

        queue.add(pedidosRequest);
    }

    public void goToMenus(View view) {
        Intent intent = new Intent(SolicitudesActivity.this, MenuActivity.class);
        SolicitudesActivity.this.startActivity(intent);
    }
}

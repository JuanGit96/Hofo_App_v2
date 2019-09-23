package com.login.hofo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    TextView tv_goTo_registerPage, etEmailUser, etPasswordUser;

    LinearLayout option_login_diner;

    Button btnLogin;

    ProgressDialog dialog;

    Context thiscontext = this;

    //variables para capturar data de actividad anterior
    String nombre_menu, descripcion_menu;
    int precio_menu, user_id, menu_id, numero_platos, type_order, total_cobro, type_pay, modality;
    String order_address, order_hour, order_additionalComments, chance, food_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(existSession(User.isChef()))
        {
            validateRedirct();
        }

        //validando si sellega al registro desde la creacion de una orden
        if (Order.orderInProcess())
            setVaulesOrder();

        btnLogin = (Button) findViewById(R.id.btn_login);

        etEmailUser = (EditText) findViewById(R.id.login_user);
        etPasswordUser = (EditText) findViewById(R.id.login_password);

        tv_goTo_registerPage = (TextView) findViewById(R.id.tv_goTo_registerPage);

        option_login_diner = (LinearLayout) findViewById(R.id.option_login_diner);

        if(!User.isChef())
            showOptionToDiner();

        tv_goTo_registerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Order.orderInProcess())
                {
                    // Redirigiendo a registro
                    Intent intentLogin = new Intent(LoginActivity.this, RegisterActivity.class);
                    intentLogin.putExtra("numero_platos",numero_platos);
                    intentLogin.putExtra("order_address",order_address);
                    intentLogin.putExtra("order_hour",order_hour);
                    intentLogin.putExtra("order_additionalComments",order_additionalComments);
                    intentLogin.putExtra("type_order",type_order);
                    intentLogin.putExtra("menu_id",menu_id);
                    intentLogin.putExtra("total_cobro",total_cobro);
                    intentLogin.putExtra("type_pay",type_pay);

                    //para servicios agendados
                    intentLogin.putExtra("modality",modality);
                    intentLogin.putExtra("chance",chance);
                    intentLogin.putExtra("food_type",food_type);
                    LoginActivity.this.startActivity(intentLogin);
                }
                else
                {
                    // Redirigiendo a registro
                    Intent intentLogin = new Intent(LoginActivity.this, RegisterActivity.class);
                    LoginActivity.this.startActivity(intentLogin);
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setTitle("Iniciando sesión");
                dialog.setMessage("Cargando...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                dialog.setCancelable(false);

                final String emailUser = etEmailUser.getText().toString();
                final String passUser = etPasswordUser.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);

                            String code = jsonResponse.getString("code");

                            if (code.equals("200"))
                            {
                                //Guardando datos en session

                                JSONObject resultData = jsonResponse.getJSONObject("data");

                                String username = resultData.optString("name");
                                String email = resultData.getString("email");
                                String api_token = resultData.getString("api_token");
                                int id = resultData.getInt("id");
                                int roleId = resultData.getInt("role_id");
                                String address = resultData.getString("address");
                                String phone = resultData.getString("phone");

                                SharedPreferences sp = getSharedPreferences("your_prefs", MainActivity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username", username);
                                editor.putString("email", email);
                                editor.putString("api_token", api_token);
                                editor.putInt("id", id);
                                editor.putInt("roleId", roleId);
                                editor.putString("address", address);
                                editor.putString("phone", phone);
                                editor.commit();


                                actualizarFMCToken(id, FirebaseInstanceId.getInstance().getToken());

                                dialog.dismiss();

                                validateRedirct();

//                                if (currentModality != 0) //si llegamos al registro por  un menu
//                                {
//                                    goToFormularioDePedido();
//                                }
//                                else
//                                {
//                                    Intent loginIntent = new Intent(LoginActivity.this, PerfilActivity.class);
//                                    LoginActivity.this.startActivity(loginIntent);
//                                }

                            }

                            if (code.equals("400"))
                            {

                                //Mostrando errores en pantalla

                                String error = jsonResponse.getString("error");

                                dialog.dismiss();

                                Toast alertMessage = Toast.makeText(getApplicationContext(),error, Toast.LENGTH_LONG);
                                alertMessage.setGravity(Gravity.CENTER, 0, 0);
                                alertMessage.show();

                            }

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();

                            Toast alertMessage = Toast.makeText(getApplicationContext(),"Error en la comunicacion con el servidor", Toast.LENGTH_LONG);
                            alertMessage.setGravity(Gravity.CENTER, 0, 0);
                            alertMessage.show();
                        }

                    }
                };

                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError)
                    {
                        Toast alertMessage = Toast.makeText(getApplicationContext(), "Error al iniciar sesión: "+volleyError, Toast.LENGTH_LONG);
                        alertMessage.setGravity(Gravity.CENTER, 0, 0);
                        alertMessage.show();

                        dialog.dismiss();
                    }
                };

                LoginRequest loginrequest = new LoginRequest(emailUser, passUser, responseListener, errorListener);

                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                queue.add(loginrequest);


            }
        });




    }

    public void setVaulesOrder()
    {
        Intent intent = getIntent();
        numero_platos = intent.getIntExtra("numero_platos",0);
        order_address = intent.getStringExtra("order_address");
        order_hour = intent.getStringExtra("order_hour");
        order_additionalComments = intent.getStringExtra("order_additionalComments");
        type_order = intent.getIntExtra("type_order",0);
        menu_id = intent.getIntExtra("menu_id",0);
        total_cobro = intent.getIntExtra("total_cobro",0);
        type_pay = intent.getIntExtra("type_pay",0);

        //para servicios agendados
        modality = intent.getIntExtra("modality",0);
        chance = intent.getStringExtra("chance");
        food_type = intent.getStringExtra("food_type");
    }

    public boolean existSession (boolean isChef)
    {
        //datos de session
        SharedPreferences sp = getSharedPreferences("your_prefs", LoginActivity.MODE_PRIVATE);
        String nameSession = sp.getString("username",null);
        int roleId = sp.getInt("roleId",0);

        if (isChef)
        {
            return roleId == 1;
        }
        else
        {
            return roleId == 2;
        }
    }

    private void goToBenefitsPage ()
    {
        Intent intent = new Intent(LoginActivity.this,BenefitsActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void goToCreateOrder ()
    {
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setTitle("Registrando Pedido");
        dialog.setMessage("Cargando...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);

        SharedPreferences sp = getSharedPreferences("your_prefs", PerfilActivity.MODE_PRIVATE);
        int idUser = sp.getInt("id", -1);

        final String orderAddress = order_address;
        final String orderHour = order_hour;
        final String orderAdditionalComments = order_additionalComments;
        final String type_orderR = Integer.toString(type_order);
        final String numero_platosR = Integer.toString(numero_platos);
        final String diner_idR = Integer.toString(idUser);
        final String menu_idR = Integer.toString(menu_id);
        final String type_payR = Integer.toString(type_pay);
        final String totalACobrar = Integer.toString(total_cobro);

        //para servicios agendados
        final String modalityR = Integer.toString(modality);
        final String chanceR = chance;
        final String foodTypeR = food_type;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    JSONObject jsonResponse = new JSONObject(response);

                    String code = jsonResponse.getString("code");

                    if (code.equals("201"))
                    {
                        //pedido creado correctamente
                        dialog.dismiss();
                        if (modality == 0)
                            ConfirmOrderActivity.goToOrderSuccess(LoginActivity.this, type_pay, menu_id, type_order,false);
                        else
                            ConfirmOrderActivity.goToOrderSuccess(LoginActivity.this, type_pay, menu_id, type_order,true);
                        //goToOrderSuccess();
                    }

                    if (code.equals("400"))
                    {
                        //Mostrando errores en pantalla

                        String error = jsonResponse.getString("error");

                        dialog.dismiss();

                        Toast alertMessage = Toast.makeText(getApplicationContext(),error, Toast.LENGTH_LONG);
                        alertMessage.setGravity(Gravity.CENTER, 0, 0);
                        alertMessage.show();
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
                Toast.makeText(getApplicationContext(), "Error en el servidor al crear pedido", Toast.LENGTH_LONG).show();
            }
        };

        NewOrderRequest newOrder = new NewOrderRequest(orderHour, orderAddress, numero_platosR,
                orderAdditionalComments, menu_idR, diner_idR, type_orderR, type_payR, totalACobrar,
                modalityR, chanceR, foodTypeR, Integer.toString(Order.getDomiciliary()), responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        queue.add(newOrder);
    }

    public void goToAllMenus (View view)
    {
        goToAllMenus();
    }

    private void goToAllMenus ()
    {
        Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void validateRedirct ()
    {
        if (User.isChef())
        {
            goToMenusByChef();
        }
        else
        {
            if (Order.orderInProcess())
                goToCreateOrder();
            else
                goToAllMenus();
        }
    }

//    public boolean sessionChef()
//    {
//        //datos de session
//        SharedPreferences sp = getSharedPreferences("your_prefs", LoginActivity.MODE_PRIVATE);
//        int roleId = sp.getInt("roleId",0);
//
//        return roleId == 1;
//    }
//
//    private void goToFormularioDePedido()
//    {
//        // Redirigiendo a formulario de menu
//        Intent intentFormMenu = new Intent(LoginActivity.this, DomiciliosFormActivity.class);
//        intentFormMenu.putExtra("nombre_menu", nombre_menu);
//        intentFormMenu.putExtra("descripcion_menu", descripcion_menu);
//        intentFormMenu.putExtra("precio_menu", precio_menu);
//        intentFormMenu.putExtra("user_id", user_id);
//        intentFormMenu.putExtra("menu_id", menu_id);
//        intentFormMenu.putExtra("currentModality", currentModality);
//        LoginActivity.this.startActivity(intentFormMenu);
//    }

    public void actualizarFMCToken(int id, String FMCTocken)
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try
                {
                    JSONObject jsonResponse = new JSONObject(response);

                    String code = jsonResponse.getString("code");

                    if (code.equals("200"))
                    {
                        //Todo Bien
                    }

                    if (code.equals("400"))
                    {
                        dialog.dismiss();

                        Toast alertMessage = Toast.makeText(getApplicationContext(),"error al actualizar codigo de telefono", Toast.LENGTH_LONG);
                        alertMessage.setGravity(Gravity.CENTER, 0, 0);
                        alertMessage.show();

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();

                    Toast alertMessage = Toast.makeText(getApplicationContext(),"Error al actualizar codigo de telefono", Toast.LENGTH_LONG);
                    alertMessage.setGravity(Gravity.CENTER, 0, 0);
                    alertMessage.show();
                }

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                Toast alertMessage = Toast.makeText(getApplicationContext(), "Error al iniciar sesión: "+volleyError, Toast.LENGTH_LONG);
                alertMessage.setGravity(Gravity.CENTER, 0, 0);
                alertMessage.show();

                dialog.dismiss();
            }
        };

        UpdateFMCTockenRequest updateFMCTockenRequest = new UpdateFMCTockenRequest(id, FMCTocken,responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

        queue.add(updateFMCTockenRequest);
    }

    public void goToMenusByChef()
    {
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        this.startActivity(intent);
    }

    public void showOptionToDiner()
    {
        option_login_diner.setVisibility(View.VISIBLE);
    }


}

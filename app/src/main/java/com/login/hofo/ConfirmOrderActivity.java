package com.login.hofo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmOrderActivity extends ConstructNavigationBar {

    int numero_platos, type_order, menu_id, total_cobro, diner_id;

    int roleId;

    String order_address, order_hour, order_additionalComments;

    String api_token;

    RadioGroup radio_type_pay;

    int type_pay = 0;

    ProgressDialog dialog;

    TextView precio_venta_suma, name_menu, descriptionMenuChef;

    String nombreMenu, descripcionMenu, suma;

    String rutaImagenMenu;

    ImageView viewMenuChef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        startNavigationBar();

        //data de session
        SharedPreferences sp = getSharedPreferences("your_prefs", PerfilActivity.MODE_PRIVATE);
        api_token = sp.getString("api_token", null);
        roleId = sp.getInt("roleId", 0);

        precio_venta_suma = (TextView) findViewById(R.id.precio_venta_suma);
        name_menu = (TextView) findViewById(R.id.name_menu);
        descriptionMenuChef = (TextView) findViewById(R.id.descriptionMenuChef);

        radio_type_pay = (RadioGroup) findViewById(R.id.user_radio_type_pay);

        viewMenuChef = (ImageView) findViewById(R.id.viewMenuChef);

        //Capturando radios en radiogroup
        radio_type_pay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.contraentrega){
                    type_pay = 1;
                }else if (checkedId == R.id.transferencia){
                    type_pay = 2;
                }

            }

        });

        setVaulesOrder();

        getImageMenu();

        precio_venta_suma.setText(suma);
        name_menu.setText(nombreMenu);
        descriptionMenuChef.setText(descripcionMenu);

    }

    private void setVaulesOrder()
    {
        Intent intent = getIntent();
        nombreMenu = intent.getStringExtra("nombreMenu");
        descripcionMenu = intent.getStringExtra("descripcionMenu");
        suma = intent.getStringExtra("suma");
        rutaImagenMenu = intent.getStringExtra("rutaImagenMenu");

        numero_platos = intent.getIntExtra("numero_platos",0);
        order_address = intent.getStringExtra("order_address");
        order_hour = intent.getStringExtra("order_hour");
        order_additionalComments = intent.getStringExtra("order_additionalComments");
        type_order = intent.getIntExtra("type_order",0);
        menu_id = intent.getIntExtra("menu_id",0);
        total_cobro = intent.getIntExtra("total_cobro",0);
    }

    public void goToGenerateOrder(View view)
    {
        if(!validateCamps())
            return;

        if(!validateLogin())
        {
            goToRegister();
            return;
        }


        //si estamos logueados genera la orden
        dialog = new ProgressDialog(ConfirmOrderActivity.this);
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
        final String totalACobrar = Integer.toString(total_cobro);
        final String type_payR = Integer.toString(type_pay);

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
                        ConfirmOrderActivity.goToOrderSuccess(ConfirmOrderActivity.this, type_pay,menu_id, type_order, false);
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
                orderAdditionalComments, menu_idR, diner_idR, type_orderR, type_payR, totalACobrar,null,null,null, Integer.toString(Order.getDomiciliary()),
                responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(ConfirmOrderActivity.this);

        queue.add(newOrder);
    }

    private void goToRegister()
    {
        Order.setOrder(true); //especificamos que la orden queda en proceso

        Intent intentLoginComensal = new Intent(ConfirmOrderActivity.this, RegisterActivity.class);
        intentLoginComensal.putExtra("numero_platos",numero_platos);
        intentLoginComensal.putExtra("order_address",order_address);
        intentLoginComensal.putExtra("order_hour",order_hour);
        intentLoginComensal.putExtra("order_additionalComments",order_additionalComments);
        intentLoginComensal.putExtra("type_order",type_order);
        intentLoginComensal.putExtra("menu_id",menu_id);
        intentLoginComensal.putExtra("total_cobro",total_cobro);
        intentLoginComensal.putExtra("type_pay",type_pay);
        ConfirmOrderActivity.this.startActivity(intentLoginComensal);
    }

    private boolean validateCamps ()
    {
        if (type_pay == 0)
        {
            Toast.makeText(getApplicationContext(),"Porfavor dinos de que forma vas a pagar",Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private boolean validateLogin()
    {

        boolean isLoggedComensal = validateLoggedComensal();

        if (!isLoggedComensal)
        {
            return false;
        }
        else
        {
            //data del comensal en sesión
            SharedPreferences sp = getSharedPreferences("your_prefs", PerfilActivity.MODE_PRIVATE);
            diner_id = sp.getInt("id", 0);
        }

        return true;
    }

    public boolean validateLoggedComensal()
    {
        if (roleId == 0)
            return false;

        return roleId == 2;
    }

    public static void goToOrderSuccess(Context context, int type_pay, int menu_id, int type_order,boolean isSchedule)
    {
        Intent intent = new Intent(context, OrderSuccessActivity.class);
        intent.putExtra("type_pay",type_pay);
        intent.putExtra("type_order",type_order);
        intent.putExtra("menu_id",menu_id);
        intent.putExtra("isSchedule",isSchedule);
        context.startActivity(intent);
    }

    public void getImageMenu ()
    {
        Response.Listener<Bitmap> responseListener = new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {

                viewMenuChef.setImageBitmap(response);

            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                Toast.makeText(getApplicationContext(), "Error en el servidor al cargar imagen de plato", Toast.LENGTH_LONG).show();
            }
        };


        ImageProfileRequest imageProfileRequest = new ImageProfileRequest(rutaImagenMenu, responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(ConfirmOrderActivity.this);

        queue.add(imageProfileRequest);
    }

}

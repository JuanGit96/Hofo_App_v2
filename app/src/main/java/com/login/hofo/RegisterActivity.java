package com.login.hofo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    TextView tv_loginPage;

    EditText etNameUser, etLastNameUser, etEmailUser, etPhonelUser, etAddress, etPasswordUser, etConfirmPasswordUser;

    Button btnRegister;

    ProgressDialog dialog;

    //Para agrupar campos exclusivos de chef
    TextView user_text_type_chef, user_text_have_restaurant, RegisterFormTitle;
    RadioGroup user_radio_type_chef, user_radio_have_restaurant;

    Context thiscontext = this;

    boolean orderInProcess;

    //variables para capturar data de actividad anterior
    String nombre_menu, descripcion_menu;
    int precio_menu, user_id, menu_id, numero_platos, type_order, total_cobro, type_pay, modality;

    String order_address, order_hour, order_additionalComments, chance, food_type;

    //para capturar radios
    int type_chef, have_restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //inicializacion de campos activity
        tv_loginPage = (TextView) findViewById(R.id.tv_goTo_loginPage);

        btnRegister = (Button) findViewById(R.id.user_register);

        etNameUser = (EditText) findViewById(R.id.user_name);
        etLastNameUser = (EditText) findViewById(R.id.user_lastname);
        etEmailUser = (EditText) findViewById(R.id.user_email);
        etAddress = (EditText) findViewById(R.id.user_address);
        etPhonelUser = (EditText) findViewById(R.id.user_phone);
        etPasswordUser = (EditText) findViewById(R.id.user_password);
        etConfirmPasswordUser = (EditText) findViewById(R.id.user_confirmPassword);

        user_text_type_chef = (TextView) findViewById(R.id.user_text_type_chef);
        user_text_have_restaurant = (TextView) findViewById(R.id.user_text_have_restaurant);
        user_radio_type_chef = (RadioGroup) findViewById(R.id.user_radio_type_chef);
        user_radio_have_restaurant = (RadioGroup) findViewById(R.id.user_radio_have_restaurant);
        RegisterFormTitle = (TextView) findViewById(R.id.RegisterFormTitle);

        //validando si sellega al registro desde la creacion de una orden
        if (Order.orderInProcess())
            setVaulesOrder();

        //inicializando variables
        type_chef = 3;
        have_restaurant = 3;

        if (!User.isChef()) //si NO es un chef
        {
            ocultandoCamposParaRegistroComensal();
            RegisterFormTitle.setText("Registro de comensal");
        }
        else
        {
            RegisterFormTitle.setText("Registro de cocinero");
        }

        //Click en ir a login
        tv_loginPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Order.orderInProcess()) //si llegamos al registro por  un menu
                {
                    // Redirigiendo a LoginActivity
                    Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
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

                    RegisterActivity.this.startActivity(intentLogin);
                }
                else
                {
                    // Redirigiendo a LoginActivity
                    Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(intentLogin);
                }

            }
        });

        //Capturando radios en radiogroup
        user_radio_type_chef.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // TODO Auto-generated method stub
                if (checkedId == R.id.profesional){
                    type_chef = 1;
                }else if (checkedId == R.id.amateur){
                    type_chef = 0;
                }

            }

        });

        user_radio_have_restaurant.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub

                if (checkedId == R.id.radio_si){
                    have_restaurant = 1;
                }else if (checkedId == R.id.radio_no){
                    have_restaurant = 0;
                }

            }

        });

        //click en registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new ProgressDialog(RegisterActivity.this);
                dialog.setTitle("Registrando Cocinero");
                dialog.setMessage("Cargando...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
                dialog.setCancelable(false);

                final String nameUser = etNameUser.getText().toString().trim();
                final String lastNameUser = etLastNameUser.getText().toString().trim();
                final String emailUser = etEmailUser.getText().toString().trim();
                final String phoneUser = etPhonelUser.getText().toString().trim();
                final String addressUser = etAddress.getText().toString().trim();
                final String type_chefR = Integer.toString(type_chef);
                final String have_restaurantR = Integer.toString(have_restaurant);
                final String FMCToken = FirebaseInstanceId.getInstance().getToken();

                String role_id = "2";

                if (User.isChef())
                    role_id = "1";


                final String roleUser = role_id;

                final String passUser = etPasswordUser.getText().toString().trim();
                final String passConfirmUser = etConfirmPasswordUser.getText().toString().trim();


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);

                            String code = jsonResponse.getString("code");

                            if (code.equals("201"))
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

                                dialog.dismiss();

                                validateRedirct();

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

                RegisterRequest registerrequest = new RegisterRequest(nameUser, lastNameUser, emailUser, phoneUser, addressUser, roleUser,passUser, passConfirmUser, type_chefR, have_restaurantR, FMCToken, responseListener);

                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

                queue.add(registerrequest);

            }
        });

    }

    private void setVaulesOrder()
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

    private void ocultandoCamposParaRegistroComensal()
    {
        user_text_type_chef.setVisibility(View.GONE);
        user_text_have_restaurant.setVisibility(View.GONE);
        user_radio_type_chef.setVisibility(View.GONE);
        user_radio_have_restaurant.setVisibility(View.GONE);
    }

    private void goToBenefitsPage ()
    {
        Intent intent = new Intent(RegisterActivity.this, BenefitsActivity.class);
        RegisterActivity.this.startActivity(intent);
    }

    private void goToMenus ()
    {
        Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
        RegisterActivity.this.startActivity(intent);
    }
//
//    public  void goToMainActivity ()
//    {
//        Intent intentMain = new Intent(RegisterActivity.this, MainActivity.class);
//        RegisterActivity.this.startActivity(intentMain);
//    }
//
//    private void goToFormularioDePedido()
//    {
//
//        // Redirigiendo a formulario de menu
//        Intent intentFormMenu = new Intent(RegisterActivity.this, DomiciliosFormActivity.class);
//        intentFormMenu.putExtra("nombre_menu", nombre_menu);
//        intentFormMenu.putExtra("descripcion_menu", descripcion_menu);
//        intentFormMenu.putExtra("precio_menu", precio_menu);
//        intentFormMenu.putExtra("user_id", user_id);
//        intentFormMenu.putExtra("menu_id", menu_id);
//        intentFormMenu.putExtra("currentModality", currentModality);
//        RegisterActivity.this.startActivity(intentFormMenu);
//    }

    private void goToCreateOrder ()
    {
        dialog = new ProgressDialog(RegisterActivity.this);
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
                            ConfirmOrderActivity.goToOrderSuccess(RegisterActivity.this, type_pay, menu_id, type_order,false);
                        else
                            ConfirmOrderActivity.goToOrderSuccess(RegisterActivity.this, type_pay, menu_id, type_order,true);
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
                modalityR,chanceR,foodTypeR, Integer.toString(Order.getDomiciliary()),responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);

        queue.add(newOrder);
    }

    private void validateRedirct ()
    {
        if (User.isChef())
        {
            goToBenefitsPage();
        }
        else
        {
            if (Order.orderInProcess())
                goToCreateOrder();
            else
                goToAllMenus();
        }
    }

    private void goToAllMenus()
    {
        Intent intent = new Intent(RegisterActivity.this,MenuActivity.class);
        RegisterActivity.this.startActivity(intent);
    }

}

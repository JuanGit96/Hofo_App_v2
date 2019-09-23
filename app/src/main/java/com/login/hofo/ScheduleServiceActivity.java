package com.login.hofo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleServiceActivity extends ConstructNavigationBar {

    RadioGroup user_radio_modality;

    RadioButton chef_en_casa, en_casa_chef;

    EditText etOcacion, etTiopComida, etNumeroPlatos, etFechaHora, order_additionalComments;

    int modality_ = 0;

    int diner_id,roleId;

    String msg;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_service);

        startNavigationBar();

        user_radio_modality = (RadioGroup) findViewById(R.id.user_radio_modality);

        chef_en_casa = (RadioButton) findViewById(R.id.chef_en_casa);
        en_casa_chef = (RadioButton) findViewById(R.id.en_casa_chef);

        etOcacion = (EditText) findViewById(R.id.etOcacion);
        etTiopComida = (EditText) findViewById(R.id.etTiopComida);
        etNumeroPlatos = (EditText) findViewById(R.id.etNumeroPlatos);
        etFechaHora = (EditText) findViewById(R.id.etFechaHora);
        order_additionalComments = (EditText) findViewById(R.id.order_additionalComments);

        //data de session
        SharedPreferences sp = getSharedPreferences("your_prefs", PerfilActivity.MODE_PRIVATE);
        roleId = sp.getInt("roleId", 0);

        //Capturando radios en radiogroup
        user_radio_modality.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.chef_en_casa){
                    modality_ = 2;
                }else if (checkedId == R.id.en_casa_chef){
                    modality_ = 1;
                }

            }

        });

    }

    public void createSchedule(View view)
    {
        //validando que todos los campos obligatorios esten llenos
        if(!validateCamps())
        {
            Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
            return;
        }

        //confirmacion de orden
        if(!validateLogin())
        {
            goToRegister();
            return;
        }

        //si estamos logueados genera la orden
        dialog = new ProgressDialog(ScheduleServiceActivity.this);
        dialog.setTitle("Registrando Pedido");
        dialog.setMessage("Cargando...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);

        SharedPreferences sp = getSharedPreferences("your_prefs", PerfilActivity.MODE_PRIVATE);
        int idUser = sp.getInt("id", -1);

        final String orderHour = etFechaHora.getText().toString();
        final String orderAdditionalComments = order_additionalComments.getText().toString();
        final String numero_platosR = etNumeroPlatos.getText().toString();
        final String diner_idR = Integer.toString(idUser);

        final String modalityR = Integer.toString(modality_);
        final String ocacionR = etOcacion.getText().toString();
        final String tipoComida = etTiopComida.getText().toString();

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
                        ConfirmOrderActivity.goToOrderSuccess(ScheduleServiceActivity.this, 0,0, 0, true);
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

        NewOrderRequest newOrder = new NewOrderRequest(orderHour, null, numero_platosR,
                orderAdditionalComments, null, diner_idR, null, null, null,
                modalityR,ocacionR,tipoComida, Integer.toString(Order.getDomiciliary()),responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(ScheduleServiceActivity.this);

        queue.add(newOrder);
    }

    private void goToRegister()
    {
        Order.setOrder(true); //especificamos que la orden queda en proceso

        Intent intentLoginComensal = new Intent(ScheduleServiceActivity.this, RegisterActivity.class);
        intentLoginComensal.putExtra("numero_platos",Integer.parseInt(etNumeroPlatos.getText().toString()));
        intentLoginComensal.putExtra("order_hour",etFechaHora.getText().toString());
        intentLoginComensal.putExtra("order_additionalComments",order_additionalComments.getText().toString());

        //especiales para ordenes agendadas
        intentLoginComensal.putExtra("modality",modality_);
        intentLoginComensal.putExtra("chance",etOcacion.getText().toString());
        intentLoginComensal.putExtra("food_type",etTiopComida.getText().toString());

        ScheduleServiceActivity.this.startActivity(intentLoginComensal);
    }

    private boolean validateCamps()
    {
        if (modality_ == 0)
            msg = "Porfavor dinos la modalidad del servicio";

        else if (etOcacion.getText().toString().equals(""))
            msg = "Porfavor dinos la ocacion de tu comida";

        else if (etTiopComida.getText().toString().equals(""))
            msg = "Porfavor dinos que tipo de comida quieres";

        else if (etNumeroPlatos.getText().toString().equals(""))
            msg = "Porfavor el numero de platos";

        else if (etFechaHora.getText().toString().equals(""))
            msg = "Porfavor dinos la hora y fecha de entrega";
        else
            msg = null;

        return (msg == null);

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
}

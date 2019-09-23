package com.login.hofo;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderSuccessActivity extends ConstructNavigationBar {

    int menu_id, type_pay, type_order;

    boolean isSchedule;

    TextView titulo, titulo2, message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        startNavigationBar();

        titulo = (TextView) findViewById(R.id.titulo);
        titulo2 = (TextView) findViewById(R.id.titulo2);
        message = (TextView) findViewById(R.id.message);

        ActividadAnterior();

        setValuesTextView();

        if (!isSchedule) //si no es un servicio agendado
            enviarNotificacionChef();

        defaultValues();
    }

    @Override
    public void onBackPressed() {


        goToMenus();
    }

    public void goToMenus()
    {
        Intent intent = new Intent(OrderSuccessActivity.this, MenuActivity.class);
        OrderSuccessActivity.this.startActivity(intent);
    }

    private void defaultValues()
    {
        //Se termina el proceso de la  orden
        Order.setOrder(false);
    }

    private void ActividadAnterior()
    {
        Intent intent = getIntent();
        menu_id = intent.getIntExtra("menu_id",0);
        type_pay = intent.getIntExtra("type_pay",0);
        type_order = intent.getIntExtra("type_order",0);
        isSchedule = intent.getBooleanExtra("isSchedule",false);
    }

    private void enviarNotificacionChef()
    {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //en caso de exito
                Toast.makeText(OrderSuccessActivity.this, "Notificacion enviada a cocinero", Toast.LENGTH_LONG).show();

                //

            }
        };

        //en caso de error
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
                Toast alertMessage = Toast.makeText(getApplicationContext(), "Error al enviar notificacion a cocinero", Toast.LENGTH_LONG);
                alertMessage.setGravity(Gravity.CENTER, 0, 0);
                alertMessage.show();

            }
        };

        OrderSuccessRequest orderSuccessRequest = new OrderSuccessRequest(menu_id, responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(OrderSuccessActivity.this);

        queue.add(orderSuccessRequest);
    }

    private void setValuesTextView()
    {
        if (isSchedule)
        {
            titulo.setText("Solicitud especial");
            titulo2.setText("Haremos publica tu solicitud a todos nuestros chefs");
            message.setText("Tu soliicitud se ha enviado, en el transcurso\n" +
                    "del día nos comunicaremos por Whatsapp\n" +
                    "con las opciones que nuestros chefs\n" +
                    "preparen para ti");
        } else if (type_pay == 1) {
            titulo.setText("Pago Contraentrega");

            if (type_order == 1)
                titulo2.setText("El pedido va en camino");
            else
                titulo2.setText("El pedido se enviará a la hora indicada");

            message.setText("Recuerda contar con el dinero en efectivo");
        } else {
            titulo.setText("Transferencia Bancaria");
            titulo2.setText("Por favor consigna a la cuenta de ahorros número xxxxxxxxxxxx");
            message.setText("El domiciliario recogerá tu pedido.\n" +
                    "Recuerda que solo se hará entrega del\n" +
                    "mismo una vez se haya realizado la\n" +
                    "transferencia");
        }
    }

    public void goToBack(View view)
    {
        Intent intent = new Intent(OrderSuccessActivity.this, SolicitudesActivity.class);
        OrderSuccessActivity.this.startActivity(intent);
    }
}

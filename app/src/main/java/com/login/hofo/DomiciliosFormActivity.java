package com.login.hofo;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.login.hofo.R.drawable.five_stars;
import static com.login.hofo.R.drawable.four_stars;
import static com.login.hofo.R.drawable.one_star;
import static com.login.hofo.R.drawable.three_stars;
import static com.login.hofo.R.drawable.two_stars;

public class DomiciliosFormActivity extends ConstructNavigationBar {

    String descripcion_menu, nombre_menu, rutaImagenMenu;
    int chef_id, menu_id, diner_id;

    String msg = null;

    int precio_menu, precio_venta;

    String api_token;

    int roleId;
    int type_order = 0;

    int total_cobro;

    TextView nombreMenu, descripcionMenu, precioMenu, precioVenta, nombreUsuario;

    ImageView viewMenuChef;

    ProgressDialog dialog;

    //items del formulario
    EditText numero_platos, order_address, order_hour, order_additionalComments;
    Button btn_ordenar;

    RadioGroup user_radio_type_order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domicilios_form);

        startNavigationBar();

        //data de activity anterior
        Intent intent = getIntent();
        nombre_menu = intent.getStringExtra("nombre_menu");
        descripcion_menu = intent.getStringExtra("descripcion_menu");
        precio_menu = intent.getIntExtra("precio_menu",0);
        precio_venta = intent.getIntExtra("precio_venta",0);
        chef_id = intent.getIntExtra("chef_id",0);
        menu_id = intent.getIntExtra("menu_id",0);
        rutaImagenMenu = intent.getStringExtra("rutaImagenMenu");

        getImageMenu();

        //data de session
        SharedPreferences sp = getSharedPreferences("your_prefs", PerfilActivity.MODE_PRIVATE);
        api_token = sp.getString("api_token", null);
        roleId = sp.getInt("roleId", 0);

        nombreMenu = (TextView) findViewById(R.id.name_menu);
        descripcionMenu = (TextView) findViewById(R.id.descriptionMenuChef);
        //precioMenu = (TextView) findViewById(R.id.priceMenuChef);
        precioVenta = (TextView) findViewById(R.id.precio_venta);
        nombreUsuario = (TextView) findViewById(R.id.user_name);

        viewMenuChef = (ImageView) findViewById(R.id.viewMenuChef);

        user_radio_type_order = (RadioGroup) findViewById(R.id.user_radio_type_order);

        //elementos del formulario
        numero_platos = (EditText) findViewById(R.id.order_number_people);
        order_address = (EditText) findViewById(R.id.order_address);
        order_hour = (EditText) findViewById(R.id.order_hour);
        order_additionalComments = (EditText) findViewById(R.id.order_additionalComments);
        btn_ordenar = (Button) findViewById(R.id.btn_ordenar);

        //cargando datos del menu
        nombreMenu.setText(nombre_menu);
        //precioMenu.setText(Integer.toString(precio_menu));

        //tachando textView
        //precioMenu.setPaintFlags(precioMenu.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        precioVenta.setText(Integer.toString(precio_venta));
        descripcionMenu.setText(descripcion_menu);

        //Capturando radios en radiogroup
        user_radio_type_order.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.inmediata){
                    type_order = 1;
                    order_hour.setText("Entrega inmedata");
                    order_hour.setEnabled(false);
                }else if (checkedId == R.id.programada){
                    type_order = 2;
                    order_hour.setText("");
                    order_hour.setEnabled(true);
                }

            }

        });

        //click en ordenar
        btn_ordenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //validando que todos los campos obligatorios esten llenos
                if(!validateCamps())
                {
                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                    return;
                }

                total_cobro = (precio_venta * Integer.parseInt(numero_platos.getText().toString().trim())) + Order.getDomiciliary();

                //confirmacion de orden
                goToConfirmOrder();

            }
        });
    }

    private void goToConfirmOrder ()
    {
        Intent intentLoginComensal = new Intent(DomiciliosFormActivity.this, ConfirmOrderActivity.class);
        //datos de menú
        intentLoginComensal.putExtra("nombreMenu",nombreMenu.getText().toString());
        intentLoginComensal.putExtra("descripcionMenu",descripcionMenu.getText().toString());
        intentLoginComensal.putExtra("suma","("+precio_venta+" x "+numero_platos.getText().toString()+") +"+Order.getDomiciliary()+" = "+total_cobro);
        intentLoginComensal.putExtra("rutaImagenMenu",rutaImagenMenu);
        //datos de la orden
        intentLoginComensal.putExtra("numero_platos",Integer.parseInt(numero_platos.getText().toString()));
        intentLoginComensal.putExtra("order_address",order_address.getText().toString());
        intentLoginComensal.putExtra("order_hour",order_hour.getText().toString());
        intentLoginComensal.putExtra("order_additionalComments",order_additionalComments.getText().toString());
        intentLoginComensal.putExtra("type_order",type_order);
        intentLoginComensal.putExtra("menu_id",menu_id);
        intentLoginComensal.putExtra("total_cobro",total_cobro);
        DomiciliosFormActivity.this.startActivity(intentLoginComensal);
    }

    private boolean validateCamps()
    {
        if (type_order == 0)
            msg = "Porfavor dinos si la entrega es inmediata o programada";

        else if (order_address.getText().toString().equals(""))
            msg = "Porfavor dinos la direccion de entrega";

        else if (type_order == 2 && order_hour.getText().toString().equals(""))
            msg = "Porfavor dinos la hora de entrega";
        else
            msg = null;

        return (msg == null);

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

        RequestQueue queue = Volley.newRequestQueue(DomiciliosFormActivity.this);

        queue.add(imageProfileRequest);
    }


}

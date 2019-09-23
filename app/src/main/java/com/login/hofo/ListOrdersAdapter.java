package com.login.hofo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ListOrdersAdapter extends ArrayAdapter<Solicitud> {

    //the list values in the List of type hero
    List<Solicitud> solicitudList;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    ProgressDialog dialog;


    //constructor initializing the values
    public ListOrdersAdapter(Context context, int resource, List<Solicitud> solicitudList) {
        super(context, resource, solicitudList);
        this.context = context;
        this.resource = resource;
        this.solicitudList = solicitudList;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view

//        final TextView nombre_plato = view.findViewById(R.id.nombre_plato);
        final TextView tvNameMenuChef = view.findViewById(R.id.nameMenuChef);
        final TextView tvObservacionMenu = view.findViewById(R.id.observacionesMenuChef);
        final TextView tvAmountPeople = view.findViewById(R.id.numeroDePersonas);
        final TextView tvStatusOrder = view.findViewById(R.id.statusOrder);
        final ImageView viewMenuChef = view.findViewById(R.id.viewMenuChef);

        //botones
        final Button btn_aceptar = (Button) view.findViewById(R.id.btn_aceptar);
        final Button btn_rechazar = (Button) view.findViewById(R.id.btn_rechazar);

        if (!User.isChef())
        {
            btn_aceptar.setVisibility(View.GONE);
            btn_rechazar.setVisibility(View.GONE);
        }

        LinearLayout lClick = view.findViewById(R.id.orderClick);

        //getting the hero of the specified position
        final Solicitud solicitud = solicitudList.get(position);

        Picasso.get()
                .load(UrlApi.getUrlServer()+"storage/"+solicitud.getFotoMenu())
                .placeholder(R.drawable.imagen_predeterminada)
                .error(R.drawable.imagen_predeterminada)
                .into(viewMenuChef);


        if (solicitud.getStatus() != 0)
        {
            btn_aceptar.setVisibility(View.GONE);
            btn_rechazar.setVisibility(View.GONE);
        }

        if (solicitud.getStatus() == 1)
        {
            tvStatusOrder.setVisibility(View.VISIBLE);
            tvStatusOrder.setText("ACEPTADO");
        }


        if (solicitud.getStatus() == 2)
        {
            tvStatusOrder.setVisibility(View.VISIBLE);
            tvStatusOrder.setText("RECHAZADO");
        }

        //LLenando dinamicamente cada elemento
        tvNameMenuChef.setText(solicitud.getNameMenu());
        tvObservacionMenu.setText((solicitud.getAdditional_comments().equals("null")?"--":solicitud.getAdditional_comments()));
        tvAmountPeople.setText(Integer.toString(solicitud.getAmount_people()));

        //adding a click listener to the button to remove item from the list
        lClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //removePregon(position);

                //al dar click en el elemento de la lista
                goToOrderDetail(
                    solicitud.getId()
                );

            }
        });


        //accion de los botones de accion
        btn_aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateOrder(solicitud.getId(),1);
            }
        });

        btn_rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateOrder(solicitud.getId(),2);
            }
        });

        //finally returning the view
        return view;
    }

    //this method will remove the item from the list
    private void removePregon(final int position) {
        //Creating an alert dialog to confirm the deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Are you sure you want to delete this?");

        //if the response is positive in the alert
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //removing the item
                solicitudList.remove(position);

                //reloading the list
                notifyDataSetChanged();
            }
        });

        //if response is negative nothing is being done
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //creating and displaying the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void goToOrderDetail(int order_id)
    {

    }

    private void checkOrder(String id_menu)
    {
        // Redirigiendo a Bienvenida a usuario por su registro
        Intent intentEdit = new Intent(getContext(), EditMenyActivity.class);
        intentEdit.putExtra("id_menu", id_menu);
        getContext().startActivity(intentEdit);
    }

    private void updateOrder(int order_id,int status)
    {
        dialog = new ProgressDialog(getContext());
        if (status == 1)
            dialog.setTitle("Aceptando la orden");
        if (status == 2)
            dialog.setTitle("Rechazando la orden");

        dialog.setMessage("Cargando...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);

        final String statusR = Integer.toString(status);
        final String orderR = Integer.toString(order_id);

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

                        dialog.dismiss();

                        String msg =(statusR.equals("1"))?"aceptada":"rechazada";

                        // Mostrando mensaje de exito
                        Toast alertMessage = Toast.makeText(getContext(),"La orden a sido "+msg+" correctamente", Toast.LENGTH_LONG);
                        alertMessage.setGravity(Gravity.CENTER, 0, 0);
                        alertMessage.show();

                        reloadActivity();
                    }

                    if (code.equals("400"))
                    {
                        //Mostrando errores en pantalla

                        String error = jsonResponse.getString("error");

                        dialog.dismiss();

                        Toast alertMessage = Toast.makeText(getContext(),error, Toast.LENGTH_LONG);
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
                dialog.dismiss();
                Toast.makeText(getContext(), "Error en el servidor al editar orden -> "+volleyError, Toast.LENGTH_LONG).show();
            }
        };

        UpdateOrderRequest updateOrderRequest = new UpdateOrderRequest(orderR, statusR, responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        queue.add(updateOrderRequest);
    }

    private void reloadActivity()
    {
        Intent intent = new Intent(getContext(), SolicitudesActivity.class);
        getContext().startActivity(intent);
    }


}

package com.login.hofo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.login.hofo.UrlApi.getUrlApi;

public class NewOrderRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL =  getUrlApi()+"orders";

    private Map<String,String> params;

    NewOrderRequest(String hour, String address, String amount_people,
                    String additional_comments, String menu_id, String diner_id, String type_order,
                    String type_pay, String total_cobrar, String modalidad, String ocacion, String tipoCmida, String domiciliary,
                    Response.Listener<String> listener, Response.ErrorListener errorListener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, errorListener);

        params = new HashMap<>();

        params.put("hour",hour);

        if (address != null)
            params.put("address",address);

        params.put("amount_people",amount_people);

        if (type_order != null && !type_order.equals("0"))
            params.put("type_order",type_order);



        if (additional_comments != null)
            params.put("additional_comments",additional_comments);

        if (menu_id != null && !menu_id.equals("0"))
            params.put("menu_id",menu_id);


        params.put("diner_id",diner_id);

        if (total_cobrar != null && !total_cobrar.equals("0"))
            params.put("total_charge",total_cobrar);



        if (type_pay != null && !type_pay.equals("0"))
            params.put("type_pay",type_pay);




        //para solicitudes agendadas

        if (modalidad != null && !modalidad.equals("0"))
            params.put("modality",modalidad);



        if (ocacion != null)
            params.put("chance",ocacion);

        if (tipoCmida != null)
            params.put("food_type",tipoCmida);

        if (modalidad != null && ocacion != null && tipoCmida != null)
            params.put("isSchedule","1");

        params.put("domiciliary", domiciliary);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

}

package com.login.hofo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.login.hofo.UrlApi.getUrlApi;

public class UpdateOrderRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL =  getUrlApi()+"orders/";

    private Map<String,String> params;

    UpdateOrderRequest(String order_id, String status, Response.Listener<String> listener, Response.ErrorListener errorListener)
    {
        super(Method.PATCH, REGISTER_REQUEST_URL+order_id, listener, errorListener);

        params = new HashMap<>();

        params.put("status",status);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }


}

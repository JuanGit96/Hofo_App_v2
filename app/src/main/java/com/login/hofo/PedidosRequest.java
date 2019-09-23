package com.login.hofo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import static com.login.hofo.UrlApi.getUrlApi;

public class PedidosRequest extends StringRequest {

    private static final String USER_REQUEST_URL =  getUrlApi()+"ordersByDiner/";

    PedidosRequest (String api_token, int diner_id, Response.Listener<String> listener, Response.ErrorListener errorListener)
    {
        super(Method.GET, USER_REQUEST_URL+diner_id+"?api_token="+api_token, listener, errorListener);
    }
}

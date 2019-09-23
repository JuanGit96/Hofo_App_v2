package com.login.hofo;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import static com.login.hofo.UrlApi.getUrlApi;

class ListMenusRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL =  getUrlApi()+"menus/";


    ListMenusRequest (String api_token, Response.Listener<String> listener, Response.ErrorListener errorListener)
    {
        super(Method.GET, REGISTER_REQUEST_URL+"?api_token="+api_token, listener, errorListener);
    }


}

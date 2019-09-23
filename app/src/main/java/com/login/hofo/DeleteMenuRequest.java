package com.login.hofo;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import static com.login.hofo.UrlApi.getUrlApi;

public class DeleteMenuRequest extends StringRequest {

    private static final String USER_REQUEST_URL =  getUrlApi()+"menus/";

    DeleteMenuRequest (String api_token, String menu_id, Response.Listener<String> listener, Response.ErrorListener errorListener)
    {
        super(Method.DELETE, USER_REQUEST_URL+menu_id+"?api_token="+api_token, listener, errorListener);
    }
}

package com.login.hofo;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.login.hofo.UrlApi.getUrlApi;


public class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL =  getUrlApi()+"register";

    private Map<String,String> params;

    RegisterRequest (String nameUser, String lastNameUser, String emailUser, String phoneUser, String addressUser, String roleUser,
                     String passUser, String passConfirmUser, String type_chefR, String have_restaurantR, String FMCToken, Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);

        params = new HashMap<>();

        params.put("name",nameUser);
        params.put("lastName",lastNameUser);
        params.put("email",emailUser);
        params.put("password",passUser);
        params.put("password_confirmation",passConfirmUser);
        params.put("address",addressUser);
        params.put("role_id",roleUser);
        params.put("phone",phoneUser);
        params.put("FMCToken",FMCToken);

        if (!type_chefR.equals("3"))
            params.put("type_chef",type_chefR);

        if (!have_restaurantR.equals("3"))
            params.put("have_restaurant_or_foodPoint",have_restaurantR);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

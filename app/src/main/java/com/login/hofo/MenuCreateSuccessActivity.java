package com.login.hofo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MenuCreateSuccessActivity extends ConstructNavigationBar {

    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_create_success);

        startNavigationBar();

        message = (TextView) findViewById(R.id.message);

        Intent intent = getIntent();
        String message_text = intent.getStringExtra("message");

        message.setText(message_text);

    }

    public void goToMenus(View view) {
        Intent intent = new Intent(MenuCreateSuccessActivity.this, MenuActivity.class);
        MenuCreateSuccessActivity.this.startActivity(intent);
    }
}

package com.login.hofo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FaqListActivity extends ConstructNavigationBar {

    Button modalidades, functioning, howToUpMenu, requests, pays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_list);

        startNavigationBar();

        modalidades = (Button) findViewById(R.id.modalities);
        functioning = (Button) findViewById(R.id.functioning);
        howToUpMenu = (Button) findViewById(R.id.howToUpMenu);
        requests = (Button) findViewById(R.id.requests);
        pays = (Button) findViewById(R.id.pays);


        //al dar click
        modalidades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFaq(1);
            }
        });

        functioning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFaq(2);
            }
        });

        howToUpMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFaq(3);
            }
        });

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFaq(4);
            }
        });

        pays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFaq(5);
            }
        });
    }

    private void goToFaq(int faq)
    {
        Intent intent = new Intent(FaqListActivity.this,FaqActivity.class);
        intent.putExtra("faqNumber",faq);
        FaqListActivity.this.startActivity(intent);
    }

}

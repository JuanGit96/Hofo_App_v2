package com.login.hofo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FaqActivity extends ConstructNavigationBar {

    private static final int MODALITIES = 1;
    private static final int FUNCTIONING = 2;
    private static final int HOWTOUPMENU = 3;
    private static final int REQUESTS = 4;
    private static final int PAYS = 5;

    //Modalidades
    LinearLayout modalities, functioning, howToUpMenu, requests, pays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        startNavigationBar();

        modalities = (LinearLayout) findViewById(R.id.modalities);
        functioning = (LinearLayout) findViewById(R.id.functioning);
        howToUpMenu = (LinearLayout) findViewById(R.id.howToUpMenu);
        requests = (LinearLayout) findViewById(R.id.requests);
        pays = (LinearLayout) findViewById(R.id.pays);

        //variables de actividad anterior
        Intent intent = getIntent();
        int faqNumber = intent.getIntExtra("faqNumber",0);

        showFaq(faqNumber);
    }

    private void showFaq(int faqNumber)
    {
        switch (faqNumber)
        {
            case MODALITIES:
                modalities.setVisibility(View.VISIBLE);
                break;

            case FUNCTIONING:
                functioning.setVisibility(View.VISIBLE);
                break;

            case HOWTOUPMENU:
                howToUpMenu.setVisibility(View.VISIBLE);
                break;

            case REQUESTS:
                requests.setVisibility(View.VISIBLE);
                break;

            case PAYS:
                pays.setVisibility(View.VISIBLE);
                break;

            default:
                Toast.makeText(FaqActivity.this,"Preginta no encontrada",Toast.LENGTH_LONG).show();
        }
    }

    public void goBack(View view)
    {
        Intent intent = new Intent(FaqActivity.this, FaqListActivity.class);
        FaqActivity.this.startActivity(intent);
    }
}

package com.login.hofo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class CreateMenuActivity extends ConstructNavigationBar {

    EditText edNameMenu, edDescriptionMenu, edPriceMenu, edIngredientsMenu, edUtenciliosMenu;

    Button addImagePlato, actionMenu;

    ImageView viewMenu;

    ProgressDialog dialog;

    RadioGroup type_menu;

    Context thiscontext = this;

    //captura de fotos
    String photoMenuString = "";
    //Ruta para guardar imagen desde celular
    private final String CARPETA_RAIZ = "misImagenesHofo/";
    private final String RUTA_IMAGEN = CARPETA_RAIZ+"misFotos";
    //Guardar ruta de imagen (almacenamiento)
    String path;

    String api_token;
    int idUser;

    int type_menu_;


    //codigos para diferenciar si se carga una imagen desde galeria o si es tomada desde camara
    private final int COD_GALERIA = 10;
    private final int COD_CAMARA = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_menu);

        startNavigationBar();

        SharedPreferences sp = getSharedPreferences("your_prefs", CreateMenuActivity.MODE_PRIVATE);
        api_token = sp.getString("api_token", null);
        idUser = sp.getInt("id", -1);

        edNameMenu = (EditText) findViewById(R.id.edNameMenu);
        edDescriptionMenu = (EditText) findViewById(R.id.edDescriptionMenu);
        edPriceMenu = (EditText) findViewById(R.id.edPriceMenu);


        addImagePlato = (Button) findViewById(R.id.addImagePlato);
        actionMenu = (Button) findViewById(R.id.actionMenu);

        viewMenu = (ImageView) findViewById(R.id.viewMenu);

        type_menu = (RadioGroup) findViewById(R.id.type_menu);

        type_menu_ = 3;

        //Capturando radios en radiogroup
        type_menu.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.semanal){
                    type_menu_ = 2;
                }else if (checkedId == R.id.diario){
                    type_menu_ = 1;
                }

            }

        });


        //validando permisos
        if (validaPermisos())
        {
            addImagePlato.setEnabled(true);
            actionMenu.setEnabled(true);
        }
        else
        {
            Toast errorPermisionMessage = Toast.makeText(getApplicationContext(),"Para que la aplicacion funcione necesitamos que aceptes todos los permisos", Toast.LENGTH_LONG);
            errorPermisionMessage.show();

            addImagePlato.setEnabled(true);
            actionMenu.setEnabled(true);
        }

        actionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createMenu();

            }
        });

    }


    public boolean validaPermisos ()
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if ((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)  && (checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED))
            return true;


        if ((shouldShowRequestPermissionRationale(CAMERA))  ||  (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))  )
            cargarDialogoRecomendacion();
        else
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);


        return false;

    }

    //Se le dice al usuario que tiene que habilitar permisos para poder usar la aplicacion
    public void cargarDialogoRecomendacion ()
    {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(CreateMenuActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la aplicacion");
        dialogo.setCancelable(false);

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
            }
        });

        dialogo.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100)  //pregunta nuevamente por los permisos
        {
            if (grantResults.length == 2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED)
            {
                addImagePlato.setEnabled(true);
                actionMenu.setEnabled(true);
            }
            else
            {
                solicitarPermisosManual();
            }
        }

    }

    public void solicitarPermisosManual ()
    {
        final CharSequence[] opciones = {"SI","NO"};

        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(CreateMenuActivity.this);
        alertOpciones.setTitle("¿Prefiere dar permisos de forma manual?");

        //Seteando las opciones
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (opciones[which].equals("SI"))
                {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }
                else
                {
                    Toast alertPermisos = Toast.makeText(getApplication(),"Necesitamos dichos permisos para el funcionamiento de la aplicacion",Toast.LENGTH_SHORT);
                    alertPermisos.setGravity(Gravity.CENTER, 0, 0);
                    alertPermisos.show();
                    dialog.dismiss();

                    goToMainActivity();
                }
            }
        });

        alertOpciones.show();
    }

    //Tomando foto
    public void uploadImageMenu (View view)
    {
        cargarImagen();
    }


    @SuppressLint("IntentReset")
    public void cargarImagen()
    {

        final CharSequence[] opciones = {"Tomar foto", "Cargar imagen","Cancelar"};

        final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(CreateMenuActivity.this);
        alertOpciones.setTitle("seleccione una opcion");

        //Seteando las opciones
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (opciones[which].equals("Tomar foto"))
                {
                    tomarFotografia();
                }
                else if (opciones[which].equals("Cargar imagen"))
                {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //ACTION_PICK O ACTION_GET_CONTENT

                    intent.setType("image/");

                    startActivityForResult(intent.createChooser(intent, "Seleccione la aplicacion"),COD_GALERIA); // va al metodo onActivityResult
                }
                else
                {
                    dialog.dismiss();
                }
            }
        });

        alertOpciones.show();

    }

    public void tomarFotografia()
    {
        //Abre la imagen
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);

        String nombre_imagen = "";

        //valida si la imagen se crecorrectamente
        boolean isCreada = fileImagen.exists();


        if (!isCreada)
        {
            isCreada = fileImagen.mkdirs();
        }

        if (isCreada)
        {
            nombre_imagen = (System.currentTimeMillis()/1000)+".jpg";
        }


        //ruta de almacenamiento
        path = Environment.getExternalStorageDirectory()+ File.separator+RUTA_IMAGEN+File.separator+nombre_imagen;

        //Creando archivo
        File imagen = new File(path);


        //Lanzar la aplicacion de camara
        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }


        startActivityForResult(intent,COD_CAMARA);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Bitmap bitmap;
            Bitmap bitmapVideo;

            switch (requestCode)
            {
                case COD_GALERIA:

                    Uri miPath = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), miPath);
                        bitmap = redimencionarImagen(bitmap, 300,400);


                        viewMenu.setImageBitmap(bitmap);
                        photoMenuString = convertFileToString(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;


                case  COD_CAMARA:

                    MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override //si el proceso termino completamente
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("Ruta de almacenamiento","Path: "+path);
                        }
                    });

                    bitmap = BitmapFactory.decodeFile(path);
                    bitmap = redimencionarImagen(bitmap, 300,400);


                    viewMenu.setImageBitmap(bitmap);
                    photoMenuString = convertFileToString(bitmap);

                    break;

                default:
                    Toast.makeText(getApplication(),"Otro contactese con servicio tecnico",Toast.LENGTH_SHORT).show();


            }

        }
        else
        {
            Toast.makeText(getApplication(),"Error en la aplicacion porfavor contacte al servicio tecnico",Toast.LENGTH_SHORT).show();
        }
    }

    public String convertFileToString (Bitmap bitmapFile)
    {
        String fileString = "";

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmapFile.compress(Bitmap.CompressFormat.JPEG,100,array);

        byte[] fileByte = array.toByteArray();
        fileString = Base64.encodeToString(fileByte,Base64.DEFAULT);

        //Toast.makeText(getApplication(),fileString,Toast.LENGTH_SHORT).show();

        return fileString;
    }

    public Bitmap redimencionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo)
    {

        int ancho=bitmap.getWidth();

        int alto=bitmap.getHeight();

        if (ancho >anchoNuevo || alto>altoNuevo)
        {
            float escalaAncho = anchoNuevo/ancho;
            float escalaAlto =altoNuevo/alto;

            Matrix matrix = new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);

            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);
        }

        return bitmap;

    }

    public  void goToMainActivity ()
    {
        Intent intentMain = new Intent(CreateMenuActivity.this, MainActivity.class);
        CreateMenuActivity.this.startActivity(intentMain);
    }

    public void createMenu ()
    {
        dialog = new ProgressDialog(CreateMenuActivity.this);
        dialog.setTitle("Creando tu menú");
        dialog.setMessage("Cargando...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();
        dialog.setCancelable(false);

        final String nameMenu = edNameMenu.getText().toString().trim();
        final String description = edDescriptionMenu.getText().toString().trim();
        final String price = edPriceMenu.getText().toString().trim();
        final String photo = photoMenuString;
        final String type_menu_R = Integer.toString(type_menu_);



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

                        // Mostrando mensaje de exito
                        Toast alertMessage = Toast.makeText(getApplicationContext(),"Tu plato ha sido creado exitosamente", Toast.LENGTH_LONG);
                        alertMessage.setGravity(Gravity.CENTER, 0, 0);
                        alertMessage.show();

                        goToCreateSuccess();
                    }

                    if (code.equals("400"))
                    {
                        //Mostrando errores en pantalla

                        String error = jsonResponse.getString("error");

                        dialog.dismiss();

                        Toast alertMessage = Toast.makeText(getApplicationContext(),error, Toast.LENGTH_LONG);
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
                Toast.makeText(getApplicationContext(), "Error en el servidor al crear menu -> "+volleyError, Toast.LENGTH_LONG).show();
            }
        };

        CreateMenuRequest createMenuRequest = new CreateMenuRequest(api_token, Integer.toString(idUser),nameMenu, description, price, photo, type_menu_R, responseListener, errorListener);

        RequestQueue queue = Volley.newRequestQueue(CreateMenuActivity.this);

        queue.add(createMenuRequest);
    }

    private void goToCreateSuccess()
    {
        Intent intent = new Intent(CreateMenuActivity.this, MenuCreateSuccessActivity.class);
        intent.putExtra("message", "Tu menú se ha guardado con éxito.\n" +
                "Recuerda que se eliminará dependiendo del tiempo que escogiste");
        CreateMenuActivity.this.startActivity(intent);
    }

    public void goBack(View view) {
        Intent intent = new Intent(CreateMenuActivity.this, MenuActivity.class);
        CreateMenuActivity.this.startActivity(intent);
    }


//    public String validarModalidadesSeleccionadas()
//    {
//        String modalidades = "";
//
//        if (modality1.isChecked())
//            modalidades = modalidades+"1_";
//
//        if (modality2.isChecked())
//            modalidades = modalidades+"2_";
//
//        if (modality3.isChecked())
//            modalidades = modalidades+"3_";
//
//        return modalidades;
//    }
}

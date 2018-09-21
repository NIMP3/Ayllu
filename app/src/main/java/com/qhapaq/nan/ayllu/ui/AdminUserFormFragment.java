package com.qhapaq.nan.ayllu.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.qhapaq.nan.ayllu.domain.Mensaje;
import com.qhapaq.nan.ayllu.domain.usuario.Usuario;
import com.qhapaq.nan.ayllu.R;
import com.qhapaq.nan.ayllu.domain.usuario.UsuarioDbHelper;
import com.qhapaq.nan.ayllu.io.ApiConstants;
import com.qhapaq.nan.ayllu.io.AylluApiService;
import com.qhapaq.nan.ayllu.ui.utilities.ToolbarUtility;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.regex.Pattern;

public class AdminUserFormFragment extends Fragment implements View.OnClickListener {

    Activity activity;

    //Views del Formulario
    private EditText etID, etName, etSurname, etEmail, etWork, etPassword, etConfirmation;
    private TextInputLayout tilID, tilName, tilSurname, tilEmail, tilWork, tilPassword, tilConfirmation;

    //Variables globales
    private String transaction_type;
    private UsuarioDbHelper usuarioDbHelper;
    private String id, name, surname, email, work, pais = "", codigo = "";

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        transaction_type = intent.getExtras().getString("TYPE");
        usuarioDbHelper = new UsuarioDbHelper(getActivity());

        if (transaction_type.equals("UPDATE")){
            id = intent.getExtras().getString("ID");
            name = intent.getExtras().getString("NAME");
            surname = intent.getExtras().getString("SURNAME");
            codigo = intent.getExtras().getString("CODIGO");
            email = intent.getExtras().getString("EMAIL");
            work = intent.getExtras().getString("WORK");
        }

        //--------------------------------------------------------------------------------------
        //Obtenemos el pais del administrador
        Cursor cursor = usuarioDbHelper.generateQuery("SELECT * FROM ");
        if (cursor.moveToFirst()) pais = "0" + cursor.getString(7);
        cursor.close();
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_user_form, container, false);
        activity = getActivity();
        ToolbarUtility.showToolbar(activity, view, getResources().getString(R.string.form_admin_title_register), false);

        etID = view.findViewById(R.id.et_id);
        etName = view.findViewById(R.id.et_name);
        etSurname = view.findViewById(R.id.et_surname);
        etEmail = view.findViewById(R.id.etEmail);
        etWork = view.findViewById(R.id.etWork);
        etPassword = view.findViewById(R.id.et_psw);
        etConfirmation = view.findViewById(R.id.et_conf_psw);


        tilID = view.findViewById(R.id.til_id);
        tilName = view.findViewById(R.id.til_name);
        tilSurname = view.findViewById(R.id.til_surname);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilWork = view.findViewById(R.id.tilWork);
        tilPassword = view.findViewById(R.id.til_psw);
        tilConfirmation = view.findViewById(R.id.til_conf_psw);

        FloatingActionButton fbTransaction = view.findViewById(R.id.fab_transaction);
        if (transaction_type.equals("UPDATE")) {
            fbTransaction.setImageDrawable(getResources().getDrawable(R.drawable.ic_refresh));
            ToolbarUtility.showToolbar(activity, view, getResources().getString(R.string.form_admin_title_edit), false);

            etID.setText(id);
            etName.setText(name);
            etSurname.setText(surname);
            etEmail.setText(email);
            etWork.setText(work);
        }
        fbTransaction.setOnClickListener(this);

        return view;
    }

    /**
     * =============================================================================================
     * METODO:
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_transaction:
                InputMethodManager manager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);

                processTransaction();
                break;
        }
    }

    /**
     * =============================================================================================
     * METODO:
     */
    private void processTransaction() {
        boolean band = false;
        if (transaction_type.equals("UPDATE")){
            if(etPassword.getText().toString().equals("") && etConfirmation.getText().toString().equals("")){
                tilPassword.setError(null);
                tilConfirmation.setError(null);
                if (isValidFields("PARCIAL")) band = true;
            }
            else{
                if (isValidFields("TOTAL")) band = true;
            }
        }
        else {
            if (isValidFields("TOTAL")) band = true;
        }

        if(band){
            //Creamos el Usuario y preparamos el servicio
            //String pass = SHA1.getHash(etPassword.getText().toString(), "SHA1");
            Usuario new_user = new Usuario(
                    codigo, etID.getText().toString(), etName.getText().toString(),
                    etSurname.getText().toString(), etEmail.getText().toString(), etWork.getText().toString(),
                    "M", "", etPassword.getText().toString(), pais
            );

            if (transaction_type.equals("UPDATE")) {
                ProgressDialog loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.edit_form_process_message_update_monitor), getResources().getString(R.string.registration_form_process_message), false, false);
                updateUser(loading, new_user);
            }
            else{
                ProgressDialog loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.registration_form_process_shipping_status), getResources().getString(R.string.registration_form_process_message), false, false);
                registerUser(loading, new_user);
            }
        }
    }

    private void registerUser(final ProgressDialog progressDialog, Usuario new_user) {
        final Fragment fragment = new MonitoringInfoFragment();
        final Bundle params = new Bundle();
        params.putString("TIPO","USER");

        Retrofit retrofit = prepareRetrofit();
        AylluApiService service = retrofit.create(AylluApiService.class);
        Call<Mensaje> call = service.registrarUsuario(new_user);
        call.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()){
                    Mensaje mensaje = response.body();
                    if(mensaje.getEstado().equals("1")){
                        Toast login = Toast.makeText(getActivity(),
                                getResources().getString(R.string.registration_form_process_successful_message), Toast.LENGTH_LONG);
                        login.show();

                        //Limpiar los datos del formulario
                        etID.setText("");
                        etName.setText("");
                        etSurname.setText("");
                        etPassword.setText("");
                        etConfirmation.setText("");

                        params.putString("RESULT","OK");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.transaction_principal_context, fragment)
                                .commit();
                    }
                }
                else {
                    params.putString("RESULT","ERROR");
                    fragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.transaction_principal_context, fragment)
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {
                progressDialog.dismiss();
                params.putString("RESULT","ERROR");
                fragment.setArguments(params);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.transaction_principal_context, fragment)
                        .commit();
            }
        });
    }

    /**
     * =============================================================================================
     * METODO:
     */
    private void updateUser(final ProgressDialog progressDialog, Usuario new_user) {
        final Fragment fragment = new MonitoringInfoFragment();
        final Bundle params = new Bundle();
        params.putString("TIPO","EDIT");

        Retrofit retrofit = prepareRetrofit();
        AylluApiService service = retrofit.create(AylluApiService.class);
        Call<Mensaje> call = service.actualizarUsuario(new_user);
        call.enqueue(new Callback<Mensaje>() {
            @Override
            public void onResponse(Call<Mensaje> call, Response<Mensaje> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()){
                    Mensaje mensaje = response.body();
                    if(mensaje.getEstado().equals("1")){
                        //Limpiar los datos del formulario
                        etID.setText("");
                        etName.setText("");
                        etSurname.setText("");
                        etPassword.setText("");
                        etConfirmation.setText("");

                        params.putString("RESULT","Ok");
                        fragment.setArguments(params);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .add(R.id.transaction_principal_context, fragment)
                                .commit();
                    }
                }
                else {
                    params.putString("RESULT","ERROR");
                    fragment.setArguments(params);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .add(R.id.transaction_principal_context, fragment)
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<Mensaje> call, Throwable t) {
                progressDialog.dismiss();
                params.putString("RESULT","ERROR");
                fragment.setArguments(params);

                getActivity().getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.transaction_principal_context, fragment)
                        .commit();
            }
        });
    }

    /**
     * =============================================================================================
     * METODO:
     */
    private boolean isValidFields(String type) {
        boolean equals_passwords = false;
        boolean id, name, surname, email, work, password, confirmation;

        id = isValidID(etID.getText().toString(), tilID);
        name = isValidText(etName.getText().toString(), tilName);
        surname = isValidText(etSurname.getText().toString(), tilSurname);
        email = isValidEmail(etEmail.getText().toString(), tilEmail);
        work = isValidText(etWork.getText().toString(), tilWork);


        if (type.equals("TOTAL")){
            password = isValidPassword(etPassword.getText().toString(), tilPassword);
            confirmation = isValidPassword(etConfirmation.getText().toString(), tilConfirmation);

            if (password && confirmation) equals_passwords = isSamePasswords();
            return id && name && surname && email && work && equals_passwords;
        }

        return id && name && surname && email && work;
    }

    /**
     * =============================================================================================
     * METODO: Valida los datos de las cajas de texto (Nombre, Apellido, Contraseñas)
     */
    private boolean isValidText(String texto, TextInputLayout til) {
        Pattern patron = Pattern.compile("^[a-zA-Z ]+$");

        if (!patron.matcher(texto).matches() || texto.length() > 30) {
            til.setError(getResources().getString(R.string.registration_form_alert_invalid_field));
            return false;
        }
        else til.setError(null);

        return true;
    }

    //TODO: Construir el metodo para validar un Correo electronico
    /*----------------------------------------------------------------------------------------------
    * Valida los si un texto es o no un correo electronico
    *
    * @param texto : Texto al cual se le aplica el analisis
    * @param <code>TextInputLayout til</code> : View para enviar el error
    *
    * @return <code>Boolean</code> : Respuesta verdadera si cumple con el analisis o falsa en caso
    * contrario*/
    private boolean isValidEmail(String texto, TextInputLayout til) {
        return true;
    }

    /**
     * =============================================================================================
     * METODO: Valida si una identificación es correcta
     */
    private boolean isValidID(String texto, TextInputLayout til) {
        if (texto.length() <= 0){
            til.setError(getResources().getString(R.string.registration_form_alert_empty_field));
            return false;
        }
        else til.setError(null);

        return true;
    }

    /**
     * =============================================================================================
     * METODO: Valida si una contraseña es correcta
     */
    private boolean isValidPassword(String texto, TextInputLayout til) {
        Pattern patron = Pattern.compile("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
        if (!patron.matcher(texto).matches() || texto.length() > 30) {
            til.setError(getResources().getString(R.string.registration_form_alert_password));
            return false;
        }
        else til.setError(null);

        return true;
    }

    /**
     * =============================================================================================
     * METODO: Valida si las contraseñas ingresadas son iguales
     */
    private boolean isSamePasswords() {
        if (!etPassword.getText().toString().equals(etConfirmation.getText().toString())) {
            tilPassword.setError(getResources().getString(R.string.registration_form_alert_different_passwords));
            tilConfirmation.setError(getResources().getString(R.string.registration_form_alert_different_passwords));
            return false;
        } else {
            tilPassword.setError(null);
            tilConfirmation.setError(null);
        }

        return true;
    }

    private Retrofit prepareRetrofit() {
        //------------------------------------------------------------------------------------------
        //Preparamos el servicio de Retrofit
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        return new Retrofit.Builder()
                .baseUrl(ApiConstants.URL_API_AYLLU)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient.build())
                .build();
    }
}

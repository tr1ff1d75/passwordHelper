package com.belstu.gulko.passwordHelper.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.belstu.gulko.passwordHelper.APIService;
import com.belstu.gulko.passwordHelper.Client;
import com.belstu.gulko.passwordHelper.R;
import com.belstu.gulko.passwordHelper.model.AppStatus;
import com.belstu.gulko.passwordHelper.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.belstu.gulko.passwordHelper.Client.BaseURL;


public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    Button register;
    Button existinguser;
    String str_Password, str_RePassword, str_Email;
    EditText edt_Password, edt_RePassword, edt_Email;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    AppStatus appStatus;
    public static String SOAP_RESP="Server is offl1ne";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();
        appStatus=new AppStatus(getApplicationContext());
        register = (Button) findViewById(R.id.btn_register);
        existinguser = (Button) findViewById(R.id.existinguser);
        edt_Password = (EditText) findViewById(R.id.edt_Rpassword);
        edt_RePassword = (EditText) findViewById(R.id.edt_RRepassword);
        edt_Email = (EditText) findViewById(R.id.edt_email);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        register.setOnClickListener(this);
        existinguser.setOnClickListener(this);

        String title_1 = null;
        try {
            title_1= new SoapAsyncTask().execute("offline").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, SOAP_RESP, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        if(v==existinguser) {

            Intent go = new Intent(RegistrationActivity.this, LoginActivity.class);

            startActivity(go);

        }

        if (appStatus.isOnline()) {
            str_Password = edt_Password.getText().toString();
            str_RePassword = edt_RePassword.getText().toString();
            str_Email = edt_Email.getText().toString();
            if (str_Email.length() == 0 & str_Password.length() == 0
                    & str_RePassword.length() == 0) {
                Toast.makeText(getApplicationContext(),
                        R.string.registration_all_fields_mandatory, Toast.LENGTH_LONG)
                        .show();
            } else if (str_Password.length() == 0) {
                Toast.makeText(getApplicationContext(),
                        R.string.registration_enterPass, Toast.LENGTH_LONG).show();
            } else if (str_RePassword.length() == 0) {
                Toast.makeText(getApplicationContext(),
                        R.string.registration_enterRePass, Toast.LENGTH_LONG).show();
            } else if (str_Email.length() == 0) {
                Toast.makeText(getApplicationContext(),
                        R.string.registration_enter_email, Toast.LENGTH_LONG).show();
            } else if (str_Password.contains(str_RePassword) != str_RePassword
                    .contains(str_Password)) {
                Toast.makeText(getApplicationContext(),
                        R.string.registration_pass_notmatch, Toast.LENGTH_LONG)
                        .show();
            }  else {
                progressBar.setVisibility(View.VISIBLE);
                SplashActivity.editor.putString("password", str_RePassword);
                SplashActivity.editor.putString("email", str_Email);
                SplashActivity.editor.commit();

                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(str_Email, str_Password)
                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {

                                    try {
                                        throw task.getException();
                                    } catch (FirebaseNetworkException e) {
                                        Toast.makeText(RegistrationActivity.this,
                                                R.string.registration_network_problem,Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(RegistrationActivity.this,
                                                R.string.registration_error+String.valueOf(task.getException()),Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(RegistrationActivity.this,
                                            R.string.registration_error+String.valueOf(task.getException()),Toast.LENGTH_SHORT).show();


                                    if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                        Toast.makeText(RegistrationActivity.this,
                                                R.string.registration_email_collision, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(RegistrationActivity.this,
                                            R.string.registration_check_email_verify, Toast.LENGTH_LONG).show();
                                    sendVerificationEmail();

                                    //отправка данных о юзере на сервер
                                    //SaveUser save=new SaveUser();
                                    //save.execute();

                                    User user = new User(str_Email, str_Password);


                                    APIService service = Client.getService();
                                    Call<Void> call = service.saveUser(user);
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                                            }
                                            else Toast.makeText(RegistrationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {
                                            Toast.makeText(RegistrationActivity.this, "Not connected to the server", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
            }
        } else {
            Toast.makeText(getApplicationContext(),R.string.appstatus_no_connection,Toast.LENGTH_LONG).show();
        }
    }
   private void sendVerificationEmail(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent sendtoLogin = new Intent(getApplicationContext(),
                            LoginRegistrationActivity.class);
                    startActivity(sendtoLogin);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(RegistrationActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private class SoapAsyncTask extends AsyncTask<String, Void, String> {

        String request;

        @Override
        protected String doInBackground(String... strings) {
            request = SOAPRequest(strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }

        private String SOAPRequest(String option) {
            String SOAP_ACTION = "http://gulko.com/soap/getNameRequest";
            String METHOD_NAME = "getNameRequest";
            String NAMESPACE = "http://gulko.com/soap";
            String URL = BaseURL+"/soapws/name.wsdl";

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            PropertyInfo info = new PropertyInfo();
            info.setName("name_project");
            info.setValue(option);
            info.setType(String.class);
            request.addProperty(info);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
            try {
                httpTransportSE.debug = true;
                httpTransportSE.call(SOAP_ACTION, envelope);
                String str = "Запрос\n" + httpTransportSE.requestDump + "\n" +
                        "Ответ\n" + httpTransportSE.responseDump;
                SoapPrimitive response = (SoapPrimitive ) envelope.getResponse();
                Log.d("SOAP ___ ",str+ response.toString());
                SOAP_RESP=response.toString();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

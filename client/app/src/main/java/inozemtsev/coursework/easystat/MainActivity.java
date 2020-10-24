package inozemtsev.coursework.easystat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import inozemtsev.coursework.easystat.configure.UserState;
import inozemtsev.coursework.easystat.questionnaires.UserAttributes;
import inozemtsev.coursework.easystat.utils.EasyStatRequestType;
import inozemtsev.coursework.easystat.utils.NetWorkUtils;
import inozemtsev.coursework.easystat.utils.Request;
import inozemtsev.coursework.easystat.utils.RequestType;

public class MainActivity extends AppCompatActivity {

    Button buttonIgnoreAuth;
    Button buttonSignIn;
    Button buttonSignUp;

    LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonIgnoreAuth = findViewById(R.id.bt_ignore_auth);
        buttonSignIn = findViewById(R.id.bt_sign_in);
        buttonSignUp = findViewById(R.id.bt_sign_up);

        rootLayout = findViewById(R.id.root);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpWindow();
            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignInWindow();
            }
        });
        buttonIgnoreAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity();
            }
        });
    }

    class EasyStatSignUpTask extends AsyncTask<Request, Void, String>{

        @Override
        protected String doInBackground(Request... requests) {
            String response = null;
            try{
                switch (requests[0].getType()){
                    case GET:{
                        response = NetWorkUtils.SendGetRequest(requests[0].getUrl());
                        break;
                    }
                    case POST:{
                        response = NetWorkUtils.SendPostRequest(
                                requests[0].getUrl(),
                                requests[0].getBody(),
                                requests[0].getToken());
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String response){
            try {
                JSONObject jsonUser = new JSONObject(response);
                UserState.firstName = jsonUser.getString(UserAttributes.FIRST_NAME);
                UserState.username = jsonUser.getString(UserAttributes.USERNAME);
                UserState.lastName = jsonUser.getString(UserAttributes.LAST_NAME);
                UserState.email = jsonUser.getString(UserAttributes.EMAIL);
                UserState.id = jsonUser.getLong(UserAttributes.ID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            URL signInUrl = NetWorkUtils.GenerateURL(EasyStatRequestType.SIGN_IN, null);
            JSONObject signInForm = new JSONObject();
            try {
                signInForm.put(UserAttributes.USERNAME, UserState.username);
                signInForm.put(UserAttributes.PASSWORD, UserState.password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new EasyStatSignInTask().execute(new Request(signInUrl, signInForm.toString(), RequestType.POST));
        }
    }

    class EasyStatSignInTask extends AsyncTask<Request, Void, String>{

        @Override
        protected String doInBackground(Request... requests) {
            String response = null;
            try{
                switch (requests[0].getType()){
                    case GET:{
                        response = NetWorkUtils.SendGetRequest(requests[0].getUrl());
                        break;
                    }
                    case POST:{
                        response = NetWorkUtils.SendPostRequest(
                                requests[0].getUrl(),
                                requests[0].getBody(),
                                requests[0].getToken());
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String response){
            try {
                JSONObject tokenForm = new JSONObject(response);
                UserState.firstName = tokenForm.getString(UserAttributes.FIRST_NAME);
                UserState.username = tokenForm.getString(UserAttributes.USERNAME);
                UserState.lastName = tokenForm.getString("lastname");
                UserState.email = tokenForm.getString(UserAttributes.EMAIL);
                UserState.id = tokenForm.getLong(UserAttributes.ID);
                UserState.token = tokenForm.getString(UserAttributes.TOKEN);
                UserState.isAuth = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void showSignUpWindow() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View signUpWindow = inflater.inflate(R.layout.sign_up_layout, null);
        dialog.setView(signUpWindow);

        final AlertDialog show = dialog.show();

        final MaterialEditText nickname = signUpWindow.findViewById(R.id.et_sign_up_nickname);
        final MaterialEditText name = signUpWindow.findViewById(R.id.et_sign_up_name);
        final MaterialEditText surname = signUpWindow.findViewById(R.id.et_sign_up_surname);
        final MaterialEditText email = signUpWindow.findViewById(R.id.et_sign_up_email);
        final MaterialEditText password = signUpWindow.findViewById(R.id.et_sign_up_password);

        final Button signUpButton = signUpWindow.findViewById(R.id.bt_sign_up_form_button);
        final Button cancelButton = signUpWindow.findViewById(R.id.bt_sign_up_form_cancel);



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nickname.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "Введите ваш никнейм", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(TextUtils.isEmpty(name.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "Введите ваш имя", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(TextUtils.isEmpty(surname.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "Введите ваш фамилию", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(TextUtils.isEmpty(email.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "Введите ваш email", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                if(password.getText().toString().length() <= 5){
                    Toast toast = Toast.makeText(getApplicationContext(), "Пароль должен быть не менее 6 символов", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                JSONObject newUser = new JSONObject();
                try {
                    newUser.put(UserAttributes.USERNAME, nickname.getText().toString());
                    newUser.put(UserAttributes.FIRST_NAME, name.getText().toString());
                    newUser.put(UserAttributes.LAST_NAME, surname.getText().toString());
                    newUser.put(UserAttributes.EMAIL, email.getText().toString());
                    newUser.put(UserAttributes.PASSWORD, password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                URL url = NetWorkUtils.GenerateURL(EasyStatRequestType.SIGN_UP, null);
                new EasyStatSignUpTask().execute(new Request(url, newUser.toString(), RequestType.POST));
                changeActivity();
                show.dismiss();
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });

    }

    private void showSignInWindow() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View signInWindow = inflater.inflate(R.layout.sign_in_layout, null);
        dialog.setView(signInWindow);

        final AlertDialog show = dialog.show();

        final Button signInButton = signInWindow.findViewById(R.id.bt_sign_in_form_button);
        final Button cancelButton = signInWindow.findViewById(R.id.bt_sign_in_from_cancel);

        final MaterialEditText nickname = signInWindow.findViewById(R.id.et_sign_in_nickname);
        final MaterialEditText password = signInWindow.findViewById(R.id.et_sign_in_password);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(nickname.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "Введите ваш никнейм", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if(TextUtils.isEmpty(password.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "Введите ваш пароль", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                JSONObject user = new JSONObject();
                try {
                    user.put(UserAttributes.USERNAME, nickname.getText().toString());
                    user.put(UserAttributes.PASSWORD, password.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                URL url = NetWorkUtils.GenerateURL(EasyStatRequestType.SIGN_IN, null);


                new MainActivity.EasyStatSignInTask().execute(new Request(url, user.toString(), RequestType.POST));
                changeActivity();
                show.dismiss();
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });

    }

    private void changeActivity(){
        Context context = MainActivity.this;
        Class questionnaireListActivity = QuestionnaireListActivity.class;

        Intent questionnaireListIntent = new Intent(context, questionnaireListActivity);
        startActivity(questionnaireListIntent);
    }



}
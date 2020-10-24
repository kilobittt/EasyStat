package inozemtsev.coursework.easystat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import inozemtsev.coursework.easystat.adaptors.QuestionnairesAdapter;
import inozemtsev.coursework.easystat.configure.UserState;
import inozemtsev.coursework.easystat.questionnaires.QuestionItem;
import inozemtsev.coursework.easystat.questionnaires.QuestionnaireItem;
import inozemtsev.coursework.easystat.questionnaires.QuestionnaireTypes;
import inozemtsev.coursework.easystat.questionnaires.UserAttributes;
import inozemtsev.coursework.easystat.utils.EasyStatRequestType;
import inozemtsev.coursework.easystat.utils.NetWorkUtils;
import inozemtsev.coursework.easystat.utils.Request;
import inozemtsev.coursework.easystat.utils.RequestType;

public class ProfileActivity extends AppCompatActivity {
    private final String QUESTIONS_NUMBER_TITTLE = "Количество вопросов: ";


    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private LinearLayout withAuthLinearLayout;
    private LinearLayout withoutAuthLinearLayout;

    private Button signInButton;
    private Button signUpButton;

    private Button createNewQuestionnaireButton;

    private TextView nicknameTextView;
    private TextView fullNameTextView;
    private TextView emailTextView;

    private RecyclerView myQuestionnaireList;
    private QuestionnairesAdapter questionnairesAdapter;

    private List<QuestionnaireItem> myQuestionnaires = new ArrayList<>();

    class EasyStatGetAllMyQuestionnaire extends AsyncTask<Request, Void, String> {

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


            List<QuestionnaireItem> responseItems = new ArrayList<>();
            try {
                JSONArray questionnairesAsArray = new JSONArray(response);
                for(int i = 0; i < questionnairesAsArray.length(); ++i){
                    JSONObject questionnaire = questionnairesAsArray.getJSONObject(i);
                    QuestionnaireItem currentItem  = new QuestionnaireItem(questionnaire);
                    currentItem.setTittle(QUESTIONS_NUMBER_TITTLE + currentItem.getQuestionsNumber());

                    switch (questionnaire.getString("type")){
                        case QuestionnaireTypes.FUNNY:{
                            currentItem.setImageResource(R.drawable.ic_baseline_emoji_emotions_24);
                            responseItems.add(currentItem);
                            break;
                        }
                        case QuestionnaireTypes.SCHOOL:{
                            currentItem.setImageResource(R.drawable.ic_baseline_school_24);
                            responseItems.add(currentItem);
                            break;
                        }
                        case QuestionnaireTypes.PSYCHOLOGY:{
                            currentItem.setImageResource(R.drawable.ic_baseline_message_24);
                            responseItems.add(currentItem);
                            break;
                        }
                        case QuestionnaireTypes.SOCIOLOGY:{
                            currentItem.setImageResource(R.drawable.ic_baseline_people_24);
                            responseItems.add(currentItem);
                            break;
                        }
                        case QuestionnaireTypes.PERSONAL:{
                            currentItem.setImageResource(R.drawable.ic_baseline_assignment_24);
                            responseItems.add(currentItem);
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                }

                myQuestionnaires = responseItems;
                setRecycleView(responseItems);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class EasyStatSignUpTask extends AsyncTask<Request, Void, String> {

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
            new ProfileActivity.EasyStatSignInTask().execute(new Request(signInUrl, signInForm.toString(), RequestType.POST));
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

                showProfile();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        withAuthLinearLayout = findViewById(R.id.ll_with_auth_profile);
        withoutAuthLinearLayout = findViewById(R.id.ll_without_auth_profile);

        signInButton = findViewById(R.id.bt_sign_in_from_profile);
        signInButton.setOnClickListener(signInListener);

        signUpButton = findViewById(R.id.bt_sign_up_from_profile);
        signUpButton.setOnClickListener(signUpListener);

        createNewQuestionnaireButton = findViewById(R.id.bt_create_new_questionnaire);
        createNewQuestionnaireButton.setOnClickListener(createNewQuestionnaireListener);

        nicknameTextView = findViewById(R.id.tv_profile_nickname);
        fullNameTextView = findViewById(R.id.tv_profile_full_name);
        emailTextView = findViewById(R.id.tv_profile_email);

        myQuestionnaireList = findViewById(R.id.rv_my_questionnaire);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myQuestionnaireList.setLayoutManager(layoutManager);

        bottomNavigationView = findViewById(R.id.btnv_main_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nvbt_profile);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Context context = ProfileActivity.this;

                switch (item.getItemId()){
                    case R.id.nvbt_stat:{
                        startActivity(new Intent(context, StatActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    }
                    case R.id.nvbt_profile:{
                        return true;
                    }
                    case R.id.nvbt_my_questionnaire:{
                        startActivity(new Intent(context, QuestionnaireListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    }
                }
                return false;
            }
        });

        if(UserState.isAuth){
            showProfile();
        }else{
            withAuthLinearLayout.setVisibility(View.INVISIBLE);
            withoutAuthLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    void showProfile(){
        URL myQuestionnaireURL = NetWorkUtils.GenerateURL(EasyStatRequestType.FIND_QUESTIONNAIRE_WITH_USER_ID, UserState.id.toString());
        new ProfileActivity.EasyStatGetAllMyQuestionnaire().execute(new Request(myQuestionnaireURL, null, RequestType.GET));

        nicknameTextView.setText(UserState.username);
        String fullName = UserState.firstName + " " + UserState.lastName;
        fullNameTextView.setText(fullName);
        emailTextView.setText(UserState.email);

        withAuthLinearLayout.setVisibility(View.VISIBLE);
        withoutAuthLinearLayout.setVisibility(View.INVISIBLE);
    }

    void setRecycleView(List<QuestionnaireItem> questionnaireItemsList){
        questionnairesAdapter = new QuestionnairesAdapter(questionnaireItemsList, this);
        myQuestionnaireList.setAdapter((questionnairesAdapter));
    }

    private void showSignInWindow(){
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


                new ProfileActivity.EasyStatSignInTask().execute(new Request(url, user.toString(), RequestType.POST));
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

    private void showSignUpWindow(){
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
                new ProfileActivity.EasyStatSignUpTask().execute(new Request(url, newUser.toString(), RequestType.POST));
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

    private void showNewQuestionnaireWindow(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View creatingWindow = inflater.inflate(R.layout.new_questionnaire_layout, null);
        dialog.setView(creatingWindow);

        final AlertDialog show = dialog.show();

        final Button createButton = creatingWindow.findViewById(R.id.bt_create_new_questionnaire_in_form);
        final Button cancelButton = creatingWindow.findViewById(R.id.bt_cancel_creating_form);

        final MaterialEditText questionnaireName = creatingWindow.findViewById(R.id.et_new_questionnaire_name);
        final MaterialEditText questionnaireDescription = creatingWindow.findViewById(R.id.et_new_questionnaire_description);


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(questionnaireName.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "У опросника должно быть название", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                if(TextUtils.isEmpty(questionnaireDescription.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "У опросника должно быть описание", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                QuestionnaireItem newQuestionnaire = new QuestionnaireItem();
                newQuestionnaire.setName(questionnaireName.getText().toString());
                newQuestionnaire.setDescription(questionnaireDescription.getText().toString());
                newQuestionnaire.setAccess("PRIVATE");
                newQuestionnaire.setMarked(true);
                newQuestionnaire.setOwnerId(UserState.id);
                newQuestionnaire.setType(QuestionnaireTypes.PERSONAL);
                newQuestionnaire.setRepresented(false);


                Context context = ProfileActivity.this;
                Intent creatingIntent = new Intent(context, CreatingQuestionnaireActivity.class);
                creatingIntent.putExtra("questionnaire", newQuestionnaire.toStringJSON());
                startActivity(creatingIntent);

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

    View.OnClickListener signInListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showSignInWindow();
        }
    };

    View.OnClickListener signUpListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showSignUpWindow();
        }
    };

    View.OnClickListener createNewQuestionnaireListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showNewQuestionnaireWindow();
        }
    };
}
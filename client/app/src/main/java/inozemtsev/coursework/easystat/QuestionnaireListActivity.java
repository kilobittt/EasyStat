package inozemtsev.coursework.easystat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inozemtsev.coursework.easystat.adaptors.QuestionnairesAdapter;
import inozemtsev.coursework.easystat.questionnaires.QuestionnaireItem;
import inozemtsev.coursework.easystat.utils.EasyStatRequestType;
import inozemtsev.coursework.easystat.utils.NetWorkUtils;
import inozemtsev.coursework.easystat.utils.Request;
import inozemtsev.coursework.easystat.utils.RequestType;
import inozemtsev.coursework.easystat.utils.SearchFormRequest;
import inozemtsev.coursework.easystat.utils.SearchFormResponse;


public class QuestionnaireListActivity extends AppCompatActivity {
    private static final String NOT_FOUND_FOR_ID_AND_NAME_MESSAGE = "Опросников с таким именем и ID не удалось найти. Попробуйте указать только имя или только ID.\nВы ввели:";
    private static final String NOT_FOUND_FOR_ID_MESSAGE = "Опросник с таким ID не удалось найти.\nВы ввели:";
    private static final String NOT_FOUND_NAME_MESSAGE = "Опросников с таким именем не найденно.\nВы ввели:";
    private final String NOT_FOUND_SERVER_MESSAGE = "NOT FOUND";
    private final String QUESTIONS_NUMBER_TITTLE = "Количество вопросов: ";
    private final String FUNNY_TEXT = "FUNNY";
    private final String SCHOOL_TEXT = "SCHOOL";
    private final String PSYCHOLOGY_TEXT = "PSYCHOLOGY";
    private final String SOCIOLOGY_TEXT = "SOCIOLOGY";

    private Toolbar toolbar;
    private RecyclerView questionnaireList;
    private BottomNavigationView bottomNavigationView;

    private QuestionnairesAdapter questionnairesAdapter;

    private Map<String, List<QuestionnaireItem>> questionnaires = new HashMap<>();


    class EasyStatGetAllQuestionnaire extends AsyncTask<Request, Void, String> {

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
            List<QuestionnaireItem> funnyQuestionnaires = new ArrayList<>();
            List<QuestionnaireItem> schoolQuestionnaires = new ArrayList<>();
            List<QuestionnaireItem> psychologyQuestionnaires = new ArrayList<>();
            List<QuestionnaireItem> sociologicalQuestionnaires = new ArrayList<>();

            List<QuestionnaireItem> responseItems = new ArrayList<>();
            try {
                JSONArray questionnairesAsArray = new JSONArray(response);
                for(int i = 0; i < questionnairesAsArray.length(); ++i){
                    JSONObject questionnaire = questionnairesAsArray.getJSONObject(i);
                    QuestionnaireItem currentItem  = new QuestionnaireItem(questionnaire);
                    currentItem.setTittle(QUESTIONS_NUMBER_TITTLE + currentItem.getQuestionsNumber());
                    switch (questionnaire.getString("type")){
                        case FUNNY_TEXT:{
                            currentItem.setImageResource(R.drawable.ic_baseline_emoji_emotions_24);
                            funnyQuestionnaires.add(currentItem);
                            responseItems.add(currentItem);
                            break;
                        }
                        case SCHOOL_TEXT:{
                            currentItem.setImageResource(R.drawable.ic_baseline_school_24);
                            schoolQuestionnaires.add(currentItem);
                            responseItems.add(currentItem);
                            break;
                        }
                        case PSYCHOLOGY_TEXT:{
                            currentItem.setImageResource(R.drawable.ic_baseline_message_24);
                            psychologyQuestionnaires.add(currentItem);
                            responseItems.add(currentItem);
                            break;
                        }
                        case SOCIOLOGY_TEXT:{
                            currentItem.setImageResource(R.drawable.ic_baseline_people_24);
                            sociologicalQuestionnaires.add(currentItem);
                            responseItems.add(currentItem);
                            break;
                        }
                        default:{
                            break;
                        }
                    }
                }

                questionnaires.put(FUNNY_TEXT, funnyQuestionnaires);
                questionnaires.put(SCHOOL_TEXT, schoolQuestionnaires);
                questionnaires.put(PSYCHOLOGY_TEXT, psychologyQuestionnaires);
                questionnaires.put(SOCIOLOGY_TEXT, sociologicalQuestionnaires);

                setRecycleView(responseItems);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class EasyStatFindQuestionnaire extends AsyncTask<SearchFormRequest, Void, SearchFormResponse> {

        String getResponse(Request request){
            String response = null;
            try{
                switch (request.getType()){
                    case GET:{
                        response = NetWorkUtils.SendGetRequest(request.getUrl());
                        break;
                    }
                    case POST:{
                        response = NetWorkUtils.SendPostRequest(
                                request.getUrl(),
                                request.getBody(),
                                request.getToken());
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected SearchFormResponse doInBackground(SearchFormRequest... requests) {
            SearchFormResponse response = new SearchFormResponse();
            SearchFormRequest request = requests[0];

            if(request.searchByIdRequest == null){
                String currentResponse = getResponse(request.searchByNameRequest);
                if(currentResponse == null) {
                    response.searchByNameResponse = NOT_FOUND_SERVER_MESSAGE;
                }else{
                    response.searchByNameResponse = currentResponse;
                }
                response.questionnaireName = request.questionnaireName;
                response.questionnaireId = request.questionnaireId;
            }else{
                String currentResponse = getResponse(request.searchByIdRequest);
                if(currentResponse == null){
                    response.searchByIdResponse = NOT_FOUND_SERVER_MESSAGE;
                }else {
                    response.searchByIdResponse = currentResponse;
                }
                response.questionnaireId = request.questionnaireId;
                if(request.searchByNameRequest != null){
                    response.questionnaireName = request.questionnaireName;
                }
            }

            return response;
        }

        QuestionnaireItem getItem(JSONObject questionnaire){
            QuestionnaireItem currentItem = null;
            try {
                currentItem  = new QuestionnaireItem(questionnaire);
                currentItem.setTittle(QUESTIONS_NUMBER_TITTLE + currentItem.getQuestionsNumber());
                switch (questionnaire.getString("type")){
                    case FUNNY_TEXT:{
                        currentItem.setImageResource(R.drawable.ic_baseline_emoji_emotions_24);
                        break;
                    }
                    case SCHOOL_TEXT:{
                        currentItem.setImageResource(R.drawable.ic_baseline_school_24);
                        break;
                    }
                    case PSYCHOLOGY_TEXT:{
                        currentItem.setImageResource(R.drawable.ic_baseline_message_24);
                        break;
                    }
                    case SOCIOLOGY_TEXT:{
                        currentItem.setImageResource(R.drawable.ic_baseline_people_24);
                        break;
                    }
                    default:{
                        break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return currentItem;
        }


        protected void onPostExecute(SearchFormResponse response){
            List<QuestionnaireItem> responseItems = new ArrayList<>();
            boolean needToRefresh = true;
            if(response.searchByIdResponse != null){
                if(response.searchByIdResponse.equals(NOT_FOUND_SERVER_MESSAGE)){
                    needToRefresh = false;
                    if(response.searchByNameResponse != null){
                        showNotFoundWindow(NOT_FOUND_FOR_ID_AND_NAME_MESSAGE +
                                "\nID: " + response.questionnaireId +
                                "\nНазвание: " + response.questionnaireName);
                    }else{
                        showNotFoundWindow(NOT_FOUND_FOR_ID_MESSAGE +
                                "\nID: " + response.questionnaireId);
                    }
                }
                QuestionnaireItem currentItem = null;
                try {
                    currentItem = getItem(new JSONObject(response.searchByIdResponse));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(response.questionnaireName != null
                && !response.questionnaireName.equals(currentItem.getName())){
                    needToRefresh = false;
                    showNotFoundWindow(NOT_FOUND_FOR_ID_AND_NAME_MESSAGE +
                            "\nID: " + response.questionnaireId +
                            "\nНазвание: " + response.questionnaireName);
                }else{
                    responseItems.add(currentItem);
                }
            }else{
                try {
                    if(response.searchByNameResponse.equals(NOT_FOUND_SERVER_MESSAGE)){
                        needToRefresh = false;
                        showNotFoundWindow(NOT_FOUND_NAME_MESSAGE +
                                "\nНазвание: " + response.questionnaireName);
                    }
                    JSONArray questionnairesAsArray = new JSONArray(response.searchByNameResponse);
                    if(questionnairesAsArray.length() == 0){
                        needToRefresh = false;
                        showNotFoundWindow(NOT_FOUND_NAME_MESSAGE +
                                "\nНазвание: " + response.questionnaireName);
                    }
                    for(int i = 0; i < questionnairesAsArray.length(); ++i) {
                        JSONObject questionnaire = questionnairesAsArray.getJSONObject(i);
                        QuestionnaireItem currentItem = getItem(questionnaire);
                        responseItems.add(currentItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(needToRefresh) {
                questionnairesAdapter.filterList(responseItems);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire_list);

        URL allQuestionnaireURL = NetWorkUtils.GenerateURL(EasyStatRequestType.GET_ALL_QUESTIONNAIRES_WITH_ACCESS, "PUBLIC");
        new QuestionnaireListActivity.EasyStatGetAllQuestionnaire().execute(new Request(allQuestionnaireURL, null, RequestType.GET));

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        questionnaireList = findViewById(R.id.rv_questionnaire);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        questionnaireList.setLayoutManager(layoutManager);

        bottomNavigationView = findViewById(R.id.btnv_main_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nvbt_my_questionnaire);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Context context = QuestionnaireListActivity.this;

                switch (item.getItemId()){
                    case R.id.nvbt_stat:{
                        startActivity(new Intent(context, StatActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    }
                    case R.id.nvbt_profile:{
                        startActivity(new Intent(context, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    }
                    case R.id.nvbt_my_questionnaire:{
                        return true;
                    }
                }

                return false;
            }
        });
    }

    void setRecycleView(List<QuestionnaireItem> questionnaireItemsList){
        questionnairesAdapter = new QuestionnairesAdapter(questionnaireItemsList, this);
        questionnaireList.setAdapter((questionnairesAdapter));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.tb_filter:{
                showFilterWindow();
                break;
            }
            case R.id.tb_search:{
                showSearchWindow();
                break;
            }
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void showNotFoundWindow(String message) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View notFoundWindow = inflater.inflate(R.layout.not_found_layout, null);
        dialog.setView(notFoundWindow);

        final AlertDialog show = dialog.show();

        final TextView notFoundMessage = notFoundWindow.findViewById(R.id.tv_not_found_message);
        notFoundMessage.setText(message);
        final Button okButton = notFoundWindow.findViewById(R.id.b_ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
            }
        });
    }

    private void showSearchWindow() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View searchWindow = inflater.inflate(R.layout.search_layout, null);
        dialog.setView(searchWindow);

        final AlertDialog show = dialog.show();

        final MaterialEditText id = searchWindow.findViewById(R.id.et_questionnaire_id);
        final MaterialEditText questionnaireName = searchWindow.findViewById(R.id.et_questionnaire_name);

        final Button searchButton = searchWindow.findViewById(R.id.b_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(id.getText().toString())
                && TextUtils.isEmpty(questionnaireName.getText().toString())){
                    Toast toast = Toast.makeText(getApplicationContext(), "Вы ничего не ввели", Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }

                SearchFormRequest request = new SearchFormRequest();
                if(!TextUtils.isEmpty(id.getText().toString())){
                    URL url = NetWorkUtils.GenerateURL(EasyStatRequestType.FIND_QUESTIONNAIRE_BY_ID, id.getText().toString());
                    request.searchByIdRequest = new Request(url,null,RequestType.GET);
                    request.questionnaireId = id.getText().toString();
                }

                if(!TextUtils.isEmpty(questionnaireName.getText().toString())){
                    URL url = NetWorkUtils.GenerateURL(EasyStatRequestType.FIND_QUESTIONNAIRES_WITH_NAME, questionnaireName.getText().toString());
                    request.searchByNameRequest = new Request(url,null,RequestType.GET);
                    request.questionnaireName = questionnaireName.getText().toString();
                }

                new QuestionnaireListActivity.EasyStatFindQuestionnaire().execute(request);
                show.dismiss();
            }
        });
    }

    private void showFilterWindow(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View filterWindow = inflater.inflate(R.layout.filter_questionnaire_group_layout, null);
        dialog.setView(filterWindow);

        final AlertDialog show = dialog.show();

        final CheckBox funnyCheckBox = filterWindow.findViewById(R.id.chb_funny);
        final CheckBox schoolCheckBox = filterWindow.findViewById(R.id.chb_school);
        final CheckBox psychologyCheckBox = filterWindow.findViewById(R.id.chb_psychology);
        final CheckBox sociologyCheckBox = filterWindow.findViewById(R.id.chb_sociology);

        final Button applyFilterButton = filterWindow.findViewById(R.id.b_filter_button);
        //TODO Переделать фильтрацию, чтобы они в группы не сталкивались
        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<QuestionnaireItem> filteredItems = new ArrayList<>();
                if(funnyCheckBox.isChecked() && questionnaires.get(FUNNY_TEXT) != null) {
                    filteredItems.addAll(questionnaires.get(FUNNY_TEXT));
                }
                if(schoolCheckBox.isChecked() && questionnaires.get(SCHOOL_TEXT) != null) {
                    filteredItems.addAll(questionnaires.get(SCHOOL_TEXT));
                }
                if(psychologyCheckBox.isChecked() && questionnaires.get(PSYCHOLOGY_TEXT) != null) {
                    filteredItems.addAll(questionnaires.get(PSYCHOLOGY_TEXT));
                }
                if(sociologyCheckBox.isChecked() && questionnaires.get(SOCIOLOGY_TEXT) != null) {
                    filteredItems.addAll(questionnaires.get(SOCIOLOGY_TEXT));
                }
                questionnairesAdapter.filterList(filteredItems);
                show.dismiss();
            }
        });
    }


}
package inozemtsev.coursework.easystat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.customview.widget.ViewDragHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;


import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import inozemtsev.coursework.easystat.questionnaires.AnswerItem;
import inozemtsev.coursework.easystat.questionnaires.OptionItem;
import inozemtsev.coursework.easystat.questionnaires.QuestionItem;
import inozemtsev.coursework.easystat.questionnaires.QuestionTypes;
import inozemtsev.coursework.easystat.questionnaires.QuestionnaireItem;
import inozemtsev.coursework.easystat.swipes.SwipeDetector;
import inozemtsev.coursework.easystat.utils.EasyStatRequestType;
import inozemtsev.coursework.easystat.utils.NetWorkUtils;
import inozemtsev.coursework.easystat.utils.Request;
import inozemtsev.coursework.easystat.utils.RequestType;
import inozemtsev.coursework.easystat.utils.SearchFormRequest;

public class QuestionActivity extends AppCompatActivity {
    private final int START_POSITION = 0;
    private final int ANSWERS_MAX_COUNT = 8;
    private final List<Integer> checkboxesIds = Arrays.asList(
            R.id.chb_answer1,
            R.id.chb_answer2,
            R.id.chb_answer3,
            R.id.chb_answer4,
            R.id.chb_answer5,
            R.id.chb_answer6,
            R.id.chb_answer7,
            R.id.chb_answer8
    );

    private final List<Integer> radioButtonsIds = Arrays.asList(
            R.id.rdb_answer1,
            R.id.rdb_answer2,
            R.id.rdb_answer3,
            R.id.rdb_answer4,
            R.id.rdb_answer5,
            R.id.rdb_answer6,
            R.id.rdb_answer7,
            R.id.rdb_answer8
    );


    TextView questionnaireNameTextView;
    TextView questionnaireDescriptionTextView;
    Button startQuestionnaireButton;
    Toolbar toolbar;

    LinearLayout headerLinearLayout;
    LinearLayout questionLinearLayout;

    TextView questionHeaderTextView;
    TextView questionDescriptionTextView;

    Button nextQuestionButton;
    Button prevQuestionButton;
    Button endQuestionnaireButton;

    ScrollView checkboxesScrollView;
    ScrollView radioButtonsScrollView;
    ScrollView textAnswerScrollView;

    List<RadioButton> radioButtons = new ArrayList<>();
    List<CheckBox> checkBoxes = new ArrayList<>();
    MaterialEditText textAnswer;

    private SwipeDetector swipeDetector;

    QuestionnaireItem questionnaire;
    List<QuestionItem> questions = new ArrayList<>();
    List<AnswerItem> answers = new ArrayList<>();
    int currentQuestionPosition = -1;

    class EasyStatSendAnswer extends AsyncTask<Request, Void, String> {

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
                System.out.println("ПОЛУЧИЛИ ВОТ ТАКОЙ РЕСПОС " + response);
                JSONObject interpretation = new JSONObject(response);
                String header = interpretation.getString("mainMessage");
                String description = interpretation.getString("description");

                showInterpretationWindow(header, description);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class EasyStatGetQuestions extends AsyncTask<Request, Void, String> {

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
                JSONArray questionsAsArray = new JSONArray(response);
                for(int i = 0; i < questionsAsArray.length(); ++i) {
                    JSONObject question = questionsAsArray.getJSONObject(i);
                    QuestionItem currentQuestion = new QuestionItem(question);
                    questions.add(currentQuestion);
                    answers.add(new AnswerItem(currentQuestion));
                }

                showQuestion(START_POSITION);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        headerLinearLayout = findViewById(R.id.ll_questionnaire_header);
        questionLinearLayout = findViewById(R.id.ll_question);

        checkboxesScrollView = findViewById(R.id.sv_checkboxes);
        radioButtonsScrollView = findViewById(R.id.sv_radio_buttons);
        textAnswerScrollView = findViewById(R.id.sv_text_answer);

        questionHeaderTextView = findViewById(R.id.tv_question_name);
        questionDescriptionTextView = findViewById(R.id.tv_question_description);

        nextQuestionButton = findViewById(R.id.bt_next_question);
        nextQuestionButton.setOnClickListener(nextQuestionListener);

        prevQuestionButton = findViewById(R.id.bt_prev_question);
        prevQuestionButton.setOnClickListener(prevQuestionListener);

        endQuestionnaireButton = findViewById(R.id.bt_end_questionnaire);
        endQuestionnaireButton.setOnClickListener(endQuestionnaireListener);

        textAnswer = findViewById(R.id.et_text_answer);
        initCheckboxes();
        initRadioButtons();

        questionnaireNameTextView = findViewById(R.id.tv_questionnaire_header_name);
        questionnaireDescriptionTextView = findViewById(R.id.tv_questionnaire_header_description);
        startQuestionnaireButton = findViewById(R.id.bt_start_questionnaire);
        startQuestionnaireButton.setOnClickListener(startQuestionnaireListener);

        toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        swipeDetector = new SwipeDetector(ViewConfiguration.get(this).getScaledTouchSlop()) {
            @Override
            public void onSwipeDetected(Direction direction) {
                switch (direction){
                    case LEFT:{
                        if(currentQuestionPosition > 0){
                            showQuestion(currentQuestionPosition - 1);
                        }
                        break;
                    }
                    case RIGHT:{
                        if(currentQuestionPosition < questions.size() - 1){
                            showQuestion(currentQuestionPosition + 1);
                        }
                        break;
                    }
                    default:{
                        break;
                    }
                }
            }
        };

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra("questionnaire")){
            JSONObject questionnaireJSON;
            try {
                questionnaireJSON = new JSONObject(parentIntent.getStringExtra("questionnaire"));
                questionnaire = new QuestionnaireItem(questionnaireJSON);
                questionnaireNameTextView.setText(questionnaire.getName());
                questionnaireDescriptionTextView.setText(questionnaire.getDescription());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        questionLinearLayout.setVisibility(View.INVISIBLE);
        headerLinearLayout.setVisibility(View.VISIBLE);

    }

    private List<OptionItem> getCurrentQuestionOptions(){
        QuestionItem question = questions.get(currentQuestionPosition);
        List<OptionItem> options = question.getOptions();
        List<OptionItem> answer = new ArrayList<>();
        switch(question.getType()) {
            case QuestionTypes.RADIO_BUTTON:{
                System.out.println("ТИП ВОПРОСА RADIO_BUTTON ебучий");
                for(int i = 0; i < options.size(); ++i){
                    System.out.println("ОПЦИЯ " + options.get(i).getAnswer() + " : " + radioButtons.get(i).isChecked());
                    if(radioButtons.get(i).isChecked()){
                        answer.add(options.get(i));
                        break;
                    }
                }
                break;
            }
            case QuestionTypes.CHECKBOX:{
                for(int i = 0; i < options.size(); ++i){
                    if(checkBoxes.get(i).isChecked()){
                        answer.add(options.get(i));
                    }
                }
                break;
            }
            case QuestionTypes.TEXT:{
                OptionItem textOption = new OptionItem();
                textOption.setAnswer(textAnswer.getText().toString());
                answer.add(textOption);
                break;
            }
        }
        return answer;
    }

    private void showQuestion(int position){

        if(position < 0){
            position = START_POSITION;
        }
        System.out.println("СЕЙЧАС КАРЕНТ_ПОЗИШН РАВЕН " + currentQuestionPosition);
        if(currentQuestionPosition >= 0 && currentQuestionPosition < questions.size()) {
            answers.get(currentQuestionPosition).setOptions(getCurrentQuestionOptions());
        }else{
            System.out.println("ПОЭТОМУ СКИПАЕМ");
        }

        QuestionItem question = questions.get(position);
        toolbar.setTitle("Вопрос " + question.getNumber());

        questionHeaderTextView.setText(question.getName());
        questionDescriptionTextView.setText(question.getDescription());

        hideAll();
        List<OptionItem> options = question.getOptions();
        switch(question.getType()){
            case QuestionTypes.RADIO_BUTTON:{
                hideAllRadioButtons();
                for(int i = 0; i < options.size(); ++i){
                    RadioButton currentRadioButton = radioButtons.get(i);
                    currentRadioButton.setText(options.get(i).getAnswer());
                    currentRadioButton.setChecked(false);
                    currentRadioButton.setVisibility(View.VISIBLE);

                }
                radioButtonsScrollView.setVisibility(View.VISIBLE);
                break;
            }
            case QuestionTypes.CHECKBOX:{
                hideAllCheckboxes();
                for(int i = 0; i < options.size(); ++i){
                    CheckBox currentCheckbox = checkBoxes.get(i);
                    currentCheckbox.setText(options.get(i).getAnswer());
                    currentCheckbox.setChecked(false);
                    currentCheckbox.setVisibility(View.VISIBLE);
                }
                checkboxesScrollView.setVisibility(View.VISIBLE);
                break;
            }
            case QuestionTypes.TEXT:{
                textAnswer.setText("");
                textAnswerScrollView.setVisibility(View.VISIBLE);
            }
        }
        currentQuestionPosition = position;

        endQuestionnaireButton.setVisibility(View.INVISIBLE);

        if(currentQuestionPosition == START_POSITION){
            prevQuestionButton.setVisibility(View.INVISIBLE);
        }else {
            prevQuestionButton.setVisibility(View.VISIBLE);
        }

        if(currentQuestionPosition == questions.size() - 1){
            endQuestionnaireButton.setVisibility(View.VISIBLE);
            nextQuestionButton.setVisibility(View.INVISIBLE);
        }else {
            nextQuestionButton.setVisibility(View.VISIBLE);
        }

        headerLinearLayout.setVisibility(View.INVISIBLE);
        questionLinearLayout.setVisibility(View.VISIBLE);
    }

    private void hideAll(){
        checkboxesScrollView.setVisibility(View.INVISIBLE);
        radioButtonsScrollView.setVisibility(View.INVISIBLE);
        textAnswerScrollView.setVisibility(View.INVISIBLE);
    }

    private void hideAllCheckboxes(){
        for(CheckBox checkBox : checkBoxes){
            checkBox.setVisibility(View.INVISIBLE);
        }
    }

    private void hideAllRadioButtons(){
        for(RadioButton radioButton : radioButtons){
            radioButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initCheckboxes(){
        for(int i = 0; i < ANSWERS_MAX_COUNT; ++i){
            CheckBox currentCheckbox = findViewById(checkboxesIds.get(i));
            checkBoxes.add(currentCheckbox);
        }
    }

    private void initRadioButtons(){
        for(int i = 0; i < ANSWERS_MAX_COUNT; ++i){
            RadioButton currentRadioButton = findViewById(radioButtonsIds.get(i));
            radioButtons.add(currentRadioButton);
        }
    }

    private void showInterpretationWindow(String header, String description) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View interpretationWindow = inflater.inflate(R.layout.interpretation_layout, null);
        dialog.setView(interpretationWindow);

        final AlertDialog show = dialog.show();

        final TextView mainMessageTextView = interpretationWindow.findViewById(R.id.tv_main_message);
        mainMessageTextView.setText(header);

        final TextView descriptionTextView = interpretationWindow.findViewById(R.id.tv_interpretation_description);
        descriptionTextView.setText(description);

        final Button okButton = interpretationWindow.findViewById(R.id.bt_close_questionnaire);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Class questionnaireActivity = QuestionnaireListActivity.class;
                Intent questionsActivityIntent = new Intent(context, questionnaireActivity);

                context.startActivity(questionsActivityIntent);
                show.dismiss();
            }
        });
    }

    private View.OnClickListener startQuestionnaireListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            URL getQuestionsURL = NetWorkUtils.GenerateURL(EasyStatRequestType.GET_QUESTIONS_FOR_QUESTIONNAIRE, questionnaire.getId().toString());
            new QuestionActivity.EasyStatGetQuestions().execute(new Request(getQuestionsURL, null, RequestType.GET));
        }
    };

    private View.OnClickListener nextQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentQuestionPosition < questions.size() - 1){
                showQuestion(currentQuestionPosition + 1);
            }
        }
    };

    private View.OnClickListener prevQuestionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(currentQuestionPosition > 0){
                showQuestion(currentQuestionPosition - 1);
            }
        }
    };

    private View.OnClickListener endQuestionnaireListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            answers.get(currentQuestionPosition).setOptions(getCurrentQuestionOptions());

            URL sendAnswerUrl = NetWorkUtils.GenerateURL(EasyStatRequestType.SEND_ANSWER, null);
            JSONArray answersJsonArray = new JSONArray();
            for(AnswerItem answer : answers){
                answersJsonArray.put(answer.toJSON());
            }
            new QuestionActivity.EasyStatSendAnswer().execute(new Request(sendAnswerUrl, answersJsonArray.toString(), RequestType.POST));
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return swipeDetector.onTouchEvent(event);
    }

}
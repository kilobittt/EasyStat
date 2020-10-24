package inozemtsev.coursework.easystat.questionnaires;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnswerItem {
    private Long questionId;
    private Long respondentId;
    private List<OptionItem> options = new ArrayList<>();

    public AnswerItem(QuestionItem question){
        this.questionId = question.getId();
        this.respondentId = 1L;//TODO поменять в бэке, чтобы ID возвращал
    }

    public void setOptions(List<OptionItem> options) {
        this.options = options;
    }

    public JSONObject toJSON(){
        JSONObject answer = new JSONObject();
        try {
            answer.put(AnswerAttributes.QUESTIONNAIRE_ID, questionId);
            answer.put(AnswerAttributes.RESPONDENT_ID, respondentId);
            JSONArray optionsArray = new JSONArray();
            for(OptionItem option : options){
                optionsArray.put(option.toJSON());
            }
            answer.put(AnswerAttributes.OPTIONS, optionsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answer;
    }

}

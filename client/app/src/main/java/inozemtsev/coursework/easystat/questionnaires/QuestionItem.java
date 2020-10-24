package inozemtsev.coursework.easystat.questionnaires;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionItem {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long questionnaireId;
    private int number;
    private List<OptionItem> options = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Long getQuestionnaireId() {
        return questionnaireId;
    }

    public int getNumber() {
        return number;
    }

    public List<OptionItem> getOptions() {
        return options;
    }


    public QuestionItem(JSONObject question){
        try {
            this.id = question.getLong(QuestionAttributes.ID);
            this.name = question.getString(QuestionAttributes.NAME);
            this.description = question.getString(QuestionAttributes.DESCRIPTION);
            this.type = question.getString(QuestionAttributes.TYPE);
            this.questionnaireId = question.getLong(QuestionAttributes.QUESTIONNAIRE_ID);
            this.number = question.getInt(QuestionAttributes.NUMBER);

            JSONArray optionsJsonArray = question.getJSONArray(QuestionAttributes.OPTIONS);
            for(int i = 0; i < optionsJsonArray.length(); ++i){
                options.add(new OptionItem(optionsJsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

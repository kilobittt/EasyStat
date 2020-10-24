package inozemtsev.coursework.easystat.questionnaires;

import org.json.JSONException;
import org.json.JSONObject;

public class QuestionnaireItem {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Long ownerId;
    private boolean isRepresented;
    private boolean isMarked;
    private String access;
    private int questionsNumber;

    private int imageResource;

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setRepresented(boolean represented) {
        isRepresented = represented;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public void setQuestionsNumber(int questionsNumber) {
        this.questionsNumber = questionsNumber;
    }

    private String tittle;

    public QuestionnaireItem(){}

    public QuestionnaireItem(JSONObject questionnaire) {
        try {
            this.id = questionnaire.getLong(QuestionnaireAttributes.ID);
            this.ownerId = questionnaire.getLong(QuestionnaireAttributes.OWNER_ID);
            this.questionsNumber = questionnaire.getInt(QuestionnaireAttributes.QUESTIONS_NUMBERS);
            this.isRepresented = questionnaire.getBoolean(QuestionnaireAttributes.IS_REPRESENTED);
            this.isMarked = questionnaire.getBoolean(QuestionnaireAttributes.IS_MARKED);
            this.name = questionnaire.getString(QuestionnaireAttributes.NAME);
            this.description = questionnaire.getString(QuestionnaireAttributes.DESCRIPTION);
            this.type = questionnaire.getString(QuestionnaireAttributes.TYPE);
            this.access = questionnaire.getString(QuestionnaireAttributes.ACCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

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

    public Long getOwnerId() {
        return ownerId;
    }

    public boolean isRepresented() {
        return isRepresented;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public String getAccess() {
        return access;
    }

    public int getQuestionsNumber() {
        return questionsNumber;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTittle() {
        return tittle;
    }

    public String toStringJSON(){
        JSONObject questionnaire = new JSONObject();
        try {
            questionnaire.put(QuestionnaireAttributes.ID, id);
            questionnaire.put(QuestionnaireAttributes.OWNER_ID, ownerId);
            questionnaire.put(QuestionnaireAttributes.NAME, name);
            questionnaire.put(QuestionnaireAttributes.DESCRIPTION, description);
            questionnaire.put(QuestionnaireAttributes.TYPE, type);
            questionnaire.put(QuestionnaireAttributes.ACCESS, access);
            questionnaire.put(QuestionnaireAttributes.IS_MARKED, isMarked);
            questionnaire.put(QuestionnaireAttributes.IS_REPRESENTED, isRepresented);
            questionnaire.put(QuestionnaireAttributes.QUESTIONS_NUMBERS, questionsNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionnaire.toString();
    }
}

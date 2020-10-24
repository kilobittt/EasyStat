package inozemtsev.coursework.easystat.questionnaires;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OptionItem {
    String artifact;
    String answer;

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public OptionItem(){}

    public OptionItem(JSONObject option){
        try {
            this.artifact = option.getString(OptionAttributes.ARTIFACT);
            this.answer = option.getString(OptionAttributes.ANSWER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getArtifact() {
        return artifact;
    }

    public String getAnswer() {
        return answer;
    }

    public JSONObject toJSON(){
        JSONObject option = new JSONObject();
        try {
            option.put(OptionAttributes.ARTIFACT, artifact);
            option.put(OptionAttributes.ANSWER, answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return option;
    }
}

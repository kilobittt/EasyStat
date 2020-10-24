package inozemtsev.coursework.easystat.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

import inozemtsev.coursework.easystat.QuestionActivity;
import inozemtsev.coursework.easystat.QuestionnaireListActivity;
import inozemtsev.coursework.easystat.R;
import inozemtsev.coursework.easystat.questionnaires.QuestionnaireItem;

public class QuestionnairesAdapter
        extends RecyclerView.Adapter<QuestionnairesAdapter.QuestionnairesViewHolder>{

    private Context parentContext;

    private List<QuestionnaireItem> questionnaires;

    public QuestionnairesAdapter(List<QuestionnaireItem> questionnaires, Context parent) {
        this.questionnaires = questionnaires;
        this.parentContext = parent;
    }

    @NonNull
    @Override
    public QuestionnairesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForThisItem = R.layout.questionnaire_item;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForThisItem, parent, false);

        return new QuestionnairesViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull QuestionnairesViewHolder holder, int position) {
        QuestionnaireItem currentItem =  questionnaires.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return questionnaires.size();
    }

    class QuestionnairesViewHolder extends RecyclerView.ViewHolder{

        TextView questionnaireNameView;
        TextView questionnaireTittle;
        ImageView questionnaireTypeImage;
        RelativeLayout questionnaireItem;

        public QuestionnairesViewHolder(@NonNull View itemView) {
            super(itemView);

            questionnaireNameView = itemView.findViewById(R.id.tv_questionnaire_name);
            questionnaireTittle = itemView.findViewById(R.id.tv_questionnaire_tittle);
            questionnaireTypeImage = itemView.findViewById(R.id.iv_questionnaire_type_image);
            questionnaireItem = itemView.findViewById(R.id.test_for_listner);



            questionnaireItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Context context = v.getContext();
                    Class questionsActivity = QuestionActivity.class;
                    Intent questionsActivityIntent = new Intent(context, questionsActivity);

                    questionsActivityIntent.putExtra("questionnaire", questionnaires.get(position).toStringJSON());

                    context.startActivity(questionsActivityIntent);
                }
            });
        }


        void bind(QuestionnaireItem item) {
            questionnaireTypeImage.setImageResource(item.getImageResource());
            questionnaireNameView.setText(item.getName());
            questionnaireTittle.setText(item.getTittle());
        }
    }

    public void filterList(List<QuestionnaireItem> filteredItems){
        questionnaires = filteredItems;
        notifyDataSetChanged();
    }
}

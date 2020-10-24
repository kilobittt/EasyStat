package inozemtsev.coursework.easystat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StatActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        bottomNavigationView = findViewById(R.id.btnv_main_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nvbt_stat);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Context context = StatActivity.this;

                switch (item.getItemId()){
                    case R.id.nvbt_stat:{
                        return true;
                    }
                    case R.id.nvbt_profile:{
                        startActivity(new Intent(context, ProfileActivity.class));
                        overridePendingTransition(0,0);
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
    }
}
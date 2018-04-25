package ndf333.nathaniel.kotoba;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowExamplesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_examples);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        Intent starter = getIntent();
        String term = starter.getStringExtra("word");

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        ArrayList<Object> usages = dbHelper.queryExamples(term);

        if (usages.size() > 0) {
            RecyclerView exampleResults = (RecyclerView) findViewById(R.id.exampleResults);

            LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
            exampleResults.setLayoutManager(manager);
            exampleResults.setItemAnimator(new DefaultItemAnimator());

            SearchAdapter exampleAdapter = new SearchAdapter(this);

            exampleResults.setAdapter(exampleAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(exampleResults.getContext(),
                    manager.getOrientation());
            exampleResults.addItemDecoration(dividerItemDecoration);

            exampleAdapter.addAll(usages);
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! Kotoba could not find any example sentences using this word.", Toast.LENGTH_LONG).show();
        }


    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }
}

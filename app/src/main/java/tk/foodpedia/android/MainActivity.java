package tk.foodpedia.android;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.fragment_container) == null) {
            fm.beginTransaction().add(R.id.fragment_container, ProductInformationFragment.newInstance()).commit();
        }

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "<TBD>", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}

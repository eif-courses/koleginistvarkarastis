package eif.viko.lt.paskaitu.koleginistvarkarastis;

import static eif.viko.lt.paskaitu.koleginistvarkarastis.TimetableService.BASE_URL;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExampleAppWidgetConfig extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String SHARED_PREFS = "prefs";
    public static final String KEY_BUTTON_TEXT = "keyButtonText";
    public static final String WIDGET_SYNC = "WIDGET_SYNC";
    public static final String CURRENT_TEACHER = "CURRENT_TEACHER";
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText editTextButton;
    private ProgressBar progressBarStatus;
    List<Person> persons = new ArrayList<>();
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_app_widget_config);

        progressBarStatus = findViewById(R.id.progressBar);

        Intent configIntent = getIntent();
        Bundle extras = configIntent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        editTextButton = findViewById(R.id.edit_text_button);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Spinner spin = findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        // TimetableService service = retrofit.create(TimetableService.class);


        TimetableService service2 = retrofit.create(TimetableService.class);

        Call<List<Person>> lectures = service2.getAllTeachers();

        lectures.enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(@NonNull Call<List<Person>> call, @NonNull Response<List<Person>> response) {
                persons = response.body();

                //Creating the ArrayAdapter instance having the country list
                ArrayAdapter aa = new ArrayAdapter(ExampleAppWidgetConfig.this, android.R.layout.simple_spinner_item, response.body());
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                //Setting the ArrayAdapter data on the Spinner
                spin.setAdapter(aa);
                progressBarStatus.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Person>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void confirmConfiguration(View v) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String buttonText = editTextButton.getText().toString();

        RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.example_widget);
        views.setOnClickPendingIntent(R.id.example_widget_button, pendingIntent);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        TimetableService service = retrofit.create(TimetableService.class);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();

        Call<List<Teacher>> lectures = service.getTeacherTimetable(buttonText, dtf.format(now), dtf.format(now));
        lectures.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(@NonNull Call<List<Teacher>> call, @NonNull Response<List<Teacher>> response) {

                assert response.body() != null;

                StringBuilder str = new StringBuilder();

                for (Teacher d : response.body()) {
                    str.append("\n").append(d.getSubject()).append(" (").append(d.getGroup()).append(")").append("\n").append(d.getClassroom()).append(" (").append(d.getStarttime()).append(" val. ), ").append(d.getUniperiod()).append(" pask.\n");
                }
                if (!response.body().isEmpty()) {
                    views.setCharSequence(R.id.example_widget_button, "setText", response.body().get(0).getTeacher());
                    views.setTextViewText(R.id.appwidget_text, str.toString());
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }else{
                    views.setCharSequence(R.id.example_widget_button, "setText", "ŠIANDIEN PASKAITŲ NĖRA");
                    views.setTextViewText(R.id.appwidget_text, "");
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Teacher>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BUTTON_TEXT + appWidgetId, buttonText);
        editor.apply();

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        editTextButton.setEnabled(false);
        editTextButton.setText(persons.get(position).getId());

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CURRENT_TEACHER, persons.get(position).getId());
        editor.apply();



    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
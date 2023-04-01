package eif.viko.lt.paskaitu.koleginistvarkarastis;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static eif.viko.lt.paskaitu.koleginistvarkarastis.ExampleAppWidgetConfig.CURRENT_TEACHER;
import static eif.viko.lt.paskaitu.koleginistvarkarastis.ExampleAppWidgetConfig.KEY_BUTTON_TEXT;
import static eif.viko.lt.paskaitu.koleginistvarkarastis.ExampleAppWidgetConfig.SHARED_PREFS;
import static eif.viko.lt.paskaitu.koleginistvarkarastis.TimetableService.BASE_URL;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_CLICK = "ACTION_CLICK";

    String temp = "";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_widget);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String buttonText = prefs.getString(CURRENT_TEACHER, "Press me");


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_widget);

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
                    AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, ExampleAppWidgetProvider.class), views);

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Teacher>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });

//        if (ACTION_CLICK.equals(intent.getAction())) {
//            // update the TextView when the button is clicked
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_widget);
//            views.setTextViewText(R.id.appwidget_text, "Button clicked");
//            AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, ExampleAppWidgetProvider.class), views);
//            System.out.println("LABWAEWEAWE");
//        }
        super.onReceive(context, intent);


    }

}
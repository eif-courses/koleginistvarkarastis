package eif.viko.lt.paskaitu.koleginistvarkarastis;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TimetableService {
    String BASE_URL = "https://eifvikolttvarkarastisfastapi-production.up.railway.app/";

    @GET("teacher/{id}/{from}/{to}/2022/")
    Call<List<Teacher>> getTeacherTimetable(@Path("id") String id,
                                            @Path("from") String from,
                                            @Path("to") String to);
    @GET("/teachers")
    Call<List<Person>> getAllTeachers();
}

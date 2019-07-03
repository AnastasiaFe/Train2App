package ua.nure.fedorenko.kidstim.rest;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Completable;
import rx.Observable;
import rx.Single;
import ua.nure.fedorenko.kidstim.auth.jwt.JwtAuthenticationResponse;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Parent;
import ua.nure.fedorenko.kidstim.entity.Reward;
import ua.nure.fedorenko.kidstim.entity.Task;
import ua.nure.fedorenko.kidstim.entity.User;

public interface APIService {

    @POST("login")
    Single<JwtAuthenticationResponse> login(@Body User user);

    @POST("logout")
    Single<Response<Void>> logout();

    @POST("register")
    Single<Parent> register(@Body Parent parent);

    @PUT("updateParent")
    Completable updateParent(@Body Parent parent);

    @PUT("updateParentWithChild")
    Completable updateParentWithChild(@Query("parent") String parent, @Query("childId") String childId);

    @PUT("updateChild")
    Completable updateChild(@Body Child child);

    @POST("addChild")
    Completable addChild(@Body Child child, @Query("id") String parentId);

    @DELETE("deleteChild")
    Observable<Child> deleteChild(@Body Child child);

    @GET("parent")
    Single<Parent> getParentById(@Query("id") String id);

    @GET("child")
    Single<Child> getChildById(@Query("id") String id);

    @GET("currentUser")
    Single<User> getCurrentUser();

    @GET("parentByEmail")
    Single<Parent> getParentByEmail(@Query("email") String id);

    @GET("childByEmail")
    Single<Child> getChildByEmail(@Query("email") String id);

    @GET("childrenByParent")
    Single<List<Child>> getChildrenByParent(@Query("email") String id);

    @GET("tasksByParent")
    Single<List<Task>> getTasksByParent(@Query("id") String id);

    @GET("tasksByChild")
    Single<List<Task>> getTasksByChild(@Query("id") String id);

    @GET("rewardsByChild")
    Single<List<Reward>> getRewardsByChild(@Query("id") String id);

    @GET("rewardsByParent")
    Single<List<Reward>> getRewardsByParent(@Query("id") String id);

    @PUT("markAsCompleted")
    Completable markAsCompleted(@Query("id") String taskId, @Query("complete") boolean complete);

    @PUT("updateReward")
    Completable updateReward(@Body Reward reward);

    @Multipart
    @POST("uploadImage")
    Completable uploadImage(@Part MultipartBody.Part image, @Query("name") String name, @Query("email") String email, @Query("role") String role);

    @POST("saveReward")
    Completable addReward(@Body Reward reward);

    @DELETE("deleteReward")
    Completable deleteReward(@Query("id") String id);

    @GET("reward")
    Single<Reward> getRewardById(@Query("id") String id);

    @PUT("updateTask")
    Completable updateTask(@Body Task task);

    @POST("saveTask")
    Completable addTask(@Body Task task);

    @DELETE("deleteTask")
    Completable deleteTask(@Query("id") String id);

    @GET("task")
    Single<Task> getTaskById(@Query("id") String id);

}

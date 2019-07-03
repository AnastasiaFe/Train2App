package ua.nure.fedorenko.kidstim.rest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import ua.nure.fedorenko.kidstim.R;
import ua.nure.fedorenko.kidstim.activity.ChildTasksActivity;
import ua.nure.fedorenko.kidstim.activity.ParentMainActivity;
import ua.nure.fedorenko.kidstim.auth.jwt.JwtAuthenticationResponse;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Parent;
import ua.nure.fedorenko.kidstim.entity.Reward;
import ua.nure.fedorenko.kidstim.entity.Task;
import ua.nure.fedorenko.kidstim.entity.User;
import ua.nure.fedorenko.kidstim.utils.Constants;

public class APIServiceImpl {

    private Context context;

    private APIService apiService;

    public Context getContext() {
        return context;
    }

    public APIServiceImpl(Context context) {
        this.context = context;
        apiService = RetrofitClient.getClient(context).create(APIService.class);
    }


    public void login(User user) {
        apiService.login(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<JwtAuthenticationResponse>() {
                    @Override
                    public void onSuccess(JwtAuthenticationResponse response) {
                        String token = response.getAccessToken();
                        String email = response.getEmail();
                        String role = response.getRole();
                        SharedPreferences pref = context.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor edit = pref.edit();
                        edit.putString(Constants.AUTHORIZATION_HEADER, response.getTokenType() + " " + token);
                        edit.putString(Constants.USER, email);
                        edit.putString(Constants.ROLE, role);
                        edit.apply();
                        if (role.equals(Constants.PARENT_ROLE)) {
                            context.startActivity(new Intent(context, ParentMainActivity.class));
                        } else if (role.equals(Constants.CHILD_ROLE)) {
                            context.startActivity(new Intent(context, ChildTasksActivity.class));
                        }
                    }

                    @Override
                    public void onError(Throwable error) {
                        Toast.makeText(context, context.getString(R.string.error_authorization), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public Single<Parent> register(Parent parent) {
        return apiService.register(parent)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Task>> getTasksByParent(String id) {
        return apiService.getTasksByParent(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Task>> getTasksByChild(String id) {
        return apiService.getTasksByChild(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Task> getTaskById(String id) {
        return apiService.getTaskById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Reward> getRewardById(String id) {
        return apiService.getRewardById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Reward>> getRewardsByParent(String id) {
        return apiService.getRewardsByParent(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Reward>> getRewardsByChild(String id) {
        return apiService.getRewardsByChild(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Parent> getParentByEmail(String email) {
        return apiService.getParentByEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Child>> getChildrenByParent(String email) {
        return apiService.getChildrenByParent(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Child> getChildByEmail(String email) {
        return apiService.getChildByEmail(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Child> getChildById(String id) {
        return apiService.getChildById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Response<Void>> logout() {
        return apiService.logout().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateParent(Parent parent) {
        return apiService.updateParent(parent).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Completable updateTask(Task task) {
        return apiService.updateTask(task).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable addChild(Child child, String parentId) {
        return apiService.addChild(child, parentId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void updateChild(Child child) {
        Completable completable = apiService.updateChild(child).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        Completable.CompletableSubscriber subscriber = new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSubscribe(Subscription d) {

            }
        };
        completable.subscribe(subscriber);
    }


    public Completable updateReward(Reward reward) {
        return apiService.updateReward(reward).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable uploadImage(File file, String name, String email, String role) {
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getAbsolutePath(), requestFile);
        return apiService.uploadImage(body, name, email, role)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable addTask(Task task) {
        return apiService.addTask(task)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable addReward(Reward reward) {
        return apiService.addReward(reward)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteTask(String id) {
        return apiService.deleteTask(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteReward(String id) {
        return apiService.deleteReward(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable markTaskAsCompleted(String taskId, boolean complete) {
        return apiService.markAsCompleted(taskId, complete).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}




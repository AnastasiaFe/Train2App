package ua.nure.fedorenko.kidstim.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.ButterKnife;
import retrofit2.Response;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import ua.nure.fedorenko.kidstim.AppConstants;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Parent;
import ua.nure.fedorenko.kidstim.entity.User;
import ua.nure.fedorenko.kidstim.rest.APIServiceImpl;
import ua.nure.fedorenko.kidstim.utils.Constants;
import ua.nure.fedorenko.kidstim.R;

public class BaseNavigableActivity extends AppCompatActivity {

    protected APIServiceImpl apiService;
    protected SharedPreferences sharedPreferences;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        final DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);
        ButterKnife.bind(this);
        sharedPreferences = getBaseContext().getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE);
        apiService = new APIServiceImpl(this);
        final NavigationView navigationView = (NavigationView) fullView.findViewById(R.id.navigationView);
        setNavigationDrawerInfo(navigationView);
    }

    private void setNavigationDrawerInfo(NavigationView navigationView) {
        View header = navigationView.getHeaderView(0);
        String email = sharedPreferences.getString(Constants.USER, Constants.EMPTY_STRING);
        String role = sharedPreferences.getString(Constants.ROLE, Constants.EMPTY_STRING);
        TextView fullNameTextView = (TextView) header.findViewById(R.id.accountFullname);
        TextView emailTextView = (TextView) header.findViewById(R.id.accountEmail);
        emailTextView.setText(email);
        getUserAndSetInfo(email, role, fullNameTextView);
        ImageView imageView = (ImageView) header.findViewById(R.id.accountImage);
        setImage(email, role, imageView);
        if (role.equals(Constants.PARENT_ROLE)) {
            navigationView.getMenu().add(R.id.grp1, 10, Menu.NONE, R.string.children);
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.logoutMenuItem:
                    logout();
                    return true;
                case R.id.tasksMenuItem:
                    if (role.equals(Constants.CHILD_ROLE)) {
                        startActivity(new Intent(getBaseContext(), ChildTasksActivity.class));
                    } else if (role.equals(Constants.PARENT_ROLE)) {
                        startActivity(new Intent(getBaseContext(), ParentTasksActivity.class));
                    }
                    return true;
                case R.id.rewardsMenuItem:
                    if (role.equals(Constants.CHILD_ROLE)) {
                        startActivity(new Intent(getBaseContext(), ChildRewardsActivity.class));
                    } else if (role.equals(Constants.PARENT_ROLE)) {
                        startActivity(new Intent(getBaseContext(), ParentRewardsActivity.class));
                    }
                    return true;
                case 10:
                    startActivity(new Intent(getBaseContext(), ParentMainActivity.class));
                default:
                    return false;
            }
        });

    }

    private void logout() {
        Single<Response<Void>> single = apiService.logout();
        SingleSubscriber<Response<Void>> subscriber = new SingleSubscriber<Response<Void>>() {
            @Override
            public void onSuccess(Response<Void> value) {
                sharedPreferences.edit().clear().apply();
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(getBaseContext(), getString(R.string.error_logout), Toast.LENGTH_LONG).show();
            }
        };
        single.subscribe(subscriber);
    }

    private void setImage(String email, String role, ImageView imageView) {
        String token = sharedPreferences.getString(Constants.AUTHORIZATION_HEADER, Constants.EMPTY_STRING);
        GlideUrl glideUrl = new GlideUrl(AppConstants.IMAGE_URL+"?name=" + email + "&role=" + role, new LazyHeaders.Builder()
                .addHeader(Constants.AUTHORIZATION_HEADER, token)
                .build());
        Glide.with(this).load(glideUrl).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    private void getUserAndSetInfo(String email, String role, TextView textView) {
        if (role.equals(Constants.PARENT_ROLE)) {
            getParent(email, textView);
        } else {
            getChild(email, textView);
        }
    }

    private void getChild(String email, TextView textView) {
        Single<Child> childSingle = apiService.getChildByEmail(email);
        SingleSubscriber<User> subscriber = new SingleSubscriber<User>() {
            @Override
            public void onSuccess(User value) {
                saveInfo(value, textView);
                Child child = (Child) value;
                ((TextView) (findViewById(R.id.points))).setText(child.getPoints());
                apiService.updateChild((Child) value);
            }

            @Override
            public void onError(Throwable error) {
              //  Toast.makeText(getBaseContext(), getString(R.string.error_child_retrieve), Toast.LENGTH_LONG).show();
            }
        };
        childSingle.subscribe(subscriber);
    }

    private void getParent(String email, TextView textView) {
        Single<Parent> parentSingle = apiService.getParentByEmail(email);
        SingleSubscriber<Parent> subscriber = new SingleSubscriber<Parent>() {
            @Override
            public void onSuccess(Parent parent) {
                saveInfo(parent, textView);
                Completable completable = apiService.updateParent(parent);
                Completable.CompletableSubscriber subscriber1 = new Completable.CompletableSubscriber() {
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
                completable.subscribe(subscriber1);
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(getBaseContext(), getString(R.string.error_parent_retrieving), Toast.LENGTH_LONG).show();
            }
        };
        parentSingle.subscribe(subscriber);
    }

    private void saveInfo(User value, TextView textView) {
        String fullName = value.getName() + " " + value.getSurname();
        sharedPreferences.edit().putString(Constants.FULL_NAME, fullName).apply();
        sharedPreferences.edit().putString(Constants.ID, value.getId()).apply();
        String device_token = value.getDeviceToken();
        String newToken = FirebaseInstanceId.getInstance().getToken();
        if (device_token == null || !device_token.equals(newToken)) {
            value.setDeviceToken(newToken);
        }
        textView.setText(fullName);
    }
}

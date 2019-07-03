package ua.nure.fedorenko.kidstim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Single;
import rx.SingleSubscriber;
import ua.nure.fedorenko.kidstim.AppConstants;
import ua.nure.fedorenko.kidstim.R;
import ua.nure.fedorenko.kidstim.adapter.ChildAdapter;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Parent;
import ua.nure.fedorenko.kidstim.utils.Constants;

public class ParentMainActivity extends BaseNavigableActivity implements ChildAdapter.ChildListener {

    @BindView(R.id.childrenRecycleView)
    RecyclerView childrenRecycleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_main);
        String parentEmail = sharedPreferences.getString(Constants.USER, Constants.EMPTY_STRING);
        Single<Parent> parentSingle = apiService.getParentByEmail(parentEmail);
        SingleSubscriber<Parent> subscriber = new SingleSubscriber<Parent>() {
            @Override
            public void onSuccess(Parent value) {
                Single<List<Child>> childrenSingle = apiService.getChildrenByParent(value.getEmail());
                SingleSubscriber<List<Child>> subsc = new SingleSubscriber<List<Child>>() {
                    @Override
                    public void onSuccess(List<Child> ch) {
                        ChildAdapter adapter = new ChildAdapter();
                        setChildListener(adapter);
                        adapter.setChildren(ch);
                        childrenRecycleView.setAdapter(adapter);
                        childrenRecycleView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                };
                childrenSingle.subscribe(subsc);

            }

            @Override
            public void onError(Throwable error) {

            }
        };
        parentSingle.subscribe(subscriber);
    }

    private void setChildListener(ChildAdapter adapter) {
        adapter.setChildListener(this);
    }

    @OnClick(R.id.addChildFloatingActionButton)
    void onAddChildFloatingButtonClick() {
        startActivity(new Intent(ParentMainActivity.this, AddChildActivity.class));
    }

    @Override
    public void onChildSelected(@NonNull Child child) {

    }

    @Override
    public void onGetImage(@NonNull String photo, ImageView imageView) {
        String token = sharedPreferences.getString(Constants.AUTHORIZATION_HEADER, Constants.EMPTY_STRING);
        GlideUrl glideUrl = new GlideUrl(AppConstants.IMAGE_URL + "?name=" + photo + "&role=child", new LazyHeaders.Builder()
                .addHeader(Constants.AUTHORIZATION_HEADER, token)
                .build());
        Glide.with(this).load(glideUrl).into(imageView);
    }

}

package ua.nure.fedorenko.kidstim.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import ua.nure.fedorenko.kidstim.AppConstants;
import ua.nure.fedorenko.kidstim.adapter.RewardAdapter;
import ua.nure.fedorenko.kidstim.entity.Reward;
import ua.nure.fedorenko.kidstim.entity.RewardStatus;
import ua.nure.fedorenko.kidstim.service.RewardService;
import ua.nure.fedorenko.kidstim.utils.Constants;
import ua.nure.fedorenko.kidstim.R;

public class ParentRewardsActivity extends BaseNavigableActivity implements RewardAdapter.RewardListener {

    private List<Reward> rewards;

    @BindView(R.id.rewardsRecycleView)
    RecyclerView rewardsRecycleView;
    private RewardAdapter adapter;
    private String parentId;
    private RewardService rewardService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_rewards);
        adapter = new RewardAdapter();
        setRewardListener(adapter);
        rewardsRecycleView.setAdapter(adapter);
        rewardsRecycleView.setLayoutManager(new LinearLayoutManager(ParentRewardsActivity.this));
        parentId = sharedPreferences.getString(Constants.ID, Constants.EMPTY_STRING);
        rewardService = new RewardService();

    }

    private void getAllRewards() {
        Single<List<Reward>> rewardsSingle = apiService.getRewardsByParent(parentId);
        SingleSubscriber<List<Reward>> subscriber = new SingleSubscriber<List<Reward>>() {
            @Override
            public void onSuccess(List<Reward> value) {
                rewards = value;
                adapter.setRewards(rewards);
            }

            @Override
            public void onError(Throwable error) {

            }
        };
        rewardsSingle.subscribe(subscriber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tasks_menu, menu);
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(this,
                R.array.reward_statuses, android.R.layout.simple_spinner_item);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                switch (pos) {
                    case 1:
                        adapter.setRewards(rewardService.getRewardsByStatus(RewardStatus.CREATED, rewards));
                        break;
                    case 2:
                        adapter.setRewards(rewardService.getRewardsByStatus(RewardStatus.RECEIVED, rewards));
                        break;
                    case 3:
                        adapter.setRewards(rewardService.getRewardsByStatus(RewardStatus.REQUESTED, rewards));
                    default:
                        if (rewards == null) {
                            getAllRewards();
                        } else {
                            adapter.setRewards(rewards);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return true;
    }


    private void setRewardListener(RewardAdapter adapter) {
        adapter.setRewardListener(this);
    }

    @OnClick(R.id.addRewardFloatingActionButton)
    void onShowAddRewardFloatingButtonClick() {
        startActivity(new Intent(this, SaveRewardActivity.class));
    }

    @Override
    public void onEditReward(String rewardId) {
        Intent intent = new Intent(this, SaveRewardActivity.class);
        intent.putExtra(Constants.ID, rewardId);
        startActivity(intent);
    }

    @Override
    public ImageView onGetImage(@NonNull String photo) {
        ImageView imageView = new ImageView(getBaseContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(70, 70);
        imageView.setLayoutParams(layoutParams);
        String token = sharedPreferences.getString(Constants.AUTHORIZATION_HEADER, Constants.EMPTY_STRING);
        GlideUrl glideUrl = new GlideUrl(AppConstants.IMAGE_URL + "?name=" + photo + "&role=child", new LazyHeaders.Builder()
                .addHeader(Constants.AUTHORIZATION_HEADER, token)
                .build());
        Glide.with(this).load(glideUrl).into(imageView);
        return imageView;
    }

    @Override
    public void onDeleteReward(String rewardId) {
        Completable completable = apiService.deleteReward(rewardId);
        Completable.CompletableSubscriber subscriber = new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {
                Toast.makeText(getBaseContext(), "Ok", Toast.LENGTH_LONG).show();
                refreshActivity();
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

    private void refreshActivity() {
        finish();
        startActivity(getIntent());
    }
}

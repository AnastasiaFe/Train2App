package ua.nure.fedorenko.kidstim.activity;


import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import ua.nure.fedorenko.kidstim.R;
import ua.nure.fedorenko.kidstim.adapter.ChildRewardAdapter;
import ua.nure.fedorenko.kidstim.entity.Child;
import ua.nure.fedorenko.kidstim.entity.Reward;
import ua.nure.fedorenko.kidstim.entity.RewardStatus;
import ua.nure.fedorenko.kidstim.service.RewardService;
import ua.nure.fedorenko.kidstim.utils.Constants;

public class ChildRewardsActivity extends BaseNavigableActivity implements ChildRewardAdapter.RewardListener {

    private List<Reward> rewards;
    @BindView(R.id.rewardsRecycleView)
    RecyclerView rewardsRecycleView;
    private ChildRewardAdapter adapter;
    private String childId;
    private RewardService rewardService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_rewards);
        adapter = new ChildRewardAdapter();
        adapter.setRewardListener(this);
        rewardsRecycleView.setAdapter(adapter);
        rewardsRecycleView.setLayoutManager(new LinearLayoutManager(ChildRewardsActivity.this));
        childId = sharedPreferences.getString(Constants.ID, Constants.EMPTY_STRING);
        rewardService = new RewardService();
    }

    private void getAllRewards() {
        Single<List<Reward>> rewardsSingle = apiService.getRewardsByChild(childId);
        SingleSubscriber<List<Reward>> subscriber = new SingleSubscriber<List<Reward>>() {
            @Override
            public void onSuccess(List<Reward> value) {
                rewards = value;
                adapter.setRewards(rewards);
            }

            @Override
            public void onError(Throwable error) {
                Toast.makeText(getBaseContext(), getString(R.string.error_rewards_by_child), Toast.LENGTH_LONG).show();
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

    @Override
    public void onRequestReward(Reward reward) {
        Single<Child> childSingle = apiService.getChildById(childId);
        SingleSubscriber<Child> subscriber = new SingleSubscriber<Child>() {
            @Override
            public void onSuccess(Child child) {
                if (child.getPoints() - reward.getPoints() <= 0) {
                    Toast.makeText(getBaseContext(), getString(R.string.not_enough_points), Toast.LENGTH_LONG).show();
                } else {
                    reward.setStatus(RewardStatus.REQUESTED);
                    Completable completable = apiService.updateReward(reward);
                    Completable.CompletableSubscriber subsc = new Completable.CompletableSubscriber() {
                        @Override
                        public void onCompleted() {
                            Toast.makeText(getBaseContext(), getString(R.string.reward_was_requested), Toast.LENGTH_LONG).show();
                            sendNotification(child, reward);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onSubscribe(Subscription d) {

                        }
                    };
                    completable.subscribe(subsc);
                }
            }

            @Override
            public void onError(Throwable error) {

            }
        };
        childSingle.subscribe(subscriber);
    }


    private void sendNotification(Child child, Reward reward) {
        JSONObject json = new JSONObject();
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("body", child.getName() + " requests " + reward.getDescription());
            dataJson.put("title", "Hi! It's time to reward your child!");
            json.put("notification", dataJson);
            json.put("to", reward.getParent().getDeviceToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("JSON"), json.toString());
        Request request = new Request.Builder().url(Constants.FCM_URL)
                .post(body)
                .header("Content-Type", "application/json")
                .header("Authorization", Constants.SERVER_KEY)
                .build();
        OkHttpClient client = new OkHttpClient();
        Callback responseCallBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Fail Message", "fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.v("response", response.toString());
            }
        };
        okhttp3.Call call = client.newCall(request);
        call.enqueue(responseCallBack);
    }
}

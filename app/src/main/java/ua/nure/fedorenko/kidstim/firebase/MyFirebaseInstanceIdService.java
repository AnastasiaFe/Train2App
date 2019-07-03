package ua.nure.fedorenko.kidstim.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import ua.nure.fedorenko.kidstim.utils.Constants;


public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("MyTag", "Refreshed token: " + refreshedToken);
       // sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        getBaseContext().getSharedPreferences(Constants.APP_PREFERENCES, MODE_PRIVATE).edit().putString("device_token", refreshedToken).apply();
    }

    public String getToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }
}

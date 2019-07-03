package ua.nure.fedorenko.kidstim.rest;

import android.content.Context;
import android.content.Intent;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.nure.fedorenko.kidstim.activity.LoginActivity;
import ua.nure.fedorenko.kidstim.utils.Constants;

class RetrofitClient {

    private static Retrofit retrofit = null;

    private static final String BASE_URL = "http://10.23.10.215:8080/";

    private RetrofitClient() {
    }

    static Retrofit getClient(Context context) {
        if (retrofit == null) {
            String token = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("Authorization", "");
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(
                            chain -> {
                                Request request = chain.request()
                                        .newBuilder()
                                        .addHeader(Constants.AUTHORIZATION_HEADER, token)
                                        .build();

                                Response response = chain.proceed(request);
                                if (response.code() == 401) {
                                    context.startActivity(new Intent(context, LoginActivity.class));
                                    return response;
                                }
                                return response;

                            });
            OkHttpClient client = builder.build();

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}

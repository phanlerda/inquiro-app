// File: com/inquiro/app/data/remote/ApiClient.java
package com.inquiro.app.data.remote;

import com.inquiro.app.data.local.PrefsManager;

import java.io.IOException;
// THÊM IMPORT NÀY
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Interceptor authInterceptor = chain -> {
                Request originalRequest = chain.request();
                String token = PrefsManager.getInstance().getAuthToken();
                if (token != null && !token.isEmpty()) {
                    Request.Builder builder = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + token);
                    Request newRequest = builder.build();
                    return chain.proceed(newRequest);
                }
                return chain.proceed(originalRequest);
            };

            // ---- SỬA ĐỔI QUAN TRỌNG Ở ĐÂY ----
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    // TĂNG THỜI GIAN TIMEOUT LÊN 60 GIÂY
                    .connectTimeout(60, TimeUnit.SECONDS) // Thời gian chờ để kết nối tới server
                    .readTimeout(60, TimeUnit.SECONDS)    // Thời gian chờ để đọc dữ liệu từ server
                    .writeTimeout(60, TimeUnit.SECONDS)   // Thời gian chờ để ghi dữ liệu tới server
                    // CÁC INTERCEPTOR HIỆN CÓ
                    .addInterceptor(authInterceptor)
                    .addInterceptor(loggingInterceptor)
                    .build();
            // ---- KẾT THÚC SỬA ĐỔI ----

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
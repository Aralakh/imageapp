package com.example.lawrenjuip.imageapp.apiservices;

import com.example.lawrenjuip.imageapp.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class ImgurService {
    private static OkHttpClient.Builder okHttpClient;
    private static Retrofit.Builder builder= new Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S getRetrofit(Class<S> serviceClass){
        okHttpClient = new OkHttpClient.Builder()
        .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request newRequest = originalRequest.newBuilder()
                        .build();
                return chain.proceed(newRequest);
            }
        });

        retrofit = retrofit.newBuilder()
                .client(okHttpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }
}

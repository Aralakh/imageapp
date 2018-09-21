package com.example.lawrenjuip.imageapp.apiservices;

public interface RestCallback<T>{
    void onResponse(T response);

    void onError();
}

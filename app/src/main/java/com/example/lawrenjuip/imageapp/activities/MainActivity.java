package com.example.lawrenjuip.imageapp.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.lawrenjuip.imageapp.fragments.GalleryFragment;

public class MainActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context context){
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected Fragment createFragment(){
        return GalleryFragment.newInstance();
    }
}

package com.example.lawrenjuip.imageapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.adapters.ImageAdapter;
import com.example.lawrenjuip.imageapp.fragments.GalleryFragment;
import com.example.lawrenjuip.imageapp.fragments.SingleImageFragment;
import com.example.lawrenjuip.imageapp.models.SavedImage;

public class MainActivity extends AppCompatActivity implements ImageAdapter.OnImageClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = GalleryFragment.newInstance();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onImageClick(SavedImage image) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(image.getDeleteHash() == null){
            image.setDeleteHash("");
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable("saved_image", image);
        Fragment fragment = SingleImageFragment.newInstance();
        fragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, fragment).addToBackStack(null).commit();
    }
}

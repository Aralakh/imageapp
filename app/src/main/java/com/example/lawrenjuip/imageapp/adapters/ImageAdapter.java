package com.example.lawrenjuip.imageapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.models.Image;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder>{
    private List<Image> imageList;
    private List<String> phoneGalleryImageList;
    private Context context;
    private Drawable placeholderImage;

    public ImageAdapter(List<Image> images, Context context){
        phoneGalleryImageList.addAll(getAllShownImagesPath());
        imageList = images;
        this.context = context;
    }

    public class ImageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final View view;
        public ImageView imageView;

        public ImageHolder(View itemView){
            super(itemView);
            view = itemView;
            imageView = view.findViewById(R.id.image_item_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView){
            //launch the detailed image view activity/fragment
        }
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.image_list_item_view, viewGroup, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageHolder imageHolder, int position){
        Image image = imageList.get(position);
        placeholderImage = createPlaceHolder();

        Picasso.get()
                .load(image.getLink())
                .error(R.drawable.ic_image_error)
                .placeholder(placeholderImage)
                .into(imageHolder.imageView);
    }

    @Override
    public int getItemCount(){ return imageList == null ? 0 : imageList.size(); }

    private Drawable createPlaceHolder(){
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        Drawable drawable = null;

        try{
            inputStream = assetManager.open("placeholder.png");
            drawable = Drawable.createFromStream(inputStream, null);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }finally {
            if(inputStream == null){
                try{
                    inputStream.close();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        }

        return drawable;
    }

    public void add(Image image){
        imageList.add(image);
        notifyItemInserted(imageList.size() - 1);
    }

    public void remove(Image image){
        imageList.remove(image);
        notifyDataSetChanged();
    }

    public boolean isEmpty(){ return getItemCount() == 0; }

    private List<String> getAllShownImagesPath(){
        Uri uri;
        Cursor cursor;
        int column_index_data;
        List<String> listOfAllImagesPaths = new ArrayList<>();
        String absolutePathOfImage;

        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
        cursor = context.getContentResolver().query(uri, projection, null, null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while(cursor.moveToNext()){
            absolutePathOfImage = cursor.getString(column_index_data);
            listOfAllImagesPaths.add(absolutePathOfImage);
        }

        return listOfAllImagesPaths;
    }
}

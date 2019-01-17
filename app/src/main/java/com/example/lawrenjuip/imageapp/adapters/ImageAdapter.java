package com.example.lawrenjuip.imageapp.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.lawrenjuip.imageapp.R;
import com.example.lawrenjuip.imageapp.models.SavedImage;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    private List<SavedImage> imageList;
    private Context context;
    private Drawable placeholderImage;
    private OnImageClickListener listener;

    public ImageAdapter(List<SavedImage> images, Context context, OnImageClickListener imageClickListener){
        listener = imageClickListener;
        imageList = images;
        this.context = context;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public final View view;
        public ImageView imageView;

        public ImageHolder(View itemView){
            super(itemView);
            view = itemView;
            imageView = view.findViewById(R.id.image_item_view);
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
        final SavedImage image = imageList.get(position);
        placeholderImage = createPlaceHolder();

        Picasso.get()
                .load(image.getImageUrl())
                .error(R.drawable.ic_image_error)
                .placeholder(placeholderImage)
                .into(imageHolder.imageView);

        imageHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClick(image);
            }
        });
    }

    @Override
    public int getItemCount(){ return imageList == null ? 0 : imageList.size(); }

    private Drawable createPlaceHolder(){
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        Drawable drawable = null;

        try{
            inputStream = assetManager.open("portrait_placeholder.png");
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

    public void addAll(List<SavedImage> imageList){
        this.imageList = imageList;
    }

    public void remove(SavedImage image){
        imageList.remove(image);
        notifyDataSetChanged();
    }

    public boolean isEmpty(){ return getItemCount() == 0; }

    public interface  OnImageClickListener {
        void onImageClick(SavedImage image);
    }
}

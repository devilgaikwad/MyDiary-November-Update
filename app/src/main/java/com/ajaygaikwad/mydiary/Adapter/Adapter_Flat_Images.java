package com.ajaygaikwad.mydiary.Adapter;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.ajaygaikwad.mydiary.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;

public class Adapter_Flat_Images extends PagerAdapter {

    private Context context;
    private ArrayList <String> imageUrls;
    public Adapter_Flat_Images(Context context, ArrayList<String> imagearray) {

        this.context = context;
        this.imageUrls = imagearray;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;

    }

    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        RequestOptions requestOptions =new RequestOptions();

        requestOptions.error(R.drawable.no_image);
        requestOptions.fitCenter();

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageUrls.get(position))
                .into(imageView);
        container.addView(imageView);

        /*try{
            Glide.with(context).load(imageUrls.get(position)).apply(new RequestOptions()
                    .placeholder(R.drawable.loading)
                    .centerCrop()
                    .error(R.drawable.internet_error)).into(imageView);
        }catch (Exception e)
        {
            Picasso.with(context)
                    .load(imageUrls.get(position))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.internet_error)
                    .fit()
                    .centerCrop()
                    .into(imageView);
            container.addView(imageView);
        }
*/



        return imageView;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

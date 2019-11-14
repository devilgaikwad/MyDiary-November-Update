package com.ajaygaikwad.mydiary.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.SlidingRootNavActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignFragment extends Fragment {


    public SignFragment() {
        // Required empty public constructor
    }

    private DrawableView drawableView;
    private DrawableViewConfig config = new DrawableViewConfig();
    LinearLayout llImage;
    String imageString;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_sign, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        editor = preferences.edit();
        drawableView = (DrawableView) v.findViewById(R.id.paintView);
        Button strokeWidthMinusButton = (Button) v.findViewById(R.id.strokeWidthMinusButton);
        Button strokeWidthPlusButton = (Button) v.findViewById(R.id.strokeWidthPlusButton);
        Button changeColorButton = (Button) v.findViewById(R.id.changeColorButton);
        Button undoButton = (Button) v.findViewById(R.id.undoButton);
        Button saveButton = (Button) v.findViewById(R.id.saveButton);
        llImage = v.findViewById(R.id.llImage);

        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(15.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1080);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);

        strokeWidthPlusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() + 10);
            }
        });
        strokeWidthMinusButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                config.setStrokeWidth(config.getStrokeWidth() - 10);
            }
        });
        changeColorButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                Random random = new Random();
                config.setStrokeColor(
                        Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
        });
        undoButton.setOnClickListener(new View.OnClickListener() {

            @Override public void onClick(View v) {
                drawableView.undo();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = saveBitMap(getActivity(), llImage);
            }
        });


   return v;
    }

    public void onResume(){
        super.onResume();

        try{
            ((MainNavActivity)getActivity()).setActionBarTitle("Add Signature");
        }catch (Exception e){
            ((SlidingRootNavActivity)getActivity()).setActionBarTitle("Add Signature");
        }
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Signatures");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            imageString = Base64.encodeToString(bytes, Base64.DEFAULT);

            editor.putString("encodedSignString",imageString);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());


        /*Fragment fragment = new AddAppointmentFragment();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        if(preferences.getString("view","").equals("2")){
            ft.replace(R.id.container,  fragment).addToBackStack("").commit();
        }else{
            ft.replace(R.id.fmain,  fragment).addToBackStack("").commit();
        }*/
        getActivity().finish();

        return pictureFile;
    }
    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }
        else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

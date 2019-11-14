package com.ajaygaikwad.mydiary.Fragments;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ajaygaikwad.mydiary.BuildConfig;
import com.ajaygaikwad.mydiary.MainNavActivity;
import com.ajaygaikwad.mydiary.R;
import com.ajaygaikwad.mydiary.SlidingRootNavActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUsFragment extends Fragment {


    public AboutUsFragment() {
        // Required empty public constructor
    }

    TextView versionName;
    private AdView mAdView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_us, container, false);
        versionName = v.findViewById(R.id.versionName);

        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        /*Toast.makeText(getActivity(),
                "PackageName = " + info.packageName + "\nVersionCode = "
                        + info.versionCode + "\nVersionName = "
                        + info.versionName + "\nPermissions = " + info.permissions, Toast.LENGTH_SHORT).show();*/

        versionName.setText(BuildConfig.VERSION_NAME);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        return v;
    }

    public void  onResume() {
        super.onResume();
        try{
            ((MainNavActivity)getActivity()).setActionBarTitle("About Us");
        }catch (Exception e){
            ((SlidingRootNavActivity)getActivity()).setActionBarTitle("About Us");
        }

    }

}

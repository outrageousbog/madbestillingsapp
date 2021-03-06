package com.example.gruppeb.madbestillingsapp.Domain.JSON;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.gruppeb.madbestillingsapp.Domain.FragmentGenerator;
import com.example.gruppeb.madbestillingsapp.FoodFragments.SplashFragment;
import com.example.gruppeb.madbestillingsapp.MainScreen;
import com.example.gruppeb.madbestillingsapp.R;

public class JsonController implements JsonObserver {
    Context context;
    MainScreen mainScreen;
    FragmentGenerator fg;
    SplashFragment fragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fm;

    public JsonController(){}

    public JsonController(Context context, MainScreen mainScreen) {
        this.context = context;
        this.mainScreen = mainScreen;
    }

    public void doAction(){

        if (haveNetworkConnection()) {
            fg = FragmentGenerator.getInstance();
            fg.setContext(context);
            fg.getJsonFiles();
            fg.setController(this);
            beginTransaction();
        }
        else{
            Toast.makeText(context, "Der skete en fejl (intet internet)", Toast.LENGTH_SHORT).show();
        }
    }

    private void beginTransaction() {
        fm = mainScreen.getSupportFragmentManager();
        fragmentTransaction = fm.beginTransaction();
        fragment = new SplashFragment();
        fragmentTransaction.add(R.id.mainscreen_full, fragment).commit();
    }

    @Override
    public void jsonCompleted() {
        fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.remove(fragment).commit();
        mainScreen.initialView();
    }

    private boolean haveNetworkConnection(){
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public void setAdapter(ViewPager viewPager){
        fg.setAdapter(viewPager, mainScreen);
    }

    public String getLanguage(){
        return fg.getLanguage();
    }

    public String getFragmentTitle(int currentItem) {
        return fg.getFragmentTitle(currentItem);
    }

    public String getFragmentDescription(String language, int position){
        return fg.getFragmentDescription(language, position);
    }

    public String getFragmentTitle(String language, int position){
        return fg.getFragmentTitle(language, position);
    }

}

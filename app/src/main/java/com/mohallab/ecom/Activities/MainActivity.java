package com.mohallab.ecom.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mohallab.ecom.Databases.SPManager;
import com.mohallab.ecom.Fragments.CartFragment;
import com.mohallab.ecom.Fragments.CategoryFragment;
import com.mohallab.ecom.Fragments.HomeFragment;
import com.mohallab.ecom.R;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Debug";
    public static final int RC_SIGN_IN = 1;
    private static final String ANONYMOUS = "anonymous";
    private FirebaseAuth mAuth;
    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    NavigationView navigationView;
    MenuItem previousItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        login();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, new HomeFragment());
            fragmentTransaction.commit();
        } else {
            Log.i(TAG, "onRestoreInstanceState");
            CartFragment.cart_list = savedInstanceState.getParcelableArrayList("cart");
        }

        String fragment = getIntent().getStringExtra("fragment");
        if (fragment != null) {
            if (fragment.equals("cart")) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new CartFragment());
                fragmentTransaction.commit();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        item.setCheckable(true);
        item.setChecked(true);
        if (previousItem != null && previousItem != item)
            previousItem.setChecked(false);
        previousItem = item;
        int id = item.getItemId();
        Fragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();

        if (id == R.id.nav_home)
            fragment = new HomeFragment();
        else if (id == R.id.nav_cart) {
            fragment = new CartFragment();
        } else if (id == R.id.breakfast_and_tea) {
            bundle.putString("category", "Breakfast & Tea");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_biscuits) {
            bundle.putString("category", "Biscuits, Snacks & Chocolates");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_nuts) {
            bundle.putString("category", "Dry Fruits & Nuts");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_oils) {
            bundle.putString("category", "Edible Oils, Ghee & Vanaspati");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_food_grains) {
            bundle.putString("category", "Food Grains");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_noodles) {
            bundle.putString("category", "Noodles, Sauce & Instant Foods");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_household) {
            bundle.putString("category", "Personal Care & Household Needs");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_veg) {
            bundle.putString("category", "Vegetables & Fruits");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        } else if (id == R.id.nav_spices) {
            bundle.putString("category", "Spices");
            fragment = new CategoryFragment();
            fragment.setArguments(bundle);
        }

        if (fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putParcelableArrayList("cart", CartFragment.cart_list);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    private void login() {
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSignedInInitialize(user.getDisplayName());
                    //set_nav_user_details();
                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build()
                                    ))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        SPManager.DeleteData(this);
    }
}



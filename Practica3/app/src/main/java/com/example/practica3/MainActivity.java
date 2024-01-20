package com.example.practica3;


import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.example.practica3.databinding.ActivityMainBinding;
import com.example.practica3.ui.home.BikeFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 007;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_CODIGO = 123;
    double latitud=0;
    double longitud=0;
    public static Address address;
    public static String emailString;
    public static FirebaseAuth mAuth;
    public static FirebaseUser user;
    public static boolean isLoged=false;
    private GoogleSignInClient mGoogleSignInClient;
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mostrarUbicacion();

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("967069109504-bpbgvpatlmn8nrd16pekqns06jl2soq7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        updateUI(mAuth.getCurrentUser());
        binding.signInButton.setOnClickListener(this::onClick);
        binding.signOutButton.setOnClickListener(this::onClick);
        binding.btnContinue.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent loginIntent = new Intent(getApplicationContext(), BikeActivity.class);
                 startActivity(loginIntent);
             }
         });



    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();


        } else if (i == R.id.signOutButton) {
            signOut();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();
        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase\

                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            updateUI(mAuth.getCurrentUser());
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user){
        if (user != null) {
            Toast.makeText(this,"Welcome "+user.getDisplayName(),Toast.LENGTH_LONG).show();
            binding.email.setText(mAuth.getCurrentUser().getEmail());
            binding.signInButton.setVisibility(View.GONE);
            binding.signOutButton.setVisibility(View.VISIBLE);
            isLoged=true;
        } else {
            isLoged=false;
            binding.signInButton.setVisibility(View.VISIBLE);
            binding.signOutButton.setVisibility(View.GONE);
        }
    }
    public void mostrarUbicacion(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODIGO);
        }

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                latitud = location.getLatitude();
                                longitud = location.getLongitude();


                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);

                                    if (addresses.size() > 0) {
                                        address = addresses.get(0);

                                        String calle = address.getThoroughfare();
                                        String numeroCalle = address.getSubThoroughfare();
                                        String codPostal = address.getPostalCode();
                                        binding.location.setText(calle+", "+numeroCalle+ ", "+codPostal);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                Toast.makeText(MainActivity.this, "Please activate GPS", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

    }







}




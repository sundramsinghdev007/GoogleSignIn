package in.developer.googlesignin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    LinearLayout ll;
    SignInButton signInButton;
    Button logout_btn;
    private TextView name_tv, email_tv, mobile_tv;
    private ImageView user_img;
    private GoogleApiClient mGoogleApiClient;
    private final static int REQ_CODE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll = findViewById(R.id.ll);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        logout_btn = findViewById(R.id.LogOut);
        logout_btn.setOnClickListener(this);
        name_tv = findViewById(R.id.user_name_tv);
        email_tv = findViewById(R.id.user_name_email);
        user_img = findViewById(R.id.user_img);
        ll.setVisibility(View.GONE);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                enableAutoManage(this,this).
                addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handlerequest(result);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in_button:
                signInMethod();
                break;
            case R.id.LogOut:
                logOutMethod();
                break;
        }
    }

    private void signInMethod(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    private void logOutMethod(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });
    }
    private void handlerequest(GoogleSignInResult googleSignInResult){
        if (googleSignInResult.isSuccess()){
            GoogleSignInAccount result = googleSignInResult.getSignInAccount();
            String name = result.getDisplayName();
            String email = result.getEmail();

            name_tv.setText(name);
            email_tv.setText(email);
            try {
                if (!result.getPhotoUrl().equals("")) {
                    String image_url = result.getPhotoUrl().toString();
                    Glide.with(MainActivity.this).load(image_url).fitCenter().into(user_img);
                }
            }catch (Exception ex){
                Log.e("MainActivity : ",ex.toString());
            }
            updateUI(true);
        }else {
            updateUI(false);
        }
    }
    private void updateUI(boolean isLogin){
        if (isLogin){
            ll.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
        }else {
            ll.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }
}

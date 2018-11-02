package tralafarlaw.miguel.find;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    // firebase auth
    FirebaseAuth mAuth;

    //Cliente del API de firebase
    GoogleApiClient mapiclient;

    public static final String TAG = "SignInActivity";
    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onStart() {
        super.onStart();
        mapiclient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        
        setContentView(R.layout.activity_main);
        inicio_de_sesion();
    }
    public void inicio_de_sesion(){
        //creamos el menu para seleccionar el correo que jalara del celular
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mapiclient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
               // .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .addApi(LocationServices.API)
                .build();
        SignInButton Login_Button = findViewById(R.id.btnSignIn);
        Login_Button.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "login exitoso");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "error de login", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        Intent it = Auth.GoogleSignInApi.getSignInIntent(mapiclient);
        startActivity(it);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GoogleSignInResult result  = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        TrabajarConElLogin(result);
    }
    public void TrabajarConElLogin (GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount acc = result.getSignInAccount();
            //trabajar con la cuenta acc que es el usuario
            firebaseAuthWithGoogle(acc);
        }
    }

}

package tralafarlaw.miguel.find;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    // firebase auth
    FirebaseAuth mAuth;

    //Cliente del API de firebase
    GoogleApiClient mapiclient;

    public static final String TAG = "SignInActivity";
    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicio_de_sesion();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mapiclient.isConnected()){
            loginUser();
        }
    }

    public void inicio_de_sesion(){
        mAuth = FirebaseAuth.getInstance();



        // Configura Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mapiclient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SignInButton login = (SignInButton) findViewById(R.id.btnSignIn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();

            }
        });
    }
    public void loginUser (){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(mapiclient);
        startActivityForResult(intent, RC_SIGN_IN);
        Toast.makeText(getApplicationContext(), "login inicia", Toast.LENGTH_SHORT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getApplicationContext(), "onactvresult", Toast.LENGTH_LONG);
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            ManejarELResultadoDelLogin(result);


        }
    }
    public void ManejarELResultadoDelLogin (GoogleSignInResult result){
        Log.d(TAG, "manejar el resultado: "+result.isSuccess());
        if (result.isSuccess()){

            GoogleSignInAccount account = result.getSignInAccount();
            Toast.makeText(getApplicationContext(), "Bienvenido: "+account.getDisplayName(), Toast.LENGTH_SHORT);
            firebaseAuthWithGoogle(account);

        }else {
            Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"Error de Coneccion");
    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Intent it = new Intent(getApplicationContext(), mapaosm.class);


                            it.putExtra("Nombre", acct.getDisplayName());

                            startActivity(it);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }
}

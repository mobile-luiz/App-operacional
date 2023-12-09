package com.usinacucau.horasparadas;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    public static String MyPREFERENCES;  // referencia pagina sair
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnLogin;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_login );


        mAuth = FirebaseAuth.getInstance();
//        getSupportActionBar().hide();
       // Objects.requireNonNull(getSupportActionBar()).hide();

        FirebaseAuth.getInstance().signOut();  // opção sair do farebase
        // Obter instância de autenticação do Firebase
        auth=FirebaseAuth.getInstance ();
        if (auth.getCurrentUser () != null) {
            startActivity ( new Intent( Login.this, Usuario_logado.class ) );
            FirebaseAuth.getInstance().signOut();
            finish ();
        }

        // defina a vista agora
        setContentView ( R.layout.activity_login );

        inputEmail=( EditText ) findViewById ( R.id.email );
        inputPassword=( EditText ) findViewById ( R.id.password );
        progressBar=( ProgressBar ) findViewById ( R.id.progressBar );

        btnLogin=( Button ) findViewById ( R.id.btn_login);



        btnLogin.setOnClickListener (
                v -> {
                    String email=inputEmail.getText ().toString ();
                    final String password=inputPassword.getText ().toString ();

                    if (TextUtils.isEmpty ( email )) {
                        Toast.makeText (
                                        getApplicationContext (),
                                        "Insira o endereço de e-mail!",
                                        Toast.LENGTH_SHORT )
                                .show ();
                        return;
                    }

                    if (TextUtils.isEmpty ( password )) {
                        Toast.makeText (
                                        getApplicationContext (),
                                        "Digite a senha!",
                                        Toast.LENGTH_SHORT )
                                .show ();
                        return;
                    }

                    ProgressDialog progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setMessage("Autenticando...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    // autenticar usuário
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(
                                    Login.this,
                                    task -> {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(Login.this, Usuario_logado.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            // handle authentication failure
                                            Toast.makeText (
                                                            getApplicationContext (),
                                                            "Erro ao conectar",
                                                            Toast.LENGTH_SHORT )
                                                    .show ();
                                        }
                                    });
                });


        // botões de  registre-se

        Button cadastro=( Button ) findViewById ( R.id.cadastro ); //
        cadastro.setOnClickListener (
                v -> {
                    Intent it=new Intent ( Login.this, Cadastro.class );
                    startActivity ( it );
                });

        //  Chanar aqui recuperar a senha
        Button senha=( Button ) findViewById ( R.id.senha );
        senha.setOnClickListener (
                v -> {
                    Intent it=new Intent ( Login.this, Recuperar_senha.class );
                    startActivity ( it );
                });
    }

    @SuppressLint({"ObsoleteSdkInt" , "MissingSuperCall"})
    @RequiresApi(api= Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() { // Botão BACK padrão do android
        finishAffinity (); // Método para matar a activity e não deixa-lá indexada na pilhagem
        return;

    }


}

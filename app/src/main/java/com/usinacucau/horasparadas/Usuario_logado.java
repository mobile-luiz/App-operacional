package com.usinacucau.horasparadas;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.usinacucau.horasparadas.databinding.ActivityUsuarioLogadoBinding;

public class Usuario_logado extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUsuarioLogadoBinding binding;

    // sair do aplicativo apos 60 segundos
    private long lastPauseTime;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );

        binding = ActivityUsuarioLogadoBinding.inflate ( getLayoutInflater () );
        setContentView ( binding.getRoot () );

        setSupportActionBar ( binding.appBarUsuarioLogado.toolbar );
        binding.appBarUsuarioLogado.fab.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Snackbar.make ( view , "Replace with your own action" , Snackbar.LENGTH_LONG )
                        .setAction ( "Action" , null ).show ();
            }
        } );

        // paradas
        TextView t1 = (TextView) findViewById(R.id.t1);
        t1.setOnClickListener(
                v -> {
                    Intent it = new Intent(Usuario_logado.this, Parada_teste.class);
                    startActivity(it);
                });

        // frequencia
        TextView t2 = (TextView) findViewById(R.id.t2);
        t2.setOnClickListener(
                v -> {
                    Intent it = new Intent(Usuario_logado.this,Apontamento.class);
                    startActivity(it);
                });

        // Consultar apo
        TextView t3 = (TextView) findViewById(R.id.t3);
        t3.setOnClickListener(
                v -> {
                    Intent it = new Intent(Usuario_logado.this,Visualizar_paradas.class);
                    startActivity(it);
                });

        // Escapes erros
        TextView t4 = (TextView) findViewById(R.id.t4);
        t4.setOnClickListener(
                v -> {
                    Intent it = new Intent(Usuario_logado.this,Visualizar_apontamento.class);
                    startActivity(it);
                });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder (
                R.id.nav_home , R.id.nav_gallery , R.id.nav_slideshow )
                .setOpenableLayout ( drawer )
                .build ();
        NavController navController = Navigation.findNavController ( this , R.id.nav_host_fragment_content_usuario_logado );
        NavigationUI.setupActionBarWithNavController ( this , navController , mAppBarConfiguration );
        NavigationUI.setupWithNavController ( navigationView , navController );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate ( R.menu.usuario_logado , menu );
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController ( this , R.id.nav_host_fragment_content_usuario_logado );
        return NavigationUI.navigateUp ( navController , mAppBarConfiguration )
                || super.onSupportNavigateUp ();
    }

    ///sair do aplicativo apos 60 segundos em seegundo plano
    @Override
    protected void onPause() {
        super.onPause();
        lastPauseTime = System.currentTimeMillis();
        startTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopTimer();
    }

    ///sair do aplicativo apos 2 minutos
    private void startTimer() {
        timer = new CountDownTimer ( 120000 , 1000 ) { // Inicializa um CountDownTimer com 1800 segundos (30 minutos) e intervalo de 1000 milissegundos (1 segundo)
            @Override
            public void onTick(long millisUntilFinished) {
                // Este método é chamado a cada segundo durante a contagem regressiva
            }

            @Override
            public void onFinish() {
                finish (); // Quando a contagem regressiva termina, encerra a atividade (app)
            }
        }.start (); // Inicia o CountDownTimer
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel ();

// Cancela o CountDownTimer, se estiver em andamento
        }
    }
}


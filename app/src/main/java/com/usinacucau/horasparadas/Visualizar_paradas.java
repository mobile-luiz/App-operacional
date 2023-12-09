package com.usinacucau.horasparadas;

import static androidx.appcompat.app.AlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Visualizar_paradas extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HorasAdapter horasAdapter;
    private List<ParadaDados> dadosList;
    private DatabaseReference databaseReference;
    private EditText editTextPesquisa;
    private TextView textViewItemCount;

    // sair do aplicativo apos 60 segundos
    private long lastPauseTime;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_visualizar_paradas );

        //getSupportActionBar ().setTitle ( "Painel do administrador app " );
        recyclerView = findViewById ( R.id.recyclerView );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );
        dadosList = new ArrayList<> ();
        horasAdapter = new HorasAdapter ( dadosList );
        recyclerView.setAdapter ( horasAdapter );

        // Carregar as paradas do banco de dados local

        dadosList.clear();

        horasAdapter.notifyDataSetChanged();

        textViewItemCount = findViewById(R.id.textViewItemCount);
        editTextPesquisa = findViewById ( R.id.editTextPesquisa );

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uidDoUsuarioAtual = currentUser.getUid();

            // Obtenha a referência ao nó "paradas" no Firebase
            databaseReference = FirebaseDatabase.getInstance().getReference().child("paradas");
            // Agora, crie uma consulta para filtrar os dados com base no UID do usuário atual
            Query query = databaseReference.orderByChild("uid").equalTo(uidDoUsuarioAtual);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dadosList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ParadaDados paradaDados = dataSnapshot.getValue(ParadaDados.class);
                        if (paradaDados != null) {
                            paradaDados.setId(dataSnapshot.getKey());
                            dadosList.add(paradaDados);
                        }
                    }
                    Collections.reverse(dadosList);
                    horasAdapter.notifyDataSetChanged();
                    updateItemCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Visualizar_paradas.this, "Falha ao obter dados do Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Adicionar um listener para a ação de digitar no EditText de pesquisa
        editTextPesquisa.addTextChangedListener ( new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s , int start , int count , int after) {
                // Nenhum código necessário aqui
            }

            @Override
            public void onTextChanged(CharSequence s , int start , int before , int count) {
                // Filtra os dados com base no texto inserido no EditText
                filtrarDados ( s.toString () );
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nenhum código necessário aqui
            }
        } );

        // Adicionar um clique longo para excluir um item
        horasAdapter.setOnItemLongClickListener ( new HorasAdapter.OnItemLongClickListener () {
            @Override
            public void onItemLongClick(int position) {
                excluirItem ( position );
            }
        } );
    }

    // Method to update the item count
    private void updateItemCount() {
        int count = dadosList.size();
        textViewItemCount.setText("Total dos lançamentos: " + count);
    }

    private void excluirItem(int position) {
        ParadaDados paradaDados = dadosList.get ( position );
        String itemId = paradaDados.getId ();

        Builder builder = new Builder ( this );
        builder.setTitle ( "Excluir item" );
        builder.setMessage ( "Deseja realmente excluir o item selecionado?" );
        builder.setPositiveButton ( "Sim" , new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog , int which) {
                // Remove the item from Firebase
                databaseReference.child ( itemId ).removeValue ()
                        .addOnSuccessListener ( new OnSuccessListener<Void> () {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText ( Visualizar_paradas.this , "Item excluído com sucesso" , Toast.LENGTH_SHORT ).show ();
                            }
                        } )
                        .addOnFailureListener ( new OnFailureListener () {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText ( Visualizar_paradas.this , "Falha ao excluir item" , Toast.LENGTH_SHORT ).show ();
                            }
                        } );
            }
        } );
        builder.setNegativeButton ( "Não" , null );
        builder.create ().show ();
    }

    private void filtrarDados(String textoPesquisa) {
        // Atualiza a lista de dados exibidos no RecyclerView
        List<ParadaDados> dadosFiltrados = new ArrayList<> ();

        for (ParadaDados dado : dadosList) {
            if (dado.frente != null && dado.frente.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.fazenda != null && dado.fazenda.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.frota != null && dado.frota.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.motivoParada != null && dado.motivoParada.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.dataInicial != null && dado.dataInicial.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.dataFinal != null && dado.dataFinal.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.observacao != null && dado.observacao.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.uid != null && dado.uid.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.email != null && dado.email.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.dataHora != null && dado.dataHora.toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.getTempoParadaFormatado () != null && dado.getTempoParadaFormatado ().toLowerCase ().contains ( textoPesquisa.toLowerCase () ) ||
                    dado.numeroOS != null && dado.numeroOS.toLowerCase ().contains ( textoPesquisa.toLowerCase () )) {
                dadosFiltrados.add ( dado );
            }
        }

        horasAdapter.setDados ( dadosFiltrados );
        horasAdapter.notifyDataSetChanged ();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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




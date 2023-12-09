package com.usinacucau.horasparadas;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.FirebaseApp;
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

public class Visualizar_apontamento extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DadosAdapter dadosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_visualizar_apontamento );


        // Inicialização do Firebase
        FirebaseApp.initializeApp ( this );

        // Configuração do Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference myRef = database.getReference ( "apontamento" ); // Substitua pelo nome do seu nó no Firebase

        // Configurar o RecyclerView
        recyclerView = findViewById ( R.id.recyclerView );
        recyclerView.setLayoutManager ( new LinearLayoutManager ( this ) );
        dadosAdapter = new DadosAdapter ( this , new ArrayList<> () );
        recyclerView.setAdapter ( dadosAdapter );

        FirebaseAuth mAuth = FirebaseAuth.getInstance ();
        FirebaseUser currentUser = mAuth.getCurrentUser ();

        if (currentUser != null) {
            String userUid = currentUser.getUid ();

            // Remova a declaração de DatabaseReference myRef = ...

            // Crie a consulta para filtrar os dados pelo UID do usuário atual
            Query query = myRef.orderByChild ( "uid" ).equalTo ( userUid );

            query.addValueEventListener ( new ValueEventListener () {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Dados> dadosList = new ArrayList<> ();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren ()) {
                        Dados dados = snapshot.getValue ( Dados.class );
                        dadosList.add ( dados );
                    }

                    // Inverter a ordem dos dados
                    Collections.reverse ( dadosList );

                    // Atualizar o RecyclerView com os dados recuperados
                    dadosAdapter.setDadosList ( dadosList );
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Tratar erros se necessário
                }
            } );
        } else {
            // O usuário não está autenticado, faça algo aqui, se necessário
        }
    }


}
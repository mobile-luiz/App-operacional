package com.usinacucau.horasparadas;



import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
public class Parada_teste extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    private RadioGroup radioGroup;
    private RadioButton radioButtonA1;
    private RadioButton radioButtonA2;
    private RadioButton radioButtonB1;
    private RadioButton radioButtonB2;
    private RadioButton radioButtonF5;
    private RadioButton radioButtonCM;
    private RadioButton radioButtonOutros;
    private Spinner spinnerFazenda;
    private EditText editTextFrota;
    private Spinner spinnerMotivoParada;
    private Spinner spinnerTipoVeiculo;
    private EditText editTextDataInicial;
    private EditText editTextDataFinal;
    private EditText editTextObservacao;
    private EditText editTextNumeroOS;
    private Button buttonEnviar;
    private TextView textViewTempoParada;

    private DatabaseReference databaseReference;
    private DatabaseReference offlineReference;


    // sair do aplicativo apos 60 segundos
    private long lastPauseTime;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_parada_teste );

        // Inicializa o Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance ();

        // Inicializa as referências dos elementos de UI
        radioGroup = findViewById ( R.id.radioGroup );
        radioButtonA1 = findViewById ( R.id.radioButtonA1 );
        radioButtonA2 = findViewById ( R.id.radioButtonA2 );
        radioButtonB1 = findViewById ( R.id.radioButtonB1 );
        radioButtonB2 = findViewById ( R.id.radioButtonB2 );
        radioButtonF5 = findViewById ( R.id.radioButtonF5 );
        radioButtonCM = findViewById ( R.id.radioButtonCM );
        radioButtonOutros = findViewById ( R.id.radioButtonOutros );
        spinnerFazenda = findViewById ( R.id.spinnerFazenda );
        spinnerTipoVeiculo = findViewById ( R.id.spinnerTipoVeiculo);
        editTextFrota = findViewById ( R.id.editTextFrota );
        spinnerMotivoParada = findViewById ( R.id.spinnerMotivoParada );
        editTextDataInicial = findViewById ( R.id.editTextDataInicial );
        editTextDataFinal = findViewById ( R.id.editTextDataFinal );
        editTextObservacao = findViewById ( R.id.editTextObservacao );
        editTextNumeroOS = findViewById ( R.id.editTextNumeroOS );
        buttonEnviar = findViewById ( R.id.buttonEnviar );
        textViewTempoParada = findViewById ( R.id.textViewTempoParada );

        textViewTempoParada.setText ( "Tempo de parada: 00:00" );
        textViewTempoParada.setVisibility ( View.INVISIBLE );
        List<ParadaDados> listaDadosTemporaria = new ArrayList<> ();

        // Define o listener de clique do botão Enviar
        buttonEnviar.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                // Verifica se o usuário está autenticado
                currentUser = firebaseAuth.getCurrentUser ();
                if (currentUser == null) {
                    // Usuário não autenticado, redirecionar para a tela de login ou exibir mensagem de erro
                    Toast.makeText ( Parada_teste.this , "Usuário não autenticado!" , Toast.LENGTH_SHORT ).show ();
                    return;
                }

                // Verifica a conectividade de rede
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService ( Context.CONNECTIVITY_SERVICE );
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo ();

                if (networkInfo != null && networkInfo.isConnected ()) {
                    // Obtém os valores dos campos de entrada
                    String frente = obterFrenteSelecionada ();
                    String fazenda = spinnerFazenda.getSelectedItem ().toString ();
                    String frota = editTextFrota.getText ().toString ();
                    String motivoParada = spinnerMotivoParada.getSelectedItem ().toString ();
                    String categoria =  spinnerTipoVeiculo.getSelectedItem ().toString ();
                    String dataInicial = editTextDataInicial.getText ().toString ();
                    String dataFinal = editTextDataFinal.getText ().toString ();
                    String observacao = editTextObservacao.getText ().toString ();
                    String numeroOS = editTextNumeroOS.getText ().toString ();

                    // Verifica se todos os campos obrigatórios foram preenchidos
                    if (fazenda.isEmpty () || frota.isEmpty () || motivoParada.isEmpty ()
                            || dataInicial.isEmpty () || dataFinal.isEmpty () || observacao.isEmpty () || numeroOS.isEmpty ()) {
                        Toast.makeText ( Parada_teste.this , "Preencha todos os campos obrigatórios!" , Toast.LENGTH_SHORT ).show ();
                        return;
                    }

                    // Obtém o UID e o e-mail do usuário autenticado
                    String uid = currentUser.getUid ();
                    String email = currentUser.getEmail ();

                    // Calcula o tempo de parada em minutos
                    long tempoParadaMinutos = calcularTempoParadaMinutos ( dataInicial , dataFinal );

                    // Atualiza o TextView com o tempo de parada formatado
                    atualizarTempoParada ( tempoParadaMinutos );

                    // Obtém a data e a hora atual
                    SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd/MM/yyyy HH:mm" , Locale.getDefault () );
                    String dataHoraString = dateFormat.format ( new Date () );

                    // Cria um objeto ParadaDados com os dados inseridos
                    ParadaDados dados = new ParadaDados ( frente , fazenda , frota , categoria, motivoParada , dataInicial , dataFinal , observacao , uid , email , dataHoraString , numeroOS );

                    // Adiciona os dados à lista temporária
                    listaDadosTemporaria.add ( dados );

                    // Limpa os campos de entrada após o envio bem-sucedido
                    spinnerFazenda.setSelection ( 0 );
                    spinnerTipoVeiculo.setSelection ( 0 ); // Define a seleção inicial no spinner
                    editTextFrota.setText ( "" );
                    spinnerMotivoParada.setSelection ( 0 ); // Define a seleção inicial no spinner
                    editTextDataInicial.setText ( "" );
                    editTextDataFinal.setText ( "" );
                    editTextObservacao.setText ( "" );
                    editTextNumeroOS.setText ( "" );
                    textViewTempoParada.setText ( "Tempo de parada: 00:00" );
                    textViewTempoParada.setVisibility ( View.INVISIBLE );

                    // Verifica se há conexão com a internet antes de enviar os dados
                    if (networkInfo.isConnected ()) {
                        // Obtém a referência do Firebase Database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance ().getReference ( "paradas" );

                        // Percorre a lista de dados temporários e envia cada um para o Firebase Realtime Database
                        for (ParadaDados dadosTemporarios : listaDadosTemporaria) {
                            // Cria um novo nó com um ID gerado automaticamente
                            DatabaseReference novoNo = databaseReference.push ();

                            // Obtém a chave do novo nó
                            String chave = novoNo.getKey ();

                            // Atualiza a chave do objeto ParadaDados com a chave gerada
                            dadosTemporarios.setChave ( chave );

                            // Envia os dados para o Firebase Realtime Database
                            novoNo.setValue ( dadosTemporarios )
                                    .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful ()) {
                                                // Dados enviados com sucesso
                                                Toast.makeText ( Parada_teste.this , "Dados enviados com sucesso!" , Toast.LENGTH_SHORT ).show ();
                                            } else {
                                                // Ocorreu um erro ao enviar os dados
                                                Toast.makeText ( Parada_teste.this , "Erro ao enviar os dados. Tente novamente." , Toast.LENGTH_SHORT ).show ();
                                            }
                                        }
                                    } );
                        }

                        // Limpa a lista de dados temporários após o envio bem-sucedido
                        listaDadosTemporaria.clear ();
                    }
                } else {
                    // Sem conexão de rede
                    Toast.makeText ( Parada_teste.this , "Sem conexão com a internet. Verifique sua conexão e tente novamente." , Toast.LENGTH_SHORT ).show ();

                    // Armazena os dados localmente
                    String frente = obterFrenteSelecionada ();
                    String fazenda = spinnerFazenda.getSelectedItem ().toString ();
                    String categoria =  spinnerTipoVeiculo.getSelectedItem ().toString ();
                    String frota = editTextFrota.getText ().toString ();
                    String motivoParada = spinnerMotivoParada.getSelectedItem ().toString ();
                    String dataInicial = editTextDataInicial.getText ().toString ();
                    String dataFinal = editTextDataFinal.getText ().toString ();
                    String observacao = editTextObservacao.getText ().toString ();
                    String numeroOS = editTextNumeroOS.getText ().toString ();

                    // Verifica se todos os campos obrigatórios foram preenchidos
                    if (fazenda.isEmpty () || frota.isEmpty () || motivoParada.isEmpty ()
                            || dataInicial.isEmpty () || dataFinal.isEmpty () || observacao.isEmpty () || numeroOS.isEmpty ()) {
                        Toast.makeText ( Parada_teste.this , "Preencha todos os campos obrigatórios!" , Toast.LENGTH_SHORT ).show ();
                        return;
                    }

                    // Obtém o UID e o e-mail do usuário autenticado
                    String uid = currentUser.getUid ();
                    String email = currentUser.getEmail ();

                    // Calcula o tempo de parada em minutos
                    long tempoParadaMinutos = calcularTempoParadaMinutos ( dataInicial , dataFinal );

                    // Atualiza o TextView com o tempo de parada formatado
                    atualizarTempoParada ( tempoParadaMinutos );

                    // Obtém a data e a hora atual
                    SimpleDateFormat dateFormat = new SimpleDateFormat ( "dd/MM/yyyy HH:mm" , Locale.getDefault () );
                    String dataHoraString = dateFormat.format ( new Date () );

                    // Cria um objeto ParadaDados com os dados inseridos
                    ParadaDados dados = new ParadaDados ( frente , fazenda , frota ,categoria, motivoParada , dataInicial , dataFinal , observacao , uid , email , dataHoraString , numeroOS );

                    // Adiciona os dados à lista temporária
                    listaDadosTemporaria.add ( dados );

                    // Limpa os campos de entrada após o envio bem-sucedido
                    spinnerFazenda.setSelection ( 0 );
                    spinnerTipoVeiculo.setSelection ( 0 );// Define a seleção inicial no spinner
                    editTextFrota.setText ( "" );
                    spinnerMotivoParada.setSelection ( 0 ); // Define a seleção inicial no spinner
                    editTextDataInicial.setText ( "" );
                    editTextDataFinal.setText ( "" );
                    editTextObservacao.setText ( "" );
                    editTextNumeroOS.setText ( "" );
                    textViewTempoParada.setText ( "Tempo de parada: 00:00" );
                    textViewTempoParada.setVisibility ( View.INVISIBLE );

                }

            }
        } );


        // Registra um BroadcastReceiver para verificar quando a conexão de rede é reestabelecida
        BroadcastReceiver networkChangeReceiver = new BroadcastReceiver () {
            @Override
            public void onReceive(Context context , Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService ( Context.CONNECTIVITY_SERVICE );
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo ();

                if (networkInfo != null && networkInfo.isConnected ()) {
                    // Verifica se há dados armazenados localmente
                    if (!listaDadosTemporaria.isEmpty ()) {
                        // Obtém a referência do Firebase Database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance ().getReference ( "paradas" );

                        // Percorre a lista de dados temporários e envia cada um para o Firebase Realtime Database
                        for (ParadaDados dadosTemporarios : listaDadosTemporaria) {
                            // Cria um novo nó com um ID gerado automaticamente
                            DatabaseReference novoNo = databaseReference.push ();

                            // Obtém a chave do novo nó
                            String chave = novoNo.getKey ();

                            // Atualiza a chave do objeto ParadaDados com a chave gerada
                            dadosTemporarios.setChave ( chave );

                            // Envia os dados para o Firebase Realtime Database
                            novoNo.setValue ( dadosTemporarios )
                                    .addOnCompleteListener ( new OnCompleteListener<Void> () {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful ()) {
                                                // Dados enviados com sucesso
                                                Toast.makeText ( Parada_teste.this , "Dados enviados com sucesso!" , Toast.LENGTH_SHORT ).show ();
                                            } else {
                                                // Ocorreu um erro ao enviar os dados
                                                Toast.makeText ( Parada_teste.this , "Erro ao enviar os dados. Tente novamente." , Toast.LENGTH_SHORT ).show ();
                                            }
                                        }
                                    } );
                        }

                        // Limpa a lista de dados temporários após o envio bem-sucedido
                        listaDadosTemporaria.clear ();
                    }
                }
            }
        };

// Registra o BroadcastReceiver para a ação de mudança de rede
        registerReceiver ( networkChangeReceiver , new IntentFilter ( ConnectivityManager.CONNECTIVITY_ACTION ) );


        // Define os listeners de clique dos EditTexts de data e hora
        editTextDataInicial.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                exibirDatePicker ( editTextDataInicial );
            }
        } );

        // boatao visializar paradas
        @SuppressLint({"MissingInflatedId" , "LocalSuppress"})
        Button lacan = (Button) findViewById ( R.id.lacan );
        lacan.setOnClickListener (
                v -> {
                    Intent it = new Intent ( Parada_teste.this , Visualizar_paradas.class );
                    startActivity ( it );
                } );

        // botao infor  chamar informaçoes
        @SuppressLint({"MissingInflatedId" , "LocalSuppress"})
        Button infor = (Button) findViewById ( R.id.infor );
        infor.setOnClickListener (
                v -> {
                    Intent it = new Intent ( Parada_teste.this , Visualizar_paradas.class );
                    startActivity ( it );
                } );

        editTextDataFinal.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                exibirDatePicker ( editTextDataFinal );
            }
        } );
    }

    // Retorna a frente selecionada
    @SuppressLint("NonConstantResourceId")
    private String obterFrenteSelecionada() {
        // Create a HashMap to map RadioButton IDs to their corresponding values
        HashMap<Integer, String> radioButtonMapping = new HashMap<>();
        radioButtonMapping.put(R.id.radioButtonA1, "A1");
        radioButtonMapping.put(R.id.radioButtonA2, "A2");
        radioButtonMapping.put(R.id.radioButtonB1, "B1");
        radioButtonMapping.put(R.id.radioButtonB2, "B2");
        radioButtonMapping.put(R.id.radioButtonF5, "F5");
        radioButtonMapping.put(R.id.radioButtonCM, "CM");
        radioButtonMapping.put(R.id.radioButtonOutros, "Outros");

        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        // Check if the selectedRadioButtonId is in the mapping, and return the corresponding value
        if (radioButtonMapping.containsKey(selectedRadioButtonId)) {
            return radioButtonMapping.get(selectedRadioButtonId);
        } else {
            return "";  // Return a default value if none is selected
        }
    }

    // Exibe o DatePicker para selecionar uma data
    private void exibirDatePicker(final EditText editTextData) {
        Calendar calendar = Calendar.getInstance();

        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Parada_teste.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dataSelecionada = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                dayOfMonth, monthOfYear + 1, year);
                        exibirTimePicker(dataSelecionada, editTextData);
                    }
                }, ano, mes, dia);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    // Exibe o TimePicker para selecionar uma hora
    private void exibirTimePicker(final String data, final EditText editTextData) {
        Calendar calendar = Calendar.getInstance();

        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Parada_teste.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String horaSelecionada = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        editTextData.setText(data + " " + horaSelecionada);

                        if (!editTextDataInicial.getText().toString().isEmpty() && !editTextDataFinal.getText().toString().isEmpty()) {
                            long tempoParadaMinutos = calcularTempoParadaMinutos(editTextDataInicial.getText().toString(), editTextDataFinal.getText().toString());
                            atualizarTempoParada(tempoParadaMinutos);
                        }
                    }
                }, hora, minuto, true);
        timePickerDialog.show();
    }

    // Calcula o tempo de parada em minutos
    private long calcularTempoParadaMinutos(String dataInicial, String dataFinal) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            Date dataInicialDate = simpleDateFormat.parse(dataInicial);
            Date dataFinalDate = simpleDateFormat.parse(dataFinal);

            long diferencaMillis = dataFinalDate.getTime() - dataInicialDate.getTime();
            return TimeUnit.MILLISECONDS.toMinutes(diferencaMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Atualiza o TextView com o tempo de parada formatado
    private void atualizarTempoParada(long tempoParadaMinutos) {
        String tempoParadaFormatado = String.format(Locale.getDefault(), "%02d:%02d", tempoParadaMinutos / 60, tempoParadaMinutos % 60);
        textViewTempoParada.setText("Tempo de parada: " + tempoParadaFormatado);
        textViewTempoParada.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent it = new Intent(Parada_teste.this, Usuario_logado.class);
                startActivity(it);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //voltar tela anterior
    @SuppressLint("MissingSuperCall")
    @RequiresApi(api= Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() { // Botão BACK padrão do android
        Intent it = new Intent(Parada_teste.this, Usuario_logado.class);
        startActivity(it);
        return ;

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





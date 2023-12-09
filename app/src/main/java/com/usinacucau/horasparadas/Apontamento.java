package com.usinacucau.horasparadas;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.Manifest;
public class Apontamento extends AppCompatActivity {
    private EditText editTextMatricula, editTextNome, editTextDataInicio, editTextDataFinal;

    private TextView textViewDiferencaHoras;
    private Spinner spinnerFuncao;
    private Spinner spinnerObs;
    private ImageView imageView;
    private Button btnTirarFoto, btnEnviar, btnCancelar;
    // Referências ao nó principal do Realtime Database e ao armazenamento do Firebase
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("apontamento");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("imagens_apontamento");
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apontamento);

        // Inicializar as views
        editTextMatricula = findViewById(R.id.editTextMatricula);
        editTextNome = findViewById(R.id.editTextNome);
        editTextDataInicio = findViewById(R.id.editTextDataInicio);
        editTextDataFinal = findViewById(R.id.editTextDataFinal);
        textViewDiferencaHoras = findViewById(R.id.textViewDiferencaHoras);
        spinnerFuncao = findViewById(R.id.spinnerFuncao);
        spinnerObs = findViewById(R.id.spinneObs);
        imageView = findViewById(R.id.imageView);
        btnTirarFoto = findViewById(R.id.btnTirarFoto);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Configurar o spinner com opções (substitua R.array.opcoes_array com suas opções)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.opcoes_arrayy, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuncao.setAdapter(adapter);

        // Configurar o botão para tirar foto
        btnTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tirarFoto();
            }
        });

        // Configurar o botão de enviar
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarDadosParaFirebase();
            }
        });

        // Configurar o botão de cancelar
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Configurar o clique nos campos de data
        editTextDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog(editTextDataInicio);
            }
        });

        editTextDataFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog(editTextDataFinal);
            }
        });
    }

    private void showDateTimePickerDialog(final EditText editText) {
        final Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Apontamento.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                // Após selecionar a data, exibe o diálogo de seleção de hora
                showTimePickerDialog(editText, selectedYear, selectedMonth, selectedDayOfMonth);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showTimePickerDialog(final EditText editText, final int year, final int month, final int day) {
        final Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Apontamento.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                // Atualiza o campo de texto com a data e hora selecionadas
                Calendar selectedDateTime = new GregorianCalendar (year, month, day, selectedHour, selectedMinute);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String dateTimeString = dateFormat.format(selectedDateTime.getTime());
                editText.setText(dateTimeString);

                // Se o editText for editTextDataFinal, calcular e exibir a diferença de horas
                if (editText.getId() == R.id.editTextDataFinal) {
                    calcularEExibirDiferencaHoras();
                }
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void calcularEExibirDiferencaHoras() {
        // Obter os valores das datas e horas
        String dataInicio = editTextDataInicio.getText().toString().trim();
        String dataFinal = editTextDataFinal.getText().toString().trim();

        if (!TextUtils.isEmpty(dataInicio) && !TextUtils.isEmpty(dataFinal)) {
            // Calcular a diferença de horas
            SimpleDateFormat dateFormatHora = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            try {
                Date inicio = dateFormatHora.parse(dataInicio);
                Date fim = dateFormatHora.parse(dataFinal);

                long diferenca = fim.getTime() - inicio.getTime();
                long segundos = diferenca / 1000;
                long minutos = segundos / 60;
                long horas = minutos / 60;

                // Calcular os minutos restantes após calcular as horas
                minutos = minutos % 60;

                // Exibir o resultado no TextView
                TextView textViewDiferencaHoras = findViewById(R.id.textViewDiferencaHoras);
                textViewDiferencaHoras.setText("Horas trabalhadas: " + horas + " horas e " + minutos + " minutos");
                textViewDiferencaHoras.setVisibility(View.VISIBLE);

            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(Apontamento.this, "Erro ao calcular horas trabalhadas.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void tirarFoto() {
        // Verifica se a permissão CAMERA foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Solicita permissão CAMERA se ainda não foi concedida
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Permissão já foi concedida, prossegue para abrir a câmera
            abrirCamera();
        }
    }

    private void abrirCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            // Verifica se a permissão foi concedida
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, prossegue para abrir a câmera
                abrirCamera();
            } else {
                // Permissão negada. Trata conforme necessário (ex: exibe mensagem para o usuário)
                Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void enviarDadosParaFirebase() {
        // Obter o usuário autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Obter o UID e o e-mail do usuário
            String uid = currentUser.getUid();
            String email = currentUser.getEmail();

            String matricula = editTextMatricula.getText().toString().trim();
            String nome = editTextNome.getText().toString().trim();
            String dataInicio = editTextDataInicio.getText().toString().trim();
            String dataFinal = editTextDataFinal.getText().toString().trim();
            String diferencaHoras = textViewDiferencaHoras.getText().toString().trim();
            String funcao = spinnerFuncao.getSelectedItem().toString();
            String obs = spinnerObs.getSelectedItem().toString();

            if (!TextUtils.isEmpty(matricula) && !TextUtils.isEmpty(nome) && !TextUtils.isEmpty(dataInicio) && !TextUtils.isEmpty(dataFinal)) {
                // Obter a data e hora atuais
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                String dataHoraAtual = dateFormat.format(calendar.getTime());


                // Verificar se uma imagem foi selecionada
                if (imageView.getDrawable() != null) {
                    // Cria um nome único para a imagem usando timestamp
                    String nomeImagem = "imagem_" + System.currentTimeMillis() + ".jpg";
                    StorageReference caminhoImagem = storageReference.child(nomeImagem);

                    // Obter a imagem como um byte array
                    imageView.setDrawingCacheEnabled(true);
                    imageView.buildDrawingCache();
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] dadosImagem = stream.toByteArray();

                    // Upload da imagem para o Firebase Storage
                    UploadTask uploadTask = caminhoImagem.putBytes(dadosImagem);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot> () {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            //progressDialog.dismiss(); // Fecha o ProgressDialog
                            if (task.isSuccessful()) {
                                // Imagem carregada com sucesso, agora obtenha a URL da imagem
                                caminhoImagem.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri> () {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // Obter a URL da imagem
                                        String urlImagem = uri.toString();

                                        // Continuar com o envio de dados para o Realtime Database
                                        salvarDadosNoRealtimeDatabase(matricula, nome, dataInicio, dataFinal, funcao,obs, dataHoraAtual, uid, email, urlImagem, diferencaHoras);
                                    }

                                });
                            } else {
                                // Se houver um erro durante o upload da imagem, manipule o erro
                                Toast.makeText(Apontamento.this, "Erro ao enviar a imagem.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    //progressDialog.dismiss(); // Fecha o ProgressDialog
                    // Caso nenhuma imagem tenha sido selecionada
                    salvarDadosNoRealtimeDatabase(matricula, nome, dataInicio, dataFinal, funcao,obs, dataHoraAtual, uid, email, null, diferencaHoras);
                }
            } else {
                // Se algum campo estiver vazio, exiba uma mensagem de erro ou tome outra ação apropriada.
                Toast.makeText(Apontamento.this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // O usuário não está autenticado. Você pode redirecionar para a tela de login ou tomar outra ação apropriada.
            Toast.makeText(Apontamento.this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarDadosNoRealtimeDatabase(String matricula, String nome, String dataInicio, String dataFinal, String funcao,String obs, String dataHoraAtual, String uid, String email, String urlImagem, String diferencaHoras) {
        // Cria um objeto Dados
        Dados dados = new Dados(matricula, nome, dataInicio, dataFinal, funcao,obs, dataHoraAtual, uid, email, urlImagem, diferencaHoras);
        // Envia para o Firebase usando push() para gerar uma chave única automaticamente
        databaseReference.push().setValue(dados);
        // Limpa os campos
        limparCampos();
        Toast.makeText(Apontamento.this, "Dados enviados com sucesso.", Toast.LENGTH_SHORT).show();
    }

    private void limparCampos() {
        editTextMatricula.setText("");
        editTextNome.setText("");
        editTextDataInicio.setText("");
        editTextDataFinal.setText("");
        textViewDiferencaHoras.setText("");
        spinnerFuncao.setSelection(0);
        spinnerObs.setSelection(0);
        imageView.setImageBitmap(null);
    }
}

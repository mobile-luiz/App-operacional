package com.usinacucau.horasparadas;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ParadaDados {

    public String id;
    public String frente;
    public String fazenda;
    public String frota;
    public String motivoParada;
    public String categoria;
    public String dataInicial;
    public String dataFinal;
    public String observacao;
    public String uid;
    public String email;
    public String dataHora;
    public String numeroOS;

    public ParadaDados() {
        // Construtor vazio necessário para o Firebase
    }

    public ParadaDados(String frente, String fazenda, String frota,String categoria, String motivoParada,
                       String dataInicial, String dataFinal, String observacao,
                       String uid, String email, String dataHora, String numeroOS) {
        this.frente = frente;
        this.fazenda = fazenda;
        this.frota = frota;
        this.motivoParada = motivoParada;
        this.categoria = categoria;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.observacao = observacao;
        this.uid = uid;
        this.email = email;
        this.dataHora = dataHora;
        this.numeroOS = numeroOS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTempoParadaFormatado() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date dataInicio = format.parse(dataInicial);
            Date dataFim = format.parse(dataFinal);

            long diferencaMilissegundos = dataFim.getTime() - dataInicio.getTime();
            int tempoParadaMinutos = (int) (diferencaMilissegundos / (60 * 1000));

            int horas = tempoParadaMinutos / 60;
            int minutos = tempoParadaMinutos % 60;
            return String.format("%02d:%02d", horas, minutos);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "00:00";
    }

    public void setChave(String chave) {
        // Implemente a lógica para definir a chave da parada
        this.id = chave;
    }

    public String getNumeroOS() {
        return numeroOS;
    }

    public void setNumeroOS(String numeroOS) {
        this.numeroOS = numeroOS;
    }

    public String getUid() {
        return uid;
    }

}
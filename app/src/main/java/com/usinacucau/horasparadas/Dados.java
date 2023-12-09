package com.usinacucau.horasparadas;

public class Dados {
    public String matricula;
    public String nome;
    public String dataInicio;
    public String dataFinal;
    public String funcao;

    public String obs;
    public String dataHoraAtual;
    public String uid;
    public String email;
    public String urlImagem;
    public String diferencaHoras; // Nova propriedade para armazenar a diferença de horas

    public Dados() {
        // Construtor padrão necessário para chamar DataSnapshot.getValue(Dados.class)
    }

    public Dados(String matricula, String nome, String dataInicio, String dataFinal, String funcao, String obs, String dataHoraAtual, String uid, String email, String urlImagem, String diferencaHoras) {
        this.matricula = matricula;
        this.nome = nome;
        this.dataInicio = dataInicio;
        this.dataFinal = dataFinal;
        this.funcao = funcao;
        this.obs = obs;
        this.dataHoraAtual = dataHoraAtual;
        this.uid = uid;
        this.email = email;
        this.urlImagem = urlImagem;
        this.diferencaHoras = diferencaHoras;
    }
}



package com.usinacucau.horasparadas;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class DadosAdapter extends RecyclerView.Adapter<DadosAdapter.ViewHolder> {

    private List<Dados> dadosList;
    private Context context;

    public DadosAdapter(Context context, List<Dados> dadosList) {
        this.context = context;
        this.dadosList = dadosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dados, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dados dados = dadosList.get(position);

        // Carregar dados nos TextViews
        holder.textViewMatricula.setText("Matrícula: " + dados.matricula);
        holder.textViewNome.setText("Nome: " + dados.nome);
        holder.textViewDataInicio.setText("Data de Início: " + dados.dataInicio);
        holder.textViewDataFinal.setText("Data Final: " + dados.dataFinal);
        holder.textViewFuncao.setText("Função: " + dados.funcao);
        holder.textViewObs.setText("Observação: " + dados.obs);
        holder.textViewDataHoraAtual.setText("Lançado em : " + dados.dataHoraAtual);

        // Carregar imagem usando Glide
        Glide.with(context)
                .load(dados.urlImagem)
                // .placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                .into(holder.imageViewImagem);
    }

    @Override
    public int getItemCount() {
        return dadosList.size();
    }

    public void setDadosList(List<Dados> dadosList) {
        this.dadosList = dadosList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMatricula;
        public TextView textViewNome;
        public TextView textViewDataInicio;
        public TextView textViewDataFinal;
        public TextView textViewFuncao;
        public TextView textViewObs;
        public TextView textViewDataHoraAtual;
        public ImageView imageViewImagem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMatricula = itemView.findViewById(R.id.textViewMatricula);
            textViewNome = itemView.findViewById(R.id.textViewNome);
            textViewDataInicio = itemView.findViewById(R.id.textViewDataInicio);
            textViewDataFinal = itemView.findViewById(R.id.textViewDataFinal);
            textViewFuncao = itemView.findViewById(R.id.textViewFuncao);
            textViewObs = itemView.findViewById(R.id.textViewObs);
            textViewDataHoraAtual = itemView.findViewById(R.id.textViewDataHoraAtual);
            imageViewImagem = itemView.findViewById(R.id.imageViewImagem);
        }
    }
}

package com.usinacucau.horasparadas;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HorasAdapter extends RecyclerView.Adapter<HorasAdapter.ViewHolder> {
    private List<ParadaDados> registros;
    private OnItemLongClickListener onItemLongClickListener;

    public HorasAdapter(List<ParadaDados> registros) {
        this.registros = registros;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void sethoras(List<ParadaDados> horas) {
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro_parada, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParadaDados registro = registros.get(position);

        holder.textViewFrente.setText("Frente: " + registro.frente);
        holder.textViewCategoria.setText("Categoria: " + registro.categoria);
        holder.textViewFazenda.setText("Fazenda: " + registro.fazenda);
        holder.textViewFrota.setText("Frota: " + registro.frota);
        holder.textViewMotivoParada.setText("Motivo de Parada: " + registro.motivoParada);
        holder.textViewDataInicial.setText("Data Inicial: " + registro.dataInicial);
        holder.textViewDataFinal.setText("Data Final: " + registro.dataFinal);
        holder.textViewObservacao.setText("Observação: " + registro.observacao);
        holder.textViewUid.setText("UID: " + registro.uid);
        holder.textViewEmail.setText("Email: " + registro.email);
        holder.textViewDataHora.setText("Lançamento em: " + registro.dataHora);
        holder.textViewTempoParada.setText("Tempo de Parada: " + registro.getTempoParadaFormatado());
        holder.textViewNumeroOS.setText("Número OS: " + registro.numeroOS);
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public void setDados(List<ParadaDados> dadosFiltrados) {
        registros = dadosFiltrados; // Substitui a lista de registros
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView textViewFrente;
        public TextView textViewCategoria;
        public TextView textViewFazenda;
        public TextView textViewFrota;
        public TextView textViewMotivoParada;
        public TextView textViewDataInicial;
        public TextView textViewDataFinal;
        public TextView textViewObservacao;
        public TextView textViewUid;
        public TextView textViewEmail;
        public TextView textViewDataHora;
        public TextView textViewTempoParada;
        public TextView textViewNumeroOS;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFrente = itemView.findViewById(R.id.textViewFrente);
            textViewCategoria = itemView.findViewById(R.id.textViewCategoria);
            textViewFazenda = itemView.findViewById(R.id.textViewFazenda);
            textViewFrota = itemView.findViewById(R.id.textViewFrota);
            textViewMotivoParada = itemView.findViewById(R.id.textViewMotivoParada);
            textViewDataInicial = itemView.findViewById(R.id.textViewDataInicial);
            textViewDataFinal = itemView.findViewById(R.id.textViewDataFinal);
            textViewObservacao = itemView.findViewById(R.id.textViewObservacao);
            textViewUid = itemView.findViewById(R.id.textViewUid);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewDataHora = itemView.findViewById(R.id.textViewDataHora);
            textViewTempoParada = itemView.findViewById(R.id.textViewTempoParada);
            textViewNumeroOS = itemView.findViewById(R.id.textViewNumeroOS);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClickListener.onItemLongClick(position);
                    return true;
                }
            }
            return false;
        }
    }
}

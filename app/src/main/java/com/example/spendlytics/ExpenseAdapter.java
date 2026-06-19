package com.example.spendlytics;

import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> list;
    private OnDeleteListener onDelete;

    public interface OnDeleteListener {
        void onDelete(int id);
    }

    public ExpenseAdapter(List<Expense> list, OnDeleteListener onDelete) {
        this.list = list;
        this.onDelete = onDelete;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense e = list.get(position);
        holder.tvCategory.setText(e.getCategory());
        holder.tvAmount.setText("₹" + String.format("%.2f", e.getAmount()));
        holder.tvDate.setText(e.getDate());
        holder.tvNote.setText(e.getNote());
        holder.btnDelete.setOnClickListener(v -> onDelete.onDelete(e.getId()));
    }

    @Override public int getItemCount() { return list.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvDate, tvNote;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvAmount   = itemView.findViewById(R.id.tvAmount);
            tvDate     = itemView.findViewById(R.id.tvDate);
            tvNote     = itemView.findViewById(R.id.tvNote);
            btnDelete  = itemView.findViewById(R.id.btnDelete);
        }
    }
}

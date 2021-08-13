package com.example.loginregister.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginregister.Fragment2;
import com.example.loginregister.R;
import com.example.loginregister.Subject;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    ArrayList<Subject> list;
    private OnItemClickListener mListener = null;


    public SubjectAdapter(ArrayList<Subject> list){
        this.list = list;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject, parent, false);
        SubjectViewHolder subjectViewHolder = new SubjectViewHolder(view);

        return subjectViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        holder.onBind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {
        private TextView textView1;
        private TextView textView2;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);

            textView1 = itemView.findViewById(R.id.textView1);
            textView2 = itemView.findViewById(R.id.textView2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        Log.e("###", list.get(pos).getName());
                        if(mListener != null){
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }

        void onBind(Subject subject) {
            textView1.setText(subject.getName());
            textView2.setText(subject.getCode());
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    public void setOnItemListener(OnItemClickListener listener){
        this.mListener = listener;
    }
}

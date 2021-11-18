package com.example.helloroutine;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

class ViewHolder extends RecyclerView.ViewHolder {
    public static TextView textView, textView2, textView3;
    public static ProgressBar progressBar;

    ViewHolder(Context context, View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        textView2 = itemView.findViewById(R.id.challenge_title);
        textView3 = itemView.findViewById(R.id.challenge_sub);
        progressBar = itemView.findViewById(R.id.challenge_prg);

    }
}

package com.example.helloroutine;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

class ViewHolder extends RecyclerView.ViewHolder {
    TextView textView, textView2, textView3, txtRqId, txtRvId, txtFav, txtSub;
    ProgressBar progressBar, prgFav;
    Button btnCancel, btnRefuse, btnAccept;
    CheckBox fav;

    ViewHolder(Context context, View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.textView);
        textView2 = itemView.findViewById(R.id.challenge_title);
        textView3 = itemView.findViewById(R.id.challenge_sub);
        txtRqId = itemView.findViewById(R.id.txtRqID);
        txtRvId = itemView.findViewById(R.id.txtRvID);
        txtFav = itemView.findViewById(R.id.title);
        txtSub = itemView.findViewById(R.id.sub);
        progressBar = itemView.findViewById(R.id.challenge_prg);
        prgFav = itemView.findViewById(R.id.prg);
        btnCancel = itemView.findViewById(R.id.btnCancel);
        btnRefuse = itemView.findViewById(R.id.btnRefuse);
        btnAccept = itemView.findViewById(R.id.btnAccept);
        fav = itemView.findViewById(R.id.cbFav);

    }
}

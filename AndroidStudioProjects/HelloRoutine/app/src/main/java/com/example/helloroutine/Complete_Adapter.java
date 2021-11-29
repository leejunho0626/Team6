package com.example.helloroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Complete_Adapter extends RecyclerView.Adapter<ViewHolder> {
    static ArrayList<String> arrayList;
    static ArrayList<String> arrayList2;

    public Complete_Adapter() {
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_complete, parent, false);
        ViewHolder viewholder = new ViewHolder(context, view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = arrayList.get(position);
        String text2 = arrayList2.get(position);

        holder.txtCom.setText(text);
        holder.txtTime.setText(text2);


    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setArrayData(String strData, String time) {

        arrayList.add(strData);
        arrayList2.add(time);


    }
}

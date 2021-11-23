package com.example.helloroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Challenge_Adapter extends RecyclerView.Adapter<ViewHolder> {
    ArrayList<String> arrayList;
    ArrayList<Integer> arrayList2;
    ArrayList<Boolean> arrayList3;
    ArrayList<String> arrayList4;

    public Challenge_Adapter() {
        arrayList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_challenge, parent, false);
        ViewHolder viewholder = new ViewHolder(context, view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = arrayList.get(position);
        int value = arrayList2.get(position);
        boolean fav = arrayList3.get(position);
        holder.txtFav.setText(text);
        holder.txtSub.setText(Integer.toString(value));
        holder.prgFav.setProgress(value);
        holder.fav.setChecked(fav);


    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public void setArrayData(String txtList, int value, boolean fav) {

        arrayList.add(txtList);
        arrayList2.add(value);
        arrayList3.add(fav);
        notifyDataSetChanged();

    }
}

package com.example.helloroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ChallengeAdapter extends RecyclerView.Adapter<ViewHolder> {
    static ArrayList<String> arrayList;
    static ArrayList<Integer> arrayList2;
    static ArrayList<Integer> arrayList3;
    public ChallengeAdapter() {
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_fav, parent, false);
        ViewHolder viewholder = new ViewHolder(context, view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = arrayList.get(position);
        int text2 = arrayList2.get(position);
        int text3 = arrayList3.get(position);
        holder.textView2.setText(text);
        holder.textView3.setText(Integer.toString(text2)+"%");
        holder.progressBar.setProgress(text3);

    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setArrayData(String strData, int value, int value2) {

        arrayList.add(strData);
        arrayList2.add(value);
        arrayList3.add(value2);
    }
}

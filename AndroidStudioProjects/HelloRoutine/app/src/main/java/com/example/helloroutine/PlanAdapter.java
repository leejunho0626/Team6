package com.example.helloroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> implements OnPlanItemClickListener{
    ArrayList<String> arrayList;
    OnPlanItemClickListener listener;
    public PlanAdapter() {
        arrayList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_list, parent, false);

        return new ViewHolder(view,this);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = "▶ "+arrayList.get(position);
        holder.textView.setText(text);


    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public void OnItemClick(ViewHolder holder, View view, int position){
        if(listener != null){
            listener.OnItemClick(holder, view, position);
        }
    }
    //외부에서 리스너를 설정할 수 있도록 메서드 추가 하기
    public void setOnItemClicklistener(OnPlanItemClickListener listener){
        this.listener = listener;
    }


    public void setArrayData(String strData) {

        arrayList.add(strData);
        notifyDataSetChanged();

    }

    //뷰홀더를 내부클래스로 선언
    static class ViewHolder extends RecyclerView.ViewHolder{

        //뷰홀더에 아이템.xml 변수 선언
        TextView textView;

        //뷰홀더 생성자로 전달되는 뷰 객체 참조하기
        public ViewHolder(@NonNull View itemView,OnPlanItemClickListener listener) {
            super(itemView);

            //뷰 객체에 들어 있는 텍스트뷰 참조하기
            textView = itemView.findViewById(R.id.textView);

            itemView.setOnClickListener(new View.OnClickListener() {//아이템 뷰에 OnClickListener 설정하기
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    //아이템 뷰 클릭 시 미리 정의한 다른 리스너의 메서드 호출하기
                    if(listener != null){
                        listener.OnItemClick(PlanAdapter.ViewHolder.this,v,position);
                    }
                }
            });
        }

        public void setItem(Plan item){
            textView.setText(item.getPlan());
        }
    }


    //아이템 추가 메소드
    public void addItem(String item){
        arrayList.add(item);
        notifyItemChanged(0);
    }

    public String getItem(int position){
        return arrayList.get(position);
    }
}

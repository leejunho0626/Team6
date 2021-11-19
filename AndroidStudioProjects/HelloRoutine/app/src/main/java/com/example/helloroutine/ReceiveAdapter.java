package com.example.helloroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ReceiveAdapter extends RecyclerView.Adapter<ViewHolder> {
    static ArrayList<String> arrayList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public ReceiveAdapter() {
        arrayList = new ArrayList<>();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_rv, parent, false);
        ViewHolder viewholder = new ViewHolder(context, view);
        return viewholder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = position+1+". "+arrayList.get(position);
        holder.txtRvId.setText(text);
        holder.btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRv(arrayList.get(position));

            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public void setArrayData(String strData) {
        arrayList.add(0,strData);
    }

    public void deleteRv(String id){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("ID").collection(id).document("uid")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    //DB 필드명 표시 지워서 데이터 값만 표시
                    String str2 = document.getData().toString();
                    str2 = str2.substring(str2.indexOf("=")+1);
                    String y = str2.substring(0, str2.indexOf("}"));

                    db.collection("DB").document("User").collection(user.getUid()).document("AS_Friend").collection("Uid").document(y)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    db.collection("DB").document("User").collection(y).document("RQ_Friend").collection("Uid").document(user.getUid())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });


                } else {

                }
            }
        });



    }


}

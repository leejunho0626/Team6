package com.example.helloroutine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    ArrayList<String> arrayList;
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
                refuseRv(arrayList.get(position), position);

            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptRv(arrayList.get(position));

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

    //거절
    public void refuseRv(String id, int postion){

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
                                                    notifyItemRemoved(postion);

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

    //수락
    public void acceptRv(String id){
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
                    String uid = str2.substring(0, str2.indexOf("}")); //상대 id의 uid

                    addFriend(uid);
                    deletList(uid);



                } else {

                }
            }
        });


    }
    //친구 추가
    public void addFriend(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserFriend userFriend = new UserFriend(uid);
        db.collection("DB").document("User").collection(user.getUid()).document("Friend").collection("Uid").document(uid).set(userFriend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {

                        UserFriend userFriend = new UserFriend(user.getUid());
                        db.collection("DB").document("User").collection(uid).document("Friend").collection("Uid").document(user.getUid()).set(userFriend)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void avoid) {
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
    }

    //목록 삭제
    public void deletList(String uid){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("DB").document("User").collection(user.getUid()).document("AS_Friend").collection("Uid").document(uid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("DB").document("User").collection(uid).document("RQ_Friend").collection("Uid").document(user.getUid())
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
    }





}

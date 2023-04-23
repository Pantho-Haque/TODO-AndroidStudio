package com.pantho.todo;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Model>list;

    public MyAdapter(Context context, ArrayList<Model> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(context).inflate(R.layout.retrived_layout,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
        Model ts =list.get(position);
        holder.task.setText(ts.getTask());
        holder.desc.setText(ts.getDes());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // creating view
                View myView = LayoutInflater.from(context).inflate(R.layout.activity_update, null);
                final AlertDialog dialog = new AlertDialog.Builder(context).setView(myView).create();
                dialog.setCancelable(false);

                EditText upTask= myView.findViewById(R.id.updateTask),
                        upDes= myView.findViewById(R.id.updateDes);
                Button upBtn= myView.findViewById(R.id.updateBtn),
                        canBtn=myView.findViewById(R.id.canBtn),
                        delBtn= myView.findViewById(R.id.deleteBtn);

                upTask.setText(ts.getTask());
                upDes.setText(ts.getDes());


                FirebaseAuth myAuth = FirebaseAuth.getInstance();
                String onlineUserId = myAuth.getCurrentUser().getUid();
                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Tasks").child(onlineUserId);

                String id=ts.getId();

                dialog.show();

                upBtn.setOnClickListener(v->{
                    Map<String,Object> map=new HashMap<>();
                    map.put("task",upTask.getText().toString().trim());
                    map.put("des",upDes.getText().toString().trim());
                    map.put("id",id);

                    ProgressDialog loader=new ProgressDialog(context);
                    loader.setMessage("Updating The task...");
                    loader.setCanceledOnTouchOutside(true);
                    loader.show();

                    reference.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Task has been Updated successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                String err = task.getException().toString();
                                Toast.makeText(context, "Failed: " + err, Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                    dialog.dismiss();
                });

                canBtn.setOnClickListener(v->{
                    dialog.dismiss();
                });

                delBtn.setOnClickListener(v->{
                    Map<String,Object> map=new HashMap<>();

                    ProgressDialog loader=new ProgressDialog(context);
                    loader.setMessage("Deleting The task...");
                    loader.setCanceledOnTouchOutside(true);
                    loader.show();
                    reference.child(id).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Task has been Deleted successfully", Toast.LENGTH_SHORT).show();

                            } else {
                                String err = task.getException().toString();
                                Toast.makeText(context, "Failed: " + err, Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                    dialog.dismiss();
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView task, desc;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            task=itemView.findViewById(R.id.taskHeadView);
            desc=itemView.findViewById(R.id.desView);
        }

    }


}
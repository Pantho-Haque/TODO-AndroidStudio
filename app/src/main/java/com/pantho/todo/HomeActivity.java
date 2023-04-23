package com.pantho.todo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;

    private ProgressDialog loader;

    private DatabaseReference reference;
    private FirebaseAuth myAuth;
    String key = "", task, description ,onlineUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        TimePicker inputTime =findViewById(R.id.time);
//
//        inputTime.setHour(5);
//        inputTime.setMinute(44);
//        inputTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
//            @Override
//            public void onTimeChanged(TimePicker view, int h, int m) {
//                selectedTime= h+" : "+m;
//                Toast.makeText(HomeActivity.this, selectedTime, Toast.LENGTH_SHORT).show();
//            }
//        });

        // toolbar
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todo List App");


        ArrayList<Model> list;
        list = new ArrayList<>();
        MyAdapter myAdapter=new MyAdapter(this,list);

        // modal
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(myAdapter);


        // Firebase
        myAuth = FirebaseAuth.getInstance();
        onlineUserId = myAuth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Tasks").child(onlineUserId);

        // read from database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    Model model=dataSnapshot.getValue(Model.class);
                    list.add(model);
                }
                class SortByHead implements Comparator<Model> {
                    // Used for sorting in ascending order of task head
                    public int compare(@NonNull Model a,@NonNull Model b)
                    {
                        return b.task.compareTo(a.task);
                    }
                }

                Collections.sort(list, new SortByHead());
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("failed", "Failed to read value.", error.toException());
            }
        });






        // fab clicked
        floatingActionButton = findViewById(R.id.addTask);
        loader=new ProgressDialog(this);

        floatingActionButton.setOnClickListener(view -> {

            // creating view
            View myView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.input_file, null);
            final AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this).setView(myView).create();
            dialog.setCancelable(false);

            final EditText task=myView.findViewById(R.id.taskHead),
                    description= myView.findViewById(R.id.taskDes);
            Button save=myView.findViewById(R.id.saveBtn),
                    cancel=myView.findViewById(R.id.cancelBtn);

            save.setOnClickListener((v)->{
                // prepare data to send to database
                    String mTaskHead=task.getText().toString().trim(),
                            mTaskDes=description.getText().toString().trim(),
                            id=reference.push().getKey();
//                                time=selectedTime;

                    if(TextUtils.isEmpty(mTaskHead)){
                            task.setError("Task Head Required");
                    }else if(TextUtils.isEmpty(mTaskDes)){
                        description.setError("Task Description Required");
                    }else{
                            loader.setMessage("Adding The task...");
                            loader.setCanceledOnTouchOutside(false);
                            loader.show();

                        Model data = new Model(mTaskHead, mTaskDes, id);
                        // data inserting to database
                        reference.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(HomeActivity.this, "Task has been added successfully", Toast.LENGTH_SHORT).show();

                                } else {
                                    String err = task.getException().toString();
                                    Toast.makeText(HomeActivity.this, "Failed: " + err, Toast.LENGTH_SHORT).show();
                                }
                                loader.dismiss();
                            }
                        });

                        dialog.dismiss();
                    }
            });

            cancel.setOnClickListener((v)->{
                dialog.dismiss();

            });

            dialog.show();
        });



    }


    // dealing with menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                myAuth.signOut();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
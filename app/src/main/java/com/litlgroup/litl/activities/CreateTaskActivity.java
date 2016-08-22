package com.litlgroup.litl.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.litlgroup.litl.R;
import com.litlgroup.litl.model.Task;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateTaskActivity extends AppCompatActivity {

    @BindView(R.id.etTitle)
    EditText etTitle;

    @BindView(R.id.etDescription)
    EditText etDescription;

    @BindView(R.id.tvDueDate)
    TextView tvDueDate;

    @BindView(R.id.spCategory)
    Spinner spCategory;

    @BindView(R.id.btnPostTask)
    Button btnPostTask;

    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.etPrice)
    EditText etPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        ButterKnife.bind(this);



    }


    @OnClick(R.id.btnPostTask)
    public void postTask()
    {
        try
        {

            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            String date = tvDueDate.getText().toString();
            String address = etAddress.getText().toString();
            String price = etPrice.getText().toString();
//            if(spCategory.getSelectedItem().toString() != null) {
//                String category = spCategory.getSelectedItem().toString();
//
////                writeNewTask(title, description, price, date, category);
//            }
            writeNewTask(title, description, price, date, "");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void writeNewTask(String title, String description, String price,
                              String date, String category)
    {
        try {
            DatabaseReference mDatabase =
                    FirebaseDatabase.getInstance().getReference();
            String key = mDatabase.child("Tasks").push().getKey();

            Task task = new Task(title, description, price, date, category);
            Map<String, Object> taskValues = task.toMap();

            Map<String, Object> childUpdates = new HashMap<>();

            //update the offers node
            childUpdates.put("/Tasks/" + key, taskValues);

            mDatabase.updateChildren(childUpdates);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }




}

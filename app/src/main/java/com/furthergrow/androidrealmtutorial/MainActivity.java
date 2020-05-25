package com.furthergrow.androidrealmtutorial;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Realm realm;
    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm=Realm.getDefaultInstance();
        Button insert=findViewById(R.id.insert);
        Button update=findViewById(R.id.update);
        Button read=findViewById(R.id.read);
        Button delete=findViewById(R.id.delete);
        output=findViewById(R.id.show_data);

        insert.setOnClickListener(this);
        update.setOnClickListener(this);
        read.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.insert){
            Log.d("Insert","Insert");
            ShowInsertDialog();
        }
        if(v.getId()==R.id.update){
            showUpdateDialog();
            Log.d("Insert","Insert");
        }
        if(v.getId()==R.id.delete){
            Log.d("Insert","Insert");
            ShowDeleteDialog();
        }
        if(v.getId()==R.id.read){
            showData();
            Log.d("Insert","Insert");
        }
    }

    private void showUpdateDialog() {
        final AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);

        final EditText data_id=view.findViewById(R.id.data_id);
        Button delete=view.findViewById(R.id.delete);
        final AlertDialog alertDialog=al.show();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                long id= Long.parseLong(data_id.getText().toString());
                final DataModel dataModel=realm.where(DataModel.class).equalTo("id",id).findFirst();
                ShowUpdateDialog(dataModel);
            }
        });
    }

    private void ShowDeleteDialog() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);

        final EditText data_id=view.findViewById(R.id.data_id);
        Button delete=view.findViewById(R.id.delete);
        final AlertDialog alertDialog=al.show();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id= Long.parseLong(data_id.getText().toString());
                final DataModel dataModel=realm.where(DataModel.class).equalTo("id",id).findFirst();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        alertDialog.dismiss();
                        dataModel.deleteFromRealm();
                    }
                });
            }
        });
    }

    private void ShowInsertDialog() {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.data_input_dialog,null);
        al.setView(view);

        final EditText name=view.findViewById(R.id.name);
        final EditText age=view.findViewById(R.id.age);
        final Spinner gender=view.findViewById(R.id.gender);
        Button save=view.findViewById(R.id.save);
        final AlertDialog alertDialog=al.show();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                final DataModel dataModel=new DataModel();

                Number current_id=realm.where(DataModel.class).max("id");
                long nextId;
                if(current_id==null){
                    nextId=1;
                }
                else{
                    nextId=current_id.intValue()+1;
                }

                dataModel.setId(nextId);
                dataModel.setAge(Integer.parseInt(age.getText().toString()));
                dataModel.setName(name.getText().toString());
                dataModel.setGender(gender.getSelectedItem().toString());

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(dataModel);
                    }
                });
            }
        });

    }

    private void ShowUpdateDialog(final DataModel dataModel) {
        AlertDialog.Builder al=new AlertDialog.Builder(MainActivity.this);
        View view=getLayoutInflater().inflate(R.layout.data_input_dialog,null);
        al.setView(view);

        final EditText name=view.findViewById(R.id.name);
        final EditText age=view.findViewById(R.id.age);
        final Spinner gender=view.findViewById(R.id.gender);
        Button save=view.findViewById(R.id.save);
        final AlertDialog alertDialog=al.show();

        name.setText(dataModel.getName());
        age.setText(""+dataModel.getAge());
        if(dataModel.getGender().equalsIgnoreCase("Male")) {
            gender.setSelection(0);
        }
        else{
            gender.setSelection(1);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();


                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        dataModel.setAge(Integer.parseInt(age.getText().toString()));
                        dataModel.setName(name.getText().toString());
                        dataModel.setGender(gender.getSelectedItem().toString());
                        realm.copyToRealmOrUpdate(dataModel);
                    }
                });
            }
        });

    }


    private void showData(){
        List<DataModel> dataModels=realm.where(DataModel.class).findAll();
        output.setText("");
        for(int i=0;i<dataModels.size();i++){
            output.append("ID : "+dataModels.get(i).getId()+" Name : "+dataModels.get(i).getName()+" Age : "+dataModels.get(i).getAge()+" Gender : "+dataModels.get(i).getGender()+" \n");
        }
    }
}

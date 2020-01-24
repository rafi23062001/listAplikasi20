package com.rafi.training.listaplikasibaru;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listitem;
    FloatingActionButton fab;
    EditText edttodo;

    ArrayList<String> data = new ArrayList<String>();

    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //CreateTodos();
        loadDataSharedPreference();

        listitem=findViewById(R.id.list_item);
        arrayAdapter=new ArrayAdapter<String>(this,R.layout.content,R.id.mobile, data);
        listitem.setAdapter(arrayAdapter);

        fab=findViewById(R.id.fab_todolist);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFabAdd();
            }
        });
        //7.1 membuat onItemLongClickListener di list view untuk menghapus data
        listitem.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                deleteItem(position);
                deleteFromSP(position);
                return true;
            }
        });

        listitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogEdit(position);
            }
        });
        listitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showDialogOption(position);
            }
        });
    }

    private void CreateTodos(){
        data.add("mobile legends");
        data.add("free fire");
        data.add("PUBG");
        data.add("pes 2010");
    }

    private void onClickFabAdd(){
        View view=View.inflate(this,R.layout.dialog_add_view, null);
        edttodo=view.findViewById(R.id.edt_todo);
        edttodo.setError("Tidak boleh kosong");

        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Mau ngapain lagi ?");
        dialog.setView(view);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                int newKey=data.size();
                String item = edttodo.getText().toString();
                data.add(item);
                arrayAdapter.notifyDataSetChanged();

                addToSP(newKey,item);

                Toast.makeText(getApplicationContext(),String.valueOf(newKey),Toast.LENGTH_LONG).show();
            }
        });
        dialog.setNegativeButton("Cancel", null);
        dialog.create();
        dialog.show();
    }
    //
    private void deleteItem(final int position){
        final int index = position;

        AlertDialog.Builder delete = new AlertDialog.Builder(this);
        delete.setTitle("Are you sure you want to delete it ?");
        delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                data.remove(position);
                reGenerateSortSP();
                arrayAdapter.notifyDataSetChanged();
            }
        });
        delete.setNegativeButton("No",null);
        delete.create().show();
    }

    private void addToSP(int key, String item){
        String newKey = String.valueOf(key);
        SharedPreferences todopref = getSharedPreferences("todoplayer",MODE_PRIVATE);
        SharedPreferences.Editor todosprefEditor = todopref.edit();
        todosprefEditor.putString(newKey,item);
        todosprefEditor.apply();
    }

    private void loadDataSharedPreference(){
        SharedPreferences todoplayer = getSharedPreferences("todoplayer",MODE_PRIVATE);

        if (todoplayer.getAll().size() > 0){
            for (int i = 0; i < todoplayer.getAll().size(); i++){
                String key = String.valueOf(i);
                String item = todoplayer.getString(key,null);
                data.add(item);
            }
        }
    }
    private void deleteFromSP(int position){
        String key = String.valueOf(position);
        SharedPreferences todopref = getSharedPreferences("todoplayer",MODE_PRIVATE);
        SharedPreferences.Editor todosprefEditor = todopref.edit();
        todosprefEditor.remove(key);
        todosprefEditor.apply();
    }
    private void reGenerateSortSP(){
        SharedPreferences todopref = getSharedPreferences("todoplayer",MODE_PRIVATE);
        SharedPreferences.Editor todosprefEditor = todopref.edit();
        todosprefEditor.clear();
        todosprefEditor.apply();

        for (int i = 0; i < data.size();i++){
            String key = String.valueOf(i);
            todosprefEditor.putString(key,data.get(i));
        }
        todosprefEditor.apply();
    }

    private void showDialogEdit(final int position){
        View view = View.inflate(this,R.layout.dialog_add_view,null);
        edttodo=view.findViewById(R.id.edt_todo);
        edttodo.setText(arrayAdapter.getItem(position));

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("What do you want to change ?");
        dialog.setView(view);
        dialog.setPositiveButton("Change it", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editItem(position,edttodo.getText().toString());
            }
        });
        dialog.setNegativeButton("cancel",null);
        dialog.create();
        dialog.show();
    }

    private void editItem(int position, String newItem){
        data.set(position,newItem);
        reGenerateSortSP();
        arrayAdapter.notifyDataSetChanged();
    }
    private void showDialogOption(int pos){
        final int position = pos;
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("What are you trying to do ?");
        dialog.setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showDialogEdit(position);
            }
        });
        dialog.setNeutralButton("cancel",null);
        dialog.setNegativeButton("delete",null);
        dialog.setNegativeButton("delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
            }
        });
        dialog.create();
        dialog.show();
    }
}

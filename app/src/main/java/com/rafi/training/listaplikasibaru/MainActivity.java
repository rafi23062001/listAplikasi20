package com.rafi.training.listaplikasibaru;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    ListView listitem;
    FloatingActionButton fab;
    EditText edttodo;
    ArrayList<String>getData = new ArrayList<String>();

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

        listitem.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listitem.setMultiChoiceModeListener(multiplayer);

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
        edttodo.setError("Tidak boleh kosong !");
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Mau ngapain lagi ?");
        dialog.setMessage("Isi dengan bener");
        dialog.setView(view);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (TextUtils.isEmpty(edttodo.getText())){
                    edttodo.setError("Tidak boleh kosong");
                    Toast.makeText(getApplicationContext(), "Gaboleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }
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

    public void removeSelectedItem(List<String> items){
        for (String item: items){
            data.remove(item);
            reGenerateSortSP();
        }
        arrayAdapter.notifyDataSetChanged();
    }
    AbsListView.MultiChoiceModeListener multiplayer = new AbsListView.MultiChoiceModeListener() {
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (getData.contains(data.get(position))){
                getData.remove(data.get(position));
                listitem.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
            }
            else {
                getData.add(data.get(position));
                listitem.getChildAt(position).setBackgroundColor(Color.parseColor("#FFF753"));
            }
            mode.setTitle(data.size()+"Kegiatan Terpilih");
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.menubaru_layout, menu);
            fab.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            int id =item.getItemId();
            if (id == R.id.item_hapus){
                AlertDialog.Builder builderdeleteterpilih = new AlertDialog.Builder(MainActivity.this);
                builderdeleteterpilih.setTitle("Hapus Kegiatan Terpilih");
                builderdeleteterpilih.setMessage("lu beneran pengen hapus " + data.size() + "Kegiatan yang terpilih ? :(");
                builderdeleteterpilih.setPositiveButton("yes i do", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listitem.setBackgroundColor(Color.TRANSPARENT);
                        Toast.makeText(getApplicationContext(),data.size()+"Kegiatan terpilih berhasil dihapus :(", Toast.LENGTH_SHORT).show();
                        removeSelectedItem(getData);
                        mode.finish();
                    }
                });
                builderdeleteterpilih.setNegativeButton("Gajadi deh",null);
                builderdeleteterpilih.create();
                builderdeleteterpilih.show();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            listitem.setAdapter(arrayAdapter);
            getData.clear();
            fab.setEnabled(true);
        }
    };
}

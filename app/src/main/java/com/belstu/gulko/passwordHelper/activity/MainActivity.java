package com.belstu.gulko.passwordHelper.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.belstu.gulko.passwordHelper.APIService;
import com.belstu.gulko.passwordHelper.Client;
import com.belstu.gulko.passwordHelper.R;
import com.belstu.gulko.passwordHelper.adapter.PasswordRecyclerViewAdapter;
import com.belstu.gulko.passwordHelper.database.PasswordDatabase;
import com.belstu.gulko.passwordHelper.model.Base;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String SHARED_PREFS_NAME="MyPrefs";
    private RecyclerView recyclerView;
    TextView emptyText;
    PasswordRecyclerViewAdapter adapter;
    List<String> collection;
    List<String> collection2;
    List<String> myListAcc=new ArrayList<String>();
    List<String> myListLgn=new ArrayList<String>();
    PasswordDatabase passwordDatabase;
    PasswordDatabase loginDatabase;
    AdapterView.AdapterContextMenuInfo info;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordDatabase = new PasswordDatabase(getApplicationContext());
        loginDatabase = new PasswordDatabase(getApplicationContext());
        myListAcc = getArrayAcc();
        myListLgn = getArrayLgn();
        collection = new ArrayList<>();
        collection2 = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.listViewID);
        emptyText = (TextView)findViewById(R.id.text2);
        adapter = new PasswordRecyclerViewAdapter(this, myListAcc, myListLgn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        registerForContextMenu(recyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFloatingAdditionButton();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void onClickFloatingAdditionButton() {
        final AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
        LayoutInflater inflater=this.getLayoutInflater();
        final View dialogView=inflater.inflate(R.layout.custom_dialog,null);
        dialogBuilder.setView(dialogView);
        final EditText acnt=(EditText)dialogView.findViewById(R.id.dialogEditAccID);

        final EditText lgn=(EditText)dialogView.findViewById(R.id.dialogEditLgnID);
        final EditText pass=(EditText)dialogView.findViewById(R.id.dialogEditPassID);
        dialogBuilder.setIcon(R.mipmap.icon);
        dialogBuilder.setTitle(R.string.main_acnt_info);
        dialogBuilder.setPositiveButton(R.string.main_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                collection=passwordDatabase.getAcc();
                collection2=passwordDatabase.getLogins();
                if(!isDuplicate(collection,acnt.getText().toString())){
                    if(!TextUtils.isEmpty(acnt.getText().toString())){
                        myListAcc.add(acnt.getText().toString());
                        myListLgn.add(lgn.getText().toString());
                        adapter.notifyDataSetChanged();
                        passwordDatabase.addCredentials(getApplicationContext(),acnt.getText().toString(),lgn.getText().toString(),pass.getText().toString());
                        Toast.makeText(getApplicationContext(),
                                "Added "+acnt.getText().toString(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),
                                "sf "+SplashActivity.sh.getString("email", null), Toast.LENGTH_SHORT).show();

                        Base base = new Base(SplashActivity.sh.getString("email", null).toString(), acnt.getText().toString(), lgn.getText().toString(), pass.getText().toString());

                        APIService service = Client.getService();
                        Call<Void> call = service.saveBase(base);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Saved on the server", Toast.LENGTH_SHORT).show();
                                }
                                else Toast.makeText(MainActivity.this, "Error saved on the server", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Not connected to the server", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        Toast.makeText(getApplicationContext(),
                                R.string.main_empty_field,Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            R.string.main_duplicate,Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBuilder.setNegativeButton(R.string.main_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search_button:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

   /* public boolean saveArray(){
        SharedPreferences sp=this.getSharedPreferences(SHARED_PREFS_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        Set<String> set=new HashSet<String>();
        set.addAll(myListAcc);
        editor.putStringSet("list",set);
        return editor.commit();
    }*/

    public List<String> getArrayAcc(){
        List accounts=passwordDatabase.getAcc();
        return accounts;
    }

    public List<String> getArrayLgn(){
        List logins=passwordDatabase.getLogins();
        return logins;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveArray();
    }

    @Override
    protected void onStop() {
       // saveArray();
        super.onStop();
    }

    public boolean isDuplicate(List<String> col,String value){
        boolean isDuplicate=false;
        for(String s:col){
            if(s.equals(value)){
                isDuplicate=true;
                break;
            }
        }
        return isDuplicate;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(MainActivity.this,
                    SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        s=myListAcc.get(info.position);
        if(item.getItemId()==R.id.deletecontext){
            AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(this);
            LayoutInflater inflater=this.getLayoutInflater();
            final View dialogView=inflater.inflate(R.layout.confirm_delete,null);
            dialogBuilder.setView(dialogView);
            final EditText pass1=(EditText)dialogView.findViewById(R.id.passDialog);
            dialogBuilder.setTitle("Are you sure you want to delete "+s);
            dialogBuilder.setIcon(R.mipmap.icon);
            dialogBuilder.setPositiveButton(R.string.main_confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(pass1.getText().toString().equals(SplashActivity.sh.getString("password",null))){
                        Toast.makeText(getApplicationContext(),
                                "Deleted "+s,Toast.LENGTH_SHORT).show();
                        passwordDatabase.deleteRow(String.valueOf(info.position));
                        myListAcc.remove(info.position);
                        adapter.notifyDataSetChanged();

                    }
                    else{
                        Toast.makeText(getApplicationContext(),R.string.main_wrong_pass,Toast.LENGTH_LONG);
                    }
                }
            });

            dialogBuilder.setNegativeButton(R.string.main_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog b=dialogBuilder.create();
            b.show();
            return true;
        }
        else
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_logout: {

                Toast.makeText(getApplicationContext(),
                        R.string.main_logout, Toast.LENGTH_LONG).show();
                SplashActivity.editor.remove("loginTest");
                SplashActivity.editor.commit();
                Intent sendToLoginAndRegistration = new Intent(getApplicationContext(),
                        LoginActivity.class);

                String vibratorService = Context.VIBRATOR_SERVICE;
                Vibrator vibrator = (Vibrator)getSystemService(vibratorService);
                long[] pattern = {200, 2000, 4000, 8000, 16000 }; vibrator.vibrate(pattern, 0);
                vibrator.vibrate(200);
                //vibrator.cancel();

                startActivity(sendToLoginAndRegistration);
                overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
            }
            case R.id.nav_about_us: {
                Intent sendToAboutUs = new Intent(getApplicationContext(), AboutUsActivity.class);
                startActivity(sendToAboutUs);
                overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
            }
            default: {
                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}





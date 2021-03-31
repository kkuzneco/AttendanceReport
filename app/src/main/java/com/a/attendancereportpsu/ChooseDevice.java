package com.a.attendancereportpsu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;

import static android.os.SystemClock.sleep;

public class ChooseDevice extends AppCompatActivity {
    ListView lv;
    ArrayList<String> devices;
    ArrayList<String> macs;
    DatabaseHelper dbHelper;
    ProgressBar pb;
    ArrayAdapter<String> adapter;
    DevicesAdapter da;
    SQLiteDatabase db;
    TextView tv;
    private BluetoothAdapter bluetoothAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_device);
        lv = (ListView) findViewById(R.id.devices);
        devices = new ArrayList<>();
        pb = (ProgressBar) findViewById(R.id.pb);
        tv = (TextView) findViewById(R.id.noDevicesFound);
        dbHelper = new DatabaseHelper(this);
        da = new DevicesAdapter();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        db = dbHelper.getWritableDatabase();
        macs = new ArrayList<>();
        if (bluetoothAdapter != null) {
            Log.d("myBluetooth", "Bluetooth OK");
        }
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, 1);
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d("myBluetooth", "стоп поиск1");

        }
        tv.setVisibility(View.INVISIBLE);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled) {
            startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 2);
        }

        boolean f = bluetoothAdapter.startDiscovery();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mReceiver, filter);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_expandable_list_item_1, devices);
        lv.setAdapter(adapter);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ChooseDevice.this, RegistrationOfDevice.class);
                Bundle bundle = new Bundle();
                if((!macs.get(position).equals(null))&&(!devices.get(position).equals(null))) {
                    bundle.putString("device_mac", macs.get(position));
                    bundle.putString("device_name", devices.get(position));
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finishActivity(2);
                    finish();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(mReceiver);
        Log.d("myBluetooth", "стоп поиск2");
        super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu3) {
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu3);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_update_device://если выбрано "Обновить список"
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                dbHelper.removeMACRows(db);
                devices.clear();
                macs.clear();

                ArrayAdapter<String> adapter = new ArrayAdapter(this,
                        android.R.layout.simple_expandable_list_item_1, devices);
                lv.setAdapter(adapter);
                pb.setVisibility(View.VISIBLE);
                bluetoothAdapter.startDiscovery();
                 return true;

        }
        return false;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            Log.d("myBluetooth", "receive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    String name = device.getName();
                    String mac = device.getAddress();
                    formList(name, mac);
                }

            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                pb.setVisibility(View.VISIBLE);
                tv.setVisibility(View.INVISIBLE);
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                pb.setVisibility(View.INVISIBLE);
                if(devices.size()==0) tv.setVisibility(View.VISIBLE);
            }

        }


    };



    public void formList(String name, String mac){

        Log.d("myBluetooth", "gettingDevice " +name);
        if((!name.equals(null))&&(!mac.equals(null))) {
            if(!macs.contains(mac)) {
                devices.add(name);
                macs.add(devices.lastIndexOf(name), mac);
                ArrayAdapter<String> adapter = new ArrayAdapter(this,
                        android.R.layout.simple_expandable_list_item_1, devices);
                lv.setAdapter(adapter);
            }
        }

    }
}
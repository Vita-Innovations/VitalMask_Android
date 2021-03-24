package com.example.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.Menu;
import android.content.Intent;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myapplication.ui.chart.ChartFragment;
import com.example.myapplication.ui.device.DeviceFragment;
import com.example.myapplication.ui.device.DeviceViewModel;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.myapplication.ui.history.HistoryFragment;
import com.example.myapplication.ui.home.HomeFragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static android.R.layout.simple_list_item_1;

public class DrawerApp extends AppCompatActivity implements DeviceFragment.Listener,
        ChartFragment.Listener, HomeFragment.Listener, HistoryFragment.Listener{
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private CheckBox mLED1;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private AppBarConfiguration mAppBarConfiguration;
    NavController navController;

    BarChart barChart;
    BarDataSet dayDataSet;
    BarDataSet weekDataSet;
    BarDataSet monthDataSet;
    BarDataSet yearDataSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_chart, R.id.nav_history, R.id.nav_device, R.id.nav_settings,
                R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_appbar_menu);


        // Creating Data Sets
        ArrayList<BarEntry> dayEntries = new ArrayList<>();
        dayEntries.add(new BarEntry(1, 1));
        dayEntries.add(new BarEntry(2, 3));
        dayEntries.add(new BarEntry(3, 2));
        dayEntries.add(new BarEntry(4, 4));
        dayEntries.add(new BarEntry(5, 3));
        dayEntries.add(new BarEntry(6, 5));
        dayEntries.add(new BarEntry(7, 4));
        dayEntries.add(new BarEntry(8, 6));
        dayEntries.add(new BarEntry(9, 5));
        dayEntries.add(new BarEntry(10, 7));
        dayEntries.add(new BarEntry(11, 6));
        dayEntries.add(new BarEntry(12, 8));
        dayDataSet = new BarDataSet(dayEntries, "Dates");

        ArrayList<BarEntry> weekEntries = new ArrayList<>();
        weekEntries.add(new BarEntry(1, 1));
        weekEntries.add(new BarEntry(2, 2));
        weekEntries.add(new BarEntry(3, 3));
        weekEntries.add(new BarEntry(4, 4));
        weekEntries.add(new BarEntry(5, 5));
        weekEntries.add(new BarEntry(6, 6));
        weekEntries.add(new BarEntry(7, 7));
        weekDataSet = new BarDataSet(weekEntries, "Dates");

        ArrayList<BarEntry> monthEntries = new ArrayList<>();
        monthEntries.add(new BarEntry(1, 4));
        monthEntries.add(new BarEntry(2, 1));
        monthEntries.add(new BarEntry(3, 3));
        monthEntries.add(new BarEntry(4, 2));
        /**Change*/

        monthDataSet = new BarDataSet(monthEntries, "Dates");

        ArrayList<BarEntry> yearEntries = new ArrayList<>();
        yearEntries.add(new BarEntry(1, 1));
        yearEntries.add(new BarEntry(2, 2));
        yearEntries.add(new BarEntry(3, 3));
        yearEntries.add(new BarEntry(4, 4));
        yearEntries.add(new BarEntry(5, 5));
        yearEntries.add(new BarEntry(6, 6));
        yearEntries.add(new BarEntry(7, 6));
        yearEntries.add(new BarEntry(8, 5));
        yearEntries.add(new BarEntry(9, 4));
        yearEntries.add(new BarEntry(10, 3));
        yearEntries.add(new BarEntry(11, 2));
        yearEntries.add(new BarEntry(12, 1));
        yearDataSet = new BarDataSet(yearEntries, "Dates");


    }

    @Override
    public void onBackPressed(){
        navController.navigate(R.id.nav_home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.nameView);
        textView.setText(message);
        return true;
    }

    public void onHomeView() {
        Button chartButton = findViewById(R.id.button2);
        Button historyButton = findViewById(R.id.button3);
        Button deviceButton = findViewById(R.id.button5);
        Button settingsButton = findViewById(R.id.button6);
        Button profileButton = findViewById(R.id.button8);
        final ToggleButton toggleSideButton = findViewById(R.id.toggleSpineSide);
        final ToggleButton toggleBackButton = findViewById(R.id.toggleSpineBack);
        toggleBackButton.setChecked(false);
        toggleSideButton.setChecked(true);
        final ImageView sideImage = findViewById(R.id.spine_side);
        final ImageView backImage = findViewById(R.id.spine_back);
        chartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_chart);
            }
        });
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_history);
            }
        });
        deviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_device);
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_settings);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_profile);
            }
        });
        final Drawable buttonDrawablePrimary = DrawableCompat.wrap(toggleSideButton.getBackground());
        DrawableCompat.setTint(buttonDrawablePrimary, R.attr.colorPrimary);
        final Drawable buttonDrawablePrimaryDark = DrawableCompat.wrap(toggleSideButton.getBackground());
        DrawableCompat.setTint(buttonDrawablePrimaryDark, R.attr.colorPrimaryDark);
        toggleSideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backImage.setVisibility(View.GONE);
                sideImage.setVisibility(View.VISIBLE);
                toggleSideButton.setChecked(true);
                toggleBackButton.setChecked(false);
//                toggleBackButton.setBackground(buttonDrawablePrimary);
//                toggleSideButton.setBackground(buttonDrawablePrimaryDark);
            }
        });
        toggleBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sideImage.setVisibility(View.GONE);
                backImage.setVisibility(View.VISIBLE);
                toggleBackButton.setChecked(true);
                toggleSideButton.setChecked(false);
//                toggleSideButton.setBackground(buttonDrawablePrimary);
//                toggleBackButton.setBackground(buttonDrawablePrimaryDark);
            }
        });

    }

    public void onHistoryView(){
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                navController.navigate(R.id.nav_chart);
            }
        });
    }

    public void onChartView() {
/*
    So what I'm envisioning is the history tab with the calendar will be the menu of which day to
    display on the chart tab. You see I've already put in two of the buttons in the chart tab that
    are pretty self explanatory. That way we don't need to share data between the two tabs beyond
    the particular date the user selects on the history tab, the arguments of which can be seen
    above in the setOnDateChangeListener in onHistoryView. We need some way of storing arrays that
    contain the pertinent information for each day, month, and year if we choose to do this.

 */
        Button historyButton = findViewById(R.id.button7);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.nav_history);
            }
        });

        // Initialize
        barChart = (BarChart) findViewById(R.id.bargraph);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // Data Sets
        final ArrayList<IBarDataSet> dayDataSets = new ArrayList<IBarDataSet>();
        dayDataSets.add(dayDataSet);
        final ArrayList<IBarDataSet> weekDataSets = new ArrayList<IBarDataSet>();
        weekDataSets.add(weekDataSet);
        final ArrayList<IBarDataSet> monthDataSets = new ArrayList<IBarDataSet>();
        monthDataSets.add(monthDataSet);
        final ArrayList<IBarDataSet> yearDataSets = new ArrayList<IBarDataSet>();
        yearDataSets.add(yearDataSet);
        
        
        BarData theData = new BarData(weekDataSets);
        barChart.setData(theData);



        //Remove background grid
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        // Remove legend, right axis labels, and description
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setDrawLabels(false);

        // Creating day labels
        final ArrayList<String> dayLabels = new ArrayList<>();
        dayLabels.add("2");
        dayLabels.add("4");
        dayLabels.add("6");
        dayLabels.add("8");
        dayLabels.add("10");
        dayLabels.add("12");
        dayLabels.add("14");
        dayLabels.add("16");
        dayLabels.add("18");
        dayLabels.add("20");
        dayLabels.add("22");
        dayLabels.add("24");
        dayLabels.add("24");

        // Creating day labels
        final ArrayList<String> weekLabels = new ArrayList<>();
        weekLabels.add("M");
        weekLabels.add("T");
        weekLabels.add("W");
        weekLabels.add("Th");
        weekLabels.add("F");
        weekLabels.add("S");
        weekLabels.add("Su");
        weekLabels.add("Su");

        // Creating month labels
        final ArrayList<String> monthLabels = new ArrayList<>();
        monthLabels.add("First");
        monthLabels.add("Second");
        monthLabels.add("Third");
        monthLabels.add("Fourth");
        monthLabels.add("Fourth");


        // Creating year labels
        final ArrayList<String> yearLabels = new ArrayList<>();
        yearLabels.add("Jan");
        yearLabels.add("Feb");
        yearLabels.add("Mar");
        yearLabels.add("Apr");
        yearLabels.add("May");
        yearLabels.add("Jun");
        yearLabels.add("Jul");
        yearLabels.add("Aug");
        yearLabels.add("Sep");
        yearLabels.add("Oct");
        yearLabels.add("Nov");
        yearLabels.add("Dec");
        yearLabels.add("Dec");

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                return weekLabels.get((int)v - 1);
            }
        });

        // Buttons
        Button chartDayButton = findViewById(R.id.chartDay);
        Button chartWeekButton = findViewById(R.id.chartWeek);
        Button chartMonthButton = findViewById(R.id.chartMonth);
        Button chartYearButton = findViewById(R.id.chartYear);
        chartDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float v) {
                        System.out.print(v);
                        return dayLabels.get((int)v - 1);
                    }
                });
                BarData dayData = new BarData(dayDataSets);
                barChart.setData(dayData);
                barChart.getXAxis().setLabelCount(4);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }
        });
        chartWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float v) {
                        System.out.print(v);
                        return weekLabels.get((int)v - 1);
                    }
                });
                BarData weekData = new BarData(weekDataSets);               
                barChart.getXAxis().setLabelCount(4);
                barChart.setData(weekData);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }
        });
        chartMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float v) {
                        System.out.print(v);
                        return weekLabels.get((int)v - 1);
                    }
                });
                BarData monthData = new BarData(monthDataSets);
                barChart.setData(monthData);
                barChart.getXAxis().setLabelCount(4);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }
        });
        chartYearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float v) {
                        System.out.print(v);
                        return yearLabels.get((int)v - 1);
                    }
                });
                BarData yearData = new BarData(yearDataSets);
                barChart.getXAxis().setLabelCount(4);
                barChart.setData(yearData);
                barChart.notifyDataSetChanged();
                barChart.invalidate();
            }
        });

    }


    //@Override
    public void onDeviceView() {
        mBluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) findViewById(R.id.readBuffer);
        mScanBtn = (Button) findViewById(R.id.scan);
        mOffBtn = (Button) findViewById(R.id.off);
        mDiscoverBtn = (Button) findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button) findViewById(R.id.PairedBtn);
        mLED1 = (CheckBox) findViewById(R.id.checkboxLED1);
        mBTArrayAdapter = new ArrayAdapter<String>(this, simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        mDevicesListView = (ListView) findViewById(R.id.devicesListView);
//        if (mDevicesListView == null) {
//            mDevicesListView = (ListView)getLayoutInflater().inflate(R.id.devicesListView, simple_list_item_1);
//        }
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }
                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String) (msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {
//            mLED1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mConnectedThread != null) //First check to make sure thread created
//                        if (mLED1.isChecked()) {
//                            mConnectedThread.write("1");
//                        } else {
//                            mConnectedThread.write("0");
//                        }
//                }
//            });


            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discover(v);
                }
            });
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            } else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
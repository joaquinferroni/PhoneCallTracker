package com.example.joaco.phonecalltracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PHONE_CALL = 1;
    private static final int REQUEST_CALL_LOG = 2;
    private static final int REQUEST_READ_PHONE = 3;
    private static final int REQUEST_READ_CALL_LOG = 4;
    private static final int REQUEST_PROCESS_OUTGOING_CALLS = 5;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    private String number;
    private TextView tv_phone_number;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar=(Toolbar)findViewById(R.id.my_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        number = getIntent().getStringExtra("phone");
        checkPermissionOverlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent service = new Intent(this, PhoneCallService.class);
        this.startService(service);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        number = getIntent().getStringExtra("phone");
        //Intent service = new Intent(this, PhoneCallsService.class);
        //this.stopService(service);
        checkPermissionOverlay();
        Intent service = new Intent(this, PhoneCallService.class);
        this.startService(service);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent service = new Intent(this, PhoneCallService.class);
        this.startService(service);
    }

    public void checkPermissionOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void checkPermissions() {
        boolean hasCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
        boolean hasWriteCallPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        boolean hasReadPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        boolean hasReadCallLogPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        boolean hasProcessOutgoingCallsPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED;


        if (!hasWriteCallPermission) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CALL_LOG}, REQUEST_CALL_LOG);
        } else if (!hasReadPhonePermission) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE);
        } else if (!hasReadCallLogPermission) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, REQUEST_READ_CALL_LOG);
        } else if (!hasProcessOutgoingCallsPermission) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, REQUEST_PROCESS_OUTGOING_CALLS);
        } else if (!hasCallPhonePermission) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
        }
    }

    public void goChat(View v) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Se redireccionará al chat Albert");
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();

    }

    public void makeCall(View v) {
        if (number != null && !number.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    setFlag();
                    startActivity(intent);
                    DeleteCallLogByNumber();
                }
            } else {
                setFlag();
                startActivity(intent);
                DeleteCallLogByNumber();
            }
        }
    }

    public void nextPage(View v) {
        String name=getResources().getResourceEntryName(v.getId());
        String service_name;
        switch (name){
            case "ll_service_1": service_name="SERVICIO 1";
                                break;
            case "ll_service_2": service_name="SERVICIO 2";
                break;
            case "ll_service_3": service_name="SERVICIO 3";
                break;
            case "ll_service_4": service_name="SERVICIO 4";
                break;
            default:service_name="SERVICIO";
                    break;
        }
        Intent intent=new Intent();
        intent.setClass(this,Services.class);
        intent.putExtra("service", service_name);
        this.startActivity(intent);
    }

    public void exit(View v) {
        this.finish();
    }

    private void setFlag() {
        SharedPreferences.Editor editor = this.getSharedPreferences("preferences", MODE_PRIVATE).edit();
        editor.putBoolean("flag", true);
        editor.apply();
    }

    public void DeleteCallLogByNumber() {
        String queryString = CallLog.Calls.NUMBER + " LIKE '" + number + "'";
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_CALL_LOG}, REQUEST_CALL_LOG);
            } else {
                this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
            }
        } else {
            this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, queryString, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // makeCall(null);
                    checkPermissions();
                }

                return;
            }
            case REQUEST_CALL_LOG: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //DeleteCallLogByNumber();
                    checkPermissions();
                }
                return;
            }
            case REQUEST_READ_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //DeleteCallLogByNumber();
                    checkPermissions();
                }
                return;
            }
            case REQUEST_READ_CALL_LOG: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //DeleteCallLogByNumber();
                    checkPermissions();
                }
                return;
            }
            case REQUEST_PROCESS_OUTGOING_CALLS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //DeleteCallLogByNumber();
                    checkPermissions();
                }
                return;
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            checkPermissions();

        }

    }
}

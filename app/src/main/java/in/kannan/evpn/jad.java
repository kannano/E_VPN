package in.kannan.evpn;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.IpPrefix;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.VpnService;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class jad extends AppCompatActivity {

    ActivityResultLauncher<Intent> mPrepare = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "VPN Prepare Success", Toast.LENGTH_LONG).show();
            }

        }
    });

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vpn);

        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        @SuppressLint("DefaultLocale")
        String ipAddress = String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
        TextView ip_address = findViewById(R.id.ip_addres);
        ip_address.setText("IP : " + ipAddress);


        LinearLayout connect_btn_container = findViewById(R.id.connect_btn_container);

        connect_btn_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_LONG).show();
                Intent prepare_vpn = VpnService.prepare(getApplicationContext());
                if (prepare_vpn != null) {
                    mPrepare.launch(prepare_vpn);
                } else {
                    onActivityResult(0, 401, null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 401) {
            Intent vpn_Start_Intent = new Intent(getApplicationContext(), evpnservice.class);
            startService(vpn_Start_Intent);
            TextView status = findViewById(R.id.status);
            status.setText("Status: " + "Connected");
            TextView btn_text_connected = findViewById(R.id.btn_text_connected);
            btn_text_connected.setText("Tap to Disconnect");
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                     en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            TextView ip_address = findViewById(R.id.ip_addres);
                            ip_address.setText("IP : " + inetAddress.toString().replace("/",""));
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }
}

package in.kannan.evpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import android.net.Ikev2VpnProfile;

public class evpnservice extends VpnService{
    private Thread mThread;
    private ParcelFileDescriptor mInterface;
    Builder builder = new Builder();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    builder.setSession("MyVPNService").addAddress("141.95.37.119", 24)
                            .establish();
                    FileInputStream in = new FileInputStream(mInterface.getFileDescriptor());
                    FileOutputStream out = new FileOutputStream(mInterface.getFileDescriptor());
                    DatagramChannel tunnel = DatagramChannel.open();
                    tunnel.connect(new InetSocketAddress("ip119.ip-141-95-37.eu", 445));
                    protect(tunnel.socket());
                    while (true) {
                        Thread.sleep(100);
                    }
                }catch (Exception a) {
                    a.printStackTrace();
                } finally {
                    try {
                        if (mInterface != null) {
                            mInterface.close();
                            mInterface = null;
                        }
                    } catch (IOException e) {
                    }
                }
            }
        },"MyVpnRunnable");
        mThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

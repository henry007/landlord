package com.hurray.landlord.server;

import static com.hurray.landlord.server.ServerConstants.LOG_TAG;

import com.hurray.landlord.R;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.lordserver.protocol.message.MessageFormatException;
import com.hurray.lordserver.protocol.message.MessageList;
import com.hurray.lordserver.protocol.message.base.BaseMessage;
import com.hurray.lordserver.protocol.message.base.Request;
import com.hurray.lordserver.protocol.message.fake.TestRequest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yizhou He
 * 4/27/12 16:27
 */
public class MessageExampleActivity extends Activity {
    private MessageServerWrapper service;
    private MessageServiceConnection connection;
    ListView listView;
    private List<BaseMessage> messages = new ArrayList<BaseMessage>();
    MessageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_example);
        messages.clear();

        listView = (ListView) findViewById(R.id.listView);

        adapter = new MessageListAdapter(this);
        listView.setAdapter(adapter);

    }

    private MessageListener listener = new MessageListener() {


        @Override
        public void onReceive(MessageList list) {
            messages.addAll(list.getList());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onSentResult(boolean status, int[] serialNum) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    class MessageServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            //service = IMessageServer.Stub.asInterface(boundService);
            service = new MessageServerWrapper(boundService);
            LogUtil.d(LOG_TAG, "onServiceConnected() connected");
            Toast.makeText(MessageExampleActivity.this, "Service connected", Toast.LENGTH_LONG)
                    .show();
            findViewById(R.id.btnSend).setEnabled(true);
            try {
                service.addListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
            LogUtil.d(LOG_TAG, "onServiceDisconnected() disconnected");
            Toast.makeText(MessageExampleActivity.this, "Service connected", Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void doClear(View target) {
        messages.clear();
        adapter.notifyDataSetChanged();
    }

    public void doConnect(View target) {
        connection = new MessageServiceConnection();
        Intent i = new Intent();
        i.setClassName(this, MessageServerService.class.getName());
        boolean ret = bindService(i, connection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "Server add: " + ServerConstants.getServerUrl(), Toast.LENGTH_LONG).show();
        LogUtil.d(LOG_TAG, "initService() bound with " + ret);
    }

    public void doSend(View target) throws MessageFormatException, RemoteException {
        TestRequest req = new TestRequest();

        messages.add(req);
        service.sendMessage(req);
        adapter.notifyDataSetChanged();
    }

    public void doTestPage(View target) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(ServerConstants.getServerUrl()));
        Toast.makeText(this, ServerConstants.getServerUrl(), Toast.LENGTH_LONG).show();
        startActivity(i);
    }

    private class MessageListAdapter extends BaseAdapter {

        private Context context;

        private MessageListAdapter(Context context) {
            this.context = context;

        }

        @Override
        public int getCount() {
            return messages.size();
        }

        @Override
        public Object getItem(int i) {
            return messages.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.message_item, null);
            }

            TextView tv = (TextView) convertView.findViewById(R.id.textView);
            BaseMessage msg = messages.get(i);

            //tv.setText(MessageHelper.toString(msg));
            tv.setText(msg.toString());

            if (msg instanceof Request) {
                tv.setTextColor(Color.BLUE);
            } else {
                tv.setTextColor(Color.RED);
            }
            return convertView;
        }
    }
}

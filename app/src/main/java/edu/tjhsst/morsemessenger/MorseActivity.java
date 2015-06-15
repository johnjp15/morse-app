package edu.tjhsst.morsemessenger;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class MorseActivity extends ActionBarActivity {

    private final WebSocketConnection mConnection = new WebSocketConnection();
    static final String TAG = "de.tavendo.autobahn.echo";

    static Button dot;
    static Button dash;
    static TextView display;
    static Vibrator mVibrator;

    static String history;

    private void start() {
        final String uri = "ws://104.236.237.210:8000";
        Log.d(TAG, "Status: Starting");

        try {
            mConnection.connect(uri, new WebSocketHandler() {
                @Override
                public void onOpen() {
                    Log.d(TAG, "Status: Connected to " + uri);
                }

                @Override
                public void onTextMessage(String payload) {
                    if(payload.equals("dot")) {
                        display.setText(".");
                        updateHistory(".");
                        mVibrator.vibrate(100);
                    }
                    else {
                        display.setText("-");
                        updateHistory("-");
                        mVibrator.vibrate(250);
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(TAG, "Connection lost, " + code + ", " + reason);
                }
            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
    }

    private void updateHistory(String addition) {
        int histLength = history.length();

        history += (addition + " ");

        TextView msgHistoryTV = (TextView) findViewById(R.id.msgHistory_textview);

        msgHistoryTV.setText(history);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse);

        dot = (Button) findViewById(R.id.dot);
        dash = (Button) findViewById(R.id.dash);
        display = (TextView) findViewById(R.id.received);

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        history = "Session history: ";

        start();

        dot.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mConnection.sendTextMessage("dot");
//                updateHistory(".");
            }
        });
        dash.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                mConnection.sendTextMessage("dash");
//                updateHistory("-");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_morse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

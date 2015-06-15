package edu.tjhsst.morsemessenger;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


public class MorseActivity extends ActionBarActivity {

    private final WebSocketConnection mConnection = new WebSocketConnection();
    static final String TAG = "de.tavendo.autobahn.echo";

    static Button dot;
    static Button dash;
    static Button exportHistoryButton;
    static TextView display;
    static Vibrator mVibrator;

    static String history;

    static RelativeLayout fl;

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        //        public boolean onSingleTapConfirmed(MotionEvent e)   {
//            Log.e("", "SingleTapConfirmed detected.");
//            return false;
//        }
        //use for dots
        public boolean onSingleTapUp(MotionEvent e) {
//            Log.e("", "SingleTapUp detected."); //debug
            mConnection.sendTextMessage("dot");
            return false;
        }

        //use for dashes
        public void onLongPress(MotionEvent e) {
//            Log.e("", "LongPress detected."); //debug
            mConnection.sendTextMessage("dash");
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//            Log.e("", "Fling detected."); //debug
            mConnection.sendTextMessage("dash");
            return false;
        }
    });

    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    ;

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
                        mVibrator.vibrate(350);
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
        exportHistoryButton = (Button) findViewById(R.id.history_button);
        display = (TextView) findViewById(R.id.received);

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        history = "Session history: ";



        /*
        fl = (RelativeLayout)findViewById(R.id.layoutframe);
        fl.setOnTouchListener(new RelativeLayout.OnTouchListener() {
            final Handler handler = new Handler();
            Runnable mLongPressed = new Runnable() {
                public void run() {
                    Log.i("", "Long press!");
                }
            };

            public void onClick(View v) {
                Log.e("", "Click detected");
            }

            public void onLongPress(MotionEvent e) {
                Log.e("", "Longpress detected");
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //gesture detector to detect swipe.
//                gestureDetector.onTouchEvent(arg1);
                Log.e("", "Touch detected");
                return false;//always return true to consume event

            }
        });
        */

        start();
        exportHistoryButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
//                Calendar c = Calendar.getInstance();
//                Date d = c.getTime();

//                String dateAndTime = Calendar.get(Calendar.MONTH) + "/" + Calendar.get(Calendar.DAY_OF_WEEK) + "/" + Calendar.get(Calendar.YEAR) - 1900 + " " + Calendar.get(Calendar.HOUR_OF_DAY) + ":" + Calendar.get(Calendar.MINUTE) + ":" + Calendar.get(Calendar.SECOND);
//
//                String exportedText = dateAndTime + " " + history;
//
//                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
//                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, exportedText);
//                startActivity(Intent.createChooser(sharingIntent, exportedText));//getResources().getString(R.string.share_using)));
            }
        });
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

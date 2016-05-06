package hkust.engg1200.airshipcontrol;

import android.app.Activity;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements OnClickListener, OnCheckedChangeListener,
        OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    // This is the textview where we display all the status messages
    TextView mtxtStatus;

    // This is the reference to the Stop Button on the UI. If you include other similar image
    // buttons in your UI, you can use the same method as used for stop button. Just choose the
    // appropriate image, get a reference to the button in the onCreate() method. Then declare
    // the appropriate onClick() listener code in the onClick() method.
    ImageButton mbtnStop, mbtnDirUp, mbtnDirDown, mbtnDirLeft, mbtnDirRight, mbtnElevUp, mbtnElevDown, mbtnRotCw, mbtnRotCcw;

    // This is the Bluetooth On/Off Switch
    Switch mswConn;


    // The spinner is used to select a specific motor and set its speed.
//    Spinner mspMotor;
//    ArrayAdapter<String> motorsAdapter;

    // This value is initialized to the currently selected motor. This variable
    // takes on values from 0 .. 5 corresponding to Motor 1 .. Motor 6. Hence if
    // you need to get the motor number, add 1 to this variable.
    int currentMotorIndex;

    SeekBar mseekBarMotorSpeed, mseekBarMotorElev, mseekBarMotorHover;

    // The maximum and minimum speeds of the motors. This is used to set the range of
    // the seek bar (from -MAX_DUTY_CYCLE to +MAX_DUTY_CYCLE). The whole range of the
    // speed is 2*MAX_DUTY_CYCLE. The seekbar position value ranges from 0 .. maximum value.
    // The maximum value of the seekbar is set to 2*MAX_DUTY_CYCLE. Thus the centre point will
    // be at MAX_DUTY_CYCLE corresponding to a motor speed of 0.
    final int MIN_DUTY_CYCLE = 0;
    final int MAX_DUTY_CYCLE = 255;

    int motorVel = 0, elevMotorVel = 0, hoverMotorVel = 0;

    // This array keeps track of the seekbar position for each motor
    // set this value in the onProgressChanged() method of the seekbar
    // DO seekBarPos[currentMotorIndex] = progress;
    int[] seekBarPos = {MAX_DUTY_CYCLE, MAX_DUTY_CYCLE, MAX_DUTY_CYCLE, MAX_DUTY_CYCLE, MAX_DUTY_CYCLE, MAX_DUTY_CYCLE};

    // This array keeps track of the motor speeds for each of the six motors.
    // set this value in the onProgressChanged() method of the seekbar
    // DO motorSpeed[currentMotorIndex] = (progress - MAX_DUTY_CYCLE);
    int[] motorSpeed = {MIN_DUTY_CYCLE, MIN_DUTY_CYCLE, MIN_DUTY_CYCLE, MIN_DUTY_CYCLE, MIN_DUTY_CYCLE, MIN_DUTY_CYCLE};

    int[] motorUpValues = {-145, -135, -135, 200, -200, -200};
    int[] motorDownValues = {200, -135, -135, -175, 200, 180};
    int[] motorLeftValues = {-200, -135, -135, -0, 200, 0};
    int[] motorRightValues = {-0, -135, -135, -200, 0, 200};
    int[] motorElevUpValues = {0, 0, -200, -200, -200, 0};
    int[] motorElevDownValues = {0, 0, 200, 200, 200, 0};
    int[] hoverValues = {0, -135, -135, 0, 0, 0};

    TextView mspeedMin, mspeedMax;

    /*
     * Controller:
     *
     *    The controller object gives us the right Application Programmer's Interface (API) to
     *    manage the connection to the Arduino board through Bluetooth to issue commands to the
     *    motors. This class exposes the following methods which you can call to control the motors
     *    attached to the Arduino board
     *
     *    Connect(): Turn on the Bluetooth Connection and establish connection to a device
     *
     *    Disconnect(): Turn off the Bluetooth Connection
     *
     *    IsConnected(): Is the Bluetooth connection on (returns true) or off (returns false)
     *
     *    AutoStartBT(): Automatically enable the Bluetooth adapter so that we can establish connection.
     *         This is done at the start.
     *
     *    AllStop(): Stop all the motors and set their speeds to zero
     *
     *    MotorStart(int motorindex, int velocity): set the speed of the motor "motorindex" to "velocity"
     *         In this case "motorindex" goes from 0 .. 5 corresponding to Motor 1 .. Motor 6
     *         velocity ranges from -MAX_DUTY_CYCLE .. MAX_DUTY_CYCLE
     *
     *
     *
     */
    Controller controller;

    /*****
     * Bluetooth protection timer and timer task begins
     *****/
    //To monitor the bluetooth connection
    Timer timer = new Timer(true);

    // Bluetooth connection protection
    public class timerTask extends TimerTask {
        public void run() {
//            if (controller.isBTSignal == false) {    // if no motor signal is currently sending
                Object arr[] = {0};
                controller.SendCmd("PING", arr);
//                //Log.v("Timer working", "Done!!");
//            }
        }
    }

    ;

    /*****
     * Bluetooth protection timer and timer task ends
     *****/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Send bluetooth signal to Arduino in a fix period of time
        timer.schedule(new timerTask(), 1000, 2000);

        // This sets the User Interface to the layout designed in activity_main.xml file
        setContentView(R.layout.activity_main);

        // create the controller object
        controller = new Controller(this);

        // check if bluetooth is available and turn it on automatically
        if (!controller.AutoStartBT()) {
            return;
        }

        // get a reference to the status textview
        mtxtStatus = (TextView) findViewById(R.id.txtStatus);

        // get a reference to the stop button and set it to be disabled
        // set the onclick listener for the button.
        // You can use the same procedure to include other image buttons on the UI
        mbtnStop = (ImageButton) findViewById(R.id.btnStop);
        mbtnStop.setOnTouchListener(this);
        mbtnStop.setEnabled(false);

        mbtnDirUp = (ImageButton) findViewById(R.id.fwdBtn);
        mbtnDirUp.setOnTouchListener(this);
        mbtnDirUp.setEnabled(false);

        mbtnDirDown = (ImageButton) findViewById(R.id.backBtn);
        mbtnDirDown.setOnTouchListener(this);
        mbtnDirDown.setEnabled(false);

        mbtnDirLeft = (ImageButton) findViewById(R.id.leftBtn);
        mbtnDirLeft.setOnTouchListener(this);
        mbtnDirLeft.setEnabled(false);

        mbtnDirRight = (ImageButton) findViewById(R.id.rightBtn);
        mbtnDirRight.setOnTouchListener(this);
        mbtnDirRight.setEnabled(false);

        mbtnElevDown = (ImageButton) findViewById(R.id.elevDownBtn);
        mbtnElevDown.setOnTouchListener(this);
        mbtnElevDown.setEnabled(false);

        mbtnElevUp = (ImageButton) findViewById(R.id.elevUpBtn);
        mbtnElevUp.setOnTouchListener(this);
        mbtnElevUp.setEnabled(false);

        // get a reference to the switches and st them to be disabled. set their on checked change listeners
        mswConn = (Switch) findViewById(R.id.swConn);
        mswConn.setOnCheckedChangeListener(this);


        //get a reference to the spinner
//        mspMotor = (Spinner) findViewById(R.id.spMotor);
//        motorsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
//        motorsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        for (int i = 1; i <= 6; i++) {
//            motorsAdapter.add("Motor " + i);
//        }
//        mspMotor.setAdapter(motorsAdapter);
//        mspMotor.setEnabled(false);
//        mspMotor.setOnItemSelectedListener(this);

        // Recall that the motor speed ranges from -MAX_DUTY_CYCLE to MAX_DUTY_CYCLE.
        // so the whole range is 2*MAX_DUTY_CYCLE. The seek bar position is always positive
        // Thus the seek bar position goes from 0 .. 2*MAX_DUTY_CYCLE
        mseekBarMotorSpeed = (SeekBar) findViewById(R.id.seekBarMotorSpeed);
        mseekBarMotorSpeed.setEnabled(false);
        mseekBarMotorSpeed.setMax(150);
        mseekBarMotorSpeed.setProgress(0);
        mseekBarMotorSpeed.setOnSeekBarChangeListener(this);

        mseekBarMotorElev = (SeekBar) findViewById(R.id.seekBarMotorSpeedElev);
        mseekBarMotorElev.setEnabled(false);
        mseekBarMotorElev.setMax(MAX_DUTY_CYCLE);
        mseekBarMotorElev.setProgress(0);
        mseekBarMotorElev.setOnSeekBarChangeListener(this);

        mseekBarMotorHover = (SeekBar) findViewById(R.id.seekBarHover);
        mseekBarMotorHover.setEnabled(false);
        mseekBarMotorHover.setMax(MAX_DUTY_CYCLE);
        mseekBarMotorHover.setProgress(0);
        mseekBarMotorHover.setOnSeekBarChangeListener(this);

        // These three textviews are used to show the scale indicator below the seekbar
        mspeedMin = (TextView) findViewById(R.id.speedMin);
        mspeedMax = (TextView) findViewById(R.id.speedMax);

        Integer i = MAX_DUTY_CYCLE;
        mspeedMin.setText("0");
        mspeedMax.setText(i.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {

            // if you add more image buttons to the UI, include similar case statements
            // for those buttons here and include the code before the break statement.
            // Don't forget to include the break statement at the end of each section
            // of the code. This is a major source of bugs in the code
            case R.id.btnStop:
                mtxtStatus.setText("Stop All Motors");
                // This statement is used to stop all the motors and set their speeds to 0
                controller.AllStop();
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {

                // if you add more image buttons to the UI, include similar case statements
                // for those buttons here and include the code before the break statement.
                // Don't forget to include the break statement at the end of each section
                // of the code. This is a major source of bugs in the code
                case R.id.btnStop:
                    mtxtStatus.setText("Stop All Motors");

                    // This statement is used to stop all the motors and set their speeds to 0
                    controller.AllStop();
                    break;
                case R.id.fwdBtn:
                    mtxtStatus.setText("Forward");
                    controller.calculateMotorValues(motorVel, 90, false, elevMotorVel, false, hoverMotorVel);
                    break;
                case R.id.backBtn:
                    mtxtStatus.setText("Backward");
                    controller.calculateMotorValues(motorVel, 270, false, elevMotorVel, false, hoverMotorVel);
                    //controller.sendHoverMotorCmd(motorDownValues, motorVel / 255f);
                    break;
                case R.id.leftBtn:
                    mtxtStatus.setText("Left");
                    controller.calculateMotorValues(motorVel, 180, false, elevMotorVel, false, hoverMotorVel);
                    //controller.sendHoverMotorCmd(motorLeftValues, motorVel / 255f);
                    break;
                case R.id.rightBtn:
                    mtxtStatus.setText("Right");
                    controller.calculateMotorValues(motorVel, 0 ,false, elevMotorVel, false, hoverMotorVel);
                    //controller.sendHoverMotorCmd(motorRightValues, motorVel / 255f);
                    break;
                case R.id.elevUpBtn:
                    mtxtStatus.setText("Up");
                    controller.calculateMotorValues(motorVel, 0 , true, elevMotorVel, false, hoverMotorVel);
                    //controller.sendMotorCmd(motorElevUpValues, elevMotorVel / 255f);
                    break;
                case R.id.elevDownBtn:
                    mtxtStatus.setText("Down");
                    controller.calculateMotorValues(motorVel, 1 , true, elevMotorVel, false, hoverMotorVel);
                    //controller.sendMotorCmd(motorElevDownValues, elevMotorVel / 255f);
                    break;

                default:
                    break;
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP && v.getId() != R.id.btnStop) {
            mtxtStatus.setText("Hover");
            controller.calculateMotorValues(0,0,true, 200, true, hoverMotorVel);
            //controller.sendMotorCmd(hoverValues, 1);
//            controller.AllStop();
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton btn, boolean set) {

        if (btn == mswConn && set) {
            // If the bluetooth button is turned on, then execute the code below

            // create asyncTask for the user interaction
            // DO NOT disturb this code.
            class EnableUI extends AsyncTask<Void, Void, Void> {

                protected void onPreExecute() {

                    // show a paired devices list and wait for the user to select a device
                    // by pressing a selection for connection
                    controller.Connect();
                }

                protected void onPostExecute(Void result) {

                    // turn on UI after connected
                    mbtnStop.setEnabled(true);
                    mbtnDirLeft.setEnabled(true);
                    mbtnDirDown.setEnabled(true);
                    mbtnDirUp.setEnabled(true);
                    mbtnDirRight.setEnabled(true);
                    mbtnElevUp.setEnabled(true);
                    mbtnElevDown.setEnabled(true);
//                    mspMotor.setEnabled(true);
                    mseekBarMotorSpeed.setEnabled(true);
                    mseekBarMotorElev.setEnabled(true);
                    mseekBarMotorHover.setEnabled(true);

                }

                @Override
                protected Void doInBackground(Void... params) {

                    // check if it is connected
                    while (!controller.IsConnected()) ;
                    return null;
                }
            }

            EnableUI enableUI = new EnableUI();
            enableUI.execute();
            mtxtStatus.setText("Bluetooth Turned On!");
        } else if (btn == mswConn && !set) {
            // If the bluetooth button is turned off, then execute the code below

            // create asyncTask for the user interaction
            // DO NOT disturb this code.
            class DisableUI extends AsyncTask<Void, Void, Void> {


                protected void onPreExecute() {

                    // disconnecting
                    controller.Disconnect();
                }

                protected void onPostExecute(Void result) {

                    // turn off UI after disconnected
                    mbtnStop.setEnabled(false);
                    mbtnDirLeft.setEnabled(false);
                    mbtnDirDown.setEnabled(false);
                    mbtnDirUp.setEnabled(false);
                    mbtnDirRight.setEnabled(false);
                    mbtnElevUp.setEnabled(false);
                    mbtnElevDown.setEnabled(false);
//                    mspMotor.setEnabled(true);
                    mseekBarMotorSpeed.setEnabled(false);
                    mseekBarMotorElev.setEnabled(false);
                    mseekBarMotorHover.setEnabled(false);

                }

                @Override
                protected Void doInBackground(Void... params) {

                    // check if it is disconnected
                    while (controller.IsConnected()) ;
                    return null;
                }
            }

            DisableUI disableUI = new DisableUI();
            disableUI.execute();
            mtxtStatus.setText("Bluetooth Turned Off!");
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {

        // position indicated which position of the spinner. By default
        // position starts at 0. Remember, Motor 1 is in position 0 on
        // the spinner. So to get the motor number I have to add 1 to
        // position

        currentMotorIndex = position;

        mtxtStatus.setText("Motor " + (currentMotorIndex + 1) + " selected");

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        switch(seekBar.getId()){
            case R.id.seekBarMotorSpeed:
                motorVel = progress;
                mtxtStatus.setText("Directional motor speed set to " + progress);
                break;
            case R.id.seekBarMotorSpeedElev:
                elevMotorVel = progress;
                mtxtStatus.setText("Elevation motor speed set to " + progress);
                break;
            case R.id.seekBarHover:
                hoverMotorVel = progress;
                mtxtStatus.setText("Hover motor speed set to " + progress);
                controller.calculateMotorValues(0,0,true, 200, true, hoverMotorVel);
                break;
        }
//        mtxtStatus.setText("Motor " + (currentMotorIndex + 1) + "'s Speed set to " + (progress));
//        seekBarPos[currentMotorIndex] = progress;
//        motorSpeed[currentMotorIndex] = (progress);

        // start the motor selected by the spinner at the speed specified by the seekbar
        // Note that we identify Motor 1 as 0, Motor 2 as 1 and so on.
//        motorVel = progress;
        //controller.MotorStart(currentMotorIndex, progress - MAX_DUTY_CYCLE);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

}

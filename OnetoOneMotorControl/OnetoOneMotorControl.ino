#include <SoftwareSerial.h>
//SoftwareSerial debug(0,1);

// Libraries
//#include <communication.h>
#include <motor_control.h>
#include <BT_timer.h>
#include <SimpleTimer.h>

String data = "";

/*NOTE: Notice that throughout this code there are
        references to timers. These are safety functions
        we added so that the motors shut-down automatically
        when the bluetooth is disconnected. You do not need
        to know how these functions work */


void setup(){  
  //comm_init();                     // To initiate the bluetooth communication function.  
  motor_control_init();       // To initiate the motor control (mapping pins and handling direction).
  timer_init();         // To initiate the bluetooth connection protection timer
  Serial.begin(115200);
  //debug.begin(115200);
}

void loop(){
  timerRun();             // Keep the timer running
  if(Serial.available()){
    char c = Serial.read();
    if(c == '\n'){
       handleCommand(data);
       data ="";
    }
    else{
      data += c;
    }
  }
} 

void handleCommand(String cmd) {
  String cmdBuffer[100] = {};
    int appendIndex = 0;
    while (cmd.indexOf(",") != -1) {
       cmdBuffer[appendIndex++] = cmd.substring(0, cmd.indexOf(","));
       cmd = cmd.substring(cmd.indexOf(",")+1, cmd.length());
    }
//    Serial.println("Index: " + appendIndex);
//    for(int i = 0; i<=appendIndex; i++){
//      Serial.println(cmdBuffer[i]);
//    }
  if(cmdBuffer[0].equals("PING") && appendIndex == 2){
    startTimer();
  }
  else if(cmdBuffer[0].equals("MOTOR_CONTROL")&& appendIndex == 7){
    for(int i = 0; i < 6; i++){
      motor_start(i, cmdBuffer[i+1].toInt());
    }
  }
  else if(cmdBuffer[0].equals("STOP") && appendIndex == 2){
    stop_all();
  }
}

//void execute_cmd(int index, int value){   // Function to receive parameters from the Android apps
//
//  if(index>=0 && index<6){            // Check if the index value (motor number 0-5) is available                    
//    motor_start(index, value);            // If yes, start the indexed motor with given power value (-255 255)  
//  }  
//  else if(index == 7){                          // Android app will send a signal to this Arduino program for every one second
//    startTimer();                               // Restart the timer
//  } 
//  else if(index == 8){
//    stop_all();
//  }
//  else{   
//    stop_all();                             // Stop all the motors.  
//  }
//}

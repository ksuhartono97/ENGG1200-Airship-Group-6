// Libraries
#include <communication.h>
#include <motor_control.h>
#include <BT_timer.h>
#include <SimpleTimer.h>

/*NOTE: Notice that throughout this code there are
        references to timers. These are safety functions
        we added so that the motors shut-down automatically
        when the bluetooth is disconnected. You do not need
        to know how these functions work */


void setup(){  
  comm_init();                     // To initiate the bluetooth communication function.  
  motor_control_init();       // To initiate the motor control (mapping pins and handling direction).
  timer_init();         // To initiate the bluetooth connection protection timer
}

void loop(){
  timerRun();             // Keep the timer running
} 

void execute_cmd(int index, int value){   // Function to receive parameters from the Android app

  if(index != 7){                         // If index does not equal to 7
    Serial.print("Index = ");             // The following code will print out the Index and Value
    Serial.print(index);                  // received by the Arduino from the App
    Serial.print("\t");
    Serial.print("Value = ");
    Serial.print(value);
    Serial.print("\n");
  }
  else if(index == 7){                          // Android app will send a signal of index = 7 and value = 0  every second
    startTimer();                               // Once the Arduino receives this signal, the timer will restart
  }                                             // This is a safety feature
}

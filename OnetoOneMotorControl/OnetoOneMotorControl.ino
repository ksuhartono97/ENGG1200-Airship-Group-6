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

void execute_cmd(int index, int value){   // Function to receive parameters from the Android apps

  if(index>=0 && index<6){            // Check if the index value (motor number 0-5) is available                    
    motor_start(index, value);            // If yes, start the indexed motor with given power value (-255 255)  
  }  
  else if(index == 7){                          // Android app will send a signal to this Arduino program for every one second
    startTimer();                               // Restart the timer
  } 
  else if(index == 8){
    stop_all();
  }
  else if(index >=9 && index <=16){
    switch(index){
      case 9: //Forward
      motor_start(0, value);
      motor_start(3, value);
      motor_start(4, value);
      motor_start(5, value);
      break;
      case 10: //Backward
      motor_start(0, value);
      motor_start(3, -value);
      motor_start(4, value);
      motor_start(5, value);
      break;
      case 11: //Left
      motor_start(0, value);
      motor_start(5, value);
      motor_start(3, -value);
      motor_start(4, -value);
      break;
      case 12: //Right
      motor_start(0, -value);
      motor_start(5, -value);
      motor_start(3, value);
      motor_start(4, value);
      break;
      case 13: //Elev Up
      motor_start(1, value);
      motor_start(2, -value);
      break;
      case 14: //Elev Down
      motor_start(1, -value);
      motor_start(2, value);
      break;
      case 15: // CW
      motor_start(0, value);
      motor_start(5, -value);
      motor_start(3, value);
      motor_start(4, -value);
      break;
      case 16: //CCW
      motor_start(0, -value);
      motor_start(5, value);
      motor_start(3, -value);
      motor_start(4, value);
      break;
    }
  }
  else{   
    stop_all();                             // Stop all the motors.  
  }
}

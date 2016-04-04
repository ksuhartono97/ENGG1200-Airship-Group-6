// Libraries
#include <communication.h>
#include <motor_control.h>
#include <BT_timer.h>
#include <SimpleTimer.h>

//Lab Task 6: Create Speed Variables here



/*--------------------------------------*/
void setup(){  
  comm_init();                   	// To initiate the bluetooth communication function.  
  motor_control_init();    		// To initiate the motor control (mapping pins and handling direction).
  timer_init();    		 	// To initiate the bluetooth connection protection timer
}
void loop(){
  timerRun();                                   // Keep the timer running
} 
void execute_cmd(int index, int value){ 	// Function to receive parameters from the Android apps

  if(index>=0 && index<6 && value != 0){	 	        // Check if the index value (motor number 0-5) is available, and value is not 0                    
    motor_start(index, value);  	        // If yes, start the indexed motor with given power value (-255 255)  
  }  
  else if(index == 7){                          // Android app will send a signal to this Arduino program for every one second
    startTimer();                               // Restart the timer, DO NOT REMOVE THIS PART
  } 
  else if(index == 8){
    
  }
  else if (index == 9){
    
  }
  else if (index == 10){
      
  }
  else if (index == 11){
    
  }
  else if (index == 12) {
    
  }
  else if (index == 13){
    
  }
  else if(index == 14){
    
  }
  else if(index == 0 && value == 0){            // If index is 0 and value is 0 (This is the data sent by the STOP button)
    stop_all();                                 // Stop all motors
  }
  else{
    stop_all();                                 // If none of the conditions above are fulfilled, stop all motors
  }
}


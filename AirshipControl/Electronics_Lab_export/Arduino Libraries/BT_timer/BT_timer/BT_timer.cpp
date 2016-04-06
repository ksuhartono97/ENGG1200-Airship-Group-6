#include <BT_timer.h>

// Timer variables for bluetooth disconnected protection
SimpleTimer timer;
int BT_timer_id = 0;

// Function to protect bluetooth disconnection
void timer_init(void){
  BT_timer_id = timer.setTimeout(1000, terminate);  // Stop all the motors in two seconds after bluetooth is disconnected, setTimeout function will return the ID of the timer.
  timer.disable(BT_timer_id);                       // Disable to timer before connected to Android phone
}

void startTimer(void){
  if(!timer.isEnabled(BT_timer_id)){
    timer.enable(BT_timer_id);                      // Enable the timer when bluetooth connection is established
  }
  timer.restartTimer(BT_timer_id);                // Restart the timer
}

void timerRun(void){
  timer.run();									// Keep the timer running
}

void terminate(void){                               // terminate function is called 2 seconds after bluetooth is disconnected
  for(int index = 0;index<6;index++){
    motor_start(index, 0);
  }
}

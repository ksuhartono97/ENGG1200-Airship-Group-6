// Including necessary libraries
#include <motor_control.h>
#include <BT_timer.h>
#include <SimpleTimer.h>

void setup()       
{
  motor_control_init();  //initialize the necessary pins
}

void loop()
{
  //Loop will run repeatedley
  //Enter your code here
  motor_start(0, 100);
}

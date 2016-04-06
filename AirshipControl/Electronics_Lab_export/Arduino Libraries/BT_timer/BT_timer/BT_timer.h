#ifndef __BTTIMER_H
#define __BTTIMER_H

#include <Arduino.h>
#include <SimpleTimer.h>
#include <motor_control.h>

void timer_init(void);
void startTimer(void);
void timerRun(void);
void terminate(void);

#endif
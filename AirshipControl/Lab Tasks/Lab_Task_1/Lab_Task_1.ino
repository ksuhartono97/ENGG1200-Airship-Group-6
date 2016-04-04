// Blink Program
 
void setup() {                
  // the setup routine runs once when you press reset
  // initialize the digital pin as an output.
  pinMode(13, OUTPUT);     
}

void loop() {
  // the loop routine runs over and over again forever
  digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(1000);               // wait for a second
  digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
  delay(1000);               // wait for a second
}


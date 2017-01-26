/* LICENSE
 * Copyright (C) 2014 openQCM
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/gpl-3.0.txt
 *
 * INTRO
 * openQCM is the unique opensource quartz crystal microbalance http://openqcm.com/
 * openQCM Java software project is available on github repository 
 * https://github.com/marcomauro/openQCM
 * 
 * Measure QCM frequency using FreqCount library developed by Paul Stoffregen 
 * https://github.com/PaulStoffregen/FreqCount
 *
 * NOTE       - designed for 6 and 10 Mhz At-cut quartz crystal
 *            - 3.3 VDC supply voltage quartz crystal oscillator
 *            - Configure EXTERNAL reference voltage used for analog input
 *            - Thermistor temperature sensor
 *
 * author     Marco Mauro / Luciano Zu
 * version    2.0
 * date       september 2016 
 *
 */

String inputString = "";         // a string to hold incoming data (Ardulink)
boolean stringComplete = false;  // whether the string is complete (Ardulink)

// print data to serial port 
void dataPrint(unsigned long Count, int Temperature){
  Serial.print("alp://cevnt/");
  Serial.print("RAWMONITOR");
  Serial.print(Count);
  Serial.print("_");
  Serial.print(Temperature);
  Serial.print('\n');
  Serial.flush();
}

// QCM frequency by counting the number of pulses in a fixed time 
unsigned long frequency = 0;
// thermistor temperature
int temperature = 0;

void setup(){
  Serial.begin(115200);
  while(!Serial); // Wait until Serial not connected (because difference between Leonardo and Micro with UNO and others)
}

void loop(){
  frequency = 20;       // measure QCM frequency
  temperature = 10;     // measure temperature 
  dataPrint(frequency, temperature);  // print data
  delay(1000);
}


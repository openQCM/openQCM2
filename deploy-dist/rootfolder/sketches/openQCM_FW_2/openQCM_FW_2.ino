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

// include library for frequency counting
#include <FreqCount.h>

#include <EEPROM.h>

// fixed "gate interval" time for counting cycles 1000ms  
#define GATE   1000
// Thermistor pin
#define THERMISTORPIN A1 
// // resistance at 25 degrees C
#define THERMISTORNOMINAL 10000
// temp. for nominal resistance (almost always 25 C)
#define TEMPERATURENOMINAL 25   
// how many samples to take and average 
#define NUMSAMPLES 10
// The beta coefficient of the thermistor (usually 3000-4000)
#define BCOEFFICIENT 3950
// the value of the 'other' resistor
#define SERIESRESISTOR 10000    

/*
Unique ID is suggested by connected PC. Then it is stored and used for ever.
Unique ID is stored in EEPROM at a given address. A magic number (2 bytes) "LZ"
is used to understand if it is already stored or not.
Actually Unique ID is a UUID in the string format (36 bytes instead of just 16, yes it could be better).
https://en.wikipedia.org/wiki/Universally_unique_identifier
So the final length to store the unique id is 38 bytes.
*/
#define UNIQUE_ID_EEPROM_ADDRESS 0
#define UNIQUE_ID_LENGTH 38
#define UNIQUE_ID_MAGIC_NUMBER_HIGH 76
#define UNIQUE_ID_MAGIC_NUMBER_LOW 90

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


// measure temperature
int getTemperature(void){
  int i;
  float average;
  int samples[NUMSAMPLES];
  float thermistorResistance;
  int Temperature; 

  // acquire N samples
  for (i=0; i< NUMSAMPLES; i++) {
    samples[i] = analogRead(THERMISTORPIN);
    delay(10);
  }

  // average all the samples out
  average = 0;
  for (i=0; i< NUMSAMPLES; i++) {
    average += samples[i];
  }
  average /= NUMSAMPLES;

  // convert the value to resistance
  thermistorResistance = average * SERIESRESISTOR / (1023 - average);
  
  float steinhart;
  steinhart = thermistorResistance / THERMISTORNOMINAL;          // (R/Ro)
  steinhart = log(steinhart);                       // ln(R/Ro)
  steinhart /= BCOEFFICIENT;                        // 1/B * ln(R/Ro)
  steinhart += 1.0 / (TEMPERATURENOMINAL + 273.15); // + (1/To)
  steinhart = 1.0 / steinhart;                      // Invert
  steinhart -= 273.15;                              // convert to C

  // decimal value
  Temperature = steinhart * 10;
  return(Temperature);
}

// read commands sent with Ardulink
void readInputCommands(){
  while (Serial.available() && !stringComplete) {
     // get the new byte:
    char inChar = (char)Serial.read();
    // add it to the inputString:
    inputString += inChar;
    // if the incoming character is a newline, set a flag
    // so the main loop can do something about it:
    if (inChar == '\n') {
      stringComplete = true;
    }
  }

  if (stringComplete) {
    
    if(inputString.startsWith("alp://")) { // OK is a message I know (Ardulink)
    
      boolean msgRecognized = true;
      String uniqueID = "";
      
      if(inputString.substring(6,10) == "cust") { // Custom Message
        int separatorPosition = inputString.indexOf('/', 11 );
        int messageIdPosition = inputString.indexOf('?', 11 );
        String customCommand = inputString.substring(11,separatorPosition);
        String value = inputString.substring(separatorPosition + 1, messageIdPosition); // suggested uniqueID
        if(customCommand == "getUniqueID") {
        	 uniqueID = getUniqueID(value);
        } else {
          msgRecognized = false; // this sketch doesn't know other messages in this case command is ko (not ok)
        }
      } else {
        msgRecognized = false; // this sketch doesn't know other messages in this case command is ko (not ok)
      }
      
      // Prepare reply message if caller supply a message id (this is general code you can reuse)
      int idPosition = inputString.indexOf("?id=");
      if(idPosition != -1) {
        String id = inputString.substring(idPosition + 4);
        // print the reply
        Serial.print("alp://rply/");
        if(msgRecognized) { // this sketch doesn't know other messages in this case command is ko (not ok)
          Serial.print("ok?id=");
        } else {
          Serial.print("ko?id=");
        }
        Serial.print(id);
        if(uniqueID.length() > 0) {
        	Serial.print("&UniqueID=");
        	Serial.print(uniqueID);
        }
        Serial.print('\n'); // End of Message
        Serial.flush();
      }
    }
    
    // clear the string:
    inputString = "";
    stringComplete = false;
  }

}

String getUniqueID(String suggested) {

	char buffer[UNIQUE_ID_LENGTH + 1];
	String retvalue = suggested;
	
	EEPROM.get( UNIQUE_ID_EEPROM_ADDRESS, buffer );
	if(buffer[0] == UNIQUE_ID_MAGIC_NUMBER_HIGH && buffer[1] == UNIQUE_ID_MAGIC_NUMBER_LOW) {
		retvalue = String(&buffer[2]);
	} else {
		buffer[0] = UNIQUE_ID_MAGIC_NUMBER_HIGH;
		buffer[1] = UNIQUE_ID_MAGIC_NUMBER_LOW;
		suggested.toCharArray(&buffer[2], UNIQUE_ID_LENGTH - 1);
		EEPROM.put( UNIQUE_ID_EEPROM_ADDRESS, buffer );
	}

	return retvalue;
}


// QCM frequency by counting the number of pulses in a fixed time 
unsigned long frequency = 0;
// thermistor temperature
int temperature = 0;

void setup(){
  Serial.begin(115200);
  // Configure the reference voltage used for analog input 
  analogReference(EXTERNAL);
  // init the frequency counter
  FreqCount.begin(GATE);
}

void loop(){
  if (FreqCount.available()) 
  {
    frequency = FreqCount.read();       // measure QCM frequency
    temperature = getTemperature();     // measure temperature 
    dataPrint(frequency, temperature);  // print data
  }
  
  readInputCommands();
}


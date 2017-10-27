/*
  XBEE #2 SENDER
  ==============
  commente 
 */

#include <SoftwareSerial.h>

#define ON 100
#define OFF 0

SoftwareSerial mySerial(10,11); // Rx, Tx
String reponse = "";
int valeur_actuelle = 0;

void setup() {
  
  Serial.begin(9600);
  while(!Serial){;}
  mySerial.begin(9600);
  
  Serial.println("**** Début du programme émetteur ****");
  xbeeInit();
  valeur_actuelle = OFF;
  commanderLampe(valeur_actuelle);
}

void loop() {
  // Receive serial data from xbee #1 (recepteur)
  delay(30);
  if(mySerial.available() > 0) {
    reponse = reponse + (char) mySerial.read();
    if(mySerial.available() ==0){
      Serial.println(reponse);
      if(reponse.toInt() != valeur_actuelle){
          commanderLampe(valeur_actuelle);
      } else{
        //algo de test 
         if (valeur_actuelle == ON){
            valeur_actuelle = OFF;
         }else {
            valeur_actuelle = valeur_actuelle + 10;
         }
        //jusque ici
         commanderLampe(valeur_actuelle);
       }
       reponse="";                                    //reset reponse
     }
   }
}

// Xbee configuration
void xbeeInit() {
  
  mySerial.print("+++");
  Serial.println("+++");
  
  verifyACK();    
  
  mySerial.print("ATRE\r");
  Serial.println("ATRE\r");
   
  verifyACK();

  mySerial.print("ATNIEMITTER\r"); 
  Serial.println("NAME (ATNI)\r");
   
  verifyACK();

// mySerial.print("ATDL0\r");
//  Serial.println("ATDL0\r");
//
//  verifyACK();
  
  mySerial.print("ATID1111\r");
  Serial.println("ATID1111\r");
   
  verifyACK();
  
  mySerial.print("ATCN\r");
  Serial.println("ATCN\r");
 
  verifyACK();
  
  Serial.println("Emetteur configuré !");
 
}

// Verify the acknowkedge of the Xbee module 
void verifyACK () {
  
  char thisByte = 0;
  Serial.print("ACK = ");
  while (thisByte != '\r') {
    if (mySerial.available()  > 0) {
      thisByte = mySerial.read();
      Serial.print(thisByte);
    }    
  }
  Serial.println();  
  
}

//void eteindreLampe(){
//  mySerial.print(OFF);
//}
//void allumerLampe(int valeur){
//  mySerial.println(ON);
//  mySerial.print(valeur);
//}

void commanderLampe(int valeur){
    mySerial.print(valeur);
}


/*
  XBEE #1 RECEIVER
  ================
  commente
 */
#include <SoftwareSerial.h>

SoftwareSerial mySerial(10,11); //Rx, Tx

const int ledPin = 3; //pin PWM pour varié l'intensité
String commande = ""; 

int valeur = 0;

void setup() {
  
    Serial.begin(9600);
    while(!Serial){;}    
    mySerial.begin(9600);
    
    Serial.println("**** Début du programme récepteur ****");
    pinMode (ledPin, OUTPUT);
    xbeeInit();
    
}

void loop() {
  // Receive serial data from xbee #2 (emetteur)
  delay(30);
  if (mySerial.available() > 0) {
    commande = commande + (char) mySerial.read();
    if (mySerial.available() ==0){
      Serial.println(commande);
      valeur = map(commande.toInt(), 0, 100, 0, 254);    //mise a l'achelle 
      analogWrite(ledPin, valeur);                       //allume la led
      mySerial.print(commande.toInt());                 //envoie l'ack (la même valeur que celle reçu)
      commande="";                                       //reset commande reçue
    }
    
  }
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

// Xbee configuration
void xbeeInit() {

  mySerial.print("+++");
  Serial.println("+++"); 
   
  verifyACK();  
  
  mySerial.print("ATRE\r");
  Serial.println("ATRE\r");
   
  verifyACK();

  mySerial.print("ATNIRECEIVER\r"); 
  Serial.println("NAME (ATNI)\r");
   
  verifyACK();
  
  mySerial.print("ATID1111\r");
  Serial.println("ATID1111\r");
   
  verifyACK();
  
  mySerial.print("ATCN\r");
  Serial.println("ATCN\r");
 
  verifyACK();
  
  Serial.println("Récepteur configuré !");
}


  


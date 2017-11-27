/*
  XBEE #1 RECEIVER
  ================
  commente
 */
#include <SoftwareSerial.h>

SoftwareSerial mySerial(10,11); //Rx, Tx

const int ledPin = 3; //pin PWM pour varié l'intensité
String commande = ""; 
String id = "";
String lettre;
boolean flag = false;
String retour = "";

int valeur = 0;
String ID = "1";

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
  //Serial.println("aboucle");
  if (mySerial.available() > 0) {
        lettre = (char) mySerial.read();
    if (lettre == "|"){
        flag = true;
        //Serial.println("flag");
    }else if (flag == false){
         id = id + lettre;
    }else if (flag == true){
        commande = commande + lettre;
    }
     // Serial.println(id);
      
    if (lettre =="."){
        Serial.print("Debut. Id:");
        Serial.println(id);
       if (id == ID){
        Serial.print("J'ai reconnu mon id je dois executer :");
        Serial.println(commande);
        valeur = map(commande.toInt(), 0, 100, 0, 254);    //mise a l'achelle 
        analogWrite(ledPin, valeur); 
        retour = id + "|" + commande + ".";
        mySerial.print(retour);                 //envoie l'ack (la même valeur que celle reçu)
        Serial.print(retour);
        Serial.println(" : envoie du ack. Fin.");
        }
       id="";
       flag =false;
       commande="";
       retour= "";
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


  


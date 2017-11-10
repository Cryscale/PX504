const EventEmitter = require("events");
var SerialPort = require("serialport");
var port = new SerialPort("/dev/ttyAMA0", {baudRate: 9600});
var count = 0;

class MyEmitter extends EventEmitter{};

const myEmitter = new MyEmitter();

myEmitter.on("ok", function() {
	//TODO : Valeur de la base de données à mettre ici 
	setLamp("50");
});

port.on("open", function(){
	console.log("open");
	xbeeInit();
	port.on("data", function(data){
		console.log("data received: " + data);
		if(data == "OK\r"){
			xbeeInit();
		}
	});

});

//xbeeInit(count);


function setLamp(data){
	port.write(data);
};

function xbeeInit(){

	switch(count) {
		case 0: 
			console.log("Xbee Init");
			port.write("+++");
			console.log("+++");
			count++;
			break;
		case 1:
			port.write("ATRE\r");
			console.log("ATRE");
			count++;
			break;
		case 2:
			port.write("ATNIEMITTER\r");
			console.log("ATNIEMITTER");
			count++;
			break;
		case 3:
			port.write("ATID1111\r");
			console.log("ATID1111");
			count++;
			break;
		case 4:
			port.write("ATCN\r");
			console.log("ATCN");
			count++;
			break;
		default:
			console.log("Emetteur configuré");
			myEmitter.emit("ok");
	}

};




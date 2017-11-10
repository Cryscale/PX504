var http = require('http');
var fs = require('fs');
var io = require('socket.io');
var mysql = require('mysql');
const EventEmitter = require("events");
var SerialPort = require("serialport");
var port = new SerialPort("/dev/ttyAMA0", {baudRate: 9600});
var count = 0;

class MyEmitter extends EventEmitter{};

const myEmitter = new MyEmitter();

myEmitter.on("ok", function() {


    mysqlClient.connect(error => {
        if (error != null) {
            console.log(error);
            process.exit();
        }
        else {
            console.log("Connexion à la base de données réussi");
            server.listen(8080);
            console.log("Serveur démarré, pour vous connecter à celui-ci, dans un navigateur Web, entrez l'adresse https://localhost:8080");
        }
    });

});

var mysqlClient = mysql.createConnection({
        host: 'localhost',
        user: 'root',
        password: 'PX504',
        database: 'smart_remote'
    });



    // Chargement du fichier index.html affiché au client
    var server = http.createServer(function (req, res) {
            fs.readFile('./test.html', 'utf-8', function (error, content) {
                res.writeHead(200, { "Content-Type": "text/html" });
                res.end(content);
            });
        });

    server.on('error', error => {
        console.log(error);
        process.exit();
    });



    WSServer = io.listen(server);

    // Chargement de socket.io


    WSServer.sockets.on('connection', function (socket) {

        // Quand un client se connecte, on le note dans la console
        console.log('Un client est connecté !');

        socket.join('lampRoom');

        socket.on("getListLamp", msg => {

            console.log('Demande liste de lampe par '+msg.user);

            mysqlClient.query("SELECT * FROM Lamp INNER JOIN Control ON Lamp.id = Control.id WHERE Control.username = ?", [msg.user], (error, result) => {
                socket.emit('getListLampResult', { list: result });
            });
        });

        //Réaction à la requête de changement d'état d'une lampe
        socket.on("setLamp", msg => {

            console.log('Requète de changement');

            /*mysqlClient.query(
                "UPDATE Lamp SET State = ?, brightness = ? WHERE location = ?",
                [msg.state, msg.brightness, msg.location],
                (error, result) => {
                    if (error != null) {
                        console.log(error);
                    } else {
                        socket.broadcast.emit("setLampResult", { location: msg.location, state: msg.state, brightness: msg.brightness });
                        socket.emit("setLampResult", { location: msg.location, state: msg.state, brightness: msg.brightness });
                    }
                });*/

                setLamp(String(msg.brightness));

        });

        socket.on("login", msg => {

            console.log('Identification');

            //Recherche et vérification dans la base de données pour l'authentification

            mysqlClient.query("SELECT password FROM User WHERE username=?", [msg.user], (error, result) => {

                if (error != null) {
                    socket.emit("loginResult", { success: false });
                    console.log(error);
                } else {
                    if(result[0] != null) {
                        if (result[0].password == msg.password) {
                            socket.emit("loginResult", { success: true });
                        } else {
                            socket.emit("loginResult", { success: false });
                        }
                    } else {
                        socket.emit("loginResult", { success: false });
                    }
                    
                }
            });
        });


        /*En cas de perte dela communication avec le Arduino :
        
            PS : Préciser la localisation de la lampe grâce à la BDD
            socket.emit("zigbeeFail", {error: "Erreur : la connexion avec la lampe à été perdu"});

            En cas de défaillance du module zigbee sur la Raspberry
            socket.emit("zigbeeFail", {error: "Erreur : Le module Zigbee de la Raspberry est défaillant});
        */


    });

port.on("open", function(){
    console.log("open");
    xbeeInit();
    port.on("data", function(data){
        console.log("data received: " + data);
        if(data == "OK\r"){
            xbeeInit();
        } else {

            var msg = parseData(data);
            if(msg.brightness == 0) {

            } else {
                mysqlClient.query(
                "UPDATE Lamp SET State = ?, brightness = ? WHERE location = ?",
                ["on", msg.brightness, msg.location],
                (error, result) => {
                    if (error != null) {
                        console.log(error);
                    } else {
                        //socket.broadcast.emit("setLampResult", { location: msg.location, state: msg.state, brightness: msg.brightness });
                        //socket.emit("setLampResult", { location: msg.location, state: msg.state, brightness: msg.brightness });
                        socket.to('lampRoom').emit("setLampResult", { location: msg.location, state: msg.state, brightness: msg.brightness });
                    }
            });
            }
            
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

function parseData(data) {
    var id = data.substr(0, data.indexOf("\r"));
    var brightness = data.substr(data.indexOf("\r"), data.length - 1);
    mysqlClient.query("SELECT * FROM Lamp WHERE id=?", [parseInt(id)], (error, result) => {
        return({location: result[0].location, brightness: parseInt(brightness)});
    })
}
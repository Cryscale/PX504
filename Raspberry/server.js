var http = require('http');
var fs = require('fs');
var io = require('socket.io');
var mysql = require('mysql');

//Variables de test avant la mise en place de la base de données
const listLamp = ["Cuisine", "Salle à Manger", "Chambre"];
var state = [true, false, true];


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

    socket.on("getListLamp", msg => {

        mysqlClient.query("SELECT * FROM Lamp INNER JOIN Control ON Lamp.id = Control.id WHERE Control.username = ?", [msg.user], (error, result) => {
            socket.emit('listLamp', { list: result });
        });
    });

    //Réaction à la requête de changement d'état d'une lampe
    socket.on("changeLampState", msg => {



        mysqlClient.query(
            "UPDATE Lamp SET State = ?, brightness = ? WHERE location = ?",
            [msg.state, msg.brightness, msg.location],
            (error, result) => {
                if (error != null) {
                    console.log(error);
                } else {
                    socket.broadcast.emit("lampStateChanged", { location: msg.location, state: msg.state, brightness: msg.brightness });
                    socket.emit("lampStateChanged", { location: msg.location, state: msg.state, brightness: msg.brightness });
                }
            });

    });

    socket.on("login", msg => {

        //Recherche et vérification dans la base de données pour l'authentification

        mysqlClient.query("SELECT password FROM User WHERE username=?", [msg.user], (error, result) => {

            if (error != null) {
                socket.emit("loginResult", { success: false });
                console.log(error);
            } else {
                if (result[0].password == msg.password) {
                    socket.emit("loginResult", { success: true });
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

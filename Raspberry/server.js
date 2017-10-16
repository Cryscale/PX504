var http = require('http');
var fs = require('fs');
var io = require('socket.io');
var mysql = require('mysql');

//Variables de test avant la mise en place de la base de données
const listLamp = ["Cuisine", "Salle à Manger", "Chambre"];
var state = [true, false, true];

// Chargement du fichier index.html affiché au client
var server = http.createServer(function (req, res) {
    fs.readFile('./test.html', 'utf-8', function (error, content) {
        res.writeHead(200, { "Content-Type": "text/html" });
        res.end(content);
    });
});

// Chargement de socket.io
WSServer = io.listen(server);



WSServer.sockets.on('connection', function (socket) {

    // Quand un client se connecte, on le note dans la console
    console.log('Un client est connecté !');

    socket.on("getListLamp", msg => {

        /*
            Partie où l'on recherche les lampes sur la BDD
        */

        //Template de liste pour tester la fonctionnalité du message
        socket.emit("listLamp", { list: ["Cuisine", "Salle à Manger", "Chambre"], state: state });
    });

    //Réaction à la requête de changement d'état d'une lampe
    socket.on("changeLampState", msg => {

        var stateIndex = listLamp.indexOf(msg.location);
        state[stateIndex] = msg.state;

        //Normalement lance un programme mais dans ce cas pour les tests renvoie un message de notification
        socket.broadcast.emit("lampStateChanged", { location: msg.location, state: state[stateIndex], brightness: 50 });
        socket.emit("lampStateChanged", { location: msg.location, state: state[stateIndex], brightness: 50 });
    });

    socket.on("login", msg =>{
        
        /*
            Recherche et vérification dans la base de données pour l'authentification
        */

        //Envoi d'un message template indicant un succès de connexion
        socket.emit("loginResult", {success: true});
        //socket.emit("loginResult", {success: false});

    });


    /*En cas de perte dela communication avec le Arduino :
        PS : Préciser la localisation de la lampe grâce à la BDD
        socket.emit("zigbeeFail", {error: "Erreur : la connexion avec la lampe à été perdu"});

        En cas de défaillance du module zigbee sur la Raspberry
        socket.emit("zigbeeFail", {error: "Erreur : Le module Zigbee de la Raspberry est défaillant});
    */
    

});




server.listen(8080);

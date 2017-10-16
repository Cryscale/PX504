var http = require('http');
var fs = require('fs');
var io = require('socket.io');

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


// Quand un client se connecte, on le note dans la console
WSServer.sockets.on('connection', function (socket) {
    socket.broadcast.emit("message", "zebfiezoybo");
    console.log('Un client est connecté !');

    socket.on("getListLamp", msg => {

        /*
            Partie où l'on recherche les lampes sur la BDD
        */

        //Template de liste pour tester la fonctionnalité du message
        socket.emit("listLamp", { list: ["Cuisine", "Salle à Manger", "Chambre"], state: state });
    });

    socket.on("changeLampState", msg => {

        var stateIndex = listLamp.indexOf(msg.location);
        state[stateIndex] = msg.state;
        socket.broadcast.emit("lampStateChanged", { location: msg.location, state: state[stateIndex], brightness: 50 });
        socket.emit("lampStateChanged", { location: msg.location, state: state[stateIndex], brightness: 50 });
    });

});




server.listen(8080);

var https = require('https');
var fs = require('fs');
var io = require('socket.io');
var mysql = require('mysql');
var session = require('express-session');
var MySQLStore = require('express-mysql-session')(session);
var bodyParser = require('body-parser');
var express = require('express');


var mysqlClient = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'PX504',
    database: 'smart_remote'
});

var sessionStore = new MySQLStore({}, mysqlClient);

var app = express()

app.use(session({
    secret: 'PX504',
    cookie: { secure: false },
    store: sessionStore,
    saveUninitialized: true,
    resave: true
}));

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.post('/login', (req, res) => {
    res.setHeader('Content-Type', 'text/plain');
    res.setHeader('Cookie', "TEST");
    console.log(req.body.username);
    mysqlClient.query("SELECT password FROM User WHERE username=?", [req.body.username], (error, result) => {

        if (error != null) {
            req.session.destroy();
            res.end("ERROR");
        } else {
            if (result[0] != null) {
                if (result[0].password == req.body.password) {
                    
                    res.end("OK");
                } else {
                    req.session.destroy();
                    res.end("ERROR PASSWORD");
                }
            } else {
                req.session.destroy();
                res.end("ERROR USERNAME");
            }

        }
    });
    
});



// Chargement du fichier index.html affiché au client
var server = https.createServer({
    key: fs.readFileSync('encryption/key.pem'),
    cert: fs.readFileSync('encryption/cert.pem')
}, app);

server.on('error', error => {
    console.log(error);
    process.exit();
});



WSServer = io.listen(server);

WSServer.use((socket, next) => {

    console.log(socket.request.headers.cookie);
    if (socket.request.headers.cookie) {
        console.log(socket.request.headers.cookie);
        mysqlClient.query("SELECT * FROM sessions WHERE session_id=?", [socket.request.headers.cookie], (error, result) => {
            if (result[0] != null) {
                console.log(result);
                return next();
            } else {
                console.log("Erreur : Mauvais cookie");
            }
        })
    }
    next(new Error('Authentication error'));
});

// Chargement de socket.io


WSServer.sockets.on('connection', function (socket) {

    // Quand un client se connecte, on le note dans la console
    console.log('Un client est connecté !');

    socket.on("getListLamp", msg => {

        console.log('Demande liste de lampe par ' + msg.user);

        mysqlClient.query("SELECT * FROM Lamp INNER JOIN Control ON Lamp.id = Control.id WHERE Control.username = ?", [msg.user], (error, result) => {
            socket.emit('getListLampResult', { list: result });
        });
    });

    //Réaction à la requête de changement d'état d'une lampe
    socket.on("setLamp", msg => {

        console.log('Requète de changement');

        mysqlClient.query(
            "UPDATE Lamp SET State = ?, brightness = ? WHERE location = ?",
            [msg.state, msg.brightness, msg.location],
            (error, result) => {
                if (error != null) {
                    console.log(error);
                } else {
                    socket.broadcast.emit("setLampResult", { location: msg.location, state: msg.state, brightness: msg.brightness });
                    socket.emit("setLampResult", { location: msg.location, state: msg.state, brightness: msg.brightness });
                }
            });

    });

    socket.on("login", msg => {

        console.log('Identification');

        //Recherche et vérification dans la base de données pour l'authentification

        mysqlClient.query("SELECT password FROM User WHERE username=?", [msg.user], (error, result) => {

            if (error != null) {
                socket.emit("loginResult", { success: false });
                console.log(error);
            } else {
                if (result[0] != null) {
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

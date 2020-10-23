# TP-reseaux
TP de programmation réseaux 4IF (octobre 2020).

## Projet

Ce TP explore l'utilisation de fonctionalités réseau par la programmation. Il s'agit d'une mise en pratique des théories des réseaux, ainsi que de la réflexion sur l'architecture d'un système et sa documentation.  
Concrètement, il consiste en l'implémentation from scratch de systèmes de chat, ainsi que d'un serveur HTTP.  
Le langage de choix est Java.

## Sous-projets

Contient 4 sous-projets dans CodeSocket/src :
- [Chat TCP](Code-Socket/src/chat_TCP) : Implémentation d'un système de chat en interface graphique (également utilisable en ligne de commande), utilisant le protocole TCP, avec N clients reliés par 1 serveur. Le système supporte la connexion de multiples utilisateurs, leur identification, ainsi que la sauvegarde et restitution de l'historique des messages.
- [Serveur HTTP](Code-Socket/src/HTTP) : Implémentation d'un serveur HTTP, accessible depuis un navigateur. Contient le serveur en lui-même, ainsi que le programme test en java pour ping le serveur. Le serveur supporte les requêtes GET, POST et DELETE, divers types de ressources (html, texte brut, images, audio, js, ...), ainsi que l'exécution dynamique de code. Une page d'accueil est disponible, avec possibilité de tester la demande de divers fichiers, ainsi que l'exécution distante de code Python.
- [Chat Multicast](Code-Socket/src/chat_multicast) : Système de chat en ligne de commande, utilisant le multicast UDP, et donc N clients sans serveur.
- [Stream](Code-Socket/src/stream) : Fichiers de démonstration de l'utilisation de socket en Java, [disponibles sur Moodle](https://moodle.insa-lyon.fr/course/view.php?id=5793).

<img src="https://github.com/eneiss/TP-reseaux/blob/master/diagramme.png" width="50%">  
Logique du système de chat TCP

## Structure

**Structure des sous-projets chat_TCP et HTTP/HTTP_server**  
Le dossier du sous-projet contient 4 sous-dossiers :
- **src** : contient les fichiers source .java du sous-projet.
- **bin** : contient les versions .class compilées des fichiers source .java.
- **doc** : contient la javadoc du sous-projet, à consulter en ouvrant index.html.
- **lib** : contient les bibliothèques nécessaires au sous-projet (actuellement vide).

Il contient également des scripts bash pour l'automatisation de la compilation et de l'exécution des deux sous-projets.

## Compilation et Exécution

**Chat TCP**  
Dans le répertoire [Code-Socket/src/chat_TCP](Code-Socket/src/chat_TCP), la commande  
    `javac -d bin src/*.java`  
permet de compiler le code source du sous-projet (src/) et de produire les fichiers .class (bin/).  
La commande  
    `java -classpath bin chat_TCP.src.ServerConnectionThread [port]`  
permet de démarrer le serveur. Celui-ci doit être démarré avant les clients pour qu'ils puissent se connecter. (port) est le port sur lequel la socket de connexion du serveur doit être connectée. Cette valeur doit être connue des clients pour qu'il puissent se connecter au serveur.  
La commande  
    `java -classpath bin chat_TCP.src.ChatClient [host] [port]`  
permet de démarrer un client, et de le connecter au serveur localisé sur (host) et dont la socket de connexion est liée au port (port). Le client tente immédiatement de se connecter, puis ouvre son IHM. Il peut être utilisé via l'IHM aussi bien que via la console, en parallèle.
Alternativement, les fichiers [launch_chat_server.sh](Code-Socket/src/chat_TCP/launch_chat_server.sh) et [launch_chat_client.sh](Code-Socket/src/chat_TCP/launch_chat_client.sh) contiennent ces commandes, et peuvent être exécutés directement pour compiler et démarrer respectivement le serveur et un client. Ils utilisent le port 1234 et l'hôte local, mais peuvent être modifiés pour constituer un raccourci à une configuration couramment utilisée.

**Serveur HTTP**  
Dans le répertoire [Code-Socket\src\HTTP\HTTP_server](Code-Socket\src\HTTP\HTTP_server), la commande  
    `javac -d bin src/*.java`  
permet de compiler le code source du sous-projet (src/) et de produire les fichiers .class (bin/).  
La commande  
    `java -classpath bin HTTP.HTTP_server.src.WebServer [port]`  
permet de démarrer le serveur. (port) est le port sur lequel le serveur doit être ouvert. Il est alors possible d'y accéder via un navigateur, en accédant à l'URL [host]:[port].  
Alternativement, le fichier [launch_server.sh](Code-Socket\src\HTTP\HTTP_server/launch_chat_server.sh) contient ces commandes, et peut être exécuté directement pour compiler et démarrer le serveur. Il utilise le port 8080, mais peut être modifié pour constituer un raccourci à une configuration couramment utilisée.

## Auteurs

Emma Neiss & Yann Dupont, 2020.

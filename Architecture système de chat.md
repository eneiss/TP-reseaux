# Architecture système de chat

## Archi distribuée :

- un seul serveur central
- plusieurs clients

## Sockets

- un socket d'écoute sur le serveur pour recevoir les demandes de connexion
- le serveur ouvre un socket (et un thread) par client qui se connecte pour échanger des infos avec lui

## Threads


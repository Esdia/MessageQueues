# Channels

## Spécifications

Cette bibliothèque met à dispositions des canaux de communication bidirectionnels permettant à deux threads de
s'échanger des flux d'octets de manière FIFO garantie sans perte de données.

### Les Brokers

Les brokers sont des objets qui servent à établir la communication entre deux threads.
Chaque thread qui souhaite communiquer avec un autre doit instancier la classe `Broker` au moyen du constructeur, en
fournissant un nom qui l'identifiera de manière unique dans le système.

C'est à l'utilisateur de garantir que chaque broker est nommé de façon unique. Le comportement de la bibliothèque n'est
pas garanti si plusieurs brokers ont le même nom.

Les méthodes des brokers ne sont **pas** thread safe.

### Établissement de la connexion

L'établissement de la connexion est asymétrique. Un thread (que l'on nommera 'A') doit se mettre en attente d'une
connexion entrante et l'autre (que l'on nommera B) doit essayer de se connecter au premier.

Pour cela, le thread A appellera la méthode `accept` de son broker, en fournissant un port. Le port permet de
différencier les différentes connexions sur le broker, il en découle donc que chaque connexion établie sur un même
broker doit utiliser un port différent.

La méthode `accept` est bloquante jusqu'à-ce qu'un autre broker essaie de se connecter. Une fois la connexion établie,
cette méthode retournera le canal de communication : un `Channel`.

De son côté, le thread B appellera la méthode `connect`, en fournissant le nom de l'autre broker et un port sur lequel
se connecter. Le port utilisé n'est considéré comme occupé que pour le broker qui a appelé `accept`. Il est donc
possible pour un broker d'utiliser plusieurs fois le même port pour des appels à `connect`.

De la même manière que pour la méthode `accept`, la méthode `connect` est bloquante jusqu'à-ce que l'autre broker ait
appelé `accept` sur ce port, et elle retournera un objet `Channel` lorsque la connexion sera établie.

Les méthodes `accept` et `connect` peuvent être appelées dans n'importe quel ordre.

### Les Channels

Les canaux obtenus comme décris précédemment permettent aux deux threads concernés de s'échanger des flux d'octets en
FIFO lossless.

Contrairement à l'établissement de la connexion, le canal est bidirectionnel, et se comporte de manière parfaitement
identique des deux côtés.

La méthode `write` du channel permet d'envoyer un flux d'octets de l'autre côté. Elle attend un tableau d'octets à
envoyer, un décalage dans le flux (qui correspond à l'indice dans le tableau à partir duquel on va chercher les octets à
envoyer), et un nombre d'octets à envoyer. Il peut arriver qu'on ne parvienne pas à envoyer tous les octets, c'est
pourquoi cette méthode renvoie le nombre d'octets qui ont effectivement été envoyés.
Cette méthode n'est bloquante que si elle n'arrive pas à envoyer le moindre octet immédiatement.

La méthode `read` permet de lire un flux d'octets provenant de l'autre côté. Elle attend un tableau dans lequel stocker
les octets lus, un décalage dans ce tableau (qui correspond à l'indice dans le tableau à partir duquel on va commencer à
écrire), et un nombre d'octets à lire. Comme pour l'écriture, il est possible qu'on ne puisse pas lire le maximum
d'octets demandés, cette méthode renvoie donc le nombre d'octets effectivement lus. Cette méthode est bloquante tant
qu'il n'y a rien à lire (c'est-à-dire jusqu'à-ce que l'autre côté écrive quelque chose).

Il est possible de fermer la connexion via la méthode `disconnect` du channel. Cette fermeture est unilatérale : inutile
de fermer des deux cotés. Si une entité ferme la connexion alors que l'autre est en train de lire ou écrire, la
connexion restera ouverte jusqu'à la fin de cette opération, puis elle se fermera. La méthode `disconnect` est alors
bloquante.

La méthode `isdisconnected` indique le statut de la connexion.

Il n'est pas possible de lire ou d'écrire sur une connexion fermée : les méthodes renverront une valeur spéciale (-1)
indiquant cela. Les méthodes `read` et `write` ne sont alors plus bloquantes.

## Design

### Broker

- Utilisation d'un paterne de rendez-vous pour communiquer avec les autres brokers.
- Un objet de RDV est identifié par une clé : concaténation du nom et du port, séparés par un underscore.
- Le premier broker à appeler une méthode `accept` ou `connect` crée l'objet de RDV puis se met en attente.
- Le deuxième broker trouve l'objet de RDV et réveille le premier.
- L'objet RDV instancie les circular buffers (voir section sur les `channels`), ce sont ces objets qui sont partagés
  entre les threads.
- Une fois le premier broker réveillé, les deux brokers instancient leur `channel` en récupérant les buffers dans
  l'objet de RDV.

### Rendez-vous

- Singleton contenant une hashmap permettant de stocker les objets de RDV.
- Un broker qui appelle `connect` ou `accept` après l'autre viendra chercher son objet RDV dans cette table.
- Lorsqu'une connexion est établie, l'objet de RDV est conservé dans la table. Les channels viendront la chercher pour
  confirmer que la connexion est toujours active.
- La fermeture d'une connexion consiste donc à supprimer l'objet de la table.

### Channel

- Deux buffers circulaires par channel : un input et un output. Ces buffers sont partagés et l'input de l'un est
  l'output de l'autre.
- Les buffers agissent donc comme un canal unidirectionnel, c'est pourquoi on en a deux.
- Les méthodes `read` et `write` sont synchronisés via le buffer concerné : input pour `read`, output pour `write`.
  Utilisation de wait/notify pour le blocage.

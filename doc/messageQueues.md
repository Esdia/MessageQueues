# Message Queues

## Specifications

### Les QueueBrokers

Les QueueBrokers fonctionnent de manière parfaitement identique aux [Brokers des Channels](channels.md#les-brokers) à
ceci près qu'ils retournent une `MessageQueue` au lieu d'un `Channel`.

### Établissement de la connexion

Puisque les Brokers sont similaires à ceux des Channels, l'établissement de la connexion fonctionne également
de [la même manière](channels.md#tablissement-de-la-connexion).

### Les MessageQueues

Les queues de messages sont des canaux permettant à deux threads de s'échanger des messages sous forme d'ensemble
d'octets. Elles sont bidirectionnelles et garantissent un échange en FIFO sans pertes.

Les méthodes de la classe `MessageQueue` sont toutes thread safe.

La méthode `send` permet d'envoyer un message de manière atomique. Elle attend un message sous forme de tableau
d'octets, un décalage dans ce tableau (l'indice à partir duquel on va chercher les octets à envoyer), et la taille du
message. Contrairement à la méthode `write` des [channels](channels.md#les-channels), cette méthode garantit d'envoyer
l'entièreté du message. Cette méthode est bloquante si besoin.

La méthode `receive` permet de recevoir un message de manière atomique. Elle ne prend aucun argument et renvoie le
message reçu sous forme d'ensemble d'octets. Cette méthode est bloquante jusqu'à-ce qu'un message ait été reçu dans son
intégralité.

Les méthodes `close` et `closed` sont les équivalents respectifs de `disconnect` et `isdisconnected`
des [channels](channels.md#les-channels). Elles fonctionnent de manière parfaitement identique.

## Design

### QueueBroker

- Création d'un Broker de channel dans le constructeur
- Pour chaque méthode : appel des méthodes correspondantes dans le Broker de channel.
- Création de la MessageQueue en fonction du Channel créé

### MessageQueue

- Le constructeur prend un `Channel`.
- `close` et `closed` : Appel de la méthode équivalente dans le `Channel`.
- `Write` : Ajout d'un entête de 4 octets indiquant la taille du message puis `channel.write` en boucle en changeant
  l'offset et la taille au fur et à mesure jusqu'à-ce que tout soit envoyé.
- `Read` : Lecture de 4 octets pour lire la taille, puis lecture en boucle en changeant l'offset et la taille au fur et
  à mesure jusqu'à-ce que tout soit lu.
- `synchronize` sur toutes les méthodes.
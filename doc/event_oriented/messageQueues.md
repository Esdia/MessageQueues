# Message Queues

Passage du paradigme orienté Thread au paradigme orienté événement pour les MessageQueues

## Specifications

Le but de cette bibliothèque est de proposer une communication entre threads basée sur une programmation événementielle.
Puisqu'elle est basée sur ce paradigme, aucun appel n'est bloquant.

Comme dans la [version précédente](../thread_oriented/messageQueues.md), l'envoi et la réception des messages sont atomiques, et permettent donc à une application utilisant cette bibliothèque d'être multi-threadée.

### Établissement de la connexion

Pour établir une connexion, chaque thread doit instancier une implémentation de QueueBroker (QueueBrokerImpl) en fournissant au constructeur un nom qui identifiera le broker de manière unique.

Un même broker peut être utilisé pour former plusieurs connexions, mais un même broker ne doit être utilisé que par un seul thread.

L'établissement de la connexion est asymétrique. Un thread attendra une connexion entrante, l'autre créera une connexion sortante.

Pour mettre le broker en attente d'une connexion entrante, il faut appeler sa méthode `bind(port, listener)`. Le port fourni indique sur quel port la connexion se créera. Un port ne peut être utilisé qu'une fois par connexion entrante à la fois. Si vous souhaitez réutiliser un port, il faudra d'abord fermer la connexion qui l'occupe. Le listener fourni doit être une implémentation de l'interface `AcceptListener`. Cette interface expose une méthode `accepted(port, queue)` qui sera appelée automatiquement lorsque la connexion sera établie, le paramètre `queue` étant l'objet qui sera utilisé pour communiquer, et le `port` étant celui fourni lors de l'appel à bind.

Pour faire émettre une connexion sortante de son broker, il faut appeler sa méthode `connect(name, port, listener)`. Le paramètre `name` permet d'identifier le broker auquel on souhaite se connecter. Le paramètre `port` a la même fonction que précédemment, à ceci près que dans le cas de connexions sortantes, on n'est pas limité à une par port. Enfin, le `listener` doit être une implémentation de l'interface `ConnectListener`, qui expose une méthode `connected(name, port, queue)` qui sera appelée automatiquement lorsque la connexion sera établie, les paramètres `name` et `port` étant ceux fournis à la méthode `connect`, et la queue étant l'objet utilisé pour communiquer. Cette interface expose également une méthode `refused(name, port)` qui sera appelée si la connexion ne peut s'établir.

### Utilisation de la MessageQueue

Une fois la connexion établie, le système devient parfaitement symétrique : la queue est bidirectionnelle, et se comporte de la même façon quel que soit le côté où on se trouve.

Pour pouvoir utiliser la queue correctement, il faut lui fournir une instance d'une implémentation de l'interface `MessageListener` au moyen de la méthode `setListener(listener)`. Cette interface expose trois méthodes, qui seront appelées lors d'un événement :
- La méthode `received` sera appelée lorsque la queue en question aura reçu un message. Elle prend en paramètre le message reçu sous forme d'un tableau d'octets
- La méthode `sent` sera appelée lorsque la queue en question aura émis un message. Elle prend en paramètres le message envoyé sous forme d'un tableau d'octets, ainsi que des informations sur ce qui a été envoyé dans ce tableau (les paramètres `offset` et `length`)
- La méthode `closed` sera appelée lorsque la connexion sera fermée 

La méthode `send(bytes)` permet d'envoyer un message sous forme de tableau d'octets. Comme mentionné précédemment, une fois le message envoyé, la méthode `sent` du listener sera appelée.
Cette méthode ne fait **pas** de copie du tableau en entrée, il est donc important de ne pas le modifier avant d'avoir reçu l'événement d'envoi, sous peine d'envoyer des données erronées.

La méthode `send(bytes, offset, length)` fonctionne comme la méthode précédente, mais elle permet de préciser un offset dans le tableau, ainsi qu'un nombre d'octets à envoyer

La méthode `close()` permet de fermer la connexion. Une fois la connexion effectivement fermée, la méthode `closed` du listener sera appelée.

La méthode `closed()` permet de consulter l'état de la connexion. Attention, puisqu'on est basé sur un modèle événementiel, rien ne garantit qu'un appel à `closed` renverra `true` immédiatement après un appel à `close`.


## Design

### Événements
- Une classe abstraite `Event` qui regroupe tous les événements, implémentée autant de fois qu'il y a d'événement différent (SendEvent, ReceiveEvent, etc...)

### Exécuteur d'événements
- Une classe `Executor` chargée de l'exécution des événements.
- Cette classe est un singleton qui possède en attribut une file qui stocke les événements, permettant une gestion FIFO qui garantit que les événements seront tous exécutés, dans l'ordre où ils ont été posés.
- Un thread exécuteur va boucler sur la file : si la file d'événements n'est pas vide, on défile, on traite, et on recommence. Si la file est vide, on fait `wait()`. Synchronisation sur la file.
- Pour initier ce thread exécuteur, il faut appeler la méthode `startLoop` de la classe `Executor`. Il est possible de poser des événements avant, mais aucun événement ne sera traité tant que la boucle n'est pas démarrée.
- À l'inverse, la méthode `stopLoop` interrompt la boucle.
- Pour poser un événement, la classe émettrice crée l'événement et appelle la méthode `putEvent` de l'instance de l'exécuteur. Si la file était vide à ce moment, cette méthode appelle également `notify()` pour réveiller l'exécuteur.
- Pour chaque type d'événement, il y a une méthode `handle<type>Event` dans la classe `Executor`, qui va appeler une méthode "privée" de la classe concernée. Par exemple, pour un envoi de message, la classe `MessageQueue` a une méthode `sendInternal` qui contient la logique de l'envoi, l'exécuteur ne fait que l'appeler.
- Pour gérer l'encapsulation de ces méthodes "privées", les classes `MessageQueue` et `QueueBroker` implémentent respectivement l'interface `MessageQueueInternal` et l'interface `QueueBrokerInternal` qui exposent les méthodes en question.
- À cause de la nature bloquante des channels, chaque méthode de traitement d'événement est appelée dans un thread créé pour ça, les événements sont donc traités en parallèle.

### QueueBroker et MessageQueue
- Comme mentionné précédemment, les logiques sont déplacées dans les méthodes `internal` (`sendInternal`, `connectInternal`, ...) appelées par l'exécuteur d'événement. Les méthodes "classiques" (`send`, `receive`, `connect`, ...) qui ont vocation à être appelées par le code applicatif ne vont en fait rien faire de plus que créer l'événement associé, et le transmettre à l'exécuteur au moyen de sa méthode `putEvent`
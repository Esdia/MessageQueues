# Circular Buffer

## Spécifications

Un circular buffer est un buffer permettant de stocker des octets en FIFO.

On crée un circular buffer en instanciant la classe en fournissant une capacité maximum. Cette valeur correspond au nombre maximum d'octets que ce buffer peut contenir. 

- La méthode `put` permet d'insérer un octet dans le buffer. Si on essaie d'ajouter un octet dans un buffer plein, une exception est levée.
- La méthode `get` permet de lire un octet dans le buffer. Puisque le buffer est FIFO, cela correspond au premier octet inséré. Une fois qu'un octet est lu, il est retiré du buffer. Une place est alors libérée et le prochain appel à get retournera l'octet suivant. Si on essaie de lire dans un buffer vide, une exception est levée.
- La méthode `full` permet de savoir si le buffer est plein.
- La méthode `empty` permet de savoir si le buffer est vide.


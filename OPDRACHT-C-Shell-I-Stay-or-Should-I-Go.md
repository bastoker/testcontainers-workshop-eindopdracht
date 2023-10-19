# Shell I stay or should I go?
https://www.youtube.com/watch?v=xMaE6toi4mk

# Opdracht
Doel van deze opdracht is om in het File System van draaiende containers in te breken,
zodat je bijvoorbeeld kan zien hoe een applicatie is geconfigureerd.

Dit is ook handig om eenmalinge tools te gebruiken, bijv een database-client zoals PSQL.

Of bijv. logfiles te kunnen bekijken, als de applicatie niet alles
naar System Output of System Error streams wegschrijft. 
(Deze twee standaard outputstreams worden standaard door ``docker logs al weergegeven``)

## Stap 1
Zorg ervoor dat de applicatie (nog of weer) draait, volgens de stappen van Opdracht A.
En zorg ervoor dat er een paar vakanties zijn opgevoerd in de applicatie, als testdata, zodat de 
database gevuld is.

## Stap 2
Breek in in the SHELL van de docker container van de database (dit is de container van het image ``postgres:15`).

TIP: gebruik het Docker-commando ``docker exec -it <container-id> psql -h localhost -p 5432 -d standalone-db -U admin``
TIP-2: het wachtwoord van de admin db user is ``admin``

Voer vervolgens SQL-queries uit om de data te bekijken die je via de UI zelf hebt ingevoerd.

## Stap 3
Gefeliciteerd, dit was de laatste opdracht van de workshop!

Bedenk dat als je tests wilt scripten, dat elk docker commando via testcontainers
ook vanuit je testscript kan worden uitgevoerd.
Dit geeft veel mogelijkheden om dingen te testen die voorheen lastig waren, het testen
van logging-output is een goed voorbeeld.

Veel testplezier met Testcontainers!



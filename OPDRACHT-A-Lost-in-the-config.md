# Lost in the config
https://www.youtube.com/watch?v=68dk01GkxDI

# Opdracht
Doel van deze opdracht is de applicatie draaiend krijgen in Stand-alone modus.
Dit is dus niet als onderdeel van een geautomatiseerde test, maar om zelf live door de applicatie
te kunnen klikken, en wat exploratory te kunnen testen bijvoorbeeld.

## Stap 1
Bedenk in welke volgorde je de verschillende onderdelen wilt (of moet) starten:
 - StartApplicationStandalone (dit onderdeel heeft nog extra configuratie nodig)
 - StartDatabaseStandalone
 - StartFrontendStandalone
 - StartKeycloakStandalone

Deze processen staan in de folder ``src/main/java/standalone``.

Vraag: Welk onderdeel draait niet in Docker?
Zet je antwoord in het bestand ``antwoorden.txt``

## Stap 2
De extra configuratie voor de StartApplicationStandalone (de backend)
staat in het bestand ``application-standalone.yaml``.

Start nu de verschillende onderdelen in de (hierboven bedachte) volgorde en pas waar nodig
op het juiste moment deze configuratie van de applicaties aan.

TIP: met het commando ``docker ps`` kan je informatie opvragen over draaiende containers.

## Stap 3
Klik wat rond in de applicatie en zoek je eerste bug :-)

## Afronden opdracht
Voor deze opdracht hoef je alleen de config-aanpassingen te committen en het bestand ``antwoorden.txt``.

## Na afloop
Voeg deze wijziging door met met een ``git commit`` en ``git push`` naar deze Git-repo. Je kan de test ook in de UI
van GitHub.com zelf aanpassen.

Check na afloop even of alle Java-processen echt zijn gestopt.
Je kan ook altijd even checken of al je containers zijn gestopt met het 
het commando ``docker ps``.

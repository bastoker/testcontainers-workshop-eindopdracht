# testcontainers-workshop

<p align="middle">
    <img src="logo.svg" height="180"/>
    <img src="testcontainers-logo.svg" height="180"/>
</p>    

## "De VakantiePlanner"

Tijdens de workshop werken we aan het testen van een VakantiePlanner.
Met deze VakantiePlanner kunnen collega's onderling hun vakantie afstemmen.
Want we verdienen allemaal weleens een dagje vrij, toch?

<img src="holiday.jpg" width="500" />

## High level architectuur
De architectuur ziet er in grote lijnen zo uit:

```mermaid
graph LR;
    HPT_UI[Web UI]-->HPT_backend
    HPT_UI[Web UI]-->Keycloak
    HPT_backend-->PostgreSQL_hpt[(HPT DB)];
    HPT_backend-->Keycloak;
    Keycloak-->PostgreSQL_keycloak[(Keycloak DB)];
```

De authenticatie werkt via Keycloak. Keycloak is een veelgebruikte authenticatie-oplossing.

De VakantiePlanner zelf gebruikt ook een Postgres database, maar deze instantie staat helemaal
los van de database van Keycloak.
s
De UI communiceert alleen met de VakantiePlanner-backend en met Keuycloak voor het inloggen.
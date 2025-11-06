# Documentatie: Gemeente Overzicht Pagina

## Wat is dit?

Dit is de hoofdpagina voor de gemeentelijke uitslagen (`MunicipalityElectionResults.vue`). Het is in principe het startpunt waar de gebruiker kan zien welke gemeenten er allemaal in de data zitten.

Je kunt hem in de app vinden via de "Gemeenten" link in de navigatiebalk.

## Hoe werkt het?


### 1. Data Ophalen (de "Script" kant)

* Zodra de pagina laadt (`onMounted`), roept 'ie meteen de functie `getMunicipalityNames` aan.
* Die functie (uit `MunicipalityElectionResults_api.ts`) doet een API-call naar de backend. Specifiek naar het endpoint `/api/elections/TK2023/regions/gemeenten`.
* De backend stuurt dan een lijst terug van alle "regio's" die de categorie 'GEMEENTE' hebben.
* Mijn code pakt die lijst en filtert hem zodat we alleen een simpele array met namen overhouden (bijv. ["Amsterdam", "Utrecht", "'s-Hertogenbosch", ...]).

### 2. Feedback aan de Gebruiker

* Terwijl de data wordt geladen, laat ik een laad-indicator zien ("Lijst van gemeenten wordt geladen...") zodat de gebruiker weet dat er iets gebeurt.
* Als de API-call mislukt (bijv. backend is down), toon ik een duidelijke foutmelding op de pagina.

### 3. Wat je Ziet (de "Template" kant)

* Bovenaan staat een simpele titel: "Gemeentelijke Uitslagen".
* De *echte* content is een `v-for` loop die over de lijst met gemeentenamen gaat.
* Voor elke gemeente in de lijst maak ik een "kaart" (`.municipality-card`).
* Op die kaart staan twee dingen:
    1.  **Een Icoon:** Dit is gewoon een blauwe cirkel (`.municipality-icon`) met daarin de **eerste letter** van de gemeentenaam. Lekker simpel, maar ziet er uniform uit.
    2.  **De Naam:** Gewoon de volledige naam van de gemeente (bijv. "Bonaire").

### 4. Interactie (Wat kun je doen?)

* Elke kaart is klikbaar. Ik heb er een `@click` event op gezet.
* Als je klikt, wordt de functie `goToMunicipalityDetails` aangeroepen en die krijgt de naam van de gemeente mee.
* **BELANGRIJK:** Dit is op dit moment een **placeholder**! De functie `goToMunicipalityDetails` opent nu alleen een `alert` die zegt: "De detailpagina... komt binnenkort". De volgende stap is om dit te laten navigeren naar een échte detailpagina voor die gemeente.

## Design (Styling)

* Ik heb een grid-layout (`.municipality-grid`) gebruikt, zodat de kaarten mooi naast elkaar vloeien en zich aanpassen aan de schermgrootte.
* De hele pagina heeft een lichte gradient-achtergrond om het wat minder saai te maken.
* De kaarten hebben een hover-effect (schaduw en een lichte beweging) zodat de gebruiker *voelt* dat je erop kunt klikken.
![](Vergelijking.jpg)
### Partijen Pagina Documentatie

Dit document beschrijft de functionaliteit en het ontwerp van de partijenpagina, specifiek de vergelijkingsfunctie, zoals weergegeven is in de afbeelding.


### Componenten

De pagina is opgebouwd uit verschillende hoofdonderdelen:  een centrale vergelijkingsgrafiek en een selectiepaneel voor partijen onderaan.

#### Vergelijkingsgrafiek

Het hoofdonderdeel van de pagina is een horizontale staafgrafiek die de geselecteerde partijen vergelijkt.

* **Titel:** De grafiek heeft een titel "Vergelijking (2 / 2)", wat suggereert dat er een limiet is (waarschijnlijk 2) op het aantal partijen dat tegelijkertijd vergeleken kan worden.
* **Grafiek Titel:** Een subtitel "Stemmen" geeft aan welke data wordt gevisualiseerd.
* **Dataweergave:** Voor elke geselecteerde partij wordt een horizontale staaf getoond.
    * **Partijnaam:** De afkorting van de partij (bijv. "VVD", "D66") staat op de Y-as.
    * **Staafkleur:** Elke partij heeft een eigen onderscheidende kleur (bijv. oranje voor VVD, groen voor D66) die consistent wordt gebruikt.
    * **Waarde:** De lengte van de staaf correspondeert met het totale aantal stemmen, af te lezen op de X-as.
* **X-as:** Een numerieke as onderaan de grafiek toont de schaal van het aantal stemmen (bijv. van 0 tot 1.600.000).

#### Partijselectie Kaarten

Onder de grafiek bevindt zich een horizontaal scroll-baar (impliciet) paneel met kaarten voor alle beschikbare partijen.

* **Subtitel:** Boven de kaarten staat een informatieve tekst: "26 partijen geregistreerd voor TK2023".
* **Partij Kaart:** Elke kaart vertegenwoordigt één partij en bevat:
    * **Partijlogo:** Een prominente, gekleurde cirkel met een afkorting of initiaal (bijv. "VV", "D6", "G/").
    * **Partijnaam:** De volledige naam of officiële afkorting van de partij (bijv. "VVD", "D66", "GROENLINKS / Partij van de Arbeid (PvdA)").
* **Selectiestatus:** De kaarten hebben een duidelijke visuele status:
    * **Geselecteerd:** De kaart heeft een dikke, gekleurde rand die overeenkomt met de partijkleur, en een vinkje onderaan.
    * **Niet-geselecteerd:** De kaart heeft een neutrale achtergrond (lichtroze/grijs in het voorbeeld).



### Functionaliteit

* **Partijen Vergelijken:** De kernfunctionaliteit is het selecteren en vergelijken van politieke partijen op basis van hun totale stemmenaantal.
* **Interactieve Selectie:** Gebruikers kunnen partijen (de)selecteren door op de kaarten in het onderste paneel te klikken. De afbeelding impliceert dat er maximaal 2 partijen tegelijk geselecteerd kunnen worden ("2 / 2").
* **Dynamische Grafiek:** De vergelijkingsgrafiek wordt direct bijgewerkt op basis van de selectie van de gebruiker, waardoor een onmiddellijke visuele vergelijking mogelijk is.
* **Navigatie:** De hoofdnavigatiebalk bovenaan (met links als "Admin", "Gemeenten", "Landelijk", "Kandidaten", etc.) stelt de gebruiker in staat om naar andere onderdelen van de applicatie te navigeren. De "Partijen" link is momenteel actief.



### Ontwerpoverwegingen

* **Duidelijkheid:** Het ontwerp is helder en overzichtelijk. De focus ligt direct op de vergelijkingsgrafiek, wat het hoofddoel van de pagina is.
* **Consistent Kleurgebruik:** Partijkleuren worden consistent toegepast in de logo's, de selectieranden van de kaarten, en de staven in de grafiek. Dit versterkt de visuele koppeling en herkenbaarheid.
* **Visuele Feedback:** De statusverandering van de kaarten (gekleurde rand, vinkje) en de update van de grafiek geven duidelijke en onmiddellijke feedback op de acties van de gebruiker.
* **Data-visualisatie:** Een horizontale staafgrafiek is een zeer effectieve en makkelijk te interpreteren keuze voor het vergelijken van totalen (stemmen) tussen een klein aantal categorieën (partijen).
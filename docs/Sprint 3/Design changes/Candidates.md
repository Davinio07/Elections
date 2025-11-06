![](candidates.jpg)
# Kandidaten Pagina Documentatie

Dit document beschrijft de functionaliteit en het ontwerp van de **Kandidatenpagina**, met nadruk op de filtermogelijkheden en de weergave van kandidaten.

---

## Overzicht

De Kandidatenpagina toont een overzicht van alle beschikbare kandidaten.  
Gebruikers kunnen eenvoudig filteren op verkiezing, partij en geslacht, en krijgen vervolgens een duidelijk visueel overzicht van de bijbehorende kandidaten.

De pagina bestaat uit twee hoofdonderdelen:
- **Filtersectie** bovenaan de pagina
- **Kandidatenoverzicht** in een grid-weergave

---

## Filtersectie

De filters vormen het belangrijkste interactie-element van de pagina.  
Ze helpen gebruikers om snel te navigeren tussen verschillende partijen en kandidaten.

### Beschikbare Filters

| Filter | Type | Beschrijving |
|--------|------|--------------|
| **Partij** | Dropdown | Toont alleen de partijen die horen bij de gekozen verkiezing. Wanneer je een partij selecteert, worden alleen de kandidaten van die partij getoond. |
| **Geslacht** | Knoppen | Hiermee kun je snel filteren op mannelijke of vrouwelijke kandidaten. |

De filters werken soepel samen. Een wijziging in de verkiezing past automatisch de lijst met partijen aan, en de lijst met kandidaten wordt steeds in real time bijgewerkt.

---

## Kandidatenoverzicht

Na het instellen van de gewenste filters worden de kandidaten weergegeven in een overzichtelijke **grid-weergave**.

### Opbouw van een Kandidaatkaart

Elke kandidaat wordt weergegeven als een afzonderlijke **kaart** met een eenvoudige en consistente opmaak:

- **Initiaal:** Bovenaan staat een grote letter — de eerste letter van de naam van de kandidaat.
- **Naam en Woonplaats:** De volledige naam en woonplaats worden duidelijk getoond.
- **Geslacht:** Vermeld onder de naam.
- **Actieknop:** De knop **“Meer info”** opent een pop-upvenster met alle beschikbare gegevens van de kandidaat.

Deze visuele indeling maakt het makkelijk om snel kandidaten te herkennen en informatie te vinden.

---

## Functionaliteit en Interactie

- **Dynamisch Filteren:** Zodra een gebruiker een filter aanpast, wordt de lijst met kandidaten direct bijgewerkt zonder dat de pagina opnieuw geladen hoeft te worden.
- **Snelle Navigatie:** De combinatie van dropdowns en knoppen zorgt voor een soepele gebruikerservaring.
- **Overzichtelijkheid:** Door de grid-opmaak blijft de lay-out overzichtelijk, zelfs bij grote aantallen kandidaten.
- **Modal Weergave:** De “Meer info”-knop opent extra details zonder de gebruiker naar een andere pagina te sturen.

---

## Ontwerpoverwegingen

- **Gebruiksvriendelijkheid:** De filters en kaarten zijn ontworpen om intuïtief te werken, ook voor gebruikers zonder technische kennis.
- **Snelheid:** Alle gegevens komen rechtstreeks uit de database, waardoor het laden en filteren snel verloopt.
- **Consistente Opmaak:** De kleuren, lettertypes en knoppen zijn consistent toegepast binnen de gehele applicatie.
- **Toegankelijkheid:** De knoppen en kaarten zijn groot genoeg voor gebruik op zowel desktop als mobiel.

---

## Samenvatting

De Kandidatenpagina biedt een **duidelijk, interactief overzicht** van kandidaten.  
Gebruikers kunnen eenvoudig filteren op partij en geslacht, en met één klik meer informatie bekijken.  
Het resultaat is een overzichtelijke en moderne interface die politieke kandidaten op een toegankelijke manier presenteert.

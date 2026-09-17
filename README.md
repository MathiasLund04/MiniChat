## Gruppe
Gruppe 6, Null & Void: Mathias lund, Henriette Larsen, Sofie Jørgensen, Oliver Ellegaard

### Vejledning på start af server og klient
For at starte chatprogrammet skal der bruges en server og en klient. Først skal der tjekkes at begge parter bruger den samme port for at forbinde til hinanden, derefter starter man først ServerApplication og derefter ClientApplication. 
Der er en fast liste af allerede tilføjede brugere som man kan logge ind som:
”alice”, ”bob”, ”charlie”
### Beskrivelse af protokol
Protokollen i dette program er sat op på den måde at klienten viser brugeren nogle kommandoer de kan vælge i mellem såsom ’ROOMS’, ’TEXT’, ’JOIN_ROOM’, ’PRIVATE’ og ’QUIT’. Systemet sørger for at man får en fejlbesked hvis én af disse ikke er blevet valgt. Derefter kan de vælge, ud fra disse 3, ’TEXT’, ’JOIN_ROOM’, ’PRIVATE’, at skrive:
-	TEXT – en tekstbesked ud fra hvilket rum de er i
-	JOIN_ROOM – hvilket rum de vil tilmelde sig
-	PRIVATE – en privat besked til en anden bruger der er logget på.
Systemet sørger selv at formatere beskederne så brugeren ikke selv behøver at skrive | mellem de forskellige felter.
Serveren sender tilbage en formateret besked med tidsangivelse, hvilken form for kommando der er blevet brugt, afsender, modtager og hvad de fik sendt ind. Dette er gjort i formatet: TIMESTAMP|TYPE|SENDER|TARGET|PAYLOAD 
### Forklaring af trådmodellen og de delte ressourcer
Trådmodellen:
Serveren bruger tråde i form af thread pool med ExecutorService. Dette program er i stand til at kunne bruge mindst 3 worker-tråde i thread-pool. Når klienten forbinder til serveren, oprettes der en Clienthandler til den, og den implementerer Runnable, der gør den i stand til at arbejde med disse tråde. Clienthandleren sendes derfor til ExecutorService (inde i ChatServer) hvor en ledig worker-tråd arbejder med klientens forbindelse.
Dette gør at serveren kan håndtere flere klienter på en gang uden at man manuelt skal oprette en enkelt ny tråd for hver klient.
På den anden side bruger klienten også nogle tråde, en til at håndtere input fra brugeren og en anden tråd til at håndtere beskeder fra serverens side af. Dette gør klienten i stand til at modtage beskeder og samtidig vente på input fra brugeren.

### Delte ressourcer:
ClientHandler-trådene skal kunne have adgang til fælles information om klienterne og chatrummene. Derfor bliver ClientRegistry(ClientRepository) og ChatRoomManager(RoomRepository) delt mellem alle ClientHandler i form af static.
ClientRepository holder styr på de tilsluttede klienter mens RoomRepository holder styr på de eksisterende rum.
Da der er flere ClientHandler-tråde der skal kunne tilgå disse ressourcer på samme tid, skal det håndteres på en sikker måde. Inde i RoomRepository bruges der ConcurrentHashMap som holder sig opdateret på de forskellige rum og tilsluttede bruger (til rummet).
Beskrivelse af valgt udvidelse
Vi havde tænkt at indsætte ”en udvidet oversigt over aktive brugere, chatrum og brugernes status ” da vi mente at det ville få vores program til bedre at ligne et chatprogram. Ved at kunne give en pænere oversigt over de forskellige rum og de brugere der befinder sig i selve programmet, ville det gøre det mere overskueligt at kunne sende en besked til en anden/et chatrum, ligesom med andre chat-platformer såsom discord, messenger og whatsapp.

### AI-Dokumentation

| Opgave        | AI-Værktøj | AI's Forslag | Jeres vurdering og ændringer | kontrol og test |
| ------- |:-----------:| -----:| --- | --- |
| Issue#3 – tilføj flere klienter	| copilot | Opdatere nuværende kode så det kan håndtere flere klienter på samme tid | Godkendt delvist. Dens forslag var godt men manglede en while lykke så man kunne skrive mere en 1 besked | Manuel kontrol og test af programmet |
| Issue#5 -Tilføjelse af Chatrum og private beskeder      | Copilot      |  Udvide protokollen med Join_room/Private kommandoer. Implementere maneger til rummene, søge på bruger, diverse valideringer | Godkendt.  Implementerede alle kravne for issuet som det skulle uden nogle fejl | Copilot egen test, samt manuel kontrol og test |
| Re-strukturering  | Copilot      |    Den kom med et forslag til en struktur der blander MVC, 3-lags modelen og clean architecture | Efter flere iterationer hvor vi ændrede kravne kom den frem til en struktur som vi godkendte og som ikke ødelage funktionaliteten af programmet | Copilot lavede en socket-smoke-test af funktunaliteten samt manuel kontrol og test|



### Diagrammer


Klassediagram

Link til diagram: https://mermaid.ai/d/c917d5f7-2359-466c-8bb4-37254cf8054d 
<img width="8192" height="4523" alt="MiniChat-KlasseDiagram" src="https://github.com/user-attachments/assets/ad461c2d-277c-4024-b7f6-15ee3c78a110" />

Sekvensdiagram

login:
 Link til diagram: https://mermaid.ai/d/fc939908-f29d-4b42-9ff8-d314c9ed413b 
<img width="8055" height="6815" alt="MiniChat-Login" src="https://github.com/user-attachments/assets/c2bb0faf-c06a-49c6-ace1-5beb330216ba" />


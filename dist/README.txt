Afhankelijkheden
----------------

Java 1.7 runtime, zit mee op de usb stick in de jre7 directory. 

Installatie
-----------

* kopieer alle bestanden in een directory
* start.bat start het programma.

Database
--------

* Programma maakt gebruikt van een embedded sql database (http://www.h2database.com/html/main.html)
* De database wordt aangemaakt in de home directory van de momenteel ingelogde gebruiker. Standaard is dat 
  * windows7: c:\users\(gebruikersnaam)\.hallway
  * windows xp c:\documentsandsettings\(gebruikersnaam)\.hallway
* Elke keer dat het programma opstart wordt er een backup van de database gemaakt in c:\users\(gebruikersnaam)\.hallway\backups
* Het programma onderhoud het schema van de database zelf.

Broncode
--------

Broncode is beschikbaar op https://github.com/jgeraerts/hallway
Voor de applicatie te bouwen heb je leiningen build tool nodig (https://github.com/technomancy/leiningen)

"lein uberjar" bouwt dan een jar met de code van het programma plus alle 3th party libraries. 

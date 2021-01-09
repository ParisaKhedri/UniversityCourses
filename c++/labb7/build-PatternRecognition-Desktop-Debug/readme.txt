/**********************************************************************
 *  Mönsterigenkänning readme.txt
 **********************************************************************/

/**********************************************************************
 *  Empirisk    Fyll i tabellen nedan med riktiga körtider i sekunder
 *  analys      när det känns vettigt att vänta på hela beräkningen.
 *              Ge uppskattningar av körtiden i övriga fall.
 *
 **********************************************************************/
    
      N       brute       sortering
 ----------------------------------
    150        68		16
    200       161		26
    300       580		50
    400       1220	88
    800       9754  	335
   1600      78410	1300
   3200       fryser 	5284 
   6400	      fryser 	21958 
  12800      fryser	92484 


/**********************************************************************
 *  Teoretisk   Ge ordo-uttryck för värstafallstiden för programmen som
 *  analys      en funktion av N. Ge en kort motivering.
 *
 **********************************************************************/

Brute:O(N**4)
4 nästlade loppar som körs N gånger( börjar först på en tal och den ska finnas minst 3 punkter till för att man ska kunna dra ett linje på samma sätt om man redan har valt en och är på andra ska det finnas minst 2 pointer till. alltså brute hittar alla kombinationer av minst 4 punkter)

Sortering:O(N**2)
för varje möjlig start pkt skapar vi en map med slop som första värde och en vektor som inehåller alla point med samma slop till förregående point. och om der finns mer än 2 punkter i den vektorn drar vi en linje mella origo och sista pkt i vektorn

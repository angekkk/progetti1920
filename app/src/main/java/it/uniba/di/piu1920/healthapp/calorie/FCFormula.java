package it.uniba.di.piu1920.healthapp.calorie;


//https://www.projectinvictus.it/il-fabbisogno-calorico-giornaliero/


public class FCFormula {

    double total=0;

    double getFC(int sex, int eta, int cm,int peso){

        if(sex==0){//uomo
            total=(10*peso)+(6.25*cm)-(5*eta)+5;
            //aggiunto un 30% che comprende 10% dall’ADS(digestione) e 20% dall’attività
            total=total+(total*30/100);
        }else{//sex==1 donna
            total=(10*peso)+(6.25*cm)-(5*eta)-161;
            //aggiunto un 30% che comprende 10% dall’ADS(digestione) e 20% dall’attività
            total=total+(total*30/100);
        }
        return total;
    }


}

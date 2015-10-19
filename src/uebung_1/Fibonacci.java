package uebung_1;

/**
 * Created by Mikel Reinhardt, Alessandro Furkim
 * on 05.10.2015.
 */
public class Fibonacci {

    /**
     * die Methode berechnet rekursiv die Fibonacci Folge
     * @param zahl
     * @return fibonacci Folge
     */
    public static int fibo(int zahl){

        if(zahl == 0){
            return 0;
        } else if (zahl == 1) {
            return 1;
        } else {
            return fibo(zahl - 2) + fibo(zahl - 1);
        }
    }
}

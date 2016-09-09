package pl.jrj.game;

import java.util.ArrayList;
import java.util.Random;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Robert Matejczuk
 */
@Stateless
public class MasterMind implements IMasterMind {

    // Metoda ktora umozliwia polaczenie sie z ejb GameMonitor
    private IGameMonitor getGameMonitor() {
        try {
            Context c = new InitialContext();
            return (IGameMonitor) c.lookup("java:global/ejb-project/GameMonitor!pl.jrj.game.IGameMonitor");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

    /**
     * maksymalna dlugosc kombinacji znakow
     */
    public static final int K = 5;
    /**
     * Zbior elementow z ktorych powstaje kod do odgadniecia
     */
    public static final char[] INI = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
    /**
     * ArrayList przechowujacy wszystkie mozliwe kombinacje
     */
    public static ArrayList<String> kombinacje = new ArrayList<String>();
    /**
     * Pomocniczy ArrayList wykorzystywany w celu redukcji ilosci kombinacji
     * ktore zostaly odrzucone
     */
    public static ArrayList<String> tmpKombinacje = new ArrayList<String>();

    /**
     * Moja wlasna metoda Verify ktora porownuje dwa kody w celu odrzucenia
     * pozostalych ktore nie pasuja
     *
     * @param state kod ktory zostaje sprawdzony z kodem ktory byl tzw. strzalem
     * @param tmpKod kod ktory byl ostatnio wpisany jako tzw. strzal
     * @return zwraca wynik typu String w postaci np. 32 oznajacy, ze 3 kolory
     * sa wlasciwe oraz znajduja sie na poprawnym miejscu, natomiast 2 z nich sa
     * wlasciwymi kolorami ale nie leza w odpowiednim miejscu
     */
    public static String myVerify(String state, String tmpKod) {
        int trafione = 0;
        int kolor = 0;
        String newState = "";
        String newTmpKod = "";

        // Porownuje znaki rownoczesnie z dwoch kodow. Jesli sa takie same
        // zamienia je na X oraz zwieksza wartosc pola trafione
        // oznaczajacego, ze kolor jak i miejsce sa poprawne
        for (int i = 0; i < 5; i++) {
            if (tmpKod.charAt(i) == state.charAt(i)) {
                trafione++;
                newState = state.substring(0, i) + "X" + state.substring(i + 1);
                state = newState;
                newTmpKod = tmpKod.substring(0, i) + "X"
                        + tmpKod.substring(i + 1);
                tmpKod = newTmpKod;
            }
        }

        // Porownuje kazdy znak jednego kodu z drugim. Jesli trafi na X to 
        // pomija. Jesli trafi na takie same znaki zamienia je na X i zwieksza
        // wartosc pola kolor oznaczajacego, ze w zadanej kombinacji istnieje
        // poprawny kolor ale nie jest na swoim miejscu
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (tmpKod.charAt(i) == state.charAt(j)) {
                    if (Character.valueOf(tmpKod.charAt(i)).equals('X')
                            || Character.valueOf(state.charAt(j)).equals('X')) {
                        continue;
                    }
                    kolor++;
                    newState = state.substring(0, j) + "X"
                            + state.substring(j + 1);
                    state = newState;
                    newTmpKod = tmpKod.substring(0, i) + "X"
                            + tmpKod.substring(i + 1);
                    tmpKod = newTmpKod;
                    break;
                }
            }
        }
        return "" + trafione + kolor;
    }

    /**
     * Metoda ktora rozgrywa i rozwiazuje zadany kod na zasadach gry MasterMind
     * Algorytm rozwiazania: 1. Tworzy liste wszystkich mozliwych kombinacji:
     * 32768 2. Losuje jeden z mozliwych kodow sposrod puli mozliwych kombinacji
     * 3. Wywoluje metode verify i zapisuje zwrocony wynik do wVerify 4. Dla
     * wszystkich kombinacji z listy sprawdza aby dawaly taki sam wynik jaki
     * zostal zwrocony dla kombinacji ktora byla tzw. Strzalem za pomoca metody
     * myVerify 5. Wszystkie kombinacje ktore zwrocily taki sam wynik trafiaja
     * do tymczasowej listy tmpKombinacje. 6. Czyscimy liste kombinacje. 7.
     * Przepisujemy wszystkie elementy z listy tmpKombinacje do listy kombinacje
     * 8. Czyscimy liste tmpKombinacje 9. Zwiekszamy wartosc pola iloscPodejsc.
     * 10. Wykonujemy ten alborytm od punku 2 az wynik nie bedzie rowny 50
     *
     * @return
     */
    @Override
    public int rozegraj() {
        Random rand = new Random();
        IGameMonitor gameMonitor = getGameMonitor();
        String wVerify = "";
        int iloscPodejsc = 0;
        GeneratorKombinacjiString.wypiszKombinacje(INI, K, kombinacje);

        // poczatek while;
        while (!wVerify.equalsIgnoreCase("50")) {
            String strzal = kombinacje.get(rand.nextInt(kombinacje.size()));
            wVerify = gameMonitor.verify(strzal);
            for (String s : kombinacje) {
                if (myVerify(s, strzal).equalsIgnoreCase(wVerify)) {
                    tmpKombinacje.add(s);
                }
            }

            // Oczyszczamy ArrayList z niepotrzebnych mozliwosci
            kombinacje.clear();
            for (String s : tmpKombinacje) {
                kombinacje.add(s);
            }
            tmpKombinacje.clear();
            iloscPodejsc++;
        }
        return iloscPodejsc;
    }
}

/**
 * Klasa zawierajaca metody do generowania roznych kombinacji stringow
 *
 * @author Drake
 */
class GeneratorKombinacjiString {

    /**
     * Metoda ktora generuje wszystkie mozliwe ustawienia String o dlugosci K
     * jest to glowny opakowywacz rekurencyjnej metody wypiszKombinacjeRec()
     *
     * @param ini
     * @param k
     * @param kombinacje
     */
    public static void wypiszKombinacje(char[] ini, int k,
            ArrayList<String> kombinacje) {
        int n = ini.length;
        wypiszKombinacjeRec(ini, "", n, k, kombinacje);
    }

    /**
     * Glowna rekurencyjna metoda do wypisywania wszystkich mozliwych kombinacji
     * Stringow.
     *
     * @param ini
     * @param prefix
     * @param n
     * @param k
     * @param kombinacje
     */
    static void wypiszKombinacjeRec(char[] ini, String prefix, int n, int k,
            ArrayList<String> kombinacje) {
        // Base case: K is 0, print prefix
        if (k == 0) {
            kombinacje.add(prefix);
            return;
        }

        // Pojedynczo dodawaj wszystkie literki z INI i wywolaj rekurencje
        for (int i = 0; i < n; ++i) {

            // Następna literka jest dodawana do prefixu
            String newPrefix = prefix + ini[i];

            // K jest zmniejszane, poniewaz dodajemy nowa literke
            wypiszKombinacjeRec(ini, newPrefix, n, k - 1, kombinacje);
        }
    }
}

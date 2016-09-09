
import java.io.IOException;
import java.io.PrintWriter;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pl.jrj.game.IGameMonitor;
import pl.jrj.game.IMasterMind;

/**
 *
 * @author Robert Matejczuk
 */
@WebServlet(urlPatterns = {"/MGame"})
public class MGame extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Metoda ktora umozliwia polaczenie sie z ejb MasterMind
    private IMasterMind getMasterMind() {
        try {
            Context c = new InitialContext();
            return (IMasterMind) c.lookup("java:global/ejb-project/MasterMind!pl.jrj.game.IMasterMind");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }

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
     * Metoda ktora nawiazuje polaczenie z dwoma interfejsami i za ich pomoca
     * rozwiazuje zadanie
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        IGameMonitor gameMonitor = getGameMonitor();
        IMasterMind masterMind = getMasterMind();
        boolean wynik;
        // Rejestracja
        wynik = gameMonitor.register(5, "98045");
        // Jesli rejestracja sie powiodla mozemy przystapic do dalszego
        // dzialania
        if (wynik == true) {
            try (PrintWriter out = response.getWriter()) {
                // Zgodnie z specyfikacja rozpoczynamy gre za pomoca metody
                // initGame znajdujacej sie w GameMonitor.java
                gameMonitor.initGame(request.getParameter("m").toUpperCase());
                // Za pomoca metody rozegraj znajdujacej sie w MasterMind.java
                // przeprowadzamy rozgrywke i zwracamy wynik bedacy iloscia
                // krokow ktore musialy zostac wykonane w celu odszyfrowania
                // zadanego kodu
                out.println(masterMind.rozegraj());
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

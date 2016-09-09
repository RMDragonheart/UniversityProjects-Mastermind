package pl.jrj.game;

import javax.ejb.Remote;

/**
 * 
 * @author Robert Matejczuk
 */
@Remote
public interface IGameMonitor {

    /**
     * 
     * @param hwork
     * @param album
     * @return 
     */
    public boolean register(int hwork, String album);

    /**
     * 
     * @param state 
     */
    public void initGame(String state);

    /**
     * 
     * @param state
     * @return 
     */
    public String verify(String state);
}

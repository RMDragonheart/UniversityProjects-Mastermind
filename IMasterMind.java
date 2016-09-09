package pl.jrj.game;

import javax.ejb.Remote;

/**
 *
 * @author Robert Matejczuk
 */
@Remote
public interface IMasterMind {

    /**
     * 
     * @return 
     */
    public int rozegraj();

}

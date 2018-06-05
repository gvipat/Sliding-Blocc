import java.awt.Toolkit;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;


/**
 * The game class opens a title screen and opens the game.
 *
 * @author Roshan Sevalia
 * @version May 18, 2018
 * @author Period: 4
 * @author Assignment: APCSfinal
 *
 * @author Sources: Gaurav Vipat, Charles Huang
 */
public class Game
{
    /**
     * Main method
     * 
     * @param args
     *            command line arguments (s plays the game immediatley and d
     *            runs debug)
     */
    public static void main( String[] args )
    {
        ArrayList<String> arrrgs = new ArrayList<String>(Arrays.asList(args));
        if ( arrrgs.contains( "-d" ) )
        {
            Engine.DEBUG_MODE = true;
        }
        if (arrrgs.contains("-t"))
        {
            Engine.TEXTURES_ENABLED = false;
        }
        if ( arrrgs.contains( "-s" ) )
        {
            playGame();
        }
        else
        {
            openTitleScreen();
        }
    }


    /**
     * Plays the game.
     */
    private static void playGame()
    {
        Level level = new Level();
        level.play();
    }


    /**
     * Opens the title screen which has a button to play the game.
     */
    private static void openTitleScreen()
    {
        final JFrame window = new JFrame( "Sliding Blocc" );
        window.setSize( 400, 400 );
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JButton playButton = new JButton( "Play" );
        playButton.addActionListener( new ActionListener()
        {
            public void actionPerformed( ActionEvent e )
            {
                window.setEnabled( false );
                window.setVisible( false );
                playGame();
            }
        } );
        window.add( playButton );
        window.setLocationRelativeTo( null );
        window.setIconImage( Toolkit.getDefaultToolkit().getImage( "SlidingBloccIcon.png" ) );
        window.setResizable( false );
        window.setVisible( true );
    }

}
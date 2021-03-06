import java.awt.*;
import java.awt.event.*;
import java.util.PriorityQueue;
import javax.swing.*;


/**
 * An extension of JPanel that draws all of the sprites the engine gives it and
 * listens for keyboard inputs to pass to the engine.
 *
 * @author Roshan Sevalia
 * @version May 18, 2018
 * @author Period: 4
 * @author Assignment: APCSfinal
 *
 * @author Sources: Charles Huang, Gaurav Vipat
 */
public class Window extends JPanel
{
    /**
     * Holds the sprites to render.
     */
    private PriorityQueue<Sprite> sprites;

    /**
     * Holds the JFrame that is used to display this JPanel
     */
    private JFrame frame;

    /**
     * Holds the engine that opened this window
     */
    private Engine engine;

    private final Color TRANSPARENT = new Color(255, 255, 255, 0);


    /**
     * Constructor
     * 
     * @param height
     *            the height of the window
     * @param width
     *            the width of the window
     * @param sprites
     *            the sprites to draw
     * @param engine
     *            the engine that created this sprite
     */
    public Window( int height, int width, PriorityQueue<Sprite> sprites, Engine engine )
    {
        frame = new JFrame( "Level " + ( Level.level + 1 ) );
        this.setPreferredSize( new Dimension( width, height ) );
        super.setBackground( Color.WHITE );
        frame.add( this );
        frame.pack();
        frame.setResizable( false );
        frame.addKeyListener( new upKeyListener() );
        frame.addKeyListener( new leftKeyListener() );
        frame.addKeyListener( new rightKeyListener() );
        frame.addKeyListener( new PKeyListener() );
        frame.setLocationRelativeTo( null );
        frame.setIconImage( Toolkit.getDefaultToolkit().getImage( "SlidingBloccIcon.png" ) );
        super.setVisible( true );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event)
            {
                Game.exit();
            }
        });
        // System.out.println( getSize().toString() );
        // System.out.println( super.getSize().toString() );
        this.sprites = sprites;
        this.engine = engine;

        frame.addKeyListener( new AKeyListener() );
        frame.addKeyListener( new QKeyListener() );
        this.addMouseListener( new MouseClickListener() );
    }


    /**
     * Paints the window. Draws all of the sprites in the priority queue and
     * adjusts the camera accordingly.
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );
        drawFlag( g );
        // g.drawLine();
        // System.out.println(sprites);
        for ( Sprite s : sprites )
        {
            g.setColor( s.getColor() );
            if (s.getImage() == null || s.getWidth() < Sprite.TEXTURE_SIZE || s.getHeight() % Sprite.TEXTURE_SIZE != 0 || s.getWidth() % 20 != 0 || !Engine.TEXTURES_ENABLED)
            {
                g.fillRect( s.getX() - Engine.camera, s.getY(), s.getWidth(), s.getHeight() );
            }
            else
            {
                for (int row = s.getX() - Engine.camera; row < s.getX() + s.getWidth() - Engine.camera; row += Sprite.TEXTURE_SIZE)
                {
                    for (int col = s.getY(); col < s.getY() + s.getHeight(); col += Sprite.TEXTURE_SIZE)
                    {
                        if (!(Sprite.TEXTURE_SIZE > s.getHeight() + s.getY() - col || Sprite.TEXTURE_SIZE > s.getWidth() + s.getX() - Engine.camera - row))
                        {
                            g.drawImage(s.getImage(), row, col, Sprite.TEXTURE_SIZE, Sprite.TEXTURE_SIZE, this.TRANSPARENT, null);
                        }
                    }
                }
            }
        }
    }


    /**
     * Draws the flag at the end of the stage.
     * 
     * @param g
     *            the graphics context
     */
    private void drawFlag( Graphics g )
    {
        g.setColor( Color.ORANGE );
        g.fillRect( Level.getLevelEndZone() - Engine.camera + engine.getPlayer().getWidth(),
            0,
            50,
            Engine.WINDOW_HEIGHT );

        int[] flagX = {
            Level.getLevelEndZone() + 50 - Engine.camera + engine.getPlayer().getWidth(),
            Level.getLevelEndZone() + 250 - Engine.camera + engine.getPlayer().getWidth(),
            Level.getLevelEndZone() + 50 - Engine.camera + engine.getPlayer().getWidth() };
        int[] flagY = { 0, 49, 99 };

        g.setColor( Color.RED );
        g.fillPolygon( flagX, flagY, 3 );
    }


    /**
     * Returns the frame that holds this JPanel
     * 
     * @return this JPanel's frame
     */
    public JFrame getFrame()
    {
        return frame;
    }


    /**
     * Listens for the P key being pressed
     *
     * @author Roshan Sevalia
     * @version May 18, 2018
     * @author Period: 4
     * @author Assignment: APCSfinal
     *
     * @author Sources: Charles Huang, Gaurav Vipat
     */
    private class PKeyListener implements KeyListener
    {
        /**
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped( KeyEvent e )
        {
        }


        /**
         * If P key is pressed, toggle pause in the engine
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_P )
            {
                if ( engine.paused )
                {
                    engine.resume();
                }
                else
                {
                    engine.pause();
                }
            }
        }


        /**
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased( KeyEvent e )
        {

        }
    }


    /**
     * Listens for the up arrow key being pressed and released
     *
     * @author Roshan Sevalia
     * @version May 18, 2018
     * @author Period: 4
     * @author Assignment: APCSfinal
     *
     * @author Sources: Charles Huang, Gaurav Vipat
     */
    private class upKeyListener implements KeyListener
    {
        /**
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped( KeyEvent e )
        {
        }


        /**
         * If Up arrow key is pressed, tell the engine
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_UP )
            {
                engine.upKeyPressed();
            }
        }


        /**
         * If Up arrow key is released, tell the engine
         * 
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_UP )
            {
                engine.upKeyReleased();
            }
        }
    }


    /**
     * Listens for the left arrow key being pressed and released
     *
     * @author Roshan Sevalia
     * @version May 18, 2018
     * @author Period: 4
     * @author Assignment: APCSfinal
     *
     * @author Sources: Charles Huang, Gaurav Vipat
     */
    private class leftKeyListener implements KeyListener
    {
        /**
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped( KeyEvent e )
        {
        }


        /**
         * If Left arrow key is pressed, tell the engine
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_LEFT )
            {
                engine.leftKeyPressed();
            }
            // System.out.println(e.getKeyCode());
        }


        /**
         * If Left arrow key is released, tell the engine
         * 
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_LEFT )
            {
                engine.leftKeyReleased();
            }
        }
    }


    /**
     * Listens for the right arrow key being pressed and released
     *
     * @author Roshan Sevalia
     * @version May 18, 2018
     * @author Period: 4
     * @author Assignment: APCSfinal
     *
     * @author Sources: Charles Huang, Gaurav Vipat
     */
    private class rightKeyListener implements KeyListener
    {
        /**
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped( KeyEvent e )
        {
        }


        /**
         * If Right arrow key is pressed, tell the engine
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
            {
                engine.rightKeyPressed();
            }
            // System.out.println(e.getKeyCode());
        }


        /**
         * If Right arrow key is released, tell the engine
         * 
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
            {
                engine.rightKeyReleased();
            }
        }
    }


    /**
     * Also sets the JFrame's enables setting in addition to this.
     * 
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    @Override
    public void setEnabled( boolean enabled )
    {
        super.setEnabled( enabled );
        frame.setEnabled( enabled );
    }


    /**
     * Also sets the JFrame's visible setting in addition to this.
     * 
     * @see javax.swing.JComponent#setVisible(boolean)
     */
    @Override
    public void setVisible( boolean visible )
    {
        super.setVisible( visible );
        frame.setVisible( false );
    }


    /**
     * Calls the frame's dispose method
     */
    public void dispose()
    {
        frame.dispose();
    }


    /**
     * Listens for the A key being pressed
     *
     * @author Roshan Sevalia
     * @version May 18, 2018
     * @author Period: 4
     * @author Assignment: APCSfinal
     *
     * @author Sources: Charles Huang, Gaurav Vipat
     */
    private class AKeyListener implements KeyListener
    {
        /**
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped( KeyEvent e )
        {
        }


        /**
         * If the A key is pressed
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_A )
            {
                engine.manualMove();
                ;
            }
        }


        /**
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased( KeyEvent e )
        {

        }
    }


    /**
     * Listens for the Q key being pressed
     *
     * @author Roshan Sevalia
     * @version May 18, 2018
     * @author Period: 4
     * @author Assignment: APCSfinal
     *
     * @author Sources: Charles Huang, Gaurav Vipat
     */
    private class QKeyListener implements KeyListener
    {
        /**
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped( KeyEvent e )
        {
        }


        /**
         * Terminate the program if the Q key is pressed
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent e )
        {
            if ( e.getKeyCode() == KeyEvent.VK_Q )
            {
                System.exit( 0 );
            }
        }


        /**
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased( KeyEvent e )
        {
        }
    }
    
    private class MouseClickListener implements MouseListener
    {

        @Override
        public void mouseClicked( MouseEvent arg0 )
        {
            for (Sprite s : Engine.sprites)
            {
                for (int x = s.getX(); x < s.getWidth() + s.getX(); x++)
                {
                    for (int y = s.getY(); y < s.getHeight() + s.getY(); y++)
                    {
                        if (arg0.getX() == x - Engine.camera && arg0.getY() == y)
                        {
                            System.out.println( s.getName() );
                        }
                    }
                }
            }
            
        }

        @Override
        public void mouseEntered( MouseEvent arg0 )
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseExited( MouseEvent arg0 )
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mousePressed( MouseEvent arg0 )
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseReleased( MouseEvent arg0 )
        {
            // TODO Auto-generated method stub
            
        }
        
    }

}

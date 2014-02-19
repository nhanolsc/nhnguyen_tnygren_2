/**
 * This is the main driver/GUI for the Pump/PowerSupply simulation PEX.
 */
package pex2;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main
{
  // N > 0 must be true.  360 % N == 0 should be true for the GUI to look decent.
  private static int N = 5;

  /**
   * The capacity of the main water tank. This is somewhat arbitrary, but each
   * pump is specified as being able to output one gallon per millisecond so
   * one pump running continuously would fill the tank in one minute. Again,
   * nothing magic about this number but it makes a good baseline.
   */
  protected static final int CAPACITY = 60000;

  // A constant for the main panel size so everything will scale nicely
  // if the GUI is ever drawn larger or smaller.
  private static int PANEL_SIZE = 600;
  // GUI components.
  private JFrame frame;
  private JPanel panel;

  public Main()
  {
    // **** Create pumping station components here. ****





    // Create and show the GUI.
    frame = new JFrame( "PEX 2" );
    frame.setResizable( false );
    frame.setLocation( 32, 32 );
    frame.setForeground( Color.WHITE );
    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

    panel = new MainPanel();
    panel.setPreferredSize( new Dimension( PANEL_SIZE, PANEL_SIZE ) );

    frame.add( panel, BorderLayout.CENTER );
    frame.pack();
    frame.setVisible( true );
  }

  public static void main( String[] args )
  {
    new Main().run();
  }

  /**
   * Runs and times the simulation.
   */
  public void run()
  {
    long startTime = System.nanoTime();

    // Run until the tank is full, re-painting the GUI frequently.
    //while( true )
    //{
    //  panel.repaint();
    //}

    long stopTime = System.nanoTime();

    // Make the output look pretty.
    java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
    nf.setGroupingUsed( true );
    System.out.printf( "%s nanoseconds.\n", nf.format( stopTime - startTime ) );
  }

  // JPanel that shows the current state of the pumping station.
  class MainPanel extends JPanel
  {
    // These colors correspond to the PumpState values Ready, Waiting, Pumping, Cleaning.
    private final Color pumpColors[] = { Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED };

    // Since the paint method will be called frequently, save these graphic items
    // to avoid re-creating/re-allocating and killing the garbage collector.
    private final Font font;
    private final BasicStroke stroke2, stroke4, stroke6, stroke8;
    Line2D.Double pipe, leftPowerCord, rightPowerCord;

    // A few values calculated based on PANEL_SIZE.
    int pumpSize, switchSize, tankWidth, tankHeight, fontSize;

    public MainPanel()
    {
      super();
      this.setBackground( Color.WHITE );

      // Create a font and a few drawing tools.
      font = new Font( Font.SANS_SERIF, Font.BOLD, PANEL_SIZE / 20 );
      fontSize = font.getSize();
      stroke2 = new BasicStroke( 2 );
      stroke4 = new BasicStroke( 4 );
      stroke6 = new BasicStroke( 6 );
      stroke8 = new BasicStroke( 8 );

      // Everything is based on the size of the JPanel in which we're drawing.
      pumpSize = PANEL_SIZE / 10;
      switchSize = pumpSize / 2;
      tankWidth = pumpSize * 2;
      tankHeight = pumpSize * 4;

      // The x,y coordinate of the center of the pump (with 0,0 at the center of the window).
      // Used to calculate end points of lines connected to each pump.
      int x = 0, y = -PANEL_SIZE/2 + pumpSize/2;

      // Create a line to be used to draw the pipes.
      pipe = new Line2D.Double( 0, 0, x, y );

      // Create two lines to be used to draw the power cords.
      double angle = Math.toRadians( 360 / N / 2 );
      leftPowerCord = new Line2D.Double( x, y,
          x * Math.cos(  angle ) - y * Math.sin(  angle ),
          y * Math.cos(  angle ) + x * Math.sin(  angle ) );
      rightPowerCord = new Line2D.Double( x, y,
          x * Math.cos( -angle ) - y * Math.sin( -angle ),
          y * Math.cos( -angle ) + x * Math.sin( -angle ) );
    }

    /**
     * This method paints the pumping station.
     * 
     * @param g The Graphics object to use to paint.
     */
    @Override
    public void paintComponent( Graphics g )
    {
      // Make sure the JPanel does whatever it needs to do when painted.
      super.paintComponent( g );

      // Use the Graphics2D class for more advanced drawing tools. For details, see
      //   http://docs.oracle.com/javase/tutorial/2d/overview/rendering.html
      //   http://docs.oracle.com/javase/7/docs/api/java/awt/Graphics2D.html
      Graphics2D g2 = (Graphics2D)g;
      g2.setFont( font );

      // Shift the coordinate system so 0,0 is in the center of the window.
      g2.translate( PANEL_SIZE/2, PANEL_SIZE/2 );

      // Draw each pump, rotating the Graphics2D object so each pump
      // can be drawn as if it is at the top-center of the panel.
      for( int i = 0; i < N; i++ )
      {
        // Outline the power cords.
        g2.setColor( Color.BLACK );
        g2.setStroke( stroke6 );
        g2.draw( leftPowerCord );
        g2.draw( rightPowerCord );
        // Draw the inner part of the power cord to show on/off.
        g2.setStroke( stroke4 );
        g2.setColor( Math.random() < 0.5 ? Color.ORANGE : Color.LIGHT_GRAY );   // **** Set color based on the pumps/power supplies, as appropriate! ****
        g2.draw( leftPowerCord );
        g2.setColor( Math.random() < 0.5 ? Color.ORANGE : Color.LIGHT_GRAY );   // **** Set color based on the pumps/power supplies, as appropriate! ****
        g2.draw( rightPowerCord );

        // The pipe from the pump to the tank.
        g2.setStroke( stroke8 );
        g2.setColor( Color.BLACK );
        g2.draw( pipe );
        g2.setStroke( stroke6 );
        g2.setColor( pumpColors[ (int)(Math.random() * pumpColors.length) ] );  // **** Set color based on the pumps/power supplies! ****
        g2.draw( pipe );

        // The actual pump, drawn last so it will cover the ends of the
        // power cords and the pipe.
        g2.setColor( pumpColors[ (int)(Math.random() * pumpColors.length) ] );  // **** Set color based on the pumps/power supplies! ****
        g2.fillOval( -pumpSize/2, -PANEL_SIZE/2, pumpSize, pumpSize );
        // Outline the pump so it looks pretty.
        g2.setStroke( stroke2 );
        g2.setColor( Color.BLACK );
        g2.drawOval( -pumpSize/2, -PANEL_SIZE/2, pumpSize, pumpSize );
        // Show the pump ID number, mostly for debugging purposes.
        g2.setColor( Color.WHITE );
        g2.drawString( "" + i, -font.getSize()/4, -PANEL_SIZE/2 + pumpSize/2 + font.getSize()/3 );

        // Rotate the graphics object to draw the next pump.
        g2.rotate( Math.toRadians( 360 / N ) );
      }

      // Draw power supplies at the intersections of the power cords.
      g2.rotate( Math.toRadians( 360 / N / 2 ) );  // Initial rotation to get between two pumps.
      for( int i = 0; i < N; i++ )
      {
        // The switch, filled and then outlined.
        g2.setColor( Color.ORANGE );
        g2.fillRect( -switchSize/2, -PANEL_SIZE/2 + switchSize/2, switchSize, switchSize );
        g2.setColor( Color.BLACK );
        g2.drawRect( -switchSize/2, -PANEL_SIZE/2 + switchSize/2, switchSize, switchSize );

        // Label each switch with a letter.
        g2.drawString( "" + (char)('A' + i), -fontSize/3, -PANEL_SIZE/2 + pumpSize/2 + fontSize/3 );

        // Rotate the graphics object to draw the next switch.
        g2.rotate( Math.toRadians( 360 / N ) );
      }
      g2.rotate( -Math.toRadians( 360 / N / 2 ) );  // Un-do initial rotation.

      // Fill the entire tank with blue.
      g2.setColor( Color.BLUE );
      g2.fillRect( -tankWidth/2, -tankHeight/2, tankWidth, tankHeight );
      // Fill the empty part of the tank with white so it appears empty.
      // Note the last parameter to fillRect is based on the current level of the tank.
      g2.setColor( Color.WHITE );
      // **** Replace the 20000 in the line below with the current level of the water tank! ****
      g2.fillRect( -tankWidth/2, -tankHeight/2, tankWidth, tankHeight - tankHeight * 20000 / CAPACITY );
      // Outline the tank so it looks pretty.
      g2.setColor( Color.BLACK );
      g2.setStroke( stroke4 );
      g2.drawRect( -tankWidth/2, -tankHeight/2, tankWidth, tankHeight );
    }

    /**
     * For an explanation of this constant, see
     *   http://docs.oracle.com/javase/7/docs/api/java/io/Serializable.html
     * Basically, it makes the complier happy, so just leave it here.
     */
    private static final long serialVersionUID = 1L;
  }
}
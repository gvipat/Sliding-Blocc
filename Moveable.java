import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;

//Description of what print statements active
//**** signifies going into (non-corner based) v collision stuff
//followed by cornertype print statement
public abstract class Moveable extends Sprite
{
    private float horizontalVelocity;

    private float verticalVelocity;

    public static final int MAX_V_VELOCITY = 5;

    public static final int MAX_H_VELOCITY = 2;

    public boolean applyGravity = true;

    public Sprite enemyToKill = null;

    public boolean isDead = false;


    public enum CollisionType {
        NO_COLLISION,
        VERTICAL_GROUND_OVER,
        VERTICAL_GROUND_UNDER,
        HORIZONTAL_ENEMY,
        UNDER_ENEMY,
        OVER_ENEMY,
        CONTACT,
        HORIZONTAL_GROUND_FROM_LEFT,
        HORIZONTAL_GROUND_FROM_RIGHT,
    };


    private enum CornerType {
        TR_BL, TR_BR, TL_BR, TL_BL, BL_TR, BR_TR, BR_TL, BL_TL, PERF_CNTCT
    };


    public abstract boolean move();


    public Moveable( int x, int y, int width, int height, Color color, String named )
    {
        super( x, y, width, height, color, named );
        this.compareValue++;
    }


    public float getHVelocity()
    {
        return horizontalVelocity;
    }


    public float getVVelocity()
    {
        return verticalVelocity;
    }


    public void setHVelocity( float newVelocity )
    {
        horizontalVelocity = newVelocity;
    }


    public void setVVelocity( float newVelocity )
    {
        verticalVelocity = newVelocity;
    }


    public void addGravity()
    {
        if ( applyGravity && verticalVelocity < MAX_V_VELOCITY )
        {
            verticalVelocity += 0.05;
            if ( GameMath.roundToHundreths( verticalVelocity ) == 0.0 )
            {
                verticalVelocity += 0.05;
            }
            verticalVelocity = GameMath.roundToHundreths( verticalVelocity );
        }
    }


    // NO_COLLISION, HORIZONTAL_GROUND, VERTICAL_GROUND, HORIZONTAL_ENEMY,
    // UNDER_ENEMY, OVER_ENEMY
    public LinkedList<CollisionType> checkCollision()
    {
        LinkedList<CollisionType> list = new LinkedList<CollisionType>();

        for ( Sprite s : Engine.sprites )
        {
            if ( s != this )
            {
                // if (Math.abs(this.getX() - s.getX()) < 4 * MAX_H_VELOCITY + 1
                // || Math.abs(this.getY() - s.getY()) < 2 * MAX_V_VELOCITY + 1)
                // {

                // boolean bothMoveable = false;

                if ( s instanceof Moveable && this instanceof Moveable )
                {

                    if ( s instanceof EnemySprite && this instanceof EnemySprite )
                    {
                        // do nothing
                    }
                    // bothMoveable = true;

                    else if ( s instanceof EnemySprite && this instanceof PlayerSprite )
                    {
                        this.setContact( "PLAYER_ENEMY" );
                        list.add(
                            checkCollision_BothMoveable( (PlayerSprite)this, (EnemySprite)s ) );
                    }

                    else
                    {
                        s.setContact( "PLAYER_ENEMY" );
                        list.add( checkCollision_BothMoveable( (PlayerSprite)s, (EnemySprite)this ) );
                    }

                }

                else if ( s instanceof CornerSprite )
                {
                    CornerSprite tempCorner = (CornerSprite)( s );

                    if ( tempCorner.isRightSide()
                        && ( this.getX() > tempCorner.getTopRightCorner().getX() ) )
                    {
                        list.add( CollisionType.NO_COLLISION );
                        return list;
                    }
                    if ( !tempCorner.isRightSide()
                        && ( this.getX() < (int)tempCorner.getTopLeftCorner().getX() ) )
                    {
                        list.add( CollisionType.NO_COLLISION );
                        return list;
                    }

                }

                else
                {
                    list.add( checkCollision_OneMoveable( this, (GroundSprite)s ) );
                }
            }

        }

        // }
        return list;

    }


    // NO_COLLISION, HORIZONTAL_GROUND, VERTICAL_GROUND, HORIZONTAL_ENEMY,
    // UNDER_ENEMY, OVER_ENEMY
    private CollisionType checkCollision_BothMoveable( PlayerSprite player, EnemySprite enemy ) ///////// ACTUALLY
                                                                                                ///////// I
                                                                                                ///////// DON'T
                                                                                                ///////// NEED
                                                                                                ///////// TO
                                                                                                ///////// GET
                                                                                                ///////// RID
                                                                                                ///////// OF
                                                                                                ///////// TEMP
                                                                                                ///////// BECAUSE
                                                                                                ///////// TEMP
                                                                                                ///////// IS
                                                                                                ///////// NEVER
                                                                                                ///////// ADDED
                                                                                                ///////// TO
                                                                                                ///////// PRIORITY
                                                                                                ///////// QUEUE
                                                                                                ///////// AND
                                                                                                ///////// RENDERED
    {
        Object[] temp = checkCorners( player, enemy );
        Sprite tempSprite = new GroundSprite( Math.round( enemy.getX() + enemy.getHVelocity() ),
            Math.round( enemy.getY() + enemy.getVVelocity() ),
            enemy.getWidth(),
            enemy.getHeight() , "tempSprite");
        CollisionType playerTempCollision = checkCollision_OneMoveable( player, tempSprite );
        if ( playerTempCollision == CollisionType.HORIZONTAL_GROUND_FROM_LEFT || playerTempCollision == CollisionType.HORIZONTAL_GROUND_FROM_RIGHT )
        {
            return CollisionType.HORIZONTAL_ENEMY;
        }
        else if ( playerTempCollision == CollisionType.VERTICAL_GROUND_OVER )
        {
            if ( player.getVVelocity() < 0 
                || (GameMath.roundToHundreths(player.getVVelocity()) == 0 && enemy.getVVelocity() > 0))
            {
                return CollisionType.UNDER_ENEMY;
            }
            if ( player.getVVelocity() > 0 )
            {
                Engine.sprites.remove( enemy );
                return CollisionType.OVER_ENEMY;
            }
        }

        return CollisionType.NO_COLLISION;
    }


    // NO_COLLISION, HORIZONTAL_GROUND, VERTICAL_GROUND, HORIZONTAL_ENEMY,
    private CollisionType checkCollision_OneMoveable( Moveable mover, Sprite ground )
    {
        Object[] temp = checkCorners( mover, ground );

        //System.commented.out.println("//////////////////////////////////////////////////");
        //System.out.print("v = " + mover.getVVelocity() + "dc = " + GameMath.roundToHundreths(mover.getVVelocity()));
        //System.out.print("h = " + mover.getHVelocity() + "dist = " + (Float)temp[1]);

        

        if (Math.abs( mover.getHVelocity())   >= (Float)temp[1]
            && (mover.getBotLeftCorner().y <= ground.getBotLeftCorner().y + mover.getHeight()
                && mover.getY() >= ground.getY() - ground.getHeight() 
                && mover.getY() - ground.getY() < ground.getHeight()
                && mover.getY() - ground.getY() > -mover.getY()))
        {
            //System.commented.out.println("\t\t\t straight horizontal");
            //mover.setHVelocity(0);
            mover.setHVelocity((Float)temp[1] * GameMath.getSign(mover.getHVelocity()) );
           // System.out.println(mover.getHVelocity() + "***************************");
            mover.setX(mover.getX() + mover.getHVelocity());
            if (mover.getX() < ground.getX())
            {
                return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
            }
            else if (mover.getX() > ground.getX())
            {
                return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
            }
        }
        if (ground.getName().equals( "hanging 1" ))
        {
            System.out.println( "groundName : " + ground.getName() + "; V velocity = " 
        + mover.getVVelocity() + " y distance = " + temp[2] + "Y__" + mover.getY() + " groundY+height" + (ground.getY() + ground.getHeight()));
        }
        
        // System.out.println( "a = " + temp[1] + "** b =" + temp[2] +
        // "*************" );
        if ( /*GameMath.roundToHundreths( mover.getHVelocity() ) == 0 &&*/
             (mover.getY() < ground.getY() && mover.getVVelocity()  > (Float)temp[2]) ||
             (mover.getY() >= (ground.getY()  + ground.getHeight()) && -mover.getVVelocity() > (Float)temp[2]) ) 
        {
            
            System.out.println(" ********************************** + "  + ground.getName());
            if ( !( ( mover.getX() - ground.getX() ) < ground.getWidth()
                && ( mover.getX() - ground.getX() ) > -mover.getWidth() ) )
            {
                return CollisionType.NO_COLLISION;
            } // Roshan explains
            if ( mover.getY() < ground.getY() )
            {
                applyGravity = false;
                mover.setVVelocity(
                    (Float)temp[2] * GameMath.getSign(mover.getVVelocity()) ) ;
                mover.setY( mover.getY() + mover.getVVelocity() );
                //System.commented.out.println( "STRAIGHT VERTICAL" + "unabs y distance " + temp[3] );
                return CollisionType.VERTICAL_GROUND_OVER;
            }
            if ( mover.getY() >= (ground.getY() + ground.getHeight()) )
            {
                System.out.println( "\t\t\t\t\t\tinside STRAIGHT VERTICAL GROUND UNDER!" );
                mover.setVVelocity(
                    (Float)temp[2] * GameMath.getSign(mover.getVVelocity()) );
                mover.setY(mover.getY() + mover.getVVelocity());
                //System.commented.out.println( "STRAIGHT VERTICAL" + "unabs y distance " + temp[3] );
                return CollisionType.VERTICAL_GROUND_UNDER;
            }

        }

        int hSign;
        int vSign;

        System.out.println( "spriteName = " + ground.getName() + " corner type" + (CornerType)temp[0]  );
        switch ( (CornerType)temp[0] )
        {
            case PERF_CNTCT:
                //System.commented.out.println( "\t\t\t\tb" );
                return CollisionType.CONTACT;

            case BL_TL:
                // System.out.println("TLBL************");
                hSign = GameMath.getSign(mover.getHVelocity());//(int)( Math.abs( mover.getHVelocity() ) / mover.getHVelocity() );
                vSign = GameMath.getSign(mover.getVVelocity());//(int)( Math.abs( mover.getVVelocity() ) / mover.getVVelocity() );

                // if ( vSign > 0 && mover.getHVelocity() - mover.getWidth() > (Float)temp[1] )
                // { /////// maybe minus 1
                //     mover.setHVelocity( (Float)temp[1] + mover.getWidth() );
                //     float slope = Math.abs( mover.getVVelocity() / mover.getHVelocity() );
                //     mover.setVVelocity( hSign * (Float)( slope * mover.getHVelocity() ) );
                //     if (mover.getX() < ground.getX())
                //     {
                //         return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
                //     }
                //     else if (mover.getX() > ground.getX())
                //     {
                //         return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
                //     }
                // }TR
                if ( mover.getHVelocity() != 0 && mover.getVVelocity() > (Float)temp[2] )// weird
            {

                    applyGravity = false;
                    mover.setVVelocity( vSign * (Float)temp[2] );
                    mover.setY(mover.getY() + mover.getVVelocity());//TODO check for others
                    double slope_inverted = Math.abs( mover.getHVelocity() / mover.getVVelocity() );
                    // mover.setHVelocity((float)(slope_inverted*getHVelocity()));
                    mover.setHVelocity( 0 );

                    return CollisionType.VERTICAL_GROUND_OVER;
                }
                break;
            case BR_TR:
                // System.out.println("TLBL************");
                hSign = GameMath.getSign(mover.getHVelocity());//(int)( Math.abs( mover.getHVelocity() ) / mover.getHVelocity() );
                vSign = GameMath.getSign(mover.getVVelocity());//(int)( Math.abs( mover.getVVelocity() ) / mover.getVVelocity() );

            //     if ( vSign > 0 && mover.getHVelocity() - mover.getWidth() > (Float)temp[1] )
            //     { /////// maybe minus 1
            //         mover.setHVelocity( (Float)temp[1] + mover.getWidth() );
            //         float slope = Math.abs( mover.getVVelocity() / mover.getHVelocity() );
            //         mover.setVVelocity( hSign * (Float)( slope * mover.getHVelocity() ) );
            //         if (mover.getX() < ground.getX())
            // {
            //    // return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
            // }
            // else if (mover.getX() > ground.getX())
            // {
            //     //return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
            // }
            //     }
                if ( mover.getHVelocity() != 0 && mover.getVVelocity() > (Float)temp[2] )// weird
                                                                                // hsign
                {

                    applyGravity = false;
                    mover.setVVelocity( vSign * (Float)temp[2] );
                    double slope_inverted = Math.abs( mover.getHVelocity() / mover.getVVelocity() );
                    // mover.setHVelocity((float)(slope_inverted*getHVelocity()));
                    mover.setHVelocity( 0 );

                    return CollisionType.VERTICAL_GROUND_OVER;
                }
                break;
            // case TL_BL:
            // //System.commented.out.println("TLBL************");
            // hSign = (int) (Math.abs(mover.getHVelocity()) /
            // mover.getHVelocity());
            // vSign = (int) (Math.abs(mover.getVVelocity()) /
            // mover.getVVelocity());
            //
            // if(vSign > 0 && mover.getHVelocity() - mover.getWidth() >
            // (Float)temp[1]){ ///////maybe minus 1
            // mover.setHVelocity((Float)temp[1] + mover.getWidth());
            // float slope = Math.abs(mover.getVVelocity() /
            // mover.getHVelocity());
            // mover.setVVelocity(hSign*(Float)(slope*mover.getHVelocity()));
            // return CollisionType.HORIZONTAL_GROUND;
            // }
            // if(hSign != 0 && mover.getVVelocity() > (Float)temp[2])// weird
            // now because can't have an if statement on hsign
            // {
            // //applyGravity = false; ///POSSIBLE ERROR HERE!
            // mover.setVVelocity(vSign*(Float)temp[2]);
            // double slope_inverted = Math.abs(mover.getHVelocity() /
            // mover.getVVelocity());
            // //mover.setHVelocity((float)(slope_inverted*getHVelocity()));
            // return CollisionType.VERTICAL_GROUND;
            // }
            // break;

            case TR_BL: // doned
                //System.commented.out.println( "\t\t\t\tc" );
                hSign = GameMath.getSign(mover.getHVelocity());//(int)( Math.abs( mover.getHVelocity() ) / mover.getHVelocity() );
                vSign = GameMath.getSign(mover.getVVelocity());//(int)( Math.abs( mover.getVVelocity() ) / mover.getVVelocity() );

                if ( !( ( mover.getX() - ground.getX() ) < ground.getWidth()
                    && ( mover.getX() - ground.getX() ) > -mover.getWidth() ) )
                {
                    return CollisionType.NO_COLLISION;
                }
                // if ( vSign < 0 && mover.getHVelocity() >= (Float)temp[1] )
                // { /////// maybe minus 1
                //     //float slope = Math.abs( mover.getVVelocity() / mover.getHVelocity() );
                //     mover.setHVelocity( hSign * (Float)temp[1]);
                //     mover.setVVelocity( 0 );
                //     mover.setX(mover.getX() + mover.getHVelocity());
                //     if (mover.getX() < ground.getX())
                //     {
                //        // return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
                //     }
                //     else if (mover.getX() > ground.getX())
                //     {
                //         //return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
                //     }
                // }
                if ( hSign > 0 && mover.getVVelocity() > (Float)temp[2] )
                {
                    mover.setVVelocity( vSign * (Float)temp[2] );
                    double slopeInverted = Math.abs( mover.getHVelocity() / mover.getVVelocity() );
                    mover.setHVelocity( (float)( slopeInverted * getHVelocity() ) );
                    return CollisionType.VERTICAL_GROUND_OVER;
                }
                break;

            case TL_BR:

                //System.commented.out.println( "\t\t\t\td" );
                hSign = GameMath.getSign(mover.getHVelocity());//(int)( Math.abs( mover.getHVelocity() ) / mover.getHVelocity() );
                vSign = GameMath.getSign(mover.getVVelocity());//(int)( Math.abs( mover.getVVelocity() ) / mover.getVVelocity() );

            //     if ( vSign < 0
            //         && Math.abs( mover.getHVelocity() )  >= (Float)temp[1] )
            //     { /////// maybe minus 1
            //         mover.setHVelocity( hSign * ( (Float)temp[1]  ) );
            //         //double slope = Math.abs( GameMath.roundToHundreths(mover.getVVelocity()) / GameMath.roundToHundreths(mover.getHVelocity() ));
            //         //mover.setVVelocity( vSign * (float)( slope * mover.getHVelocity() ) );
            //         mover.setVVelocity(0);
            //         if (mover.getX() < ground.getX())
            // {
            //     //return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
            // }
            // else if (mover.getX() > ground.getX())
            // {
            //     //return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
            // }
            //     }
                if ( hSign < 0 && Math.abs( mover.getVVelocity() ) > (Float)temp[2] )
                {
                    if ( ( mover.getX() - ground.getX() ) < ground.getWidth()
                        && ( mover.getX() - ground.getX() ) > -mover.getWidth() )
                    {
                        mover.setVVelocity( vSign * (Float)temp[2] );
                        double slopeInverted = Math.abs( GameMath.roundToHundreths(mover.getHVelocity()) / GameMath.roundToHundreths(mover.getVVelocity()) );
                        mover.setHVelocity( hSign * (float)( slopeInverted * getHVelocity() ) );
                        return CollisionType.VERTICAL_GROUND_OVER;
                    }
                }
                break;

            case BL_TR://///////////////////////////////////////////////////////////////////////////////////////////////// PROBLEM
                       /////////////////////////////////////////////////////////////////////////////////////////////////// HERE!!!!!!!!!!!!!!!!!!!!!!!!

                //System.commented.out.println( "\t\t\t\te" );
                hSign = GameMath.getSign(GameMath.roundToTenths(mover.getHVelocity()));//(int)( Math.abs( mover.getHVelocity() ) / mover.getHVelocity() );
                vSign = GameMath.getSign(GameMath.roundToTenths(mover.getVVelocity()));//(int)( Math.abs( mover.getVVelocity() ) / mover.getVVelocity() );

            //     if ( vSign > 0
            //         && Math.abs( mover.getHVelocity() ) >= (Float)temp[1] )
            //     { /////// maybe minus 1
            //         mover.setHVelocity( hSign * ( (Float)temp[1] + mover.getWidth() ) );
            //         //double slope = Math.abs( GameMath.roundToHundreths(mover.getVVelocity()) / GameMath.roundToHundreths(mover.getHVelocity() ) );
            //         //mover.setVVelocity(
            //         //    vSign * (float)( slope * Math.abs( mover.getHVelocity() ) ) );
            //         mover.setVVelocity(0);
            //         //System.commented.out.println( "BL_TR horiz ground ***********" );
            //         if (mover.getX() < ground.getX())
            // {
            //     //return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
            // }
            // else if (mover.getX() > ground.getX())
            // {
            //     //return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
            // }
            //    }
                if ( hSign < 0 && Math.abs( mover.getVVelocity() ) > (Float)temp[2] )
                {
                    if ( ( mover.getX() - ground.getX() ) < ground.getWidth()
                        && ( mover.getX() - ground.getX() ) > -mover.getWidth() )
                    {
                        mover.setVVelocity( vSign * (Float)temp[2] );
                        double slopeInverted = GameMath.roundToHundreths(mover.getHVelocity()) / GameMath.roundToHundreths(mover.getVVelocity() );
                        mover.setHVelocity(
                        hSign * (float)( slopeInverted * Math.abs( getHVelocity() ) ) );
                        //System.commented.out.println(mover.getVVelocity() + " " + GameMath.roundToHundreths(mover.getVVelocity() ));
                        //System.commented.out.println( "BL_TR vertical ground **********" );
                        return CollisionType.VERTICAL_GROUND_OVER;
                    }
                }

                break;

            case BR_TL:

                //System.commented.out.println( "\t\t\t\tf" );
                hSign = GameMath.getSign(mover.getHVelocity());//(int)( Math.abs( mover.getHVelocity() ) / mover.getHVelocity() );
                vSign = GameMath.getSign(mover.getVVelocity());//(int)( Math.abs( mover.getVVelocity() ) / mover.getVVelocity() );


                System.out.println("hVelocity : " + mover.getHVelocity() + " x distance: "+ temp[1]);
                // if ( vSign > 0
                //     && Math.abs( mover.getHVelocity() )  >= (Float)temp[1] )
                // { /////// maybe minus 1
                //     double slope = Math.abs( mover.getVVelocity() / mover.getHVelocity() );
                //     mover.setHVelocity( hSign * ( (Float)temp[1] ) );
                //     //mover.setVVelocity(
                //       //  vSign * (float)( slope * Math.abs( mover.getHVelocity() ) ) );
                //       mover.setVVelocity(0);  
                //       mover.setX(mover.getX() + mover.getHVelocity());
                //         if (mover.getX() < ground.getX())
                //         {
                //             //return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
                //         }
                //         else if (mover.getX() > ground.getX())
                //         {
                //            // //return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
                //         }
                // }
                if ( mover.getX() > ground.getX() - MAX_H_VELOCITY
                    && mover.getX() < ground.getX() + MAX_H_VELOCITY )
                {
                    if ( hSign > 0 && Math.abs( mover.getVVelocity() ) > (Float)temp[2] )
                    {
                        mover.setVVelocity( vSign * (Float)temp[2] );
                        double slope_inverted = Math
                            .abs( mover.getHVelocity() / mover.getVVelocity() );
                        mover.setHVelocity(
                            hSign * (float)( slope_inverted * Math.abs( getHVelocity() ) ) );
                        return CollisionType.VERTICAL_GROUND_OVER;
                    }
                }

                break;

            case TL_BL:
                //System.commented.out.println( "\t\t\t\tq" );
                hSign = GameMath.getSign(mover.getHVelocity());//(int)( Math.abs( mover.getHVelocity() ) / mover.getHVelocity() );
                vSign = GameMath.getSign(mover.getVVelocity());//(int)( Math.abs( mover.getVVelocity() ) / mover.getVVelocity() );

                // if ( vSign > 0
                //     && Math.abs( mover.getHVelocity() )  >= (Float)temp[1] )
                // { /////// maybe minus 1
                //     mover.setHVelocity( hSign * ( (Float)temp[1] + mover.getWidth() ) );
                //     //double slope = Math.abs( mover.getVVelocity() / mover.getHVelocity() );
                //     mover.setVVelocity(0);//mover.setVVelocity(
                //       //  vSign * (float)( slope * Math.abs( mover.getHVelocity() ) ) );
                        
                //       if (mover.getX() < ground.getX())
                //         {
                //             //return CollisionType.HORIZONTAL_GROUND_FROM_LEFT;
                //         }
                //         else if (mover.getX() > ground.getX())
                //         {
                //             //return CollisionType.HORIZONTAL_GROUND_FROM_RIGHT;
                //         }
                // }
                if ( mover.getX() > ground.getX() - MAX_H_VELOCITY
                    && mover.getX() < ground.getX() + MAX_H_VELOCITY )
                {
                    if ( hSign > 0 && Math.abs( mover.getVVelocity() ) > (Float)temp[2] )
                    {
                        mover.setVVelocity( vSign * (Float)temp[2] );
                        double slope_inverted = Math
                            .abs( mover.getHVelocity() / mover.getVVelocity() );
                        mover.setHVelocity(
                            hSign * (float)( slope_inverted * Math.abs( getHVelocity() ) ) );
                        return CollisionType.VERTICAL_GROUND_OVER;
                    }
                }
                break;

        }

        //System.commented.out.println( "\t\t\t\tg" );
        return CollisionType.NO_COLLISION;

    }


    private double[] getDiag( Point a, Point b )
    {
        double[] temp = new double[4];
        double x = ( a.getX() - b.getX() ) * ( a.getX() - b.getX() );
        double y = ( a.getY() - b.getY() ) * ( a.getY() - b.getY() );
        temp[0] = Math.sqrt( x + y );
        temp[1] = Math.sqrt( x );
        temp[2] = Math.sqrt( y );
        temp[3] = a.getY() - b.getY(); // NON ABS VALUE!!!
        return temp;
    }


    // enums: {TR_BL/, TR_BR/, TL_BR/, TL_BL/, BL_TR/, BR_TR/, BR_TL/, BL_TL/}
    // Object array contains 3 vals, type of collision, minimum x, and minimum y
    private Object[] checkCorners( Sprite og, Sprite other )
    {
        CornerType cornerTemp = CornerType.TR_BL;
        double[] mindistance = getDiag( og.getTopRightCorner(), other.getBotLeftCorner() );

        double[] temp = getDiag( og.getTopRightCorner(), other.getBotRightCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.TR_BR;
        }

        temp = getDiag( og.getTopLeftCorner(), other.getBotRightCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.TL_BR;
        }

        temp = getDiag( og.getTopLeftCorner(), other.getBotLeftCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.TL_BL;
        }

        temp = getDiag( og.getBotLeftCorner(), other.getTopRightCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.BL_TR;
        }

        temp = getDiag( og.getBotRightCorner(), other.getTopRightCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.BR_TR;
        }

        temp = getDiag( og.getBotRightCorner(), other.getTopLeftCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.BR_TL;
        }

        temp = getDiag( og.getBotLeftCorner(), other.getTopLeftCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.BL_TL;
        }

        temp = getDiag( og.getTopLeftCorner(), other.getBotLeftCorner() );
        if ( temp[0] < mindistance[0] )
        {
            mindistance = temp;
            cornerTemp = CornerType.TL_BL;
        }
        double a = mindistance[1];
        double b = mindistance[2];
        CornerType c = cornerTemp;

        // if(DecimalRounder.roundToTenths((float)a) == 1 ||
        // DecimalRounder.roundToTenths((float)b)==1){
        // //System.commented.out.println( "perfect contacting" );

        // c = CornerType.PERF_CNTCT;

        // //System.commented.out.println( "check corners: perf_cnct" );

        // // if (a > b)
        // // {
        // // applyGravity = true;
        // // }
        // }

        // System.out.println(DecimalRounder.roundToTenths((float)b));
        // ((DecimalRounder.roundToTenths((float)b)==0 ||
        // DecimalRounder.roundToTenths((float)b)==1 && (og.getX() -
        // other.getX()) < other.getWidth() && (og.getX() - other.getX()) >
        // -og.getWidth()) )
        if ( (int)b == 0 && ( og.getX() - other.getX() ) < other.getWidth()
            && ( og.getX() - other.getX() ) > -og.getWidth()
            && og.getY() < other.getY() )
        {
            c = CornerType.PERF_CNTCT;
        }
        //System.commented.out.println( "THIS IS B: " + b );

        Object[] thing = new Object[4];
        thing[0] = c;
        thing[1] = (float)Math.abs( a );
        thing[2] = (float)Math.abs( b );
        thing[3] = (float)mindistance[3];

        return thing; /////////////////////////////////////////////////////////////////////////////////////// POSSIBLE
                      /////////////////////////////////////////////////////////////////////////////////////// ERROR
                      /////////////////////////////////////////////////////////////////////////////////////// CHECK
                      /////////////////////////////////////////////////////////////////////////////////////// HERE

    }

}

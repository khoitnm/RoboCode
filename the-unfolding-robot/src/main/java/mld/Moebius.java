package mld;
import robocode.*;
import java.awt.Color;

//  Moebius - a robot by Michael Dorgan

// --------------------------------------------------------------------------------------------------------------------------------------------
// Revision 3-09-03 ver 1.0
//
// After very carefully studying the pattern-matching code that Albert provided to the Wiki, I've modified it slightly,
// added a pattern it has trouble hitting to my bot, and here it is - Mobius, my first effective pattern Matching aim bot.
// The parameters can be tweeked later, but for now, I'm just happy it's alive and working.  A strong 1v1 contender in
// NanoBot land.
//
// CodeSize: 248 (No colors yet)
//
// Revision 3-12-03 ver 1.01
//
// Lowered the search depth to 9.  Changed movement to exact triangle pattern as this seems even better than box.
// Freed a bunch of bytes from the pattern code and that allowed me to use a more exact value for the pattern
// match itself - the angular velocity vs the straight velocity.  Really helps when locking onto circle bots.
// Alos made room for colors (style does matter!)  Fixed the spelling of our name (thanks Dave), and a few typos in 
// the comments.  A very, very strong 1v1 Nanobot.
//
// Codesize: 249 with colors!
//
// Revision 3-17-03 ver 1.1
//
// Changed to tan() style locking to gain a few bytes back.  Removed toString() from pattern matcher as it wasn't
// needed.  Had to swtich to dirty triangle movement.  Used saved bytes to implement a fire power control system.
// This allows better survival against certain bots and helps a bit in melee.  Upped movement per leg to 170 to
// help dodge no linear/circular aim shots.  A fairly good improvement.  It survives better against all bots, but
// doesn't win as convincingly against a few it locked onto last revision.  Still, a good improvement.
//
// Codesize: 248 with colors!
//
// Revision 3-25-03 ver 1.2
//
// Radar now uses Infinity style Infinite radar.  I've also re-purposed Albert's wonderful, wonderful pattern
// matching gun for my own use.  I've shrank its codesize somewhat in the process so he can feel free to take 
// it back and have his bot do new things too (like color!)  It's also more accurate than his due to the endBufferCheck.
// I've upped my triange edge a bit to help dodge  as this is the cheapest pattern I could find that caused 
// this new system of aiming to have (just a little) problem hitting.  It's a nice melee pattern as well so I 
// may do decent there too.  I've taken out the power management stuff as it was costing me too many damage points 
// and now I'm hitting even more often so it's not needed.  So far, I've not found a single Nanobot that consistently 
// beats me - though Smog 1.5 gives me problems.  All others fall before my (borrowed and improved) Nano-Scythe 
// of destruction!!! :) 
//
// Codesize: 249 with colors!
//
// Revision 3-26-03 ver 1.3
// I've switched from box movement to orbit movement with random jerkiness thrown in to kill pattern matching.
// This pattern really gives our aim trouble, keeps us equal distance from target which improves aim quality,
// and does decent dodging in all cases.  I've done a few code tricks to optimize codespace and make room for this
// new pattern.  Basically, my death shot code got reduced by 10 shots, my aim cannon at target isn't quite as good, 
// and I still don't get adjustGun() call to help with dead bots.  Still, this bot is really awesome right now.  It 
// annihilates most and only Pinball and Graviton currently stay somewhat close.  An excellent NanoBot 1v1 specialist!
// Many thanks to Albert for this wonderful gun idea and to all other coders who have made me stretch my mind in 
// order to compete.  Thanks!
//
// Codesize: 249 with colors! (And no more room to be had that I can find!)
//
// Revision 4-25-03 ver 1.3.1
// Dieted 4 bytes using a doJob() private method which packs the often-used queries and declares variables in the
// method signature instead of in the body. Diet recepie by PEZ.
//
// Codesize: 245 with colors!
// --------------------------------------------------------------------------------------------------------------------------------------------

public class Moebius extends AdvancedRobot
{
	// Constants
	static final int 	SEARCH_DEPTH = 30;	// Increasing this slows down game execution - beware!
	static final int 	MOVEMENT_LENGTH = 150;	// Larger helps on no-aim and nanoLauLectrik - smaller on linear-lead bots
	static final int	BULLET_SPEED = 11;	// 3 power bullets travel at this speed.
	static final int	MAX_RANGE = 800;  	// Range where we're guarenteed to get a look-ahead lock
							// 1200 would be another good value as this is the max radar distance.
							// Yet too large makes it take longer to hit new movement patterns (Lemon)...
	static final int	SEARCH_END_BUFFER = SEARCH_DEPTH + MAX_RANGE / BULLET_SPEED;	// How much room to leave for leading
										
		// Globals
	static double 		arcLength[] = new double[100000];
	static StringBuffer patternMatcher = new StringBuffer("\0\3\6\1\4\7\2\5\b" + (char)(-1) + (char)(-4) + (char)(-7) + (char)(-2) + 
							  (char)(-5) + (char)(-8) + (char)(-3) + (char)(-6) + "This space filler for end buffer." +
							  "The numbers up top assure a 1 length match every time.  This string must be " +
							  "longer than SEARCH_END_BUFFER. - Mike Dorgan");
	
	//Code
	public void run() 
	{
		// Nice (cheap) Green/Blue color
		setColors(Color.green,null,null);

		turnRadarRightRadians(Double.POSITIVE_INFINITY);
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
    public void onScannedRobot(ScannedRobotEvent e) 
    {
        doMoebius(0, patternMatcher.length(), e, SEARCH_DEPTH, e.getBearingRadians() + getHeadingRadians());
    }

    private void doMoebius(int matchIndex, int historyIndex, ScannedRobotEvent e, int searchDepth, double targetBearing)
    {
		// Assign ArcMovement here to save a byte with the targetBearing assign.
		double arcMovement = e.getVelocity() * Math.sin(e.getHeadingRadians() - targetBearing);
																										
		// Move in a SHM oscollator pattern with a bit of random thrown in for good measure.
		setAhead(Math.cos(historyIndex>>4) * MOVEMENT_LENGTH * Math.random());
							
		// Try to stay equa-distance to the target -  a slight movement towards
		// target would help with corner death, but no room.
		setTurnRightRadians(e.getBearingRadians() + Math.PI/2);

		// Assume small aim increment so we can always fire.  Too much cost for gun turn check
		// Add simple power management code.  This keeps us alive a bit longer against bots we
		// have trouble locking on to.  Helps in melee as well.  It basically gives us 9 more shots.
		// -2 is better, but costs 1 more byte
		setFire(getEnergy()-1);

		// Cummulative radial velocity relative to us. This is the ArcLength that the enemy traces relative to us.  
		// ArcLength S = Angle (radians) * Radius of circle.
		arcLength[historyIndex+1] = arcLength[historyIndex] + arcMovement;
		
		// Add ArcMovement to lookup buffer.  Typecast to char so it takes 1 entry.
		patternMatcher.append((char)(arcMovement));

		// Do adjustable buffer pattern match.  Use above buffer to save all out of bounds checks... ;)
		do 
		{
			matchIndex = patternMatcher.lastIndexOf(
							patternMatcher.substring(historyIndex - --searchDepth),
							historyIndex-SEARCH_END_BUFFER);
		}
		while (matchIndex < 0); 

		// Update index to end of search		
		matchIndex += searchDepth;
		
		// Aim at target (asin() in front of sin would be better, but no room at 3 byte cost.)
		setTurnGunRightRadians(Math.sin( 
			(arcLength[matchIndex+((int)(e.getDistance()/BULLET_SPEED))]-arcLength[matchIndex])/e.getDistance() +
			targetBearing - getGunHeadingRadians() ));
				
		// Lock Radar Infinite style.  About a 99% lock rate - plus good melee coverage.
		setTurnRadarLeftRadians(getRadarTurnRemaining());
		
		// I'd love to drop a clearAllEvents() here for melee radar locking help, but no space.
	}
}

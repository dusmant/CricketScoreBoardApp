package com.example.android.scorekeeperapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnTouchListener ;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private int ballsThisOver = 0 ;
    private int oversBowled = 0 ;
    private int totalRuns = 0 ;
    private int extraRuns = 0 ;
    private int noBalls = 0 ;
    private int wideBalls = 0 ;
    private int wicketsGone = 0 ;
    private Player[] players = new Player[11] ;
    private Player playerWhoGotOut ;
    private Player nextBatsMan ;


    private int MAX_OVERS_TO_BE_BOWLED = 50 ; // For 50 over match, 2 is kept for testing purpose

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializePlayers();
        initializeDisplay() ;
    }

    private void initializePlayers(){
        for(int i = 0 ; i < 11 ; i++){
            players[i] = new Player(i) ;
        }

        //1st and 2nd player will start the batting innings
        players[0].startBatting();
        players[1].startBatting();

        players[0].faceTheBall(); //player1 is going to face the ball
    }

    private void initializeDisplay() {
        //We will initially do it for first 4 playser to check
        //how the initialization is working fine or not

        TextView t ;
        t = (TextView) findViewById(R.id.player1_status);
        t.setText("Batting"); //Player1 will start batting
        t = (TextView) findViewById(R.id.player2_status);
        t.setText("Batting"); //Player1 will start batting
        t = (TextView) findViewById(R.id.player1_score);
        t.setText("0"); //Player1 will start batting
        t = (TextView) findViewById(R.id.player2_score);
        t.setText("0"); //Player1 will start batting

        //Rest of the players are just in the list
        t = (TextView) findViewById(R.id.player3_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player4_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player5_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player6_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player7_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player8_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player9_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player10_status);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player11_status);
        t.setText(""); //YTB

        t = (TextView) findViewById(R.id.player3_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player4_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player5_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player6_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player7_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player8_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player9_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player10_score);
        t.setText(""); //YTB
        t = (TextView) findViewById(R.id.player11_score);
        t.setText(""); //YTB
    }

    private void extrasDisplay(int extra){
        TextView t = (TextView) findViewById(R.id.extra_runs);
        t.setText(extra + "");
    }

    public void onClickNoBall(View view){
        noBalls++ ;
        extraRuns++ ;
        totalRuns++ ;
        extrasDisplay(extraRuns) ;
        updateTotalRunDisplay();
    }

    public void onClickWideBall(View view){
        wideBalls++ ;
        extraRuns++ ;
        totalRuns++ ;
        extrasDisplay(extraRuns) ;
        updateTotalRunDisplay();
    }

    //This method takes care of only certain types of outs : bold, catch
    //Does not handle the run-out situations
    public void onClickOUT(View view){
        Log.v("onClickOUT","Entered into this method...") ;
        wicketsGone++ ;
        ballsThisOver++ ;
        oversAndBallsDisplay() ; // Update the display of the balls and overs, because with every wicket one ball is also going

        Log.v("onClickOUT","Updated the Player object, who got out...") ;

        if(wicketsGone < 10)  {
            playerWhoGotOut = players[whoIsBatting()] ; //This is the reference of the player, who was batting,
                                                      //but just now got out. His status is not yet changed, because till we find
                                                      //who is the next batsman, we can not change his status
            nextBatsMan = bringNextBatsManToBat() ; //bring the new batsman to bat
            //Update the player, who was facing the ball (of course, this does not take care of the run-out situations)
            playerWhoGotOut.gotOut(); //Now the players data structure is updated
            updateBatsmanStatusDisplayWhoGotOut(playerWhoGotOut) ; //update the scoreboard with the out going batsman
            Log.v("onClickOUT","Finished displaying the batsman's status who got out...") ;
            updateNextBatsmanStatusAndScoreDisplay(nextBatsMan) ;
        }

        else
            Toast.makeText(getApplicationContext(),"All OUT !!!", Toast.LENGTH_LONG).show();

        if(ballsThisOver == 6){ //One over got over
            ballsThisOver = 0 ;
            oversBowled++ ;
                changeBattingSide() ;
        }

        updateWicketsDisplay() ;

    }

    public void onClickSixRuns(View view){
        handleRunsHitOfABall(6) ; //6 runs is hit by the batsman
    }

    public void onClickFourRuns(View view){
        handleRunsHitOfABall(4) ; //4 runs is hit by the batsman
    }

    public void onClickThreeRuns(View view){
        handleRunsHitOfABall(3) ; //3 runs is hit by the batsman
    }

    public void onClickTwoRuns(View view){
        handleRunsHitOfABall(2) ; //2 runs is hit by the batsman
    }

    public void onClickOneRun(View view){
        handleRunsHitOfABall(1) ; //1 runs is hit by the batsman
    }

    public void onClickZeroRun(View view){
        handleRunsHitOfABall(0) ; //0 runs is hit by the batsman
    }

    //This method is the key method, which handles the runs made from a ball, including zero runs (duck balls)
    private void handleRunsHitOfABall(int runsHit){
        Log.v("MainActivity","Inside the handleRunsAndDisplay method, runsHit: "+runsHit) ;
        ballsThisOver++ ;

        //Update the batsman's score in the data-structure
        Player playerFacingTheBall = players[whoIsBatting()] ;
        playerFacingTheBall.madeRun(runsHit); //Ball facing player made 1 run
        totalRuns += runsHit ;  //Total runs increased by 1

        //Update the score board, with batsman's runs, total runs and everything else,
        //barring only the overs and balls, which will be updated next after the condition is checked.
        updateScoreBoard() ;

        if(ballsThisOver == 6){ //One over got over
            ballsThisOver = 0 ;
            oversBowled++ ;
            //Now the other batsman has to bat, because this batsman got either zero or two  or four or six runs and the over got over
            if(     (runsHit == 0) ||
                    (runsHit == 2) ||
                    (runsHit == 4) ||
                    (runsHit == 6) )
                   changeBattingSide() ;
        }else if((runsHit == 1) || (runsHit == 3)) //if the batsman hit 1 or 3 runs, the other batsman has to bat
                   changeBattingSide() ;

        oversAndBallsDisplay() ;
        checkIfAllOversGotOver() ;
    }

    private void checkIfAllOversGotOver(){
        if(oversBowled >= MAX_OVERS_TO_BE_BOWLED) { //Batting got over for this team for the OneDay Match
            //Inform the user that batting overs got over
            Toast.makeText(getApplicationContext(),"Batting Finished !!!", Toast.LENGTH_LONG).show(); ;
            disableAllButtons() ;
        }
    }

    //This method will update the scoreboard with the current runs and batsman runs, along with
    //total runs, with wickets gone, everything apart from the overs and balls part
    private void updateScoreBoard(){
        Player batsmanFacingTheBall = players[whoIsBatting()] ;
        Log.v("MainActivity","onClickOneRun:: BatsmanPosition :  "+
                (batsmanFacingTheBall.getPlayerNumher())) ;

        updateBattingBatsMansScoreDisplay(batsmanFacingTheBall) ;
        //updateExtraRunsDisplay() ;
        updateTotalRunDisplay() ;
    }

    //This method updates the wickets gone display
    private void updateWicketsDisplay() {
        TextView wktsDisplay = (TextView) findViewById(R.id.wickets_display) ;
        wktsDisplay.setText( wicketsGone + "");
    }

    //Bring the next batsman to bat and update his status and score on the score board.
    //Also update the radio button with the batsman's name, who is coming in to bat
    private Player bringNextBatsManToBat(){
        Log.v("bringNextBatsManToBat","Entered into this method...") ;
        int nextPlayerPosition = 0 ;
        Player nextBatsMan  ;

        Player gotOutPlayer = players[whoIsBatting()] ;//This is the player, who just now got out
        Player runnerPlayer = whoIsOnRunnerSide() ; //This is the player, who is on the runner side
        Log.v("bringNextBatsManToBat()","Got both the gotOutPlayer and the RunnerPlayer objects...") ;

        int runnerPlayerNumber = runnerPlayer.getPlayerNumher() ;
        Log.v("bringNextBatsManToBat()"," runnerPlayerNumber: "+runnerPlayerNumber) ;
        int gotOutPlayerNumber = gotOutPlayer.getPlayerNumher() ;
        Log.v("bringNextBatsManToBat()"," gotOutPlayerNumber: "+gotOutPlayerNumber) ;

        //What we are tring to do here is :
        //Find out the max position of the player that was batting,
        //because, the next player will come only after that max position,
        //irrespective of who got out !!!
        if( runnerPlayerNumber > gotOutPlayerNumber )
            nextPlayerPosition = runnerPlayerNumber + 1 ;
        else nextPlayerPosition =  gotOutPlayerNumber + 1 ;

        Log.v("bringNextBatsManToBat","nextPlayerPosition: "+ nextPlayerPosition) ;

        nextBatsMan = players[nextPlayerPosition];
        nextBatsMan.startBatting();
        nextBatsMan.faceTheBall(); //because he is coming at the positon of the batsman who was facing the ball
                                   //if current over is finished, then the swap will happen automatically



        return nextBatsMan ;
    }

    private void updateRadioButtonDisplay(Player whoGotOut, Player newBatsMan) {
        RadioButton batsMan1 = (RadioButton) findViewById(R.id.batsman1) ;
        RadioButton batsMan2 = (RadioButton) findViewById(R.id.batsman2) ;
        String batsMan1Name = (String) batsMan1.getText() ;
        String batsMan2Name = (String) batsMan2.getText() ;

        Log.v("updateRadioButton()","Before updating the radio button, BatsMan1 Name: "+batsMan1Name+
                                                    " BatsMan2 Name: "+batsMan2Name) ;

        String gotOutPlayerName = whoGotOut.name ;
        if(batsMan1Name.compareTo(gotOutPlayerName) == 0) {
            batsMan1.setText(newBatsMan.name);
        } else if(batsMan2Name.compareTo(gotOutPlayerName) == 0) {
            batsMan2.setText(newBatsMan.name);
        }
    }

    //This will update the status and score of the next batsman, who is coming in to bat
    private void updateNextBatsmanStatusAndScoreDisplay(Player nextBatsMan){
        TextView batsManStatusText = null, batsManScoreText = null;
        switch (nextBatsMan.getPlayerNumher()) {
            case 0 : { //Player number 1 -- Actually, this should never come here
                batsManStatusText = (TextView) findViewById(R.id.player1_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player1_score) ;
                break;
            }
            case 1 : { //Player number 2
                batsManStatusText = (TextView) findViewById(R.id.player2_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player2_score) ;
                break;
            }
            case 2 : { //Player number 3
                batsManStatusText = (TextView) findViewById(R.id.player3_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player3_score) ;
                break;
            }
            case 3 : { //Player number 4
                batsManStatusText = (TextView) findViewById(R.id.player4_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player4_score) ;
                break;
            }
            case 4 : { //Player number 5
                batsManStatusText = (TextView) findViewById(R.id.player5_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player5_score) ;
                break;
            }
            case 5 : { //Player number 6
                batsManStatusText = (TextView) findViewById(R.id.player6_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player6_score) ;
                break;
            }
            case 6 : { //Player number 7
                batsManStatusText = (TextView) findViewById(R.id.player7_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player7_score) ;
                break;
            }
            case 7 : { //Player number 8
                batsManStatusText = (TextView) findViewById(R.id.player8_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player8_score) ;
                break;
            }
            case 8 : { //Player number 9
                batsManStatusText = (TextView) findViewById(R.id.player9_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player9_score) ;
                break;
            }
            case 9 : { //Player number 10
                batsManStatusText = (TextView) findViewById(R.id.player10_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player10_score) ;
                break;
            }
            case 10 : { //Player number 11
                batsManStatusText = (TextView) findViewById(R.id.player11_status) ;
                batsManScoreText  = (TextView) findViewById(R.id.player11_score) ;
                break;
            }
        }
        if(batsManStatusText != null) {
            batsManStatusText.setText("Batting");
        }
        if(batsManScoreText != null) batsManScoreText.setText("0");
        updateRadioButtonDisplay(playerWhoGotOut, nextBatsMan ) ;
    }

    //This will update the status of the batsman, who got out
    private void updateBatsmanStatusDisplayWhoGotOut(Player batsmanFacingTheBall){
        TextView batsManStatusText = null;
        switch (batsmanFacingTheBall.getPlayerNumher()) {
            case 0 : { //Player number 1
                batsManStatusText = (TextView) findViewById(R.id.player1_status) ;
                break;
            }
            case 1 : { //Player number 2
                batsManStatusText = (TextView) findViewById(R.id.player2_status) ;
                break;
            }
            case 2 : { //Player number 3
                batsManStatusText = (TextView) findViewById(R.id.player3_status) ;
                break;
            }
            case 3 : { //Player number 4
                batsManStatusText = (TextView) findViewById(R.id.player4_status) ;
                break;
            }
            case 4 : { //Player number 5
                batsManStatusText = (TextView) findViewById(R.id.player5_status) ;
                break;
            }
            case 5 : { //Player number 6
                batsManStatusText = (TextView) findViewById(R.id.player6_status) ;
                break;
            }
            case 6 : { //Player number 7
                batsManStatusText = (TextView) findViewById(R.id.player7_status) ;
                break;
            }
            case 7 : { //Player number 8
                batsManStatusText = (TextView) findViewById(R.id.player8_status) ;
                break;
            }
            case 8 : { //Player number 9
                batsManStatusText = (TextView) findViewById(R.id.player9_status) ;
                break;
            }
            case 9 : { //Player number 10
                batsManStatusText = (TextView) findViewById(R.id.player10_status) ;
                break;
            }
            case 10 : { //Player number 11
                batsManStatusText = (TextView) findViewById(R.id.player11_status) ;
                break;
            }
        }
        if(batsManStatusText != null) batsManStatusText.setText("OUT");
    }

    //This will update the score card of the batsman, who is facing the ball !!!
    private void updateBattingBatsMansScoreDisplay(Player batsmanFacingTheBall){
        TextView batsManScoreText = null;
        switch (batsmanFacingTheBall.getPlayerNumher()) {
            case 0 : { //Player number 1
                batsManScoreText = (TextView) findViewById(R.id.player1_score) ;
                break;
            }
            case 1 : { //Player number 2
                batsManScoreText = (TextView) findViewById(R.id.player2_score) ;
                break;
            }
            case 2 : { //Player number 3
                batsManScoreText = (TextView) findViewById(R.id.player3_score) ;
                break;
            }
            case 3 : { //Player number 4
                batsManScoreText = (TextView) findViewById(R.id.player4_score) ;
                break;
            }
            case 4 : { //Player number 5
                batsManScoreText = (TextView) findViewById(R.id.player5_score) ;
                break;
            }
            case 5 : { //Player number 6
                batsManScoreText = (TextView) findViewById(R.id.player6_score) ;
//                batsManScoreText.setText(batsmanFacingTheBall.runsScored + "");
                break;
            }
            case 6 : { //Player number 7
                batsManScoreText = (TextView) findViewById(R.id.player7_score) ;
//                batsManScoreText.setText(batsmanFacingTheBall.runsScored + "");
                break;
            }
            case 7 : { //Player number 8
                batsManScoreText = (TextView) findViewById(R.id.player8_score) ;
//                batsManScoreText.setText(batsmanFacingTheBall.runsScored + "");
                break;
            }
            case 8 : { //Player number 9
                batsManScoreText = (TextView) findViewById(R.id.player9_score) ;
//                batsManScoreText.setText(batsmanFacingTheBall.runsScored + "");
                break;
            }
            case 9 : { //Player number 10
                batsManScoreText = (TextView) findViewById(R.id.player10_score) ;
//                batsManScoreText.setText(batsmanFacingTheBall.runsScored + "");
                break;
            }
            case 10 : { //Player number 11
                batsManScoreText = (TextView) findViewById(R.id.player11_score) ;
                break;
            }
        }
        if(batsManScoreText != null) batsManScoreText.setText(batsmanFacingTheBall.runsScored + "");
    }

    //This method will update the total score
    private void updateTotalRunDisplay(){
        TextView totalRunsScoredTextView =  (TextView) findViewById(R.id.total_score) ;
        totalRunsScoredTextView.setText(totalRuns + "");
    }

    //This method will make the other batsman bat
    private void changeBattingSide(){
        //Change in the players datastructure, who is going to face the ball now
        Player playerFacingTheBall = players[whoIsBatting()] ;
        Player runnerSidePlayer = whoIsOnRunnerSide() ;
        playerFacingTheBall.goToRunnersEnd(); //The batting player now goes to the runner end
        runnerSidePlayer.faceTheBall(); //The runner end player is now going to face the ball

        RadioButton batsMan1RdButton = (RadioButton) findViewById(R.id.batsman1);
        RadioButton batsMan2RdButton = (RadioButton) findViewById(R.id.batsman2);

        if(batsMan1RdButton.isChecked()) //If the batsman1 was batting
            batsMan2RdButton.setChecked(true); //now make batsman2 bat, because the bowling side got changed.
        else batsMan1RdButton.setChecked(true);

    }

    //Provides the player position, who is currently batting and facing the ball
    private int whoIsBatting(){
        Log.v("whoIsBatting()","Entered into this method...") ;
        for(int i=0 ; i < 11 ; i++){
            //Actually, we should only see, if the player is facingTheBall, then he is the one
            Log.v("whoIsBatting()","Player"+(i+1)+" Status: "+
                    players[i].getStatus()+" facing ball: "+players[i].isFacingTheBall()) ;
            if((players[i].getStatus().compareTo("BATTING") == 0) && (players[i].isFacingTheBall())){
                Log.v("MainActivity.java", "Player"+(i+1)+" is facing the ball") ;
                return i ;
            }

        }

        return -1 ; //if none of the players are, then this situation would come
    }

    //Provides the player object who is at the running end : Runner
    private Player whoIsOnRunnerSide(){
        Log.v("whoIsOnRunnerSide","Entered into this method...") ;
        for(int i=0 ; i < 11 ; i++){
            //Player is at the batting creach, but not facing the ball, basically at the runners end
            if((players[i].getStatus().compareTo("BATTING") == 0) && (!players[i].isFacingTheBall())){
                Log.v("MainActivity.java", "Player"+(i+1)+" is at the runner end") ;
                return players[i] ;
            }
        }
        return null ;
    }

    //This method will highlight the two batsmen in the scoreboard, so that it's easy to figure out in the view
    private void highlightCurrentBatsMen(){
        Player batsman = players[whoIsBatting()] ;
        Player runnerBatsMan = whoIsOnRunnerSide() ;
    }

    private void oversAndBallsDisplay(){
        TextView oversDisplay = (TextView) findViewById(R.id.overs_bowled);
        TextView ballsDisplay = (TextView) findViewById(R.id.balls_bowled);
        ballsDisplay.setText(ballsThisOver + "");
        oversDisplay.setText(oversBowled + "");
    }

    private void disableAllButtons(){
        Button tempBtnPtr = (Button) findViewById(R.id.zero_run);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.one_run);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.two_runs);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.three_runs);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.four_runs);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.six_runs);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.no_ball);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.wide_ball);
        tempBtnPtr.setEnabled(false);
        tempBtnPtr = (Button) findViewById(R.id.out);
        tempBtnPtr.setEnabled(false);
    }

    private class Player {
        private String name ; //This field will store the name of the player
        private int playerNumber ; //This is to get the player and link it to the display, where he comes
                                   //Is he player1 or player3 or player8
        private String status  ; //Yet to Bat(YTB), or BATTING or OUT
        private int runsScored  ;
        private boolean facingTheBall ; //This is going to be true only when the batsman is going to face the ball
                                        //He might be batting, but he still does not face the ball,
                                        //because he is at the runners end

        //Everytime a player object created, it would be initialized to YTB and 0 score
        public Player(int playerNum){
            playerNumber = playerNum ;
            status = "YTB" ; //yet TO Bat
            int runsScored = 0 ;
            facingTheBall = false ;
            name = "Player"+ (playerNum+1) ; //Actual string player number begins from 1, not from 0 like an array
        }

        //The player can make runs, only if he is facing the ball, otherwise, that's not a correct use of this API
        public void madeRun(int runs){
            if(facingTheBall == true) {
                runsScored += runs;
                if (status.equalsIgnoreCase("YTB")) {
                    status = "BATTING";
                }
            } else {
                Log.e("MainActivity.java","madeRun() method can not be invoked, because the BatsMan is not facing the ball") ;
            }
        }

        //Asking the batsman to face the ball, so that he can now make Runs
        public void faceTheBall(){ //Only if the player is in batting status
            if(status.compareTo("BATTING") == 0) {
                facingTheBall = true;
            }
        }

        //Asking the batsman to go to the runners end, can't make runs
        public void goToRunnersEnd() { //Only if the player is in batting status
            if(status.compareTo("BATTING") == 0){
                facingTheBall = false ;
            }
        }

        //This method will make the player go to the creach for batting. He might go to the batting creach or to the runners
        //creach, depending upon the situation.
        public void startBatting(){
            status = "BATTING";
        }

        public void gotOut(){
            status = "OUT" ;
            facingTheBall = false ;
        }

        public String getStatus(){
            return status ;
        }

        public boolean isFacingTheBall(){
            return facingTheBall ;
        }

        public int getPlayerNumher(){
            return playerNumber ;
        }
    }
}

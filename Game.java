//Hannah Ewing

package Game;
import java.util.*;
import javax.swing.*;

import java.io.*;
import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
public class Game
{
  private Grid grid;
  private int userRow;
  private int msElapsed;
  private int timesGet;
  private int timesAvoid;
  private int correctAnswers = 0;
  private int ans;
//  private int done;
//  private int count = 0;
  boolean askFlag = false;
  private String name;
  private int [] answerArray; 
  ArrayList<String> qList = new ArrayList<String>();
  
  public void printAnswerArray()
  {
	  System.out.println("here is the answerarray");
	  for (int i = 0; i < answerArray.length; i++)
		  System.out.print(answerArray[i]+" ");
	  System.out.println ();
  }//printAnswerArray
    
  public Game()
  {
    grid = new Grid(5, 10); //holds the grid used to store and display images
    userRow = 0; //keeps track of which row the user-controlled image appears in, on the left edge of the grid
    msElapsed = 0; //keeps track of the total milliseconds that have elapsed since the start of the game
    timesGet = 0; //total number of times the user has gotten the things they're supposed to get in the game
    timesAvoid = 0; //total number of times the user has been hit by the things they're supposed to avoid
    updateTitle();
    grid.setImage(new Location(userRow, 0), "knight.gif");
    answerArray = new int [40];
  }//Game
  
  public void play()
  {
	  loadQuestionsFromFile();
	  answer();
	  printAnswerArray();
	  JLabel welcomeMessage = new JLabel();
	  welcomeMessage.setFont (new Font 
	  			   ("Papyrus", Font.BOLD, 30) );   

	  welcomeMessage.setText ("Welcome to Knight Quest!");
	  JOptionPane.showMessageDialog (null, welcomeMessage,
	  				    "Phase II Game",
	  	                         JOptionPane.PLAIN_MESSAGE,
	  				     null);
	  
	  JLabel nameMessage = new JLabel();
	  nameMessage.setFont (new Font 
	  			   ("Papyrus", Font.BOLD, 30) );   

	  nameMessage.setText ("What is your name?");
	  name = JOptionPane.showInputDialog(null, nameMessage);
	  
	  JLabel storyMessage = new JLabel();
	  storyMessage.setFont (new Font 
	  			   ("Papyrus", Font.BOLD, 20) );   
	  
	  storyMessage.setText (name + " you're a knight trying to invade the castle."
	  		+ " You first must get past the fire-breathing dragon. Be sure to watch out for the dragon's fire.");
	  JOptionPane.showMessageDialog (null, storyMessage,
	  				    "Phase II Game",
	  	                         JOptionPane.PLAIN_MESSAGE,
	  				     null);
	  
	  JLabel story2Message = new JLabel();
	  story2Message.setFont (new Font 
	  			   ("Papyrus", Font.BOLD, 20) );   
	  
	  story2Message.setText ("If his fire hits you three times you'll have to begin your quest over again."
	  		+ " Along the way you will want to collect the swords to fight the dragon. To win the game"
	  		+ " you must answer 15 questions correctly.");
	  JOptionPane.showMessageDialog (null, story2Message,
	  				    "Phase II Game",
	  	                         JOptionPane.PLAIN_MESSAGE,
	  				     null);
	  		  
    while (!isGameOver())
    {
      handleKeyPress();
      grid.pause(100);
      if (msElapsed % 500 == 0)
      {
        scrollLeft();
        populateRightEdge();
      }//if
      updateTitle();
      msElapsed += 100;
    }//while
    JLabel closeMessage = new JLabel();
	  closeMessage.setFont (new Font 
	  			   ("Papyrus", Font.BOLD, 30) );   

	  closeMessage.setText ("Game over!");
	  JOptionPane.showMessageDialog (null, closeMessage,
	  				    "Phase II Game",
	  	                         JOptionPane.PLAIN_MESSAGE,
	  				     null);
  }//play
  
  public void scoreKeeper()
  {
	  Heap h = new Heap();
	  Object [] team = new Object [150];
	  int [] runs = new int [150];

	    try
	    {
	    	File scores = new File("Scores.txt");
	    	Scanner scan = new Scanner(scores);
	   
		    while (scan.hasNext())
		    {
		        String peopleName = scan.next();
		        int peopleScore = scan.nextInt();
		        h.insert(new String (peopleName),peopleScore);
		    }//while
	    }catch(IOException e)//try
	    {
	    	System.out.println(e.getStackTrace());
	    }//catch 
	    
	    h.insert(new String(name), getScore());
	    
	    int i = 0;
	    while (!h.isEmpty())
	    {
	        HeapEntry node = (HeapEntry) h.remove();
	        team[i] = node.getElement();
	        runs[i] = node.getPriority();
	        i++;
	    }//while
	    
	    JOptionPane.showMessageDialog(null, "Top Five Scores: \n" + team[0] + ' ' + runs[0] + "\n" + team[1] + ' ' + runs[1] + "\n" + team[2] + ' ' + runs[2]
	    		+ "\n" + team[3] + ' ' + runs[3] + '\n' + team[4] + ' ' + runs[4]);
	    
	    try
	    {
	    	FileOutputStream umpire = new FileOutputStream("Scores.txt", true);
	    	PrintWriter runner = new PrintWriter(umpire);
	    	
	    	runner.print(name + " ");
	    	runner.println(getScore());
	    	System.out.print(name + getScore());
	    	runner.close();
	    }catch(FileNotFoundException fnfe) //try
	    {
	    	System.out.println("Can't find scores.");
	    }//catch
  }//scoreKeeper
  
  public void handleKeyPress()
  {
	  int key = grid.checkLastKeyPressed();
	  grid.setImage(new Location(userRow, 0), null);
	  if (key ==38)//up arrow
	  {
		  
		  if (userRow > 0)
		  {
			  handleCollision(new Location(userRow-1, 0));
			  userRow--;	
		  }//if
	  }//if
	  
	  if (key==40)//down arrow
	  {
		  
		  if (userRow < grid.getNumRows()-1)
		  {
			  handleCollision(new Location(userRow+1, 0));
			  userRow++;
		  }//if
	  }//if
	  grid.setImage(new Location(userRow, 0), "knight.gif");
  }//handleKeyPress
  
  public void populateRightEdge()
  {
	  for (int i = 1; i<=grid.getNumRows(); i++)
	  {
		  double chance = Math.random();
		  if (chance>0.90)
			  grid.setImage(new Location(grid.getNumRows()-i, grid.getNumCols()-1), "Fireball2.gif");
		  else if (chance>0.80)
			  grid.setImage(new Location(grid.getNumRows()-i, grid.getNumCols()-1), "sword2.gif");
		  else
			  grid.setImage(new Location(grid.getNumRows()-i, grid.getNumCols()-1), null);
	  }//for
  }//populateRightEdge
  
  public void scrollLeft()
  {
	  askFlag = timesGet % 5 == 0;
	  handleCollision(new Location(userRow, 1));
	  for (int r = 0; r<grid.getNumRows(); r++)
	  {
		  for (int c = 0; c<grid.getNumCols()-1; c++)
		  {
			 
			  grid.setImage(new Location(r,c), grid.getImage(new Location(r,c+1)));
			  grid.setImage(new Location(r,c+1), null);
		  }//for
	  }//for
  }//scrollLeft
  
  public void handleCollision(Location loc)
  {
	  if (grid.getImage(loc)!=null) //collision
	  {
		  if (grid.getImage(loc).equals("Fireball2.gif"))
		  {
			timesAvoid++;
			grid.setImage(loc, "exploding fireball.gif");
		  	System.out.println("There was a Collision.");
		  }//if
		  else//must be a sword
		  {
			timesGet++;
		  }//else
		  System.out.println("timesGet" + timesGet);
	  }//if
	  
		  if (timesGet % 5==0 && timesGet >1 && askFlag)
		  {
			  Random r = new Random();
			  int n = r.nextInt(40);
			  //ask a question
			  JLabel questionMessage = new JLabel();
			  questionMessage.setFont (new Font 
			  			   ("Papyrus", Font.BOLD, 30) );   
	
			  questionMessage.setText (qList.get(n));
			  String input = JOptionPane.showInputDialog(null, questionMessage);
			  int userInput = Integer.parseInt(input);
			  timesGet++;
			  askFlag = false;

			  
			  if (answerArray[n] == userInput)
			  {
				    JLabel rightMessage = new JLabel();
					  rightMessage.setFont (new Font 
					  			   ("Papyrus", Font.BOLD, 30) );   

					  rightMessage.setText ("Good Job!");
					  JOptionPane.showMessageDialog (null, rightMessage,
					  				    "Phase II Game",
					  	                         JOptionPane.PLAIN_MESSAGE,
					  				     null);
					  System.out.println(answerArray[n] + " " + userInput);
					  correctAnswers++;
			  }//if
			  else
			  {
				    JLabel wrongMessage = new JLabel();
					  wrongMessage.setFont (new Font 
					  			   ("Papyrus", Font.BOLD, 30) );   

					  wrongMessage.setText ("Not quite. Keep trying! You can do it!");
					  JOptionPane.showMessageDialog (null, wrongMessage,
					  				    "Phase II Game",
					  	                         JOptionPane.PLAIN_MESSAGE,
					  				     null);
			  }//else
		  }//if
  }//handleCollision
  
  public int getScore()
  {
    return timesGet;
  }//getScore
  
  public void updateTitle()
  {
    grid.setTitle("Score:  " + getScore() + "      You have used " + timesAvoid + " lives");
  } //updateTitle
 
  public class Question<E>  
  {
      private Question<E> next;
      private Question<E> prev;
      private E element;

      public Question()
      {
          next = null;
          prev = null;
          element = null;
      }//Question

      public Question (E elem)
      {
          next = null;
          prev = null;
          element = elem;
      }
      
      public Question<E> getNext()
      {
          return next;
      }//getNext

      public Question<E> getPrev()
      {
         return prev;
      }//getPrev

      public void setNext (Question<E> node)
      {
          next = node;
      }//setNext

      public void setPrev (Question<E> node)
      {
          prev = node;
      }//setPrev
      
      public E getElement()
      {
          return element;
      }//getElement

      public void setElement (E elem)
      {
          element = elem;
      }//setElement
  }//end Question

  
  public boolean isGameOver()
  {
	  if (timesAvoid==3)
	  {
		  double percentDec = correctAnswers / 30;
		  double percent = percentDec * 100;
		    JLabel lostMessage = new JLabel();
			  lostMessage.setFont (new Font 
			  			   ("Papyrus", Font.BOLD, 30) );   

			  lostMessage.setText ("You are " + percent + "% of the way to completing the quest.");
			  JOptionPane.showMessageDialog (null, lostMessage,
			  				    "Phase II Game",
			  	                         JOptionPane.PLAIN_MESSAGE,
			  				     null);
			 System.out.println(correctAnswers); 
			scoreKeeper();  
			  
		  grid.closeFrame();
		  return true;
	  }  //if
	  else if (correctAnswers ==15)
	  {
		    JLabel wonMessage = new JLabel();
			  wonMessage.setFont (new Font 
			  			   ("Papyrus", Font.BOLD, 30) );   

			  wonMessage.setText ("You won the game!");
			  JOptionPane.showMessageDialog (null, wonMessage,
			  				    "Phase II Game",
			  	                         JOptionPane.PLAIN_MESSAGE,
			  				     null);
		  grid.closeFrame();
		  return true;
	  }//else if
	  else
		  return false;
  }//isGameOver
  
  public static void test()
  {
    Game game = new Game();
    game.play();
  }//test
  
  
  public void loadQuestionsFromFile()
  //TASK:  This method will open the file and read all the questions into the ArrayList
  { 
	  String que;
	  Scanner q;
	  try {
		  	if (correctAnswers < 11)
		  	{
	  		q = new Scanner(new File("gameQuestion.txt"));
	  		while (q.hasNext())
	  	    {
	  		  que = q.nextLine();
	  		  qList.add(que);
	  	    }//while
	  		
	  		q.close();
		  	}//if
		  	else if (correctAnswers < 21)
		  	{
		  		q = new Scanner(new File("Level 2.txt"));
		  		while (q.hasNext())
		  	    {
		  		  que = q.nextLine();
		  		  qList.add(que);
		  	    }//while
		  	}//else if
		  	else
		  	{
		  		q = new Scanner(new File("Level 3.txt"));
		  		while (q.hasNext())
		  	    {
		  		  que = q.nextLine();
		  		  qList.add(que);
		  	    }//while
		  	}//else
	  	  }//try 
	      catch (IOException E)
	  	  {
	    	  System.err.println("Error opening the file");
	  	  }//catch
  }//loadQuestionsFromFile
  
  public void answer()
  {
	  String que;
	  char a;
	  char b;
	  String e;
	  String f;
	  Scanner q;
	  try {
			  if (correctAnswers < 11)
			  {
		  		q = new Scanner(new File("gameQuestion.txt"));
		  		while (q.hasNext())
		  		{
		  			for (int i = 0; i < 40; i++)
		  			{
				  		  que = q.nextLine();
				  		  System.out.println(que);
				  		  if (que.length()==14)//both are single digit integers
				  		  {
				  			  a = que.charAt(8);//first int as string
				  			  
				  			  b = que.charAt(12);//second int as string
				  			  int c = a-48;//makes first int into an int
				  			  int d = b-48;//makes second int into an int
				  			  ans = c * d;//multiplies the numbers to give you an answer
				  			  System.out.println(ans);
				  			  answerArray[i] = ans;
				  			  
				  			  System.out.print("This is the answerArray value: " + answerArray[i]);
				  		  }// if
				  		  else if (que.length()==15)//one single one double
				  		  {
				  			 e = que.substring(8,9);
				  			 f = que.substring(12,14);
				  			 int c = Integer.parseInt(e);//makes first int into an int
				  			 int d = Integer.parseInt(f);//makes second int into an int
				  			 ans = c * d;//multiplies the numbers to give you an answer
				  			 answerArray[i] = ans;
				  		  }	//else if
				  		  else//double double
				  		  {
				  			  e = que.substring(8,10);//first int as string
				  			  f = que.substring(13,15);//second int as string
				  			  int c = Integer.parseInt(e);//makes first int into an int
				  			  int d = Integer.parseInt(f);//makes second int into an int
				  			  ans = c * d;//multiplies the numbers to give you an answer
				  			  answerArray[i] = ans;
				  		  }//else
		  			}//for
		  		} //while
		  		q.close();
			  } //if
	  		else if (correctAnswers < 21)
	  		{
	  			q = new Scanner(new File("Level 2.txt"));
	  			while (q.hasNext())
	  			{
	  				for (int i = 0; i < 41; i++)
	  				{
	  					que = q.nextLine();
				  		  System.out.println(que);
				  		  if (que.length()==14)//both are single digit integers
				  		  {
				  			  a = que.charAt(8);//first int as string
				  			  
				  			  b = que.charAt(12);//second int as string
				  			  int c = a-48;//makes first int into an int
				  			  int d = b-48;//makes second int into an int
				  			  ans = c * d;//multiplies the numbers to give you an answer
				  			  answerArray[i] = ans;
				  			  System.out.print("This is the answerArray value: " + answerArray[i]);
				  		  } //if
				  		  else if (que.length()==15)//one single one double
				  		  {
				  			 e = que.substring(8,9);
				  			 f = que.substring(12,14);
				  			 int c = Integer.parseInt(e);//makes first int into an int
				  			 int d = Integer.parseInt(f);//makes second int into an int
				  			 ans = c * d;//multiplies the numbers to give you an answer
				  			 answerArray[i] = ans;
				  		  }	//else if
				  		  else//double double
				  		  {
				  			  e = que.substring(8,10);//first int as string
				  			  f = que.substring(13,15);//second int as string
				  			  int c = Integer.parseInt(e);//makes first int into an int
				  			  int d = Integer.parseInt(f);//makes second int into an int
				  			  ans = c * d;//multiplies the numbers to give you an answer
				  			  answerArray[i] = ans;
				  		  }//else
			  		}//for
	  			}//while
	  			q.close();
	  		} //else if
	  		
	  		else
	  		{
	  			q = new Scanner(new File("Level 3.txt"));
	  			while (q.hasNext())
	  			{
	  				for (int i = 0; i < 41; i++)
	  				{
	  					que = q.nextLine();
				  		  System.out.println(que);
				  		  if (que.length()==14)//both are single digit integers
				  		  {
				  			  a = que.charAt(8);//first int as string
				  			  
				  			  b = que.charAt(12);//second int as string
				  			  int c = a-48;//makes first int into an int
				  			  int d = b-48;//makes second int into an int
				  			  ans = c * d;//multiplies the numbers to give you an answer
				  			  answerArray[i] = ans;
				  			  System.out.print("This is the answerArray value: " + answerArray[i]);
				  		  } //if
				  		  else if (que.length()==15)//one single one double
				  		  {
				  			 e = que.substring(8,9);
				  			 f = que.substring(12,14);
				  			 int c = Integer.parseInt(e);//makes first int into an int
				  			 int d = Integer.parseInt(f);//makes second int into an int
				  			 ans = c * d;//multiplies the numbers to give you an answer
				  			 answerArray[i] = ans;
				  		  }	//else if
				  		  else//double double
				  		  {
				  			  e = que.substring(8,10);//first int as string
				  			  f = que.substring(13,15);//second int as string
				  			  int c = Integer.parseInt(e);//makes first int into an int
				  			  int d = Integer.parseInt(f);//makes second int into an int
				  			  ans = c * d;//multiplies the numbers to give you an answer
				  			  answerArray[i] = ans;
				  		  }//else
			  		}//for
	  			}//while
	  			q.close();
	  		}//else
	  		} catch (IOException E) //try
	  	    {}  
  }//answer
  
  public void playAgain()
  {
  	Object [] candidates = {"Yes", "No"};

    boolean playAgain = true;
    while(playAgain)
    {
      	int ans = JOptionPane.showOptionDialog(null, "Do you want to play again?", "Title", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, candidates, candidates[0]);

  	if(ans == 0)
  	{
    	Game game = new Game();
	game.play();
  	}//if
  	else
  		playAgain = false;
    }//while
  }//playAgain
  public static void main(String[] args)
  {
    	Game game = new Game();
    	game.play();
    	game.playAgain();

    System.exit(0);
  }//main
}//Game
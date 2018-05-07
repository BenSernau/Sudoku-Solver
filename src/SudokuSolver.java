import java.io.*;

import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

//DISCLAIMER: Download sat4j-sat4j-sat-v20130419.zip from sat4j.org, extract it to your location of choice, and add it as a library to this project in the IDE.

//puz0 is unsolvable. 97 milliseconds.  A little short.  I guess that makes sense, since it's unsolvable.
//puz1 is solvable. 107 milliseconds.
//puz2 is solvable. 125 milliseconds.
//puz3 is solvable. 100 milliseconds.
//puz4 is solvable. 128 milliseconds.
//puz5 is solvable. 111 milliseconds.
//puz6 is solvable. 109 milliseconds.
//puz7 is solvable. 124 milliseconds.
//puz8 is solvable. 114 milliseconds.
//puz9 is solvable. 135 milliseconds.  This one must be challenging.
//The program works basically in 0.12 seconds.

/*

For example, puz6 looks like this:

3 3
0 0 0 5 0 3 0 0 0 
0 0 0 0 6 0 7 0 0 
5 0 8 0 0 0 0 1 6 
3 6 0 0 2 0 0 0 0 
0 0 0 4 0 1 0 0 0 
0 0 0 0 3 0 0 0 5 
6 7 0 0 0 0 2 0 8 
0 0 4 0 7 0 0 0 0 
0 0 0 2 0 0 5 0 0 

The solution looks like this:

puz6 has been solved!  Here's how it looks:

7 4 6 5 1 3 8 9 2 
1 3 2 8 6 9 7 5 4 
5 9 8 7 4 2 3 1 6 
3 6 7 9 2 5 4 8 1 
9 2 5 4 8 1 6 7 3 
4 8 1 6 3 7 9 2 5 
6 7 9 1 5 4 2 3 8 
2 5 4 3 7 8 1 6 9 
8 1 3 2 9 6 5 4 7 

RUNTIME: 140 milliseconds

*/

public class SudokuSolver
{
	static long runTime = System.currentTimeMillis();
	static int clauseNum = 11988;
	static boolean[][][] puzzleBools = new boolean[9][9][9]; //Store the 700+ booleans.
	static int[][] puzzleNums = new int[0][0];
	static File fileToPassSolver = new File("puzzle.cnf");
	
	static boolean certifier(String problem, int[] solution)
	{
		if (solution == null)
		{
			System.out.println("There exists no solution to " + problem + ".  Sorry, fam.");
			return false;
		}
		
		else
		{
			System.out.println(problem + " has been solved!  Here's how it looks:\n");
			
			int solutionCursor = 0;
			
			for (int row = 0; row < puzzleBools.length; row++)
			{
				for (int column = 0; column < puzzleBools[row].length; column++)
				{
					for (int value = 0; value < puzzleBools[row][column].length; value++)
					{
						if (solution[solutionCursor] > 0)
						{
							puzzleNums[row][column] = value;
						}
						
						solutionCursor++;
					}
				}
			}
			
			for (int row = 0; row < puzzleNums.length; row++)
			{
				for (int column = 0; column < puzzleNums[row].length; column++)
				{
					System.out.print((puzzleNums[row][column] + 1) + " ");
				}
				
				System.out.print("\n");
			}
			
			return true;
		}
	}
	
	static int[][] readPuzzle(String puzzleToRead)
	{
		int dimensionX = 0; //How wide is each box within the puzzle?  For our intents and purposes, this is basically not applicable.
		int dimensionY = 0; //How tall is each box within the puzzle?  For our intents and purposes, this is basically not applicable.
		int rowIndex = 0; //Keep track of whatever row we're currently reading.
		int[][] puzzleToSolve = new int[0][0]; //Store the numbers in here, remembering their places.
		String currLine = null; //This is for storing the current line.
		String[] currRow = new String[0]; //This is for using the split function.

		try 
		{
			BufferedReader puzzleBufferedReader = new BufferedReader(new FileReader(puzzleToRead));

			while ((currLine = puzzleBufferedReader.readLine()) != null) 
			{
				if (currLine.indexOf('c') == 0)
				{
					continue;
				}

				else if (currLine.length() == 3) //The prerequisites for a sudoku puzzle probably won't go into double digits or even change from 3 x 3.  If the previous lines were comments, and if this one has only a length of 3, then it's certainly the dimensions of the puzzle.
				{
					dimensionX = Integer.parseInt(currLine.substring(0, 1));
					dimensionY = Integer.parseInt(currLine.substring(2, 3));
					puzzleToSolve = new int[(int) Math.pow(dimensionX, 2)][(int) Math.pow(dimensionY, 2)];
				}

				else
				{
					currRow = currLine.split(" ");

					for (int columnIndex = 0; columnIndex < puzzleToSolve.length; columnIndex++)
					{
						puzzleToSolve[rowIndex][columnIndex] = Integer.parseInt(currRow[columnIndex]);
					}

					rowIndex++;
				}
			}

			puzzleBufferedReader.close();
		}

		catch(FileNotFoundException ex) 
        {
            System.out.println("The file cannot open.");                
        }

        catch(IOException ex) 
        {
			ex.printStackTrace();
        }	

        return puzzleToSolve;
	}

	static void setBooleans() 
	{
		for (int row = 0; row < puzzleBools.length; row++)
		{
			for (int column = 0; column < puzzleBools[row].length; column++)
			{
				for (int value = 0; value < puzzleBools[row][column].length; value++)
				{
					if (puzzleNums[row][column] - 1 == value)
					{
						puzzleBools[row][column][value] = true;
						clauseNum++;
					}

					else
					{
						puzzleBools[row][column][value] = false;
					}
				}
			}
		}
	}
	
	public static void main(String [] args)
	{
		puzzleNums = readPuzzle(args[0]);

		setBooleans();

		try
		{
			BufferedWriter cnfWriter = new BufferedWriter(new FileWriter(fileToPassSolver));
			cnfWriter.write("p cnf 729 " + clauseNum);
			cnfWriter.newLine();

			//Create each row.

			for (int row = 0; row < 9; row++)
			{
				for (int value = 0; value < 9; value++)
				{
					for (int column = 0; column < 9; column++)
					{
						cnfWriter.write((row * 81 + column * 9 + value + 1) + " ");
					}

					cnfWriter.write("0");
					cnfWriter.newLine();

					for (int column_l = 0; column_l < 9; column_l++) //The 36 clauses: (-column_l v -column_r)
					{
						for (int column_r = column_l + 1; column_r < 9; column_r++) 
						{
							cnfWriter.write("-" + (row * 81 + column_l * 9 + value + 1) + " -" + (row * 81 + column_r * 9 + value + 1) + " ");
							cnfWriter.write("0");
							cnfWriter.newLine();
						}
					}
				}
			}

			//Create each column.

			for (int column = 0; column < 9; column++)
			{
				for (int value = 0; value < 9; value++)
				{
					for (int row = 0; row < 9; row++)
					{
						cnfWriter.write((row * 81 + column * 9 + value + 1) + " ");
					}

					cnfWriter.write("0");
					cnfWriter.newLine();

					for (int row_l = 0; row_l < 9; row_l++) //The 36 clauses: (-row_l v -row_r)
					{
						for (int row_r = row_l + 1; row_r < 9; row_r++)  
						{
							cnfWriter.write("-" + (column * 81 + row_l * 9 + value + 1) + " -" + (column* 81 + row_r * 9 + value + 1) + " ");
							cnfWriter.write("0");
							cnfWriter.newLine();
						}
					}
				}
			}

			//Create each box.

			for (int box_w = 0; box_w < 3; box_w++)
			{
				int box_w_min = (3 * box_w); //For boxes 0 through 8, start at 0, then proceed to 3, and finally proceed to 6.

				for (int box_h = 0; box_h < 3; box_h++) //For boxes 0 through 8, start at 0, then proceed to 3, and finally proceed to 6.
				{
					int box_h_min = (3 * box_h);

					for (int value = 0; value < 9; value++)
					{
						for (int cursor_w = 0; cursor_w < 3; cursor_w++) //This is the horizontal cursor within the current box.
						{
							for (int cursor_h = 0; cursor_h < 3; cursor_h++) //This is the vertical cursor within the current box.
							{
								cnfWriter.write(((box_w_min + cursor_w) * 81 + (box_h_min + cursor_h) * 9 + value + 1) + " ");
							}
						}

						cnfWriter.write("0");
						cnfWriter.newLine();

						for (int cursor_w_l = 0; cursor_w_l < 3; cursor_w_l++)
						{
							for (int cursor_h_l = 0; cursor_h_l < 3; cursor_h_l++)
							{
								int cursor = Integer.parseInt(cursor_w_l + "" + cursor_h_l); //Track the current position in the box so that nothing earlier (higher up or further left) is compared to it.

								for (int cursor_w_r = 0; cursor_w_r < 3; cursor_w_r++)
								{
									for (int cursor_h_r = 0; cursor_h_r < 3; cursor_h_r++)
									{
										if (Integer.parseInt(cursor_w_r + "" + cursor_h_r) > cursor)
										{
											cnfWriter.write("-" + ((box_w_min + cursor_w_l) * 81 + (box_h_min + cursor_h_l) * 9 + value + 1) + " -" + ((box_w_min + cursor_w_r) * 81 + (box_h_min + cursor_h_r) * 9 + value + 1) + " ");
											cnfWriter.write("0");
											cnfWriter.newLine();
										}
									}
								}
							}
						}
					}
				}
			}

			//Create each cell.

			for (int row = 0; row < 9; row++)
			{
				for (int column = 0; column < 9; column++)
				{
					for (int value = 0; value < 9; value++)
					{
						cnfWriter.write((row * 81 + column * 9 + value + 1) + " ");
					}

					cnfWriter.write("0");
					cnfWriter.newLine();

					for (int value_l = 0; value_l < 9; value_l++)
					{
						for (int value_r = value_l + 1; value_r < 9; value_r++) 
						{
							cnfWriter.write("-" + (row * 81 + column * 9 + value_l + 1) + " -" + (row * 81 + column * 9 + value_r + 1) + " ");
							cnfWriter.write("0");
							cnfWriter.newLine();
						}
					}
				}
			}

			for (int row = 0; row < 9; row++)
			{
				for (int column = 0; column < 9; column++)
				{
					for (int value = 0; value < 9; value++)
					{
						if (puzzleBools[row][column][value])
						{
							cnfWriter.write(row * 81 + column * 9 + value + 1 + " 0");
							cnfWriter.newLine();
						}
					}
				}
			}

			cnfWriter.close();
			
			int[] assignment = SATSolver.solve("puzzle.cnf");
			
			certifier(args[0], assignment);
		}

		catch(FileNotFoundException ex) 
        {
            System.out.println("The file cannot open.");                
        }

        catch(IOException ex) 
        {
			ex.printStackTrace();
        }	
		
		catch(ParseFormatException ex)
		{
			ex.printStackTrace();
		}
		
		catch(ContradictionException ex)
		{
			ex.printStackTrace();
		}
		
		catch(TimeoutException ex)
		{
			ex.printStackTrace();
		}
		
		runTime = System.currentTimeMillis() - runTime;
		
		System.out.println("\nRUNTIME: " + runTime + " milliseconds"); //This is for acquiring the run time.
	}
}


import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

public class halpern_p1
{
	private static int TRANSITION_SIZE = 100000; // Up to 100,000 transitions
	private static int MAX_STATES_SIZE = 1001; // [0,1000] inclusive
	private static boolean print = false;

	private static int id_counter = 0;
	private static HashSet<Integer> nodes_hit = new HashSet<>();
	private static boolean atAcceptState = false;

	public static void main(String[] args)
	{
		/*
		 * Compiled as such:
		 * states[x][0] = STATE_NUMBER
		 * states[x][1] = START
		 * states[x][2] = ACCEPT
		 * states[x][3] = VALID_BIT
		 * Start state will ALWAYS be the first state
		 */
		String states[][] = new String[MAX_STATES_SIZE][4];

		/*
		 * Compiled as such:
		 * transitions[x][0] = CURRENT_STATE
		 * transitions[x][1] = INPUT_STRING_VALUE
		 * transitions[x][2] = NEW_STATE
		 * transitions[x][3] = VALID_BIT
		 */
		String transitions[][] = new String[TRANSITION_SIZE][4];

		// File to open && string to run through
		if (args.length < 2)
			System.exit(1);

		String fileName = args[0];
		String testString = args[1];

		// File reader scanner
		Scanner sc = null;

		try
		{
			sc = new Scanner(new File(fileName));
		}
		catch (Exception e)
		{
			System.exit(2);
		}

		// Read file into the buffers
		int states_index = 1, transitions_index = 0;

		while(sc.hasNextLine())
		{
			String[] readLine = sc.nextLine().split("\t", -1);

			if (readLine[0].equals("state"))
			{
				if (readLine.length == 4)
				{
					states[0][0] = readLine[1]; // STATE_NUM
					states[0][1] = "start"; // START_STATE
					states[0][2] = "accept"; // STATE_NUM
					states[0][3] = "1";
				}
				else
				{
					if (readLine[2].equals("start"))
					{
						states[0][0] = readLine[1]; // STATE_NUM
						states[0][1] = "start"; // START_STATE
						states[0][2] = ""; // START_STATE
						states[0][3] = "1";
					}
					else if (readLine[2].equals("accept"))
					{
						states[states_index][0] = readLine[1]; // STATE_NUM
						states[states_index][1] = ""; // START_STATE
						states[states_index][2] = "accept"; // STATE_NUM
						states[states_index][3] = "1";
						states_index++;
					}
				}

			}
			else if (readLine[0].equals("transition"))
			{
				transitions[transitions_index][0] = readLine[1]; // CURRENT_STATE
				transitions[transitions_index][1] = readLine[2]; // INPUT_STRING
				transitions[transitions_index][2] = readLine[3]; // NEXT_STATE
				transitions[transitions_index][3] = "1";
				transitions_index++;
			}
			else
			{
				// Shouldn't be here??
				System.exit(3);
			}
		}

		/*
		 * Starting the program, we have one node that runs and it can make more nodes
		 * as it progresses through the state.
		 * Don't actually need the result of this, so discard it 
		 */
		NodeRunner(id_counter++, states[0][0], testString, states, transitions);

		// See if we have accept/reject state
		for (int i = 0; i < states.length; i++)
		{
			if (states[i][3] == null)
				break;

			if (nodes_hit.contains(Integer.parseInt(states[i][0])) && states[i][2].equals("accept"))
			{
				atAcceptState = true;
				break;
			}
		}

		// Go through the accept/reject service now
		if (!atAcceptState)
		{
			System.out.print("reject ");
			for (Integer s : nodes_hit)
				System.out.print(s + " ");
			System.out.println();
		}
		else
		{
			System.out.print("accept ");
			for (int i = 0; i < states.length; i++)
			{
				if (states[i][3] == null)
					break;

				if (nodes_hit.contains(Integer.parseInt(states[i][0])) && states[i][2].equals("accept"))
					System.out.print(states[i][0] + " ");
			}
			System.out.println();
		}

		// Reset all the variables as this was not written in OOP format... sorry <3
		reset();
	}

	public static void NodeRunner(int id, String state, String testString, String[][] states, String[][] transitions)
	{
		if (print)
			System.out.println("NodeRunner #" + id + " (state:" + state + " string:" + testString + ")");

		if (testString.length() == 0)
		{
			/* This is an end state for the node
			 * As such, we need to log it as a potential end state
			 * Attempt to add the final state to the HashMap (will fail if already there)
			 */
			nodes_hit.add(Integer.parseInt(state));

			return;
		}

		/*
		 * This will take one character at a time
		 * It will also advance the string; (i.e. "abcde" => "bcde")
		 */
		String nextInput = testString.substring(0,1);
		testString = testString.substring(1);

		/*
		 * Thought process:
		 * NodeRunners will take only ONE step.
		 * They will log what their end states were
		 * They will also for() to create new NodeRunner(s) based on the transition
		 * list and give them the new part of the substring
		 * 
		 */
		for (int i = 0; i < transitions.length; i++)
		{
			if (transitions[i][3] == null)
				break;

			// We have matched "transition P x q"
			if (transitions[i][0].equals(state))
			{
				// We have matched "transition P X q"
				if (nextInput.equals(transitions[i][1]))
				{
					// Create new runner that starts in Q
					// Test string was already substring'd to omit first entry
					NodeRunner(id_counter++, transitions[i][2], testString, states, transitions);
				}
			}
		}

		return;
	}

	public static void reset()
	{
		// Since I didn't objectify this, we're gonna have to reset the state of the program after each run... sorry <3
		id_counter = 0;
		nodes_hit = new HashSet<Integer>();
		atAcceptState = false;
	}
}

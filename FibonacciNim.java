// Packages
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

// Classes
public class FibonacciNim {
    // Finals
    static final int COINS_PER_HEAP = 13;
    static final int HEAPS = 3;

    // Methods
    // Method used to count coins in all heaps recursively
    public static int counting(List<Integer> coinHeap, boolean print) {
        int countedHeap = 0;

        for (int i = 1; i < coinHeap.size(); i++) {
            int value = coinHeap.get(i);
            countedHeap += value;

            // If I want to also display new coin count
            if (print) {
                // Making sure there is no comma at the end via index
                if (i == 1) {
                    System.out.print("Remaining coins: ");
                } else {
                    System.out.print(", ");
                }

                System.out.print(value);
            }
        }

        return countedHeap;
    };

    // Main
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        List<Integer> coinHeaps = new ArrayList<>(); // [Heap number: Amount of coins]
        List<Integer> priorTurn = new ArrayList<>(); // [Heap number: Last amount taken]
        List<Boolean> usedResets = new ArrayList<>(); // [Player: Used a heap reset]

        int currentPlayer = 1; // Shortened number for sake of simplicity
        int chosenHeapStorage = 0; // How much coins is stored in heap
        int totalHeapAmount = 1; // Combined heap coins

        // Initiate and assign every n heap a value of coins
        for (int i = 1; i <= (HEAPS + 1); i++) {
            coinHeaps.add(COINS_PER_HEAP);
            priorTurn.add(0);
        }

        // Setting up players & quickly displaying coin count
        usedResets.add(false);
        usedResets.add(false);
        counting(coinHeaps, true);

        // While there is more than 0 coins, have game play
        while (totalHeapAmount > 0) {
            // Shortened strings to save space & time
            final String startingErr = "Sorry you must enter an integer in the range ";
            final String totalErr = startingErr + -HEAPS + " to " + HEAPS +", excluding zero.";
            final String chooseHeap = "\nPlayer " + currentPlayer + ": choose a heap: ";
            final String illegalCoinCount = "Sorry that's not a legal number of coins for that heap.\n";
            int chosenHeap;

            // Until chosen heap is within the possible range ask the user
            do {
                System.out.print(chooseHeap);

                // Type checking
                while (!in.hasNextInt()) {
                    System.out.print(totalErr);
                    System.out.print(chooseHeap);
                    in.next();
                    in.nextLine();
                }

                // Select the next heap and then get the index to align with the list,
                chosenHeap = in.nextInt();
                final int correctedIndex = currentPlayer - 1;
                final boolean alreadyReset = usedResets.get(correctedIndex);

                if (chosenHeap < -HEAPS || chosenHeap > HEAPS || chosenHeap == 0) {
                    System.out.print("Sorry that's not a legal heap choice.");
                } else if ((chosenHeap < 0 && coinHeaps.get(-chosenHeap) != null) && (!alreadyReset)) {
                    coinHeaps.set(-chosenHeap, COINS_PER_HEAP); // Fill heap back to max
                    usedResets.set(correctedIndex, true); // Register user's reset
                    priorTurn.set(-chosenHeap, 0); // Reset prior takes back to 0
                    System.out.print("Heap " + -chosenHeap + " has been reset\n");
                } else if (chosenHeap < 0 && alreadyReset) {
                    System.out.print("Sorry you have used your reset.\n");
                    counting(coinHeaps, true); // Quickly display the counter again
                    chosenHeap = 0; // Number doesn't matter, we want to loop back again
                } else {
                    chosenHeapStorage = coinHeaps.get(chosenHeap); // Get how much heap has
                }
            } while (chosenHeap > HEAPS || chosenHeap < -HEAPS || chosenHeap == 0);

            // Check whether heap is empty
            if (chosenHeapStorage == 0 && chosenHeap > 0) {
                System.out.print("Sorry that's not a legal heap choice.");
            } else if (chosenHeap > 0) {
                // Some more variable initiation & shortened strings
                int takeAmount;
                final int priorTake = priorTurn.get(chosenHeap);
                final String startString = "Now choose a number of coins between ";
                final String otherStart = "Now choose between ";

                // To save on space for code & match reference outputs, output can be manipulated
                String chosenOutput = startString + "1 and 12: ";

                // First time has unique format for user output
                if (priorTake == 0) {
                    do {
                        System.out.print(chosenOutput);

                        // Type checking
                        while (!in.hasNextInt()) {
                            chosenOutput = "Now choose between 1 and 12: ";
                            System.out.print("Sorry you must enter an integer.\n");
                            System.out.print(chosenOutput);
                            in.next();
                            in.nextLine();
                        }

                        takeAmount = in.nextInt();

                        // Dynamically adjust if we are taking 1 less than max or less than 0
                        if (takeAmount > COINS_PER_HEAP - 1 || takeAmount <= 0) {
                            System.out.print(illegalCoinCount);
                            chosenOutput = "Now choose a number of coins between 1 and 12: ";
                        }
                    } while (takeAmount <= 0 || takeAmount >= COINS_PER_HEAP);
                } else {
                    // Max number for how much coins we can take
                    int maxRange; // Outside just to satisfy the loop
                    boolean legalCoins;

                    // Ask user for input until legal amount is entered
                    do {
                        // Calculate possible max take
                        final int priorTakeDouble = priorTake * 2;
                        maxRange = Math.min(priorTakeDouble, chosenHeapStorage);
                        System.out.print(startString + "1 and " + maxRange + ": ");

                        while (!in.hasNextInt()) {
                            System.out.print("Sorry you must enter an integer.\n");
                            System.out.print(otherStart + "1 and " + maxRange + ": ");
                            in.next();
                            in.nextLine();
                        }

                        takeAmount = in.nextInt();
                        boolean takeRange = takeAmount <= 0 || takeAmount > maxRange;
                        legalCoins = takeAmount > COINS_PER_HEAP - 1 || takeRange;

                        if (legalCoins) {
                            System.out.print(illegalCoinCount);
                        }
                    } while ((takeAmount > priorTake * 2) || legalCoins);
                } // Had to indent iteration like this as making variables would be complicated

                final int newCoins = chosenHeapStorage - takeAmount;
                priorTurn.set(chosenHeap, takeAmount); // Set how much was taken this turn
                coinHeaps.set(chosenHeap, newCoins); // Set new coin amount in given heap
                totalHeapAmount = counting(coinHeaps, false); // Scan if all heaps are empty
            }

            // If total coin count is more than 0 and current heap is empty, or we are resetting
            // then to display the new amount of coins in heap (154 line limit on main method)
            if (totalHeapAmount > 0 && chosenHeapStorage > 0 || chosenHeap < 0) {
                counting(coinHeaps, true);

                // Save space, switch players around
                if (currentPlayer == 1) {
                    currentPlayer = 2;
                } else {
                    currentPlayer = 1;
                }
            }
        }

        System.out.println("Player " + currentPlayer + " wins!"); // Who ever is last wins
    }
}

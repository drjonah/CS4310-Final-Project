package virtual_memory;

import java.util.ArrayList;

/**
 * Implementation of the Second Chance (Clock) page replacement algorithm.
 * This algorithm gives each page a "second chance" by using a reference bit.
 * When a page fault occurs and memory is full, pages are inspected in circular order:
 *   - If the reference bit is 0, the page is replaced.
 *   - If the reference bit is 1, the bit is cleared and the page is skipped (given a second chance).
 */
public class SecondChance {

    // 'hand' acts as the clock hand, pointing to the next frame to inspect for replacement
    private static int hand = 0;

    public static void main(String[] args) {
        // Example reference string: sequence of page accesses
        int[] referenceString = {2, 1, 4, 0};
        // Total number of page references to process
        int numberOfPages = referenceString.length;
        // Number of frames (slots) available in memory
        int numberOfFrames = 4;

        // Print simulation header information
        System.out.println("\n\n**** Second Chance Algorithm ***");
        System.out.println("Number of pages: " + numberOfPages);
        System.out.println("Number of frames: " + numberOfFrames);
        System.out.print("Reference string: ");
        for (int page : referenceString) {
            System.out.print(page + " ");
        }
        System.out.println("\n");

        System.out.println("**** Simulation ***");
        // Start the Second Chance simulation
        run(numberOfPages, numberOfFrames, referenceString);
    }

    /**
     * Runs the Second Chance page replacement simulation.
     *
     * @param numberOfPages   total references in the string
     * @param numberOfFrames  capacity of the frame buffer
     * @param referenceString array of page numbers to access
     */
    public static void run(int numberOfPages, int numberOfFrames, int[] referenceString) {
        // pageStates[t][f] stores the state (pageNumber, refBit) of frame f at time t
        Page[][] pageStates = new Page[numberOfFrames][referenceString.length + 1];
        // pageFaults[t] = true if a fault occurred when processing reference index t-1
        boolean[] pageFaults = new boolean[referenceString.length + 1];

        // Initialize frames with dummy pages and record initial state
        ArrayList<Frame> frameList = new ArrayList<>(numberOfFrames);
        populateFrames(frameList, numberOfFrames, pageStates);

        // Print the table headers
        printHeader(referenceString);
        printSeparator(numberOfFrames, referenceString.length);

        // Process each page request in the reference string
        for (int time = 1; time <= referenceString.length; time++) {
            int pageNumber = referenceString[time - 1]; // current page to access
            boolean pageFound = false;

            // Check if the page is already loaded in one of the frames
            for (Frame frame : frameList) {
                Page currentPage = frame.getPage();
                if (currentPage != null && currentPage.getPageNumber() == pageNumber) {
                    // Hit: set reference bit to 1 to mark recent use
                    currentPage.setReferenceBit(true);
                    pageFound = true;
                    break;
                }
            }

            // Miss: page fault occurs, need to replace a page
            if (!pageFound) {
                pageFaults[time - 1] = true;
                handlePageFault(frameList, pageNumber);
            }

            // Record the snapshot of all frames after this time step
            for (int i = 0; i < numberOfFrames; i++) {
                pageStates[i][time] = frameList.get(i).getPage().clone();
            }
        }

        // After processing all references, print the full simulation table
        printTable(pageStates, numberOfFrames, referenceString, pageFaults);
    }

    /**
     * Handles a page fault using the Second Chance (Clock) replacement policy.
     *
     * @param frames  list of frames representing memory slots
     * @param newPage the page number to load into a frame
     */
    private static void handlePageFault(ArrayList<Frame> frames, int newPage) {
        while (true) {
            Page candidate = frames.get(hand).getPage();

            if (!candidate.isReferenceBit()) {
                // If reference bit == 0, evict this page and load the new one
                frames.get(hand).setPage(new Page(newPage, true)); // new page starts with refBit=1
                // Advance the hand to the next frame (clockwise)
                hand = (hand + 1) % frames.size();
                return;
            }
            // If reference bit == 1, clear it (second chance) and advance
            candidate.setReferenceBit(false);
            hand = (hand + 1) % frames.size();
        }
    }

    /**
     * Initializes the list of frames with dummy pages and sets up initial pageStates.
     *
     * @param pageList      container to hold Frame objects
     * @param numberOfFrames total frames to create
     * @param pageStates    snapshot array to populate initial state
     */
    private static void populateFrames(ArrayList<Frame> pageList, int numberOfFrames, Page[][] pageStates) {
        for (int i = 0; i < numberOfFrames; i++) {
            // Create a dummy page (pageNumber=i, refBit defaults to false)
            Page newPage = new Page(i);
            Frame newFrame = new Frame(i);
            newFrame.setPage(newPage);

            pageList.add(newFrame);
            // Clone and store the initial state at time 0
            pageStates[i][0] = newPage.clone();
        }
    }

    /**
     * Prints the table header showing time steps and reference string values.
     */
    private static void printHeader(int[] referenceString) {
        System.out.printf("%-9s | ", "Time");
        for (int i = 0; i <= referenceString.length; i++) {
            System.out.printf("  %-4s | ", i);
        }
        System.out.println();

        System.out.printf("%-9s | ", "RS");
        for (int i = 0; i <= referenceString.length; i++) {
            if (i == 0)
                System.out.printf("  %-4s | ", "");
            else
                System.out.printf("  %-4s | ", referenceString[i - 1]);
        }
        System.out.println();
    }

    /**
     * Prints the simulation table of frame contents and reference bits,
     * followed by the page fault markers.
     */
    private static void printTable(Page[][] pageStates, int numberOfFrames, int[] referenceString, boolean[] pageFaults) {
        // Print each frame's history over time
        for (int frameNumber = 0; frameNumber < pageStates.length; frameNumber++) {
            System.out.printf("%-9s | ", "Frame " + frameNumber);
            for (int i = 0; i < referenceString.length + 1; i++) {
                Page page = pageStates[frameNumber][i];
                System.out.printf("%-6s | ", page.getPageNumber() + " : " + (page.isReferenceBit() ? "1" : "0"));
            }
            System.out.println();
        }

        // Separator line before fault summary
        printSeparator(numberOfFrames, referenceString.length);

        // Print where page faults occurred ('*')
        System.out.printf("%-7s | ", "Pg faults");
        for (int i = 0; i < referenceString.length + 1; i++) {
            if (i == 0)
                System.out.printf("%-6s | ", "");
            else
                System.out.printf("  %-4s | ", pageFaults[i - 1] ? "*" : "");
        }
        System.out.println("\n");
    }

    /**
     * Prints a separator line based on table dimensions for formatting.
     */
    private static void printSeparator(int numberOfFrames, int referenceStringLength) {
        // Calculate total width: base + per-column widths
        System.out.println("-".repeat(7 + 6 * referenceStringLength + 10 + 3 * (referenceStringLength + 1)));
    }
}
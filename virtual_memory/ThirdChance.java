package virtual_memory;

import java.util.ArrayList;

/**
 * Implementation of the Third Chance page replacement algorithm.
 * Extends the Clock algorithm by giving pages up to three "chances"
 * based on their reference and modified bits:
 *   - 1st chance: if R=1, clear R and skip
 *   - 2nd chance: if M=1, clear M and skip
 *   - 3rd chance: if R=0 and M=0, replace
 */
public class ThirdChance {

    // 'hand' acts as the clock hand, pointing to the next frame to inspect
    private static int hand = 0;

    public static void main(String[] args) {
        // Example reference string: sequence of page accesses
        int[] referenceString = {0, 4, 0, 2, 1};
        // Total number of page references to process
        int numberOfPages = referenceString.length;
        // Number of frames (slots) available in memory
        int numberOfFrames = 4;

        // Print simulation header information
        System.out.println("\n\n**** Third Chance Algorithm ***");
        System.out.println("Number of pages: " + numberOfPages);
        System.out.println("Number of frames: " + numberOfFrames);
        System.out.print("Reference string: ");
        for (int page : referenceString) {
            System.out.print(page + " ");
        }
        System.out.println("\n");

        System.out.println("**** Simulation ***");
        // Start the Third Chance simulation
        run(numberOfPages, numberOfFrames, referenceString);
    }

    /**
     * Runs the Third Chance page replacement simulation.
     *
     * @param numberOfPages   total references in the string
     * @param numberOfFrames  capacity of the frame buffer
     * @param referenceString array of page numbers to access
     */
    public static void run(int numberOfPages, int numberOfFrames, int[] referenceString) {
        // pageStates[f][t] stores the state (pageNumber, R bit, M bit) of frame f at time t
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

            // Check if the page is already loaded in one of the frames (hit)
            for (Frame frame : frameList) {
                Page currentPage = frame.getPage();
                if (currentPage != null && currentPage.getPageNumber() == pageNumber) {
                    // Mark recent use by setting the reference bit
                    currentPage.setReferenceBit(true);
                    pageFound = true;
                    break;
                }
            }

            // Miss: page fault occurs, need replacement
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
     * Handles a page fault using the Third Chance replacement policy.
     *
     * @param frames       list of frames representing memory slots
     * @param newPageNumber the page number to load into a frame
     */
    private static void handlePageFault(ArrayList<Frame> frames, int newPageNumber) {
        while (true) {
            Page candidate = frames.get(hand).getPage();

            if (candidate.isReferenceBit()) {
                // 1st chance: if R=1, clear R and skip
                candidate.setReferenceBit(false);
            }
            else if (candidate.isModifiedBit()) {
                // 2nd chance: if M=1, clear M and skip
                candidate.resetModifiedBit();
            }
            else {
                // 3rd chance: R=0 and M=0 → evict this page
                swapPage(frames.get(hand), newPageNumber);
                // Advance the hand to the next frame
                hand = (hand + 1) % frames.size();
                return;
            }

            // Advance clock hand after skipping
            hand = (hand + 1) % frames.size();
        }
    }

    /**
     * Swaps the victim page with the new page, initializing its bits.
     *
     * @param frame          the frame to load the new page into
     * @param newPageNumber  the page number being loaded
     */
    private static void swapPage(Frame frame, int newPageNumber) {
        Page p = new Page(newPageNumber);
        // New page starts with R=1 (recently used), M=0 (clean)
        p.setReferenceBit(true);
        p.setModifiedBit(false);
        frame.setPage(p);
    }

    /**
     * Initializes the list of frames with dummy pages and records initial states.
     *
     * @param pageList        container to hold Frame objects
     * @param numberOfFrames  total frames to create
     * @param pageStates      snapshot array to populate initial state
     */
    private static void populateFrames(ArrayList<Frame> pageList, int numberOfFrames, Page[][] pageStates) {
        for (int i = 0; i < numberOfFrames; i++) {
            // Create a dummy page (pageNumber=i, R and M default to false)
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
     * Prints the simulation table of frame contents (page:RbitMbit),
     * followed by page fault markers.
     */
    private static void printTable(Page[][] pageStates, int numberOfFrames, int[] referenceString, boolean[] pageFaults) {
        // Print each frame's history over time
        for (int frameNumber = 0; frameNumber < pageStates.length; frameNumber++) {
            System.out.printf("%-9s | ", "Frame " + frameNumber);
            for (int t = 0; t < referenceString.length + 1; t++) {
                Page page = pageStates[frameNumber][t];
                // Display as "pageNumber : R M" bits (e.g., "2 : 10")
                String bits = (page.isReferenceBit() ? "1" : "0")
                            + (page.isModifiedBit()  ? "1" : "0");
                System.out.printf("%-6s | ", page.getPageNumber() + " : " + bits);
            }
            System.out.println();
        }

        // Separator before fault summary
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
        System.out.println("-".repeat(7 + 6 * referenceStringLength + 10 + 3 * (referenceStringLength + 1)));
    }
}

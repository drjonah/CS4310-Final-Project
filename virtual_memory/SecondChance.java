package virtual_memory;

import java.util.ArrayList;

public class SecondChance {
    public static void main(String[] args) {
        
        int[] referenceString = {2, 1, 4, 0}; // Example reference string
        int numberOfPages = referenceString.length;       // Number of pages in memory
        int numberOfFrames = 4;      // Number of frames available
        
        System.out.println("\n\n**** Second Chance Algorithm ***");
        System.out.println("Number of pages: " + numberOfPages);
        System.out.println("Number of frames: " + numberOfFrames);
        System.out.print("Reference string: ");
        for (int i = 0; i < referenceString.length; i++) {
            System.out.print(referenceString[i] + " ");
        }

        System.out.println("\n");

        System.out.println("**** Simulation ***");
        run(numberOfPages, numberOfFrames, referenceString);
    }

    /*
     * Run the Second Chance algorithm simulation
     */
    public static void run(int numberOfPages, int numberOfFrames, int[] referenceString) {
        // State storage
        Page[][] pageStates = new Page[numberOfFrames][referenceString.length + 1];
        boolean[] pageFaults = new boolean[referenceString.length + 1];

        // Initialize the page queue
        ArrayList<Frame> frameList = new ArrayList<>(numberOfFrames);
        populateFrames(frameList, numberOfFrames, pageStates);

        // Print Headers
        printHeader(referenceString);
        printSeparator(numberOfFrames, referenceString.length);

        // Process reference string
        for (int time = 1; time <= referenceString.length; time++) {
            // Check the next required reference string
            int pageNumber = referenceString[time - 1];
            boolean pageFound = false;
            
            // Check if page is already in memory
            for (Frame frame : frameList) {
                Page currentPage = frame.getPage();
                if (currentPage.getPageNumber() == pageNumber) {
                    pageFound = true;
                    currentPage.setReferenceBit(true); // Set reference bit to 1
                    break;
                }
            }

            // If the reference string is not contained in the pageQueue, we need to handle a page fault
            if (!pageFound) {
                pageFaults[time - 1] = true;
                handlePageFault(frameList, pageNumber, numberOfFrames);
            }

            // Store the state of the pageQueue after this time step
            for (int i = 0; i < numberOfFrames; i++) {
                pageStates[i][time] = frameList.get(i).getPage().clone();
            }
        }

        // Print the table
        printTable(pageStates, numberOfFrames, referenceString, pageFaults);
    }


    /*
     * Handle a page fault by replacing a page using the Second Chance algorithm
     */
    public static void handlePageFault(ArrayList<Frame> frameList, int newPageNumber, int numberOfFrames) {
        boolean replaced = false;
        
        for (int i = 0; i < numberOfFrames && !replaced; i++) {
            // Get the current frame and its page
            Frame currentFrame = frameList.get(i);
            Page currentPage = currentFrame.getPage();
            
            if (!currentPage.isReferenceBit()) {
                // This page has a second chance bit of 0, so replace it
                Page newPage = new Page(newPageNumber);
                newPage.setReferenceBit(true); // Set reference bit for newly loaded page
                swapPage(currentPage, newPageNumber);
                replaced = true;
            } else {
                // Give this page a second chance
                currentPage.setReferenceBit(false);
            }
        }
        
        // If no page was replaced (all had reference bit = 1),
        // replace the first page (which now has reference bit = 0)
        if (!replaced) {
            swapPage(frameList.get(0).getPage(), newPageNumber);
        }
    }


    /*
     * Swap page for the new page
     */
    private static void swapPage(Page page, int newPageNumber) {
        page.setPageNumber(newPageNumber);
        page.setReferenceBit(true); // Set reference bit for newly loaded page
    }

    
    /*
     * Populate the page queue with empty frames
     */
    private static void populateFrames(ArrayList<Frame> pageList, int numberOfFrames, Page[][] pageStates) {
        for (int i = 0; i < numberOfFrames; i++) {
            Page newPage = new Page(i); // Create a new page
            Frame newFrame = new Frame(i); // Create a new frame
            newFrame.setPage(newPage); // Set the page in the frame

            pageList.add(newFrame); // Initialize empty frames
            pageStates[i][0] = newPage.clone();
        }
    }


    /*
     * Print the header for the simulation
     */
    private static void printHeader(int[] referenceString) {
        // Print time header
        System.out.printf("%-9s | ", "Time");
        for (int i = 0; i <= referenceString.length; i++) {
            System.out.printf("  %-4s | ", i); // Space for frame columns
        }
        System.out.printf("\n");

        // Print reference string header
        System.out.printf("%-9s | ", "RS");
        for (int i = 0; i <= referenceString.length; i++) {
            if (i == 0) System.out.printf("  %-4s | ", "");
            else System.out.printf("  %-4s | ", referenceString[i - 1]); 
        }
        System.out.printf("%-6s%n", "");
    }


    /*
     * Print the table showing the state of the pages in memory
     */
    private static void printTable(Page[][] pageStates, int numberOfFrames, int[] referenceString, boolean[] pageFaults) {
        for (int frameNumber = 0; frameNumber < pageStates.length; frameNumber++) {
            System.out.printf("%-9s | ", "Frame " + frameNumber);
            for (int i = 0; i < referenceString.length + 1; i++) {
                Page page = pageStates[frameNumber][i];
                System.out.printf("%-6s | ", "" + page.getPageNumber() + " : " + (page.isReferenceBit() ? "1" : "0"));
            }
            System.out.printf("%n");
        }

        // Print separator
        printSeparator(numberOfFrames, referenceString.length);

        // Print page fault summary
        System.out.printf("%-7s | ", "Pg faults");
        for (int i = 0; i < referenceString.length + 1; i++) {
            if (i == 0) System.out.printf("%-6s | ", "");
            else System.out.printf("  %-4s | ", pageFaults[i - 1] ? "*" : "");
        }
        System.out.println("\n\n");
    }


    /*
     * Print a separator line for the table
     */
    private static void printSeparator(int numberOfFrames, int referenceStringLength) {
        System.out.println("-".repeat(7 + 6 * referenceStringLength + 10 + 3 * (referenceStringLength + 1)));
    }
}
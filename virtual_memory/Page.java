package virtual_memory;

public class Page implements Cloneable {
    
    // Page number, reference bit, and modified bit
    private int pageNumber;
    private boolean referenceBit;
    private boolean modifiedBit;

    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
        this.referenceBit = false;
        this.modifiedBit = false;
    }

    // Gets and advances the page number
    public int getPageNumber() { return pageNumber; }
    public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }

    // Gets and sets the reference bit
    public boolean isReferenceBit() { return referenceBit; }
    public void setReferenceBit(boolean value) { this.referenceBit = value; }

    // Gets and sets the modified bit
    public boolean isModifiedBit() { return modifiedBit; }
    public void setModifiedBit(boolean value) { this.modifiedBit = value; }

    // Handle state transitions
    public void resetBits() {
        // Transition 11 → 01: If R=1 and M=1, set R=0, keep M=1
        if (referenceBit && modifiedBit) {
            referenceBit = false;
            // modifiedBit remains true
        }
        // Transition 01 → 00: If R=0 and M=1, set M=0 (assuming no further modifications)
        else if (!referenceBit && modifiedBit) {
            modifiedBit = false;
        }
        // Transition 10 → 00: If R=1 and M=0, set R=0
        else if (referenceBit && !modifiedBit) {
            referenceBit = false;
        }
        // If R=0 and M=0, no change needed (already 00)
    }

    // Get the state as a string for debugging
    public String getState(boolean isThirdChance) { 
        String state = "" + pageNumber + " | " + (referenceBit ? "1" : "0"); 
        if (isThirdChance) {
            state += (modifiedBit ? "1" : "0");
        }

        return state;
    }

    @Override
    public Page clone() {
        try {
            return (Page) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Should never happen since we implement Cloneable
        }
    }
}

package virtual_memory;

/**
 * Represents a memory page with a reference bit (R) and modified bit (M).
 * Used by page replacement algorithms (e.g., Second Chance, Third Chance) to
 * track page usage and dirtiness.
 */
public class Page implements Cloneable {
    
    // Unique identifier for this page frame
    private int pageNumber;
    
    // Reference bit: set to true upon page access, cleared as part of eviction
    private boolean referenceBit;
    
    // Modified bit: set to true when page is written to (dirty), cleared when written back
    private boolean modifiedBit;

    /**
     * Constructs a new Page with the given page number.
     * Reference and modified bits default to false.
     *
     * @param pageNumber unique page identifier
     */
    public Page(int pageNumber) {
        this.pageNumber = pageNumber;
        this.referenceBit = false;
        this.modifiedBit = false;
    }

    /**
     * Constructs a new Page with the given page number and reference bit.
     * Modified bit defaults to false.
     *
     * @param pageNumber   unique page identifier
     * @param referenceBit initial reference bit value
     */
    public Page(int pageNumber, boolean referenceBit) {
        this.pageNumber = pageNumber;
        this.referenceBit = referenceBit;
        this.modifiedBit = false;
    }

    /**
     * Constructs a new Page with the given page number, reference bit, and modified bit.
     *
     * @param pageNumber   unique page identifier
     * @param referenceBit initial reference bit value
     * @param modifiedBit  initial modified bit value
     */
    public Page(int pageNumber, boolean referenceBit, boolean modifiedBit) {
        this.pageNumber = pageNumber;
        this.referenceBit = referenceBit;
        this.modifiedBit = modifiedBit;
    }

    /**
     * Returns the page number identifier.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Updates the page number identifier.
     *
     * @param pageNumber new page number
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Checks if the reference bit is set (page was recently accessed).
     */
    public boolean isReferenceBit() {
        return referenceBit;
    }

    /**
     * Sets or clears the reference bit.
     *
     * @param value new reference bit value
     */
    public void setReferenceBit(boolean value) {
        this.referenceBit = value;
    }

    /**
     * Checks if the modified bit is set (page has been written to).
     */
    public boolean isModifiedBit() {
        return modifiedBit;
    }

    /**
     * Sets or clears the modified bit.
     *
     * @param value new modified bit value
     */
    public void setModifiedBit(boolean value) {
        this.modifiedBit = value;
    }

    /**
     * Clear the modified bit (M = 0). 
     * Used for giving the “second” chance in Third-Chance.
     */
    public void resetModifiedBit() {
        this.modifiedBit = false;
    }

    /**
     * Resets bits according to a simple state transition:
     *  - (R=1, M=1) → (R=0, M=1): clear R, keep M (dirty but no recent ref)
     *  - (R=0, M=1) → (R=0, M=0): clear M (write-back assumed)
     *  - (R=1, M=0) → (R=0, M=0): clear R
     *  - (R=0, M=0): no change
     */
    public void resetBits() {
        if (referenceBit && modifiedBit) {
            // Page was recently used and is dirty: clear reference, keep dirty
            referenceBit = false;
        } else if (!referenceBit && modifiedBit) {
            // Page is dirty but not referenced: assume write-back, clear modified
            modifiedBit = false;
        } else if (referenceBit && !modifiedBit) {
            // Page was referenced but clean: clear reference bit
            referenceBit = false;
        }
        // If already clean and unreferenced, no action needed
    }

    /**
     * Returns a string of the form "pageNumber | R[M]" for debugging.
     * If isThirdChance is true, includes the modified bit in the output.
     *
     * @param isThirdChance flag to include M in output
     * @return formatted state string
     */
    public String getState(boolean isThirdChance) {
        String state = pageNumber + " | " + (referenceBit ? "1" : "0");
        if (isThirdChance) {
            // Append modified bit when showing third-chance state
            state += (modifiedBit ? "1" : "0");
        }
        return state;
    }

    /**
     * Creates and returns a deep copy of this Page.
     *
     * @return cloned Page instance
     */
    @Override
    public Page clone() {
        try {
            return (Page) super.clone();
        } catch (CloneNotSupportedException e) {
            // Should never happen since we're Cloneable
            throw new AssertionError();
        }
    }
}
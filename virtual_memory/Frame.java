package virtual_memory;

/**
 * Represents a single frame (slot) in physical memory.
 * Each frame can hold one Page object.
 */
public class Frame {
    
    // Unique identifier for this frame in the frame table
    private int frameNumber;
    
    // The page currently loaded in this frame; null if the frame is empty
    private Page page;

    /**
     * Constructs a new, empty Frame with the given frame number.
     *
     * @param frameNumber index or ID of this frame
     */
    public Frame(int frameNumber) {
        this.frameNumber = frameNumber;
        this.page = null; // Frame starts out empty
    }

    /**
     * Returns the frame's identifier (position in memory).
     *
     * @return the frame number
     */
    public int getFrameNumber() {
        return frameNumber;
    }

    /**
     * Returns the Page currently stored in this frame.
     *
     * @return the loaded Page, or null if empty
     */
    public Page getPage() {
        return page;
    }

    /**
     * Loads the specified Page into this frame, replacing any existing one.
     *
     * @param page the Page to load
     */
    public void setPage(Page page) {
        this.page = page;
    }
}

package virtual_memory;

public class Frame {
    
    private int frameNumber;
    private Page page;

    public Frame(int frameNumber) {
        this.frameNumber = frameNumber;
        this.page = null; // Initially empty
    }

    public int getFrameNumber() { return frameNumber; }
    
    public Page getPage() { return page; }
    public void setPage(Page page) { this.page = page; }
}

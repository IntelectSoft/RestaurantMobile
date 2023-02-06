package md.edi.mobilewaiter.data.base;

public class Item {
    private int widthCell;
    private boolean isSelected;
    private boolean isForSelecting;
    private int headerId;
    private boolean hasError;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getWidthCell() {
        return widthCell;
    }

    public void setWidthCell(int widthCell) {
        this.widthCell = widthCell;
    }

    public boolean isForSelecting() {
        return isForSelecting;
    }

    public void setForSelecting(boolean forSelecting) {
        isForSelecting = forSelecting;
    }

    public int getHeaderId() {
        return headerId;
    }

    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }
}

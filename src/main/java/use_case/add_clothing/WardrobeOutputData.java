package use_case.add_clothing;

import use_case.outfit.OutfitOutputData.ClothingData;
import java.util.List;

/** Plain immutable response data: no Swing components or database objects. */
public final class WardrobeOutputData {
    public final List<ClothingData> clothing;
    public final String message;
    // True only right after a new item was saved (closes the Add dialog).
    public final boolean itemAdded;
    // True only right after an item was edited or deleted (closes the Edit dialog)
    public final boolean itemUpdated;

    public WardrobeOutputData(List<ClothingData> clothing, String message,
                              boolean itemAdded, boolean itemUpdated) {
        this.clothing = List.copyOf(clothing);
        this.message = message;
        this.itemAdded = itemAdded;
        this.itemUpdated = itemUpdated;
    }
}

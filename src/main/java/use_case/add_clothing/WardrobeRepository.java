package use_case.add_clothing;

import entity.ClothingItem;
import java.util.List;

/** Database teammate implements this port. Failed writes must not partially change data. */
public interface WardrobeRepository {
    List<ClothingItem> getClothing();
    void saveClothing(ClothingItem item);
    /** Replaces the stored item that has the same id. */
    void updateClothing(ClothingItem item);
    void deleteClothing(String clothingId);
}

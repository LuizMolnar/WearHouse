package use_case.add_clothing;

public interface AddClothingInputBoundary {
    /** Publish the current wardrobe to the presenter. */
    void load();

    /** Validate, store the image, and save a new clothing item (UC1). */
    void addClothing(String name, String type, String sourceImagePath);

    /**
     * Change the name, type and the image of an existing item.
     * A null or blank newImagePath keeps the current image.
     */
    void editClothing(String clothingId, String name, String type, String newImagePath);

    /** Remove an existing item from the wardrobe. */
    void deleteClothing(String clothingId);
}

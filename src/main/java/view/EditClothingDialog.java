package view;

import interface_adapter.wardrobe.WardrobeController;
import interface_adapter.wardrobe.WardrobeViewModel;
import use_case.outfit.OutfitOutputData.ClothingData;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;

/** Popup for editing a clothing item's details (including its image) or deleting it. */
public final class EditClothingDialog extends JDialog {
    private static final String[] TYPES = {"HAT", "TOP", "BOTTOM", "SHOES"};

    private final JTextField name;
    private final JComboBox<String> type = new JComboBox<>(TYPES);
    private final JLabel preview = new JLabel("No image", SwingConstants.CENTER);
    private final JLabel imageName = new JLabel(" ");
    private final JButton chooseImage = new JButton("Change image...");
    private final JButton save = new JButton("Save changes");
    private final JButton delete = new JButton("Delete");
    private final JButton cancel = new JButton("Cancel");
    private final WardrobeViewModel model;
    private final PropertyChangeListener listener;
    /** Empty means "keep the current image". */
    private String newImagePath = "";

    public EditClothingDialog(Window owner, WardrobeController controller,
                              WardrobeViewModel model, ClothingData item) {
        super(owner, "Edit clothing", ModalityType.APPLICATION_MODAL);
        this.model = model;
        this.name = new JTextField(item.name, 20);
        this.type.setSelectedItem(item.category);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(content);

        preview.setPreferredSize(new Dimension(220, 220));
        preview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        showPreview(item.imagePath);
        JPanel left = new JPanel(new BorderLayout(4, 4));
        left.add(preview, BorderLayout.CENTER);
        JPanel imageControls = new JPanel(new BorderLayout(4, 4));
        imageControls.add(chooseImage, BorderLayout.NORTH);
        imageControls.add(imageName, BorderLayout.SOUTH);
        left.add(imageControls, BorderLayout.SOUTH);
        content.add(left, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Name"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; form.add(name, c);
        c.gridx = 0; c.gridy = 1; c.fill = GridBagConstraints.NONE; c.weightx = 0;
        form.add(new JLabel("Type"), c);
        c.gridx = 1; c.fill = GridBagConstraints.HORIZONTAL; form.add(type, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
        form.add(Box.createVerticalGlue(), c);
        content.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new BorderLayout());
        buttons.add(delete, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(cancel); right.add(save);
        buttons.add(right, BorderLayout.EAST);
        content.add(buttons, BorderLayout.SOUTH);

        chooseImage.addActionListener(e -> chooseNewImage());
        save.addActionListener(e -> controller.editClothing(
                item.id, name.getText(), (String) type.getSelectedItem(), newImagePath));
        delete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this,
                    "Delete \"" + item.name + "\" from your wardrobe?", "Confirm deletion",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                controller.deleteClothing(item.id);
            }
        });
        cancel.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(save);

        listener = event -> {
            if (event.getPropertyName().equals("busy")) {
                boolean enabled = !((Boolean) event.getNewValue());
                save.setEnabled(enabled); delete.setEnabled(enabled);
                cancel.setEnabled(enabled); chooseImage.setEnabled(enabled);
                setDefaultCloseOperation(enabled ? DISPOSE_ON_CLOSE : DO_NOTHING_ON_CLOSE);
            } else if (event.getPropertyName().equals("state")) {
                if (model.getState().itemUpdated) dispose();
            }
        };
        model.addListener(listener);
        pack();
        setLocationRelativeTo(owner);
    }

    private void chooseNewImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter(
                "Images (png, jpg, jpeg, gif, bmp)", "png", "jpg", "jpeg", "gif", "bmp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        newImagePath = file.getAbsolutePath();
        imageName.setText("New image: " + file.getName());
        showPreview(newImagePath);
    }

    private void showPreview(String path) {
        preview.setIcon(null);
        preview.setText("No image");
        if (path == null || path.isBlank() || !new File(path).isFile()) return;
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0) {
            double scale = Math.min(210.0 / icon.getIconWidth(), 210.0 / icon.getIconHeight());
            preview.setIcon(new ImageIcon(icon.getImage().getScaledInstance(
                    Math.max(1, (int) (icon.getIconWidth() * scale)),
                    Math.max(1, (int) (icon.getIconHeight() * scale)), Image.SCALE_SMOOTH)));
            preview.setText("");
        } else {
            preview.setText("Preview unavailable");
        }
    }

    @Override public void dispose() {
        if (listener != null) model.removeListener(listener);
        super.dispose();
    }
}

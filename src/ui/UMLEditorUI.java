package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import canvas.Canvas;
import element.BasicObject;
import mode.AssociationMode;
import mode.CompositionMode;
import mode.GeneralizationMode;
import mode.Mode;
import mode.OvalMode;
import mode.RectMode;
import mode.SelectMode;

public class UMLEditorUI extends JFrame {
    private CanvasPanel canvasPanel;
    private Canvas canvas = new Canvas();
    private boolean draggingFromShapeButton = false;
    private JButton activeButton;
    // 六種mode物件只建立一次，切換時直接指向同一個物件，避免重複建立
    private SelectMode selectMode = new SelectMode(canvas);
    private RectMode rectMode = new RectMode(canvas);
    private OvalMode ovalMode = new OvalMode(canvas);
    private AssociationMode associationMode = new AssociationMode(canvas);
    private GeneralizationMode generalizationMode = new GeneralizationMode(canvas);
    private CompositionMode compositionMode = new CompositionMode(canvas);

    private Map<Mode, JButton> modeButtonMap = new HashMap<>(); // mode對應按鈕的map，方便切換模式時更新按鈕顏色
    private List<JButton> modeButtons = new ArrayList<>();
    // 把按鈕變成成員變數，方便在updateButtonColors方法中修改顏色

    public UMLEditorUI() {
        setTitle("UML Editor");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createMenuBar();
        add(createToolBar(), BorderLayout.WEST);

        canvasPanel = new CanvasPanel();
        add(canvasPanel, BorderLayout.CENTER);
        setupGlobalMouseListener();
    }

    private void setupGlobalMouseListener() { // 建立button跟canvas的連結
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseEvent))
                return;

            MouseEvent e = (MouseEvent) event;
            if (e.getID() != MouseEvent.MOUSE_RELEASED)
                return;
            if (!draggingFromShapeButton)
                return;

            draggingFromShapeButton = false;

            Point p = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), canvasPanel);

            if (p.x >= 0 && p.x <= canvasPanel.getWidth()
                    && p.y >= 0 && p.y <= canvasPanel.getHeight()) {
                canvas.mouseReleased(p.x, p.y);
            } else {
                canvas.switchToPreviousMode(); // 如果釋放點不在canvas上，切回之前的mode
            }

            syncActiveButtonWithCurrentMode();
            canvasPanel.repaint();

        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    private void syncActiveButtonWithCurrentMode() {
        JButton btn = modeButtonMap.get(canvas.getMode());
        if (btn != null) {
            activeButton = btn;
            updateButtonColors(activeButton);
        }
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

        JMenuItem labelItem = new JMenuItem("Label");
        labelItem.addActionListener(e -> showCustomizeLabelDialog());

        JMenuItem groupMenuItem = new JMenuItem("Group");
        groupMenuItem.addActionListener(e -> handleGroupMenuAction());

        JMenuItem UngroupMenuItem = new JMenuItem("Ungroup");
        UngroupMenuItem.addActionListener(e -> handleUngroupMenuAction());

        editMenu.add(groupMenuItem);
        editMenu.add(UngroupMenuItem);
        editMenu.add(labelItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    private void setMode(Mode newMode, JButton newButton) {
        // 更新mode 若現在是建立物件則保存previousMode
        canvas.setMode(newMode);
        activeButton = newButton;
        updateButtonColors(activeButton);
    }

    private ImageIcon loadIcon(String name) {
        String path = "icons/" + name.toLowerCase() + ".png";
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private JPanel createToolItem(String text, JButton button) {
        // 建立最左邊文字欄位
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setPreferredSize(new Dimension(150, 60));

        panel.add(label, BorderLayout.WEST);
        panel.add(button, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createToolBar() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayout(6, 1, 5, 5));
        toolPanel.setPreferredSize(new Dimension(260, 0));
        toolPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        List<ModeConfig> configs = Arrays.asList(
                new ModeConfig("Select", selectMode),
                new ModeConfig("Association", associationMode),
                new ModeConfig("Generalization", generalizationMode),
                new ModeConfig("Composition", compositionMode),
                new ModeConfig("Rect", rectMode),
                new ModeConfig("Oval", ovalMode));

        for (ModeConfig config : configs) {
            // 初始化就建好
            JButton button = new JButton(loadIcon(config.name));
            button.setPreferredSize(new Dimension(70, 70));
            button.setToolTipText(config.name);

            toolPanel.add(createToolItem(config.name, button));

            if (config.mode == rectMode || config.mode == ovalMode) {
                button.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!SwingUtilities.isLeftMouseButton(e))
                            return;
                        draggingFromShapeButton = true;

                        // 切換按鈕顏色，告知目前欲建立的物件
                        canvas.setPreviousMode();
                        setMode(config.mode, button);
                    }
                });
            } else {
                button.addActionListener(e -> { // 按鈕觸發
                    setMode(config.mode, button);
                    System.out.println(config.name + " mode activated");
                });
            }

            modeButtons.add(button);
            modeButtonMap.put(config.mode, button); // 建立mode對應按鈕的map
        }

        // 設置預設 Select 按鈕為黑色
        ModeConfig defaultConfig = configs.get(0);
        JButton defaultButton = modeButtonMap.get(defaultConfig.mode);
        setMode(defaultConfig.mode, defaultButton);
        return toolPanel;
    }

    private void updateButtonColors(JButton activeButton) {
        // 把所有按鈕設成白色背景、黑色文字
        for (JButton button : modeButtons)
            button.setBackground(Color.WHITE);

        activeButton.setBackground(Color.BLACK);
        activeButton.setOpaque(true);
        activeButton.setBorderPainted(true);
    }

    private void showCustomizeLabelDialog() {
        JDialog dialog = new JDialog(this, "Customize Label Style", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        String defaultName = canvas.getSelectedLabelText();
        String defaultColor = colorToString(canvas.getSelectedObjectColor());

        JTextField nameField = new JTextField(defaultName);
        JTextField colorField = new JTextField(defaultColor);

        dialog.add(createLabelCenterPanel(nameField, colorField), BorderLayout.CENTER);
        dialog.add(createLabelButtonPanel(dialog, nameField, colorField), BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void handleGroupMenuAction() {
        if (canvas.handleGroupAction())
            repaint();
        System.out.println("Group action triggered");
    }

    private void handleUngroupMenuAction() {
        if (canvas.handleUngroupAction())
            repaint();
        System.out.println("Ungroup action triggered");
    }

    private JPanel createLabelCenterPanel(JTextField nameField, JTextField colorField) {
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        centerPanel.add(new JLabel("Object Name:"));
        centerPanel.add(nameField);
        centerPanel.add(new JLabel("Object Color:"));
        centerPanel.add(colorField);

        return centerPanel;
    }

    private JPanel createLabelButtonPanel(JDialog dialog, JTextField nameField, JTextField colorField) {
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> handleLabelConfirm(dialog, nameField, colorField));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void handleLabelConfirm(JDialog dialog, JTextField nameField, JTextField colorField) {
        String objectName = nameField.getText();
        String colorInput = colorField.getText();

        Color parsedColor = parseColorInput(colorInput);
        if (parsedColor == null) {
            JOptionPane.showMessageDialog(
                    dialog,
                    "顏色格式不正確，請輸入顏色名稱或 #RRGGBB。",
                    "Invalid Color",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!canvas.applyLabelToSelectedObjects(objectName, parsedColor)) {
            JOptionPane.showMessageDialog(
                    dialog,
                    "請先選取一個物件再設定標籤。",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        repaint();
        dialog.dispose();
    }

    private String colorToString(Color color) {
        if (color == null)
            return "gray"; // default

        for (Map.Entry<String, Color> entry : COLOR_MAP.entrySet())
            if (entry.getValue().equals(color))
                return entry.getKey();

        // fallback HEX
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static final Map<String, Color> COLOR_MAP = Map.ofEntries(
            Map.entry("black", Color.BLACK),
            Map.entry("blue", Color.BLUE),
            Map.entry("cyan", Color.CYAN),
            Map.entry("gray", Color.GRAY),
            Map.entry("green", Color.GREEN),
            Map.entry("orange", Color.ORANGE),
            Map.entry("pink", Color.PINK),
            Map.entry("red", Color.RED),
            Map.entry("white", Color.WHITE),
            Map.entry("yellow", Color.YELLOW));

    private Color parseColorInput(String colorInput) {
        if (colorInput == null)
            return null;

        String normalized = colorInput.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty())
            return null;

        // 先查 map
        Color color = COLOR_MAP.get(normalized);
        if (color != null)
            return color;

        // fallback: HEX
        try {
            return Color.decode(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    class CanvasPanel extends JPanel { // 畫畫
        public CanvasPanel() {
            setBackground(Color.WHITE);

            // 接受滑鼠事件，並轉發給Canvas處理
            addMouseListener(new MouseAdapter() { // MouseAdapter 點擊（press / release / click）
                @Override
                public void mousePressed(MouseEvent e) {
                    canvas.mousePressed(e.getX(), e.getY());
                    repaint(); // swing內建方法，呼叫後會觸發paintComponent
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (draggingFromShapeButton)
                        return;
                    canvas.mouseReleased(e.getX(), e.getY());
                    // 因為shapeMode執行完會跳回上一個mode，所以這裡要檢查一下mode來確保button跟mode同步
                    syncActiveButtonWithCurrentMode();
                    repaint();

                }
            });

            // MouseMotionAdapter 拖曳（drag）和移動（move）
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    canvas.mouseDragged(e.getX(), e.getY());
                    repaint(); // swing內建方法，呼叫後會觸發paintComponent
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    canvas.mouseMoved(e.getX(), e.getY());
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // clear canvas

            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
            // 繪製objects
            canvas.getMode().draw(g); // 所有預覽畫面會在mode裡面畫，shape的圖形會在canvas裡面畫
            for (BasicObject obj : canvas.getObjects()) {
                obj.draw(g);
            }
        }
    }

    class ModeConfig {
        String name;
        Mode mode;

        ModeConfig(String name, Mode mode) {
            this.name = name;
            this.mode = mode;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UMLEditorUI ui = new UMLEditorUI();
            ui.setVisible(true);
        });
    }
}
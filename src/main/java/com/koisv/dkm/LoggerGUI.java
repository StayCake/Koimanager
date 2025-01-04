package com.koisv.dkm;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoggerGUI extends JFrame {
    public JPanel mainPanel;
    public JPanel discordPanel;
    public JScrollPane discordLogR;
    public JTextArea discordLog;
    public JScrollBar scrollBar4;
    public JPanel ircPanel;
    public JScrollPane ircLogR;
    public JTextArea ircLog;
    public JLabel ircTitle;
    public JScrollBar scrollBar1;
    public JPanel ktorPanel;
    public JTextArea ktor_MLog;
    public JPanel todo1;
    public JScrollPane todo2;
    public JTextArea todo4;
    public JLabel todo3;
    public JScrollBar scrollBar3;
    public JTabbedPane discordTabP;
    public JCheckBox discordNoLogin;
    public JButton discordOn;
    public JButton button2;
    public JSlider discordLogLevel;
    public JTree discordTree;
    public JButton discordQuit;
    public JLabel discordStatus;
    public JTabbedPane tabbedPane1;
    public JPanel ktor_at;
    public JPanel ktor_wsc;
    public JScrollBar scrollBar2;
    public JScrollBar ktor_wscSB;
    public JScrollPane ktor_MSP;
    public JScrollPane ktor_wscSP;
    public JTextArea ktor_wscLog;

    public LoggerGUI() throws UnsupportedLookAndFeelException {
        setContentPane(mainPanel);
        setTitle("KoiManager Logs");
        setSize(854, 480);
        setLocation(427, 240);
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        //setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
        discordLogLevel.addComponentListener(new ComponentAdapter() {
        });
        discordLogLevel.addPropertyChangeListener(evt -> {
            if (Objects.equals(evt.getPropertyName(), "value")) {
                final Logger logger = (Logger) LogManager.getLogger("KM-DBot");
                switch ((Integer) evt.getNewValue()) {
                    case 1:
                        logger.setLevel(Level.TRACE);
                        break;
                    case 2:
                        logger.setLevel(Level.DEBUG);
                        break;
                    case 3:
                        logger.setLevel(Level.INFO);
                        break;
                    case 4:
                        logger.setLevel(Level.ERROR);
                        break;
                    case 5:
                        logger.setLevel(Level.FATAL);
                        break;
                    case 6:
                        logger.setLevel(Level.OFF);
                        break;
                }
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), 0, 0, true, true));
        mainPanel.setAutoscrolls(false);
        mainPanel.setBackground(new Color(-13019068));
        mainPanel.setDoubleBuffered(false);
        mainPanel.setFocusable(true);
        mainPanel.setMaximumSize(new Dimension(-1, -1));
        mainPanel.setMinimumSize(new Dimension(-1, -1));
        mainPanel.setName("KoiManager Logs");
        mainPanel.setOpaque(true);
        mainPanel.setPreferredSize(new Dimension(854, 480));
        mainPanel.setRequestFocusEnabled(true);
        mainPanel.setVerifyInputWhenFocusTarget(true);
        mainPanel.putClientProperty("html.disable", Boolean.TRUE);
        discordPanel = new JPanel();
        discordPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 0, 0));
        discordPanel.putClientProperty("html.disable", Boolean.TRUE);
        mainPanel.add(discordPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, true));
        discordPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        discordTabP = new JTabbedPane();
        discordPanel.add(discordTabP, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        discordTabP.addTab(this.$$$getMessageFromBundle$$$("lang", "log.discord"), panel1);
        discordLogR = new JScrollPane();
        discordLogR.setAutoscrolls(true);
        discordLogR.setDoubleBuffered(true);
        discordLogR.putClientProperty("html.disable", Boolean.TRUE);
        panel1.add(discordLogR, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        discordLog = new JTextArea();
        discordLog.setAlignmentY(0.5f);
        discordLog.setAutoscrolls(true);
        discordLog.setColumns(0);
        discordLog.setDoubleBuffered(true);
        discordLog.setDragEnabled(true);
        discordLog.setEditable(false);
        discordLog.setFocusCycleRoot(false);
        discordLog.setFocusTraversalPolicyProvider(false);
        discordLog.setInheritsPopupMenu(false);
        discordLog.setLineWrap(true);
        discordLog.setMargin(new Insets(0, 0, 0, 0));
        discordLog.setMinimumSize(new Dimension(195, 240));
        discordLog.setText("");
        discordLog.setWrapStyleWord(false);
        discordLog.putClientProperty("html.disable", Boolean.TRUE);
        discordLogR.setViewportView(discordLog);
        scrollBar4 = new JScrollBar();
        scrollBar4.putClientProperty("html.disable", Boolean.TRUE);
        panel1.add(scrollBar4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        discordTabP.addTab(this.$$$getMessageFromBundle$$$("lang", "control"), panel2);
        button2 = new JButton();
        button2.setText("Button");
        panel2.add(button2, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discordNoLogin = new JCheckBox();
        discordNoLogin.setText("CheckBox");
        panel2.add(discordNoLogin, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discordOn = new JButton();
        discordOn.setText("Button");
        panel2.add(discordOn, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discordLogLevel = new JSlider();
        discordLogLevel.setEnabled(true);
        discordLogLevel.setExtent(0);
        discordLogLevel.setInverted(false);
        discordLogLevel.setMajorTickSpacing(0);
        discordLogLevel.setMaximum(6);
        discordLogLevel.setMinimum(1);
        discordLogLevel.setMinorTickSpacing(0);
        discordLogLevel.setPaintLabels(true);
        discordLogLevel.setPaintTicks(false);
        discordLogLevel.setPaintTrack(true);
        discordLogLevel.setSnapToTicks(false);
        discordLogLevel.setValue(3);
        discordLogLevel.setValueIsAdjusting(true);
        panel2.add(discordLogLevel, new GridConstraints(3, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discordTree = new JTree();
        panel2.add(discordTree, new GridConstraints(0, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
        discordQuit = new JButton();
        discordQuit.setText("Button");
        panel2.add(discordQuit, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        discordStatus = new JLabel();
        discordStatus.setText("Wait....");
        panel2.add(discordStatus, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ircPanel = new JPanel();
        ircPanel.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), 0, 0));
        ircPanel.putClientProperty("html.disable", Boolean.TRUE);
        mainPanel.add(ircPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, true));
        ircPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        ircLogR = new JScrollPane();
        ircLogR.setAutoscrolls(true);
        ircLogR.setDoubleBuffered(true);
        ircLogR.putClientProperty("html.disable", Boolean.TRUE);
        ircPanel.add(ircLogR, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ircLog = new JTextArea();
        ircLog.setDoubleBuffered(true);
        ircLog.setDragEnabled(true);
        ircLog.setEditable(false);
        ircLog.setLineWrap(true);
        ircLog.setText("");
        ircLog.putClientProperty("html.disable", Boolean.TRUE);
        ircLogR.setViewportView(ircLog);
        ircTitle = new JLabel();
        ircTitle.setAlignmentX(0.5f);
        ircTitle.setDoubleBuffered(true);
        ircTitle.setHorizontalAlignment(0);
        ircTitle.setHorizontalTextPosition(0);
        ircTitle.setOpaque(true);
        this.$$$loadLabelText$$$(ircTitle, this.$$$getMessageFromBundle$$$("lang", "logs.irc"));
        ircTitle.putClientProperty("html.disable", Boolean.TRUE);
        ircPanel.add(ircTitle, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, 1, null, null, null, 0, false));
        scrollBar1 = new JScrollBar();
        scrollBar1.putClientProperty("html.disable", Boolean.TRUE);
        ircPanel.add(scrollBar1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ktorPanel = new JPanel();
        ktorPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), 0, 0));
        ktorPanel.putClientProperty("html.disable", Boolean.TRUE);
        mainPanel.add(ktorPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, true));
        ktorPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tabbedPane1 = new JTabbedPane();
        ktorPanel.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        ktor_at = new JPanel();
        ktor_at.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("lang", "log.ktor.main"), ktor_at);
        ktor_MSP = new JScrollPane();
        ktor_at.add(ktor_MSP, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ktor_MLog = new JTextArea();
        ktor_MLog.setLineWrap(true);
        ktor_MSP.setViewportView(ktor_MLog);
        scrollBar2 = new JScrollBar();
        ktor_at.add(scrollBar2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ktor_wsc = new JPanel();
        ktor_wsc.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab(this.$$$getMessageFromBundle$$$("lang", "log.ktorwschat"), ktor_wsc);
        ktor_wscSP = new JScrollPane();
        ktor_wscSP.setName("");
        ktor_wsc.add(ktor_wscSP, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        ktor_wscLog = new JTextArea();
        ktor_wscLog.setDragEnabled(true);
        ktor_wscLog.setEditable(false);
        ktor_wscLog.setLineWrap(true);
        ktor_wscLog.setWrapStyleWord(true);
        ktor_wscSP.setViewportView(ktor_wscLog);
        ktor_wscSB = new JScrollBar();
        ktor_wsc.add(ktor_wscSB, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        todo1 = new JPanel();
        todo1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), 0, 0));
        todo1.putClientProperty("html.disable", Boolean.TRUE);
        mainPanel.add(todo1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, 1, 1, null, null, null, 0, true));
        todo1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        todo2 = new JScrollPane();
        todo2.setAutoscrolls(true);
        todo2.setDoubleBuffered(true);
        todo2.putClientProperty("html.disable", Boolean.TRUE);
        todo1.add(todo2, new GridConstraints(1, 0, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        todo4 = new JTextArea();
        todo4.setDoubleBuffered(true);
        todo4.setDragEnabled(true);
        todo4.setEditable(false);
        todo4.setLineWrap(true);
        todo4.setText("");
        todo4.putClientProperty("html.disable", Boolean.TRUE);
        todo2.setViewportView(todo4);
        todo3 = new JLabel();
        todo3.setAlignmentX(0.5f);
        todo3.setAutoscrolls(false);
        todo3.setDoubleBuffered(true);
        todo3.setHorizontalAlignment(0);
        todo3.setHorizontalTextPosition(0);
        todo3.setText("Label");
        todo3.putClientProperty("html.disable", Boolean.TRUE);
        todo1.add(todo3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, 1, 1, null, null, null, 0, false));
        scrollBar3 = new JScrollBar();
        scrollBar3.putClientProperty("html.disable", Boolean.TRUE);
        todo1.add(scrollBar3, new GridConstraints(1, 1, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        discordLogR.setVerticalScrollBar(scrollBar4);
        discordStatus.setLabelFor(ircLogR);
        ircLogR.setVerticalScrollBar(scrollBar1);
        ircTitle.setLabelFor(ircLog);
        ktor_wscSP.setHorizontalScrollBar(ktor_wscSB);
        todo2.setVerticalScrollBar(scrollBar3);
        todo3.setLabelFor(todo4);
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}

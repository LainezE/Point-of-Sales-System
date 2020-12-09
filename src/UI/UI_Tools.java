/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.Timer;

/**
 * Extra tools for common/quick UI functionality
 * @author Dustin Gritman
 */
public class UI_Tools {
    /**
     * Fix components by resizing and centering on screen.
     * @param window The component to change (JFrame, JPanel, etc.).
     */
    public static void fixWindow(Window window) {
        window.pack();
        
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - window.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - window.getHeight()) / 2);
        window.setLocation(x, y);
    }
    
    /**
     * Remove any previously displayed panel and display the new panel.
     * @param frame The frame used as the display.
     * @param page The the new panel to display.
     */
    public static void displayNewPage(JFrame frame, JPanel page) {
        updatePanelComponents(page, false);
        Timer visibleTimer = new Timer(1, (ActionEvent e) -> { 
            updatePanelComponents(page, true);
        });
        visibleTimer.start();
        visibleTimer.setRepeats(false);
        
        frame.getContentPane().removeAll();
        frame.add(page);
        frame.revalidate();
        frame.repaint();
        fixWindow(frame);
    }
    
    /**
     * Enables or Disables all editable components.
     * @param component The component (or container component) to enable/disable
     * @param status The desired status of the components (true to enable and false to disable)
     */
    public static void updatePanelComponents(Component component, boolean status) {
        if (AbstractButton.class.isAssignableFrom(component.getClass())
                || JTextField.class.isAssignableFrom(component.getClass())
                || JTable.class.isAssignableFrom(component.getClass()))
            component.setEnabled(status);
        else
            for (Component comp : ((Container) component).getComponents())
                updatePanelComponents(comp, status);
    }
    
    public static void setFocusPainted(Component component, boolean status) {
        if (AbstractButton.class.isAssignableFrom(component.getClass()))
            ((AbstractButton) component).setFocusPainted(status);
        else if (JComboBox.class.isAssignableFrom(component.getClass()))
            ((JComboBox) component).setFocusable(status);
        else
            for (Component comp : ((Container) component).getComponents())
                setFocusPainted(comp, status);
    }
    
    /**
     * Checks if a string is an integer.
     * @param s The string to check.
     * @return True if the string is an integer and false otherwise.
     */
    public static boolean isInteger(String s) {
        if (s.isEmpty())
            return false;
        
        for (int i = 0; i < s.length(); i++) {
            // Check for starting "-" sign in case number is negative
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1)
                    return false;
                else
                    continue;
            }
            if (!Character.isDigit(s.charAt(i)))
                return false;
        }
        
        return true;
    }
}

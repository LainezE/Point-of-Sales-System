/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

/**
 *
 * @author arianacarrasq
 */
public class AdminMenu extends javax.swing.JPanel {

    /**
     * Creates new form Admin1
     */
    public AdminMenu() {
        initComponents();
        
        UI_Tools.setFocusPainted(this, false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        employeeBtn = new javax.swing.JButton();
        inventoryBtn = new javax.swing.JButton();
        adminLabel = new javax.swing.JLabel();
        posBtn = new javax.swing.JButton();
        exitBtn = new javax.swing.JButton();
        userBtn = new javax.swing.JButton();
        logoutBtn = new javax.swing.JButton();

        setBackground(new java.awt.Color(102, 102, 102));
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Administrator ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Comic Sans MS", 1, 36), new java.awt.Color(255, 255, 255))); // NOI18N
        setForeground(new java.awt.Color(204, 204, 204));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        employeeBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 24)); // NOI18N
        employeeBtn.setForeground(new java.awt.Color(250, 163, 111));
        employeeBtn.setText("Employees");

        inventoryBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 24)); // NOI18N
        inventoryBtn.setForeground(new java.awt.Color(250, 163, 111));
        inventoryBtn.setText("Inventory");
        inventoryBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        adminLabel.setBackground(new java.awt.Color(255, 255, 255));
        adminLabel.setFont(new java.awt.Font("Comic Sans MS", 1, 30)); // NOI18N
        adminLabel.setForeground(new java.awt.Color(255, 255, 255));
        adminLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adminLabel.setText("Admin Menu ");

        posBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 24)); // NOI18N
        posBtn.setForeground(new java.awt.Color(250, 163, 111));
        posBtn.setText("Sale System");

        exitBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        exitBtn.setForeground(new java.awt.Color(250, 163, 111));
        exitBtn.setText("Exit");

        userBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 24)); // NOI18N
        userBtn.setForeground(new java.awt.Color(250, 163, 111));
        userBtn.setText("Users");

        logoutBtn.setFont(new java.awt.Font("Comic Sans MS", 0, 14)); // NOI18N
        logoutBtn.setForeground(new java.awt.Color(250, 163, 111));
        logoutBtn.setText("Logout");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(248, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(adminLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(employeeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(inventoryBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(posBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(125, 125, 125)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logoutBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(exitBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(adminLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logoutBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userBtn)
                    .addComponent(exitBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(employeeBtn)
                .addGap(18, 18, 18)
                .addComponent(inventoryBtn)
                .addGap(18, 18, 18)
                .addComponent(posBtn)
                .addContainerGap(88, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel adminLabel;
    public javax.swing.JButton employeeBtn;
    public javax.swing.JButton exitBtn;
    public javax.swing.JButton inventoryBtn;
    public javax.swing.JButton logoutBtn;
    public javax.swing.JButton posBtn;
    public javax.swing.JButton userBtn;
    // End of variables declaration//GEN-END:variables
}

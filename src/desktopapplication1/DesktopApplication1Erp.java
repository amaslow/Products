package desktopapplication1;

import org.jdesktop.application.Action;

public class DesktopApplication1Erp extends javax.swing.JDialog {

    public DesktopApplication1Erp(java.awt.Frame parent) {
        super(parent);
        initComponents();
        getRootPane().setDefaultButton(doButton);
    }

    @Action
    public void closeRaport() {
        int a = 0;
        if (ledCheckBox.isSelected()) {
            a = a + 10;
        }
        if (cflCheckBox.isSelected()) {
            a = a + 11;
        }
        if (halCheckBox.isSelected()) {
            a = a + 12;
        }
        if (lumCheckBox.isSelected()) {
            a = a + 100;
        }
        ErpExport export = new ErpExport();
        export.CreateExcel(a);

        dispose();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ledCheckBox = new javax.swing.JCheckBox();
        cflCheckBox = new javax.swing.JCheckBox();
        halCheckBox = new javax.swing.JCheckBox();
        lumCheckBox = new javax.swing.JCheckBox();
        doButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getResourceMap(DesktopApplication1Erp.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setIconImage(null);
        setMinimumSize(new java.awt.Dimension(400, 70));
        setModal(true);
        setName("ERP"); // NOI18N
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        ledCheckBox.setText(resourceMap.getString("ledCheckBox.text")); // NOI18N
        ledCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        ledCheckBox.setName("ledCheckBox"); // NOI18N
        ledCheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(ledCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 50, -1));

        cflCheckBox.setText(resourceMap.getString("cflCheckBox.text")); // NOI18N
        cflCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cflCheckBox.setName("cflCheckBox"); // NOI18N
        cflCheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(cflCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 50, -1));

        halCheckBox.setText(resourceMap.getString("halCheckBox.text")); // NOI18N
        halCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        halCheckBox.setName("halCheckBox"); // NOI18N
        halCheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(halCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 50, -1));

        lumCheckBox.setText(resourceMap.getString("lumCheckBox.text")); // NOI18N
        lumCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lumCheckBox.setName("lumCheckBox"); // NOI18N
        lumCheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(lumCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 70, -1));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getActionMap(DesktopApplication1Erp.class, this);
        doButton.setAction(actionMap.get("closeRaport")); // NOI18N
        doButton.setText(resourceMap.getString("doButton.text")); // NOI18N
        doButton.setName("doButton"); // NOI18N
        getContentPane().add(doButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 72, 28));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cflCheckBox;
    private javax.swing.JButton doButton;
    private javax.swing.JCheckBox halCheckBox;
    private javax.swing.JCheckBox ledCheckBox;
    private javax.swing.JCheckBox lumCheckBox;
    // End of variables declaration//GEN-END:variables

//public static void main(String args[]) {
//    
//    java.awt.EventQueue.invokeLater(new Runnable() {
//        public void run() {
//    
//     new DesktopApplication1Erp(null).setVisible(true);
//
//            }
//        });
//
//}
}

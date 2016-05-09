package desktopapplication1;

import com.mysql.jdbc.Statement;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import org.jdesktop.application.Action;

public class DesktopApplication1Raport extends javax.swing.JDialog {

    Connection con = null;
    Statement st = null;
    ResultSet rs = null;

    public DesktopApplication1Raport(java.awt.Frame parent) {
        super(parent);
        initComponents();
        getRootPane().setDefaultButton(doButton);
    }
    String stat1, stat2, stat3, stat4, stat5, stat6, stat7 = null;

    @Action
    public void closeRaport() {
        String standard = jraportTextField.getText();

        if (B1CheckBox.isSelected()) {
            stat1 = "B1";
        }
        if (G0CheckBox.isSelected()) {
            stat2 = "G0";
        }
        if (G1CheckBox.isSelected()) {
            stat3 = "G1";
        }
        if (G2CheckBox.isSelected()) {
            stat4 = "G2";
        }
        if (G3CheckBox.isSelected()) {
            stat5 = "G3";
        }
        if (U0CheckBox.isSelected()) {
            stat6 = "U0";
        }
        if (P1CheckBox.isSelected()) {
            stat7 = "P1";
        }

        try {
            con = Utils.getConnection();

            FileInputStream fis = new FileInputStream("G:\\QC\\Database\\Items\\Reports\\Standard_check.jrxml");

            //BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);

            Map map = new HashMap();
            map.put("Standard", standard);
            map.put("Stat1", stat1);
            map.put("Stat2", stat2);
            map.put("Stat3", stat3);
            map.put("Stat4", stat4);
            map.put("Stat5", stat5);
            map.put("Stat6", stat6);
            map.put("Stat7", stat7);

            JasperReport jasperReport = (JasperReport) JasperCompileManager.compileReport(fis);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, con);

            JasperViewer.viewReport(jasperPrint, false);


        } catch (Exception X) {
        } finally {
            Utils.closeDB(rs, st, con);
        }
        stat1 = null;
        stat2 = null;
        stat3 = null;
        stat4 = null;
        stat5 = null;
        stat6 = null;
        stat7 = null;
        dispose();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jraportTextField = new javax.swing.JTextField();
        G0CheckBox = new javax.swing.JCheckBox();
        G1CheckBox = new javax.swing.JCheckBox();
        G2CheckBox = new javax.swing.JCheckBox();
        P1CheckBox = new javax.swing.JCheckBox();
        B1CheckBox = new javax.swing.JCheckBox();
        G3CheckBox = new javax.swing.JCheckBox();
        U0CheckBox = new javax.swing.JCheckBox();
        doButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getResourceMap(DesktopApplication1Raport.class);
        setTitle(resourceMap.getString("title")); // NOI18N
        setIconImage(null);
        setMinimumSize(new java.awt.Dimension(420, 55));
        setModal(true);
        setName("standards"); // NOI18N
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jraportTextField.setFont(resourceMap.getFont("jraportTextField.font")); // NOI18N
        jraportTextField.setToolTipText(resourceMap.getString("jraportTextField.toolTipText")); // NOI18N
        jraportTextField.setName("jraportTextField"); // NOI18N
        jraportTextField.setPreferredSize(new java.awt.Dimension(6, 25));
        getContentPane().add(jraportTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 147, -1));

        G0CheckBox.setText(resourceMap.getString("G0CheckBox.text")); // NOI18N
        G0CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        G0CheckBox.setName("G0CheckBox"); // NOI18N
        G0CheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(G0CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 5, -1, -1));

        G1CheckBox.setText(resourceMap.getString("G1CheckBox.text")); // NOI18N
        G1CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        G1CheckBox.setName("G1CheckBox"); // NOI18N
        G1CheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(G1CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 5, -1, -1));

        G2CheckBox.setText(resourceMap.getString("G2CheckBox.text")); // NOI18N
        G2CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        G2CheckBox.setName("G2CheckBox"); // NOI18N
        G2CheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(G2CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 5, -1, -1));

        P1CheckBox.setText(resourceMap.getString("P1CheckBox.text")); // NOI18N
        P1CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        P1CheckBox.setName("P1CheckBox"); // NOI18N
        P1CheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(P1CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 15, -1, -1));

        B1CheckBox.setText(resourceMap.getString("B1CheckBox.text")); // NOI18N
        B1CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        B1CheckBox.setName("B1CheckBox"); // NOI18N
        B1CheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(B1CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 25, -1, -1));

        G3CheckBox.setText(resourceMap.getString("G3CheckBox.text")); // NOI18N
        G3CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        G3CheckBox.setName("G3CheckBox"); // NOI18N
        G3CheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(G3CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 25, -1, -1));

        U0CheckBox.setText(resourceMap.getString("U0CheckBox.text")); // NOI18N
        U0CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        U0CheckBox.setName("U0CheckBox"); // NOI18N
        U0CheckBox.setPreferredSize(new java.awt.Dimension(39, 16));
        getContentPane().add(U0CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 25, -1, -1));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getActionMap(DesktopApplication1Raport.class, this);
        doButton.setAction(actionMap.get("closeRaport")); // NOI18N
        doButton.setText(resourceMap.getString("doButton.text")); // NOI18N
        doButton.setName("doButton"); // NOI18N
        getContentPane().add(doButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, 72, 28));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox B1CheckBox;
    private javax.swing.JCheckBox G0CheckBox;
    private javax.swing.JCheckBox G1CheckBox;
    private javax.swing.JCheckBox G2CheckBox;
    private javax.swing.JCheckBox G3CheckBox;
    private javax.swing.JCheckBox P1CheckBox;
    private javax.swing.JCheckBox U0CheckBox;
    private javax.swing.JButton doButton;
    private javax.swing.JTextField jraportTextField;
    // End of variables declaration//GEN-END:variables
}
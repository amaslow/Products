
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import org.jdesktop.application.Task;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.RollbackException;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AbstractBindingListener;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.PropertyStateEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporterParameter;
import net.sf.jasperreports.view.JasperViewer;

public class DesktopApplication1View extends FrameView {

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = new Date();
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    File productContent = new File("G:\\Product Content\\PRODUCTS");
    Color color = null;

    public DesktopApplication1View(SingleFrameApplication app) {
        super(app);

        initComponents();
//        initComponents1();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        // tracking table selection
        masterTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        firePropertyChange("recordSelected", !isRecordSelected(), isRecordSelected());
                    }
                });

        // tracking changes to save
        bindingGroup.addBindingListener(new AbstractBindingListener() {

            @Override
            public void targetChanged(Binding binding, PropertyStateEvent event) {
                // save action observes saveNeeded property
                setSaveNeeded(true);
            }
        });

        // have a transaction started
        entityManager.getTransaction().begin();
    }

    public boolean isSaveNeeded() {
        return saveNeeded;
    }

    private void setSaveNeeded(boolean saveNeeded) {
        if (saveNeeded != this.saveNeeded) {
            this.saveNeeded = saveNeeded;
            firePropertyChange("saveNeeded", !saveNeeded, saveNeeded);
        }
    }

    public boolean isRecordSelected() {
        return masterTable.getSelectedRow() != -1;
    }

    @Action
    public void newRecord() {
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec") || System.getProperty("user.name").equals("RRemmig")) {
            desktopapplication1.Items i = new desktopapplication1.Items();
            entityManager.persist(i);
            list.add(i);
            //int row = list.size()-1;
            int row = masterTable.getRowCount() - 1;
            masterTable.setRowSelectionInterval(row, row);
            masterTable.scrollRectToVisible(masterTable.getCellRect(row, 0, true));
            setSaveNeeded(true);
        } else {
            JOptionPane.showMessageDialog(null, "You have no rights to add a new item", "No privileges", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action(enabledProperty = "recordSelected")
    public void deleteRecord() {
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec") || System.getProperty("user.name").equals("RRemmig")) {
            int reply = JOptionPane.showConfirmDialog(CertPanel, "Are you sure?", "DELETE", JOptionPane.OK_CANCEL_OPTION);
            if (reply == JOptionPane.OK_OPTION) {
                int[] selected = masterTable.getSelectedRows();
                List<desktopapplication1.Items> toRemove = new ArrayList<desktopapplication1.Items>(selected.length);

                for (int idx = 0; idx < selected.length; idx++) {
                    desktopapplication1.Items i = list.get(masterTable.convertRowIndexToModel(selected[idx]));
                    toRemove.add(i);
                    entityManager.remove(i);
                    //     System.out.println(list);
                }
                list.removeAll(toRemove);
                setSaveNeeded(true);
            }
        } else {
            JOptionPane.showMessageDialog(null, "You have no rights to delete any item", "No privileges", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Action(enabledProperty = "saveNeeded")
    public Task save() {

        return new SaveTask(getApplication());

    }

    class SaveTask extends Task {

        SaveTask(org.jdesktop.application.Application app) {
            super(app);
        }

        @Override
        protected Void doInBackground() {
            String modificator = System.getProperty("user.name");
            if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec")
                    || System.getProperty("user.name").equals("RRemmig")
                    || System.getProperty("user.name").equals("RvanKasteren") || System.getProperty("user.name").equals("ANetten")
                    || System.getProperty("user.name").equals("Anetten") || System.getProperty("user.name").equals("jyan")
                    || System.getProperty("user.name").equals("JJanssens") || System.getProperty("user.name").equals("RvanDommelen")) {
                if (!System.getProperty("user.name").equals("AMaslowiec")) {
                    ModDateField.setText(String.valueOf(dateFormat.format(date)));
                    ModWhoField.setText(modificator);
//                    try {
//                        String item = itemField.getText();
//                        String sap = sapField.getText();
//                        con = Utils.getConnection();
//                        st = con.createStatement();
//                        String SQL = "UPDATE elro.items SET mod_who='" + modificator + "' where sap ='" + sap + "' and item ='" + item + "';";
//                        st.executeUpdate(SQL);
//
//                    } catch (SQLException ex) {
//                        Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
//                    } finally {
//                        Utils.closeDB(rs, st, con);
//                    }
                }
                try {
                    entityManager.getTransaction().commit();
                    entityManager.getTransaction().begin();
                } catch (RollbackException rex) {
                    entityManager.getTransaction().begin();
                    List<desktopapplication1.Items> merged = new ArrayList<desktopapplication1.Items>(list.size());
                    for (desktopapplication1.Items i : list) {
                        merged.add(entityManager.merge(i));
                    }
                    list.clear();
                    list.addAll(merged);
                }
            } else {
                JOptionPane.showMessageDialog(null, "You have no rights to change any item", "No privileges", JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }

        @Override
        protected void finished() {
            setSaveNeeded(false);
        }
    }

    /**
     * An example action method showing how to create asynchronous tasks
     * (running on background) and how to show their progress. Note the
     * artificial 'Thread.sleep' calls making the task long enough to see the
     * progress visualization - remove the sleeps for real application.
     */
    @Action
    public Task refresh() {
        return new RefreshTask(getApplication());
    }

    private class RefreshTask extends Task {

        RefreshTask(org.jdesktop.application.Application app) {
            super(app);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected Void doInBackground() {
            // try {
            setProgress(0, 0, 4);
            setMessage("Rolling back the current changes...");
            setProgress(1, 0, 4);
            entityManager.getTransaction().rollback();
            //Thread.sleep(1000L); // remove for real app
            setProgress(2, 0, 4);

            setMessage("Starting a new transaction...");
            entityManager.getTransaction().begin();
            //Thread.sleep(500L); // remove for real app
            setProgress(3, 0, 4);

            setMessage("Fetching new data...");
            java.util.Collection data = query.getResultList();
            for (Object entity : data) {
                entityManager.refresh(entity);
            }
            //Thread.sleep(1300L); // remove for real app
            setProgress(4, 0, 4);

            //Thread.sleep(150L); // remove for real app
            list.clear();
            list.addAll(data);
            jItemCountLabel.setText(String.valueOf(masterTable.getRowCount()) + " items in total");
            //} catch(InterruptedException ignore) { }
            return null;
        }

        @Override
        protected void finished() {
            setMessage("Done.");
            setSaveNeeded(false);
        }
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DesktopApplication1.getApplication().getMainFrame();
            aboutBox = new DesktopApplication1AboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DesktopApplication1.getApplication().show(aboutBox);
    }

    @Action
    public void printFrame() {

        //PageFormat documentPageFormat = new PageFormat();
        //documentPageFormat.setOrientation(PageFormat.LANDSCAPE);

        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName(itemField.getText() + " sheet");



        pj.setPrintable(new Printable() {

            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;


                g2.translate(pf.getImageableX(), pf.getImageableY());
                //g2.rotate(1.565, 450, 350);


                g2.scale(0.74, 1.1);

//                pf.setOrientation(PageFormat.LANDSCAPE);

                mainPanel.paint(g2);
                //mainPanel.printAll(g2);


                return Printable.PAGE_EXISTS;
            }
        });
        if (pj.printDialog() == false) {
            return;
        }

        try {
            pj.print();
        } catch (PrinterException ex) {
            // handle exception
        }
    }
    
    @Action
    public void copyErP() {
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec")|| System.getProperty("user.name").equals("RvanKasteren")) {
            Object[] options = {"Yes", "No"};
            int reply = JOptionPane.showOptionDialog(CertPanel, "Copy all ERP info from this item for all the same items?", "COPY ERP", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    String item = itemField.getText();
                    String sap = sapField.getText();
                    String item_s = originalItemField.getText();

                    con = Utils.getConnection();
                    st = con.createStatement();

                    String SQL2 = " update elro.items"
                            + " set"
                            + " KIND_BULB=(select KIND_BULB from(select KIND_BULB from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " INCL=(select INCL from(select INCL from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " ITEM_BULB=(select ITEM_BULB from(select ITEM_BULB from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " INT_LED=(select INT_LED from(select INT_LED from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " SPECIAL_USE=(select SPECIAL_USE from(select SPECIAL_USE from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " WATTAGE=(select WATTAGE from(select WATTAGE from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " WATTAGE_RATED=(select WATTAGE_RATED from(select WATTAGE_RATED from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LUMEN=(select LUMEN from(select LUMEN from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LUMEN_RATED=(select LUMEN_RATED from(select LUMEN_RATED from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LIFETIME=(select LIFETIME from(select LIFETIME from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LIFETIME_RATED=(select LIFETIME_RATED from(select LIFETIME_RATED from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " SWICYC=(select SWICYC from(select SWICYC from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " KELVIN=(select KELVIN from(select KELVIN from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " RA=(select RA from(select RA from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " STAR60=(select STAR60 from(select STAR60 from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " START_TIME=(select START_TIME from(select START_TIME from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " COLOR_CONS=(select COLOR_CONS from(select COLOR_CONS from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " CANDELA=(select CANDELA from(select CANDELA from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " BEAM=(select BEAM from(select BEAM from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " ACCENT=(select ACCENT from(select ACCENT from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " DIMENSION_FI=(select DIMENSION_FI from(select DIMENSION_FI from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " DIMENSION_L=(select DIMENSION_L from(select DIMENSION_L from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " DIMENSION_D=(select DIMENSION_D from(select DIMENSION_D from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " POWER_FACTOR=(select POWER_FACTOR from(select POWER_FACTOR from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LUMEN_FACTOR=(select LUMEN_FACTOR from(select LUMEN_FACTOR from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " DIMMER=(select DIMMER from(select DIMMER from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " ENCLAS=(select ENCLAS from(select ENCLAS from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " KWIK=(select KWIK from(select KWIK from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " VOLTAGE=(select VOLTAGE from(select VOLTAGE from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " AMPERE=(select AMPERE from(select AMPERE from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " COMPAR=(select COMPAR from(select COMPAR from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " FITTIN=(select FITTIN from(select FITTIN from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LICHTB=(select LICHTB from(select LICHTB from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " SHAPE=(select SHAPE from(select SHAPE from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LED_Type=(select LED_Type from(select LED_Type from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " LED_NUMBER=(select LED_NUMBER from(select LED_NUMBER from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " UV=(select UV from(select UV from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " TEST_DATE=(select TEST_DATE from(select TEST_DATE from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " T6000H_DATE=(select T6000H_DATE from(select T6000H_DATE from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " COLOUR=(select COLOUR from(select COLOUR from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " BEAM_R=(select BEAM_R from(select BEAM_R from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " INDOOR=(select INDOOR from(select INDOOR from elro.items where sap='" + sap + "' and item='" + item + "')x),"
                            + " SPECTRUM=(select SPECTRUM from(select SPECTRUM from elro.items where sap='" + sap + "' and item='" + item + "')x)"
                            + " where item_s='" + item_s + "';";

                    st.executeUpdate(SQL2);
                } catch (SQLException ex) {
                    Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    Utils.closeDB(rs, st, con);
                  }
            }
        } else {
            JOptionPane.showMessageDialog(null, "You have no rights for this action", "No privileges", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Action
    public void ErP() {
        if (raportErp == null) {
            JFrame mainFrame = DesktopApplication1.getApplication().getMainFrame();
            raportErp = new DesktopApplication1Erp(mainFrame);
            raportErp.setLocationRelativeTo(mainFrame);
        }
        DesktopApplication1.getApplication().show(raportErp);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        mainPanel = new javax.swing.JPanel();
        masterScrollPane = new javax.swing.JScrollPane();
        masterTable = new javax.swing.JTable();
        itemLabel = new javax.swing.JLabel();
        itemField = new javax.swing.JTextField();
        vendorLabel = new javax.swing.JLabel();
        vendorField = new javax.swing.JTextField();
        supplierLabel = new javax.swing.JLabel();
        supplierField = new javax.swing.JTextField();
        jItemCountLabel = new javax.swing.JLabel();
        SupplierItemlabel = new javax.swing.JLabel();
        ModDateField = new javax.swing.JLabel();
        ModWhoField = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        jStatusComboBox = new javax.swing.JComboBox();
        statusLabel1 = new javax.swing.JLabel();
        brandComboBox = new javax.swing.JComboBox();
        descrNLField = new javax.swing.JTextField();
        descrESField = new javax.swing.JTextField();
        descrPLField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jFilter = new javax.swing.JTextField();
        descrENField1 = new javax.swing.JTextField();
        descrDEField1 = new javax.swing.JTextField();
        descrFRField1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        CertPanel = new javax.swing.JPanel();
        directiveLabel = new javax.swing.JLabel();
        ceLabel = new javax.swing.JLabel();
        trLabel = new javax.swing.JLabel();
        validFromDateLabel = new javax.swing.JLabel();
        nbLabel = new javax.swing.JLabel();
        lvdCheckBox = new javax.swing.JCheckBox();
        lvdDirComboBox = new javax.swing.JComboBox();
        lvdCeField = new javax.swing.JTextField();
        lvdTrField = new javax.swing.JTextField();
        lvdDateChooser = new com.toedter.calendar.JDateChooser();
        lvdCleanDateButton = new javax.swing.JButton();
        lvdNbComboBox = new javax.swing.JComboBox();
        gsNbLabel = new javax.swing.JLabel();
        oemDateFromLabel = new javax.swing.JLabel();
        gsCeField = new javax.swing.JTextField();
        gsTrField = new javax.swing.JTextField();
        emcTrField = new javax.swing.JTextField();
        cprCeField = new javax.swing.JTextField();
        cprTrField = new javax.swing.JTextField();
        erpTrField = new javax.swing.JTextField();
        rohsTrField = new javax.swing.JTextField();
        oemCeField = new javax.swing.JTextField();
        gsCheckBox = new javax.swing.JCheckBox();
        emcCheckBox = new javax.swing.JCheckBox();
        cprCheckBox = new javax.swing.JCheckBox();
        rfCheckBox = new javax.swing.JCheckBox();
        rohsCheckBox = new javax.swing.JCheckBox();
        reachCheckBox = new javax.swing.JCheckBox();
        erpCheckBox = new javax.swing.JCheckBox();
        batt1TrCheckBox = new javax.swing.JCheckBox();
        fluxCheckBox = new javax.swing.JCheckBox();
        pahCheckBox = new javax.swing.JCheckBox();
        phthCheckBox = new javax.swing.JCheckBox();
        oemCheckBox = new javax.swing.JCheckBox();
        jGSNBComboBox = new javax.swing.JComboBox();
        jEMCNBComboBox = new javax.swing.JComboBox();
        jRFNBComboBox = new javax.swing.JComboBox();
        cprNBComboBox = new javax.swing.JComboBox();
        jRoHSNBComboBox = new javax.swing.JComboBox();
        VdsCeField = new javax.swing.JTextField();
        VdsTrField = new javax.swing.JTextField();
        VdsDateLabel = new javax.swing.JLabel();
        vdsCheckBox = new javax.swing.JCheckBox();
        BosecCeField = new javax.swing.JTextField();
        bosecCheckBox = new javax.swing.JCheckBox();
        BosecDateLabel = new javax.swing.JLabel();
        KomoCeField = new javax.swing.JTextField();
        komoCheckBox = new javax.swing.JCheckBox();
        KomoDateLabel = new javax.swing.JLabel();
        nfCheckBox = new javax.swing.JCheckBox();
        NfCeField = new javax.swing.JTextField();
        NfTrField = new javax.swing.JTextField();
        otherCheckBox = new javax.swing.JCheckBox();
        otherCeField = new javax.swing.JTextField();
        otherDateLabel = new javax.swing.JLabel();
        rfNbNLabel = new javax.swing.JLabel();
        RFNBNField = new javax.swing.JTextField();
        remarksScrollPane = new javax.swing.JScrollPane();
        remarksTextArea = new javax.swing.JTextArea();
        rfTrField = new javax.swing.JTextField();
        OemDateFromChooser = new com.toedter.calendar.JDateChooser();
        cleanOemDateFromButton = new javax.swing.JButton();
        OemDateToChooser = new com.toedter.calendar.JDateChooser();
        cleanOemDateToButton = new javax.swing.JButton();
        NfDateChooser = new com.toedter.calendar.JDateChooser();
        cleanNfDateToButton = new javax.swing.JButton();
        GsDateChooser = new com.toedter.calendar.JDateChooser();
        cleanGsDateToButton = new javax.swing.JButton();
        EmcDateChooser = new com.toedter.calendar.JDateChooser();
        cleanEmcDateButton = new javax.swing.JButton();
        RtteDateChooser = new com.toedter.calendar.JDateChooser();
        cleanRfDateButton = new javax.swing.JButton();
        EupDateChooser = new com.toedter.calendar.JDateChooser();
        cleanErpDateButton = new javax.swing.JButton();
        RohsDateChooser = new com.toedter.calendar.JDateChooser();
        cleanRohsDateButton = new javax.swing.JButton();
        cprDateChooser = new com.toedter.calendar.JDateChooser();
        cleanCprDateButton = new javax.swing.JButton();
        VdsDateChooser = new com.toedter.calendar.JDateChooser();
        cleanVdsDateButton = new javax.swing.JButton();
        BosecDateChooser = new com.toedter.calendar.JDateChooser();
        cleanBosecDateButton = new javax.swing.JButton();
        KomoDateChooser = new com.toedter.calendar.JDateChooser();
        cleanKomoDateButton = new javax.swing.JButton();
        otherDateChooser = new com.toedter.calendar.JDateChooser();
        cleanOtherDateButton = new javax.swing.JButton();
        rohsDirComboBox = new javax.swing.JComboBox();
        erpDirComboBox = new javax.swing.JComboBox();
        emcDirComboBox = new javax.swing.JComboBox();
        pahCeComboBox = new javax.swing.JComboBox();
        rfDirComboBox = new javax.swing.JComboBox();
        erpStatusComboBox = new javax.swing.JComboBox();
        jLabel10 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        fluxTrComboBox = new javax.swing.JComboBox();
        batt1TrComboBox = new javax.swing.JComboBox();
        batt2TrCheckBox = new javax.swing.JCheckBox();
        batt2TrComboBox = new javax.swing.JComboBox();
        docCheckBox = new javax.swing.JCheckBox();
        reachCeComboBox = new javax.swing.JComboBox();
        doiCheckBox = new javax.swing.JCheckBox();
        cdfCheckBox = new javax.swing.JCheckBox();
        cprDirComboBox = new javax.swing.JComboBox();
        emcCeField = new javax.swing.JTextField();
        rfCeField = new javax.swing.JTextField();
        rohsCeField = new javax.swing.JTextField();
        photobiolCheckBox = new javax.swing.JCheckBox();
        photobiolComboBox = new javax.swing.JComboBox();
        ipCheckBox = new javax.swing.JCheckBox();
        ipComboBox = new javax.swing.JComboBox();
        standardPanel = new javax.swing.JPanel();
        emcLabel = new javax.swing.JLabel();
        lvdLabel = new javax.swing.JLabel();
        rfLabel = new javax.swing.JLabel();
        cpdLabel = new javax.swing.JLabel();
        emc1Field = new javax.swing.JTextField();
        lvd1Field = new javax.swing.JTextField();
        rf1Field = new javax.swing.JTextField();
        cpd1Field = new javax.swing.JTextField();
        cpd2Field = new javax.swing.JTextField();
        cpd3Field = new javax.swing.JTextField();
        cpd4Field = new javax.swing.JTextField();
        emc2Field = new javax.swing.JTextField();
        lvd2Field = new javax.swing.JTextField();
        rf2Field = new javax.swing.JTextField();
        emc3Field = new javax.swing.JTextField();
        lvd3Field = new javax.swing.JTextField();
        rf3Field = new javax.swing.JTextField();
        emc5Field = new javax.swing.JTextField();
        lvd5Field = new javax.swing.JTextField();
        emc7Field = new javax.swing.JTextField();
        lvd7Field = new javax.swing.JTextField();
        emc9Field = new javax.swing.JTextField();
        lvd9Field = new javax.swing.JTextField();
        emc10Field = new javax.swing.JTextField();
        emc8Field = new javax.swing.JTextField();
        lvd8Field = new javax.swing.JTextField();
        lvd6Field = new javax.swing.JTextField();
        emc6Field = new javax.swing.JTextField();
        emc4Field = new javax.swing.JTextField();
        lvd4Field = new javax.swing.JTextField();
        rf4Field = new javax.swing.JTextField();
        specsPanel = new javax.swing.JPanel();
        PowerInfoLabel = new javax.swing.JLabel();
        mainsCheckBox = new javax.swing.JCheckBox();
        mainsInVoltLabel = new javax.swing.JLabel();
        mainsInVoltComboBox = new javax.swing.JComboBox();
        mainsInWattLabel = new javax.swing.JLabel();
        mainsInWattTextField = new javax.swing.JTextField();
        mainsInPlugLabel = new javax.swing.JLabel();
        mainsInPlugTextField = new javax.swing.JTextField();
        mainsIPLabel = new javax.swing.JLabel();
        mainsIPTextField = new javax.swing.JTextField();
        mainsClassLabel = new javax.swing.JLabel();
        mainsClassComboBox = new javax.swing.JComboBox();
        mainsOutWattLabel = new javax.swing.JLabel();
        mainsOutWattTextField = new javax.swing.JTextField();
        mainsOutPlugLabel = new javax.swing.JLabel();
        mainsOutPlugTextField = new javax.swing.JTextField();
        mainsSeparator = new javax.swing.JSeparator();
        adaptorLabel = new javax.swing.JLabel();
        adaptorCheckBox1 = new javax.swing.JCheckBox();
        adaptorCheckBox2 = new javax.swing.JCheckBox();
        adaptorTypeLabel = new javax.swing.JLabel();
        adaptorType1ComboBox = new javax.swing.JComboBox();
        adaptorType2ComboBox = new javax.swing.JComboBox();
        adaptorInVoltLabel = new javax.swing.JLabel();
        adaptorInVolt1ComboBox = new javax.swing.JComboBox();
        adaptorInVolt2ComboBox = new javax.swing.JComboBox();
        adaptorInAmpLabel = new javax.swing.JLabel();
        adaptorInAmp1TextField = new javax.swing.JTextField();
        adaptorInAmp2TextField = new javax.swing.JTextField();
        adaptorInPlugLabel = new javax.swing.JLabel();
        adaptorInPlug1TextField = new javax.swing.JTextField();
        adaptorInPlug2TextField = new javax.swing.JTextField();
        adaptorOutVoltLabel = new javax.swing.JLabel();
        adaptorOutVolt1TextField = new javax.swing.JTextField();
        adaptorOutVolt2TextField = new javax.swing.JTextField();
        adaptorOutAmpLabel = new javax.swing.JLabel();
        adaptorOutAmp1TextField = new javax.swing.JTextField();
        adaptorOutAmp2TextField = new javax.swing.JTextField();
        adaptorOutPlugLabel = new javax.swing.JLabel();
        adaptorOutPlug1TextField1 = new javax.swing.JTextField();
        outputAmp2TextField1 = new javax.swing.JTextField();
        adaptorClassLabel = new javax.swing.JLabel();
        adaptorClass1ComboBox = new javax.swing.JComboBox();
        adaptorClass2ComboBox = new javax.swing.JComboBox();
        adaptorClass1Logo = new javax.swing.JLabel();
        adaptorClass2Logo = new javax.swing.JLabel();
        adaptorSeparator = new javax.swing.JSeparator();
        batt1Label = new javax.swing.JLabel();
        batt1InclLabel = new javax.swing.JLabel();
        batt1CheckBox = new javax.swing.JCheckBox();
        batt2InclLabel = new javax.swing.JLabel();
        batt2CheckBox = new javax.swing.JCheckBox();
        battQuaLabel = new javax.swing.JLabel();
        battQua1TextField = new javax.swing.JTextField();
        battQua2TextField = new javax.swing.JTextField();
        battBrandLabel = new javax.swing.JLabel();
        battBrand1TextField = new javax.swing.JTextField();
        battBrand2TextField = new javax.swing.JTextField();
        battTypeLabel = new javax.swing.JLabel();
        battType1ComboBox = new javax.swing.JComboBox();
        battType2ComboBox = new javax.swing.JComboBox();
        battSizeLabel = new javax.swing.JLabel();
        battSize1ComboBox = new javax.swing.JComboBox();
        battSize2ComboBox = new javax.swing.JComboBox();
        battVoltLabel = new javax.swing.JLabel();
        battVolt1TextField = new javax.swing.JTextField();
        battVolt2TextField = new javax.swing.JTextField();
        battAccu1CheckBox = new javax.swing.JCheckBox();
        battAccu2CheckBox = new javax.swing.JCheckBox();
        battCapLabel = new javax.swing.JLabel();
        battCap1TextField = new javax.swing.JTextField();
        battCap2TextField = new javax.swing.JTextField();
        battRepl1CheckBox = new javax.swing.JCheckBox();
        battRepl2CheckBox = new javax.swing.JCheckBox();
        lightInfoLabel = new javax.swing.JLabel();
        lightWatt1Label = new javax.swing.JLabel();
        lightWatt1Field = new javax.swing.JTextField();
        lightBulb1Label = new javax.swing.JLabel();
        lightBulb1ComboBox = new javax.swing.JComboBox();
        lightWatt2Label = new javax.swing.JLabel();
        lightWatt2TextField = new javax.swing.JTextField();
        lightBulb2Label = new javax.swing.JLabel();
        lightBulb2ComboBox = new javax.swing.JComboBox();
        lightFixtureLabel = new javax.swing.JLabel();
        lightFixtureComboBox = new javax.swing.JComboBox();
        lightSizeLabel = new javax.swing.JLabel();
        lightSizeTextField = new javax.swing.JTextField();
        lightClassLabel = new javax.swing.JLabel();
        lightClassTextField = new javax.swing.JTextField();
        lightSeparator = new javax.swing.JSeparator();
        safetyInfoLabel = new javax.swing.JLabel();
        safetyCameraLabel = new javax.swing.JLabel();
        safetySensorLabel = new javax.swing.JLabel();
        safetySensorComboBox = new javax.swing.JComboBox();
        safetySizeLabel = new javax.swing.JLabel();
        safetySizeComboBox = new javax.swing.JComboBox();
        safetyPixelLabel = new javax.swing.JLabel();
        safetyPixelTextField = new javax.swing.JTextField();
        safetyIRLabel = new javax.swing.JLabel();
        safetyIRTextField = new javax.swing.JTextField();
        safetyMonitorLabel = new javax.swing.JLabel();
        safetyScreenLabel = new javax.swing.JLabel();
        safetyScreenComboBox = new javax.swing.JComboBox();
        safetyLinesLabel = new javax.swing.JLabel();
        safetyLinesTextField = new javax.swing.JTextField();
        safetySeparator = new javax.swing.JSeparator();
        doorchimesInfoLabel = new javax.swing.JLabel();
        doorchimesSoundLabel = new javax.swing.JLabel();
        doorchimesTempLabel = new javax.swing.JLabel();
        doorchimesTempComboBox = new javax.swing.JComboBox();
        RFfLabel = new javax.swing.JLabel();
        RFfField = new javax.swing.JTextField();
        logoLabel = new javax.swing.JLabel();
        logoCECheckBox = new javax.swing.JCheckBox();
        logoWeeeCheckBox = new javax.swing.JCheckBox();
        logoNBNLabel = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        erpPanel = new javax.swing.JPanel();
        kindBulbLabel = new javax.swing.JLabel();
        kindBulbComboBox = new javax.swing.JComboBox();
        specUseLabel = new javax.swing.JLabel();
        specUseComboBox = new javax.swing.JComboBox();
        inclCheckBox = new javax.swing.JCheckBox();
        bulbTextField = new javax.swing.JTextField();
        dateTestLabel1 = new javax.swing.JLabel();
        start6000hDateChooser = new com.toedter.calendar.JDateChooser();
        cleanStart6000hButton = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        received6000hDateChooser = new com.toedter.calendar.JDateChooser();
        cleanReceived6000hButton = new javax.swing.JButton();
        PackInfoPanel = new javax.swing.JPanel();
        packingLabel = new javax.swing.JLabel();
        voltageLabel = new javax.swing.JLabel();
        voltageComboBox = new javax.swing.JComboBox();
        amperLabel = new javax.swing.JLabel();
        amperTextField = new javax.swing.JTextField();
        wattageLabel = new javax.swing.JLabel();
        wattageTextField = new javax.swing.JTextField();
        lumenLabel = new javax.swing.JLabel();
        lumenTextField = new javax.swing.JTextField();
        lifetimeLabel = new javax.swing.JLabel();
        livetimeComboBox = new javax.swing.JComboBox();
        swicycLabel = new javax.swing.JLabel();
        swicycComboBox = new javax.swing.JComboBox();
        kelvinLabel = new javax.swing.JLabel();
        kelvinComboBox = new javax.swing.JComboBox();
        enclasLabel = new javax.swing.JLabel();
        enclasComboBox = new javax.swing.JComboBox();
        star60Label = new javax.swing.JLabel();
        star60TextField = new javax.swing.JTextField();
        dimmerLabel = new javax.swing.JLabel();
        dimmerComboBox = new javax.swing.JComboBox();
        beamLabel = new javax.swing.JLabel();
        beamTextField = new javax.swing.JTextField();
        raLabel = new javax.swing.JLabel();
        raComboBox = new javax.swing.JComboBox();
        comparLabel = new javax.swing.JLabel();
        comparTextField = new javax.swing.JTextField();
        fitttinLabel = new javax.swing.JLabel();
        fittinComboBox = new javax.swing.JComboBox();
        kwikLabel = new javax.swing.JLabel();
        kwikTextField = new javax.swing.JTextField();
        accentPackCheckBox = new javax.swing.JCheckBox();
        dimension_fiLabel = new javax.swing.JLabel();
        dimension_fiTextField = new javax.swing.JTextField();
        dimension_lLabel = new javax.swing.JLabel();
        dimension_lTextField = new javax.swing.JTextField();
        dimension_dLabel = new javax.swing.JLabel();
        dimension_dTextField = new javax.swing.JTextField();
        WebInfoPanel = new javax.swing.JPanel();
        websiteLabel = new javax.swing.JLabel();
        wattageRatedLabel = new javax.swing.JLabel();
        wattageRatedTextField = new javax.swing.JTextField();
        lumenRatedLabel = new javax.swing.JLabel();
        lumenratedTextField = new javax.swing.JTextField();
        lifetimeRatedLabel = new javax.swing.JLabel();
        lifetimeRatedTextField = new javax.swing.JTextField();
        colorLabel = new javax.swing.JLabel();
        colorTextField = new javax.swing.JTextField();
        angleRatedLabel = new javax.swing.JLabel();
        angleRatedTextField = new javax.swing.JTextField();
        indoorOutdoorLabel = new javax.swing.JLabel();
        indoorOutdoorComboBox = new javax.swing.JComboBox();
        powerFactorLabel = new javax.swing.JLabel();
        powerFactorTextField = new javax.swing.JTextField();
        lumenFactorLabel = new javax.swing.JLabel();
        lumenfactorTextField = new javax.swing.JTextField();
        startTimeLabel = new javax.swing.JLabel();
        startTimeTextField = new javax.swing.JTextField();
        lsfLabel = new javax.swing.JLabel();
        lsfTextField = new javax.swing.JTextField();
        colorConsLabel = new javax.swing.JLabel();
        colorConsTextField = new javax.swing.JTextField();
        candelaLabel = new javax.swing.JLabel();
        candelaTextField = new javax.swing.JTextField();
        ExtraInfoPanel = new javax.swing.JPanel();
        extraLabel = new javax.swing.JLabel();
        shapeLabel = new javax.swing.JLabel();
        shapeComboBox = new javax.swing.JComboBox();
        numberLEDLabel = new javax.swing.JLabel();
        numberLEDTextField = new javax.swing.JTextField();
        kindLedLabel = new javax.swing.JLabel();
        kindLEDTextField = new javax.swing.JTextField();
        uvCheckBox = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox();
        spectrumLabel = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        erpSpectrumLabel = new javax.swing.JLabel();
        exportButton = new javax.swing.JButton();
        sapField = new javax.swing.JTextField();
        sapLabel = new javax.swing.JLabel();
        originalItemField = new javax.swing.JTextField();
        hierarchyLabel = new javax.swing.JLabel();
        hierarchyComboBox = new javax.swing.JComboBox();
        herarchyLabel1 = new javax.swing.JLabel();
        validDate1Label = new javax.swing.JLabel();
        checkingLabel1 = new javax.swing.JLabel();
        checkingDateChooser1 = new com.toedter.calendar.JDateChooser();
        cleanKkDateButton1 = new javax.swing.JButton();
        eanLabel = new javax.swing.JLabel();
        eanTextField = new javax.swing.JTextField();
        pictureLabel = new javax.swing.JLabel();
        qcStatusLabel = new javax.swing.JLabel();
        authorityLabel = new javax.swing.JLabel();
        authorityScrollPane = new javax.swing.JScrollPane();
        authorityTextArea = new javax.swing.JTextArea();
        componentsPanel = new javax.swing.JPanel();
        componentsLabel = new javax.swing.JLabel();
        componentStatLabel1 = new javax.swing.JLabel();
        componentSapLabel1 = new javax.swing.JLabel();
        componentStatLabel2 = new javax.swing.JLabel();
        componentSapLabel2 = new javax.swing.JLabel();
        componentStatLabel3 = new javax.swing.JLabel();
        componentSapLabel3 = new javax.swing.JLabel();
        componentStatLabel4 = new javax.swing.JLabel();
        componentSapLabel4 = new javax.swing.JLabel();
        componentStatLabel5 = new javax.swing.JLabel();
        componentSapLabel5 = new javax.swing.JLabel();
        componentStatLabel6 = new javax.swing.JLabel();
        componentSapLabel6 = new javax.swing.JLabel();
        componentStatLabel7 = new javax.swing.JLabel();
        componentSapLabel7 = new javax.swing.JLabel();
        componentStatLabel8 = new javax.swing.JLabel();
        componentSapLabel8 = new javax.swing.JLabel();
        componentStatLabel9 = new javax.swing.JLabel();
        componentSapLabel9 = new javax.swing.JLabel();
        componentStatLabel10 = new javax.swing.JLabel();
        componentSapLabel10 = new javax.swing.JLabel();
        hiddenCheckBox = new javax.swing.JCheckBox();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem newRecordMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem deleteRecordMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        javax.swing.JMenuItem saveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem refreshMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        printMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jReportMenu = new javax.swing.JMenu();
        standardsReportMenu = new javax.swing.JMenuItem();
        authorityReportMenu = new javax.swing.JMenuItem();
        overviewReportMenu = new javax.swing.JMenu();
        exportOverviewReportMenu = new javax.swing.JMenuItem();
        openOverviewReportMenu = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        userMenu = new javax.swing.JMenu();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        DocDateChooser = new com.toedter.calendar.JDateChooser();
        docPdfButton = new javax.swing.JButton();
        docWordButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        newButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        folderButton = new javax.swing.JButton();
        productButton = new javax.swing.JButton();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getResourceMap(DesktopApplication1View.class);
        entityManager = java.beans.Beans.isDesignTime() ? null : javax.persistence.Persistence.createEntityManagerFactory(resourceMap.getString("entityManager.persistenceUnit")).createEntityManager(); // NOI18N
        query = java.beans.Beans.isDesignTime() ? null : entityManager.createQuery(resourceMap.getString("query.query")); // NOI18N
        list = java.beans.Beans.isDesignTime() ? java.util.Collections.emptyList() : org.jdesktop.observablecollections.ObservableCollections.observableList(query.getResultList());
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        rowSorterToStringConverter1 = new desktopapplication1.RowSorterToStringConverter();

        mainPanel.setForeground(resourceMap.getColor("mainPanel.foreground")); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        masterScrollPane.setAutoscrolls(true);
        masterScrollPane.setFont(resourceMap.getFont("masterScrollPane.font")); // NOI18N
        masterScrollPane.setName("masterScrollPane"); // NOI18N

        masterTable.setFont(resourceMap.getFont("masterTable.font")); // NOI18N
        masterTable.setName("masterTable"); // NOI18N
        masterTable.setRowHeight(22);

        org.jdesktop.swingbinding.JTableBinding jTableBinding = org.jdesktop.swingbinding.SwingBindings.createJTableBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, list, masterTable);
        org.jdesktop.swingbinding.JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${item}"));
        columnBinding.setColumnName("Item");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sap}"));
        columnBinding.setColumnName("Sap");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sItem}"));
        columnBinding.setColumnName("SItem");
        columnBinding.setColumnClass(String.class);
        columnBinding.setEditable(false);
        bindingGroup.addBinding(jTableBinding);
        jTableBinding.bind();
        masterTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                masterTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                masterTableMousePressed(evt);
            }
        });
        masterScrollPane.setViewportView(masterTable);
        masterTable.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("masterTable.columnModel.title0")); // NOI18N
        masterTable.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("masterTable.columnModel.title2")); // NOI18N
        masterTable.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("masterTable.columnModel.title2")); // NOI18N

        mainPanel.add(masterScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 150, 330));

        itemLabel.setFont(resourceMap.getFont("itemLabel.font")); // NOI18N
        itemLabel.setText(resourceMap.getString("itemLabel.text")); // NOI18N
        itemLabel.setName("itemLabel"); // NOI18N
        mainPanel.add(itemLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 10, -1, 25));

        itemField.setFont(resourceMap.getFont("itemField.font")); // NOI18N
        itemField.addMouseListener(new ContextMenuMouseListener());
        itemField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        itemField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        itemField.setName("itemField"); // NOI18N
        itemField.setPreferredSize(new java.awt.Dimension(4, 20));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.item}"), itemField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        mainPanel.add(itemField, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 170, 25));

        vendorLabel.setFont(resourceMap.getFont("vendorLabel.font")); // NOI18N
        vendorLabel.setText(resourceMap.getString("vendorLabel.text")); // NOI18N
        vendorLabel.setName("vendorLabel"); // NOI18N
        mainPanel.add(vendorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, -1, 20));

        vendorField.setFont(resourceMap.getFont("vendorField.font")); // NOI18N
        vendorField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        vendorField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        vendorField.setName("vendorField"); // NOI18N
        vendorField.setPreferredSize(new java.awt.Dimension(4, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.vendor}"), vendorField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        supplierField.addMouseListener(new ContextMenuMouseListener());
        mainPanel.add(vendorField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 50, 50, 20));

        supplierLabel.setFont(resourceMap.getFont("itemLabel.font")); // NOI18N
        supplierLabel.setText(resourceMap.getString("supplierLabel.text")); // NOI18N
        supplierLabel.setName("supplierLabel"); // NOI18N
        mainPanel.add(supplierLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(245, 50, -1, 20));

        supplierField.setFont(resourceMap.getFont("supplierField.font")); // NOI18N
        supplierField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        supplierField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        supplierField.setName("supplierField"); // NOI18N
        supplierField.setPreferredSize(new java.awt.Dimension(4, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.supplier}"), supplierField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        supplierField.addMouseListener(new ContextMenuMouseListener());
        mainPanel.add(supplierField, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 50, 245, 20));

        jItemCountLabel.setFont(resourceMap.getFont("jItemCountLabel.font")); // NOI18N
        jItemCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jItemCountLabel.setText(resourceMap.getString("jItemCountLabel.text")); // NOI18N
        jItemCountLabel.setName("jItemCountLabel"); // NOI18N
        mainPanel.add(jItemCountLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 150, 20));

        SupplierItemlabel.setFont(resourceMap.getFont("itemLabel.font")); // NOI18N
        SupplierItemlabel.setText(resourceMap.getString("SupplierItemlabel.text")); // NOI18N
        SupplierItemlabel.setName("SupplierItemlabel"); // NOI18N
        mainPanel.add(SupplierItemlabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(548, 50, -1, 20));

        ModDateField.setFont(resourceMap.getFont("ModDateField.font")); // NOI18N
        ModDateField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ModDateField.setName("ModDateField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.modDate}"), ModDateField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        mainPanel.add(ModDateField, new org.netbeans.lib.awtextra.AbsoluteConstraints(840, 535, 140, 15));

        ModWhoField.setFont(resourceMap.getFont("ModWhoField.font")); // NOI18N
        ModWhoField.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ModWhoField.setName("ModWhoField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.modWho}"), ModWhoField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        mainPanel.add(ModWhoField, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 535, 70, 15));

        statusLabel.setFont(resourceMap.getFont("statusLabel.font")); // NOI18N
        statusLabel.setText(resourceMap.getString("statusLabel.text")); // NOI18N
        statusLabel.setName("statusLabel"); // NOI18N
        mainPanel.add(statusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(815, 50, -1, 20));

        jStatusComboBox.setFont(resourceMap.getFont("jStatusComboBox.font")); // NOI18N
        jStatusComboBox.setMaximumRowCount(9);
        jStatusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "B1", "B3", "G0", "G1", "G2", "G3", "P1", "U0", "NA" }));
        jStatusComboBox.setName("jStatusComboBox"); // NOI18N
        jStatusComboBox.setPreferredSize(new java.awt.Dimension(70, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), jStatusComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.status}"), jStatusComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        jStatusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStatusComboBoxActionPerformed(evt);
            }
        });
        binding.setSourceUnreadableValue(null);
        mainPanel.add(jStatusComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 50, 45, -1));

        statusLabel1.setFont(resourceMap.getFont("statusLabel1.font")); // NOI18N
        statusLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        statusLabel1.setText(resourceMap.getString("statusLabel1.text")); // NOI18N
        statusLabel1.setName("statusLabel1"); // NOI18N
        mainPanel.add(statusLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 70, 180, 15));

        brandComboBox.setFont(resourceMap.getFont("brandComboBox.font")); // NOI18N
        brandComboBox.setForeground(resourceMap.getColor("brandComboBox.foreground")); // NOI18N
        brandComboBox.setMaximumRowCount(52);
        brandComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ELRO", "ABBHAF", "AJAX", "AKROBAC", "ALDI", "ALPHA", "ANSLUT", "ARTON", "AS WATSON", "BASELINE", "BAVARIA", "BLYSS", "BYRON", "CASAYA", "CAVIUS", "CHIQUE", "DIFFERNZ", "DUOLEC", "EATEL", "EDEN", "EDENPRO", "ELTRIC", "ENCHANTE", "ENERGYCARE", "EYSTON", "FIRST ALERT", "FLAMINGO", "GAMMA", "HAGEBAU", "HEMA", "HOFER", "HOMEBASE", "HOMEEASY", "HOMEWIZARD", "I-Glow", "INTERTOYS", "KONZUM", "KWANTUM", "LAP", "LIEF", "LUXTOOLS", "MAPE", "Maxeda", "MIRO", "mumbi", "NEDIS", "NORMA", "OK", "PACKINING", "PARTY LIGHTS", "POWERTEC", "PRAXIS", "PROMAX", "PROTEC", "PRYMOS", "RAINBOW", "RANEX", "SCANPART", "SITER", "SMARTLIGHTS", "SMARTWARES", "TECHNETIX", "TOPCRAFT", "UNBRANDED", "WATSHOME", "WETTLINE", "XQLITE" }));
        brandComboBox.setName("brandComboBox"); // NOI18N
        brandComboBox.setPreferredSize(new java.awt.Dimension(54, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), brandComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.brand}"), brandComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        binding.setSourceUnreadableValue(null);
        mainPanel.add(brandComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 50, 115, -1));

        descrNLField.setFont(resourceMap.getFont("descrNLField.font")); // NOI18N
        descrNLField.setName("descrNLField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descrNl}"), descrNLField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        descrNLField.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        mainPanel.add(descrNLField, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 110, 320, -1));

        descrESField.setFont(resourceMap.getFont("descrNLField.font")); // NOI18N
        descrESField.setName("descrESField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descrEs}"), descrESField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        descrESField.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        mainPanel.add(descrESField, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 110, 320, -1));

        descrPLField.setFont(resourceMap.getFont("descrPLField.font")); // NOI18N
        descrPLField.setName("descrPLField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descrPl}"), descrPLField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        descrPLField.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        mainPanel.add(descrPLField, new org.netbeans.lib.awtextra.AbsoluteConstraints(725, 110, 325, -1));

        jLabel11.setFont(resourceMap.getFont("jLabel11.font")); // NOI18N
        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(15, 14));
        mainPanel.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 90, 12, -1));

        jLabel12.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N
        jLabel12.setPreferredSize(new java.awt.Dimension(15, 14));
        mainPanel.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 90, 12, -1));

        jLabel13.setFont(resourceMap.getFont("jLabel12.font")); // NOI18N
        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(15, 13));
        mainPanel.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(712, 90, 12, -1));

        jFilter.setFont(resourceMap.getFont("jFilter.font")); // NOI18N
        jFilter.setToolTipText(resourceMap.getString("jFilter.toolTipText")); // NOI18N
        jFilter.setName("jFilter"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${rowSorter}"), jFilter, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setConverter(rowSorterToStringConverter1);
        bindingGroup.addBinding(binding);

        jFilter.addMouseListener(new ContextMenuMouseListener());
        mainPanel.add(jFilter, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 150, 30));

        descrENField1.setFont(resourceMap.getFont("descrENField1.font")); // NOI18N
        descrENField1.setName("descrENField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descrEn}"), descrENField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        descrENField1.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        mainPanel.add(descrENField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 90, 320, -1));

        descrDEField1.setFont(resourceMap.getFont("descrDEField1.font")); // NOI18N
        descrDEField1.setName("descrDEField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descrFr}"), descrDEField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        descrDEField1.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        mainPanel.add(descrDEField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 90, 320, -1));

        descrFRField1.setFont(resourceMap.getFont("descrFRField1.font")); // NOI18N
        descrFRField1.setName("descrFRField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.descrDe}"), descrFRField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        descrFRField1.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        mainPanel.add(descrFRField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(725, 90, 325, -1));

        jLabel14.setFont(resourceMap.getFont("jLabel14.font")); // NOI18N
        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(15, 14));
        mainPanel.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 110, 12, 10));

        jLabel15.setFont(resourceMap.getFont("jLabel15.font")); // NOI18N
        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(15, 14));
        mainPanel.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 110, 12, -1));

        jLabel16.setFont(resourceMap.getFont("jLabel16.font")); // NOI18N
        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(15, 13));
        mainPanel.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(712, 110, 12, -1));

        jTabbedPane1.setBackground(resourceMap.getColor("jTabbedPane1.background")); // NOI18N
        jTabbedPane1.setForeground(resourceMap.getColor("jTabbedPane1.foreground")); // NOI18N
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setName("jTabbedPane1");

        CertPanel.setBackground(resourceMap.getColor("CertPanel.background")); // NOI18N
        CertPanel.setName("CertPanel"); // NOI18N
        CertPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        directiveLabel.setFont(resourceMap.getFont("trLabel.font")); // NOI18N
        directiveLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        directiveLabel.setText(resourceMap.getString("directiveLabel.text")); // NOI18N
        directiveLabel.setName("directiveLabel"); // NOI18N
        directiveLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), directiveLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(directiveLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 0, 103, 20));

        ceLabel.setFont(resourceMap.getFont("trLabel.font")); // NOI18N
        ceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ceLabel.setText(resourceMap.getString("ceLabel.text")); // NOI18N
        ceLabel.setName("ceLabel"); // NOI18N
        ceLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), ceLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(ceLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 0, 120, 20));

        trLabel.setFont(resourceMap.getFont("trLabel.font")); // NOI18N
        trLabel.setForeground(resourceMap.getColor("trLabel.foreground")); // NOI18N
        trLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        trLabel.setText(resourceMap.getString("trLabel.text")); // NOI18N
        trLabel.setName("trLabel"); // NOI18N
        trLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), trLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(trLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 0, 140, 20));

        validFromDateLabel.setFont(resourceMap.getFont("trLabel.font")); // NOI18N
        validFromDateLabel.setForeground(resourceMap.getColor("validFromDateLabel.foreground")); // NOI18N
        validFromDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        validFromDateLabel.setText(resourceMap.getString("validFromDateLabel.text")); // NOI18N
        validFromDateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        validFromDateLabel.setName("validFromDateLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), validFromDateLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(validFromDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 0, 70, 20));

        nbLabel.setFont(resourceMap.getFont("trLabel.font")); // NOI18N
        nbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        nbLabel.setText(resourceMap.getString("nbLabel.text")); // NOI18N
        nbLabel.setName("nbLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), nbLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(nbLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 0, 80, 20));
        nbLabel.getAccessibleContext().setAccessibleName(resourceMap.getString("lvdNbLabel.AccessibleContext.accessibleName")); // NOI18N

        lvdCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        lvdCheckBox.setFont(resourceMap.getFont("emcCheckBox.font")); // NOI18N
        lvdCheckBox.setForeground(resourceMap.getColor("erpCheckBox.foreground")); // NOI18N
        lvdCheckBox.setText(resourceMap.getString("lvdCheckBox.text")); // NOI18N
        lvdCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lvdCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        lvdCheckBox.setName("lvdCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd}"), lvdCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), lvdCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvdCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lvdCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(lvdCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 20, 60, -1));

        lvdDirComboBox.setForeground(resourceMap.getColor("lvdDirComboBox.foreground")); // NOI18N
        lvdDirComboBox.setMaximumRowCount(2);
        lvdDirComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2006/95/EC", "2014/35/EU" }));
        lvdDirComboBox.setName("lvdDirComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), lvdDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdCe}"), lvdDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), lvdDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(lvdDirComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 95, -1));

        lvdCeField.setName("lvdCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdCert}"), lvdCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), lvdCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(lvdCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 20, 122, -1));

        lvdTrField.setName("lvdTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdTr}"), lvdTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), lvdTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvdTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(lvdTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 20, 140, -1));

        lvdDateChooser.setBackground(resourceMap.getColor("lvdDateChooser.background")); // NOI18N
        lvdDateChooser.setForeground(resourceMap.getColor("cprDateChooser.foreground")); // NOI18N
        lvdDateChooser.setDateFormatString(resourceMap.getString("lvdDateChooser.dateFormatString")); // NOI18N
        lvdDateChooser.setName("lvdDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdDate}"), lvdDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), lvdDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvdDateChooser.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(lvdDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 20, 85, -1));

        lvdCleanDateButton.setFont(resourceMap.getFont("lvdCleanDateButton.font")); // NOI18N
        lvdCleanDateButton.setForeground(resourceMap.getColor("lvdCleanDateButton.foreground")); // NOI18N
        lvdCleanDateButton.setText(resourceMap.getString("lvdCleanDateButton.text")); // NOI18N
        lvdCleanDateButton.setName("lvdCleanDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), lvdCleanDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvdCleanDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lvdCleanDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(lvdCleanDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(509, 20, 10, 10));

        lvdNbComboBox.setFont(resourceMap.getFont("jRoHSNBComboBox.font")); // NOI18N
        lvdNbComboBox.setMaximumRowCount(11);
        lvdNbComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "BV", "CTS", "EMTEK", "IECC", "INTERTEK", "KEMA", "LCS", "SGS", "TUV", "WALTEK" }));
        lvdNbComboBox.setName("lvdNbComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), lvdNbComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvdNb}"), lvdNbComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), lvdNbComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(lvdNbComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 20, 74, -1));

        gsNbLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gsNbLabel.setText(resourceMap.getString("gsNbLabel.text")); // NOI18N
        gsNbLabel.setName("gsNbLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), gsNbLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(gsNbLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 205, 21, 20));
        gsNbLabel.getAccessibleContext().setAccessibleName(resourceMap.getString("gsNbLabel.AccessibleContext.accessibleName")); // NOI18N

        oemDateFromLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        oemDateFromLabel.setText(resourceMap.getString("oemDateFromLabel.text")); // NOI18N
        oemDateFromLabel.setName("oemDateFromLabel"); // NOI18N
        oemDateFromLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, oemCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), oemDateFromLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(oemDateFromLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 180, 50, 20));

        gsCeField.setName("gsCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.gsCe}"), gsCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), gsCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        gsCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(gsCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 205, 129, -1));

        gsTrField.setName("gsTrField"); // NOI18N
        gsTrField.addMouseListener(new ContextMenuMouseListener());
        gsTrField.setScrollOffset(100);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.gsTr}"), gsTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), gsTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(gsTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 205, 140, -1));

        emcTrField.setForeground(resourceMap.getColor("emcTrField.foreground")); // NOI18N
        emcTrField.setName("emcTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcTr}"), emcTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), emcTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emcTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(emcTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 45, 140, -1));

        cprCeField.setName("cprCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpdCe}"), cprCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cprCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cprCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cprCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(cprCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 95, 122, -1));

        cprTrField.setName("cprTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpdTr}"), cprTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cprCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cprTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cprTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(cprTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 95, 140, -1));

        erpTrField.setName("erpTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eupTr}"), erpTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, erpCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), erpTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(erpTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 120, 140, -1));

        rohsTrField.setName("rohsTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohsTr}"), rohsTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rohsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), rohsTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rohsTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(rohsTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 150, 140, -1));

        oemCeField.setName("oemCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.oemCe}"), oemCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, oemCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), oemCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        oemCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(oemCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 180, 129, -1));

        gsCheckBox.setBackground(resourceMap.getColor("gsCheckBox.background")); // NOI18N
        gsCheckBox.setFont(resourceMap.getFont("komoCheckBox.font")); // NOI18N
        gsCheckBox.setForeground(resourceMap.getColor("komoCheckBox.foreground")); // NOI18N
        gsCheckBox.setText(resourceMap.getString("gsCheckBox.text")); // NOI18N
        gsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gsCheckBox.setName("gsCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.gs}"), gsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), gsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(gsCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 205, 142, 20));

        emcCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        emcCheckBox.setFont(resourceMap.getFont("emcCheckBox.font")); // NOI18N
        emcCheckBox.setForeground(resourceMap.getColor("erpCheckBox.foreground")); // NOI18N
        emcCheckBox.setText(resourceMap.getString("emcCheckBox.text")); // NOI18N
        emcCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        emcCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        emcCheckBox.setName("emcCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc}"), emcCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), emcCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emcCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emcCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(emcCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 45, 60, -1));

        cprCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        cprCheckBox.setFont(resourceMap.getFont("emcCheckBox.font")); // NOI18N
        cprCheckBox.setForeground(resourceMap.getColor("erpCheckBox.foreground")); // NOI18N
        cprCheckBox.setText(resourceMap.getString("cprCheckBox.text")); // NOI18N
        cprCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cprCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cprCheckBox.setName("cprCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpd}"), cprCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), cprCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cprCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cprCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(cprCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(-5, 95, 65, -1));

        rfCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        rfCheckBox.setFont(resourceMap.getFont("emcCheckBox.font")); // NOI18N
        rfCheckBox.setForeground(resourceMap.getColor("erpCheckBox.foreground")); // NOI18N
        rfCheckBox.setText(resourceMap.getString("rfCheckBox.text")); // NOI18N
        rfCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        rfCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        rfCheckBox.setName("rfCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rf}"), rfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), rfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rfCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rfCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(rfCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 70, 62, -1));

        rohsCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        rohsCheckBox.setForeground(resourceMap.getColor("erpCheckBox.foreground")); // NOI18N
        rohsCheckBox.setText(resourceMap.getString("rohsCheckBox.text")); // NOI18N
        rohsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        rohsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        rohsCheckBox.setName("rohsCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohs}"), rohsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), rohsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rohsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rohsCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(rohsCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 60, -1));

        reachCheckBox.setBackground(resourceMap.getColor("reachCheckBox.background")); // NOI18N
        reachCheckBox.setForeground(resourceMap.getColor("reachCheckBox.foreground")); // NOI18N
        reachCheckBox.setText(resourceMap.getString("reachCheckBox.text")); // NOI18N
        reachCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        reachCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        reachCheckBox.setName("reachCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.reach}"), reachCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), reachCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        reachCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reachCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(reachCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(741, 150, 59, 20));

        erpCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        erpCheckBox.setFont(resourceMap.getFont("emcCheckBox.font")); // NOI18N
        erpCheckBox.setForeground(resourceMap.getColor("erpCheckBox.foreground")); // NOI18N
        erpCheckBox.setText(resourceMap.getString("erpCheckBox.text")); // NOI18N
        erpCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        erpCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        erpCheckBox.setName("erpCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), erpCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), erpCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                erpCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(erpCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 120, 60, 20));

        batt1TrCheckBox.setBackground(resourceMap.getColor("batt1TrCheckBox.background")); // NOI18N
        batt1TrCheckBox.setText(resourceMap.getString("batt1TrCheckBox.text")); // NOI18N
        batt1TrCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        batt1TrCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        batt1TrCheckBox.setName("batt1TrCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.batt}"), batt1TrCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), batt1TrCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        batt1TrCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batt1TrCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(batt1TrCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(698, 240, 100, 18));

        fluxCheckBox.setBackground(resourceMap.getColor("fluxCheckBox.background")); // NOI18N
        fluxCheckBox.setForeground(resourceMap.getColor("fluxCheckBox.foreground")); // NOI18N
        fluxCheckBox.setText(resourceMap.getString("fluxCheckBox.text")); // NOI18N
        fluxCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fluxCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        fluxCheckBox.setName("fluxCheckBox"); // NOI18N
        fluxCheckBox.setPreferredSize(new java.awt.Dimension(17, 18));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.flux}"), fluxCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), fluxCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        fluxCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fluxCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(fluxCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 120, 71, -1));

        pahCheckBox.setBackground(resourceMap.getColor("pahCheckBox.background")); // NOI18N
        pahCheckBox.setForeground(resourceMap.getColor("pahCheckBox.foreground")); // NOI18N
        pahCheckBox.setText(resourceMap.getString("pahCheckBox.text")); // NOI18N
        pahCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        pahCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        pahCheckBox.setName("pahCheckBox"); // NOI18N
        pahCheckBox.setPreferredSize(new java.awt.Dimension(61, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.pah}"), pahCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), pahCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        pahCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pahCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(pahCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(593, 150, 45, -1));

        phthCheckBox.setBackground(resourceMap.getColor("phthCheckBox.background")); // NOI18N
        phthCheckBox.setFont(resourceMap.getFont("phthCheckBox.font")); // NOI18N
        phthCheckBox.setText(resourceMap.getString("phthCheckBox.text")); // NOI18N
        phthCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        phthCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        phthCheckBox.setName("phthCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.phth}"), phthCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), phthCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(phthCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 220, 103, 14));

        oemCheckBox.setBackground(resourceMap.getColor("oemCheckBox.background")); // NOI18N
        oemCheckBox.setFont(resourceMap.getFont("komoCheckBox.font")); // NOI18N
        oemCheckBox.setForeground(resourceMap.getColor("komoCheckBox.foreground")); // NOI18N
        oemCheckBox.setText(resourceMap.getString("oemCheckBox.text")); // NOI18N
        oemCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        oemCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        oemCheckBox.setName("oemCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.oem}"), oemCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), oemCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(oemCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 180, 142, -1));

        jGSNBComboBox.setMaximumRowCount(10);
        jGSNBComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "BV", "CTS", "IECC", "INTERTEK", "KEMA", "LCS", "SGS", "TUV", "WALTEK" }));
        jGSNBComboBox.setName("jGSNBComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), jGSNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.gsNb}"), jGSNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), jGSNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(jGSNBComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 205, 80, -1));

        jEMCNBComboBox.setFont(resourceMap.getFont("jRoHSNBComboBox.font")); // NOI18N
        jEMCNBComboBox.setMaximumRowCount(11);
        jEMCNBComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "BV", "CTS", "EMTEK", "IECC", "INTERTEK", "KEMA", "LCS", "SGS", "TUV", "WALTEK" }));
        jEMCNBComboBox.setName("jEMCNBComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), jEMCNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcNb}"), jEMCNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), jEMCNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(jEMCNBComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 45, 74, -1));

        jRFNBComboBox.setFont(resourceMap.getFont("jRoHSNBComboBox.font")); // NOI18N
        jRFNBComboBox.setMaximumRowCount(11);
        jRFNBComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "BV", "CTS", "EMTEK", "IECC", "INTERTEK", "KEMA", "LCS", "SGS", "TUV", "WALTEK" }));
        jRFNBComboBox.setName("jRFNBComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), jRFNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfNb}"), jRFNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), jRFNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(jRFNBComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 70, 74, -1));

        cprNBComboBox.setFont(resourceMap.getFont("jRoHSNBComboBox.font")); // NOI18N
        cprNBComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "AFNOR", "ANPI", "BRE Global", "KRIWAN", "VdS" }));
        cprNBComboBox.setName("cprNBComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), cprNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpdNb}"), cprNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cprCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cprNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(cprNBComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 95, 74, -1));

        jRoHSNBComboBox.setFont(resourceMap.getFont("jRoHSNBComboBox.font")); // NOI18N
        jRoHSNBComboBox.setMaximumRowCount(11);
        jRoHSNBComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "BV", "CTS", "EMTEK", "IECC", "INTERTEK", "KEMA", "LCS", "SGS", "TUV", "WALTEK" }));
        jRoHSNBComboBox.setName("jRoHSNBComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), jRoHSNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohsNb}"), jRoHSNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rohsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), jRoHSNBComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(jRoHSNBComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 150, 72, -1));

        VdsCeField.setName("VdsCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.vdsCe}"), VdsCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, vdsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), VdsCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        VdsCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(VdsCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 230, 129, -1));

        VdsTrField.setName("VdsTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.vdsTr}"), VdsTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, vdsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), VdsTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        VdsTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(VdsTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 230, 140, -1));

        VdsDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        VdsDateLabel.setText(resourceMap.getString("VdsDateLabel.text")); // NOI18N
        VdsDateLabel.setName("VdsDateLabel"); // NOI18N
        VdsDateLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, vdsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), VdsDateLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(VdsDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 230, 40, 20));

        vdsCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        vdsCheckBox.setFont(resourceMap.getFont("komoCheckBox.font")); // NOI18N
        vdsCheckBox.setForeground(resourceMap.getColor("komoCheckBox.foreground")); // NOI18N
        vdsCheckBox.setText(resourceMap.getString("vdsCheckBox.text")); // NOI18N
        vdsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        vdsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        vdsCheckBox.setName("vdsCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.vds}"), vdsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), vdsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(vdsCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 230, 142, -1));

        BosecCeField.setName("BosecCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bosecCe}"), BosecCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, bosecCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), BosecCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        BosecCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(BosecCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 280, 129, -1));

        bosecCheckBox.setBackground(resourceMap.getColor("lvdCheckBox.background")); // NOI18N
        bosecCheckBox.setFont(resourceMap.getFont("komoCheckBox.font")); // NOI18N
        bosecCheckBox.setForeground(resourceMap.getColor("komoCheckBox.foreground")); // NOI18N
        bosecCheckBox.setText(resourceMap.getString("bosecCheckBox.text")); // NOI18N
        bosecCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        bosecCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        bosecCheckBox.setName("bosecCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bosec}"), bosecCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), bosecCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(bosecCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 280, 142, -1));

        BosecDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        BosecDateLabel.setText(resourceMap.getString("BosecDateLabel.text")); // NOI18N
        BosecDateLabel.setMaximumSize(new java.awt.Dimension(100, 15));
        BosecDateLabel.setMinimumSize(new java.awt.Dimension(100, 15));
        BosecDateLabel.setName("BosecDateLabel"); // NOI18N
        BosecDateLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, bosecCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), BosecDateLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(BosecDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 280, 50, 20));

        KomoCeField.setName("KomoCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.komoCe}"), KomoCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, komoCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), KomoCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        KomoCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(KomoCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 305, 129, -1));

        komoCheckBox.setBackground(resourceMap.getColor("komoCheckBox.background")); // NOI18N
        komoCheckBox.setFont(resourceMap.getFont("komoCheckBox.font")); // NOI18N
        komoCheckBox.setForeground(resourceMap.getColor("komoCheckBox.foreground")); // NOI18N
        komoCheckBox.setText(resourceMap.getString("komoCheckBox.text")); // NOI18N
        komoCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        komoCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        komoCheckBox.setName("komoCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.komo}"), komoCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), komoCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(komoCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 305, 142, -1));

        KomoDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        KomoDateLabel.setText(resourceMap.getString("KomoDateLabel.text")); // NOI18N
        KomoDateLabel.setName("KomoDateLabel"); // NOI18N
        KomoDateLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, komoCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), KomoDateLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(KomoDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 305, 50, 20));

        nfCheckBox.setBackground(resourceMap.getColor("nfCheckBox.background")); // NOI18N
        nfCheckBox.setFont(resourceMap.getFont("komoCheckBox.font")); // NOI18N
        nfCheckBox.setForeground(resourceMap.getColor("komoCheckBox.foreground")); // NOI18N
        nfCheckBox.setText(resourceMap.getString("nfCheckBox.text")); // NOI18N
        nfCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        nfCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        nfCheckBox.setName("nfCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nf}"), nfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), nfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(nfCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 255, 142, -1));

        NfCeField.setName("NfCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nfCe}"), NfCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, nfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), NfCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        NfCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(NfCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 255, 129, -1));

        NfTrField.setName("NfTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nfTr}"), NfTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, nfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), NfTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        NfTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(NfTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 255, 140, -1));

        otherCheckBox.setBackground(resourceMap.getColor("otherCheckBox.background")); // NOI18N
        otherCheckBox.setFont(resourceMap.getFont("komoCheckBox.font")); // NOI18N
        otherCheckBox.setForeground(resourceMap.getColor("komoCheckBox.foreground")); // NOI18N
        otherCheckBox.setText(resourceMap.getString("otherCheckBox.text")); // NOI18N
        otherCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        otherCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        otherCheckBox.setName("otherCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kk}"), otherCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), otherCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(otherCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 330, 142, -1));

        otherCeField.setName("otherCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kkCe}"), otherCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, otherCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), otherCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        otherCeField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(otherCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 330, 129, -1));

        otherDateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        otherDateLabel.setText(resourceMap.getString("otherDateLabel.text")); // NOI18N
        otherDateLabel.setName("otherDateLabel"); // NOI18N
        otherDateLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, otherCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), otherDateLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(otherDateLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 330, 50, 20));

        rfNbNLabel.setForeground(resourceMap.getColor("rfNbNLabel.foreground")); // NOI18N
        rfNbNLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        rfNbNLabel.setText(resourceMap.getString("rfNbNLabel.text")); // NOI18N
        rfNbNLabel.setName("rfNbNLabel"); // NOI18N
        rfNbNLabel.setPreferredSize(new java.awt.Dimension(40, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), rfNbNLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(rfNbNLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 70, 35, -1));

        RFNBNField.setForeground(resourceMap.getColor("rfNbNLabel.foreground")); // NOI18N
        RFNBNField.setName("RFNBNField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfNbN}"), RFNBNField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), RFNBNField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(RFNBNField, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 70, 30, -1));

        remarksScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        remarksScrollPane.setHorizontalScrollBar(null);
        remarksScrollPane.setName("remarksScrollPane"); // NOI18N

        remarksTextArea.setColumns(1);
        remarksTextArea.setRows(4);
        remarksTextArea.setName("remarksTextArea"); // NOI18N
        remarksTextArea.setPreferredSize(new java.awt.Dimension(12, 75));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.remarks}"), remarksTextArea, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), remarksTextArea, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        remarksTextArea.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        remarksScrollPane.setViewportView(remarksTextArea);

        CertPanel.add(remarksScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 280, 440, 80));

        rfTrField.setName("rfTrField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfTr}"), rfTrField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), rfTrField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rfTrField.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(rfTrField, new org.netbeans.lib.awtextra.AbsoluteConstraints(282, 70, 140, -1));

        OemDateFromChooser.setBackground(resourceMap.getColor("OemDateFromChooser.background")); // NOI18N
        OemDateFromChooser.setForeground(resourceMap.getColor("OemDateFromChooser.foreground")); // NOI18N
        OemDateFromChooser.setDateFormatString(resourceMap.getString("OemDateFromChooser.dateFormatString")); // NOI18N
        OemDateFromChooser.setName("OemDateFromChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.oemDateFrom}"), OemDateFromChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, oemCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), OemDateFromChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        OemDateFromChooser.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(OemDateFromChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, 85, -1));

        cleanOemDateFromButton.setFont(resourceMap.getFont("cleanOemDateFromButton.font")); // NOI18N
        cleanOemDateFromButton.setForeground(resourceMap.getColor("cleanOemDateFromButton.foreground")); // NOI18N
        cleanOemDateFromButton.setText(resourceMap.getString("cleanOemDateFromButton.text")); // NOI18N
        cleanOemDateFromButton.setName("cleanOemDateFromButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, oemCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanOemDateFromButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanOemDateFromButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanOemDateFromButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanOemDateFromButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 180, 10, 10));

        OemDateToChooser.setBackground(resourceMap.getColor("OemDateToChooser.background")); // NOI18N
        OemDateToChooser.setForeground(resourceMap.getColor("OemDateToChooser.foreground")); // NOI18N
        OemDateToChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        OemDateToChooser.setName("OemDateToChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.oemDateTo}"), OemDateToChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, oemCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), OemDateToChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        OemDateToChooser.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(OemDateToChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 180, 85, -1));

        cleanOemDateToButton.setFont(resourceMap.getFont("cleanOemDateToButton.font")); // NOI18N
        cleanOemDateToButton.setForeground(resourceMap.getColor("cleanOemDateToButton.foreground")); // NOI18N
        cleanOemDateToButton.setText(resourceMap.getString("cleanOemDateToButton.text")); // NOI18N
        cleanOemDateToButton.setName("cleanOemDateToButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, oemCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanOemDateToButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanOemDateToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanOemDateToButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanOemDateToButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 180, 10, 10));

        NfDateChooser.setBackground(resourceMap.getColor("NfDateChooser.background")); // NOI18N
        NfDateChooser.setForeground(resourceMap.getColor("NfDateChooser.foreground")); // NOI18N
        NfDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        NfDateChooser.setName("NfDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.nfDate}"), NfDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, nfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), NfDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        NfDateChooser.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(NfDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 255, 85, -1));

        cleanNfDateToButton.setFont(resourceMap.getFont("cleanNfDateToButton.font")); // NOI18N
        cleanNfDateToButton.setForeground(resourceMap.getColor("cleanNfDateToButton.foreground")); // NOI18N
        cleanNfDateToButton.setText(resourceMap.getString("cleanNfDateToButton.text")); // NOI18N
        cleanNfDateToButton.setName("cleanNfDateToButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, nfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanNfDateToButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanNfDateToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanNfDateToButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanNfDateToButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 255, 10, 10));

        GsDateChooser.setBackground(resourceMap.getColor("GsDateChooser.background")); // NOI18N
        GsDateChooser.setForeground(resourceMap.getColor("GsDateChooser.foreground")); // NOI18N
        GsDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        GsDateChooser.setName("GsDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.gsDate}"), GsDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), GsDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        NfDateChooser.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(GsDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 205, 85, -1));

        cleanGsDateToButton.setFont(resourceMap.getFont("cleanGsDateToButton.font")); // NOI18N
        cleanGsDateToButton.setForeground(resourceMap.getColor("cleanGsDateToButton.foreground")); // NOI18N
        cleanGsDateToButton.setText(resourceMap.getString("cleanGsDateToButton.text")); // NOI18N
        cleanGsDateToButton.setName("cleanGsDateToButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanGsDateToButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanGsDateToButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanGsDateToButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanGsDateToButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 205, 10, 10));

        EmcDateChooser.setBackground(resourceMap.getColor("EmcDateChooser.background")); // NOI18N
        EmcDateChooser.setForeground(resourceMap.getColor("cprDateChooser.foreground")); // NOI18N
        EmcDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        EmcDateChooser.setName("EmcDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcDate}"), EmcDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), EmcDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvdDateChooser.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(EmcDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 45, 85, -1));

        cleanEmcDateButton.setFont(resourceMap.getFont("cleanEmcDateButton.font")); // NOI18N
        cleanEmcDateButton.setForeground(resourceMap.getColor("cleanEmcDateButton.foreground")); // NOI18N
        cleanEmcDateButton.setText(resourceMap.getString("cleanEmcDateButton.text")); // NOI18N
        cleanEmcDateButton.setName("cleanEmcDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanEmcDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanEmcDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanEmcDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanEmcDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(509, 45, 10, 10));

        RtteDateChooser.setBackground(resourceMap.getColor("RtteDateChooser.background")); // NOI18N
        RtteDateChooser.setForeground(resourceMap.getColor("cprDateChooser.foreground")); // NOI18N
        RtteDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        RtteDateChooser.setName("RtteDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfDate}"), RtteDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), RtteDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvdDateChooser.addMouseListener(new ContextMenuMouseListener());
        CertPanel.add(RtteDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 70, 85, -1));

        cleanRfDateButton.setFont(resourceMap.getFont("cleanRfDateButton.font")); // NOI18N
        cleanRfDateButton.setForeground(resourceMap.getColor("cleanRfDateButton.foreground")); // NOI18N
        cleanRfDateButton.setText(resourceMap.getString("cleanRfDateButton.text")); // NOI18N
        cleanRfDateButton.setName("cleanRfDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanRfDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanRfDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanRfDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanRfDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(509, 70, 10, 10));

        EupDateChooser.setForeground(resourceMap.getColor("cprDateChooser.foreground")); // NOI18N
        EupDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        EupDateChooser.setName("EupDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eupDate}"), EupDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, erpCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), EupDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(EupDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 120, 85, -1));

        cleanErpDateButton.setFont(resourceMap.getFont("cleanErpDateButton.font")); // NOI18N
        cleanErpDateButton.setForeground(resourceMap.getColor("cleanErpDateButton.foreground")); // NOI18N
        cleanErpDateButton.setText(resourceMap.getString("cleanErpDateButton.text")); // NOI18N
        cleanErpDateButton.setName("cleanErpDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, erpCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanErpDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanErpDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanErpDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanErpDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(509, 120, 10, 10));

        RohsDateChooser.setForeground(resourceMap.getColor("cprDateChooser.foreground")); // NOI18N
        RohsDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        RohsDateChooser.setName("RohsDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohsDate}"), RohsDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rohsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), RohsDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(RohsDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 150, 85, -1));

        cleanRohsDateButton.setFont(resourceMap.getFont("cleanRohsDateButton.font")); // NOI18N
        cleanRohsDateButton.setForeground(resourceMap.getColor("cleanRohsDateButton.foreground")); // NOI18N
        cleanRohsDateButton.setText(resourceMap.getString("cleanRohsDateButton.text")); // NOI18N
        cleanRohsDateButton.setName("cleanRohsDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rohsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanRohsDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanRohsDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanRohsDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanRohsDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(509, 150, 10, 10));

        cprDateChooser.setForeground(resourceMap.getColor("cprDateChooser.foreground")); // NOI18N
        cprDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        cprDateChooser.setName("cprDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpdDate}"), cprDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cprCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cprDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(cprDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(425, 95, 85, -1));

        cleanCprDateButton.setFont(resourceMap.getFont("cleanCprDateButton.font")); // NOI18N
        cleanCprDateButton.setForeground(resourceMap.getColor("cleanCprDateButton.foreground")); // NOI18N
        cleanCprDateButton.setText(resourceMap.getString("cleanCprDateButton.text")); // NOI18N
        cleanCprDateButton.setName("cleanCprDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cprCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanCprDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanCprDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanCprDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanCprDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 95, 10, 10));

        VdsDateChooser.setDateFormatString(resourceMap.getString("LvdDateChooser.dateFormatString")); // NOI18N
        VdsDateChooser.setName("VdsDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.vdsDate}"), VdsDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, vdsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), VdsDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(VdsDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 230, 85, -1));

        cleanVdsDateButton.setFont(resourceMap.getFont("cleanVdsDateButton.font")); // NOI18N
        cleanVdsDateButton.setForeground(resourceMap.getColor("cleanVdsDateButton.foreground")); // NOI18N
        cleanVdsDateButton.setText(resourceMap.getString("cleanVdsDateButton.text")); // NOI18N
        cleanVdsDateButton.setName("cleanVdsDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, vdsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanVdsDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanVdsDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanVdsDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanVdsDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(545, 230, 10, 10));

        BosecDateChooser.setDateFormatString(resourceMap.getString("BosecDateChooser.dateFormatString")); // NOI18N
        BosecDateChooser.setName("BosecDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.bosecDate}"), BosecDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, bosecCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), BosecDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(BosecDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 280, 85, -1));

        cleanBosecDateButton.setFont(resourceMap.getFont("cleanBosecDateButton.font")); // NOI18N
        cleanBosecDateButton.setForeground(resourceMap.getColor("cleanBosecDateButton.foreground")); // NOI18N
        cleanBosecDateButton.setText(resourceMap.getString("cleanBosecDateButton.text")); // NOI18N
        cleanBosecDateButton.setName("cleanBosecDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, bosecCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanBosecDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanBosecDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanBosecDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanBosecDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 280, 10, 10));

        KomoDateChooser.setDateFormatString(resourceMap.getString("KomoDateChooser.dateFormatString")); // NOI18N
        KomoDateChooser.setName("KomoDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.komoDate}"), KomoDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, komoCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), KomoDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(KomoDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 305, 85, -1));

        cleanKomoDateButton.setFont(resourceMap.getFont("cleanKomoDateButton.font")); // NOI18N
        cleanKomoDateButton.setForeground(resourceMap.getColor("cleanKomoDateButton.foreground")); // NOI18N
        cleanKomoDateButton.setText(resourceMap.getString("cleanKomoDateButton.text")); // NOI18N
        cleanKomoDateButton.setName("cleanKomoDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, komoCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanKomoDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanKomoDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanKomoDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanKomoDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 305, 10, 10));

        otherDateChooser.setDateFormatString(resourceMap.getString("KomoDateChooser.dateFormatString")); // NOI18N
        otherDateChooser.setName("otherDateChooser"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kkDate}"), otherDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, otherCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), otherDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(otherDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 330, 85, -1));

        cleanOtherDateButton.setFont(resourceMap.getFont("cleanOtherDateButton.font")); // NOI18N
        cleanOtherDateButton.setForeground(resourceMap.getColor("cleanOtherDateButton.foreground")); // NOI18N
        cleanOtherDateButton.setText(resourceMap.getString("cleanOtherDateButton.text")); // NOI18N
        cleanOtherDateButton.setName("cleanOtherDateButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, otherCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cleanOtherDateButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanOtherDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanOtherDateButtonActionPerformed(evt);
            }
        });
        CertPanel.add(cleanOtherDateButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(415, 330, 10, 10));

        rohsDirComboBox.setMaximumRowCount(2);
        rohsDirComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2011/65/EU", "91/338/EEC" }));
        rohsDirComboBox.setName("rohsDirComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), rohsDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohsCe}"), rohsDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rohsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), rohsDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(rohsDirComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 150, 95, -1));

        erpDirComboBox.setFont(resourceMap.getFont("erpDirComboBox.font")); // NOI18N
        erpDirComboBox.setMaximumRowCount(7);
        erpDirComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EU 874/2012", "EU 1194/2012", "EU 1194/2012, 874/2012", "EU 1194/2012, 244/2009, 859/2009", "EU 1194/2012, 244/2009, 859/2009, 874/2012", "EU 244/2009, 859/2009, 874/2012", "EU 245/2009, 874/2012" }));
        erpDirComboBox.setName("erpDirComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), erpDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eupCe}"), erpDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, erpCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), erpDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpDirComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                erpDirComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(erpDirComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, 220, -1));

        emcDirComboBox.setMaximumRowCount(2);
        emcDirComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2004/108/EC", "2014/30/EU" }));
        emcDirComboBox.setName("emcDirComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), emcDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcCe}"), emcDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), emcDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(emcDirComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 45, 95, -1));

        pahCeComboBox.setMaximumRowCount(3);
        pahCeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ZEK 01.4-08", "AfPS GS 2014:01 PAK", "MISSING" }));
        pahCeComboBox.setName("pahCeComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), pahCeComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.PAHCE}"), pahCeComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, pahCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), pahCeComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        pahCeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pahCeComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(pahCeComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(638, 150, 102, -1));

        rfDirComboBox.setMaximumRowCount(1);
        rfDirComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1999/5/EC" }));
        rfDirComboBox.setName("rfDirComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), rfDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfCe}"), rfDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), rfDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(rfDirComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 70, 95, -1));

        erpStatusComboBox.setFont(resourceMap.getFont("erpStatusComboBox.font")); // NOI18N
        erpStatusComboBox.setMaximumRowCount(4);
        erpStatusComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Initial,1000h", "2000h", "6000h" }));
        erpStatusComboBox.setName("erpStatusComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), erpStatusComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eupStatus}"), erpStatusComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, erpCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), erpStatusComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(erpStatusComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 120, 90, -1));

        jLabel10.setForeground(resourceMap.getColor("jLabel10.foreground")); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, gsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), jLabel10, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 205, 40, 20));

        jLabel18.setForeground(resourceMap.getColor("jLabel18.foreground")); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, nfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), jLabel18, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 255, 40, 20));

        jLabel19.setForeground(resourceMap.getColor("jLabel19.foreground")); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, oemCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), jLabel19, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 180, 40, 20));

        fluxTrComboBox.setMaximumRowCount(3);
        fluxTrComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "TR", "MISSING" }));
        fluxTrComboBox.setName("fluxTrComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), fluxTrComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.fluxTr}"), fluxTrComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, fluxCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), fluxTrComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        fluxTrComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fluxTrComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(fluxTrComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(691, 120, 70, -1));

        batt1TrComboBox.setMaximumRowCount(5);
        batt1TrComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "2006/66/EC", "2013/56/EU,2006/66/EC", "Lithium TR", "Excluding", "MISSING" }));
        batt1TrComboBox.setName("batt1TrComboBox"); // NOI18N
        batt1TrComboBox.setPreferredSize(new java.awt.Dimension(82, 18));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), batt1TrComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.battm}"), batt1TrComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1TrCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), batt1TrComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        batt1TrComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batt1TrComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(batt1TrComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(798, 240, -1, -1));

        batt2TrCheckBox.setBackground(resourceMap.getColor("batt2TrCheckBox.background")); // NOI18N
        batt2TrCheckBox.setText(resourceMap.getString("batt2TrCheckBox.text")); // NOI18N
        batt2TrCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        batt2TrCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        batt2TrCheckBox.setName("batt2TrCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT2}"), batt2TrCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), batt2TrCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        batt2TrCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batt2TrCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(batt2TrCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(698, 260, 100, 18));

        batt2TrComboBox.setMaximumRowCount(5);
        batt2TrComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "2006/66/EC", "2013/56/EU,2006/66/EC", "Lithium TR", "Excluding", "MISSING" }));
        batt2TrComboBox.setName("batt2TrComboBox"); // NOI18N
        batt2TrComboBox.setPreferredSize(new java.awt.Dimension(82, 18));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), batt2TrComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.battTr2}"), batt2TrComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2TrCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), batt2TrComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        batt2TrComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batt2TrComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(batt2TrComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(798, 260, -1, -1));

        docCheckBox.setBackground(resourceMap.getColor("docCheckBox.background")); // NOI18N
        docCheckBox.setFont(resourceMap.getFont("docCheckBox.font")); // NOI18N
        docCheckBox.setText(resourceMap.getString("docCheckBox.text")); // NOI18N
        docCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        docCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        docCheckBox.setName("docCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.doc}"), docCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), docCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(docCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 180, 86, 14));

        reachCeComboBox.setMaximumRowCount(3);
        reachCeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Declaration", "1907/2006", "MISSING" }));
        reachCeComboBox.setName("reachCeComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), reachCeComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.reachCe}"), reachCeComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, reachCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), reachCeComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        reachCeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reachCeComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(reachCeComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 150, 80, -1));

        doiCheckBox.setBackground(resourceMap.getColor("doiCheckBox.background")); // NOI18N
        doiCheckBox.setText(resourceMap.getString("doiCheckBox.text")); // NOI18N
        doiCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        doiCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        doiCheckBox.setName("doiCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.doi}"), doiCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), doiCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(doiCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(800, 180, 83, 14));

        cdfCheckBox.setBackground(resourceMap.getColor("cdfCheckBox.background")); // NOI18N
        cdfCheckBox.setForeground(resourceMap.getColor("cdfCheckBox.foreground")); // NOI18N
        cdfCheckBox.setText(resourceMap.getString("cdfCheckBox.text")); // NOI18N
        cdfCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        cdfCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        cdfCheckBox.setName("cdfCheckBox"); // NOI18N
        cdfCheckBox.setPreferredSize(new java.awt.Dimension(40, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.gsCdf}"), cdfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), cdfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(cdfCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(675, 205, 45, -1));

        cprDirComboBox.setMaximumRowCount(7);
        cprDirComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "(EU) 305/2011", "97/23/EC", "EN50194-1", "EN50291-1", "EN1869", "2001/95/EC", "89/686/EEC" }));
        cprDirComboBox.setName("cprDirComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), cprDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpdDir}"), cprDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cprCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), cprDirComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(cprDirComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 95, 95, -1));

        emcCeField.setName("emcCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emcCert}"), emcCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), emcCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(emcCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 45, 122, -1));

        rfCeField.setName("rfCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfCert}"), rfCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), rfCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(rfCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 70, 122, -1));

        rohsCeField.setName("rohsCeField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rohsCert}"), rohsCeField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rohsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), rohsCeField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        CertPanel.add(rohsCeField, new org.netbeans.lib.awtextra.AbsoluteConstraints(157, 150, 122, -1));

        photobiolCheckBox.setBackground(resourceMap.getColor("photobiolCheckBox.background")); // NOI18N
        photobiolCheckBox.setFont(resourceMap.getFont("photobiolCheckBox.font")); // NOI18N
        photobiolCheckBox.setForeground(resourceMap.getColor("photobiolCheckBox.foreground")); // NOI18N
        photobiolCheckBox.setText(resourceMap.getString("photobiolCheckBox.text")); // NOI18N
        photobiolCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        photobiolCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        photobiolCheckBox.setName("photobiolCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.photobiol}"), photobiolCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), photobiolCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        photobiolCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                photobiolCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(photobiolCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(605, 20, -1, -1));

        photobiolComboBox.setMaximumRowCount(2);
        photobiolComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EN62471", "MISSING" }));
        photobiolComboBox.setName("photobiolComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), photobiolComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.photobiolTr}"), photobiolComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, photobiolCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), photobiolComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        photobiolComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                photobiolComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(photobiolComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(686, 20, -1, -1));

        ipCheckBox.setBackground(resourceMap.getColor("ipCheckBox.background")); // NOI18N
        ipCheckBox.setFont(resourceMap.getFont("ipCheckBox.font")); // NOI18N
        ipCheckBox.setForeground(resourceMap.getColor("ipCheckBox.foreground")); // NOI18N
        ipCheckBox.setText(resourceMap.getString("ipCheckBox.text")); // NOI18N
        ipCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ipCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        ipCheckBox.setName("ipCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ipclass}"), ipCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, hiddenCheckBox, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), ipCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ipCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipCheckBoxActionPerformed(evt);
            }
        });
        CertPanel.add(ipCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(768, 20, -1, -1));

        ipComboBox.setMaximumRowCount(3);
        ipComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "EN60529", "Inside LVD", "MISSING" }));
        ipComboBox.setName("ipComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), ipComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ipclassTr}"), ipComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, ipCheckBox, org.jdesktop.beansbinding.ELProperty.create("${enabled&&selected}"), ipComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ipComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ipComboBoxActionPerformed(evt);
            }
        });
        CertPanel.add(ipComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(805, 20, -1, -1));

        jTabbedPane1.addTab(resourceMap.getString("CertPanel.TabConstraints.tabTitle"), CertPanel); // NOI18N

        standardPanel.setBackground(resourceMap.getColor("standardPanel.background")); // NOI18N
        standardPanel.setName("standardPanel"); // NOI18N
        standardPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        emcLabel.setForeground(resourceMap.getColor("emcLabel.foreground")); // NOI18N
        emcLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        emcLabel.setText(resourceMap.getString("emcLabel.text")); // NOI18N
        emcLabel.setName("emcLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emcLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        standardPanel.add(emcLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 260, 20));
        emcLabel.getAccessibleContext().setAccessibleParent(standardPanel);

        lvdLabel.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvdLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lvdLabel.setText(resourceMap.getString("lvdLabel.text")); // NOI18N
        lvdLabel.setName("lvdLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvdLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        standardPanel.add(lvdLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 50, 260, 20));
        lvdLabel.getAccessibleContext().setAccessibleParent(standardPanel);

        rfLabel.setForeground(resourceMap.getColor("rf1Field.foreground")); // NOI18N
        rfLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rfLabel.setText(resourceMap.getString("rfLabel.text")); // NOI18N
        rfLabel.setName("rfLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), rfLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        standardPanel.add(rfLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 50, 260, 20));
        rfLabel.getAccessibleContext().setAccessibleParent(standardPanel);

        cpdLabel.setForeground(resourceMap.getColor("cpd1Field.foreground")); // NOI18N
        cpdLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        cpdLabel.setText(resourceMap.getString("cpdLabel.text")); // NOI18N
        cpdLabel.setName("cpdLabel"); // NOI18N
        standardPanel.add(cpdLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 160, 260, 20));
        cpdLabel.getAccessibleContext().setAccessibleParent(standardPanel);

        emc1Field.setName("emc1Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc1}"), emc1Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc1Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc1Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc1Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 260, -1));
        emc1Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd1Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd1Field.setName("lvd1Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd1}"), lvd1Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd1Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd1Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd1Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 70, 260, -1));
        lvd1Field.getAccessibleContext().setAccessibleParent(standardPanel);

        rf1Field.setForeground(resourceMap.getColor("rf1Field.foreground")); // NOI18N
        rf1Field.setName("rf1Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rf1}"), rf1Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), rf1Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rf1Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(rf1Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 70, 260, -1));
        rf1Field.getAccessibleContext().setAccessibleParent(standardPanel);

        cpd1Field.setForeground(resourceMap.getColor("cpd1Field.foreground")); // NOI18N
        cpd1Field.setName("cpd1Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpd1}"), cpd1Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cpd1Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(cpd1Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 180, 260, -1));
        cpd1Field.getAccessibleContext().setAccessibleParent(standardPanel);

        cpd2Field.setForeground(resourceMap.getColor("cpd1Field.foreground")); // NOI18N
        cpd2Field.setName("cpd2Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpd2}"), cpd2Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cpd2Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(cpd2Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 200, 260, -1));
        cpd2Field.getAccessibleContext().setAccessibleParent(standardPanel);

        cpd3Field.setForeground(resourceMap.getColor("cpd1Field.foreground")); // NOI18N
        cpd3Field.setName("cpd3Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpd3}"), cpd3Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cpd3Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(cpd3Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 220, 260, -1));
        cpd3Field.getAccessibleContext().setAccessibleParent(standardPanel);

        cpd4Field.setForeground(resourceMap.getColor("cpd1Field.foreground")); // NOI18N
        cpd4Field.setName("cpd4Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.cpd4}"), cpd4Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);

        cpd4Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(cpd4Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 240, 260, -1));
        cpd4Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc2Field.setName("emc2Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc2}"), emc2Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc2Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc2Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc2Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 260, -1));
        emc2Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd2Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd2Field.setName("lvd2Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd2}"), lvd2Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd2Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd2Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd2Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 90, 260, -1));
        lvd2Field.getAccessibleContext().setAccessibleParent(standardPanel);

        rf2Field.setForeground(resourceMap.getColor("rf1Field.foreground")); // NOI18N
        rf2Field.setName("rf2Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rf2}"), rf2Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), rf2Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rf2Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(rf2Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 260, -1));
        rf2Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc3Field.setName("emc3Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc3}"), emc3Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc3Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc3Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc3Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 260, -1));
        emc3Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd3Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd3Field.setName("lvd3Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd3}"), lvd3Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd3Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd3Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd3Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 110, 260, -1));
        lvd3Field.getAccessibleContext().setAccessibleParent(standardPanel);

        rf3Field.setForeground(resourceMap.getColor("rf1Field.foreground")); // NOI18N
        rf3Field.setName("rf3Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rf3}"), rf3Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), rf3Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rf3Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(rf3Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 110, 260, -1));
        rf3Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc5Field.setName("emc5Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc5}"), emc5Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc5Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc5Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc5Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 260, -1));
        emc5Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd5Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd5Field.setName("lvd5Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd5}"), lvd5Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd5Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd5Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd5Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 150, 260, -1));
        lvd5Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc7Field.setName("emc7Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc7}"), emc7Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc7Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc7Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc7Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 260, -1));
        emc7Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd7Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd7Field.setName("lvd7Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd7}"), lvd7Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd7Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd8Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd7Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 190, 260, -1));
        lvd7Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc9Field.setName("emc9Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc9}"), emc9Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc9Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc9Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc9Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 260, -1));
        emc9Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd9Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd9Field.setName("lvd9Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd9}"), lvd9Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd9Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd9Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd9Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 230, 260, -1));
        lvd9Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc10Field.setName("emc10Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc10}"), emc10Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc10Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc10Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc10Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 260, -1));
        emc10Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc8Field.setName("emc8Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc8}"), emc8Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc8Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc8Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc8Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 260, -1));
        emc8Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd8Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd8Field.setName("lvd8Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd8}"), lvd8Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd8Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd8Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd8Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 210, 260, -1));
        lvd8Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd6Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd6Field.setName("lvd6Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd6}"), lvd6Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd6Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd6Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd6Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 170, 260, -1));
        lvd6Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc6Field.setName("emc6Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc6}"), emc6Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc6Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc6Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc6Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 260, -1));
        emc6Field.getAccessibleContext().setAccessibleParent(standardPanel);

        emc4Field.setName("emc4Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.emc4}"), emc4Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, emcCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), emc4Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        emc4Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(emc4Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 260, -1));
        emc4Field.getAccessibleContext().setAccessibleParent(standardPanel);

        lvd4Field.setForeground(resourceMap.getColor("lvd1Field.foreground")); // NOI18N
        lvd4Field.setName("lvd4Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lvd4}"), lvd4Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, lvdCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), lvd4Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lvd4Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(lvd4Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 130, 260, -1));
        lvd4Field.getAccessibleContext().setAccessibleParent(standardPanel);

        rf4Field.setForeground(resourceMap.getColor("rf1Field.foreground")); // NOI18N
        rf4Field.setName("rf4Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rf4}"), rf4Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), rf4Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        rf4Field.addMouseListener(new ContextMenuMouseListener());
        standardPanel.add(rf4Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 130, 260, -1));
        rf4Field.getAccessibleContext().setAccessibleParent(standardPanel);

        jTabbedPane1.addTab(resourceMap.getString("standardPanel.TabConstraints.tabTitle"), standardPanel); // NOI18N

        specsPanel.setBackground(resourceMap.getColor("specsPanel.background")); // NOI18N
        specsPanel.setName("specsPanel"); // NOI18N
        specsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PowerInfoLabel.setBackground(resourceMap.getColor("PowerInfoLabel.background")); // NOI18N
        PowerInfoLabel.setFont(resourceMap.getFont("PowerInfoLabel.font")); // NOI18N
        PowerInfoLabel.setForeground(resourceMap.getColor("PowerInfoLabel.foreground")); // NOI18N
        PowerInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        PowerInfoLabel.setText(resourceMap.getString("PowerInfoLabel.text")); // NOI18N
        PowerInfoLabel.setName("PowerInfoLabel"); // NOI18N
        PowerInfoLabel.setOpaque(true);
        specsPanel.add(PowerInfoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 0, 880, -1));

        mainsCheckBox.setBackground(resourceMap.getColor("mainsCheckBox.background")); // NOI18N
        mainsCheckBox.setFont(resourceMap.getFont("mainsCheckBox.font")); // NOI18N
        mainsCheckBox.setText(resourceMap.getString("mainsCheckBox.text")); // NOI18N
        mainsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        mainsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        mainsCheckBox.setName("mainsCheckBox"); // NOI18N
        mainsCheckBox.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAINS}"), mainsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), mainsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, -1, 18));

        mainsInVoltLabel.setFont(resourceMap.getFont("mainsInVoltLabel.font")); // NOI18N
        mainsInVoltLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainsInVoltLabel.setText(resourceMap.getString("mainsInVoltLabel.text")); // NOI18N
        mainsInVoltLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsInVoltLabel.setName("mainsInVoltLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsInVoltLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsInVoltLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 120, 10));

        mainsInVoltComboBox.setMaximumRowCount(13);
        mainsInVoltComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "85-240V~ 50/60Hz", "100-240V~ 50/60Hz", "AC 220-240V", "220-240V~ 50Hz", "220-240V~ 50/60Hz", "230V~ 50Hz", "230V~ 50/60Hz", "12VAC/DC", "12VDC/AC 50Hz", "12VDC/AC 50/60Hz", "12VDC", "24VDC" }));
        mainsInVoltComboBox.setName("mainsInVoltComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), mainsInVoltComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAINS_IN_VOLT}"), mainsInVoltComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsInVoltComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsInVoltComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 30, -1, 15));

        mainsInWattLabel.setFont(resourceMap.getFont("mainsInWattLabel.font")); // NOI18N
        mainsInWattLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainsInWattLabel.setText(resourceMap.getString("mainsInWattLabel.text")); // NOI18N
        mainsInWattLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsInWattLabel.setName("mainsInWattLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsInWattLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsInWattLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, 82, 10));

        mainsInWattTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mainsInWattTextField.setName("mainsInWattTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAINS_IN_WATT}"), mainsInWattTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsInWattTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsInWattTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 30, 40, 15));

        mainsInPlugLabel.setFont(resourceMap.getFont("mainsInWattLabel.font")); // NOI18N
        mainsInPlugLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainsInPlugLabel.setText(resourceMap.getString("mainsInPlugLabel.text")); // NOI18N
        mainsInPlugLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsInPlugLabel.setName("mainsInPlugLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsInPlugLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsInPlugLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 20, 50, 10));

        mainsInPlugTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mainsInPlugTextField.setName("mainsInPlugTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAINS_IN_PLUG}"), mainsInPlugTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsInPlugTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsInPlugTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 30, 50, 15));

        mainsIPLabel.setFont(resourceMap.getFont("mainsInWattLabel.font")); // NOI18N
        mainsIPLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainsIPLabel.setText(resourceMap.getString("mainsIPLabel.text")); // NOI18N
        mainsIPLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsIPLabel.setName("mainsIPLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsIPLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsIPLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, 50, 10));

        mainsIPTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mainsIPTextField.setName("mainsIPTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IP_RATE}"), mainsIPTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsIPTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsIPTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 30, 30, 15));

        mainsClassLabel.setFont(resourceMap.getFont("mainsInWattLabel.font")); // NOI18N
        mainsClassLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainsClassLabel.setText(resourceMap.getString("mainsClassLabel.text")); // NOI18N
        mainsClassLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsClassLabel.setName("mainsClassLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsClassLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsClassLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 60, 10));

        mainsClassComboBox.setMaximumRowCount(4);
        mainsClassComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "I", "II", "III" }));
        mainsClassComboBox.setName("mainsClassComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), mainsClassComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAINS_CLASS}"), mainsClassComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsClassComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsClassComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 30, -1, 15));

        mainsOutWattLabel.setFont(resourceMap.getFont("mainsInWattLabel.font")); // NOI18N
        mainsOutWattLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainsOutWattLabel.setText(resourceMap.getString("mainsOutWattLabel.text")); // NOI18N
        mainsOutWattLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsOutWattLabel.setName("mainsOutWattLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsOutWattLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsOutWattLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 20, 140, 10));

        mainsOutWattTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mainsOutWattTextField.setName("mainsOutWattTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAINS_OUT_WATT}"), mainsOutWattTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsOutWattTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsOutWattTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 30, 40, 15));

        mainsOutPlugLabel.setFont(resourceMap.getFont("mainsInWattLabel.font")); // NOI18N
        mainsOutPlugLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainsOutPlugLabel.setText(resourceMap.getString("mainsOutPlugLabel.text")); // NOI18N
        mainsOutPlugLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        mainsOutPlugLabel.setName("mainsOutPlugLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsOutPlugLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsOutPlugLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 20, 70, 10));

        mainsOutPlugTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mainsOutPlugTextField.setName("mainsOutPlugTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAINS_OUT_PLUG}"), mainsOutPlugTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mainsCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), mainsOutPlugTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(mainsOutPlugTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 30, 50, 15));

        mainsSeparator.setName("mainsSeparator"); // NOI18N
        specsPanel.add(mainsSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 870, 5));

        adaptorLabel.setFont(resourceMap.getFont("adaptorLabel.font")); // NOI18N
        adaptorLabel.setText(resourceMap.getString("adaptorLabel.text")); // NOI18N
        adaptorLabel.setName("adaptorLabel"); // NOI18N
        specsPanel.add(adaptorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(4, 70, 60, -1));

        adaptorCheckBox1.setBackground(resourceMap.getColor("adaptorCheckBox1.background")); // NOI18N
        adaptorCheckBox1.setFont(resourceMap.getFont("mainsCheckBox.font")); // NOI18N
        adaptorCheckBox1.setText(resourceMap.getString("adaptorCheckBox1.text")); // NOI18N
        adaptorCheckBox1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        adaptorCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        adaptorCheckBox1.setName("adaptorCheckBox1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ADAPTOR1}"), adaptorCheckBox1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), adaptorCheckBox1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        adaptorCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adaptorCheckBox1ActionPerformed(evt);
            }
        });
        specsPanel.add(adaptorCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 65, 32, 15));

        adaptorCheckBox2.setBackground(resourceMap.getColor("adaptorCheckBox2.background")); // NOI18N
        adaptorCheckBox2.setFont(resourceMap.getFont("mainsCheckBox.font")); // NOI18N
        adaptorCheckBox2.setText(resourceMap.getString("adaptorCheckBox2.text")); // NOI18N
        adaptorCheckBox2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        adaptorCheckBox2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        adaptorCheckBox2.setName("adaptorCheckBox2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ADAPTOR2}"), adaptorCheckBox2, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorCheckBox2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        adaptorCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adaptorCheckBox2ActionPerformed(evt);
            }
        });
        specsPanel.add(adaptorCheckBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(75, 80, 32, 15));

        adaptorTypeLabel.setFont(resourceMap.getFont("adaptorTypeLabel.font")); // NOI18N
        adaptorTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorTypeLabel.setLabelFor(adaptorInAmp1TextField);
        adaptorTypeLabel.setText(resourceMap.getString("adaptorTypeLabel.text")); // NOI18N
        adaptorTypeLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorTypeLabel.setName("adaptorTypeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorTypeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorTypeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 55, 55, 10));

        adaptorType1ComboBox.setMaximumRowCount(3);
        adaptorType1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Plug-in", "Cable" }));
        adaptorType1ComboBox.setName("adaptorType1ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), adaptorType1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ADAPTOR_TYPE1}"), adaptorType1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorType1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorType1ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 65, -1, 15));

        adaptorType2ComboBox.setMaximumRowCount(3);
        adaptorType2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Plug-in", "Cable" }));
        adaptorType2ComboBox.setName("adaptorType2ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), adaptorType2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ADAPTOR_TYPE2}"), adaptorType2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorType2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorType2ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 80, -1, 15));

        adaptorInVoltLabel.setFont(resourceMap.getFont("adaptorClassLabel.font")); // NOI18N
        adaptorInVoltLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorInVoltLabel.setLabelFor(adaptorInAmp1TextField);
        adaptorInVoltLabel.setText(resourceMap.getString("adaptorInVoltLabel.text")); // NOI18N
        adaptorInVoltLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorInVoltLabel.setName("adaptorInVoltLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInVoltLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInVoltLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 55, 122, 10));

        adaptorInVolt1ComboBox.setMaximumRowCount(4);
        adaptorInVolt1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "100-240V~ 50/60Hz", "220-240V~ 50Hz", "220-240V~ 50/60Hz" }));
        adaptorInVolt1ComboBox.setName("adaptorInVolt1ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), adaptorInVolt1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IN_VOLT1}"), adaptorInVolt1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInVolt1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInVolt1ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 65, -1, 15));

        adaptorInVolt2ComboBox.setMaximumRowCount(4);
        adaptorInVolt2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "100-240V~ 50/60Hz", "220-240V~ 50Hz", "220-240V~ 50/60Hz" }));
        adaptorInVolt2ComboBox.setName("adaptorInVolt2ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), adaptorInVolt2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IN_VOLT2}"), adaptorInVolt2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInVolt2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInVolt2ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, -1, 15));

        adaptorInAmpLabel.setFont(resourceMap.getFont("adaptorClassLabel.font")); // NOI18N
        adaptorInAmpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorInAmpLabel.setLabelFor(adaptorInAmp2TextField);
        adaptorInAmpLabel.setText(resourceMap.getString("adaptorInAmpLabel.text")); // NOI18N
        adaptorInAmpLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorInAmpLabel.setName("adaptorInAmpLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInAmpLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInAmpLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 55, 30, 10));

        adaptorInAmp1TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        adaptorInAmp1TextField.setName("adaptorInAmp1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IN_AMP1}"), adaptorInAmp1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInAmp1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInAmp1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 65, 30, 15));

        adaptorInAmp2TextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        adaptorInAmp2TextField.setName("adaptorInAmp2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IN_AMP2}"), adaptorInAmp2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInAmp2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInAmp2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 80, 30, 15));

        adaptorInPlugLabel.setFont(resourceMap.getFont("adaptorClassLabel.font")); // NOI18N
        adaptorInPlugLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorInPlugLabel.setLabelFor(adaptorInAmp1TextField);
        adaptorInPlugLabel.setText(resourceMap.getString("adaptorInPlugLabel.text")); // NOI18N
        adaptorInPlugLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorInPlugLabel.setName("adaptorInPlugLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInPlugLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInPlugLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 55, 50, 10));

        adaptorInPlug1TextField.setName("adaptorInPlug1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IN_WATT1}"), adaptorInPlug1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInPlug1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInPlug1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 65, 50, 15));

        adaptorInPlug2TextField.setName("adaptorInPlug2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IN_WATT2}"), adaptorInPlug2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorInPlug2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorInPlug2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 50, 15));

        adaptorOutVoltLabel.setFont(resourceMap.getFont("adaptorClassLabel.font")); // NOI18N
        adaptorOutVoltLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorOutVoltLabel.setLabelFor(adaptorInAmp1TextField);
        adaptorOutVoltLabel.setText(resourceMap.getString("adaptorOutVoltLabel.text")); // NOI18N
        adaptorOutVoltLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorOutVoltLabel.setName("adaptorOutVoltLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutVoltLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutVoltLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 55, 90, 10));

        adaptorOutVolt1TextField.setName("adaptorOutVolt1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.OUT_VOLT1}"), adaptorOutVolt1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutVolt1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutVolt1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 65, 40, 15));

        adaptorOutVolt2TextField.setName("adaptorOutVolt2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.OUT_VOLT2}"), adaptorOutVolt2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutVolt2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutVolt2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 80, 40, 15));

        adaptorOutAmpLabel.setFont(resourceMap.getFont("adaptorClassLabel.font")); // NOI18N
        adaptorOutAmpLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorOutAmpLabel.setLabelFor(adaptorInAmp2TextField);
        adaptorOutAmpLabel.setText(resourceMap.getString("adaptorOutAmpLabel.text")); // NOI18N
        adaptorOutAmpLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorOutAmpLabel.setName("adaptorOutAmpLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutAmpLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutAmpLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 55, 60, 10));

        adaptorOutAmp1TextField.setName("adaptorOutAmp1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.OUT_AMP1}"), adaptorOutAmp1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutAmp1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutAmp1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 65, 50, 15));

        adaptorOutAmp2TextField.setName("adaptorOutAmp2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.OUT_AMP2}"), adaptorOutAmp2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutAmp2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutAmp2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 80, 50, 15));

        adaptorOutPlugLabel.setFont(resourceMap.getFont("adaptorClassLabel.font")); // NOI18N
        adaptorOutPlugLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorOutPlugLabel.setLabelFor(adaptorInAmp1TextField);
        adaptorOutPlugLabel.setText(resourceMap.getString("adaptorOutPlugLabel.text")); // NOI18N
        adaptorOutPlugLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorOutPlugLabel.setName("adaptorOutPlugLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutPlugLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutPlugLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 55, 50, 10));

        adaptorOutPlug1TextField1.setName("adaptorOutPlug1TextField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.OUT_WATT1}"), adaptorOutPlug1TextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorOutPlug1TextField1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorOutPlug1TextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 65, 50, 15));

        outputAmp2TextField1.setName("outputAmp2TextField1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.OUT_WATT2}"), outputAmp2TextField1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), outputAmp2TextField1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(outputAmp2TextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 80, 50, 15));

        adaptorClassLabel.setFont(resourceMap.getFont("adaptorClassLabel.font")); // NOI18N
        adaptorClassLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        adaptorClassLabel.setLabelFor(adaptorClass2ComboBox);
        adaptorClassLabel.setText(resourceMap.getString("adaptorClassLabel.text")); // NOI18N
        adaptorClassLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        adaptorClassLabel.setName("adaptorClassLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorClassLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorClassLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 55, 60, 10));

        adaptorClass1ComboBox.setMaximumRowCount(4);
        adaptorClass1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "I", "II", "III" }));
        adaptorClass1ComboBox.setName("adaptorClass1ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), adaptorClass1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.APPLIANCE_CLASS1}"), adaptorClass1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorClass1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorClass1ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 65, -1, 15));

        adaptorClass2ComboBox.setMaximumRowCount(4);
        adaptorClass2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "I", "II", "III" }));
        adaptorClass2ComboBox.setName("adaptorClass2ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), adaptorClass2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.APPLIANCE_CLASS2}"), adaptorClass2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorClass2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(adaptorClass2ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 80, -1, 15));

        adaptorClass1Logo.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        adaptorClass1Logo.setLabelFor(adaptorClass1ComboBox);
        adaptorClass1Logo.setText(resourceMap.getString("adaptorClass1Logo.text")); // NOI18N
        adaptorClass1Logo.setName("adaptorClass1Logo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox1, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorClass1Logo, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        adaptorClass1Logo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                adaptorClass1LogoPropertyChange(evt);
            }
        });
        specsPanel.add(adaptorClass1Logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 65, 50, 15));

        adaptorClass2Logo.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        adaptorClass2Logo.setLabelFor(adaptorClass1ComboBox);
        adaptorClass2Logo.setName("adaptorClass2Logo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, adaptorCheckBox2, org.jdesktop.beansbinding.ELProperty.create("${selected}"), adaptorClass2Logo, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        adaptorClass2Logo.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                adaptorClass2LogoPropertyChange(evt);
            }
        });
        specsPanel.add(adaptorClass2Logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 80, 50, 15));

        adaptorSeparator.setName("adaptorSeparator"); // NOI18N
        specsPanel.add(adaptorSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 870, 5));

        batt1Label.setFont(resourceMap.getFont("batt1Label.font")); // NOI18N
        batt1Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        batt1Label.setLabelFor(batt1CheckBox);
        batt1Label.setText(resourceMap.getString("batt1Label.text")); // NOI18N
        batt1Label.setName("batt1Label"); // NOI18N
        specsPanel.add(batt1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 70, 20));

        batt1InclLabel.setFont(resourceMap.getFont("batt1InclLabel.font")); // NOI18N
        batt1InclLabel.setText(resourceMap.getString("batt1InclLabel.text")); // NOI18N
        batt1InclLabel.setName("batt1InclLabel"); // NOI18N
        specsPanel.add(batt1InclLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 115, -1, -1));

        batt1CheckBox.setBackground(resourceMap.getColor("batt1CheckBox.background")); // NOI18N
        batt1CheckBox.setText(resourceMap.getString("batt1CheckBox.text")); // NOI18N
        batt1CheckBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        batt1CheckBox.setDoubleBuffered(true);
        batt1CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        batt1CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        batt1CheckBox.setName("batt1CheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1TrCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), batt1CheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), batt1CheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(batt1CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 115, 35, 15));

        batt2InclLabel.setFont(resourceMap.getFont("batt2InclLabel.font")); // NOI18N
        batt2InclLabel.setText(resourceMap.getString("batt2InclLabel.text")); // NOI18N
        batt2InclLabel.setName("batt2InclLabel"); // NOI18N
        specsPanel.add(batt2InclLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, -1, -1));

        batt2CheckBox.setBackground(resourceMap.getColor("batt2CheckBox.background")); // NOI18N
        batt2CheckBox.setText(resourceMap.getString("batt2CheckBox.text")); // NOI18N
        batt2CheckBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        batt2CheckBox.setDoubleBuffered(true);
        batt2CheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        batt2CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        batt2CheckBox.setName("batt2CheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2TrCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), batt2CheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), batt2CheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(batt2CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 130, 35, 15));

        battQuaLabel.setFont(resourceMap.getFont("battQuaLabel.font")); // NOI18N
        battQuaLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        battQuaLabel.setLabelFor(battQua1TextField);
        battQuaLabel.setText(resourceMap.getString("battQuaLabel.text")); // NOI18N
        battQuaLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        battQuaLabel.setName("battQuaLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battQuaLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battQuaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 105, 22, 10));

        battQua1TextField.setName("battQua1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_QUA1}"), battQua1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battQua1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battQua1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 115, 20, 15));

        battQua2TextField.setName("battQua2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_QUA2}"), battQua2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battQua2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battQua2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 130, 20, 15));

        battBrandLabel.setFont(resourceMap.getFont("battQuaLabel.font")); // NOI18N
        battBrandLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        battBrandLabel.setLabelFor(battQua1TextField);
        battBrandLabel.setText(resourceMap.getString("battBrandLabel.text")); // NOI18N
        battBrandLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        battBrandLabel.setName("battBrandLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battBrandLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battBrandLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 105, 80, 10));

        battBrand1TextField.setName("battBrand1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_BRAND1}"), battBrand1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battBrand1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battBrand1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 115, 80, 15));

        battBrand2TextField.setName("battBrand2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_BRAND2}"), battBrand2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battBrand2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battBrand2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 80, 15));

        battTypeLabel.setFont(resourceMap.getFont("battQuaLabel.font")); // NOI18N
        battTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        battTypeLabel.setLabelFor(battType1ComboBox);
        battTypeLabel.setText(resourceMap.getString("battTypeLabel.text")); // NOI18N
        battTypeLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        battTypeLabel.setName("battTypeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battTypeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battTypeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 105, 80, 10));

        battType1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Alkaline", "Lead-acid", "Lithium", "Lithium-ion", "NiCd", "NiMH", "Zinc-carbon" }));
        battType1ComboBox.setName("battType1ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), battType1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_TYPE1}"), battType1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battType1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battType1ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 115, -1, 15));

        battType2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Alkaline", "Lead-acid", "Lithium", "Lithium-ion", "NiCd", "NiMH", "Zinc-carbon" }));
        battType2ComboBox.setName("battType2ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), battType2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_TYPE2}"), battType2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battType2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battType2ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, 15));

        battSizeLabel.setFont(resourceMap.getFont("battQuaLabel.font")); // NOI18N
        battSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        battSizeLabel.setLabelFor(battSize1ComboBox);
        battSizeLabel.setText(resourceMap.getString("battSizeLabel.text")); // NOI18N
        battSizeLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        battSizeLabel.setName("battSizeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battSizeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battSizeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 105, 70, 10));

        battSize1ComboBox.setMaximumRowCount(15);
        battSize1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "AA", "AAA", "C", "D", "9-volt", "A23", "CR123A", "CR2", "CR2032", "CR2025", "CR2450", "CR2477", "LR44", "accupack" }));
        battSize1ComboBox.setName("battSize1ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), battSize1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_SIZE1}"), battSize1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battSize1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        battSize1ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                battSize1ComboBoxActionPerformed(evt);
            }
        });
        specsPanel.add(battSize1ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 115, -1, 15));

        battSize2ComboBox.setMaximumRowCount(14);
        battSize2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "AA", "AAA", "C", "D", "9-volt", "A23", "CR123A", "CR2", "CR2032", "CR2025", "CR2450", "CR2477", "accupack" }));
        battSize2ComboBox.setName("battSize2ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), battSize2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_SIZE2}"), battSize2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battSize2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        battSize2ComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                battSize2ComboBoxActionPerformed(evt);
            }
        });
        specsPanel.add(battSize2ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 130, -1, 15));

        battVoltLabel.setFont(resourceMap.getFont("battQuaLabel.font")); // NOI18N
        battVoltLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        battVoltLabel.setLabelFor(battVolt1TextField);
        battVoltLabel.setText(resourceMap.getString("battVoltLabel.text")); // NOI18N
        battVoltLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        battVoltLabel.setName("battVoltLabel"); // NOI18N
        battVoltLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battVoltLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battVoltLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 105, -1, 10));

        battVolt1TextField.setName("battVolt1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_VOLT1}"), battVolt1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battVolt1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battVolt1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 115, 30, 15));

        battVolt2TextField.setName("battVolt2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_VOLT2}"), battVolt2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battVolt2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battVolt2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 130, 30, 15));

        battAccu1CheckBox.setBackground(resourceMap.getColor("battAccu1CheckBox.background")); // NOI18N
        battAccu1CheckBox.setText(resourceMap.getString("battAccu1CheckBox.text")); // NOI18N
        battAccu1CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        battAccu1CheckBox.setName("battAccu1CheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_ACCU1}"), battAccu1CheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battAccu1CheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battAccu1CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 115, -1, 15));

        battAccu2CheckBox.setBackground(resourceMap.getColor("battAccu2CheckBox.background")); // NOI18N
        battAccu2CheckBox.setText(resourceMap.getString("battAccu2CheckBox.text")); // NOI18N
        battAccu2CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        battAccu2CheckBox.setName("battAccu2CheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_ACCU2}"), battAccu2CheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battAccu2CheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battAccu2CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 130, -1, 15));

        battCapLabel.setFont(resourceMap.getFont("battQuaLabel.font")); // NOI18N
        battCapLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        battCapLabel.setText(resourceMap.getString("battCapLabel.text")); // NOI18N
        battCapLabel.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        battCapLabel.setName("battCapLabel"); // NOI18N
        battCapLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battCapLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battCapLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 105, 80, 10));

        battCap1TextField.setName("battCap1TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_CAP1}"), battCap1TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, battAccu1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battCap1TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battCap1TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 115, 50, 15));

        battCap2TextField.setName("battCap2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_CAP2}"), battCap2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, battAccu2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battCap2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battCap2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 130, 50, 15));

        battRepl1CheckBox.setBackground(resourceMap.getColor("battRepl1CheckBox.background")); // NOI18N
        battRepl1CheckBox.setText(resourceMap.getString("battRepl1CheckBox.text")); // NOI18N
        battRepl1CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        battRepl1CheckBox.setName("battRepl1CheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_REPL1}"), battRepl1CheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt1CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battRepl1CheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battRepl1CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 115, -1, 15));

        battRepl2CheckBox.setBackground(resourceMap.getColor("battRepl2CheckBox.background")); // NOI18N
        battRepl2CheckBox.setText(resourceMap.getString("battRepl2CheckBox.text")); // NOI18N
        battRepl2CheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        battRepl2CheckBox.setName("battRepl2CheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BATT_REPL2}"), battRepl2CheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, batt2CheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), battRepl2CheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(battRepl2CheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 130, -1, 15));

        lightInfoLabel.setBackground(resourceMap.getColor("lightInfoLabel.background")); // NOI18N
        lightInfoLabel.setFont(resourceMap.getFont("PowerInfoLabel.font")); // NOI18N
        lightInfoLabel.setForeground(resourceMap.getColor("lightInfoLabel.foreground")); // NOI18N
        lightInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lightInfoLabel.setText(resourceMap.getString("lightInfoLabel.text")); // NOI18N
        lightInfoLabel.setName("lightInfoLabel"); // NOI18N
        lightInfoLabel.setOpaque(true);
        specsPanel.add(lightInfoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 300, -1));

        lightWatt1Label.setLabelFor(lightWatt1Field);
        lightWatt1Label.setText(resourceMap.getString("lightWatt1Label.text")); // NOI18N
        lightWatt1Label.setName("lightWatt1Label"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightWatt1Label, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightWatt1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, -1, -1));

        lightWatt1Field.setName("lightWatt1Field"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAX_WATT1}"), lightWatt1Field, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightWatt1Field, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightWatt1Field, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 180, 40, 15));

        lightBulb1Label.setLabelFor(lightBulb1ComboBox);
        lightBulb1Label.setText(resourceMap.getString("lightBulb1Label.text")); // NOI18N
        lightBulb1Label.setName("lightBulb1Label"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightBulb1Label, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightBulb1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 180, -1, -1));

        lightBulb1ComboBox.setMaximumRowCount(4);
        lightBulb1ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "LED", "CFL", "HAL", "NOR" }));
        lightBulb1ComboBox.setName("lightBulb1ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), lightBulb1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.TYPE_BULB1}"), lightBulb1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightBulb1ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightBulb1ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 180, -1, 15));

        lightWatt2Label.setLabelFor(lightWatt2TextField);
        lightWatt2Label.setText(resourceMap.getString("lightWatt2Label.text")); // NOI18N
        lightWatt2Label.setName("lightWatt2Label"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightWatt2Label, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightWatt2Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, -1, -1));

        lightWatt2TextField.setName("lightWatt2TextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.MAX_WATT2}"), lightWatt2TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightWatt2TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightWatt2TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 200, 40, 15));

        lightBulb2Label.setLabelFor(lightBulb2ComboBox);
        lightBulb2Label.setText(resourceMap.getString("lightBulb2Label.text")); // NOI18N
        lightBulb2Label.setName("lightBulb2Label"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightBulb2Label, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightBulb2Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 200, -1, -1));

        lightBulb2ComboBox.setMaximumRowCount(4);
        lightBulb2ComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "LED", "CFL", "HAL", "NOR" }));
        lightBulb2ComboBox.setName("lightBulb2ComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), lightBulb2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.TYPE_BULB2}"), lightBulb2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightBulb2ComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightBulb2ComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, -1, 15));

        lightFixtureLabel.setLabelFor(lightFixtureComboBox);
        lightFixtureLabel.setText(resourceMap.getString("lightFixtureLabel.text")); // NOI18N
        lightFixtureLabel.setName("lightFixtureLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightFixtureLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightFixtureLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 70, -1));

        lightFixtureComboBox.setMaximumRowCount(10);
        lightFixtureComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Indoor ceiling", "Indoor hand", "Indoor pendant", "Indoor stand", "Indoor table", "Indoor wall", "Outdoor wall", "Outdoor hand", "Outdoor stand" }));
        lightFixtureComboBox.setName("lightFixtureComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), lightFixtureComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.FIXTURE_TYPE}"), lightFixtureComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightFixtureComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightFixtureComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 220, -1, 15));

        lightSizeLabel.setLabelFor(lightSizeTextField);
        lightSizeLabel.setText(resourceMap.getString("lightSizeLabel.text")); // NOI18N
        lightSizeLabel.setName("lightSizeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightSizeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightSizeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 240, -1, -1));

        lightSizeTextField.setName("lightSizeTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.FIXTURE_SIZE}"), lightSizeTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightSizeTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightSizeTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, 130, 15));

        lightClassLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lightClassLabel.setLabelFor(lightClassTextField);
        lightClassLabel.setText(resourceMap.getString("lightClassLabel.text")); // NOI18N
        lightClassLabel.setName("lightClassLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightClassLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightClassLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 220, -1, -1));

        lightClassTextField.setName("lightClassTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BULB_CLASS}"), lightClassTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), lightClassTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(lightClassTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 220, 50, 15));

        lightSeparator.setForeground(resourceMap.getColor("lightSeparator.foreground")); // NOI18N
        lightSeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        lightSeparator.setName("lightSeparator"); // NOI18N
        specsPanel.add(lightSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(323, 160, -1, 100));

        safetyInfoLabel.setBackground(resourceMap.getColor("safetyInfoLabel.background")); // NOI18N
        safetyInfoLabel.setFont(resourceMap.getFont("safetyInfoLabel.font")); // NOI18N
        safetyInfoLabel.setForeground(resourceMap.getColor("safetyInfoLabel.foreground")); // NOI18N
        safetyInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        safetyInfoLabel.setText(resourceMap.getString("safetyInfoLabel.text")); // NOI18N
        safetyInfoLabel.setName("safetyInfoLabel"); // NOI18N
        safetyInfoLabel.setOpaque(true);
        specsPanel.add(safetyInfoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 160, 310, -1));

        safetyCameraLabel.setFont(resourceMap.getFont("safetyCameraLabel.font")); // NOI18N
        safetyCameraLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        safetyCameraLabel.setLabelFor(batt1CheckBox);
        safetyCameraLabel.setText(resourceMap.getString("safetyCameraLabel.text")); // NOI18N
        safetyCameraLabel.setName("safetyCameraLabel"); // NOI18N
        specsPanel.add(safetyCameraLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 180, 50, 12));

        safetySensorLabel.setLabelFor(lightWatt1Field);
        safetySensorLabel.setText(resourceMap.getString("safetySensorLabel.text")); // NOI18N
        safetySensorLabel.setName("safetySensorLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetySensorLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetySensorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 180, -1, 12));

        safetySensorComboBox.setMaximumRowCount(3);
        safetySensorComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "CCD", "CMOS" }));
        safetySensorComboBox.setName("safetySensorComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), safetySensorComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.SENSOR_TYPE}"), safetySensorComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetySensorComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetySensorComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 180, 60, 15));

        safetySizeLabel.setLabelFor(lightWatt1Field);
        safetySizeLabel.setText(resourceMap.getString("safetySizeLabel.text")); // NOI18N
        safetySizeLabel.setName("safetySizeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetySizeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetySizeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 180, -1, 12));

        safetySizeComboBox.setMaximumRowCount(4);
        safetySizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1/2''", "1/3''", "1/4''" }));
        safetySizeComboBox.setName("safetySizeComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), safetySizeComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.SENSOR_SIZE}"), safetySizeComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetySizeComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetySizeComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 180, 50, 15));

        safetyPixelLabel.setLabelFor(lightWatt1Field);
        safetyPixelLabel.setText(resourceMap.getString("safetyPixelLabel.text")); // NOI18N
        safetyPixelLabel.setName("safetyPixelLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyPixelLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyPixelLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 200, -1, 12));

        safetyPixelTextField.setName("safetyPixelTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.PIXELS}"), safetyPixelTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyPixelTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyPixelTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 200, 60, 15));

        safetyIRLabel.setLabelFor(lightWatt1Field);
        safetyIRLabel.setText(resourceMap.getString("safetyIRLabel.text")); // NOI18N
        safetyIRLabel.setName("safetyIRLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyIRLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyIRLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 200, -1, 12));

        safetyIRTextField.setName("safetyIRTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.IRLED}"), safetyIRTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyIRTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyIRTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 200, 30, 15));

        safetyMonitorLabel.setFont(resourceMap.getFont("safetyMonitorLabel.font")); // NOI18N
        safetyMonitorLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        safetyMonitorLabel.setLabelFor(batt1CheckBox);
        safetyMonitorLabel.setText(resourceMap.getString("safetyMonitorLabel.text")); // NOI18N
        safetyMonitorLabel.setName("safetyMonitorLabel"); // NOI18N
        specsPanel.add(safetyMonitorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 220, -1, 12));

        safetyScreenLabel.setLabelFor(lightWatt1Field);
        safetyScreenLabel.setText(resourceMap.getString("safetyScreenLabel.text")); // NOI18N
        safetyScreenLabel.setName("safetyScreenLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyScreenLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyScreenLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 220, -1, 12));

        safetyScreenComboBox.setMaximumRowCount(9);
        safetyScreenComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "10\"", "11\"", "12\"", "13\"", "14\"", "15\"", "16\"", "17\"" }));
        safetyScreenComboBox.setName("safetyScreenComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), safetyScreenComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.SCREEN_SIZE}"), safetyScreenComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyScreenComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyScreenComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 220, 50, 15));

        safetyLinesLabel.setLabelFor(lightWatt1Field);
        safetyLinesLabel.setText(resourceMap.getString("safetyLinesLabel.text")); // NOI18N
        safetyLinesLabel.setName("safetyLinesLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyLinesLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyLinesLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 220, -1, 12));

        safetyLinesTextField.setName("safetyLinesTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.TV_LINES}"), safetyLinesTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), safetyLinesTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(safetyLinesTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 220, 60, 15));

        safetySeparator.setForeground(resourceMap.getColor("safetySeparator.foreground")); // NOI18N
        safetySeparator.setOrientation(javax.swing.SwingConstants.VERTICAL);
        safetySeparator.setName("safetySeparator"); // NOI18N
        specsPanel.add(safetySeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 160, -1, 70));

        doorchimesInfoLabel.setBackground(resourceMap.getColor("doorchimesInfoLabel.background")); // NOI18N
        doorchimesInfoLabel.setFont(resourceMap.getFont("doorchimesInfoLabel.font")); // NOI18N
        doorchimesInfoLabel.setForeground(resourceMap.getColor("doorchimesInfoLabel.foreground")); // NOI18N
        doorchimesInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        doorchimesInfoLabel.setText(resourceMap.getString("doorchimesInfoLabel.text")); // NOI18N
        doorchimesInfoLabel.setName("doorchimesInfoLabel"); // NOI18N
        doorchimesInfoLabel.setOpaque(true);
        specsPanel.add(doorchimesInfoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 160, 180, -1));

        doorchimesSoundLabel.setLabelFor(lightWatt1Field);
        doorchimesSoundLabel.setText(resourceMap.getString("doorchimesSoundLabel.text")); // NOI18N
        doorchimesSoundLabel.setName("doorchimesSoundLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), doorchimesSoundLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(doorchimesSoundLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 180, -1, 12));

        doorchimesTempLabel.setLabelFor(lightWatt1Field);
        doorchimesTempLabel.setText(resourceMap.getString("doorchimesTempLabel.text")); // NOI18N
        doorchimesTempLabel.setName("doorchimesTempLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), doorchimesTempLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(doorchimesTempLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 200, -1, 12));

        doorchimesTempComboBox.setMaximumRowCount(6);
        doorchimesTempComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1", "2", "3", "4", "5" }));
        doorchimesTempComboBox.setName("doorchimesTempComboBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), doorchimesTempComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BELL_TEMP}"), doorchimesTempComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), doorchimesTempComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(doorchimesTempComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 200, 100, 15));

        RFfLabel.setForeground(resourceMap.getColor("rfNbNLabel.foreground")); // NOI18N
        RFfLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        RFfLabel.setText(resourceMap.getString("RFfLabel.text")); // NOI18N
        RFfLabel.setName("RFfLabel"); // NOI18N
        RFfLabel.setPreferredSize(new java.awt.Dimension(40, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), RFfLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(RFfLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 240, 60, -1));

        RFfField.setForeground(resourceMap.getColor("rfNbNLabel.foreground")); // NOI18N
        RFfField.setName("RFfField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.rfF}"), RFfField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, rfCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), RFfField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(RFfField, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 240, 88, -1));

        logoLabel.setBackground(resourceMap.getColor("logoLabel.background")); // NOI18N
        logoLabel.setFont(resourceMap.getFont("logoLabel.font")); // NOI18N
        logoLabel.setForeground(resourceMap.getColor("logoLabel.foreground")); // NOI18N
        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        logoLabel.setText(resourceMap.getString("logoLabel.text")); // NOI18N
        logoLabel.setName("logoLabel"); // NOI18N
        logoLabel.setOpaque(true);
        specsPanel.add(logoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 270, 880, -1));

        logoCECheckBox.setBackground(resourceMap.getColor("logoCECheckBox.background")); // NOI18N
        logoCECheckBox.setText(resourceMap.getString("logoCECheckBox.text")); // NOI18N
        logoCECheckBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        logoCECheckBox.setDoubleBuffered(true);
        logoCECheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoCECheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logoCECheckBox.setName("logoCECheckBox"); // NOI18N
        logoCECheckBox.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        logoCECheckBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.CE}"), logoCECheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), logoCECheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(logoCECheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 290, 21, 33));

        logoWeeeCheckBox.setBackground(resourceMap.getColor("logoWeeeCheckBox.background")); // NOI18N
        logoWeeeCheckBox.setText(resourceMap.getString("logoWeeeCheckBox.text")); // NOI18N
        logoWeeeCheckBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        logoWeeeCheckBox.setDoubleBuffered(true);
        logoWeeeCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoWeeeCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logoWeeeCheckBox.setName("logoWeeeCheckBox"); // NOI18N
        logoWeeeCheckBox.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        logoWeeeCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.WEEE}"), logoWeeeCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), logoWeeeCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(logoWeeeCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 290, 36, 33));

        logoNBNLabel.setFont(resourceMap.getFont("logoNBNLabel.font")); // NOI18N
        logoNBNLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoNBNLabel.setLabelFor(lightSizeTextField);
        logoNBNLabel.setName("logoNBNLabel"); // NOI18N
        logoNBNLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, RFNBNField, org.jdesktop.beansbinding.ELProperty.create("${text}"), logoNBNLabel, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), logoNBNLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(logoNBNLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 290, 60, 30));

        jTextField2.setName("jTextField2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.BELL_SOUND}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), jTextField2, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        specsPanel.add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 180, 30, 15));

        jTabbedPane1.addTab(resourceMap.getString("specsPanel.TabConstraints.tabTitle"), specsPanel); // NOI18N

        erpPanel.setBackground(resourceMap.getColor("erpPanel.background")); // NOI18N
        erpPanel.setMinimumSize(new java.awt.Dimension(520, 280));
        erpPanel.setName("erpPanel"); // NOI18N
        erpPanel.setPreferredSize(new java.awt.Dimension(520, 280));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), erpPanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        kindBulbLabel.setText(resourceMap.getString("kindBulbLabel.text")); // NOI18N
        kindBulbLabel.setName("kindBulbLabel"); // NOI18N
        erpPanel.add(kindBulbLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 70, -1));

        kindBulbComboBox.setMaximumRowCount(5);
        kindBulbComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "LED", "CFL", "HAL", "Luminaire" }));
        kindBulbComboBox.setName("kindBulbComboBox"); // NOI18N
        kindBulbComboBox.setPreferredSize(new java.awt.Dimension(56, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), kindBulbComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kind_bulb}"), kindBulbComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), kindBulbComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        kindBulbComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kindBulbComboBoxActionPerformed(evt);
            }
        });
        kindBulbComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kindBulbComboBoxKeyPressed(evt);
            }
        });
        erpPanel.add(kindBulbComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 10, 80, -1));

        specUseLabel.setText(resourceMap.getString("specUseLabel.text")); // NOI18N
        specUseLabel.setName("specUseLabel"); // NOI18N
        erpPanel.add(specUseLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 10, -1, -1));

        specUseComboBox.setMaximumRowCount(5);
        specUseComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "Decorative purpose", "Refridgerator bulb", "Orientation purpose", "Toolbox light" }));
        specUseComboBox.setName("specUseComboBox"); // NOI18N
        specUseComboBox.setNextFocusableComponent(voltageComboBox);
        specUseComboBox.setPreferredSize(new java.awt.Dimension(56, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), specUseComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.special_use}"), specUseComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), specUseComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.add(specUseComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, 130, -1));

        inclCheckBox.setBackground(resourceMap.getColor("inclCheckBox.background")); // NOI18N
        inclCheckBox.setText(resourceMap.getString("inclCheckBox.text")); // NOI18N
        inclCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        inclCheckBox.setName("inclCheckBox"); // NOI18N
        inclCheckBox.setPreferredSize(new java.awt.Dimension(21, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.incl}"), inclCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), inclCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.add(inclCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(207, 10, 93, -1));

        bulbTextField.setName("bulbTextField"); // NOI18N
        bulbTextField.setPreferredSize(new java.awt.Dimension(6, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.item_Bulb}"), bulbTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.incl}"), bulbTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.add(bulbTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 10, 100, -1));

        dateTestLabel1.setText(resourceMap.getString("dateTestLabel1.text")); // NOI18N
        dateTestLabel1.setName("dateTestLabel1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), dateTestLabel1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.add(dateTestLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, 16));

        start6000hDateChooser.setDateFormatString(resourceMap.getString("start6000hDateChooser.dateFormatString")); // NOI18N
        start6000hDateChooser.setName("start6000hDateChooser"); // NOI18N
        start6000hDateChooser.setPreferredSize(new java.awt.Dimension(87, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.test_Date}"), start6000hDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), start6000hDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.add(start6000hDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 30, -1, -1));

        cleanStart6000hButton.setFont(resourceMap.getFont("cleanStart6000hButton.font")); // NOI18N
        cleanStart6000hButton.setForeground(resourceMap.getColor("cleanStart6000hButton.foreground")); // NOI18N
        cleanStart6000hButton.setText(resourceMap.getString("cleanStart6000hButton.text")); // NOI18N
        cleanStart6000hButton.setName("cleanStart6000hButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), cleanStart6000hButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanStart6000hButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanStart6000hButtonActionPerformed(evt);
            }
        });
        erpPanel.add(cleanStart6000hButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(226, 30, 10, 10));

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), jLabel21, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 30, 175, -1));

        received6000hDateChooser.setDateFormatString(resourceMap.getString("received6000hDateChooser.dateFormatString")); // NOI18N
        received6000hDateChooser.setName("received6000hDateChooser"); // NOI18N
        received6000hDateChooser.setPreferredSize(new java.awt.Dimension(87, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.t6000H_Date}"), received6000hDateChooser, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), received6000hDateChooser, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        erpPanel.add(received6000hDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(743, 30, -1, -1));

        cleanReceived6000hButton.setFont(resourceMap.getFont("cleanReceived6000hButton.font")); // NOI18N
        cleanReceived6000hButton.setForeground(resourceMap.getColor("cleanReceived6000hButton.foreground")); // NOI18N
        cleanReceived6000hButton.setText(resourceMap.getString("cleanReceived6000hButton.text")); // NOI18N
        cleanReceived6000hButton.setName("cleanReceived6000hButton"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), cleanReceived6000hButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cleanReceived6000hButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanReceived6000hButtonActionPerformed(evt);
            }
        });
        erpPanel.add(cleanReceived6000hButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 30, 10, 10));

        PackInfoPanel.setBorder(new javax.swing.border.LineBorder(null, 1, true));
        PackInfoPanel.setForeground(resourceMap.getColor("PackInfoPanel.foreground")); // NOI18N
        PackInfoPanel.setName("PackInfoPanel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), PackInfoPanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        packingLabel.setForeground(resourceMap.getColor("packingLabel.foreground")); // NOI18N
        packingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        packingLabel.setText(resourceMap.getString("packingLabel.text")); // NOI18N
        packingLabel.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("packingLabel.border.lineColor"))); // NOI18N
        packingLabel.setName("packingLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), packingLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(packingLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 60, -1));

        voltageLabel.setText(resourceMap.getString("voltageLabel.text")); // NOI18N
        voltageLabel.setName("voltageLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), voltageLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(voltageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 25, -1, 16));

        voltageComboBox.setMaximumRowCount(16);
        voltageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "85-240V~ 50/60Hz", "100-240V~ 50/60Hz", "100-240V~ 50Hz", "AC 220-240V", "220-240V~ 50Hz", "220-240V~ 50/60Hz", "230V~ 50Hz", "230V~ 50/60Hz", "3.2VDC", "6.4VDC", "12VAC/DC", "12VDC/AC 50Hz", "12VDC/AC 50/60Hz", "12VDC", "24VDC" }));
        voltageComboBox.setName("voltageComboBox"); // NOI18N
        voltageComboBox.setNextFocusableComponent(amperTextField);
        voltageComboBox.setPreferredSize(new java.awt.Dimension(128, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), voltageComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.voltage}"), voltageComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), voltageComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        voltageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voltageComboBoxActionPerformed(evt);
            }
        });
        voltageComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                voltageComboBoxKeyPressed(evt);
            }
        });
        PackInfoPanel.add(voltageComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 25, -1, 16));

        amperLabel.setText(resourceMap.getString("amperLabel.text")); // NOI18N
        amperLabel.setName("amperLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), amperLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(amperLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, -1, 16));

        amperTextField.setBorder(null);
        amperTextField.setName("amperTextField"); // NOI18N
        amperTextField.setNextFocusableComponent(wattageTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ampere}"), amperTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), amperTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(amperTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 45, 90, 16));

        wattageLabel.setText(resourceMap.getString("wattageLabel.text")); // NOI18N
        wattageLabel.setName("wattageLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), wattageLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(wattageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 65, -1, 16));

        wattageTextField.setBorder(null);
        wattageTextField.setName("wattageTextField"); // NOI18N
        wattageTextField.setNextFocusableComponent(lumenTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.wattage}"), wattageTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), wattageTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        wattageTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                wattageTextFieldMouseExited(evt);
            }
        });
        wattageTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                wattageTextFieldKeyPressed(evt);
            }
        });
        PackInfoPanel.add(wattageTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 65, 90, 16));

        lumenLabel.setText(resourceMap.getString("lumenLabel.text")); // NOI18N
        lumenLabel.setName("lumenLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lumenLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(lumenLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 85, -1, 16));
        lumenLabel.getAccessibleContext().setAccessibleParent(PackInfoPanel);

        lumenTextField.setBorder(null);
        lumenTextField.setName("lumenTextField"); // NOI18N
        lumenTextField.setNextFocusableComponent(livetimeComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lumen}"), lumenTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lumenTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lumenTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lumenTextFieldMouseExited(evt);
            }
        });
        lumenTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lumenTextFieldKeyPressed(evt);
            }
        });
        PackInfoPanel.add(lumenTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(145, 85, 90, 16));
        lumenTextField.getAccessibleContext().setAccessibleParent(PackInfoPanel);

        lifetimeLabel.setText(resourceMap.getString("lifetimeLabel.text")); // NOI18N
        lifetimeLabel.setName("lifetimeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lifetimeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(lifetimeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 5, 100, 16));

        livetimeComboBox.setMaximumRowCount(10);
        livetimeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "1500", "2000", "6000", "8000", "10000", "15000", "20000", "25000", "30000" }));
        livetimeComboBox.setName("livetimeComboBox"); // NOI18N
        livetimeComboBox.setNextFocusableComponent(swicycComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), livetimeComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lifetime}"), livetimeComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), livetimeComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        livetimeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                livetimeComboBoxActionPerformed(evt);
            }
        });
        livetimeComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                livetimeComboBoxKeyPressed(evt);
            }
        });
        PackInfoPanel.add(livetimeComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 5, -1, 16));

        swicycLabel.setText(resourceMap.getString("swicycLabel.text")); // NOI18N
        swicycLabel.setName("swicycLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), swicycLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(swicycLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 25, 100, 16));

        swicycComboBox.setMaximumRowCount(6);
        swicycComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "6000", "8000", "10000", "15000", "30000" }));
        swicycComboBox.setName("swicycComboBox"); // NOI18N
        swicycComboBox.setNextFocusableComponent(kelvinComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), swicycComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.swicyc}"), swicycComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), swicycComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(swicycComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 25, -1, 16));

        kelvinLabel.setText(resourceMap.getString("kelvinLabel.text")); // NOI18N
        kelvinLabel.setName("kelvinLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), kelvinLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(kelvinLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 45, -1, 16));

        kelvinComboBox.setMaximumRowCount(14);
        kelvinComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "2000", "2200", "2700", "2800", "2900", "3000", "4000", "4100", "5000", "6000", "6400", "6500", "RGB" }));
        kelvinComboBox.setName("kelvinComboBox"); // NOI18N
        kelvinComboBox.setNextFocusableComponent(enclasComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), kelvinComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kelvin}"), kelvinComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), kelvinComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        kelvinComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kelvinComboBoxActionPerformed(evt);
            }
        });
        kelvinComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kelvinComboBoxKeyPressed(evt);
            }
        });
        PackInfoPanel.add(kelvinComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 45, 50, 16));

        enclasLabel.setText(resourceMap.getString("enclasLabel.text")); // NOI18N
        enclasLabel.setName("enclasLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), enclasLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(enclasLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 65, -1, 16));

        enclasComboBox.setFont(resourceMap.getFont("enclasComboBox.font")); // NOI18N
        enclasComboBox.setMaximumRowCount(9);
        enclasComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "A++", "A+", "A", "B", "C", "D", "E", "NA" }));
        enclasComboBox.setName("enclasComboBox"); // NOI18N
        enclasComboBox.setNextFocusableComponent(star60TextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), enclasComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.enclas}"), enclasComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), enclasComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(enclasComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 65, 51, 16));

        star60Label.setText(resourceMap.getString("star60Label.text")); // NOI18N
        star60Label.setName("star60Label"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), star60Label, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(star60Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 85, -1, 16));

        star60TextField.setBorder(null);
        star60TextField.setName("star60TextField"); // NOI18N
        star60TextField.setNextFocusableComponent(dimmerComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.star60}"), star60TextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), star60TextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(star60TextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 85, 50, 16));

        dimmerLabel.setText(resourceMap.getString("dimmerLabel.text")); // NOI18N
        dimmerLabel.setName("dimmerLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), dimmerLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(dimmerLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 5, -1, 16));

        dimmerComboBox.setMaximumRowCount(3);
        dimmerComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "No", "Yes" }));
        dimmerComboBox.setName("dimmerComboBox"); // NOI18N
        dimmerComboBox.setNextFocusableComponent(beamTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), dimmerComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dimmer}"), dimmerComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), dimmerComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(dimmerComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 5, 42, 16));

        beamLabel.setText(resourceMap.getString("beamLabel.text")); // NOI18N
        beamLabel.setName("beamLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), beamLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(beamLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 25, -1, 16));

        beamTextField.setBorder(null);
        beamTextField.setName("beamTextField"); // NOI18N
        beamTextField.setNextFocusableComponent(raComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.beam}"), beamTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), beamTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        beamTextField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                beamTextFieldMouseExited(evt);
            }
        });
        beamTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                beamTextFieldKeyPressed(evt);
            }
        });
        PackInfoPanel.add(beamTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 25, 40, 16));

        raLabel.setText(resourceMap.getString("raLabel.text")); // NOI18N
        raLabel.setName("raLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), raLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(raLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 45, -1, 16));

        raComboBox.setMaximumRowCount(5);
        raComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "65", "80", "99", "NA" }));
        raComboBox.setName("raComboBox"); // NOI18N
        raComboBox.setNextFocusableComponent(comparTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), raComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ra}"), raComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), raComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(raComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 45, 40, 16));

        comparLabel.setText(resourceMap.getString("comparLabel.text")); // NOI18N
        comparLabel.setName("comparLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), comparLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(comparLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 65, -1, 16));

        comparTextField.setBorder(null);
        comparTextField.setName("comparTextField"); // NOI18N
        comparTextField.setNextFocusableComponent(fittinComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.compar}"), comparTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), comparTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(comparTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 65, 40, 16));

        fitttinLabel.setText(resourceMap.getString("fitttinLabel.text")); // NOI18N
        fitttinLabel.setName("fitttinLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), fitttinLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(fitttinLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 85, -1, 16));

        fittinComboBox.setMaximumRowCount(14);
        fittinComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "B22", "E14", "E27", "G4", "G9", "G13", "G24 (2p)", "GU5.3", "GU10", "GX53", "GY6.35", "R7s", "NA" }));
        fittinComboBox.setName("fittinComboBox"); // NOI18N
        fittinComboBox.setNextFocusableComponent(kwikTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), fittinComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.fittin}"), fittinComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), fittinComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(fittinComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(565, 85, 65, 16));

        kwikLabel.setText(resourceMap.getString("kwikLabel.text")); // NOI18N
        kwikLabel.setName("kwikLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), kwikLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(kwikLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(655, 5, -1, 16));

        kwikTextField.setBorder(null);
        kwikTextField.setName("kwikTextField"); // NOI18N
        kwikTextField.setNextFocusableComponent(dimension_fiTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.kwik}"), kwikTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), kwikTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(kwikTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 5, 40, 16));

        accentPackCheckBox.setText(resourceMap.getString("accentPackCheckBox.text")); // NOI18N
        accentPackCheckBox.setEnabled(false);
        accentPackCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        accentPackCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        accentPackCheckBox.setName("accentPackCheckBox"); // NOI18N
        accentPackCheckBox.setPreferredSize(new java.awt.Dimension(18, 14));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.aaccent}"), accentPackCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(accentPackCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(644, 25, 160, -1));

        dimension_fiLabel.setText(resourceMap.getString("dimension_fiLabel.text")); // NOI18N
        dimension_fiLabel.setName("dimension_fiLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), dimension_fiLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(dimension_fiLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(655, 45, 130, 16));

        dimension_fiTextField.setBorder(null);
        dimension_fiTextField.setName("dimension_fiTextField"); // NOI18N
        dimension_fiTextField.setNextFocusableComponent(dimension_lTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dimension_Fi}"), dimension_fiTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), dimension_fiTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(dimension_fiTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 45, 40, 16));

        dimension_lLabel.setText(resourceMap.getString("dimension_lLabel.text")); // NOI18N
        dimension_lLabel.setName("dimension_lLabel"); // NOI18N
        PackInfoPanel.add(dimension_lLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(755, 65, 30, -1));

        dimension_lTextField.setBorder(null);
        dimension_lTextField.setName("dimension_lTextField"); // NOI18N
        dimension_lTextField.setNextFocusableComponent(dimension_dTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dimension_L}"), dimension_lTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), dimension_lTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(dimension_lTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 65, 40, 16));

        dimension_dLabel.setText(resourceMap.getString("dimension_dLabel.text")); // NOI18N
        dimension_dLabel.setName("dimension_dLabel"); // NOI18N
        PackInfoPanel.add(dimension_dLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(765, 85, 20, 14));

        dimension_dTextField.setBorder(null);
        dimension_dTextField.setName("dimension_dTextField"); // NOI18N
        dimension_dTextField.setNextFocusableComponent(indoorOutdoorComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.dimension_D}"), dimension_dTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), dimension_dTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        PackInfoPanel.add(dimension_dTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 85, 40, 16));

        erpPanel.add(PackInfoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 50, 840, 105));

        WebInfoPanel.setBorder(new javax.swing.border.LineBorder(resourceMap.getColor("WebInfoPanel.border.lineColor"), 1, true)); // NOI18N
        WebInfoPanel.setName("WebInfoPanel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), WebInfoPanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        websiteLabel.setForeground(resourceMap.getColor("websiteLabel.foreground")); // NOI18N
        websiteLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        websiteLabel.setText(resourceMap.getString("websiteLabel.text")); // NOI18N
        websiteLabel.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("websiteLabel.border.lineColor"))); // NOI18N
        websiteLabel.setName("websiteLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), websiteLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(websiteLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 50, -1));

        wattageRatedLabel.setText(resourceMap.getString("wattageRatedLabel.text")); // NOI18N
        wattageRatedLabel.setName("wattageRatedLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), wattageRatedLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(wattageRatedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, 16));

        wattageRatedTextField.setBorder(null);
        wattageRatedTextField.setName("wattageRatedTextField"); // NOI18N
        wattageRatedTextField.setNextFocusableComponent(lumenRatedLabel);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.wattage_Rated}"), wattageRatedTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), wattageRatedTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(wattageRatedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 20, 40, 16));

        lumenRatedLabel.setText(resourceMap.getString("lumenRatedLabel.text")); // NOI18N
        lumenRatedLabel.setName("lumenRatedLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lumenRatedLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lumenRatedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, 16));

        lumenratedTextField.setBorder(null);
        lumenratedTextField.setName("lumenratedTextField"); // NOI18N
        lumenratedTextField.setNextFocusableComponent(lifetimeRatedTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lumen_Rated}"), lumenratedTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lumenratedTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lumenratedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 40, 16));

        lifetimeRatedLabel.setText(resourceMap.getString("lifetimeRatedLabel.text")); // NOI18N
        lifetimeRatedLabel.setName("lifetimeRatedLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lifetimeRatedLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lifetimeRatedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 110, 16));

        lifetimeRatedTextField.setBorder(null);
        lifetimeRatedTextField.setName("lifetimeRatedTextField"); // NOI18N
        lifetimeRatedTextField.setNextFocusableComponent(angleRatedTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lifetime_Rated}"), lifetimeRatedTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lifetimeRatedTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lifetimeRatedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 60, 40, 16));

        colorLabel.setText(resourceMap.getString("colorLabel.text")); // NOI18N
        colorLabel.setName("colorLabel"); // NOI18N
        WebInfoPanel.add(colorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 20, -1, 16));

        colorTextField.setEditable(false);
        colorTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        colorTextField.setBorder(null);
        colorTextField.setName("colorTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.colour}"), colorTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(colorTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 20, 75, 16));

        angleRatedLabel.setText(resourceMap.getString("angleRatedLabel.text")); // NOI18N
        angleRatedLabel.setName("angleRatedLabel"); // NOI18N
        WebInfoPanel.add(angleRatedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 40, -1, 16));

        angleRatedTextField.setBorder(null);
        angleRatedTextField.setName("angleRatedTextField"); // NOI18N
        angleRatedTextField.setNextFocusableComponent(indoorOutdoorComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.beam_R}"), angleRatedTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), angleRatedTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(angleRatedTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 40, 70, 16));

        indoorOutdoorLabel.setText(resourceMap.getString("indoorOutdoorLabel.text")); // NOI18N
        indoorOutdoorLabel.setName("indoorOutdoorLabel"); // NOI18N
        WebInfoPanel.add(indoorOutdoorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 60, -1, 16));

        indoorOutdoorComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Indoor", "Outdoor" }));
        indoorOutdoorComboBox.setName("indoorOutdoorComboBox"); // NOI18N
        indoorOutdoorComboBox.setNextFocusableComponent(powerFactorTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), indoorOutdoorComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.indoor}"), indoorOutdoorComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), indoorOutdoorComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(indoorOutdoorComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 60, 70, 16));

        powerFactorLabel.setText(resourceMap.getString("powerFactorLabel.text")); // NOI18N
        powerFactorLabel.setName("powerFactorLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), powerFactorLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(powerFactorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 20, 177, 16));

        powerFactorTextField.setBorder(null);
        powerFactorTextField.setName("powerFactorTextField"); // NOI18N
        powerFactorTextField.setNextFocusableComponent(lumenfactorTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.power_Factor}"), powerFactorTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), powerFactorTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(powerFactorTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 20, 40, 16));

        lumenFactorLabel.setText(resourceMap.getString("lumenFactorLabel.text")); // NOI18N
        lumenFactorLabel.setName("lumenFactorLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lumenFactorLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lumenFactorLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 40, 177, 16));

        lumenfactorTextField.setBorder(null);
        lumenfactorTextField.setName("lumenfactorTextField"); // NOI18N
        lumenfactorTextField.setNextFocusableComponent(startTimeTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lumen_Factor}"), lumenfactorTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lumenfactorTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lumenfactorTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 40, 40, 16));

        startTimeLabel.setText(resourceMap.getString("startTimeLabel.text")); // NOI18N
        startTimeLabel.setName("startTimeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), startTimeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(startTimeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 60, 177, 16));

        startTimeTextField.setBorder(null);
        startTimeTextField.setName("startTimeTextField"); // NOI18N
        startTimeTextField.setNextFocusableComponent(lsfTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.start_Time}"), startTimeTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), startTimeTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(startTimeTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 60, 40, 16));

        lsfLabel.setText(resourceMap.getString("lsfLabel.text")); // NOI18N
        lsfLabel.setName("lsfLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lsfLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lsfLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 20, -1, 16));

        lsfTextField.setBorder(null);
        lsfTextField.setName("lsfTextField"); // NOI18N
        lsfTextField.setNextFocusableComponent(colorConsTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.lichtb}"), lsfTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), lsfTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(lsfTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 20, 40, 16));

        colorConsLabel.setText(resourceMap.getString("colorConsLabel.text")); // NOI18N
        colorConsLabel.setName("colorConsLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), colorConsLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(colorConsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 40, 150, 16));

        colorConsTextField.setBorder(null);
        colorConsTextField.setName("colorConsTextField"); // NOI18N
        colorConsTextField.setNextFocusableComponent(candelaTextField);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.color_Cons}"), colorConsTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), colorConsTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(colorConsTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 40, 40, 16));

        candelaLabel.setText(resourceMap.getString("candelaLabel.text")); // NOI18N
        candelaLabel.setName("candelaLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), candelaLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(candelaLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 60, -1, 16));

        candelaTextField.setBorder(null);
        candelaTextField.setName("candelaTextField"); // NOI18N
        candelaTextField.setNextFocusableComponent(shapeComboBox);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.candela}"), candelaTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), candelaTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        WebInfoPanel.add(candelaTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 60, 40, 16));

        erpPanel.add(WebInfoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 165, 840, 90));

        ExtraInfoPanel.setBorder(new javax.swing.border.LineBorder(null, 1, true));
        ExtraInfoPanel.setForeground(resourceMap.getColor("ExtraInfoPanel.foreground")); // NOI18N
        ExtraInfoPanel.setName("ExtraInfoPanel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), ExtraInfoPanel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        extraLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        extraLabel.setText(resourceMap.getString("extraLabel.text")); // NOI18N
        extraLabel.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("extraLabel.border.lineColor"))); // NOI18N
        extraLabel.setName("extraLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), extraLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(extraLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 40, -1));

        shapeLabel.setText(resourceMap.getString("shapeLabel.text")); // NOI18N
        shapeLabel.setName("shapeLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), shapeLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(shapeLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 25, -1, 20));

        shapeComboBox.setMaximumRowCount(22);
        shapeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { " ", "A55", "A60", "C37", "CF37", "G45", "G95", "G120", "Mini", "MR16", "PAR16", "PAR20", "PAR38", "R39", "R50", "R63", "R80", "Spiral", "R7S", "T8", "T26", "NA" }));
        shapeComboBox.setName("shapeComboBox"); // NOI18N
        shapeComboBox.setPreferredSize(new java.awt.Dimension(60, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), shapeComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.shape}"), shapeComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), shapeComboBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(shapeComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 25, -1, -1));

        numberLEDLabel.setText(resourceMap.getString("numberLEDLabel.text")); // NOI18N
        numberLEDLabel.setName("numberLEDLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), numberLEDLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(numberLEDLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 45, -1, 16));

        numberLEDTextField.setBorder(null);
        numberLEDTextField.setName("numberLEDTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.led_Number}"), numberLEDTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), numberLEDTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(numberLEDTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 45, 60, 16));

        kindLedLabel.setText(resourceMap.getString("kindLedLabel.text")); // NOI18N
        kindLedLabel.setName("kindLedLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), kindLedLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(kindLedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, -1, 16));

        kindLEDTextField.setBorder(null);
        kindLEDTextField.setName("kindLEDTextField"); // NOI18N
        kindLEDTextField.setNextFocusableComponent(saveButton);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.led_Type}"), kindLEDTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), kindLEDTextField, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(kindLEDTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 30, 60, 16));

        uvCheckBox.setText(resourceMap.getString("uvCheckBox.text")); // NOI18N
        uvCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        uvCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        uvCheckBox.setName("uvCheckBox"); // NOI18N
        uvCheckBox.setPreferredSize(new java.awt.Dimension(21, 18));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.UV}"), uvCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), uvCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(uvCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 50, 90, -1));

        jComboBox1.setMaximumRowCount(2);
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "NA", "YES" }));
        jComboBox1.setName("jComboBox1"); // NOI18N
        jComboBox1.setPreferredSize(new java.awt.Dimension(56, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.spectrum}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), jComboBox1, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, -1, -1));

        spectrumLabel.setText(resourceMap.getString("spectrumLabel.text")); // NOI18N
        spectrumLabel.setName("spectrumLabel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.eup}"), spectrumLabel, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        ExtraInfoPanel.add(spectrumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, -1, 16));

        erpPanel.add(ExtraInfoPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 265, 310, 70));

        jCheckBox1.setBackground(resourceMap.getColor("jCheckBox1.background")); // NOI18N
        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        jCheckBox1.setPreferredSize(new java.awt.Dimension(103, 16));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.int_Led}"), jCheckBox1, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        erpPanel.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 10, -1, -1));

        erpSpectrumLabel.setText(resourceMap.getString("erpSpectrumLabel.text")); // NOI18N
        erpSpectrumLabel.setName("erpSpectrumLabel"); // NOI18N
        erpPanel.add(erpSpectrumLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 260, 340, 80));

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(desktopapplication1.DesktopApplication1.class).getContext().getActionMap(DesktopApplication1View.class, this);
        exportButton.setAction(actionMap.get("ErP")); // NOI18N
        exportButton.setBackground(resourceMap.getColor("exportButton.background")); // NOI18N
        exportButton.setForeground(resourceMap.getColor("exportButton.foreground")); // NOI18N
        exportButton.setText(resourceMap.getString("exportButton.text")); // NOI18N
        exportButton.setBorder(null);
        exportButton.setName("exportButton"); // NOI18N
        exportButton.setPreferredSize(new java.awt.Dimension(133, 18));
        erpPanel.add(exportButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 300, 105, 20));

        jTabbedPane1.addTab(resourceMap.getString("erpPanel.TabConstraints.tabTitle"), erpPanel); // NOI18N

        mainPanel.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 150, 890, 385));
        jTabbedPane1.getAccessibleContext().setAccessibleName(resourceMap.getString("jTabbedPane1.AccessibleContext.accessibleName")); // NOI18N

        sapField.setFont(resourceMap.getFont("itemField.font")); // NOI18N
        sapField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sapField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        sapField.setName("sapField"); // NOI18N
        sapField.setPreferredSize(new java.awt.Dimension(4, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.sap}"), sapField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        supplierField.addMouseListener(new ContextMenuMouseListener());
        binding.setSourceUnreadableValue(null);
        mainPanel.add(sapField, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 10, 100, 25));

        sapLabel.setFont(resourceMap.getFont("sapLabel.font")); // NOI18N
        sapLabel.setText(resourceMap.getString("sapLabel.text")); // NOI18N
        sapLabel.setName("sapLabel"); // NOI18N
        mainPanel.add(sapLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, -1, 25));

        originalItemField.setFont(resourceMap.getFont("originalItemField.font")); // NOI18N
        originalItemField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        originalItemField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        originalItemField.setName("originalItemField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.sItem}"), originalItemField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        supplierField.addMouseListener(new ContextMenuMouseListener());
        mainPanel.add(originalItemField, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 50, 185, 20));

        hierarchyLabel.setFont(resourceMap.getFont("hierarchyLabel.font")); // NOI18N
        hierarchyLabel.setText(resourceMap.getString("hierarchyLabel.text")); // NOI18N
        hierarchyLabel.setName("hierarchyLabel"); // NOI18N
        mainPanel.add(hierarchyLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(925, 50, -1, 20));

        hierarchyComboBox.setMaximumRowCount(12);
        hierarchyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "S0100", "S0200", "S0300", "S0400", "S0500", "S0600", "S0900", "S1000", "S1100", "S1200", "S1300", "S1400" }));
        hierarchyComboBox.setName("hierarchyComboBox"); // NOI18N
        hierarchyComboBox.setPreferredSize(new java.awt.Dimension(70, 20));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ObjectProperty.create(), hierarchyComboBox, org.jdesktop.beansbinding.BeanProperty.create("elements"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.hierarchy}"), hierarchyComboBox, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        hierarchyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hierarchyComboBoxActionPerformed(evt);
            }
        });
        binding.setSourceUnreadableValue(null);
        mainPanel.add(hierarchyComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 50, 60, -1));

        herarchyLabel1.setFont(resourceMap.getFont("herarchyLabel1.font")); // NOI18N
        herarchyLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        herarchyLabel1.setName("herarchyLabel1"); // NOI18N
        mainPanel.add(herarchyLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 70, 150, 15));

        validDate1Label.setName("validDate1Label"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("Valid in SAP since: ${selectedElement.validDate}"), validDate1Label, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        mainPanel.add(validDate1Label, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 12, -1, 20));

        checkingLabel1.setFont(resourceMap.getFont("checkingLabel1.font")); // NOI18N
        checkingLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        checkingLabel1.setText(resourceMap.getString("checkingLabel1.text")); // NOI18N
        checkingLabel1.setName("checkingLabel1"); // NOI18N
        mainPanel.add(checkingLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 534, 120, 15));

        checkingDateChooser1.setDateFormatString(resourceMap.getString("checkingDateChooser1.dateFormatString")); // NOI18N
        checkingDateChooser1.setFont(resourceMap.getFont("checkingDateChooser1.font")); // NOI18N
        checkingDateChooser1.setName("checkingDateChooser1"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.checkdate}"), checkingDateChooser1, org.jdesktop.beansbinding.BeanProperty.create("date"));
        bindingGroup.addBinding(binding);

        checkingDateChooser1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                checkingDateChooser1PropertyChange(evt);
            }
        });
        mainPanel.add(checkingDateChooser1, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 534, 85, 15));

        cleanKkDateButton1.setFont(resourceMap.getFont("cleanKkDateButton1.font")); // NOI18N
        cleanKkDateButton1.setForeground(resourceMap.getColor("cleanKkDateButton1.foreground")); // NOI18N
        cleanKkDateButton1.setText(resourceMap.getString("cleanKkDateButton1.text")); // NOI18N
        cleanKkDateButton1.setName("cleanKkDateButton1"); // NOI18N
        cleanKkDateButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanKkDateButton1ActionPerformed(evt);
            }
        });
        mainPanel.add(cleanKkDateButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(665, 534, 10, 10));

        eanLabel.setFont(resourceMap.getFont("eanLabel.font")); // NOI18N
        eanLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        eanLabel.setText(resourceMap.getString("eanLabel.text")); // NOI18N
        eanLabel.setName("eanLabel"); // NOI18N
        mainPanel.add(eanLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(715, 12, 30, 20));

        eanTextField.setFont(resourceMap.getFont("eanTextField.font")); // NOI18N
        eanTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        eanTextField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 2, true));
        eanTextField.setName("eanTextField"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.ean}"), eanTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        mainPanel.add(eanTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(744, 12, 115, 20));

        pictureLabel.setText(resourceMap.getString("pictureLabel.text")); // NOI18N
        pictureLabel.setName("pictureLabel"); // NOI18N
        mainPanel.add(pictureLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 230, 230, 240));

        qcStatusLabel.setForeground(resourceMap.getColor("qcStatusLabel.foreground")); // NOI18N
        qcStatusLabel.setName("qcStatusLabel"); // NOI18N
        qcStatusLabel.setOpaque(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.QMStatus}"), qcStatusLabel, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        qcStatusLabel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                qcStatusLabelPropertyChange(evt);
            }
        });
        mainPanel.add(qcStatusLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 10, 110, 20));

        authorityLabel.setText(resourceMap.getString("authorityLabel.text")); // NOI18N
        authorityLabel.setName("authorityLabel"); // NOI18N
        mainPanel.add(authorityLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 473, -1, -1));

        authorityScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        authorityScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        authorityScrollPane.setName("authorityScrollPane"); // NOI18N

        authorityTextArea.setColumns(1);
        authorityTextArea.setFont(resourceMap.getFont("authorityTextArea.font")); // NOI18N
        authorityTextArea.setForeground(resourceMap.getColor("authorityTextArea.foreground")); // NOI18N
        authorityTextArea.setRows(3);
        authorityTextArea.setName("authorityTextArea"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.remarks_Auth}"), authorityTextArea, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement != null}"), authorityTextArea, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        authorityScrollPane.setViewportView(authorityTextArea);

        mainPanel.add(authorityScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(1060, 486, 230, 50));

        componentsPanel.setName("componentsPanel"); // NOI18N
        componentsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        componentsLabel.setText(resourceMap.getString("componentsLabel.text")); // NOI18N
        componentsLabel.setName("componentsLabel"); // NOI18N
        componentsPanel.add(componentsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(3, 2, -1, -1));

        componentStatLabel1.setForeground(resourceMap.getColor("componentStatLabel1.foreground")); // NOI18N
        componentStatLabel1.setName("componentStatLabel1"); // NOI18N
        componentStatLabel1.setOpaque(true);
        componentsPanel.add(componentStatLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 20, 16));

        componentSapLabel1.setFont(resourceMap.getFont("componentSapLabel1.font")); // NOI18N
        componentSapLabel1.setToolTipText(resourceMap.getString("componentSapLabel1.toolTipText")); // NOI18N
        componentSapLabel1.setName("componentSapLabel1"); // NOI18N
        componentSapLabel1.setOpaque(true);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component1}"), componentSapLabel1, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel1MouseClicked(evt);
            }
        });
        componentSapLabel1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel1PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 170, 16));

        componentStatLabel2.setForeground(resourceMap.getColor("componentStatLabel5.foreground")); // NOI18N
        componentStatLabel2.setName("componentStatLabel2"); // NOI18N
        componentStatLabel2.setOpaque(true);
        componentsPanel.add(componentStatLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 20, 16));

        componentSapLabel2.setToolTipText(resourceMap.getString("componentSapLabel2.toolTipText")); // NOI18N
        componentSapLabel2.setName("componentSapLabel2"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component2}"), componentSapLabel2, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel2MouseClicked(evt);
            }
        });
        componentSapLabel2.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel2PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 170, 16));

        componentStatLabel3.setForeground(resourceMap.getColor("componentStatLabel5.foreground")); // NOI18N
        componentStatLabel3.setName("componentStatLabel3"); // NOI18N
        componentStatLabel3.setOpaque(true);
        componentsPanel.add(componentStatLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 20, 16));

        componentSapLabel3.setToolTipText(resourceMap.getString("componentSapLabel3.toolTipText")); // NOI18N
        componentSapLabel3.setName("componentSapLabel3"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component3}"), componentSapLabel3, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel3MouseClicked(evt);
            }
        });
        componentSapLabel3.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel3PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 60, 170, 16));

        componentStatLabel4.setForeground(resourceMap.getColor("componentStatLabel5.foreground")); // NOI18N
        componentStatLabel4.setName("componentStatLabel4"); // NOI18N
        componentStatLabel4.setOpaque(true);
        componentsPanel.add(componentStatLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 20, 16));

        componentSapLabel4.setToolTipText(resourceMap.getString("componentSapLabel4.toolTipText")); // NOI18N
        componentSapLabel4.setName("componentSapLabel4"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component4}"), componentSapLabel4, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel4MouseClicked(evt);
            }
        });
        componentSapLabel4.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel4PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, 170, 16));

        componentStatLabel5.setForeground(resourceMap.getColor("componentStatLabel5.foreground")); // NOI18N
        componentStatLabel5.setName("componentStatLabel5"); // NOI18N
        componentStatLabel5.setOpaque(true);
        componentsPanel.add(componentStatLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 20, 16));

        componentSapLabel5.setToolTipText(resourceMap.getString("componentSapLabel5.toolTipText")); // NOI18N
        componentSapLabel5.setName("componentSapLabel5"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component5}"), componentSapLabel5, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel5MouseClicked(evt);
            }
        });
        componentSapLabel5.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel5PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 170, 16));

        componentStatLabel6.setForeground(resourceMap.getColor("componentStatLabel6.foreground")); // NOI18N
        componentStatLabel6.setName("componentStatLabel6"); // NOI18N
        componentStatLabel6.setOpaque(true);
        componentsPanel.add(componentStatLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 20, 16));

        componentSapLabel6.setToolTipText(resourceMap.getString("componentSapLabel6.toolTipText")); // NOI18N
        componentSapLabel6.setName("componentSapLabel6"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component6}"), componentSapLabel6, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel6MouseClicked(evt);
            }
        });
        componentSapLabel6.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel6PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, 170, 16));

        componentStatLabel7.setForeground(resourceMap.getColor("componentStatLabel7.foreground")); // NOI18N
        componentStatLabel7.setName("componentStatLabel7"); // NOI18N
        componentStatLabel7.setOpaque(true);
        componentsPanel.add(componentStatLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 20, 16));

        componentSapLabel7.setToolTipText(resourceMap.getString("componentSapLabel7.toolTipText")); // NOI18N
        componentSapLabel7.setName("componentSapLabel7"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component7}"), componentSapLabel7, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel7MouseClicked(evt);
            }
        });
        componentSapLabel7.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel7PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, 170, 16));

        componentStatLabel8.setForeground(resourceMap.getColor("componentStatLabel8.foreground")); // NOI18N
        componentStatLabel8.setName("componentStatLabel8"); // NOI18N
        componentStatLabel8.setOpaque(true);
        componentsPanel.add(componentStatLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 20, 16));

        componentSapLabel8.setToolTipText(resourceMap.getString("componentSapLabel8.toolTipText")); // NOI18N
        componentSapLabel8.setName("componentSapLabel8"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component8}"), componentSapLabel8, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel8MouseClicked(evt);
            }
        });
        componentSapLabel8.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel8PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 160, 170, 16));

        componentStatLabel9.setForeground(resourceMap.getColor("componentStatLabel9.foreground")); // NOI18N
        componentStatLabel9.setName("componentStatLabel9"); // NOI18N
        componentStatLabel9.setOpaque(true);
        componentsPanel.add(componentStatLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 20, 16));

        componentSapLabel9.setToolTipText(resourceMap.getString("componentSapLabel9.toolTipText")); // NOI18N
        componentSapLabel9.setName("componentSapLabel9"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component9}"), componentSapLabel9, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel9MouseClicked(evt);
            }
        });
        componentSapLabel9.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel9PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 180, 170, 16));

        componentStatLabel10.setForeground(resourceMap.getColor("componentStatLabel10.foreground")); // NOI18N
        componentStatLabel10.setName("componentStatLabel10"); // NOI18N
        componentStatLabel10.setOpaque(true);
        componentsPanel.add(componentStatLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 20, 16));

        componentSapLabel10.setToolTipText(resourceMap.getString("componentSapLabel10.toolTipText")); // NOI18N
        componentSapLabel10.setName("componentSapLabel10"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.component10}"), componentSapLabel10, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        componentSapLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                componentSapLabel10MouseClicked(evt);
            }
        });
        componentSapLabel10.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                componentSapLabel10PropertyChange(evt);
            }
        });
        componentsPanel.add(componentSapLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 200, 170, 16));

        mainPanel.add(componentsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1070, 5, 220, 220));

        hiddenCheckBox.setFont(resourceMap.getFont("hiddenCheckBox.font")); // NOI18N
        hiddenCheckBox.setForeground(resourceMap.getColor("hiddenCheckBox.foreground")); // NOI18N
        hiddenCheckBox.setText(resourceMap.getString("hiddenCheckBox.text")); // NOI18N
        hiddenCheckBox.setEnabled(false);
        hiddenCheckBox.setFocusPainted(false);
        hiddenCheckBox.setFocusable(false);
        hiddenCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        hiddenCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        hiddenCheckBox.setName("hiddenCheckBox"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${selectedElement.hidden}"), hiddenCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        hiddenCheckBox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hiddenCheckBoxStateChanged(evt);
            }
        });
        mainPanel.add(hiddenCheckBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 132, 460, 38));

        menuBar.setBackground(resourceMap.getColor("menuBar.background")); // NOI18N
        menuBar.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("menuBar.border.lineColor"), 3)); // NOI18N
        menuBar.setForeground(resourceMap.getColor("menuBar.foreground")); // NOI18N
        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newRecordMenuItem.setAction(actionMap.get("newRecord")); // NOI18N
        newRecordMenuItem.setName("newRecordMenuItem"); // NOI18N
        fileMenu.add(newRecordMenuItem);

        deleteRecordMenuItem.setAction(actionMap.get("deleteRecord")); // NOI18N
        deleteRecordMenuItem.setName("deleteRecordMenuItem"); // NOI18N
        fileMenu.add(deleteRecordMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        fileMenu.add(jSeparator1);

        saveMenuItem.setAction(actionMap.get("save")); // NOI18N
        saveMenuItem.setName("saveMenuItem"); // NOI18N
        fileMenu.add(saveMenuItem);

        refreshMenuItem.setAction(actionMap.get("refresh")); // NOI18N
        refreshMenuItem.setName("refreshMenuItem"); // NOI18N
        fileMenu.add(refreshMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        fileMenu.add(jSeparator2);

        printMenuItem.setAction(actionMap.get("printFrame")); // NOI18N
        printMenuItem.setText(resourceMap.getString("printMenuItem.text")); // NOI18N
        printMenuItem.setName("printMenuItem"); // NOI18N
        fileMenu.add(printMenuItem);

        jMenuItem1.setAction(actionMap.get("copyErP")); // NOI18N
        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        fileMenu.add(jMenuItem1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jReportMenu.setText(resourceMap.getString("jReportMenu.text")); // NOI18N
        jReportMenu.setName("jReportMenu"); // NOI18N

        standardsReportMenu.setText(resourceMap.getString("standardsReportMenu.text")); // NOI18N
        standardsReportMenu.setName("standardsReportMenu"); // NOI18N
        standardsReportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                standardsReportMenuActionPerformed(evt);
            }
        });
        jReportMenu.add(standardsReportMenu);

        authorityReportMenu.setText(resourceMap.getString("authorityReportMenu.text")); // NOI18N
        authorityReportMenu.setName("authorityReportMenu"); // NOI18N
        authorityReportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authorityReportMenuActionPerformed(evt);
            }
        });
        jReportMenu.add(authorityReportMenu);

        overviewReportMenu.setText(resourceMap.getString("overviewReportMenu.text")); // NOI18N
        overviewReportMenu.setName("overviewReportMenu"); // NOI18N

        exportOverviewReportMenu.setText(resourceMap.getString("exportOverviewReportMenu.text")); // NOI18N
        exportOverviewReportMenu.setName("exportOverviewReportMenu"); // NOI18N
        exportOverviewReportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportOverviewReportMenuActionPerformed(evt);
            }
        });
        overviewReportMenu.add(exportOverviewReportMenu);

        openOverviewReportMenu.setText(resourceMap.getString("openOverviewReportMenu.text")); // NOI18N
        openOverviewReportMenu.setName("openOverviewReportMenu"); // NOI18N
        openOverviewReportMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openOverviewReportMenuActionPerformed(evt);
            }
        });
        overviewReportMenu.add(openOverviewReportMenu);

        jReportMenu.add(overviewReportMenu);

        menuBar.add(jReportMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        userMenu.setText(System.getProperty("user.name"));
        userMenu.setFont(resourceMap.getFont("userMenu.font")); // NOI18N
        userMenu.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userMenu.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        userMenu.setName("userMenu"); // NOI18N

        menuBar.add(Box.createHorizontalGlue());

        menuBar.add(userMenu);

        statusPanel.setName("statusPanel"); // NOI18N
        statusPanel.setPreferredSize(new java.awt.Dimension(1001, 37));
        statusPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N
        statusPanel.add(statusPanelSeparator, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1060, -1));

        DocDateChooser.setDateFormatString(resourceMap.getString("DocDateChooser.dateFormatString")); // NOI18N
        DocDateChooser.setName("DocDateChooser"); // NOI18N
        statusPanel.add(DocDateChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        docPdfButton.setText(resourceMap.getString("docPdfButton.text")); // NOI18N
        docPdfButton.setName("docPdfButton"); // NOI18N
        docPdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docPdfButtonActionPerformed(evt);
            }
        });
        statusPanel.add(docPdfButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(96, 8, 74, -1));

        docWordButton.setText(resourceMap.getString("docWordButton.text")); // NOI18N
        docWordButton.setName("docWordButton"); // NOI18N
        docWordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docWordButtonActionPerformed(evt);
            }
        });
        statusPanel.add(docWordButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 8, 81, -1));

        printButton.setAction(actionMap.get("printFrame")); // NOI18N
        printButton.setMaximumSize(new java.awt.Dimension(53, 23));
        printButton.setMinimumSize(new java.awt.Dimension(53, 23));
        printButton.setName("printButton"); // NOI18N
        printButton.setPreferredSize(new java.awt.Dimension(45, 23));
        statusPanel.add(printButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 8, 54, -1));

        newButton.setAction(actionMap.get("newRecord")); // NOI18N
        newButton.setName("newButton"); // NOI18N
        newButton.setPreferredSize(new java.awt.Dimension(45, 23));
        statusPanel.add(newButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 8, 53, -1));

        deleteButton.setAction(actionMap.get("deleteRecord")); // NOI18N
        deleteButton.setFocusable(false);
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.setPreferredSize(new java.awt.Dimension(45, 23));
        statusPanel.add(deleteButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(405, 8, 63, -1));

        refreshButton.setAction(actionMap.get("refresh")); // NOI18N
        refreshButton.setName("refreshButton"); // NOI18N
        refreshButton.setPreferredSize(new java.awt.Dimension(45, 23));
        statusPanel.add(refreshButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 8, 70, -1));

        saveButton.setAction(actionMap.get("save")); // NOI18N
        saveButton.setName("saveButton"); // NOI18N
        saveButton.setPreferredSize(new java.awt.Dimension(45, 23));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        statusPanel.add(saveButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 8, 56, -1));

        copyButton.setText(resourceMap.getString("copyButton.text")); // NOI18N
        copyButton.setName("copyButton"); // NOI18N
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });
        statusPanel.add(copyButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 8, 57, -1));

        folderButton.setText(resourceMap.getString("folderButton.text")); // NOI18N
        folderButton.setName("folderButton"); // NOI18N
        folderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                folderButtonActionPerformed(evt);
            }
        });
        statusPanel.add(folderButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(715, 8, 62, -1));

        productButton.setText(resourceMap.getString("productButton.text")); // NOI18N
        productButton.setName("productButton"); // NOI18N
        productButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productButtonActionPerformed(evt);
            }
        });
        statusPanel.add(productButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 8, 111, -1));

        statusMessageLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusMessageLabel.setAutoscrolls(true);
        statusMessageLabel.setName("statusMessageLabel"); // NOI18N
        statusPanel.add(statusMessageLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(898, 10, -1, -1));

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N
        statusPanel.add(statusAnimationLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(1045, 10, -1, -1));

        progressBar.setName("progressBar"); // NOI18N
        progressBar.setPreferredSize(new java.awt.Dimension(146, 10));
        statusPanel.add(progressBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(899, 25, -1, -1));

        rowSorterToStringConverter1.setTable(masterTable);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void initComponents1() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();
        Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), lvdCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), emcCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), cprCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), rfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), rohsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), reachCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), erpCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), batt1TrCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), fluxCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), pahCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), phthCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), oemCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), vdsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), bosecCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), komoCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), nfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), otherCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), batt2TrCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), docCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), doiCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), cdfCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), photobiolCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), ipCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, masterTable, org.jdesktop.beansbinding.ELProperty.create("${!selectedElement.hidden}"), gsCheckBox, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        bindingGroup.bind();
    }

    private void docPdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_docPdfButtonActionPerformed
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec") || System.getProperty("user.name").equals("ANetten")
                || System.getProperty("user.name").equals("Anetten") || System.getProperty("user.name").equals("RvanKasteren") || System.getProperty("user.name").equals("jyan")
                || System.getProperty("user.name").equals("RvanDommelen")) {
            String itemNo = itemField.getText();
            String sapNo = sapField.getText();
            Date Date = DocDateChooser.getDate();

            final String sapWithoutDocs = sapNo.replace(".", "");
            File newDocPath = new File(productContent + "\\" + sapWithoutDocs + "\\");
            File newDocFile = null;

            if (newDocPath.exists()) {
                File[] allDocFiles = newDocPath.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isFile() && pathname.getName().toString().startsWith("DoC_" + sapWithoutDocs);
                    }
                });
                switch (allDocFiles.length) {
                    case 0:
                        newDocFile = null;
                        break;
                    case 1:
                        newDocFile = allDocFiles[0];
                        break;
                }
            } else {
                newDocPath.mkdirs();
            }
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            if (newDocFile != null) {
                String createDay = newDocFile.getName().toString().substring(18, 20);
                String createMonth = newDocFile.getName().toString().substring(16, 18);
                String createYear = newDocFile.getName().toString().substring(12, 16);

                Object[] options = {"Open existing DoC file", "Create a new DoC file"};
                int n = JOptionPane.showOptionDialog(CertPanel, "DoC file already exist.\n" + "Created on: " + createDay + "." + createMonth + "." + createYear, "DoC",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    try {
                        if (newDocFile.exists()) {
                            desktop.open(newDocFile);
                        }
                    } catch (IOException e) {
                    }
                } else if (n == JOptionPane.NO_OPTION) {
                    createDocPDF(itemNo, Date, sapNo);
                }
            } 
            //        else if (oldDocFile.exists()) {
            //            Object[] options = {"Open old DoC file", "Create a new DoC file"};
            //            
            //            int n = JOptionPane.showOptionDialog(CertPanel, "Old DoC file already exist.\n" + "Created on: " + dateFormat.format(oldDocFile.lastModified()), "DoC",
            //                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            //            if (n == JOptionPane.YES_OPTION) {
            //                try {
            //                    if (oldDocFile.exists()) {
            //                        desktop.open(oldDocFile);
            //                    }
            //                } catch (IOException e) {
            //                }
            //            } else if (n == JOptionPane.NO_OPTION) {
            //                createDoC(itemNo, Date, sapNo);
            //            }
            //        } 
            else {
                createDocPDF(itemNo, Date, sapNo);
            }
        } else {
            JOptionPane.showMessageDialog(null, "You have no rights to make a DoC", "No privileges", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_docPdfButtonActionPerformed

    private void folderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_folderButtonActionPerformed
        String path = itemField.getText();
        String supplier_name = supplierField.getText();
        String or_item_n = originalItemField.getText();

        String path1 = path.replace("/", "_");

        File mainpath = new File("G:/QC/Certificates");

        File file = new File(mainpath + "/" + path1);

        Desktop desktop = null;

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            if (file.exists()) {
                desktop.open(file);
            } else {
                int n = JOptionPane.showConfirmDialog(CertPanel, "Would you like to create one?", "Folder doesn't exist !!!",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.OK_OPTION) {

                    File mainfolder = new File(file + "/" + "(" + or_item_n + ")" + "_" + supplier_name);
                    String[] foldery = {"/Certificates", "/QC", "/Diagrams", "/Correspondence"};

                    for (int i = 0; i < foldery.length; i++) {
                        File cert = new File(mainfolder + foldery[i]);
                        cert.mkdirs();
                    }
                    desktop.open(file);
                }
            }
        } catch (IOException e) {
        }
    }//GEN-LAST:event_folderButtonActionPerformed

    private void standardsReportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_standardsReportMenuActionPerformed
        if (raport == null) {
            JFrame mainFrame = DesktopApplication1.getApplication().getMainFrame();
            raport = new DesktopApplication1Raport(mainFrame);
            raport.setLocationRelativeTo(mainFrame);
        }
        DesktopApplication1.getApplication().show(raport);
    }//GEN-LAST:event_standardsReportMenuActionPerformed

    private void erpCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_erpCheckBoxActionPerformed
        if (erpCheckBox.isSelected()) {
            erpStatusComboBox.setSelectedIndex(0);
            erpDirComboBox.setSelectedIndex(0);
            if (erpTrField.getText().isEmpty()) {
                erpTrField.setText("MISSING");
                erpTrField.setForeground(Color.red);
            } else {
                erpTrField.setForeground(Color.black);
            }
        } else {
            erpStatusComboBox.setSelectedItem(null);
            erpDirComboBox.setSelectedItem(null);
            if (erpTrField.getText().equals("MISSING")) {
                erpTrField.setText(null);
            }
        }
    }//GEN-LAST:event_erpCheckBoxActionPerformed

    private void lvdCleanDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lvdCleanDateButtonActionPerformed
        lvdDateChooser.setDate(null);
    }//GEN-LAST:event_lvdCleanDateButtonActionPerformed

    private void cleanEmcDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanEmcDateButtonActionPerformed
        EmcDateChooser.setDate(null);
    }//GEN-LAST:event_cleanEmcDateButtonActionPerformed

    private void cleanOemDateFromButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanOemDateFromButtonActionPerformed
        OemDateFromChooser.setDate(null);
    }//GEN-LAST:event_cleanOemDateFromButtonActionPerformed

    private void cleanOemDateToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanOemDateToButtonActionPerformed
        OemDateToChooser.setDate(null);
    }//GEN-LAST:event_cleanOemDateToButtonActionPerformed

    private void cleanNfDateToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanNfDateToButtonActionPerformed
        NfDateChooser.setDate(null);
    }//GEN-LAST:event_cleanNfDateToButtonActionPerformed

    private void cleanGsDateToButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanGsDateToButtonActionPerformed
        GsDateChooser.setDate(null);
    }//GEN-LAST:event_cleanGsDateToButtonActionPerformed

    private void cleanRfDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanRfDateButtonActionPerformed
        RtteDateChooser.setDate(null);
    }//GEN-LAST:event_cleanRfDateButtonActionPerformed

    private void cleanErpDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanErpDateButtonActionPerformed
        EupDateChooser.setDate(null);
    }//GEN-LAST:event_cleanErpDateButtonActionPerformed

    private void cleanRohsDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanRohsDateButtonActionPerformed
        RohsDateChooser.setDate(null);
    }//GEN-LAST:event_cleanRohsDateButtonActionPerformed

    private void cleanCprDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanCprDateButtonActionPerformed
        cprDateChooser.setDate(null);
    }//GEN-LAST:event_cleanCprDateButtonActionPerformed

    private void cleanVdsDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanVdsDateButtonActionPerformed
        VdsDateChooser.setDate(null);
    }//GEN-LAST:event_cleanVdsDateButtonActionPerformed

    private void cleanBosecDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanBosecDateButtonActionPerformed
        BosecDateChooser.setDate(null);
    }//GEN-LAST:event_cleanBosecDateButtonActionPerformed

    private void cleanKomoDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanKomoDateButtonActionPerformed
        KomoDateChooser.setDate(null);
    }//GEN-LAST:event_cleanKomoDateButtonActionPerformed

    private void cleanOtherDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanOtherDateButtonActionPerformed
        otherDateChooser.setDate(null);

    }//GEN-LAST:event_cleanOtherDateButtonActionPerformed

    private void beamTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_beamTextFieldKeyPressed
        /*
        beamTextField.setFocusTraversalKeysEnabled(false);         if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {             if (!beamTextField.getText().isEmpty() || !beamTextField.getText().equals("") || beamTextField.getText() != null || beamTextField.getText().length() != 0) {                 if (angleRatedTextField.getText() != null && angleRatedTextField.getText().equals("")) {                     angleRatedTextField.setText(beamTextField.getText());                     if (beamTextField.getText().equals("N/A") || Integer.parseInt(beamTextField.getText()) > 89) {                         accentPackCheckBox.setSelected(false);                     } else {                         accentPackCheckBox.setSelected(true);                     }                 }                 raComboBox.grabFocus();             }         }     }//GEN-LAST:event_beamTextFieldKeyPressed
         */
        beamTextField.setFocusTraversalKeysEnabled(false);
        if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {
            if (!beamTextField.getText().isEmpty() || !beamTextField.getText().equals("") || beamTextField.getText() != null || beamTextField.getText().length() != 0) {
                if (angleRatedTextField.getText() != null && angleRatedTextField.getText().equals("")) {
                    angleRatedTextField.setText(beamTextField.getText());
                    if (beamTextField.getText().equals("NA") || Integer.parseInt(beamTextField.getText()) > 89) {
                        accentPackCheckBox.setSelected(false);
                    } else {
                        accentPackCheckBox.setSelected(true);
                    }
                }
                raComboBox.grabFocus();
            }
        }
    }
    private void beamTextFieldMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_beamTextFieldMouseExited

        if (!beamTextField.getText().isEmpty() && angleRatedTextField.getText() != null && angleRatedTextField.getText().equals("")) {             angleRatedTextField.setText(String.valueOf(Integer.parseInt(beamTextField.getText())));         }     }//GEN-LAST:event_beamTextFieldMouseExited

    private void kelvinComboBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kelvinComboBoxKeyPressed
        kelvinComboBox.setFocusTraversalKeysEnabled(false);         if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') //    {   if (kelvinComboBox.getSelectedItem()!=" ")         //        {int temp = Integer.valueOf((String) kelvinComboBox.getSelectedItem());               //        if (kelvinComboBox.getSelectedIndex()>0)           //        {   if (temp<3001)         //           {colorTextField.setText("Warm White");}         //           else if (temp>3000 && temp<6000)         //           {colorTextField.setText("Natural White");}         //           else {colorTextField.setText("Cool White");}         //       enclasComboBox.grabFocus();         //        }         //        }         //      }         {             if (kelvinComboBox.getSelectedIndex() > 0) {                 switch (kelvinComboBox.getSelectedIndex()) {                     case 1:                         colorTextField.setText("Warm White");                         break;                     case 2:                         colorTextField.setText("Warm White");                         break;                     case 3:                         colorTextField.setText("Warm White");                         break;                     case 4:                         colorTextField.setText("Warm White");                         break;                     case 5:                         colorTextField.setText("Warm White");                         break;                     case 6:                         colorTextField.setText("Natural White");                         break;                     case 7:                         colorTextField.setText("Natural White");                         break;                     case 8:                         colorTextField.setText("Cool White");                         break;                     case 9:                         colorTextField.setText("Cool White");                         break;                     case 10:                         colorTextField.setText("Cool White");                         break;                     case 11:                         colorTextField.setText("RGB");                         break;                 }                 enclasComboBox.grabFocus();             }         }     }//GEN-LAST:event_kelvinComboBoxKeyPressed
            if (kelvinComboBox.getSelectedIndex() > 0) {
                switch (kelvinComboBox.getSelectedIndex()) {
                    case 1:
                    colorTextField.setText("Warm White");
                    break;
                case 2:
                    colorTextField.setText("Warm White");
                    break;
                case 3:
                    colorTextField.setText("Warm White");
                    break;
                case 4:
                    colorTextField.setText("Warm White");
                    break;
                case 5:
                    colorTextField.setText("Warm White");
                    break;
                case 6:
                    colorTextField.setText("Warm White");
                    break;
                case 7:
                    colorTextField.setText("Natural White");
                    break;
                case 8:
                    colorTextField.setText("Natural White");
                    break;
                case 9:
                    colorTextField.setText("Cool White");
                    break;
                case 10:
                    colorTextField.setText("Cool White");
                    break;
                case 11:
                    colorTextField.setText("Cool White");
                    break;
                case 12:
                    colorTextField.setText("Cool White");
                    break;    
                case 13:
                    colorTextField.setText("RGB");
                    break;
                }
                enclasComboBox.grabFocus();
            }


    }
    private void livetimeComboBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_livetimeComboBoxKeyPressed

        livetimeComboBox.setFocusTraversalKeysEnabled(false);         if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {             if (livetimeComboBox.getSelectedIndex() != 0 && livetimeComboBox.getSelectedItem() != null) {                 if (lifetimeRatedTextField.getText() != null && lifetimeRatedTextField.getText().equals("")) {                     lifetimeRatedTextField.setText(String.valueOf(livetimeComboBox.getSelectedItem()));                 }                 swicycComboBox.grabFocus();             }         }     }//GEN-LAST:event_livetimeComboBoxKeyPressed

    private void livetimeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_livetimeComboBoxActionPerformed
        livetimeComboBox.setFocusTraversalKeysEnabled(false);

        {
            if (livetimeComboBox.getSelectedIndex() != 0 && livetimeComboBox.getSelectedItem() != null) {
                if (lifetimeRatedTextField.getText() != null && lifetimeRatedTextField.getText().equals("")) {
                    lifetimeRatedTextField.setText(String.valueOf(livetimeComboBox.getSelectedItem()));
                }
                swicycComboBox.grabFocus();
            }
        }
    }//GEN-LAST:event_livetimeComboBoxActionPerformed

    private void lumenTextFieldMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lumenTextFieldMouseExited
        /*
        if (!lumenTextField.getText().isEmpty() && lumenratedTextField.getText() != null && lumenratedTextField.getText().equals("")) {             lumenratedTextField.setText(String.valueOf(Integer.parseInt(lumenTextField.getText())));         }     }//GEN-LAST:event_lumenTextFieldMouseExited
         */
        if (!lumenTextField.getText().isEmpty() && lumenratedTextField.getText() != null && lumenratedTextField.getText().equals("")) {
            lumenratedTextField.setText(String.valueOf(Integer.parseInt(lumenTextField.getText())));
        }
    }
    private void wattageTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_wattageTextFieldKeyPressed
        /*
        wattageTextField.setFocusTraversalKeysEnabled(false);         if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {             if (!wattageTextField.getText().isEmpty() || !wattageTextField.getText().equals("") || wattageTextField.getText() != null || wattageTextField.getText().length() != 0) {                 if (wattageRatedTextField.getText() != null && wattageRatedTextField.getText().equals("")) {                     wattageRatedTextField.setText(String.valueOf(Double.parseDouble(wattageTextField.getText())));                 }                 lumenTextField.grabFocus();             }         }     }//GEN-LAST:event_wattageTextFieldKeyPressed
         */
        wattageTextField.setFocusTraversalKeysEnabled(false);
        if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {
            if (!wattageTextField.getText().isEmpty() || !wattageTextField.getText().equals("") || wattageTextField.getText() != null || wattageTextField.getText().length() != 0) {
                if (wattageRatedTextField.getText() != null && wattageRatedTextField.getText().equals("")) {
                    wattageRatedTextField.setText(String.valueOf(Double.parseDouble(wattageTextField.getText())));
                }
                lumenTextField.grabFocus();
            }
        }
    }
    private void wattageTextFieldMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_wattageTextFieldMouseExited
        /*
        if (!wattageTextField.getText().isEmpty() && wattageRatedTextField.getText() != null && wattageRatedTextField.getText().equals("")) {             wattageRatedTextField.setText(String.valueOf(Double.parseDouble(wattageTextField.getText())));         }     }//GEN-LAST:event_wattageTextFieldMouseExited
         */
        if (!wattageTextField.getText().isEmpty() && wattageRatedTextField.getText() != null && wattageRatedTextField.getText().equals("")) {
            wattageRatedTextField.setText(String.valueOf(Double.parseDouble(wattageTextField.getText())));
        }
    }
    private void voltageComboBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voltageComboBoxKeyPressed
        /*
        voltageComboBox.setFocusTraversalKeysEnabled(false);         if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {             if (voltageComboBox.getSelectedIndex() > 0) {                 amperTextField.grabFocus();             }         }     }//GEN-LAST:event_voltageComboBoxKeyPressed
         */
        voltageComboBox.setFocusTraversalKeysEnabled(false);
        if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {
            if (voltageComboBox.getSelectedIndex() > 0) {
                amperTextField.grabFocus();
            }
        }
    }
    private void voltageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voltageComboBoxActionPerformed
        /*
        voltageComboBox.setFocusTraversalKeysEnabled(false);         if (voltageComboBox.getSelectedIndex() > 0) {             amperTextField.grabFocus();         }     }//GEN-LAST:event_voltageComboBoxActionPerformed
         */
        voltageComboBox.setFocusTraversalKeysEnabled(false);
        if (voltageComboBox.getSelectedIndex() > 0) {
            amperTextField.grabFocus();
        }
    }
    private void kindBulbComboBoxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kindBulbComboBoxKeyPressed
        /*
        kindBulbComboBox.setFocusTraversalKeysEnabled(false);         if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {              if (kindBulbComboBox.getSelectedIndex() > 0) {                 switch (kindBulbComboBox.getSelectedIndex()) {                     case 1:                         kwikTextField.setText("N/A");                         colorConsTextField.setText("");                         numberLEDTextField.setText("");                         kindLEDTextField.setText("");                         uvCheckBox.setSelected(false);                         break;                     case 2:                         kwikTextField.setText("");                         colorConsTextField.setText("N/A");                         numberLEDTextField.setText("N/A");                         kindLEDTextField.setText("N/A");                         uvCheckBox.setSelected(false);                         break;                     case 3:                         kwikTextField.setText("N/A");                         colorConsTextField.setText("N/A");                         numberLEDTextField.setText("N/A");                         kindLEDTextField.setText("N/A");                         uvCheckBox.setSelected(true);                         break;                 }                  specUseComboBox.grabFocus();             }          }     }//GEN-LAST:event_kindBulbComboBoxKeyPressed
         */
        kindBulbComboBox.setFocusTraversalKeysEnabled(false);
        if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {
            if (kindBulbComboBox.getSelectedIndex() > 0) {
                switch (kindBulbComboBox.getSelectedIndex()) {
                    case 1:
                        kwikTextField.setText("NA");
                        colorConsTextField.setText("");
                        numberLEDTextField.setText("");
                        kindLEDTextField.setText("");
                        uvCheckBox.setSelected(false);
                        break;
                    case 2:
                        kwikTextField.setText("");
                        colorConsTextField.setText("NA");
                        numberLEDTextField.setText("NA");
                        kindLEDTextField.setText("NA");
                        uvCheckBox.setSelected(false);
                        break;
                    case 3:
                        kwikTextField.setText("NA");
                        colorConsTextField.setText("NA");
                        numberLEDTextField.setText("NA");
                        kindLEDTextField.setText("NA");
                        uvCheckBox.setSelected(true);
                        break;
                }
                specUseComboBox.grabFocus();
            }
        }
    }
    private void cleanReceived6000hButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanReceived6000hButtonActionPerformed
        received6000hDateChooser.setDate(null);
    }//GEN-LAST:event_cleanReceived6000hButtonActionPerformed

    private void jStatusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStatusComboBoxActionPerformed

        switch (jStatusComboBox.getSelectedIndex()) {
            case 0:
                statusLabel1.setText("Decline ");
                break;
            case 1:
                statusLabel1.setText("Purchase block – no successor ");
                break;
            case 2:
                statusLabel1.setText("ID phase ");
                break;
            case 3:
                statusLabel1.setText("Introduction phase ");
                break;
            case 4:
                statusLabel1.setText("Active ");
                break;
            case 5:
                statusLabel1.setText("op=op (ending)");
                break;
            case 6:
                statusLabel1.setText("Promotion item ");
                break;
            case 7:
                statusLabel1.setText("End of life time ");
                break;
            case 8:
                statusLabel1.setText("No SAP no. ");
                break;
        }

    }//GEN-LAST:event_jStatusComboBoxActionPerformed

    private void hierarchyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hierarchyComboBoxActionPerformed
        switch (hierarchyComboBox.getSelectedIndex()) {
            case 0:
                herarchyLabel1.setText("Fire (Michiel v/d Riet)");
                break;
            case 1:
                herarchyLabel1.setText("Door-entry (Ross Anderson)");
                break;
            case 2:
                herarchyLabel1.setText("Camera (Sven Emmen)");
                break;
            case 3:
                herarchyLabel1.setText("Alarm (Ad Daamen)");
                break;
            case 4:
                herarchyLabel1.setText("Home-automation (Ad Daamen)");
                break;
            case 5:
                herarchyLabel1.setText("Personal care (Sven Emmen)");
                break;
            case 6:
                herarchyLabel1.setText("Other (Michiel v/d Riet)");
                break;
            case 7:
                herarchyLabel1.setText("Functional (Jan-Willem)");
                break;
            case 8:
                herarchyLabel1.setText("Indoor (Marcel Trouw)");
                break;
            case 9:
                herarchyLabel1.setText("Outdoor (Jan-Willem)");
                break;
            case 10:
                herarchyLabel1.setText("Bulbs (Kit)");
                break;
            case 11:
                herarchyLabel1.setText("Smartlights (Ad Netten)");
                break;
            default:
                herarchyLabel1.setText("");
        }
    }//GEN-LAST:event_hierarchyComboBoxActionPerformed

    private void cleanKkDateButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanKkDateButton1ActionPerformed
        checkingDateChooser1.setDate(null);
    }//GEN-LAST:event_cleanKkDateButton1ActionPerformed

    private void checkingDateChooser1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_checkingDateChooser1PropertyChange
        if (checkingDateChooser1.getDate() == null) {
            checkingLabel1.setText("Sample not checked yet");
        } else {
            checkingLabel1.setText("Sample checked on: ");
        }
    }//GEN-LAST:event_checkingDateChooser1PropertyChange

    private void kindBulbComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kindBulbComboBoxActionPerformed
        kindBulbComboBox.setFocusTraversalKeysEnabled(false);
        if (kindBulbComboBox.getSelectedIndex() > 0) {
            switch (kindBulbComboBox.getSelectedIndex()) {
                case 1:
                    kwikTextField.setText("NA");
                    colorConsTextField.setText("");
                    numberLEDTextField.setText("");
                    kindLEDTextField.setText("");
                    uvCheckBox.setSelected(false);
                    break;
                case 2:
                    kwikTextField.setText("");
                    colorConsTextField.setText("NA");
                    numberLEDTextField.setText("NA");
                    kindLEDTextField.setText("NA");
                    uvCheckBox.setSelected(false);
                    break;
                case 3:
                    kwikTextField.setText("NA");
                    colorConsTextField.setText("NA");
                    numberLEDTextField.setText("NA");
                    kindLEDTextField.setText("NA");
                    uvCheckBox.setSelected(true);
                    break;
            }
            specUseComboBox.grabFocus();
        }
    }//GEN-LAST:event_kindBulbComboBoxActionPerformed

    private void adaptorClass1LogoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_adaptorClass1LogoPropertyChange
//        ImageIcon classI = new ImageIcon(getClass().getResource("/desktopapplication1/resources/ClassI.png"));
//        ImageIcon classII = new ImageIcon(getClass().getResource("/desktopapplication1/resources/ClassII.png"));
//        ImageIcon classIII = new ImageIcon(getClass().getResource("/desktopapplication1/resources/ClassIII.png"));
//            switch (applianceClass1ComboBox.getSelectedIndex()) {
//            case 0:
//                applianceClass1Logo.setIcon(null);
//                break;
//            case 1:
//                applianceClass1Logo.setIcon(classI);
//                break;
//            case 2:
//                applianceClass1Logo.setIcon(classII);
//                break;
//            case 3:
//                applianceClass1Logo.setIcon(classIII);
//                break;
//        }
    }//GEN-LAST:event_adaptorClass1LogoPropertyChange

    private void adaptorClass2LogoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_adaptorClass2LogoPropertyChange
//        ImageIcon classI = new ImageIcon(getClass().getResource("/desktopapplication1/resources/ClassI.png"));
//        ImageIcon classII = new ImageIcon(getClass().getResource("/desktopapplication1/resources/ClassII.png"));
//        ImageIcon classIII = new ImageIcon(getClass().getResource("/desktopapplication1/resources/ClassIII.png"));
//            switch (applianceClass2ComboBox.getSelectedIndex()) {
//            case 0:
//                applianceClass2Logo.setIcon(null);
//                break;
//            case 1:
//                applianceClass2Logo.setIcon(classI);
//                break;
//            case 2:
//                applianceClass2Logo.setIcon(classII);
//                break;
//            case 3:
//                applianceClass2Logo.setIcon(classIII);
//                break;
//        }
    }//GEN-LAST:event_adaptorClass2LogoPropertyChange

    private void lvdCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lvdCheckBoxActionPerformed
        if (lvdCheckBox.isSelected()) {
            lvdDirComboBox.setSelectedIndex(0);
            if (lvdTrField.getText().isEmpty()) {
                lvdTrField.setText("MISSING");
                lvdTrField.setForeground(Color.red);
            } else {
                lvdTrField.setForeground(Color.black);
            }
            if (lvdCeField.getText().isEmpty()) {
                lvdCeField.setText("MISSING");
                lvdCeField.setForeground(Color.red);
            } else {
                lvdCeField.setForeground(Color.black);
            }

        } else {
            lvdDirComboBox.setSelectedItem(null);
            if (lvdTrField.getText().equals("MISSING")) {
                lvdTrField.setText(null);
            }
            if (lvdCeField.getText().equals("MISSING")) {
                lvdCeField.setText(null);
            }
        }
    }//GEN-LAST:event_lvdCheckBoxActionPerformed

    private void emcCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emcCheckBoxActionPerformed
        if (emcCheckBox.isSelected()) {
            emcDirComboBox.setSelectedIndex(0);
            if (emcTrField.getText().isEmpty()) {
                emcTrField.setText("MISSING");
                emcTrField.setForeground(Color.red);
            } else {
                emcTrField.setForeground(Color.black);
            }
            if (emcCeField.getText().isEmpty()) {
                emcCeField.setText("NA");
            }
        } else {
            emcDirComboBox.setSelectedItem(null);
            if (emcTrField.getText().equals("MISSING")) {
                emcTrField.setText(null);
            }
            if (emcCeField.getText().equals("NA")) {
                emcCeField.setText(null);
            }
        }
    }//GEN-LAST:event_emcCheckBoxActionPerformed

    private void rfCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rfCheckBoxActionPerformed
        if (rfCheckBox.isSelected()) {
            rfDirComboBox.setSelectedIndex(0);
            if (rfTrField.getText().isEmpty()) {
                rfTrField.setText("MISSING");
                rfTrField.setForeground(Color.red);
            } else {
                rfTrField.setForeground(Color.black);
            }
            if (rfCeField.getText().isEmpty()) {
                rfCeField.setText("MISSING");
                rfCeField.setForeground(Color.red);
            } else {
                rfCeField.setForeground(Color.black);
            }
        } else {
            rfDirComboBox.setSelectedItem(null);
            if (rfTrField.getText().equals("MISSING")) {
                rfTrField.setText(null);
            }
            if (rfCeField.getText().equals("MISSING")) {
                rfCeField.setText(null);
            }
        }
    }//GEN-LAST:event_rfCheckBoxActionPerformed

    private void fluxTrComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fluxTrComboBoxActionPerformed
        if (fluxTrComboBox.getSelectedIndex() == 2) {
            fluxTrComboBox.setForeground(Color.RED);
        } else {
            fluxTrComboBox.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_fluxTrComboBoxActionPerformed

    private void pahCeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pahCeComboBoxActionPerformed
        if (pahCeComboBox.getSelectedIndex() == 1) {
            pahCeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 8));
        } else {
            pahCeComboBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
        }
        if (pahCeComboBox.getSelectedIndex() == 2) {
            pahCeComboBox.setForeground(Color.RED);
        } else {
            pahCeComboBox.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_pahCeComboBoxActionPerformed

    private void rohsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rohsCheckBoxActionPerformed
        if (rohsCheckBox.isSelected()) {
            rohsDirComboBox.setSelectedIndex(0);
            if (rohsTrField.getText().isEmpty()) {
                rohsTrField.setText("MISSING");
                rohsTrField.setForeground(Color.red);
            } else {
                rohsTrField.setForeground(Color.black);
            }
            if (rohsCeField.getText().isEmpty()) {
                rohsCeField.setText("NA");
            }

        } else {
            rohsDirComboBox.setSelectedItem(null);
            if (rohsTrField.getText().equals("MISSING")) {
                rohsTrField.setText(null);
            }
            if (rohsCeField.getText().equals("NA")) {
                rohsCeField.setText(null);
            }
        }
    }//GEN-LAST:event_rohsCheckBoxActionPerformed

    private void pahCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pahCheckBoxActionPerformed
        if (pahCheckBox.isSelected()) {
            if (pahCeComboBox.getSelectedIndex() != 0 || pahCeComboBox.getSelectedIndex() != 1) {
                pahCeComboBox.setForeground(Color.RED);
                pahCeComboBox.setSelectedIndex(2);
            } else {
                pahCeComboBox.setForeground(Color.BLACK);
            }
        } else {

            pahCeComboBox.setSelectedItem(null);
        }
    }//GEN-LAST:event_pahCheckBoxActionPerformed

    private void fluxCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fluxCheckBoxActionPerformed
        if (fluxCheckBox.isSelected()) {
            if (fluxTrComboBox.getSelectedIndex() != 1) {
                fluxTrComboBox.setForeground(Color.RED);
                fluxTrComboBox.setSelectedIndex(2);
            } else {
                fluxTrComboBox.setForeground(Color.BLACK);
            }
        } else {
            if (fluxTrComboBox.getSelectedIndex() == 2) {
                fluxTrComboBox.setSelectedIndex(0);
            }
            fluxTrComboBox.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_fluxCheckBoxActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec") || System.getProperty("user.name").equals("RRemmig") || System.getProperty("user.name").equals("RvanDommelen")) {
            Object[] options = {"Yes", "No"};
            int reply = JOptionPane.showOptionDialog(CertPanel, "Copy all info from this item for all the same items?", "COPY", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    String item = itemField.getText();
                    String sap = sapField.getText();
                    String item_s = originalItemField.getText();

                    con = Utils.getConnection();
                    st = con.createStatement();

                    String SQL2 = " update elro.items"
                            + " set"
                            //                            + " DESCR_EN=(select DESCR_EN from(select DESCR_EN from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            //                            + " DESCR_FR=(select DESCR_FR from(select DESCR_FR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            //                            + " DESCR_DE=(select DESCR_DE from(select DESCR_DE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            //                            + " DESCR_NL=(select DESCR_NL from(select DESCR_NL from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            //                            + " DESCR_ES=(select DESCR_ES from(select DESCR_ES from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            //                            + " DESCR_PL=(select DESCR_PL from(select DESCR_PL from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC1=(select EMC1 from(select EMC1 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC2=(select EMC2 from(select EMC2 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC3=(select EMC3 from(select EMC3 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC4=(select EMC4 from(select EMC4 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC5=(select EMC5 from(select EMC5 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC6=(select EMC6 from(select EMC6 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC7=(select EMC7 from(select EMC7 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC8=(select EMC8 from(select EMC8 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC9=(select EMC9 from(select EMC9 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC10=(select EMC10 from(select EMC10 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD1=(select LVD1 from(select LVD1 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD2=(select LVD2 from(select LVD2 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD3=(select LVD3 from(select LVD3 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD4=(select LVD4 from(select LVD4 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD5=(select LVD5 from(select LVD5 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD6=(select LVD6 from(select LVD6 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD7=(select LVD7 from(select LVD7 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD8=(select LVD8 from(select LVD8 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD9=(select LVD9 from(select LVD9 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " CPD1=(select CPD1 from(select CPD1 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " CPD2=(select CPD2 from(select CPD2 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " CPD3=(select CPD3 from(select CPD3 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " CPD4=(select CPD4 from(select CPD4 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF1=(select RF1 from(select RF1 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF2=(select RF2 from(select RF2 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF3=(select RF3 from(select RF3 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF4=(select RF4 from(select RF4 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " GS=(select GS from(select GS from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " GS_CE=(select GS_CE from(select GS_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " GS_TR=(select GS_TR from(select GS_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " GS_DATE=(select GS_DATE from(select GS_DATE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " GS_NB=(select GS_NB from(select GS_NB from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD=(select LVD from(select LVD from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD_CE=(select LVD_CE from(select LVD_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD_CERT=(select LVD_CERT from(select LVD_CERT from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD_TR=(select LVD_TR from(select LVD_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD_DATE=(select LVD_DATE from(select LVD_DATE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " LVD_NB=(select LVD_NB from(select LVD_NB from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " PHOTOBIOL=(select PHOTOBIOL from(select PHOTOBIOL from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " PHOTOBIOL_TR=(select PHOTOBIOL_TR from(select PHOTOBIOL_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " IPCLASS=(select IPCLASS from(select IPCLASS from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " IPCLASS_TR=(select IPCLASS_TR from(select IPCLASS_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC=(select EMC from(select EMC from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC_CE=(select EMC_CE from(select EMC_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC_CERT=(select EMC_CERT from(select EMC_CERT from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC_TR=(select EMC_TR from(select EMC_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC_DATE=(select EMC_DATE from(select EMC_DATE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EMC_NB=(select EMC_NB from(select EMC_NB from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF=(select RF from(select RF from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF_CE=(select RF_CE from(select RF_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF_CERT=(select RF_CERT from(select RF_CERT from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF_TR=(select RF_TR from(select RF_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF_DATE=(select RF_DATE from(select RF_DATE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF_NB=(select RF_NB from(select RF_NB from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF_F=(select RF_F from(select RF_F from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " RF_NBN=(select RF_NBN from(select RF_NBN from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " ROHS=(select ROHS from(select ROHS from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " ROHS_CE=(select ROHS_CE from(select ROHS_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " ROHS_CERT=(select ROHS_CERT from(select ROHS_CERT from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " ROHS_TR=(select ROHS_TR from(select ROHS_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " ROHS_DATE=(select ROHS_DATE from(select ROHS_DATE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " ROHS_NB=(select ROHS_NB from(select ROHS_NB from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EUP=(select EUP from(select EUP from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EUP_CE=(select EUP_CE from(select EUP_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EUP_TR=(select EUP_TR from(select EUP_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EUP_DATE=(select EUP_DATE from(select EUP_DATE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " EUP_STATUS=(select EUP_STATUS from(select EUP_STATUS from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " FLUX=(select FLUX from(select FLUX from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " FLUX_TR=(select FLUX_TR from(select FLUX_TR from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " KK=(select KK from(select KK from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " KK_CE=(select KK_CE from(select KK_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " KK_DATE=(select KK_DATE from(select KK_DATE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " REACH=(select REACH from(select REACH from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " REACH_CE=(select REACH_CE from(select REACH_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " PHTH=(select PHTH from(select PHTH from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " PAH=(select PAH from(select PAH from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " PAH_CE=(select PAH_CE from(select PAH_CE from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " BATT=(select BATT from(select BATT from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " BATT_M=(select BATT_M from(select BATT_M from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " BATT2=(select BATT2 from(select BATT2 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " BATT_TR2=(select BATT_TR2 from(select BATT_TR2 from elro.items where sap ='" + sap + "' and item ='" + item + "')x),"
                            + " DOC=(select DOC from(select DOC from elro.items where sap ='" + sap + "' and item ='" + item + "')x)"
                            + " where item_s='" + item_s + "';";

                    st.executeUpdate(SQL2);



                } catch (SQLException ex) {
                    Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    Utils.closeDB(rs, st, con);
                  }
            }
        } else {
            JOptionPane.showMessageDialog(null, "You have no rights for this action", "No privileges", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_copyButtonActionPerformed

    private void battSize1ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_battSize1ComboBoxActionPerformed
        if (battSize1ComboBox.getSelectedIndex() > 0) {
            switch (battSize1ComboBox.getSelectedIndex()) {
                case 1:
                    battVolt1TextField.setText("1.5");
                    break;
                case 2:
                    battVolt1TextField.setText("1.5");
                    break;
                case 3:
                    battVolt1TextField.setText("1.5");
                    break;
                case 4:
                    battVolt1TextField.setText("1.5");
                    break;
                case 5:
                    battVolt1TextField.setText("9");
                    break;
                case 6:
                    battVolt1TextField.setText("12");
                    break;
                case 13:
                    battVolt1TextField.setText("1.5");
                    break;
                case 14:
                    battVolt1TextField.setText("3.7");
                    break;

                default:
                    battVolt1TextField.setText("3");
                    break;
            }
        }
    }//GEN-LAST:event_battSize1ComboBoxActionPerformed

    private void battSize2ComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_battSize2ComboBoxActionPerformed
        if (battSize2ComboBox.getSelectedIndex() > 0) {
            switch (battSize2ComboBox.getSelectedIndex()) {
                case 1:
                    battVolt2TextField.setText("1.5");
                    break;
                case 2:
                    battVolt2TextField.setText("1.5");
                    break;
                case 3:
                    battVolt2TextField.setText("1.5");
                    break;
                case 4:
                    battVolt2TextField.setText("1.5");
                    break;
                case 5:
                    battVolt2TextField.setText("9");
                    break;
                case 6:
                    battVolt2TextField.setText("12");
                    break;
                case 13:
                    battVolt2TextField.setText("3.7");
                    break;
                default:
                    battVolt2TextField.setText("3");
                    break;
            }
        }
    }//GEN-LAST:event_battSize2ComboBoxActionPerformed

    private void batt1TrComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batt1TrComboBoxActionPerformed

        if (batt1TrComboBox.getSelectedIndex() > 0) {
            switch (batt1TrComboBox.getSelectedIndex()) {
                case 3:
                    batt1TrComboBox.setForeground(Color.BLACK);
                    batt1InclLabel.setText("excluding");
                    break;
                case 4:
                    batt1TrComboBox.setForeground(Color.RED);
                    batt1InclLabel.setText("including");
                    break;
                default:
                    batt1TrComboBox.setForeground(Color.BLACK);
                    batt1InclLabel.setText("including");
            }
        } else {
            batt1InclLabel.setText(null);
        }
    }//GEN-LAST:event_batt1TrComboBoxActionPerformed

    private void batt2TrComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batt2TrComboBoxActionPerformed
        if (batt2TrComboBox.getSelectedIndex() > 0) {
            switch (batt2TrComboBox.getSelectedIndex()) {
                case 3:
                    batt2TrComboBox.setForeground(Color.BLACK);
                    batt2InclLabel.setText("excluding");
                    break;
                case 4:
                    batt2TrComboBox.setForeground(Color.RED);
                    batt2InclLabel.setText("including");
                    break;
                default:
                    batt2TrComboBox.setForeground(Color.BLACK);
                    batt2InclLabel.setText("including");
            }
        } else {
            batt2InclLabel.setText(null);
        }
    }//GEN-LAST:event_batt2TrComboBoxActionPerformed

    private void productButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productButtonActionPerformed
        String sapWithoutDocs = sapField.getText().replace(".", "");

        File file = new File(productContent + "\\" + sapWithoutDocs);

        Desktop desktop = null;

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            if (file.exists()) {
                desktop.open(file);
            } else {
                int n = JOptionPane.showConfirmDialog(CertPanel, "Would you like to create one?", "Folder doesn't exist !!!",
                        JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.OK_OPTION) {

                    file.mkdirs();
                }
                desktop.open(file);
            }

        } catch (IOException e) {
        }
    }//GEN-LAST:event_productButtonActionPerformed

    private void reachCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reachCheckBoxActionPerformed
        if (reachCheckBox.isSelected()) {
            if (reachCeComboBox.getSelectedIndex() != 1) {
                reachCeComboBox.setForeground(Color.RED);
                reachCeComboBox.setSelectedIndex(2);
            } else {
                reachCeComboBox.setForeground(Color.BLACK);
            }
        } else {
            reachCeComboBox.setSelectedItem(null);
        }
    }//GEN-LAST:event_reachCheckBoxActionPerformed

    private void reachCeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reachCeComboBoxActionPerformed
        if (reachCeComboBox.getSelectedIndex() == 2) {
            reachCeComboBox.setForeground(Color.RED);
        } else {
            reachCeComboBox.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_reachCeComboBoxActionPerformed

private void cleanStart6000hButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanStart6000hButtonActionPerformed
    start6000hDateChooser.setDate(null);
}//GEN-LAST:event_cleanStart6000hButtonActionPerformed

    private void lumenTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lumenTextFieldKeyPressed
        lumenTextField.setFocusTraversalKeysEnabled(false);
        if (evt.getKeyChar() == '\t' || evt.getKeyChar() == '\n') {
            if (!lumenTextField.getText().isEmpty()
                    || !lumenTextField.getText().equals("")
                    || lumenTextField.getText() != null
                    || lumenTextField.getText().length() != 0) {
                if (lumenratedTextField.getText() != null && lumenratedTextField.getText().equals("")) {
                    lumenratedTextField.setText(String.valueOf(Integer.parseInt(lumenTextField.getText())));
                }
                livetimeComboBox.grabFocus();
            }
        }
    }//GEN-LAST:event_lumenTextFieldKeyPressed

    private void masterTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMouseClicked
        String sapWithoutDocs = sapField.getText().replace(".", "");
        int imgHeight, newHeight = 0;
        int imgWidth, newWidth = 0;

        BufferedImage img = null;
        File source2 = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_2.jpg");
        File source3 = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_3.jpg");
        File source10 = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_10.jpg");
        File source=null;
        if (source2.exists()) {
            source = source2;
        } else if (source3.exists()) {
            source = source3;
        } else {
            source = source10;
        }
        
        if (source.exists()) {
            try {
                img = ImageIO.read(source);
            } catch (IOException e) {
            }
            imgHeight = img.getHeight();
            imgWidth = img.getWidth();
            if ((float) imgHeight / (float) imgWidth > (float) pictureLabel.getSize().height / (float) pictureLabel.getSize().width) {
                newHeight = pictureLabel.getSize().height;
                newWidth = (int) (((float) imgWidth / (float) imgHeight) * (float) newHeight);
            } else {
                newWidth = pictureLabel.getSize().width;
                newHeight = (int) (((float) imgHeight / (float) imgWidth) * (float) newWidth);
            }
            Image dimg = img.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            pictureLabel.setIcon(imageIcon);
        } else {
            pictureLabel.setIcon(new ImageIcon(getClass().getResource("/desktopapplication1/resources/nopicture.jpg")));
        }

        BufferedImage img_erp = null;
        File erp_spectrum = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_34.jpg");
        if (erp_spectrum.exists()) {
            try {
                img_erp = ImageIO.read(erp_spectrum);
            } catch (IOException e) {
            }
            imgHeight = img_erp.getHeight();
            imgWidth = img_erp.getWidth();
            if ((float) imgHeight / (float) imgWidth > (float) erpSpectrumLabel.getSize().height / (float) erpSpectrumLabel.getSize().width) {
                newHeight = erpSpectrumLabel.getSize().height;
                newWidth = (int) (((float) imgWidth / (float) imgHeight) * (float) newHeight);
            } else {
                newWidth = erpSpectrumLabel.getSize().width;
                newHeight = (int) (((float) imgHeight / (float) imgWidth) * (float) newWidth);
            }
            Image dimg1 = img_erp.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
            ImageIcon imageIcon1 = new ImageIcon(dimg1);
            erpSpectrumLabel.setIcon(imageIcon1);
        } else {
            erpSpectrumLabel.setIcon(null);
        }
//        JTextField textFields[] = {lvdTrField, emcTrField, rfTrField, cprCeField, erpTrField, rohsTrField};
//        for (int i = 0; i < textFields.length; i += 1) {
//            if (textFields[i].getText().equals("MISSING")) {
//                textFields[i].setForeground(Color.red);
//            } else {
//                textFields[i].setForeground(Color.black);
//            }
//        }

        if (lvdTrField.getText().contains("MISSING")) {
            lvdTrField.setForeground(Color.red);
        } else {
            lvdTrField.setForeground(Color.black);
        }
        if (lvdCeField.getText().contains("MISSING")) {
            lvdCeField.setForeground(Color.red);
        } else {
            lvdCeField.setForeground(Color.black);
        }
        if (emcTrField.getText().contains("MISSING")) {
            emcTrField.setForeground(Color.red);
        } else {
            emcTrField.setForeground(Color.black);
        }
        if (rfTrField.getText().contains("MISSING")) {
            rfTrField.setForeground(Color.red);
        } else {
            rfTrField.setForeground(Color.black);
        }
        if (rfCeField.getText().contains("MISSING")) {
            rfCeField.setForeground(Color.red);
        } else {
            rfCeField.setForeground(Color.black);
        }
        if (cprCeField.getText().contains("MISSING")) {
            cprCeField.setForeground(Color.red);
        } else {
            cprCeField.setForeground(Color.black);
        }
        if (cprTrField.getText().contains("MISSING")) {
            cprTrField.setForeground(Color.red);
        } else {
            cprTrField.setForeground(Color.black);
        }
        if (erpTrField.getText().contains("MISSING")) {
            erpTrField.setForeground(Color.red);
        } else {
            erpTrField.setForeground(Color.black);
        }
        if (rohsTrField.getText().contains("MISSING")) {
            rohsTrField.setForeground(Color.red);
        } else {
            rohsTrField.setForeground(Color.black);
        }
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec") || System.getProperty("user.name").equals("RRemmig")) {
        hiddenCheckBox.setEnabled(true);
        }
        
    }//GEN-LAST:event_masterTableMouseClicked

    private void masterTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_masterTableMousePressed
        String sapWithoutDocs = sapField.getText().replace(".", "");
        int imgHeight, newHeight = 0;
        int imgWidth, newWidth = 0;

        BufferedImage img = null;
        File source2 = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_2.jpg");
        File source3 = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_3.jpg");
        File source10 = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_10.jpg");
        File source=null;
        if (source2.exists()) {
            source = source2;
        } else if (source3.exists()) {
            source = source3;
        } else {
            source = source10;
        }

        if (source.exists()) {
            try {
                img = ImageIO.read(source);
            } catch (IOException e) {
            }
            imgHeight = img.getHeight();
            imgWidth = img.getWidth();
            if ((float) imgHeight / (float) imgWidth > (float) pictureLabel.getSize().height / (float) pictureLabel.getSize().width) {
                newHeight = pictureLabel.getSize().height;
                newWidth = (int) (((float) imgWidth / (float) imgHeight) * (float) newHeight);
            } else {
                newWidth = pictureLabel.getSize().width;
                newHeight = (int) (((float) imgHeight / (float) imgWidth) * (float) newWidth);
            }
            Image dimg = img.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(dimg);
            pictureLabel.setIcon(imageIcon);
        } else {
            pictureLabel.setIcon(new ImageIcon(getClass().getResource("/desktopapplication1/resources/nopicture.jpg")));
        }

        BufferedImage img_erp = null;
        File erp_spectrum = new File(productContent + "\\" + sapWithoutDocs + "\\LR_" + sapWithoutDocs + "_34.jpg");
        if (erp_spectrum.exists()) {
            try {
                img_erp = ImageIO.read(erp_spectrum);
            } catch (IOException e) {
            }
            imgHeight = img_erp.getHeight();
            imgWidth = img_erp.getWidth();
            if ((float) imgHeight / (float) imgWidth > (float) erpSpectrumLabel.getSize().height / (float) erpSpectrumLabel.getSize().width) {
                newHeight = erpSpectrumLabel.getSize().height;
                newWidth = (int) (((float) imgWidth / (float) imgHeight) * (float) newHeight);
            } else {
                newWidth = erpSpectrumLabel.getSize().width;
                newHeight = (int) (((float) imgHeight / (float) imgWidth) * (float) newWidth);
            }
            Image dimg1 = img_erp.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH);
            ImageIcon imageIcon1 = new ImageIcon(dimg1);
            erpSpectrumLabel.setIcon(imageIcon1);
        } else {
            erpSpectrumLabel.setIcon(null);
        }
//        JTextField textFields[] = {lvdTrField, emcTrField, rfTrField, cprCeField, erpTrField, rohsTrField};
//        for (int i = 0; i < textFields.length; i += 1) {
//            if (textFields[i].getText().equals("MISSING")) {
//                textFields[i].setForeground(Color.red);
//            } else {
//                textFields[i].setForeground(Color.black);
//            }
//        }

        if (lvdTrField.getText().contains("MISSING")) {
            lvdTrField.setForeground(Color.red);
        } else {
            lvdTrField.setForeground(Color.black);
        }
        if (emcTrField.getText().contains("MISSING")) {
            emcTrField.setForeground(Color.red);
        } else {
            emcTrField.setForeground(Color.black);
        }
        if (rfTrField.getText().contains("MISSING")) {
            rfTrField.setForeground(Color.red);
        } else {
            rfTrField.setForeground(Color.black);
        }
        if (rfCeField.getText().contains("MISSING")) {
            rfCeField.setForeground(Color.red);
        } else {
            rfCeField.setForeground(Color.black);
        }
        if (cprCeField.getText().contains("MISSING")) {
            cprCeField.setForeground(Color.red);
        } else {
            cprCeField.setForeground(Color.black);
        }
        if (cprTrField.getText().contains("MISSING")) {
            cprTrField.setForeground(Color.red);
        } else {
            cprTrField.setForeground(Color.black);
        }
        if (erpTrField.getText().contains("MISSING")) {
            erpTrField.setForeground(Color.red);
        } else {
            erpTrField.setForeground(Color.black);
        }
        if (rohsTrField.getText().contains("MISSING")) {
            rohsTrField.setForeground(Color.red);
        } else {
            rohsTrField.setForeground(Color.black);
        }
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec") || System.getProperty("user.name").equals("RRemmig")) {
        hiddenCheckBox.setEnabled(true);
        }
    }//GEN-LAST:event_masterTableMousePressed

    private void adaptorCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adaptorCheckBox1ActionPerformed
        if (adaptorCheckBox1.isSelected()) {
            if (adaptorClass1ComboBox.getSelectedIndex() != 1 && adaptorClass1ComboBox.getSelectedIndex() != 3) {
                adaptorClass1ComboBox.setSelectedIndex(2);
            }
        }
    }//GEN-LAST:event_adaptorCheckBox1ActionPerformed

    private void adaptorCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adaptorCheckBox2ActionPerformed
        if (adaptorCheckBox2.isSelected()) {
            if (adaptorClass2ComboBox.getSelectedIndex() != 1 && adaptorClass2ComboBox.getSelectedIndex() != 3) {
                adaptorClass2ComboBox.setSelectedIndex(2);
            }
        }
    }//GEN-LAST:event_adaptorCheckBox2ActionPerformed

    private void cprCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cprCheckBoxActionPerformed
        if (cprCheckBox.isSelected()) {
            cprDirComboBox.setSelectedIndex(0);
            if (cprTrField.getText().isEmpty()) {
                cprTrField.setText("MISSING");
                cprTrField.setForeground(Color.red);
            } else {
                cprTrField.setForeground(Color.black);
            }
            if (cprCeField.getText().isEmpty()) {
                cprCeField.setText("MISSING");
                cprCeField.setForeground(Color.red);
            } else {
                cprCeField.setForeground(Color.black);
            }
        } else {
            cprDirComboBox.setSelectedItem(null);
            if (cprTrField.getText().equals("MISSING")) {
                cprTrField.setText(null);
            }
            if (cprCeField.getText().equals("MISSING")) {
                cprCeField.setText(null);
            }
        }
    }//GEN-LAST:event_cprCheckBoxActionPerformed

    private void photobiolCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_photobiolCheckBoxActionPerformed
        if (photobiolCheckBox.isSelected()) {
            if (photobiolComboBox.getSelectedIndex() != 0) {
                photobiolComboBox.setForeground(Color.RED);
                photobiolComboBox.setSelectedIndex(1);
            } else {
                photobiolComboBox.setForeground(Color.BLACK);
            }
        } else {
            photobiolComboBox.setSelectedItem(null);
        }
    }//GEN-LAST:event_photobiolCheckBoxActionPerformed

    private void qcStatusLabelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_qcStatusLabelPropertyChange
        try {
            Field field = Class.forName("java.awt.Color").getField(qcStatusLabel.getText());
            color = (Color) field.get(null);
            qcStatusLabel.setBackground(color);
            qcStatusLabel.setForeground(color);
        } catch (Exception e) {
            color = null;
        }
    }//GEN-LAST:event_qcStatusLabelPropertyChange

    private void photobiolComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_photobiolComboBoxActionPerformed
        if (photobiolComboBox.getSelectedIndex() == 1) {
            photobiolComboBox.setForeground(Color.RED);
        } else {
            photobiolComboBox.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_photobiolComboBoxActionPerformed

    private void ipComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipComboBoxActionPerformed
        if (ipComboBox.getSelectedIndex() == 2) {
            ipComboBox.setForeground(Color.RED);
        } else {
            ipComboBox.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_ipComboBoxActionPerformed

    private void ipCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ipCheckBoxActionPerformed
        if (ipCheckBox.isSelected()) {
            if (ipComboBox.getSelectedIndex() != 0 || ipComboBox.getSelectedIndex() != 1) {
                ipComboBox.setForeground(Color.RED);
                ipComboBox.setSelectedIndex(2);
            } else {
                ipComboBox.setForeground(Color.BLACK);
            }
        } else {
            ipComboBox.setSelectedItem(null);
        }
    }//GEN-LAST:event_ipCheckBoxActionPerformed

    private void erpDirComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_erpDirComboBoxActionPerformed
        if (erpDirComboBox.getSelectedIndex() == 4) {
            erpDirComboBox.setFont(new Font("Tahoma", Font.PLAIN, 10));
        } else {
            erpDirComboBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
        }
    }//GEN-LAST:event_erpDirComboBoxActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        if (componentStatLabel1.getText() == null) {
            if (lvdTrField.getText().contains("MISSING") || emcTrField.getText().contains("MISSING") || rfTrField.getText().contains("MISSING")
                    || cprTrField.getText().contains("MISSING") || erpTrField.getText().contains("MISSING")) {
                qcStatusLabel.setText("RED");

            } else if (!lvdTrField.getText().contains("MISSING") && !emcTrField.getText().contains("MISSING") && !rfTrField.getText().contains("MISSING")
                    && !cprTrField.getText().contains("MISSING") && !erpTrField.getText().contains("MISSING") && photobiolComboBox.getSelectedIndex() != 1
                    && ipComboBox.getSelectedIndex() != 2 && erpStatusComboBox.getSelectedIndex()!=1 && !rohsTrField.getText().contains("MISSING") && pahCeComboBox.getSelectedIndex() != 2
                    && reachCeComboBox.getSelectedIndex() != 2 && batt1TrComboBox.getSelectedIndex() != 4 && batt2TrComboBox.getSelectedIndex() != 4) {
                qcStatusLabel.setText("GREEN");

            } else if (!lvdTrField.getText().contains("MISSING") && !emcTrField.getText().contains("MISSING") && !rfTrField.getText().contains("MISSING")
                    && !cprTrField.getText().contains("MISSING") && !erpTrField.getText().contains("MISSING")
                    && (photobiolComboBox.getSelectedIndex() == 1 || erpStatusComboBox.getSelectedIndex()==1 || ipComboBox.getSelectedIndex() == 2 || rohsTrField.getText().contains("MISSING")
                    || pahCeComboBox.getSelectedIndex() == 2 || reachCeComboBox.getSelectedIndex() == 2 || batt1TrComboBox.getSelectedIndex() == 4
                    || batt2TrComboBox.getSelectedIndex() == 4)) {
                qcStatusLabel.setText("ORANGE");
            }
        } else {
            if ((componentStatLabel1.getText() != null && componentStatLabel1.getText().equals("RED"))
                    || (componentStatLabel2.getText() != null && componentStatLabel2.getText().equals("RED"))
                    || (componentStatLabel3.getText() != null && componentStatLabel3.getText().equals("RED"))
                    || (componentStatLabel4.getText() != null && componentStatLabel4.getText().equals("RED"))
                    || (componentStatLabel5.getText() != null && componentStatLabel5.getText().equals("RED"))
                    || (componentStatLabel6.getText() != null && componentStatLabel6.getText().equals("RED"))
                    || (componentStatLabel7.getText() != null && componentStatLabel7.getText().equals("RED"))
                    || (componentStatLabel8.getText() != null && componentStatLabel8.getText().equals("RED"))
                    || (componentStatLabel9.getText() != null && componentStatLabel9.getText().equals("RED"))
                    || (componentStatLabel10.getText() != null && componentStatLabel10.getText().equals("RED"))) {
                qcStatusLabel.setText("RED");
            } else if ((componentStatLabel1.getText() != null && componentStatLabel1.getText().equals("ORANGE"))
                    || (componentStatLabel2.getText() != null && componentStatLabel2.getText().equals("ORANGE"))
                    || (componentStatLabel3.getText() != null && componentStatLabel3.getText().equals("ORANGE"))
                    || (componentStatLabel4.getText() != null && componentStatLabel4.getText().equals("ORANGE"))
                    || (componentStatLabel5.getText() != null && componentStatLabel5.getText().equals("ORANGE"))
                    || (componentStatLabel6.getText() != null && componentStatLabel6.getText().equals("ORANGE"))
                    || (componentStatLabel7.getText() != null && componentStatLabel7.getText().equals("ORANGE"))
                    || (componentStatLabel8.getText() != null && componentStatLabel8.getText().equals("ORANGE"))
                    || (componentStatLabel9.getText() != null && componentStatLabel9.getText().equals("ORANGE"))
                    || (componentStatLabel10.getText() != null && componentStatLabel10.getText().equals("ORANGE"))) {
                qcStatusLabel.setText("ORANGE");
            } else {
                qcStatusLabel.setText("GREEN");
            }
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void docWordButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_docWordButtonActionPerformed
        if (System.getProperty("user.name").equals("AMaslowiec") || System.getProperty("user.name").equals("AMAslowiec") || System.getProperty("user.name").equals("ANetten")
                || System.getProperty("user.name").equals("Anetten") || System.getProperty("user.name").equals("RvanKasteren") || System.getProperty("user.name").equals("jyan")
                || System.getProperty("user.name").equals("RvanDommelen")) {
            String itemNo = itemField.getText();
            String sapNo = sapField.getText();
            Date Date = DocDateChooser.getDate();

            createDocWORD(itemNo, Date, sapNo);

        } else {
            JOptionPane.showMessageDialog(null, "You have no rights to make a DoC", "No privileges", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_docWordButtonActionPerformed

    private void authorityReportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authorityReportMenuActionPerformed
        Authority export = new Authority();
        export.CreateExcel();
    }//GEN-LAST:event_authorityReportMenuActionPerformed

    private void exportOverviewReportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportOverviewReportMenuActionPerformed
        Update_status export = new Update_status();
        try {
            export.CreateExcel();
        } catch (IOException ex) {
            Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DesktopApplication1View.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_exportOverviewReportMenuActionPerformed

    private void openOverviewReportMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openOverviewReportMenuActionPerformed
        Desktop desktop = null;

        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }

        File file = new File("G://QC//CERTIFICATION OVERVIEW 2015.xlsx");
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(Authority.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_openOverviewReportMenuActionPerformed

    private void batt1TrCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batt1TrCheckBoxActionPerformed
        if (batt1TrCheckBox.isSelected()) {
            if (batt1TrComboBox.getSelectedIndex() != 0 || batt1TrComboBox.getSelectedIndex() != 1
                    || batt1TrComboBox.getSelectedIndex() != 2 || batt1TrComboBox.getSelectedIndex() != 3 || batt1TrComboBox.getSelectedIndex() != 4) {
                batt1TrComboBox.setForeground(Color.RED);
                batt1TrComboBox.setSelectedIndex(5);
            } else {
                batt1TrComboBox.setForeground(Color.BLACK);
            }
        } else {

            batt1TrComboBox.setSelectedItem(null);
        }
    }//GEN-LAST:event_batt1TrCheckBoxActionPerformed

    private void batt2TrCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batt2TrCheckBoxActionPerformed
        if (batt2TrCheckBox.isSelected()) {
            if (batt2TrComboBox.getSelectedIndex() != 0 || batt2TrComboBox.getSelectedIndex() != 1
                    || batt2TrComboBox.getSelectedIndex() != 2 || batt2TrComboBox.getSelectedIndex() != 3 || batt2TrComboBox.getSelectedIndex() != 4) {
                batt2TrComboBox.setForeground(Color.RED);
                batt2TrComboBox.setSelectedIndex(5);
            } else {
                batt2TrComboBox.setForeground(Color.BLACK);
            }
        } else {

            batt2TrComboBox.setSelectedItem(null);
        }
    }//GEN-LAST:event_batt2TrCheckBoxActionPerformed

    private void kelvinComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kelvinComboBoxActionPerformed
        kelvinComboBox.setFocusTraversalKeysEnabled(false);
        if (kelvinComboBox.getSelectedIndex() > 0) {
            switch (kelvinComboBox.getSelectedIndex()) {
                case 1:
                    colorTextField.setText("Warm White");
                    break;
                case 2:
                    colorTextField.setText("Warm White");
                    break;
                case 3:
                    colorTextField.setText("Warm White");
                    break;
                case 4:
                    colorTextField.setText("Warm White");
                    break;
                case 5:
                    colorTextField.setText("Warm White");
                    break;
                case 6:
                    colorTextField.setText("Warm White");
                    break;
                case 7:
                    colorTextField.setText("Natural White");
                    break;
                case 8:
                    colorTextField.setText("Natural White");
                    break;
                case 9:
                    colorTextField.setText("Cool White");
                    break;
                case 10:
                    colorTextField.setText("Cool White");
                    break;
                case 11:
                    colorTextField.setText("Cool White");
                    break;
                case 12:
                    colorTextField.setText("Cool White");
                    break;    
                case 13:
                    colorTextField.setText("RGB");
                    break;
            }
            enclasComboBox.grabFocus();
        }
    }//GEN-LAST:event_kelvinComboBoxActionPerformed
    public int getIndexByname(String pSap) {
        for (Items _item : list) {
            if (_item.getSap().equals(pSap)) {
                return list.indexOf(_item);
            }
        }
        return -1;
    }

    public Color getColorByName(String pCol) {
        try {
            Field field = Class.forName("java.awt.Color").getField(pCol);
            color = (Color) field.get(null);
        } catch (Exception e) {
            color = null;
        }
        return color;
    }
    private void componentSapLabel2PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel2PropertyChange
        try {
            if (componentSapLabel2.getText().length() > 0) {
                componentStatLabel2.setText(list.get(getIndexByname(componentSapLabel2.getText().substring(0, 9))).getQMStatus());
                componentStatLabel2.setBackground(getColorByName(componentStatLabel2.getText()));
                componentStatLabel2.setForeground(getColorByName(componentStatLabel2.getText()));

            }
        } catch (NullPointerException e) {
            componentStatLabel2.setText(null);
            componentStatLabel2.setBackground(new Color(240, 240, 240));
            componentStatLabel2.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel2PropertyChange

    private void componentSapLabel3PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel3PropertyChange
        try {
            if (componentSapLabel3.getText().length() > 0) {
                componentStatLabel3.setText(list.get(getIndexByname(componentSapLabel3.getText().substring(0, 9))).getQMStatus());
                componentStatLabel3.setBackground(getColorByName(componentStatLabel3.getText()));
                componentStatLabel3.setForeground(getColorByName(componentStatLabel3.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel3.setText(null);
            componentStatLabel3.setBackground(new Color(240, 240, 240));
            componentStatLabel3.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel3PropertyChange

    private void componentSapLabel4PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel4PropertyChange
        try {
            if (componentSapLabel4.getText().length() > 0) {
                componentStatLabel4.setText(list.get(getIndexByname(componentSapLabel4.getText().substring(0, 9))).getQMStatus());
                componentStatLabel4.setBackground(getColorByName(componentStatLabel4.getText()));
                componentStatLabel4.setForeground(getColorByName(componentStatLabel4.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel4.setText(null);
            componentStatLabel4.setBackground(new Color(240, 240, 240));
            componentStatLabel4.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel4PropertyChange

    private void componentSapLabel5PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel5PropertyChange
        try {
            if (componentSapLabel5.getText().length() > 0) {
                componentStatLabel5.setText(list.get(getIndexByname(componentSapLabel5.getText().substring(0, 9))).getQMStatus());
                componentStatLabel5.setBackground(getColorByName(componentStatLabel5.getText()));
                componentStatLabel5.setForeground(getColorByName(componentStatLabel5.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel5.setText(null);
            componentStatLabel5.setBackground(new Color(240, 240, 240));
            componentStatLabel5.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel5PropertyChange

    private void componentSapLabel1PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel1PropertyChange
        try {
            if (componentSapLabel1.getText().length() > 0) {
                componentStatLabel1.setText(list.get(getIndexByname(componentSapLabel1.getText().substring(0, 9))).getQMStatus());
                componentStatLabel1.setBackground(getColorByName(componentStatLabel1.getText()));
                componentStatLabel1.setForeground(getColorByName(componentStatLabel1.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel1.setText(null);
            componentStatLabel1.setBackground(new Color(240, 240, 240));
            componentStatLabel1.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel1PropertyChange

    private void componentSapLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel1MouseClicked
        try {
            jFilter.setText(componentSapLabel1.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel1MouseClicked

    private void componentSapLabel2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel2MouseClicked
        try {
            jFilter.setText(componentSapLabel2.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel2MouseClicked

    private void componentSapLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel3MouseClicked
        try {
            jFilter.setText(componentSapLabel3.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel3MouseClicked

    private void componentSapLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel4MouseClicked
        try {
            jFilter.setText(componentSapLabel4.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel4MouseClicked

    private void componentSapLabel5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel5MouseClicked
        try {
            jFilter.setText(componentSapLabel5.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel5MouseClicked

    private void componentSapLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel6MouseClicked
        try {
            jFilter.setText(componentSapLabel6.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel6MouseClicked

    private void componentSapLabel6PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel6PropertyChange
        try {
            if (componentSapLabel6.getText().length() > 0) {
                componentStatLabel6.setText(list.get(getIndexByname(componentSapLabel6.getText().substring(0, 9))).getQMStatus());
                componentStatLabel6.setBackground(getColorByName(componentStatLabel6.getText()));
                componentStatLabel6.setForeground(getColorByName(componentStatLabel6.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel6.setText(null);
            componentStatLabel6.setBackground(new Color(240, 240, 240));
            componentStatLabel6.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel6PropertyChange

    private void componentSapLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel7MouseClicked
        try {
            jFilter.setText(componentSapLabel7.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel7MouseClicked

    private void componentSapLabel7PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel7PropertyChange
        try {
            if (componentSapLabel7.getText().length() > 0) {
                componentStatLabel7.setText(list.get(getIndexByname(componentSapLabel7.getText().substring(0, 9))).getQMStatus());
                componentStatLabel7.setBackground(getColorByName(componentStatLabel7.getText()));
                componentStatLabel7.setForeground(getColorByName(componentStatLabel7.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel7.setText(null);
            componentStatLabel7.setBackground(new Color(240, 240, 240));
            componentStatLabel7.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel7PropertyChange

    private void componentSapLabel8MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel8MouseClicked
        try {
            jFilter.setText(componentSapLabel8.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel8MouseClicked

    private void componentSapLabel8PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel8PropertyChange
        try {
            if (componentSapLabel8.getText().length() > 0) {
                componentStatLabel8.setText(list.get(getIndexByname(componentSapLabel8.getText().substring(0, 9))).getQMStatus());
                componentStatLabel8.setBackground(getColorByName(componentStatLabel8.getText()));
                componentStatLabel8.setForeground(getColorByName(componentStatLabel8.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel8.setText(null);
            componentStatLabel8.setBackground(new Color(240, 240, 240));
            componentStatLabel8.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel8PropertyChange

    private void componentSapLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel9MouseClicked
        try {
            jFilter.setText(componentSapLabel9.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel9MouseClicked

    private void componentSapLabel9PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel9PropertyChange
        try {
            if (componentSapLabel9.getText().length() > 0) {
                componentStatLabel9.setText(list.get(getIndexByname(componentSapLabel9.getText().substring(0, 9))).getQMStatus());
                componentStatLabel9.setBackground(getColorByName(componentStatLabel9.getText()));
                componentStatLabel9.setForeground(getColorByName(componentStatLabel9.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel9.setText(null);
            componentStatLabel9.setBackground(new Color(240, 240, 240));
            componentStatLabel9.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel9PropertyChange

    private void componentSapLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_componentSapLabel10MouseClicked
        try {
            jFilter.setText(componentSapLabel10.getText().substring(0, 9));
        } catch (NullPointerException e) {
        }
    }//GEN-LAST:event_componentSapLabel10MouseClicked

    private void componentSapLabel10PropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_componentSapLabel10PropertyChange
        try {
            if (componentSapLabel10.getText().length() > 0) {
                componentStatLabel10.setText(list.get(getIndexByname(componentSapLabel10.getText().substring(0, 9))).getQMStatus());
                componentStatLabel10.setBackground(getColorByName(componentStatLabel10.getText()));
                componentStatLabel10.setForeground(getColorByName(componentStatLabel10.getText()));
            }
        } catch (NullPointerException e) {
            componentStatLabel10.setText(null);
            componentStatLabel10.setBackground(new Color(240, 240, 240));
            componentStatLabel10.setForeground(new Color(240, 240, 240));
        }
    }//GEN-LAST:event_componentSapLabel10PropertyChange

    private void hiddenCheckBoxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_hiddenCheckBoxStateChanged

        Component[] components = mainPanel.getComponents();

        if (hiddenCheckBox.isSelected()) {
            hiddenCheckBox.setText("DEACTIVATED");

            remarksTextArea.setEditable(false);
            for (int i = 0; i < components.length; i++) {
                if ((components[i] instanceof JComboBox || components[i] instanceof JTextField || components[i] instanceof JTabbedPane) && components[i].getName() != "jFilter") {
//                if (components[i].getName() != "masterTable" && components[i].getName() != "jFilter" && components[i].getName() !="pictureLabel" && components[i].getName() !="qcStatusLabel") {
                    components[i].setEnabled(false);
                }
            }
        } else {
            hiddenCheckBox.setText(null);

            remarksTextArea.setEditable(true);
            for (int i = 0; i < components.length; i++) {
                components[i].setEnabled(true);
            }
        }
    }//GEN-LAST:event_hiddenCheckBoxStateChanged

    private void createDocPDF(String itemNo, Date Date, String sapNo) {
        String sapWithoutDocs = sapNo.replace(".", "");
        DateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");

        File newDocFile = new File(productContent + "\\" + sapWithoutDocs + "\\DoC_" + sapWithoutDocs + "_" + DateFormat.format(Date).toString() + ".pdf");

        try {
            con = Utils.getConnection();

            FileInputStream fis = new FileInputStream("G:\\QC\\Database\\Items\\Reports\\DoC.jrxml");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);

            Map map = new HashMap();
            map.put("itemNo", itemNo);
            map.put("Date", Date);
            map.put("sapNo", sapNo);

            JasperReport jasperReport = JasperCompileManager.compileReport(bufferedInputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, con);
//            JasperViewer.viewReport(jasperPrint, false);

            JasperExportManager.exportReportToPdfFile(jasperPrint, newDocFile.toString());

            
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            desktop.open(newDocFile);

        } catch (Exception X) {
        } finally {
            Utils.closeDB(rs, st, con);
        }
    }

    private void createDocWORD(String itemNo, Date Date, String sapNo) {
        String desktopPath = System.getProperty("user.home") + "\\" + "Desktop";
        String newDocFile = desktopPath + "\\" + itemNo + "_DoC.docx";

        try {
            con = Utils.getConnection();
            
            FileInputStream fis = new FileInputStream("G:\\QC\\Database\\Items\\Reports\\DoC.jrxml");
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fis);

            Map map = new HashMap();
            map.put("itemNo", itemNo);
            map.put("Date", Date);
            map.put("sapNo", sapNo);

            JasperReport jasperReport = (JasperReport) JasperCompileManager.compileReport(bufferedInputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, map, con);
//          JasperViewer.viewReport(jasperPrint, false);
//            JRRtfExporter exporter = new JRRtfExporter();
//            exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrint);
//            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, newDocFile);
//            exporter.exportReport();

            JRDocxExporter exporter = new JRDocxExporter();
            exporter.setParameter(JRDocxExporterParameter.OUTPUT_FILE_NAME, newDocFile);
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.exportReport();

            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
            }
            desktop.open(new File(newDocFile));


        } catch (Exception X) {
        } finally {
            Utils.closeDB(rs, st, con);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField BosecCeField;
    private com.toedter.calendar.JDateChooser BosecDateChooser;
    private javax.swing.JLabel BosecDateLabel;
    private javax.swing.JPanel CertPanel;
    private com.toedter.calendar.JDateChooser DocDateChooser;
    private com.toedter.calendar.JDateChooser EmcDateChooser;
    private com.toedter.calendar.JDateChooser EupDateChooser;
    private javax.swing.JPanel ExtraInfoPanel;
    private com.toedter.calendar.JDateChooser GsDateChooser;
    private javax.swing.JTextField KomoCeField;
    private com.toedter.calendar.JDateChooser KomoDateChooser;
    private javax.swing.JLabel KomoDateLabel;
    private javax.swing.JLabel ModDateField;
    private javax.swing.JLabel ModWhoField;
    private javax.swing.JTextField NfCeField;
    private com.toedter.calendar.JDateChooser NfDateChooser;
    private javax.swing.JTextField NfTrField;
    private com.toedter.calendar.JDateChooser OemDateFromChooser;
    private com.toedter.calendar.JDateChooser OemDateToChooser;
    private javax.swing.JPanel PackInfoPanel;
    private javax.swing.JLabel PowerInfoLabel;
    private javax.swing.JTextField RFNBNField;
    private javax.swing.JTextField RFfField;
    private javax.swing.JLabel RFfLabel;
    private com.toedter.calendar.JDateChooser RohsDateChooser;
    private com.toedter.calendar.JDateChooser RtteDateChooser;
    private javax.swing.JLabel SupplierItemlabel;
    private javax.swing.JTextField VdsCeField;
    private com.toedter.calendar.JDateChooser VdsDateChooser;
    private javax.swing.JLabel VdsDateLabel;
    private javax.swing.JTextField VdsTrField;
    private javax.swing.JPanel WebInfoPanel;
    private javax.swing.JCheckBox accentPackCheckBox;
    private javax.swing.JCheckBox adaptorCheckBox1;
    private javax.swing.JCheckBox adaptorCheckBox2;
    private javax.swing.JComboBox adaptorClass1ComboBox;
    private javax.swing.JLabel adaptorClass1Logo;
    private javax.swing.JComboBox adaptorClass2ComboBox;
    private javax.swing.JLabel adaptorClass2Logo;
    private javax.swing.JLabel adaptorClassLabel;
    private javax.swing.JTextField adaptorInAmp1TextField;
    private javax.swing.JTextField adaptorInAmp2TextField;
    private javax.swing.JLabel adaptorInAmpLabel;
    private javax.swing.JTextField adaptorInPlug1TextField;
    private javax.swing.JTextField adaptorInPlug2TextField;
    private javax.swing.JLabel adaptorInPlugLabel;
    private javax.swing.JComboBox adaptorInVolt1ComboBox;
    private javax.swing.JComboBox adaptorInVolt2ComboBox;
    private javax.swing.JLabel adaptorInVoltLabel;
    private javax.swing.JLabel adaptorLabel;
    private javax.swing.JTextField adaptorOutAmp1TextField;
    private javax.swing.JTextField adaptorOutAmp2TextField;
    private javax.swing.JLabel adaptorOutAmpLabel;
    private javax.swing.JTextField adaptorOutPlug1TextField1;
    private javax.swing.JLabel adaptorOutPlugLabel;
    private javax.swing.JTextField adaptorOutVolt1TextField;
    private javax.swing.JTextField adaptorOutVolt2TextField;
    private javax.swing.JLabel adaptorOutVoltLabel;
    private javax.swing.JSeparator adaptorSeparator;
    private javax.swing.JComboBox adaptorType1ComboBox;
    private javax.swing.JComboBox adaptorType2ComboBox;
    private javax.swing.JLabel adaptorTypeLabel;
    private javax.swing.JLabel amperLabel;
    private javax.swing.JTextField amperTextField;
    private javax.swing.JLabel angleRatedLabel;
    private javax.swing.JTextField angleRatedTextField;
    private javax.swing.JLabel authorityLabel;
    private javax.swing.JMenuItem authorityReportMenu;
    private javax.swing.JScrollPane authorityScrollPane;
    private javax.swing.JTextArea authorityTextArea;
    private javax.swing.JCheckBox batt1CheckBox;
    private javax.swing.JLabel batt1InclLabel;
    private javax.swing.JLabel batt1Label;
    private javax.swing.JCheckBox batt1TrCheckBox;
    private javax.swing.JComboBox batt1TrComboBox;
    private javax.swing.JCheckBox batt2CheckBox;
    private javax.swing.JLabel batt2InclLabel;
    private javax.swing.JCheckBox batt2TrCheckBox;
    private javax.swing.JComboBox batt2TrComboBox;
    private javax.swing.JCheckBox battAccu1CheckBox;
    private javax.swing.JCheckBox battAccu2CheckBox;
    private javax.swing.JTextField battBrand1TextField;
    private javax.swing.JTextField battBrand2TextField;
    private javax.swing.JLabel battBrandLabel;
    private javax.swing.JTextField battCap1TextField;
    private javax.swing.JTextField battCap2TextField;
    private javax.swing.JLabel battCapLabel;
    private javax.swing.JTextField battQua1TextField;
    private javax.swing.JTextField battQua2TextField;
    private javax.swing.JLabel battQuaLabel;
    private javax.swing.JCheckBox battRepl1CheckBox;
    private javax.swing.JCheckBox battRepl2CheckBox;
    private javax.swing.JComboBox battSize1ComboBox;
    private javax.swing.JComboBox battSize2ComboBox;
    private javax.swing.JLabel battSizeLabel;
    private javax.swing.JComboBox battType1ComboBox;
    private javax.swing.JComboBox battType2ComboBox;
    private javax.swing.JLabel battTypeLabel;
    private javax.swing.JTextField battVolt1TextField;
    private javax.swing.JTextField battVolt2TextField;
    private javax.swing.JLabel battVoltLabel;
    private javax.swing.JLabel beamLabel;
    private javax.swing.JTextField beamTextField;
    private javax.swing.JCheckBox bosecCheckBox;
    private javax.swing.JComboBox brandComboBox;
    private javax.swing.JTextField bulbTextField;
    public javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel candelaLabel;
    private javax.swing.JTextField candelaTextField;
    private javax.swing.JCheckBox cdfCheckBox;
    private javax.swing.JLabel ceLabel;
    private com.toedter.calendar.JDateChooser checkingDateChooser1;
    private javax.swing.JLabel checkingLabel1;
    private javax.swing.JButton cleanBosecDateButton;
    private javax.swing.JButton cleanCprDateButton;
    private javax.swing.JButton cleanEmcDateButton;
    private javax.swing.JButton cleanErpDateButton;
    private javax.swing.JButton cleanGsDateToButton;
    private javax.swing.JButton cleanKkDateButton1;
    private javax.swing.JButton cleanKomoDateButton;
    private javax.swing.JButton cleanNfDateToButton;
    private javax.swing.JButton cleanOemDateFromButton;
    private javax.swing.JButton cleanOemDateToButton;
    private javax.swing.JButton cleanOtherDateButton;
    private javax.swing.JButton cleanReceived6000hButton;
    private javax.swing.JButton cleanRfDateButton;
    private javax.swing.JButton cleanRohsDateButton;
    private javax.swing.JButton cleanStart6000hButton;
    private javax.swing.JButton cleanVdsDateButton;
    private javax.swing.JLabel colorConsLabel;
    private javax.swing.JTextField colorConsTextField;
    private javax.swing.JLabel colorLabel;
    private javax.swing.JTextField colorTextField;
    private javax.swing.JLabel comparLabel;
    private javax.swing.JTextField comparTextField;
    private javax.swing.JLabel componentSapLabel1;
    private javax.swing.JLabel componentSapLabel10;
    private javax.swing.JLabel componentSapLabel2;
    private javax.swing.JLabel componentSapLabel3;
    private javax.swing.JLabel componentSapLabel4;
    private javax.swing.JLabel componentSapLabel5;
    private javax.swing.JLabel componentSapLabel6;
    private javax.swing.JLabel componentSapLabel7;
    private javax.swing.JLabel componentSapLabel8;
    private javax.swing.JLabel componentSapLabel9;
    private javax.swing.JLabel componentStatLabel1;
    private javax.swing.JLabel componentStatLabel10;
    private javax.swing.JLabel componentStatLabel2;
    private javax.swing.JLabel componentStatLabel3;
    private javax.swing.JLabel componentStatLabel4;
    private javax.swing.JLabel componentStatLabel5;
    private javax.swing.JLabel componentStatLabel6;
    private javax.swing.JLabel componentStatLabel7;
    private javax.swing.JLabel componentStatLabel8;
    private javax.swing.JLabel componentStatLabel9;
    private javax.swing.JLabel componentsLabel;
    private javax.swing.JPanel componentsPanel;
    private javax.swing.JButton copyButton;
    private javax.swing.JTextField cpd1Field;
    private javax.swing.JTextField cpd2Field;
    private javax.swing.JTextField cpd3Field;
    private javax.swing.JTextField cpd4Field;
    private javax.swing.JLabel cpdLabel;
    private javax.swing.JTextField cprCeField;
    private javax.swing.JCheckBox cprCheckBox;
    private com.toedter.calendar.JDateChooser cprDateChooser;
    private javax.swing.JComboBox cprDirComboBox;
    private javax.swing.JComboBox cprNBComboBox;
    private javax.swing.JTextField cprTrField;
    private javax.swing.JLabel dateTestLabel1;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField descrDEField1;
    private javax.swing.JTextField descrENField1;
    private javax.swing.JTextField descrESField;
    private javax.swing.JTextField descrFRField1;
    private javax.swing.JTextField descrNLField;
    private javax.swing.JTextField descrPLField;
    private javax.swing.JLabel dimension_dLabel;
    private javax.swing.JTextField dimension_dTextField;
    private javax.swing.JLabel dimension_fiLabel;
    private javax.swing.JTextField dimension_fiTextField;
    private javax.swing.JLabel dimension_lLabel;
    private javax.swing.JTextField dimension_lTextField;
    private javax.swing.JComboBox dimmerComboBox;
    private javax.swing.JLabel dimmerLabel;
    private javax.swing.JLabel directiveLabel;
    private javax.swing.JCheckBox docCheckBox;
    private javax.swing.JButton docPdfButton;
    private javax.swing.JButton docWordButton;
    private javax.swing.JCheckBox doiCheckBox;
    private javax.swing.JLabel doorchimesInfoLabel;
    private javax.swing.JLabel doorchimesSoundLabel;
    private javax.swing.JComboBox doorchimesTempComboBox;
    private javax.swing.JLabel doorchimesTempLabel;
    private javax.swing.JLabel eanLabel;
    private javax.swing.JTextField eanTextField;
    private javax.swing.JTextField emc10Field;
    private javax.swing.JTextField emc1Field;
    private javax.swing.JTextField emc2Field;
    private javax.swing.JTextField emc3Field;
    private javax.swing.JTextField emc4Field;
    private javax.swing.JTextField emc5Field;
    private javax.swing.JTextField emc6Field;
    private javax.swing.JTextField emc7Field;
    private javax.swing.JTextField emc8Field;
    private javax.swing.JTextField emc9Field;
    private javax.swing.JTextField emcCeField;
    private javax.swing.JCheckBox emcCheckBox;
    private javax.swing.JComboBox emcDirComboBox;
    private javax.swing.JLabel emcLabel;
    private javax.swing.JTextField emcTrField;
    private javax.swing.JComboBox enclasComboBox;
    private javax.swing.JLabel enclasLabel;
    private javax.persistence.EntityManager entityManager;
    private javax.swing.JCheckBox erpCheckBox;
    private javax.swing.JComboBox erpDirComboBox;
    private javax.swing.JPanel erpPanel;
    private javax.swing.JLabel erpSpectrumLabel;
    private javax.swing.JComboBox erpStatusComboBox;
    private javax.swing.JTextField erpTrField;
    private javax.swing.JButton exportButton;
    private javax.swing.JMenuItem exportOverviewReportMenu;
    private javax.swing.JLabel extraLabel;
    private javax.swing.JComboBox fittinComboBox;
    private javax.swing.JLabel fitttinLabel;
    private javax.swing.JCheckBox fluxCheckBox;
    private javax.swing.JComboBox fluxTrComboBox;
    private javax.swing.JButton folderButton;
    private javax.swing.JTextField gsCeField;
    private javax.swing.JCheckBox gsCheckBox;
    private javax.swing.JLabel gsNbLabel;
    private javax.swing.JTextField gsTrField;
    private javax.swing.JLabel herarchyLabel1;
    private javax.swing.JCheckBox hiddenCheckBox;
    private javax.swing.JComboBox hierarchyComboBox;
    private javax.swing.JLabel hierarchyLabel;
    private javax.swing.JCheckBox inclCheckBox;
    private javax.swing.JComboBox indoorOutdoorComboBox;
    private javax.swing.JLabel indoorOutdoorLabel;
    private javax.swing.JCheckBox ipCheckBox;
    private javax.swing.JComboBox ipComboBox;
    private javax.swing.JTextField itemField;
    private javax.swing.JLabel itemLabel;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jEMCNBComboBox;
    private javax.swing.JTextField jFilter;
    private javax.swing.JComboBox jGSNBComboBox;
    private javax.swing.JLabel jItemCountLabel;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JComboBox jRFNBComboBox;
    private javax.swing.JMenu jReportMenu;
    private javax.swing.JComboBox jRoHSNBComboBox;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox jStatusComboBox;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JComboBox kelvinComboBox;
    private javax.swing.JLabel kelvinLabel;
    private javax.swing.JComboBox kindBulbComboBox;
    private javax.swing.JLabel kindBulbLabel;
    private javax.swing.JTextField kindLEDTextField;
    private javax.swing.JLabel kindLedLabel;
    private javax.swing.JCheckBox komoCheckBox;
    private javax.swing.JLabel kwikLabel;
    private javax.swing.JTextField kwikTextField;
    private javax.swing.JLabel lifetimeLabel;
    private javax.swing.JLabel lifetimeRatedLabel;
    private javax.swing.JTextField lifetimeRatedTextField;
    private javax.swing.JComboBox lightBulb1ComboBox;
    private javax.swing.JLabel lightBulb1Label;
    private javax.swing.JComboBox lightBulb2ComboBox;
    private javax.swing.JLabel lightBulb2Label;
    private javax.swing.JLabel lightClassLabel;
    private javax.swing.JTextField lightClassTextField;
    private javax.swing.JComboBox lightFixtureComboBox;
    private javax.swing.JLabel lightFixtureLabel;
    private javax.swing.JLabel lightInfoLabel;
    private javax.swing.JSeparator lightSeparator;
    private javax.swing.JLabel lightSizeLabel;
    private javax.swing.JTextField lightSizeTextField;
    private javax.swing.JTextField lightWatt1Field;
    private javax.swing.JLabel lightWatt1Label;
    private javax.swing.JLabel lightWatt2Label;
    private javax.swing.JTextField lightWatt2TextField;
    private java.util.List<desktopapplication1.Items> list;
    private javax.swing.JComboBox livetimeComboBox;
    private javax.swing.JCheckBox logoCECheckBox;
    private javax.swing.JLabel logoLabel;
    private javax.swing.JLabel logoNBNLabel;
    private javax.swing.JCheckBox logoWeeeCheckBox;
    private javax.swing.JLabel lsfLabel;
    private javax.swing.JTextField lsfTextField;
    private javax.swing.JLabel lumenFactorLabel;
    private javax.swing.JLabel lumenLabel;
    private javax.swing.JLabel lumenRatedLabel;
    private javax.swing.JTextField lumenTextField;
    private javax.swing.JTextField lumenfactorTextField;
    private javax.swing.JTextField lumenratedTextField;
    private javax.swing.JTextField lvd1Field;
    private javax.swing.JTextField lvd2Field;
    private javax.swing.JTextField lvd3Field;
    private javax.swing.JTextField lvd4Field;
    private javax.swing.JTextField lvd5Field;
    private javax.swing.JTextField lvd6Field;
    private javax.swing.JTextField lvd7Field;
    private javax.swing.JTextField lvd8Field;
    private javax.swing.JTextField lvd9Field;
    private javax.swing.JTextField lvdCeField;
    private javax.swing.JCheckBox lvdCheckBox;
    private javax.swing.JButton lvdCleanDateButton;
    private com.toedter.calendar.JDateChooser lvdDateChooser;
    private javax.swing.JComboBox lvdDirComboBox;
    private javax.swing.JLabel lvdLabel;
    private javax.swing.JComboBox lvdNbComboBox;
    private javax.swing.JTextField lvdTrField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JCheckBox mainsCheckBox;
    private javax.swing.JComboBox mainsClassComboBox;
    private javax.swing.JLabel mainsClassLabel;
    private javax.swing.JLabel mainsIPLabel;
    private javax.swing.JTextField mainsIPTextField;
    private javax.swing.JLabel mainsInPlugLabel;
    private javax.swing.JTextField mainsInPlugTextField;
    private javax.swing.JComboBox mainsInVoltComboBox;
    private javax.swing.JLabel mainsInVoltLabel;
    private javax.swing.JLabel mainsInWattLabel;
    private javax.swing.JTextField mainsInWattTextField;
    private javax.swing.JLabel mainsOutPlugLabel;
    private javax.swing.JTextField mainsOutPlugTextField;
    private javax.swing.JLabel mainsOutWattLabel;
    private javax.swing.JTextField mainsOutWattTextField;
    private javax.swing.JSeparator mainsSeparator;
    private javax.swing.JScrollPane masterScrollPane;
    private javax.swing.JTable masterTable;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel nbLabel;
    private javax.swing.JButton newButton;
    private javax.swing.JCheckBox nfCheckBox;
    private javax.swing.JLabel numberLEDLabel;
    private javax.swing.JTextField numberLEDTextField;
    private javax.swing.JTextField oemCeField;
    private javax.swing.JCheckBox oemCheckBox;
    private javax.swing.JLabel oemDateFromLabel;
    private javax.swing.JMenuItem openOverviewReportMenu;
    private javax.swing.JTextField originalItemField;
    private javax.swing.JTextField otherCeField;
    private javax.swing.JCheckBox otherCheckBox;
    private com.toedter.calendar.JDateChooser otherDateChooser;
    private javax.swing.JLabel otherDateLabel;
    private javax.swing.JTextField outputAmp2TextField1;
    private javax.swing.JMenu overviewReportMenu;
    private javax.swing.JLabel packingLabel;
    private javax.swing.JComboBox pahCeComboBox;
    private javax.swing.JCheckBox pahCheckBox;
    private javax.swing.JCheckBox photobiolCheckBox;
    private javax.swing.JComboBox photobiolComboBox;
    private javax.swing.JCheckBox phthCheckBox;
    private javax.swing.JLabel pictureLabel;
    private javax.swing.JLabel powerFactorLabel;
    private javax.swing.JTextField powerFactorTextField;
    private javax.swing.JButton printButton;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JButton productButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel qcStatusLabel;
    private javax.persistence.Query query;
    private javax.swing.JComboBox raComboBox;
    private javax.swing.JLabel raLabel;
    private javax.swing.JComboBox reachCeComboBox;
    private javax.swing.JCheckBox reachCheckBox;
    private com.toedter.calendar.JDateChooser received6000hDateChooser;
    private javax.swing.JButton refreshButton;
    private javax.swing.JScrollPane remarksScrollPane;
    private javax.swing.JTextArea remarksTextArea;
    private javax.swing.JTextField rf1Field;
    private javax.swing.JTextField rf2Field;
    private javax.swing.JTextField rf3Field;
    private javax.swing.JTextField rf4Field;
    private javax.swing.JTextField rfCeField;
    private javax.swing.JCheckBox rfCheckBox;
    private javax.swing.JComboBox rfDirComboBox;
    private javax.swing.JLabel rfLabel;
    private javax.swing.JLabel rfNbNLabel;
    private javax.swing.JTextField rfTrField;
    private javax.swing.JTextField rohsCeField;
    private javax.swing.JCheckBox rohsCheckBox;
    private javax.swing.JComboBox rohsDirComboBox;
    private javax.swing.JTextField rohsTrField;
    private desktopapplication1.RowSorterToStringConverter rowSorterToStringConverter1;
    private javax.swing.JLabel safetyCameraLabel;
    private javax.swing.JLabel safetyIRLabel;
    private javax.swing.JTextField safetyIRTextField;
    private javax.swing.JLabel safetyInfoLabel;
    private javax.swing.JLabel safetyLinesLabel;
    private javax.swing.JTextField safetyLinesTextField;
    private javax.swing.JLabel safetyMonitorLabel;
    private javax.swing.JLabel safetyPixelLabel;
    private javax.swing.JTextField safetyPixelTextField;
    private javax.swing.JComboBox safetyScreenComboBox;
    private javax.swing.JLabel safetyScreenLabel;
    private javax.swing.JComboBox safetySensorComboBox;
    private javax.swing.JLabel safetySensorLabel;
    private javax.swing.JSeparator safetySeparator;
    private javax.swing.JComboBox safetySizeComboBox;
    private javax.swing.JLabel safetySizeLabel;
    private javax.swing.JTextField sapField;
    private javax.swing.JLabel sapLabel;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox shapeComboBox;
    private javax.swing.JLabel shapeLabel;
    private javax.swing.JComboBox specUseComboBox;
    private javax.swing.JLabel specUseLabel;
    private javax.swing.JPanel specsPanel;
    private javax.swing.JLabel spectrumLabel;
    private javax.swing.JPanel standardPanel;
    private javax.swing.JMenuItem standardsReportMenu;
    private javax.swing.JLabel star60Label;
    private javax.swing.JTextField star60TextField;
    private com.toedter.calendar.JDateChooser start6000hDateChooser;
    private javax.swing.JLabel startTimeLabel;
    private javax.swing.JTextField startTimeTextField;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel statusLabel1;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField supplierField;
    private javax.swing.JLabel supplierLabel;
    private javax.swing.JComboBox swicycComboBox;
    private javax.swing.JLabel swicycLabel;
    private javax.swing.JLabel trLabel;
    private javax.swing.JMenu userMenu;
    private javax.swing.JCheckBox uvCheckBox;
    private javax.swing.JLabel validDate1Label;
    private javax.swing.JLabel validFromDateLabel;
    private javax.swing.JCheckBox vdsCheckBox;
    private javax.swing.JTextField vendorField;
    private javax.swing.JLabel vendorLabel;
    private javax.swing.JComboBox voltageComboBox;
    private javax.swing.JLabel voltageLabel;
    private javax.swing.JLabel wattageLabel;
    private javax.swing.JLabel wattageRatedLabel;
    private javax.swing.JTextField wattageRatedTextField;
    private javax.swing.JTextField wattageTextField;
    private javax.swing.JLabel websiteLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private JDialog raport;
    private JDialog raportErp;
    private boolean saveNeeded;
}
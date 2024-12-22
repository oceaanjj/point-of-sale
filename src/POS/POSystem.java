/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package POS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.math.BigDecimal;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;


public class POSystem extends javax.swing.JFrame {

    private int posX = 0, posY = 0;
    private double subtotal = 0.0;

    private static final String URL = "jdbc:mysql://localhost:3306/POS"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "@dmin001"; 
    private final String loggedInUser;


    public POSystem() {
        setUndecorated(true);
        loggedInUser = Login.getLoggedInUsername();
        initComponents();
        initReceiptLabels();
        
        time();
        date();

        Timer timer = new Timer(1000, e -> {
            time();
            date();
        });
        timer.start();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                posX = evt.getX();
                posY = evt.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                setLocation(evt.getXOnScreen() - posX, evt.getYOnScreen() - posY);
            }
        });

        exitt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dispose(); 
            }
        });

        minimizee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setState(JFrame.ICONIFIED);
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                addItem();
            }
        });

        printButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if (jpayment.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter the payment amount.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                double payment = Double.parseDouble(jpayment.getText());
                double change = payment - subtotal;
                jchange.setText(String.valueOf(change));
                generateReceipt(subtotal, payment, change);
                saveSalesData(subtotal, payment, change);
            }
        });

        jpayment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateChange();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteItem();
            }
        });
        
        jdash.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                
                Dashboard dashboard = new Dashboard();
                dashboard.setLocationRelativeTo(null);
                dashboard.setVisible(true);

                
                setVisible(false);
            }
        });
        
        jclassi.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
               
                Classification classification = new Classification();
                classification.setLocationRelativeTo(null);
                classification.setVisible(true);

               
                setVisible(false);
            }
        });
        
        jinventory.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
              
                Inventory inventory = new Inventory();
                inventory.setLocationRelativeTo(null);
                inventory.setVisible(true);

              
                setVisible(false);
            }
        });
        
        jreport.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                
                Report report = new Report();
                report.setLocationRelativeTo(null);
                report.setVisible(true);
       
                setVisible(false);
            }
        });
        
        jpos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
              
                POSystem pos = new POSystem();
                pos.setLocationRelativeTo(null);
                pos.setVisible(true);

               
                setVisible(false);
            }
        });
        
        
        ///////switch accounttt------------------
        jswitch.addMouseListener(new MouseAdapter() {
            Object options[] = {"OK", "CaNCEL"};
            public void mouseClicked(MouseEvent evt) {
                int response = JOptionPane.showConfirmDialog(POSystem.this, "Are you sure you want to SWITCH ACCOUNT?", "CONFIRM", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if(response == JOptionPane.OK_OPTION){
                    JOptionPane.showMessageDialog(null, "Account successfully logged out");
                    Login login = new Login();
                    login.setLocationRelativeTo(null);
                    login.setVisible(true);
                    setVisible(false);
                }
                else{
                    return;
                }
                 
            }
        });
        
        jlog.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int response = JOptionPane.showConfirmDialog(POSystem.this, "Are you sure you want to LOG OUT?", "CONFIRM", 
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if(response == JOptionPane.OK_OPTION){
                    JOptionPane.showMessageDialog(null, "Account successfully logged out");
                    Login login = new Login();
                    login.setLocationRelativeTo(null);
                    login.setVisible(true);
                    setVisible(false);
                }
                else{
                    return;
                }
                
               
            }
            
        });
        
        setLocationRelativeTo(null);
    }

    public void time() {
        DateTimeFormatter times = DateTimeFormatter.ofPattern("hh : mm a");
        LocalDateTime now = LocalDateTime.now();
        time.setText(times.format(now));
    }

    public void date() {
        DateTimeFormatter dates = DateTimeFormatter.ofPattern("MM / dd / yyyy");
        LocalDateTime now = LocalDateTime.now();
        date.setText(dates.format(now));
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Database Driver not found", e);
        }
    }

    private void addItem() {
        String barcode = jbarcode.getText();
        String category = jcategory.getText();
        String brand = jbrand.getText();
        String description = jdescription.getText();
        double unitPrice = Double.parseDouble(junit.getText());
        int quantity = Integer.parseInt(jquantity.getText());

        if (checkInventory(barcode, category, brand, quantity)) {
            updateInventory(barcode, category, brand, quantity);
            updateSubtotal(unitPrice, quantity);
            addToTable(barcode, category, brand, description, unitPrice, quantity);

            // Clear input fields
            jbarcode.setText("");
            jcategory.setText("");
            jbrand.setText("");
            jdescription.setText("");
            junit.setText("");
            jquantity.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Insufficient stock!");
        }
    }

    private boolean checkInventory(String barcode, String category, String brand, int quantity) {
        try (Connection conn = getConnection()) {
            String query = "SELECT qty FROM inventory WHERE barcode = ? AND category = ? AND brand = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, barcode);
            ps.setString(2, category);
            ps.setString(3, brand);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int availableQuantity = rs.getInt("qty");
                return availableQuantity >= quantity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateSubtotal(double unitPrice, int quantity) {
        subtotal += unitPrice * quantity;
        jsubtotals.setText(String.valueOf(subtotal));
    }

    private void updateInventory(String barcode, String category, String brand, int quantity) {
        try (Connection conn = getConnection()) {
            String query = "UPDATE inventory SET qty = qty - ? WHERE barcode = ? AND category = ? AND brand = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, quantity);
            ps.setString(2, barcode);
            ps.setString(3, category);
            ps.setString(4, brand);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToTable(String barcode, String category, String brand, String description, double unitPrice, int quantity) {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.addRow(new Object[]{barcode, category, brand, description, unitPrice, quantity});
    }

    private void updateChange() {
        double payment = Double.parseDouble(jpayment.getText());
        double change = payment - subtotal;
        jchange.setText(String.valueOf(change));
    }

    private void deleteItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
            double unitPrice = (Double) model.getValueAt(selectedRow, 4);
            int quantity = (Integer) model.getValueAt(selectedRow, 5);

            subtotal -= unitPrice * quantity;
            jsubtotals.setText(String.valueOf(subtotal));

           
            String barcode = (String) model.getValueAt(selectedRow, 0);
            String category = (String) model.getValueAt(selectedRow, 1);
            String brand = (String) model.getValueAt(selectedRow, 2);
            updateInventory(barcode, category, brand, -quantity); 

            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }
    
    public void generateReceipt(double subtotal, double payment, double change) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Specify a file to save");
    fileChooser.setSelectedFile(new File("receipt.pdf"));

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        
        if (!filePath.endsWith(".pdf")) {
            filePath += ".pdf";
        }

        Document document = new Document();
        StringBuilder receiptContent = new StringBuilder();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        LocalDateTime now = LocalDateTime.now();

        String date = dateFormatter.format(now);
        String time = timeFormatter.format(now);

        receiptContent.append("\n\n\n                          MALOU-WANG Supermarket\n");
        receiptContent.append("                         Santo Tomas St. Barangay 34,\n");
        receiptContent.append("                              Maypajo, Caloocan City\n");
        receiptContent.append("                                   +63 9090033456\n");
        receiptContent.append("------------------------------------------------------------------------------\n");
        receiptContent.append("\n                                PURCHASED ITEMS :\n");
        receiptContent.append("------------------------------------------------------------------------------\n");

        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            String barcode = (String) model.getValueAt(i, 0);
            String category = (String) model.getValueAt(i, 1);
            String description = (String) model.getValueAt(i, 3);
            double unitPrice = (Double) model.getValueAt(i, 4);
            int quantity = (Integer) model.getValueAt(i, 5);


            receiptContent.append("     ").append(description).append("                                       ").append(unitPrice).append(" x ").append(quantity).append("\n");
        }

        receiptContent.append("------------------------------------------------------------------------------\n");
        receiptContent.append("     Subtotal: ").append(subtotal).append("\n");
        receiptContent.append("     Cash: ").append(payment).append("\n");
        receiptContent.append("     Change: ").append(change).append("\n");
        receiptContent.append("------------------------------------------------------------------------------\n");
        receiptContent.append("                          Thank you for shopping...!\n");
        receiptContent.append("------------------------------------------------------------------------------\n");
        receiptContent.append("     Date: ").append(date).append("\n");
        receiptContent.append("     Time: ").append(time).append("\n");
        receiptContent.append("------------------------------------------------------------------------------\n");
        receiptContent.append("     Cashier in charge : ").append(loggedInUser).append("\n");
        receiptContent.append("------------------------------------------------------------------------------\n");
        receiptContent.append("\n                      ® MALOU-WANG ® Supermarket\n");
        receiptContent.append("\n                                COPYRIGHT © 2024\n");

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph(receiptContent.toString()));
            document.close();

            JOptionPane.showMessageDialog(null, "Receipt generated successfully.");
            updateReceiptLabels(receiptContent.toString());

        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}


            
            private void updateReceiptLabels(String receiptText) {
                receiptPanel.removeAll(); 

                String[] lines = receiptText.split("\n");
                Font receiptFont = new Font("Arial", Font.BOLD, 14);

                for (String line : lines) {
                    JLabel label = createReceiptLabel(receiptFont);
                    label.setText(line);
                    receiptPanel.add(label);
                }

                receiptPanel.revalidate();
                receiptPanel.repaint();
}

                private void initReceiptLabels() {
                receiptPanel = new JPanel();
                receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));

                Font receiptFont = new Font("Arial", Font.BOLD, 14);

                receiptLabel1 = createReceiptLabel(receiptFont);
                receiptLabel2 = createReceiptLabel(receiptFont);
                receiptLabel3 = createReceiptLabel(receiptFont);
                receiptLabel4 = createReceiptLabel(receiptFont);
                receiptLabel5 = createReceiptLabel(receiptFont);
                receiptLabel6 = createReceiptLabel(receiptFont);
                receiptLabel7 = createReceiptLabel(receiptFont);
                receiptLabel8 = createReceiptLabel(receiptFont);
                receiptLabel9 = createReceiptLabel(receiptFont);
                receiptLabel10 = createReceiptLabel(receiptFont);
                receiptLabel11 = createReceiptLabel(receiptFont);
                receiptLabel12 = createReceiptLabel(receiptFont);
                receiptLabel13 = createReceiptLabel(receiptFont);
                receiptLabel14 = createReceiptLabel(receiptFont);
                receiptLabel15 = createReceiptLabel(receiptFont);
                receiptLabel16 = createReceiptLabel(receiptFont);
                receiptLabel17 = createReceiptLabel(receiptFont);
                receiptLabel18 = createReceiptLabel(receiptFont);
                receiptLabel19 = createReceiptLabel(receiptFont);

                receiptPanel.add(receiptLabel1);
                receiptPanel.add(receiptLabel2);
                receiptPanel.add(receiptLabel3);
                receiptPanel.add(receiptLabel4);
                receiptPanel.add(receiptLabel5);
                receiptPanel.add(receiptLabel6);
                receiptPanel.add(receiptLabel7);
                receiptPanel.add(receiptLabel8);
                receiptPanel.add(receiptLabel9);
                receiptPanel.add(receiptLabel10);
                receiptPanel.add(receiptLabel11);
                receiptPanel.add(receiptLabel12);
                receiptPanel.add(receiptLabel13);
                receiptPanel.add(receiptLabel14);
                receiptPanel.add(receiptLabel15);
                receiptPanel.add(receiptLabel16);
                receiptPanel.add(receiptLabel17);
                receiptPanel.add(receiptLabel18);
                receiptPanel.add(receiptLabel19);

                jScrollPane10.setViewportView(receiptPanel);
            }

    private JLabel createReceiptLabel(Font font) {
        JLabel label = new JLabel();
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.CENTER); 
        return label;
    }
    
private void saveSalesData(double subtotal, double payment, double change) {
    String sql = "INSERT INTO Sales (barcode, category, brand, description, uprice, qty, subtotal, date, time, cashier) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String date = dateFormatter.format(now);
    String time = timeFormatter.format(now);

    try (Connection conn = getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        int rowCount = model.getRowCount();

        
        ps.setString(8, date);
        ps.setString(9, time);  
        ps.setString(10, loggedInUser);  
        for (int i = 0; i < rowCount; i++) {
           
            ps.setString(1, model.getValueAt(i, 0).toString()); 
            ps.setString(2, model.getValueAt(i, 1).toString()); 
            ps.setString(3, model.getValueAt(i, 2).toString()); 
            ps.setString(4, model.getValueAt(i, 3).toString());  
            ps.setBigDecimal(5, new BigDecimal(model.getValueAt(i, 4).toString())); 
            ps.setInt(6, Integer.parseInt(model.getValueAt(i, 5).toString())); 
            ps.setBigDecimal(7, new BigDecimal(subtotal)); 

            
            ps.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Sales data saved successfully!");

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to save sales data.");
    } catch (NumberFormatException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Data format error.");
    } catch (ArrayIndexOutOfBoundsException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Table data error: Index out of bounds.");
    }
}
















    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jdash = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jclassi = new javax.swing.JLabel();
        jinventory = new javax.swing.JLabel();
        jreport = new javax.swing.JLabel();
        jpos = new javax.swing.JLabel();
        jswitch = new javax.swing.JLabel();
        jlog = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        exitt = new javax.swing.JButton();
        minimizee = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        date1 = new javax.swing.JLabel();
        time1 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        printButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jbarcode = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jcategory = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jbrand = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jdescription = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        junit = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jquantity = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jsubtotals = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jpayment = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jchange = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        jScrollPane10 = new javax.swing.JScrollPane();
        receiptPanel = new javax.swing.JPanel();
        receiptLabel1 = new javax.swing.JLabel();
        receiptLabel2 = new javax.swing.JLabel();
        receiptLabel3 = new javax.swing.JLabel();
        receiptLabel4 = new javax.swing.JLabel();
        receiptLabel5 = new javax.swing.JLabel();
        receiptLabel6 = new javax.swing.JLabel();
        receiptLabel7 = new javax.swing.JLabel();
        receiptLabel8 = new javax.swing.JLabel();
        receiptLabel9 = new javax.swing.JLabel();
        receiptLabel10 = new javax.swing.JLabel();
        receiptLabel11 = new javax.swing.JLabel();
        receiptLabel12 = new javax.swing.JLabel();
        receiptLabel13 = new javax.swing.JLabel();
        receiptLabel14 = new javax.swing.JLabel();
        receiptLabel15 = new javax.swing.JLabel();
        receiptLabel16 = new javax.swing.JLabel();
        receiptLabel17 = new javax.swing.JLabel();
        receiptLabel18 = new javax.swing.JLabel();
        receiptLabel19 = new javax.swing.JLabel();

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1353, 738));

        jPanel4.setBackground(new java.awt.Color(25, 16, 47));

        jdash.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jdash.setForeground(new java.awt.Color(255, 255, 255));
        jdash.setText("DASHBOARD");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/DASH-3.png"))); // NOI18N

        jclassi.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jclassi.setForeground(new java.awt.Color(255, 255, 255));
        jclassi.setText("CLASSIFICATION");

        jinventory.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jinventory.setForeground(new java.awt.Color(255, 255, 255));
        jinventory.setText("INVENTORY");

        jreport.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jreport.setForeground(new java.awt.Color(255, 255, 255));
        jreport.setText("REPORT");

        jpos.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jpos.setForeground(new java.awt.Color(255, 255, 255));
        jpos.setText("POINT OF SALES");

        jswitch.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jswitch.setForeground(new java.awt.Color(255, 255, 255));
        jswitch.setText("SWITCH USER");

        jlog.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jlog.setForeground(new java.awt.Color(255, 255, 255));
        jlog.setText("LOG OUT");

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/CLASS-2.png"))); // NOI18N

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/INVENTORY-2.png"))); // NOI18N

        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/REPORT-2.png"))); // NOI18N

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/POS-2.png"))); // NOI18N

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/LOG OUT-2.png"))); // NOI18N

        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/USER-2.png"))); // NOI18N

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/CUTE.gif"))); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlog)
                            .addComponent(jswitch)
                            .addComponent(jpos)
                            .addComponent(jreport)
                            .addComponent(jinventory)
                            .addComponent(jclassi)
                            .addComponent(jdash))))
                .addGap(45, 45, 45))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel15)
                .addGap(77, 77, 77)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdash)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jclassi))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jinventory)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jreport)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpos)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jswitch)
                        .addGap(21, 21, 21)
                        .addComponent(jlog))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel13)))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(208, 175, 208));

        exitt.setBackground(new java.awt.Color(204, 0, 0));
        exitt.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        exitt.setForeground(new java.awt.Color(255, 255, 255));
        exitt.setText("x");
        exitt.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exitt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                exittMouseClicked(evt);
            }
        });
        exitt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exittActionPerformed(evt);
            }
        });

        minimizee.setBackground(new java.awt.Color(0, 102, 0));
        minimizee.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        minimizee.setForeground(new java.awt.Color(255, 255, 255));
        minimizee.setText("__");
        minimizee.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        minimizee.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizeeActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(25, 16, 47));
        jLabel16.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(133, 96, 20));
        jLabel16.setText("POS SYSTEM");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(minimizee, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exitt, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(exitt)
                        .addComponent(minimizee))
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        time.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        time.setForeground(new java.awt.Color(133, 96, 20));
        time.setText("TIME");

        date.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        date.setForeground(new java.awt.Color(133, 96, 20));
        date.setText("DATE");

        date1.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        date1.setForeground(new java.awt.Color(133, 96, 20));
        date1.setText("DATE :");

        time1.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        time1.setForeground(new java.awt.Color(133, 96, 20));
        time1.setText("TIME : ");

        jLabel17.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("POS SYSTEM");

        printButton.setBackground(new java.awt.Color(0, 153, 0));
        printButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        printButton.setForeground(new java.awt.Color(255, 255, 255));
        printButton.setText("PRINT");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        deleteButton.setBackground(new java.awt.Color(255, 51, 0));
        deleteButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        deleteButton.setForeground(new java.awt.Color(255, 255, 255));
        deleteButton.setText("DELETE");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "BARCODE", "CATEGORY", "BRAND", "DESCRIPTION", "PRICE", "QUANTITY"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(itemTable);
        if (itemTable.getColumnModel().getColumnCount() > 0) {
            itemTable.getColumnModel().getColumn(0).setResizable(false);
            itemTable.getColumnModel().getColumn(1).setResizable(false);
            itemTable.getColumnModel().getColumn(2).setResizable(false);
            itemTable.getColumnModel().getColumn(3).setResizable(false);
            itemTable.getColumnModel().getColumn(4).setResizable(false);
            itemTable.getColumnModel().getColumn(5).setResizable(false);
        }

        jScrollPane2.setViewportView(jScrollPane1);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jLabel18.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel18.setText("BARCODE :");

        jLabel19.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel19.setText("CATEGORY :");

        jLabel20.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel20.setText("BRAND :");

        jLabel21.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel21.setText("DESCRIPTION :");

        jLabel22.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel22.setText("UNIT PRICE :");

        jLabel23.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel23.setText("QUANTITY :");

        jLabel24.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel24.setText("SUB-TOTAL :");

        jsubtotals.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jsubtotals.setText("0.0");

        jLabel26.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel26.setText("PAYMENT");

        jpayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jpaymentActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel27.setText("CHANGE");

        jchange.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jchange.setText("0.0");

        addButton.setBackground(new java.awt.Color(0, 153, 0));
        addButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        addButton.setForeground(new java.awt.Color(255, 255, 255));
        addButton.setText("ADD");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        receiptPanel.setBackground(new java.awt.Color(255, 255, 255));
        receiptPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        receiptPanel.setForeground(new java.awt.Color(255, 255, 255));

        receiptLabel3.setText("jLabel1");

        receiptLabel4.setText("jLabel1");

        receiptLabel5.setText("jLabel1");

        receiptLabel6.setText("jLabel1");

        receiptLabel7.setText("jLabel1");

        receiptLabel8.setText("jLabel1");

        receiptLabel9.setText("jLabel1");

        receiptLabel10.setText("jLabel1");

        receiptLabel11.setText("jLabel1");

        receiptLabel12.setText("jLabel1");

        receiptLabel13.setText("jLabel1");

        receiptLabel14.setText("jLabel1");

        receiptLabel15.setText("jLabel1");

        receiptLabel16.setText("jLabel1");

        receiptLabel17.setText("jLabel1");

        receiptLabel18.setText("jLabel1");

        receiptLabel19.setText("jLabel1");

        javax.swing.GroupLayout receiptPanelLayout = new javax.swing.GroupLayout(receiptPanel);
        receiptPanel.setLayout(receiptPanelLayout);
        receiptPanelLayout.setHorizontalGroup(
            receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(receiptPanelLayout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addGroup(receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(receiptPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(receiptLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(receiptPanelLayout.createSequentialGroup()
                        .addGroup(receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(receiptLabel16)
                            .addComponent(receiptLabel15)
                            .addComponent(receiptLabel17)
                            .addComponent(receiptLabel18)
                            .addComponent(receiptLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(receiptLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(receiptLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(receiptPanelLayout.createSequentialGroup()
                                .addComponent(receiptLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(receiptLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        receiptPanelLayout.setVerticalGroup(
            receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(receiptPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(receiptLabel1)
                .addGap(33, 33, 33)
                .addComponent(receiptLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(receiptPanelLayout.createSequentialGroup()
                        .addComponent(receiptLabel12)
                        .addGap(3, 3, 3)
                        .addComponent(receiptLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(receiptPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(receiptLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(receiptLabel14)))
                    .addGroup(receiptPanelLayout.createSequentialGroup()
                        .addComponent(receiptLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(receiptLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(receiptLabel17)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptLabel19)
                .addContainerGap(39, Short.MAX_VALUE))
        );

        jScrollPane10.setViewportView(receiptPanel);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(date1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(time1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel18)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jbarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jsubtotals, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jpayment, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel20)
                                        .addComponent(jLabel19)
                                        .addComponent(jLabel21)
                                        .addComponent(jLabel23)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addComponent(jLabel22)
                                            .addGap(9, 9, 9))
                                        .addComponent(jLabel24))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jcategory, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                                                .addComponent(jbrand)
                                                .addComponent(jdescription)
                                                .addComponent(junit)
                                                .addComponent(jquantity)))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addGap(61, 61, 61)
                                            .addComponent(jLabel26)
                                            .addGap(127, 127, 127)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jchange, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel27)))))))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(printButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(49, 49, 49)
                                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 1040, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(time1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(date1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel18)
                                    .addComponent(jbarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel19)
                                    .addComponent(jcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel20)
                                    .addComponent(jbrand, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jdescription, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21))
                                .addGap(14, 14, 14)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel22)
                                    .addComponent(junit, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jquantity, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel23)))
                            .addComponent(jScrollPane10, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27))
                        .addGap(4, 4, 4)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(deleteButton)
                                .addComponent(printButton)
                                .addComponent(addButton))
                            .addComponent(jpayment, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                            .addComponent(jsubtotals, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jchange, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(45, 45, 45))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1347, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1347, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exittActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exittActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_exittActionPerformed

    private void minimizeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minimizeeActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printButtonActionPerformed

    private void jpaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jpaymentActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jpaymentActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void exittMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_exittMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_exittMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(POSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(POSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(POSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(POSystem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new POSystem().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel date;
    private javax.swing.JLabel date1;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton exitt;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jbarcode;
    private javax.swing.JTextField jbrand;
    private javax.swing.JTextField jcategory;
    private javax.swing.JLabel jchange;
    private javax.swing.JLabel jclassi;
    private javax.swing.JLabel jdash;
    private javax.swing.JTextField jdescription;
    private javax.swing.JLabel jinventory;
    private javax.swing.JLabel jlog;
    private javax.swing.JTextField jpayment;
    private javax.swing.JLabel jpos;
    private javax.swing.JTextField jquantity;
    private javax.swing.JLabel jreport;
    private javax.swing.JLabel jsubtotals;
    private javax.swing.JLabel jswitch;
    private javax.swing.JTextField junit;
    private javax.swing.JButton minimizee;
    private javax.swing.JButton printButton;
    private javax.swing.JLabel receiptLabel1;
    private javax.swing.JLabel receiptLabel10;
    private javax.swing.JLabel receiptLabel11;
    private javax.swing.JLabel receiptLabel12;
    private javax.swing.JLabel receiptLabel13;
    private javax.swing.JLabel receiptLabel14;
    private javax.swing.JLabel receiptLabel15;
    private javax.swing.JLabel receiptLabel16;
    private javax.swing.JLabel receiptLabel17;
    private javax.swing.JLabel receiptLabel18;
    private javax.swing.JLabel receiptLabel19;
    private javax.swing.JLabel receiptLabel2;
    private javax.swing.JLabel receiptLabel3;
    private javax.swing.JLabel receiptLabel4;
    private javax.swing.JLabel receiptLabel5;
    private javax.swing.JLabel receiptLabel6;
    private javax.swing.JLabel receiptLabel7;
    private javax.swing.JLabel receiptLabel8;
    private javax.swing.JLabel receiptLabel9;
    private javax.swing.JPanel receiptPanel;
    private javax.swing.JLabel time;
    private javax.swing.JLabel time1;
    // End of variables declaration//GEN-END:variables
}

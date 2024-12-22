package POS;

import POS.Report;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Inventory extends javax.swing.JFrame {

    private int posX = 0, posY = 0;
    private DefaultTableModel tableModel;
    private final String url = "jdbc:mysql://localhost:3306/POS";
    private final String dbUser = "root";
    private final String dbPassword = "@dmin001";
    private final String loggedInUser;

    public Inventory() {
        setUndecorated(true);
        loggedInUser = Login.getLoggedInUsername(); 
        initComponents();
        populateComboBoxes();
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
                dispose(); // Close the window
            }
        });

        minimizee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setState(JFrame.ICONIFIED); 
            }
        });
        
        
        //DASHBOARDDDD and etc sa may gilid siya============
        jdash.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
               
                Dashboard dashboard = new Dashboard();
                dashboard.setLocationRelativeTo(null);
                dashboard.setVisible(true);

                // Hide the current LogIn window
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
        ///////switch accounttt------------------
        jswitch.addMouseListener(new MouseAdapter() {
            Object options[] = {"OK", "CaNCEL"};
            public void mouseClicked(MouseEvent evt) {
                int response = JOptionPane.showConfirmDialog(Inventory.this, "Are you sure you want to SWITCH ACCOUNT?", "CONFIRM", 
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
                int response = JOptionPane.showConfirmDialog(Inventory.this, "Are you sure you want to LOG OUT?", "CONFIRM", 
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
        
        
        
        
        
        //=====================================
        
        
        
        //GUMIGITNA=====
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new String[]{"BARCODE", "CATEGORY NAME", "BRAND NAME", "PRODUCT DESCRIPTION", "UNIT PRICE","SALES PRICE", "STOCKS"}, 0);
        tableinventory.setModel(tableModel);
        loadTableData();

        jsave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String barcode = jbarcode.getText();
                String category = jcategory.getSelectedItem().toString();
                String brand = jbrand.getSelectedItem().toString();
                String description = jdescription.getText();
                String unitPrice = junit.getText();
                String salesPrice = jsales.getText();
                String quantity = jstocks.getText();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String date = dateFormatter.format(now);
                String time = timeFormatter.format(now);
                addInventoryItem(barcode, category, brand, description, unitPrice, salesPrice, quantity, date, time, loggedInUser);
                loadTableData();
                JOptionPane.showMessageDialog(null, "New record has been saved in the database");
            }
        });

        jupdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableinventory.getSelectedRow();
                if (selectedRow != -1) {
                    String barcode = tableModel.getValueAt(selectedRow, 0).toString();
                    String category = jcategory.getSelectedItem().toString();
                    String brand = jbrand.getSelectedItem().toString();
                    String description = jdescription.getText();
                    String unitPrice = junit.getText();
                    String salesPrice = jsales.getText();
                    String quantity = jstocks.getText();
                    updateInventoryItem(barcode, category, brand, description, unitPrice, salesPrice, quantity);
                    loadTableData();
                }
            }
        });

        jdelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tableinventory.getSelectedRow();
                if (selectedRow != -1) {
                    String barcode = tableModel.getValueAt(selectedRow, 0).toString();
                    deleteInventoryItem(barcode);
                    loadTableData();
                }
            }
        });
    }
    
    private void populateComboBoxes() {
        List<String> categories = fetchCategories();
        jcategory.removeAllItems();
        for (String category : categories) {
            jcategory.addItem(category);
        }

        List<String> brands = fetchBrands();
        jbrand.removeAllItems();
        for (String brand : brands) {
            jbrand.addItem(brand);
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<InventoryItem> inventoryItems = getAllInventoryItems();
        for (InventoryItem item : inventoryItems) {
            tableModel.addRow(new Object[]{item.getBarcode(), item.getCategory(), item.getBrand(), item.getDescription(), item.getUnitPrice(), item.getSalesPrice(), item.getQuantity(), item.getDate(), item.getTime(), item.getUser()});
        }
    }

    private void time() {
        DateTimeFormatter times = DateTimeFormatter.ofPattern("hh : mm a");
        LocalDateTime now = LocalDateTime.now();
        time.setText(times.format(now));
    }

    private void date() {
        DateTimeFormatter dates = DateTimeFormatter.ofPattern("MM / dd / yyyy");
        LocalDateTime now = LocalDateTime.now();
        date.setText(dates.format(now));
    }

    private void addInventoryItem(String barcode, String category, String brand, String description, String unitPrice, String salesPrice, String quantity, String date, String time, String user) {
        String query = "INSERT INTO Inventory (barcode, category, brand, description, uprice, sprice, qty, date, time, user) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, barcode);
            pst.setString(2, category);
            pst.setString(3, brand);
            pst.setString(4, description);
            pst.setString(5, unitPrice);
            pst.setString(6, salesPrice);
            pst.setString(7, quantity);
            pst.setString(8, date);
            pst.setString(9, time);
            pst.setString(10, user);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    private void updateInventoryItem(String barcode, String category, String brand, String description, String unitPrice, String salesPrice, String quantity) {
        String query = "UPDATE Inventory SET category = ?, brand = ?, description = ?, uprice = ?, sprice = ?, qty = ? WHERE barcode = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, category);
            pst.setString(2, brand);
            pst.setString(3, description);
            pst.setString(4, unitPrice);
            pst.setString(5, salesPrice);
            pst.setString(6, quantity);
            pst.setString(7, barcode);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    private void deleteInventoryItem(String barcode) {
        String query = "DELETE FROM Inventory WHERE barcode = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, barcode);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    private List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> inventoryItems = new ArrayList<>();
        String query = "SELECT * FROM Inventory";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String barcode = rs.getString("barcode");
                String category = rs.getString("category");
                String brand = rs.getString("brand");
                String description = rs.getString("description");
                String unitPrice = rs.getString("uprice");
                String salesPrice = rs.getString("sprice");
                String quantity = rs.getString("qty");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String user = rs.getString("user");
                inventoryItems.add(new InventoryItem(barcode, category, brand, description, unitPrice, salesPrice, quantity, date, time, user));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
        return inventoryItems;
    }
    
        private List<String> fetchCategories() {
        List<String> categories = new ArrayList<>();
        String query = "SELECT DISTINCT category FROM classification";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
        return categories;
    }

    private List<String> fetchBrands() {
        List<String> brands = new ArrayList<>();
        String query = "SELECT DISTINCT brand FROM classification";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                brands.add(rs.getString("brand"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
        return brands;
    }

    public class InventoryItem {
        private String barcode;
        private String category;
        private String brand;
        private String description;
        private String unitPrice;
        private String salesPrice;
        private String quantity;
        private String date;
        private String time;
        private String user;

        public InventoryItem(String barcode, String category, String brand, String description, String unitPrice, String salesPrice, String quantity, String date, String time, String user) {
            this.barcode = barcode;
            this.category = category;
            this.brand = brand;
            this.description = description;
            this.unitPrice = unitPrice;
            this.salesPrice = salesPrice;
            this.quantity = quantity;
            this.date = date;
            this.time = time;
            this.user = user;
        }

        public String getBarcode() {
            return barcode;
        }

        public String getCategory() {
            return category;
        }

        public String getBrand() {
            return brand;
        }

        public String getDescription() {
            return description;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public String getSalesPrice() {
            return salesPrice;
        }

        public String getQuantity() {
            return quantity;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getUser() {
            return user;
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
        jPanel6 = new javax.swing.JPanel();
        jdash = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jclassi = new javax.swing.JLabel();
        jinventory = new javax.swing.JLabel();
        jreport = new javax.swing.JLabel();
        jpos = new javax.swing.JLabel();
        jswitch = new javax.swing.JLabel();
        jlog = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        exitt = new javax.swing.JButton();
        minimizee = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        date1 = new javax.swing.JLabel();
        time1 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jbarcode = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jsave = new javax.swing.JButton();
        jupdate = new javax.swing.JButton();
        jdelete = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jdescription = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        junit = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jsales = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jstocks = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableinventory = new javax.swing.JTable();
        jbrand = new javax.swing.JComboBox<>();
        jcategory = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(1353, 738));

        jPanel6.setBackground(new java.awt.Color(25, 16, 47));

        jdash.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jdash.setForeground(new java.awt.Color(255, 255, 255));
        jdash.setText("DASHBOARD");

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/DASH-3.png"))); // NOI18N

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

        jLabel24.setForeground(new java.awt.Color(255, 255, 255));
        jLabel24.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/CLASS-2.png"))); // NOI18N

        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/INVENTORY-2.png"))); // NOI18N

        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/REPORT-2.png"))); // NOI18N

        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/POS-2.png"))); // NOI18N

        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/LOG OUT-2.png"))); // NOI18N

        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/USER-2.png"))); // NOI18N

        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/CUTE.gif"))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(50, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel30)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jlog)
                            .addComponent(jswitch)
                            .addComponent(jpos)
                            .addComponent(jreport)
                            .addComponent(jinventory)
                            .addComponent(jclassi)
                            .addComponent(jdash))))
                .addGap(45, 45, 45))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(77, 77, 77)
                .addComponent(jLabel30)
                .addGap(77, 77, 77)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdash)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jclassi))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jinventory)
                    .addComponent(jLabel25))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jreport)
                    .addComponent(jLabel26))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpos)
                    .addComponent(jLabel27))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jswitch)
                        .addGap(21, 21, 21)
                        .addComponent(jlog))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel28)))
                .addContainerGap(62, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(208, 175, 208));

        exitt.setBackground(new java.awt.Color(204, 0, 0));
        exitt.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        exitt.setForeground(new java.awt.Color(255, 255, 255));
        exitt.setText("x");
        exitt.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
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

        jLabel31.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(133, 96, 20));
        jLabel31.setText("POS SYSTEM");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(minimizee, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exitt, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(exitt)
                        .addComponent(minimizee))
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        jLabel32.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("INVENTORY");

        jLabel33.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel33.setText("BARCODE :");

        jbarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbarcodeActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel34.setText("CATEGORY :");

        jsave.setBackground(new java.awt.Color(0, 153, 0));
        jsave.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jsave.setForeground(new java.awt.Color(255, 255, 255));
        jsave.setText("SAVE");
        jsave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsaveActionPerformed(evt);
            }
        });

        jupdate.setBackground(new java.awt.Color(0, 153, 0));
        jupdate.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jupdate.setForeground(new java.awt.Color(255, 255, 255));
        jupdate.setText("UPDATE");
        jupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jupdateActionPerformed(evt);
            }
        });

        jdelete.setBackground(new java.awt.Color(255, 51, 0));
        jdelete.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jdelete.setForeground(new java.awt.Color(255, 255, 255));
        jdelete.setText("DELETE");
        jdelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdeleteActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel35.setText("BRAND :");

        jLabel36.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel36.setText("DESCRIPTION :");

        jdescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jdescriptionActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel37.setText("UNIT PRICE :");

        junit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                junitActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel38.setText("SALES PRICE :");

        jsales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsalesActionPerformed(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel39.setText("STOCKS :");

        jstocks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jstocksActionPerformed(evt);
            }
        });

        tableinventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "BARCODE", "CATEGORY NAME", "BRAND NAME", "PRODUCT DETAILS", "UNIT PRICE", "SALE PRICE", "STOCKS"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tableinventory.setDebugGraphicsOptions(javax.swing.DebugGraphics.BUFFERED_OPTION);
        tableinventory.setRowHeight(20);

        tableinventory.getColumnModel().getColumn(0).setPreferredWidth(30);
        tableinventory.getColumnModel().getColumn(1).setPreferredWidth(60);
        tableinventory.getColumnModel().getColumn(2).setPreferredWidth(100);
        tableinventory.getColumnModel().getColumn(3).setPreferredWidth(100);
        tableinventory.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableinventory.getColumnModel().getColumn(5).setPreferredWidth(60);
        tableinventory.getColumnModel().getColumn(6).setPreferredWidth(50);
        jScrollPane1.setViewportView(tableinventory);
        if (tableinventory.getColumnModel().getColumnCount() > 0) {
            tableinventory.getColumnModel().getColumn(0).setResizable(false);
            tableinventory.getColumnModel().getColumn(0).setPreferredWidth(50);
            tableinventory.getColumnModel().getColumn(1).setResizable(false);
            tableinventory.getColumnModel().getColumn(1).setPreferredWidth(100);
            tableinventory.getColumnModel().getColumn(2).setResizable(false);
            tableinventory.getColumnModel().getColumn(2).setPreferredWidth(100);
            tableinventory.getColumnModel().getColumn(3).setResizable(false);
            tableinventory.getColumnModel().getColumn(4).setResizable(false);
            tableinventory.getColumnModel().getColumn(5).setResizable(false);
            tableinventory.getColumnModel().getColumn(6).setResizable(false);
        }

        jScrollPane2.setViewportView(jScrollPane1);

        jbrand.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(date1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(time1)
                        .addGap(18, 18, 18)
                        .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jsave, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12)
                                .addComponent(jdelete, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel33)
                            .addComponent(jbarcode)
                            .addComponent(jLabel34)
                            .addComponent(jLabel35)
                            .addComponent(jLabel36)
                            .addComponent(jdescription)
                            .addComponent(jLabel37)
                            .addComponent(junit)
                            .addComponent(jLabel38)
                            .addComponent(jsales)
                            .addComponent(jLabel39)
                            .addComponent(jstocks)
                            .addComponent(jbrand, 0, 346, Short.MAX_VALUE)
                            .addComponent(jcategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 1036, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(time1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(date1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel32)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(83, 83, 83)
                                .addComponent(jLabel33)
                                .addGap(5, 5, 5)
                                .addComponent(jbarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbrand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdescription, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(junit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jsales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel39)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jstocks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jsave)
                                    .addComponent(jupdate)
                                    .addComponent(jdelete)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1345, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exittActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exittActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_exittActionPerformed

    private void minimizeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minimizeeActionPerformed

    private void jdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jdeleteActionPerformed

    private void jupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jupdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jupdateActionPerformed

    private void jsaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jsaveActionPerformed

    private void jbarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbarcodeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbarcodeActionPerformed

    private void jdescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdescriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jdescriptionActionPerformed

    private void junitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_junitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_junitActionPerformed

    private void jsalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsalesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jsalesActionPerformed

    private void jstocksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jstocksActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jstocksActionPerformed

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
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inventory.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inventory().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel date;
    private javax.swing.JLabel date1;
    private javax.swing.JButton exitt;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField jbarcode;
    private javax.swing.JComboBox<String> jbrand;
    private javax.swing.JComboBox<String> jcategory;
    private javax.swing.JLabel jclassi;
    private javax.swing.JLabel jdash;
    private javax.swing.JButton jdelete;
    private javax.swing.JTextField jdescription;
    private javax.swing.JLabel jinventory;
    private javax.swing.JLabel jlog;
    private javax.swing.JLabel jpos;
    private javax.swing.JLabel jreport;
    private javax.swing.JTextField jsales;
    private javax.swing.JButton jsave;
    private javax.swing.JTextField jstocks;
    private javax.swing.JLabel jswitch;
    private javax.swing.JTextField junit;
    private javax.swing.JButton jupdate;
    private javax.swing.JButton minimizee;
    private javax.swing.JTable tableinventory;
    private javax.swing.JLabel time;
    private javax.swing.JLabel time1;
    // End of variables declaration//GEN-END:variables
}

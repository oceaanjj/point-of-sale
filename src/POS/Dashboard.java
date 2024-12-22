package POS;


import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class Dashboard extends javax.swing.JFrame {

    private int posX = 0, posY = 0;


    public Dashboard() {
        setUndecorated(true); 
        initComponents();
        fetchSalesData();
        fetchStocks();
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

        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose(); 
            }
        });

        minimize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setState(JFrame.ICONIFIED);
            }
        });
        
                //DASHBOARDDDD and etc sa may gilid siya============
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

                // Hide the current LogIn window
                setVisible(false);
            }
        });
        
        jinventory.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Create and display Register.java centered on screen
                Inventory inventory = new Inventory();
                inventory.setLocationRelativeTo(null);
                inventory.setVisible(true);

                // Hide the current LogIn window
                setVisible(false);
            }
        });
        
        jreport.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                // Create and display Register.java centered on screen
                Report report = new Report();
                report.setLocationRelativeTo(null);
                report.setVisible(true);

                // Hide the current LogIn window
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
                int response = JOptionPane.showConfirmDialog(Dashboard.this, "Are you sure you want to SWITCH ACCOUNT?", "CONFIRM", 
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
                int response = JOptionPane.showConfirmDialog(Dashboard.this, "Are you sure you want to LOG OUT?", "CONFIRM"
                        + "", 
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
    
    public void time(){
        DateTimeFormatter times = DateTimeFormatter.ofPattern("hh : mm a");
        LocalDateTime now = LocalDateTime.now();
        time.setText(times.format(now));
    }
    
    public void date(){
        DateTimeFormatter dates = DateTimeFormatter.ofPattern("MM / dd / yyyy");
        LocalDateTime now = LocalDateTime.now();
        date.setText(dates.format(now));
    }
    
    

        private void fetchSalesData() {
            String url = "jdbc:mysql://localhost:3306/POS";
            String user = "root";
            String password = "@dmin001";

            String[] columnNames = {"BARCODE", "CATEGORY", "BRAND NAME", "DECRIPTION", "UNIT PRICE", "QUANTITY", "SUB-TOTAL", "DATE", "TIME", "CASHIER"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columnNames);
            jsales.setModel(model);

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String query = "SELECT * FROM Sales";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                 
                    String barcode = rs.getString("barcode");
                    String category = rs.getString("category");
                    String brand = rs.getString("brand");
                    String description = rs.getString("description");
                    double uprice = rs.getDouble("uprice");
                    int qty = rs.getInt("qty");
                    double subtotal = rs.getDouble("subtotal");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    String cashier = rs.getString("cashier");

                    model.addRow(new Object[]{barcode, category, brand, description, uprice, qty, subtotal, date, time, cashier});
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching data from database", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void fetchStocks() {
            String url = "jdbc:mysql://localhost:3306/POS";
            String user = "root";
            String password = "@dmin001";

            String[] columnNames = { "BARCODE", "CATEGORY NAME", "BRAND NAME", "DESCRIPTION", "QUANTITY"};
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(columnNames);
            jsaletable.setModel(model);

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String query = "SELECT * FROM Inventory";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    String barcode = rs.getString("barcode");
                    String category = rs.getString("category");
                    String brand = rs.getString("brand");
                    String description = rs.getString("description");
                    int qty = rs.getInt("qty");
                  

                    model.addRow(new Object[]{barcode, category, brand, description, qty});
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching data from database", "Error", JOptionPane.ERROR_MESSAGE);
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
        exit = new javax.swing.JButton();
        minimize = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        date1 = new javax.swing.JLabel();
        time1 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jLabel17 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jsaletable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jsales = new javax.swing.JTable();

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBackground(new java.awt.Color(208, 175, 208));

        exit.setBackground(new java.awt.Color(204, 0, 0));
        exit.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        exit.setForeground(new java.awt.Color(255, 255, 255));
        exit.setText("x");
        exit.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });

        minimize.setBackground(new java.awt.Color(0, 102, 0));
        minimize.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        minimize.setForeground(new java.awt.Color(255, 255, 255));
        minimize.setText("__");
        minimize.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        minimize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minimizeActionPerformed(evt);
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
                .addComponent(minimize, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(exit)
                        .addComponent(minimize))
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
        jLabel17.setText("DASHBOARD");

        jsaletable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "BARCODE", "CATEGORY NAME", "BRAND NAME", "PRODUCT DETAILS", "QUANTITY"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jsaletable);

        jScrollPane2.setViewportView(jScrollPane1);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("STOCKS ON HAND", jPanel3);

        jsales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "BARCODE", "CATEGORY NAME", "BRAND NAME", "PRODUCT DETAILS", "UNIT PRICE", "QUANTITY", "SUB-TOTAL", "DATE OF SALES", "TIME OF SALES", "DUTY IN CHARGE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jsales.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(jsales);
        if (jsales.getColumnModel().getColumnCount() > 0) {
            jsales.getColumnModel().getColumn(0).setResizable(false);
            jsales.getColumnModel().getColumn(1).setResizable(false);
            jsales.getColumnModel().getColumn(2).setResizable(false);
            jsales.getColumnModel().getColumn(3).setResizable(false);
            jsales.getColumnModel().getColumn(4).setResizable(false);
            jsales.getColumnModel().getColumn(5).setResizable(false);
            jsales.getColumnModel().getColumn(6).setResizable(false);
            jsales.getColumnModel().getColumn(7).setResizable(false);
            jsales.getColumnModel().getColumn(8).setResizable(false);
            jsales.getColumnModel().getColumn(9).setResizable(false);
        }

        jScrollPane6.setViewportView(jScrollPane5);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
        );

        jTabbedPane3.addTab("SALES ITEM", jPanel9);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane3)
        );

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
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 1040, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(266, 266, 266)
                                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(19, Short.MAX_VALUE))))
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
                        .addGap(31, 31, 31)
                        .addComponent(jLabel17)
                        .addGap(29, 29, 29)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1360, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 1360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void minimizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minimizeActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_exitActionPerformed

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
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel date;
    private javax.swing.JLabel date1;
    private javax.swing.JButton exit;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JLabel jclassi;
    private javax.swing.JLabel jdash;
    private javax.swing.JLabel jinventory;
    private javax.swing.JLabel jlog;
    private javax.swing.JLabel jpos;
    private javax.swing.JLabel jreport;
    private javax.swing.JTable jsales;
    private javax.swing.JTable jsaletable;
    private javax.swing.JLabel jswitch;
    private javax.swing.JButton minimize;
    private javax.swing.JLabel time;
    private javax.swing.JLabel time1;
    // End of variables declaration//GEN-END:variables
}

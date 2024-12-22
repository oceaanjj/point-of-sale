/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package POS;

import POS.Login;
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

public class Classification extends javax.swing.JFrame {

    private int posX = 0, posY = 0;
    private DefaultTableModel tableModel;
    private final String url = "jdbc:mysql://localhost:3306/POS";
    private final String dbUser = "root";
    private final String dbPassword = "@dmin001";
    private final String loggedInUser;

    public Classification() {
        setUndecorated(true);
        loggedInUser = Login.getLoggedInUsername(); 
        initComponents();
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

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                dispose(); 
            }
        });

        minimize.addActionListener(new ActionListener() {
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
                int response = JOptionPane.showConfirmDialog(Classification.this, "Are you sure you want to SWITCH ACCOUNT?", "CONFIRM", 
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
                int response = JOptionPane.showConfirmDialog(Classification.this, "Are you sure you want to LOG OUT?", "CONFIRM", 
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

        tableModel = new DefaultTableModel(new String[]{"ID", "Category Name", "Brand Name"}, 0);
        jtable.setModel(tableModel);
        loadTableData();

        jsave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoryName = jcategory.getText();
                String brandName = jbrand.getText();
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String date = dateFormatter.format(now);
                String time = timeFormatter.format(now);
                addCategoryBrand(categoryName, brandName, date, time, loggedInUser);
                loadTableData();
                JOptionPane.showMessageDialog(null, "New record has been save in database");
            }
        });

        jupdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = jtable.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    String categoryName = jcategory.getText();
                    String brandName = jbrand.getText();
                    updateCategoryBrand(id, categoryName, brandName);
                    loadTableData();
                }
            }
        });

        jdelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = jtable.getSelectedRow();
                if (selectedRow != -1) {
                    int id = (int) tableModel.getValueAt(selectedRow, 0);
                    deleteCategoryBrand(id);
                    loadTableData();
                }
            }
        });
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<CategoryBrand> categoryBrands = getAllCategoryBrands();
        for (CategoryBrand cb : categoryBrands) {
            tableModel.addRow(new Object[]{cb.getId(), cb.getCategoryName(), cb.getBrandName()});
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

    private void addCategoryBrand(String category, String brand, String date, String time, String user) {
        String query = "INSERT INTO Classification (category, brand, date, time, user) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, category);
            pst.setString(2, brand);
            pst.setString(3, date);
            pst.setString(4, time);
            pst.setString(5, user);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    private void updateCategoryBrand(int id, String category, String brand) {
        String query = "UPDATE Classification SET category = ?, brand = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, category);
            pst.setString(2, brand);
            pst.setInt(3, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    private void deleteCategoryBrand(int id) {
        String query = "DELETE FROM Classification WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
    }

    private List<CategoryBrand> getAllCategoryBrands() {
        List<CategoryBrand> categoryBrands = new ArrayList<>();
        String query = "SELECT * FROM Classification";
        try (Connection connection = DriverManager.getConnection(url, dbUser, dbPassword);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String category = rs.getString("category");
                String brand = rs.getString("brand");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String user = rs.getString("user");
                categoryBrands.add(new CategoryBrand(id, category, brand, date, time, user));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + e.getMessage());
        }
        return categoryBrands;
    }

    public class CategoryBrand {
        private int id;
        private String categoryName;
        private String brandName;
        private String date;
        private String time;
        private String user;

        public CategoryBrand(int id, String categoryName, String brandName, String date, String time, String user) {
            this.id = id;
            this.categoryName = categoryName;
            this.brandName = brandName;
            this.date = date;
            this.time = time;
            this.user = user;
        }

        public int getId() {
            return id;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getBrandName() {
            return brandName;
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
        jLabel17 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jtable = new javax.swing.JTable();
        jLabel18 = new javax.swing.JLabel();
        jcategory = new javax.swing.JTextField();
        jbrand = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jsave = new javax.swing.JButton();
        jupdate = new javax.swing.JButton();
        jdelete = new javax.swing.JButton();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exit, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
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

        jLabel17.setFont(new java.awt.Font("Arial", 1, 30)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("CATEGORY AND BRAND CLASSIFICATION");

        jtable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "CATEGORY NAME", "BRAND NAME"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jtable);

        jLabel18.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel18.setText("CATEGORY NAME");

        jcategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcategoryActionPerformed(evt);
            }
        });

        jbrand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbrandActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        jLabel19.setText("BRAND NAME");

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

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(date1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(time1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(time, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(23, 23, 23))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 1040, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel18)
                                    .addComponent(jcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19)
                                    .addComponent(jbrand, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(95, 95, 95)
                                .addComponent(jsave, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jdelete, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(71, 71, 71))))
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
                        .addGap(38, 38, 38)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(jcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(jbrand, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jsave, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jupdate, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jdelete, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(191, 191, 191))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(41, 41, 41))))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1349, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 740, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_exitActionPerformed

    private void minimizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minimizeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minimizeActionPerformed

    private void jcategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jcategoryActionPerformed

    private void jbrandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbrandActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jbrandActionPerformed

    private void jsaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jsaveActionPerformed

    private void jupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jupdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jupdateActionPerformed

    private void jdeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jdeleteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jdeleteActionPerformed

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
            java.util.logging.Logger.getLogger(Classification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Classification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Classification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Classification.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Classification().setVisible(true);
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
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jbrand;
    private javax.swing.JTextField jcategory;
    private javax.swing.JLabel jclassi;
    private javax.swing.JLabel jdash;
    private javax.swing.JButton jdelete;
    private javax.swing.JLabel jinventory;
    private javax.swing.JLabel jlog;
    private javax.swing.JLabel jpos;
    private javax.swing.JLabel jreport;
    private javax.swing.JButton jsave;
    private javax.swing.JLabel jswitch;
    private javax.swing.JTable jtable;
    private javax.swing.JButton jupdate;
    private javax.swing.JButton minimize;
    private javax.swing.JLabel time;
    private javax.swing.JLabel time1;
    // End of variables declaration//GEN-END:variables
}

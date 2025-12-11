import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.util.regex.Pattern;


public class OnlineBookStoreGUI extends JFrame {

    // Data Models
    static class Book {
        private final int id;
        private String title;
        private String author;
        private double price;
        private int stock;

        Book(int id, String title, String author, double price, int stock) {
            this.id = id;
            this.title = title.trim();
            this.author = author.trim();
            this.price = price;
            this.stock = stock;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }

    static class User {
        final String username;
        final String password;
        final boolean isAdmin;

        User(String username, String password, boolean isAdmin) {
            this.username = username.trim().toLowerCase();
            this.password = password;
            this.isAdmin = isAdmin;
        }
    }

    static class Order {
        final String user;
        final List<Book> items;
        final double total;
        final String timestamp;

        Order(String user, List<Book> items, double total) {
            this.user = user;
            this.items = new ArrayList<>(items);
            this.total = total;
            this.timestamp = java.time.LocalDateTime.now().toString();
        }
    }

    // Data stores
    private final List<Book> books = new ArrayList<>();
    private final List<Book> cart = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private User currentUser;

    // UI Components
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JTable bookTable, cartTable, adminBookTable, ordersTable;
    private DefaultTableModel bookTableModel, cartTableModel, adminBookTableModel, ordersTableModel;
    private final DecimalFormat currencyFormat = new DecimalFormat("₹#,##0.00");

    // Admin form fields (fixed references)
    private JTextField adminIdField, adminTitleField, adminAuthorField, adminPriceField, adminStockField;

    public OnlineBookStoreGUI() {
        setTitle("Online Book Store v2.1 - Production Ready");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        initSampleData();
        initUI();
        setVisible(true);
    }

    private void initSampleData() {
        books.add(new Book(1, "Clean Code", "Robert C. Martin", 450.0, 25));
        books.add(new Book(2, "Effective Java", "Joshua Bloch", 550.0, 18));
        books.add(new Book(3, "Introduction to Algorithms", "Cormen", 900.0, 12));
        books.add(new Book(4, "Design Patterns", "Gang of Four", 600.0, 30));
        books.add(new Book(5, "Head First Java", "Kathy Sierra", 400.0, 22));
        
        users.add(new User("user", "1234", false));
        users.add(new User("admin", "1234", true));
    }

    private void initUI() {
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        mainPanel.add(createHomePanel(), "HOME");
        mainPanel.add(createCartPanel(), "CART");
        mainPanel.add(createAdminPanel(), "ADMIN");
        mainPanel.add(createOrdersPanel(), "ORDERS");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void showMessage(String message, String title, int type) {
        JOptionPane.showMessageDialog(this, message, title, type);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel title = new JLabel("📚 Online Book Store - Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridy = 3;
        JButton loginBtn = new JButton("Login");
        gbc.gridx = 0;
        panel.add(loginBtn, gbc);

        JButton registerBtn = new JButton("Register");
        gbc.gridx = 1;
        panel.add(registerBtn, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                showMessage("Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User found = null;
            for (User u : users) {
                if (u.username.equals(username) && u.password.equals(password)) {
                    found = u;
                    break;
                }
            }

            if (found != null) {
                currentUser = found;
                showMessage("Login successful! Welcome " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshBookTable();
                cardLayout.show(mainPanel, found.isAdmin ? "ADMIN" : "HOME");
            } else {
                showMessage("Invalid credentials!\nDemo: user/1234 or admin/1234", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel title = new JLabel("Register New User");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridy = 3;
        JButton registerBtn = new JButton("Register");
        gbc.gridx = 0;
        panel.add(registerBtn, gbc);

        JButton backBtn = new JButton("Back");
        gbc.gridx = 1;
        panel.add(backBtn, gbc);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty() || username.length() < 3) {
                showMessage("Username & password must be 3+ characters", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (User u : users) {
                if (u.username.equals(username)) {
                    showMessage("Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            users.add(new User(username, password, false));
            showMessage("Registered successfully!\nPlease login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(mainPanel, "LOGIN");
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));
        return panel;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("🏠 Available Books", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(title, BorderLayout.NORTH);

        bookTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "Stock"}, 0);
        bookTable = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel buttonPanel = new JPanel();
        JButton addToCartBtn = new JButton("🛒 Add to Cart");
        JButton viewCartBtn = new JButton("🛍️ View Cart");
        JButton ordersBtn = new JButton("📋 Orders");
        JButton adminBtn = new JButton("⚙️ Admin");
        JButton logoutBtn = new JButton("🚪 Logout");

        buttonPanel.add(addToCartBtn);
        buttonPanel.add(viewCartBtn);
        buttonPanel.add(ordersBtn);
        buttonPanel.add(adminBtn);
        buttonPanel.add(logoutBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addToCartBtn.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row == -1) {
                showMessage("Select a book first", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (Integer) bookTableModel.getValueAt(row, 0);
            Book book = findBookById(id);
            if (book != null && book.getStock() > 0) {
                if (!cartAlreadyHasBook(id)) {
                    cart.add(new Book(book.getId(), book.getTitle(), book.getAuthor(), book.getPrice(), book.getStock()));
                    book.setStock(book.getStock() - 1);
                    refreshBookTable();
                    showMessage(book.getTitle() + " added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showMessage("Book already in cart!", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                showMessage("Book out of stock or not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        viewCartBtn.addActionListener(e -> {
            refreshCartTable();
            cardLayout.show(mainPanel, "CART");
        });

        ordersBtn.addActionListener(e -> {
            refreshOrdersTable();
            cardLayout.show(mainPanel, "ORDERS");
        });

        adminBtn.addActionListener(e -> {
            if (currentUser != null && currentUser.isAdmin) {
                refreshAdminBookTable();
                cardLayout.show(mainPanel, "ADMIN");
            } else {
                showMessage("Admin access required", "Access Denied", JOptionPane.WARNING_MESSAGE);
            }
        });

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cart.clear();
            cardLayout.show(mainPanel, "LOGIN");
        });

        return panel;
    }

    private boolean cartAlreadyHasBook(int id) {
        for (Book b : cart) {
            if (b.getId() == id) return true;
        }
        return false;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("🛒 Shopping Cart", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        cartTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price"}, 0);
        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        JPanel bottomPanel = new JPanel();
        JButton removeBtn = new JButton("❌ Remove");
        JButton placeOrderBtn = new JButton("💳 Place Order");
        JButton backBtn = new JButton("⬅️ Back");

        bottomPanel.add(removeBtn);
        bottomPanel.add(placeOrderBtn);
        bottomPanel.add(backBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        removeBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row == -1) {
                showMessage("Select book to remove", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int id = (Integer) cartTableModel.getValueAt(row, 0);
            cart.removeIf(b -> b.getId() == id);
            refreshCartTable();
        });

        placeOrderBtn.addActionListener(e -> {
            if (cart.isEmpty()) {
                showMessage("Cart is empty", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double total = 0;
            for (Book b : cart) {
                total += b.getPrice();
            }
            orders.add(new Order(currentUser.username, new ArrayList<>(cart), total));
            cart.clear();
            refreshCartTable();
            showMessage("Order placed! Total: " + currencyFormat.format(total), "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("⚙️ Admin - Manage Books", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        adminBookTableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "Stock"}, 0);
        adminBookTable = new JTable(adminBookTableModel);
        JScrollPane scrollPane = new JScrollPane(adminBookTable);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        formPanel.add(new JLabel("ID:"));
        adminIdField = new JTextField();
        formPanel.add(adminIdField);
        
        formPanel.add(new JLabel("Title:"));
        adminTitleField = new JTextField();
        formPanel.add(adminTitleField);
        
        formPanel.add(new JLabel("Author:"));
        adminAuthorField = new JTextField();
        formPanel.add(adminAuthorField);
        
        formPanel.add(new JLabel("Price:"));
        adminPriceField = new JTextField();
        formPanel.add(adminPriceField);
        
        formPanel.add(new JLabel("Stock:"));
        adminStockField = new JTextField();
        formPanel.add(adminStockField);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton backBtn = new JButton("Back");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(backBtn);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addAdminBook());
        updateBtn.addActionListener(e -> updateAdminBook());
        deleteBtn.addActionListener(e -> deleteAdminBook());
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        adminBookTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = adminBookTable.getSelectedRow();
                if (row != -1) {
                    adminIdField.setText(adminBookTableModel.getValueAt(row, 0).toString());
                    adminTitleField.setText(adminBookTableModel.getValueAt(row, 1).toString());
                    adminAuthorField.setText(adminBookTableModel.getValueAt(row, 2).toString());
                    adminPriceField.setText(adminBookTableModel.getValueAt(row, 3).toString());
                    adminStockField.setText(adminBookTableModel.getValueAt(row, 4).toString());
                }
            }
        });

        return panel;
    }

    private void addAdminBook() {
        try {
            int id = Integer.parseInt(adminIdField.getText().trim());
            String title = adminTitleField.getText().trim();
            String author = adminAuthorField.getText().trim();
            double price = Double.parseDouble(adminPriceField.getText().trim());
            int stock = Integer.parseInt(adminStockField.getText().trim());

            if (title.isEmpty() || author.isEmpty()) {
                showMessage("Fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (findBookById(id) != null) {
                showMessage("ID already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            books.add(new Book(id, title, author, price, stock));
            refreshAdminBookTable();
            showMessage("Book added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearAdminFields();
        } catch (NumberFormatException ex) {
            showMessage("Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAdminBook() {
        try {
            int id = Integer.parseInt(adminIdField.getText().trim());
            Book book = findBookById(id);
            if (book == null) {
                showMessage("Book not found", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String title = adminTitleField.getText().trim();
            String author = adminAuthorField.getText().trim();
            double price = Double.parseDouble(adminPriceField.getText().trim());
            int stock = Integer.parseInt(adminStockField.getText().trim());

            // Update only if fields are filled
            if (!title.isEmpty()) book.title = title;
            if (!author.isEmpty()) book.author = author;
            book.price = price;
            book.stock = stock;

            refreshAdminBookTable();
            showMessage("Book updated", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            showMessage("Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAdminBook() {
        try {
            int id = Integer.parseInt(adminIdField.getText().trim());
            books.removeIf(b -> b.getId() == id);
            refreshAdminBookTable();
            showMessage("Book deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearAdminFields();
        } catch (NumberFormatException ex) {
            showMessage("Invalid ID", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAdminFields() {
        adminIdField.setText("");
        adminTitleField.setText("");
        adminAuthorField.setText("");
        adminPriceField.setText("");
        adminStockField.setText("");
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("📋 Order History", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(title, BorderLayout.NORTH);

        ordersTableModel = new DefaultTableModel(new String[]{"Order Details"}, 0);
        ordersTable = new JTable(ordersTableModel);
        JScrollPane scrollPane = new JScrollPane(ordersTable);

        JButton backBtn = new JButton("Back");
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));
        return panel;
    }

    // Helper methods
    private Book findBookById(int id) {
        for (Book b : books) {
            if (b.getId() == id) return b;
        }
        return null;
    }

    private void refreshBookTable() {
        bookTableModel.setRowCount(0);
        for (Book b : books) {
            bookTableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), 
                currencyFormat.format(b.getPrice()), b.getStock()});
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (Book b : cart) {
            cartTableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), 
                currencyFormat.format(b.getPrice())});
        }
    }

    private void refreshAdminBookTable() {
        adminBookTableModel.setRowCount(0);
        for (Book b : books) {
            adminBookTableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), 
                currencyFormat.format(b.getPrice()), b.getStock()});
        }
    }

    private void refreshOrdersTable() {
        ordersTableModel.setRowCount(0);
        for (Order o : orders) {
            String details = o.user + " | Total: " + currencyFormat.format(o.total) + 
                           " | " + o.timestamp.substring(0, 16);
            ordersTableModel.addRow(new Object[]{details});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OnlineBookStoreGUI());
    }
}

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class OnlineBookStoreGUI extends JFrame {

    // Simple in‑memory models (replace later with DB/JDBC)
    static class Book {
        int id;
        String title;
        String author;
        double price;

        Book(int id, String title, String author, double price) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.price = price;
        }
    }

    static class User {
        String username;
        String password;

        User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // Data stores
    private List<Book> books = new ArrayList<>();
    private List<Book> cart = new ArrayList<>();
    private List<String> orders = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    // Logged-in user
    private User currentUser;

    // Main UI components
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Panels
    private JPanel loginPanel;
    private JPanel registerPanel;
    private JPanel homePanel;
    private JPanel cartPanel;
    private JPanel adminPanel;
    private JPanel ordersPanel;

    // Tables
    private JTable bookTable;
    private JTable cartTable;
    private JTable adminBookTable;
    private JTable ordersTable;

    // Models
    private DefaultTableModel bookTableModel;
    private DefaultTableModel cartTableModel;
    private DefaultTableModel adminBookTableModel;
    private DefaultTableModel ordersTableModel;

    public OnlineBookStoreGUI() {
        setTitle("Online Book Store");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initSampleData();
        initUI();

        setVisible(true);
    }

    private void initSampleData() {
        // Sample books (stock)
        books.add(new Book(1, "Clean Code", "Robert C. Martin", 450.0));
        books.add(new Book(2, "Effective Java", "Joshua Bloch", 550.0));
        books.add(new Book(3, "Introduction to Algorithms", "Cormen", 900.0));
        books.add(new Book(4, "Design Patterns", "Gang of Four", 600.0));
        books.add(new Book(5, "Head First Java", "Kathy Sierra", 400.0));
        books.add(new Book(6, "Java: The Complete Reference", "Herbert Schildt", 750.0));
        books.add(new Book(7, "Thinking in Java", "Bruce Eckel", 500.0));
        books.add(new Book(8, "Java Concurrency in Practice", "Brian Goetz", 650.0));
        books.add(new Book(9, "Spring in Action", "Craig Walls", 700.0));
        books.add(new Book(10, "Head First Design Patterns", "Eric Freeman", 550.0));

        // Default user
        users.add(new User("user", "1234"));
    }

    private void initUI() {
        // Create all panels
        loginPanel = createLoginPanel();
        registerPanel = createRegisterPanel();
        homePanel = createHomePanel();
        cartPanel = createCartPanel();
        adminPanel = createAdminPanel();
        ordersPanel = createOrdersPanel();

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(homePanel, "HOME");
        mainPanel.add(cartPanel, "CART");
        mainPanel.add(adminPanel, "ADMIN");
        mainPanel.add(ordersPanel, "ORDERS");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Online Book Store - Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(userLabel, gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(loginBtn, gbc);
        gbc.gridx = 1;
        panel.add(registerBtn, gbc);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            User found = null;
            for (User u : users) {
                if (u.username.equals(username) && u.password.equals(password)) {
                    found = u;
                    break;
                }
            }

            if (found != null) {
                currentUser = found;
                JOptionPane.showMessageDialog(this, "Login successful!");
                refreshBookTable(); // Show books on home panel
                cardLayout.show(mainPanel, "HOME");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        });

        registerBtn.addActionListener(e -> cardLayout.show(mainPanel, "REGISTER"));

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Register New User");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(userLabel, gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(registerBtn, gbc);
        gbc.gridx = 1;
        panel.add(backBtn, gbc);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            for (User u : users) {
                if (u.username.equals(username)) {
                    JOptionPane.showMessageDialog(this, "User already exists!");
                    return;
                }
            }

            users.add(new User(username, password));
            JOptionPane.showMessageDialog(this, "Registered successfully! Please login.");
            cardLayout.show(mainPanel, "LOGIN");
        });

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "LOGIN"));

        return panel;
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("Available Books", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        // Table for books
        bookTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Price"}, 0);
        bookTable = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addToCartBtn = new JButton("Add to Cart");
        JButton viewCartBtn = new JButton("View Cart");
        JButton orderHistoryBtn = new JButton("Order History");
        JButton adminBtn = new JButton("Admin (Books)");
        JButton logoutBtn = new JButton("Logout");

        buttonPanel.add(addToCartBtn);
        buttonPanel.add(viewCartBtn);
        buttonPanel.add(orderHistoryBtn);
        buttonPanel.add(adminBtn);
        buttonPanel.add(logoutBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Handlers
        addToCartBtn.addActionListener(e -> {
            int row = bookTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a book first.");
                return;
            }
            int id = (int) bookTableModel.getValueAt(row, 0);
            Book b = findBookById(id);
            if (b != null) {
                cart.add(b);
                JOptionPane.showMessageDialog(this, "Book added to cart.");
            }
        });

        viewCartBtn.addActionListener(e -> {
            refreshCartTable();
            cardLayout.show(mainPanel, "CART");
        });

        orderHistoryBtn.addActionListener(e -> {
            refreshOrdersTable();
            cardLayout.show(mainPanel, "ORDERS");
        });

        adminBtn.addActionListener(e -> {
            refreshAdminBookTable();
            cardLayout.show(mainPanel, "ADMIN");
        });

        logoutBtn.addActionListener(e -> {
            currentUser = null;
            cart.clear();
            cardLayout.show(mainPanel, "LOGIN");
        });

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        cartTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Price"}, 0);
        cartTable = new JTable(cartTableModel);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        JPanel bottomPanel = new JPanel();
        JButton removeBtn = new JButton("Remove Selected");
        JButton placeOrderBtn = new JButton("Place Order");
        JButton backBtn = new JButton("Back");

        bottomPanel.add(removeBtn);
        bottomPanel.add(placeOrderBtn);
        bottomPanel.add(backBtn);

        panel.add(new JLabel("Shopping Cart", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        removeBtn.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a book to remove.");
                return;
            }
            int id = (int) cartTableModel.getValueAt(row, 0);
            cart.removeIf(b -> b.id == id);
            refreshCartTable();
        });

        placeOrderBtn.addActionListener(e -> {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty.");
                return;
            }
            double total = 0;
            StringBuilder details = new StringBuilder();
            for (Book b : cart) {
                total += b.price;
                details.append(b.title).append(", ");
            }
            orders.add("User: " + currentUser.username + " | Items: " + details + "Total: " + total);
            cart.clear();
            refreshCartTable();
            JOptionPane.showMessageDialog(this, "Order placed successfully! Total: " + total);
        });

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        adminBookTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "Price"}, 0);
        adminBookTable = new JTable(adminBookTableModel);
        JScrollPane scrollPane = new JScrollPane(adminBookTable);

        JPanel topPanel = new JPanel();
        JButton backBtn = new JButton("Back");
        topPanel.add(new JLabel("Admin - Manage Books"));
        topPanel.add(backBtn);

        JPanel bottomPanel = new JPanel();
        JTextField idField = new JTextField(4);
        JTextField titleField = new JTextField(10);
        JTextField authorField = new JTextField(10);
        JTextField priceField = new JTextField(6);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        bottomPanel.add(new JLabel("ID:"));
        bottomPanel.add(idField);
        bottomPanel.add(new JLabel("Title:"));
        bottomPanel.add(titleField);
        bottomPanel.add(new JLabel("Author:"));
        bottomPanel.add(authorField);
        bottomPanel.add(new JLabel("Price:"));
        bottomPanel.add(priceField);
        bottomPanel.add(addBtn);
        bottomPanel.add(updateBtn);
        bottomPanel.add(deleteBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        addBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());

                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Fill all fields.");
                    return;
                }

                if (findBookById(id) != null) {
                    JOptionPane.showMessageDialog(this, "ID already exists.");
                    return;
                }

                books.add(new Book(id, title, author, price));
                refreshAdminBookTable();
                JOptionPane.showMessageDialog(this, "Book added.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID or price.");
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                Book b = findBookById(id);
                if (b == null) {
                    JOptionPane.showMessageDialog(this, "Book not found.");
                    return;
                }
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());

                if (!title.isEmpty()) b.title = title;
                if (!author.isEmpty()) b.author = author;
                b.price = price;

                refreshAdminBookTable();
                JOptionPane.showMessageDialog(this, "Book updated.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID or price.");
            }
        });

        deleteBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                books.removeIf(b -> b.id == id);
                refreshAdminBookTable();
                JOptionPane.showMessageDialog(this, "Book deleted (if existed).");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID.");
            }
        });

        adminBookTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = adminBookTable.getSelectedRow();
                if (row != -1) {
                    idField.setText(adminBookTableModel.getValueAt(row, 0).toString());
                    titleField.setText(adminBookTableModel.getValueAt(row, 1).toString());
                    authorField.setText(adminBookTableModel.getValueAt(row, 2).toString());
                    priceField.setText(adminBookTableModel.getValueAt(row, 3).toString());
                }
            }
        });

        return panel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        ordersTableModel = new DefaultTableModel(new Object[]{"Order Details"}, 0);
        ordersTable = new JTable(ordersTableModel);
        JScrollPane scrollPane = new JScrollPane(ordersTable);

        JButton backBtn = new JButton("Back");

        panel.add(new JLabel("Order History", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(backBtn, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "HOME"));

        return panel;
    }

    // Helper methods
    private Book findBookById(int id) {
        for (Book b : books) {
            if (b.id == id) return b;
        }
        return null;
    }

    private void refreshBookTable() {
        bookTableModel.setRowCount(0);
        for (Book b : books) {
            bookTableModel.addRow(new Object[]{b.id, b.title, b.author, b.price});
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (Book b : cart) {
            cartTableModel.addRow(new Object[]{b.id, b.title, b.price});
        }
    }

    private void refreshAdminBookTable() {
        adminBookTableModel.setRowCount(0);
        for (Book b : books) {
            adminBookTableModel.addRow(new Object[]{b.id, b.title, b.author, b.price});
        }
    }

    private void refreshOrdersTable() {
        ordersTableModel.setRowCount(0);
        for (String s : orders) {
            ordersTableModel.addRow(new Object[]{s});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineBookStoreGUI::new);
    }
}

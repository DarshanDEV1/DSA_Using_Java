import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DoublyLinkedListGUI extends JFrame {
    private DoublyLinkedList list;
    private JTextArea displayArea;
    private JTextField inputField;
    private JTextField positionField;

    public DoublyLinkedListGUI() {
        list = new DoublyLinkedList();
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Doubly Linked List GUI");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 1));

        JPanel addPanel = new JPanel();
        addPanel.setLayout(new FlowLayout());
        inputField = new JTextField(5);
        JButton appendButton = new JButton("Append Node");
        appendButton.addActionListener(this::appendNodeCallback);
        addPanel.add(new JLabel("Data:"));
        addPanel.add(inputField);
        addPanel.add(appendButton);
        inputPanel.add(addPanel);

        JPanel positionPanel = new JPanel();
        positionPanel.setLayout(new FlowLayout());
        positionField = new JTextField(5);
        JButton readButton = new JButton("Read Node");
        readButton.addActionListener(this::readNodeCallback);
        JButton updateButton = new JButton("Update Node");
        updateButton.addActionListener(this::updateNodeCallback);
        positionPanel.add(new JLabel("Position:"));
        positionPanel.add(positionField);
        positionPanel.add(readButton);
        positionPanel.add(updateButton);
        inputPanel.add(positionPanel);

        JPanel deletePanel = new JPanel();
        JButton deleteButton = new JButton("Delete Node");
        deleteButton.addActionListener(this::deleteNodeCallback);
        deletePanel.add(deleteButton);
        inputPanel.add(deletePanel);

        add(inputPanel, BorderLayout.SOUTH);

        refreshDisplay();
    }

    private void appendNodeCallback(ActionEvent e) {
        try {
            int data = Integer.parseInt(inputField.getText());
            list.appendNode(data);
            refreshDisplay();
            inputField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer.");
        }
    }

    private void readNodeCallback(ActionEvent e) {
        try {
            int position = Integer.parseInt(positionField.getText());
            Node node = list.readNode(position);
            if (node != null) {
                JOptionPane.showMessageDialog(this, "Data at position " + position + ": " + node.data);
            } else {
                JOptionPane.showMessageDialog(this, "Node not found at position: " + position);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid position.");
        }
    }

    private void updateNodeCallback(ActionEvent e) {
        try {
            int position = Integer.parseInt(positionField.getText());
            int newData = Integer.parseInt(inputField.getText());
            list.updateNode(position, newData);
            refreshDisplay();
            inputField.setText("");
            positionField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for position and data.");
        }
    }

    private void deleteNodeCallback(ActionEvent e) {
        try {
            int position = Integer.parseInt(positionField.getText());
            list.deleteNode(position);
            refreshDisplay();
            positionField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid position.");
        }
    }

    private void refreshDisplay() {
        displayArea.setText("");
        Node current = list.head;
        StringBuilder displayText = new StringBuilder("Doubly Linked List: ");
        while (current != null) {
            displayText.append(current.data).append(" <-> ");
            current = current.next;
        }
        displayText.append("null");
        displayArea.setText(displayText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DoublyLinkedListGUI gui = new DoublyLinkedListGUI();
            gui.setVisible(true);
        });
    }
}

class DoublyLinkedList {
    protected Node head;

    protected void appendNode(int data) {
        if (head == null) {
            head = new Node(data, null, null);
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = new Node(data, null, current);
        }
    }

    protected Node readNode(int position) {
        Node current = head;
        int count = 1;
        while (current != null && count < position) {
            current = current.next;
            count++;
        }
        return (count == position) ? current : null;
    }

    protected void updateNode(int position, int newData) {
        Node nodeToUpdate = readNode(position);
        if (nodeToUpdate != null) {
            nodeToUpdate.data = newData;
        }
    }

    protected void deleteNode(int position) {
        if (head == null) return;

        Node nodeToDelete = readNode(position);
        if (nodeToDelete != null) {
            if (nodeToDelete.prev != null) {
                nodeToDelete.prev.next = nodeToDelete.next;
            } else {
                head = nodeToDelete.next;
            }
            if (nodeToDelete.next != null) {
                nodeToDelete.next.prev = nodeToDelete.prev;
            }
        }
    }
}

class Node {
    int data;
    Node next;
    Node prev;

    Node(int data, Node next, Node prev) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}

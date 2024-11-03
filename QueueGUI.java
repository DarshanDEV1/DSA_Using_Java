import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Stack;

public class QueueGUI extends JFrame {
    private JTextArea displayArea;
    private JTextField inputField;
    private JComboBox<String> queueTypeComboBox;
    private Queue selectedQueue;

    public QueueGUI() {
        setTitle("Queue GUI");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display area for queue content
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        // Panel for input fields and buttons
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputField = new JTextField(5);
        inputPanel.add(new JLabel("Data:"));
        inputPanel.add(inputField);

        JButton enqueueButton = new JButton("Enqueue");
        enqueueButton.addActionListener(this::enqueueCallback);
        inputPanel.add(enqueueButton);

        JButton dequeueButton = new JButton("Dequeue");
        dequeueButton.addActionListener(this::dequeueCallback);
        inputPanel.add(dequeueButton);

        JButton peekButton = new JButton("Peek");
        peekButton.addActionListener(this::peekCallback);
        inputPanel.add(peekButton);

        add(inputPanel, BorderLayout.SOUTH);

        // Dropdown to select queue type
        JPanel topPanel = new JPanel(new FlowLayout());
        String[] queueTypes = {"Queue", "Priority Queue", "Circular Queue", "Deque", "Queue Using Stacks"};
        queueTypeComboBox = new JComboBox<>(queueTypes);
        queueTypeComboBox.addActionListener(this::queueTypeChanged);
        topPanel.add(new JLabel("Select Queue Type:"));
        topPanel.add(queueTypeComboBox);
        add(topPanel, BorderLayout.NORTH);

        // Set initial queue type
        queueTypeChanged(null);
    }

    private void queueTypeChanged(ActionEvent e) {
        String selectedType = (String) queueTypeComboBox.getSelectedItem();
        switch (selectedType) {
            case "Queue":
                selectedQueue = new SimpleQueue();
                break;
            case "Priority Queue":
                selectedQueue = new PriorityQueue();
                break;
            case "Circular Queue":
                selectedQueue = new CircularQueue(5);
                break;
            case "Deque":
                selectedQueue = new Deque();
                break;
            case "Queue Using Stacks":
                selectedQueue = new QueueUsingStacks();
                break;
        }
        refreshDisplay();
    }

    private void enqueueCallback(ActionEvent e) {
        try {
            int data = Integer.parseInt(inputField.getText());
            selectedQueue.enqueue(data);
            inputField.setText("");
            refreshDisplay();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer.");
        }
    }

    private void dequeueCallback(ActionEvent e) {
        try {
            int data = selectedQueue.dequeue();
            JOptionPane.showMessageDialog(this, "Dequeued: " + data);
            refreshDisplay();
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void peekCallback(ActionEvent e) {
        try {
            int data = selectedQueue.peek();
            JOptionPane.showMessageDialog(this, "Peek: " + data);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshDisplay() {
        displayArea.setText(selectedQueue.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QueueGUI gui = new QueueGUI();
            gui.setVisible(true);
        });
    }

    interface Queue {
        void enqueue(int data);
        int dequeue();
        int peek();
        String toString();
    }

    class SimpleQueue implements Queue {
        private Node front, rear;

        public void enqueue(int data) {
            Node newNode = new Node(data);
            if (rear == null) {
                front = rear = newNode;
            } else {
                rear.next = newNode;
                rear = newNode;
            }
        }

        public int dequeue() {
            if (front == null) throw new IllegalStateException("Queue is empty");
            int data = front.data;
            front = front.next;
            if (front == null) rear = null;
            return data;
        }

        public int peek() {
            if (front == null) throw new IllegalStateException("Queue is empty");
            return front.data;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Queue: ");
            Node current = front;
            while (current != null) {
                sb.append(current.data).append(" -> ");
                current = current.next;
            }
            sb.append("null");
            return sb.toString();
        }
    }

    class PriorityQueue implements Queue {
        private Node front;

        public void enqueue(int data) {
            Node newNode = new Node(data);
            if (front == null || data < front.data) {
                newNode.next = front;
                front = newNode;
            } else {
                Node current = front;
                while (current.next != null && current.next.data <= data) {
                    current = current.next;
                }
                newNode.next = current.next;
                current.next = newNode;
            }
        }

        public int dequeue() {
            if (front == null) throw new IllegalStateException("Priority Queue is empty");
            int data = front.data;
            front = front.next;
            return data;
        }

        public int peek() {
            if (front == null) throw new IllegalStateException("Priority Queue is empty");
            return front.data;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Priority Queue: ");
            Node current = front;
            while (current != null) {
                sb.append(current.data).append(" -> ");
                current = current.next;
            }
            sb.append("null");
            return sb.toString();
        }
    }

    class CircularQueue implements Queue {
        private int[] queue;
        private int front, rear, size, capacity;

        public CircularQueue(int capacity) {
            this.capacity = capacity;
            queue = new int[capacity];
            front = size = 0;
            rear = capacity - 1;
        }

        public void enqueue(int data) {
            if (isFull()) throw new IllegalStateException("Circular Queue is full");
            rear = (rear + 1) % capacity;
            queue[rear] = data;
            size++;
        }

        public int dequeue() {
            if (isEmpty()) throw new IllegalStateException("Circular Queue is empty");
            int data = queue[front];
            front = (front + 1) % capacity;
            size--;
            return data;
        }

        public int peek() {
            if (isEmpty()) throw new IllegalStateException("Circular Queue is empty");
            return queue[front];
        }

        public boolean isFull() {
            return size == capacity;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Circular Queue: ");
            for (int i = 0; i < size; i++) {
                sb.append(queue[(front + i) % capacity]).append(" -> ");
            }
            sb.append("null");
            return sb.toString();
        }
    }

    class Deque implements Queue {
        private Node front, rear;

        public void enqueue(int data) {
            addRear(data);
        }

        public void addFront(int data) {
            Node newNode = new Node(data);
            if (front == null) {
                front = rear = newNode;
            } else {
                newNode.next = front;
                front.prev = newNode;
                front = newNode;
            }
        }

        public void addRear(int data) {
            Node newNode = new Node(data);
            if (rear == null) {
                front = rear = newNode;
            } else {
                newNode.prev = rear;
                rear.next = newNode;
                rear = newNode;
            }
        }

        public int removeFront() {
            if (front == null) throw new IllegalStateException("Deque is empty");
            int data = front.data;
            front = front.next;
            if (front != null) front.prev = null;
            else rear = null;
            return data;
        }

        public int dequeue() {
            return removeFront();
        }

        public int peek() {
            if (front == null) throw new IllegalStateException("Deque is empty");
            return front.data;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Deque: ");
            Node current = front;
            while (current != null) {
                sb.append(current.data).append(" -> ");
                current = current.next;
            }
            sb.append("null");
            return sb.toString();
        }
    }

    class QueueUsingStacks implements Queue {
        private Stack<Integer> stack1 = new Stack<>();
        private Stack<Integer> stack2 = new Stack<>();

        public void enqueue(int data) {
            stack1.push(data);
        }

        public int dequeue() {
            if (stack2.isEmpty()) {
                while (!stack1.isEmpty()) {
                    stack2.push(stack1.pop());
                }
            }
            if (stack2.isEmpty()) throw new IllegalStateException("Queue is empty");
            return stack2.pop();
        }

        public int peek() {
            if (stack2.isEmpty()) {
                while (!stack1.isEmpty()) {
                    stack2.push(stack1.pop());
                }
            }
            if (stack2.isEmpty()) throw new IllegalStateException("Queue is empty");
            return stack2.peek();
        }

        public String toString() {
            Stack<Integer> temp = new Stack<>();
            temp.addAll(stack2);
            for (int i = stack1.size() - 1; i >= 0; i--) {
                temp.push(stack1.get(i));
            }
            return "Queue Using Stacks: " + temp.toString();
        }
    }

    class Node {
        int data;
        Node next, prev;

        Node(int data) {
            this.data = data;
            this.next = this.prev = null;
        }
    }
}

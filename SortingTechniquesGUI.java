import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SortingTechniquesGUI extends JFrame implements ActionListener {
    private JPanel panel;
    private JLabel labelInput, labelOutput;
    private JTextField textFieldInput, textFieldOutput;
    private JButton buttonBubbleSort, buttonQuickSort, buttonShellSort, buttonInsertionSort;
    private JButton buttonPrevious, buttonNext;
    private JTextArea textAreaSteps;
    private JProgressBar progressBar;
    private SortVisualizer sortVisualizer;
    private ArrayList<int[]> sortingSteps;
    private int currentStep;
    private boolean isSorting;

    private String[][] timeComplexities = {
            {"O(n^2) ", "O(n^2) ", "O(n^2) Worst"}, // Bubble Sort
            {"O(n log n) ", "O(n log n) ", "O(n^2) "}, // Quick Sort
            {"O(n log n) ", "O(n^2) ", "O(n^2) "}, // Shell Sort
            {"O(n) ", "O(n^2) ", "O(n^2) "} // Insertion Sort
    };

    public SortingTechniquesGUI() {
        setTitle("Sorting Techniques");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10)); // 2 columns, 10px horizontal and vertical gap
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelInput = new JLabel("Enter numbers (comma separated):");
        textFieldInput = new JTextField();
        textFieldInput.setPreferredSize(new Dimension(200, 30)); // Set preferred size for text field
        labelOutput = new JLabel("Sorted numbers:");
        textFieldOutput = new JTextField();
        textFieldOutput.setPreferredSize(new Dimension(200, 30)); // Set preferred size for text field

        buttonPrevious = new JButton("Previous");
        buttonPrevious.addActionListener(this);
        buttonNext = new JButton("Next");
        buttonNext.addActionListener(this);

        textAreaSteps = new JTextArea("Sorting Steps:\n");
        textAreaSteps.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textAreaSteps);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        panel.add(labelInput);
        panel.add(textFieldInput);
        panel.add(labelOutput);
        panel.add(textFieldOutput);

        buttonBubbleSort = createButton("Bubble Sort", timeComplexities[0]);
        buttonQuickSort = createButton("Quick Sort", timeComplexities[1]);
        buttonShellSort = createButton("Shell Sort", timeComplexities[2]);
        buttonInsertionSort = createButton("Insertion Sort", timeComplexities[3]);

        panel.add(buttonBubbleSort);
        panel.add(buttonQuickSort);
        panel.add(buttonShellSort);
        panel.add(buttonInsertionSort);
        panel.add(scrollPane);
        panel.add(progressBar);
        panel.add(buttonPrevious);
        panel.add(buttonNext);

        add(panel, BorderLayout.CENTER);

        sortVisualizer = new SortVisualizer();
        add(sortVisualizer, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, String[] timeComplexities) {
        JButton button = new JButton(text);
        button.addActionListener(this);
        button.setToolTipText(getToolTipText(timeComplexities)); // Set tooltip
        return button;
    }

    private String getToolTipText(String[] timeComplexities) {
        return String.format("<html><b>Time Complexities:</b><br>Best Case: %s<br>Average Case: %s<br>Worst Case: %s</html>",
                timeComplexities[0], timeComplexities[1], timeComplexities[2]);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonPrevious) {
            showPreviousStep();
        } else if (e.getSource() == buttonNext) {
            showNextStep();
        } else {
            handleSortingAction(e);
        }
    }
private void handleSortingAction(ActionEvent e) {
        String input = textFieldInput.getText();
        if (!isValidInput(input)) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please enter numbers separated by commas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] originalArray = Arrays.stream(input.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();

        sortingSteps = new ArrayList<>();
        currentStep = 0;
        isSorting = true;

        if (e.getSource() == buttonBubbleSort) {
            sortingSteps = SortingAlgorithms.bubbleSort(originalArray.clone());
        } else if (e.getSource() == buttonQuickSort) {
            sortingSteps = SortingAlgorithms.quickSort(originalArray.clone());
        } else if (e.getSource() == buttonShellSort) {
            sortingSteps = SortingAlgorithms.shellSort(originalArray.clone());
        } else if (e.getSource() == buttonInsertionSort) {
            sortingSteps = SortingAlgorithms.insertionSort(originalArray.clone());
        }

        if (!sortingSteps.isEmpty()) {
            updateGUI(sortingSteps.get(currentStep));
        }
    }

    private boolean isValidInput(String input) {
        return input.matches("^\\d+(,\\s*\\d+)*$");
    }

    private void updateGUI(int[] array) {
        SwingUtilities.invokeLater(() -> {
            StringBuilder stepsText = new StringBuilder("Sorting Steps:\n");
            for (int num : array) {
                stepsText.append(num).append(" ");
            }
            textAreaSteps.setText(stepsText.toString());
            sortVisualizer.updateArray(array);
            progressBar.setValue((int) (((double) (currentStep + 1) / (sortingSteps.size())) * 100));
            if (currentStep == sortingSteps.size() - 1) {
                isSorting = false;
                StringBuilder sortedArrayText = new StringBuilder();
                for (int num : array) {
                    sortedArrayText.append(num).append(", ");
                }
                if (sortedArrayText.length() > 0) {
                    sortedArrayText.delete(sortedArrayText.length() - 2, sortedArrayText.length());
                }
                textFieldOutput.setText(sortedArrayText.toString());
            }
        });
    }

    private void showPreviousStep() {
        if (currentStep > 0) {
            currentStep--;
            updateGUI(sortingSteps.get(currentStep));
        }
    }

    private void showNextStep() {
        if (currentStep < sortingSteps.size() - 1) {
            currentStep++;
            updateGUI(sortingSteps.get(currentStep));
        }
    }

    public static void main(String[] args) {
        new SortingTechniquesGUI();
    }
}

class SortingAlgorithms {
    public static ArrayList<int[]> bubbleSort(int[] array) {
        ArrayList<int[]> steps = new ArrayList<>();
        steps.add(array.clone());

        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
                steps.add(array.clone());
            }
        }
        return steps;
    }

    public static ArrayList<int[]> quickSort(int[] array) {
        ArrayList<int[]> steps = new ArrayList<>();
        quickSortHelper(array, 0, array.length - 1, steps);
        return steps;
    }

    private static void quickSortHelper(int[] array, int low, int high, ArrayList<int[]> steps) {
        if (low < high) {
            int pi = partition(array, low, high);
            steps.add(array.clone());
            quickSortHelper(array, low, pi - 1, steps);
            quickSortHelper(array, pi + 1, high, steps);
        }
    }
private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }

    public static ArrayList<int[]> shellSort(int[] array) {
        ArrayList<int[]> steps = new ArrayList<>();
        steps.add(array.clone());

        int n = array.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int temp = array[i];
                int j;
                for (j = i; j >= gap && array[j - gap] > temp; j -= gap) {
                    array[j] = array[j - gap];
                }
                array[j] = temp;
                steps.add(array.clone());
            }
        }
        return steps;
    }

    public static ArrayList<int[]> insertionSort(int[] array) {
        ArrayList<int[]> steps = new ArrayList<>();
        steps.add(array.clone());

        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
            }
            array[j + 1] = key;
            steps.add(array.clone());
        }
        return steps;
    }
}

class SortVisualizer extends JPanel {
    private int[] array;

    public SortVisualizer() {
        setPreferredSize(new Dimension(600, 100));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (array != null && array.length > 0) {
            int barWidth = getWidth() / array.length;
            int maxValue = Arrays.stream(array).max().orElse(1);
            for (int i = 0; i < array.length; i++) {
                int barHeight = (int) ((double) array[i] / maxValue * getHeight());
                int x = i * barWidth;
                int y = getHeight() - barHeight;
                g.setColor(getColorForValue(array[i], maxValue));
                g.fillRect(x, y, barWidth, barHeight);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, barWidth, barHeight);
            }
        }
    }

    public void updateArray(int[] array) {
        this.array = array;
        repaint();
    }

    private Color getColorForValue(int value, int maxValue) {
        float hue = 0.5f; // Constant hue
        float saturation = 0.6f; // Constant saturation
        float brightness = (float) value / maxValue; // Adjust brightness based on value
        return Color.getHSBColor(hue, saturation, brightness);
    }

    public void clear() {
        array = null;
        repaint();
    }
}
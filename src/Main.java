

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main extends JFrame implements ActionListener {
    private boolean DEBUG = true;
    private JPanel container;
    private JTextField txtNumProccess;
    private JTextField txtNumRes;
    private JButton btnCreate;
    private JPanel tablePanel;
    private JTable tableData = null;
    private JScrollPane jScrollPane = null;
    private JPanel alloPanel;
    private JTable tableAllo = null;
    private JScrollPane scrollAllo = null;
    private JButton btnSolve;
    private int nProcesses = 4;
    private int nRes = 3;
    private JLabel lbStatus;
    private DefaultListModel<String> model;

    public static void main(String[] args) {
        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            new Main();
        }catch (Exception e){
            System.out.println("e"+e.getMessage());
        }
    }

    public Main(){
        initLayout();
        initFrame();
        addListener();
    }

    private void initLayout(){
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        createInputBox();
        tablePanel = new JPanel();
        tablePanel.setPreferredSize(new Dimension(680, 200));
        tablePanel.setMinimumSize(tablePanel.getPreferredSize());
        tablePanel.setMaximumSize(tablePanel.getPreferredSize());
        container.add(tablePanel);
        createTable(4, 3);

        container.add(Box.createRigidArea(new Dimension(0, 10)));
        alloPanel = new JPanel();
        alloPanel.setPreferredSize(new Dimension(680, 45));
        alloPanel.setMaximumSize(alloPanel.getPreferredSize());
        alloPanel.setMinimumSize(alloPanel.getPreferredSize());
        container.add(alloPanel);
        createAllocateInput(4, 3);

        container.add(Box.createRigidArea(new Dimension(0, 10)));
        Box solveBox = Box.createHorizontalBox();
        solveBox.add(Box.createHorizontalGlue());
        btnSolve = new JButton("Solve");
        solveBox.add(btnSolve);
        solveBox.add(Box.createHorizontalGlue());
        container.add(solveBox);

        Box statusBox = Box.createHorizontalBox();
        statusBox.add(new JLabel("System status: "));
        lbStatus = new JLabel();
        statusBox.add(lbStatus);
        statusBox.add(Box.createHorizontalGlue());
        container.add(statusBox);

        Box statesBox = Box.createVerticalBox();
        Box lbBox = Box.createHorizontalBox();
        lbBox.add(new JLabel("List safe state: "));
        lbBox.add(Box.createHorizontalGlue());
        statesBox.add(lbBox);
        model = new DefaultListModel<>();
        JList<String> listState = new JList<>(model);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) listState.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        statesBox.add(new JScrollPane(listState));
        container.add(statesBox);
        this.add(container);
    }

    private void createInputBox(){
        Box inputBox = Box.createHorizontalBox();

        JLabel lb1 = new JLabel("Number of processes");
        inputBox.add(lb1);
        inputBox.add(Box.createRigidArea(new Dimension(5, 0)));
        txtNumProccess = new JTextField();
        txtNumProccess.setText("3");
        txtNumProccess.setMaximumSize(new Dimension(100, 25));
        txtNumProccess.setPreferredSize(txtNumProccess.getPreferredSize());
        txtNumProccess.setMinimumSize(txtNumProccess.getPreferredSize());
        inputBox.add(txtNumProccess);

        inputBox.add(Box.createRigidArea(new Dimension(20, 0)));

        JLabel lb2 = new JLabel("Number of resources");
        inputBox.add(lb2);
        inputBox.add(Box.createRigidArea(new Dimension(5, 0)));
        txtNumRes = new JTextField();
        txtNumRes.setText("3");
        txtNumRes.setMaximumSize(new Dimension(100, 25));
        txtNumRes.setPreferredSize(txtNumRes.getPreferredSize());
        txtNumRes.setMinimumSize(txtNumRes.getPreferredSize());
        inputBox.add(txtNumRes);

        inputBox.add(Box.createRigidArea(new Dimension(10, 0)));
        btnCreate = new JButton("Create");
        inputBox.add(btnCreate);

        container.add(inputBox);
    }

    private void createAllocateInput(int nProcesses, int nRes){
        if(tableAllo != null && scrollAllo != null) scrollAllo.remove(tableAllo);
        if(scrollAllo != null) alloPanel.remove(scrollAllo);
        String[] titles = new String[nRes+1];
        titles[0] = "#";
        for(int i = 1; i < titles.length; i++)
            titles[i] = "Request " + i;
        Object[][] alloData = new Object[1][nRes+1];
        for(int i = 1; i < alloData[0].length; i++){
            alloData[0][i] = 0;
        }
        tableAllo = new JTable(alloData, titles);
        tableAllo.setDefaultEditor(Object.class, new ValidCell());
        String[] dataItem = new String[nProcesses];
        for(int i = 0; i < nProcesses; i++){
            dataItem[i] = "P"+(i+1);
        }
        JComboBox countryCombo = new JComboBox(dataItem);
        countryCombo.setSelectedIndex(0);
        tableAllo.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(countryCombo));
        scrollAllo = new JScrollPane(tableAllo);
        scrollAllo.setPreferredSize(alloPanel.getMaximumSize());
        scrollAllo.setMaximumSize(alloPanel.getMaximumSize());
        scrollAllo.setMinimumSize(alloPanel.getMaximumSize());
        alloPanel.add(scrollAllo);
        alloPanel.updateUI();

    }

    private void createTable(int nProcess, int nRes){
        if(tableData != null && jScrollPane != null) jScrollPane.remove(tableData);
        if(jScrollPane != null) tablePanel.remove(jScrollPane);
        this.tableData = new JTable(new MyTableModel(nProcess, nRes)){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component comp = super.prepareRenderer(renderer, row, col);
                Object value = getModel().getValueAt(row, col);
                if(row >= 1 && col > nRes*2)
                    comp.setBackground(Color.LIGHT_GRAY);
                else
                    comp.setBackground(Color.white);
                return comp;
            }
        };
        this.tableData.setDefaultEditor(Object.class, new ValidCell());
        jScrollPane = new JScrollPane(tableData, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setPreferredSize(tablePanel.getMaximumSize());
        jScrollPane.setMaximumSize(tablePanel.getMaximumSize());
        jScrollPane.setMinimumSize(tablePanel.getMaximumSize());
        tablePanel.add(jScrollPane);
        tablePanel.updateUI();
    }

    private void initFrame(){
        this.container.setBackground(Color.WHITE);
        this.setSize(720, 480);
        this.setVisible(true);
        this.setTitle("Banker's Algorithm");
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void addListener(){
        this.btnCreate.addActionListener(this);
        this.btnSolve.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        if(button == this.btnCreate){
            if(!validInteger(this.txtNumProccess.getText()) || !validInteger(this.txtNumRes.getText())){
                JOptionPane.showMessageDialog(
                        null,
                        "Please enter number!",
                        "Alert!",JOptionPane.ERROR_MESSAGE);
                return;
            }

            int nPro = Integer.parseInt(this.txtNumProccess.getText());
            int nRes = Integer.parseInt(this.txtNumRes.getText());
            if(nPro <= 0 || nRes <= 0){
                JOptionPane.showMessageDialog(
                        null,
                        "Please enter number greater than 0!",
                        "Alert!",JOptionPane.ERROR_MESSAGE);
                return;
            }
            this.nProcesses =nPro;
            this.nRes = nRes;
            this.lbStatus.setText("");
            this.model.clear();
            createTable(nPro, nRes);
            createAllocateInput(nPro, nRes);
            System.out.println("Created");
        }else {
            if(button == this.btnSolve){
                int[][] maxRes = new int[this.nProcesses][this.nRes];
                for(int i = 0; i < nProcesses ; i++){
                    for(int j = 1; j < nRes+1; j++){
                        maxRes[i][j-1] = Integer.parseInt(this.tableData.getModel().getValueAt(i, j).toString());
                    }
                }
                int[][] aloRes = new int[this.nProcesses][this.nRes];
                for(int i = 0; i < nProcesses ; i++){
                    for(int j = nRes+1; j < nRes*2+1; j++){
                        aloRes[i][j-nRes-1] = Integer.parseInt(this.tableData.getModel().getValueAt(i, j).toString());
                    }
                }
                int[] avaRes = new int[this.nRes];
                for(int i = 0; i < 1 ; i++){
                    for(int j = nRes*2+1; j < nRes*3+1; j++){
                        avaRes[j-nRes*2-1] = Integer.parseInt(this.tableData.getModel().getValueAt(i, j).toString());
                    }
                }
                Banker banker = null;
                if(this.tableAllo.getValueAt(0, 0) != null){
                    String process = this.tableAllo.getValueAt(0,0).toString();
                    int idProc = Integer.parseInt(process.substring(1));
                    int[] reqRes = new int[nRes];
                    for(int i = 1; i < nRes+1; i++){
                        reqRes[i-1] = Integer.parseInt(this.tableAllo.getValueAt(0, i).toString());
                    }
                    banker = new Banker(maxRes, aloRes, avaRes, idProc, reqRes);
                    for(int i = 0; i < banker.getListSafeSate().size(); i++){
                        System.out.println(banker.getListSafeSate().get(i));
                    }
                }else {
                    banker = new Banker(maxRes, aloRes, avaRes);
                    for(int i = 0; i < banker.getListSafeSate().size(); i++){
                        System.out.println(banker.getListSafeSate().get(i));
                    }
                }

                int status = banker.getSystemStatus();
                this.model.clear();
                switch (status){
                    case 1:{
                        lbStatus.setText("System in safe state");
                        lbStatus.setForeground(Color.green);
                        ArrayList<String> listSafeState = banker.getListSafeSate();
                        for(String str : listSafeState){
                            this.model.addElement(str);
                        }
                        break;
                    }
                    case 2:{
                        lbStatus.setText("System in deadlock. Request allocate more than the number of resources available.");
                        lbStatus.setForeground(Color.red);
                        this.model.addElement("No safe state can be found");
                        break;
                    }
                    case 3:{
                        lbStatus.setText("System in wrong state. Maximum resource less than total allocation resource.");
                        lbStatus.setForeground(Color.ORANGE);
                        this.model.addElement("No safe state can be found");
                        break;
                    }
                    default:{
                        lbStatus.setText("System in deadlock");
                        lbStatus.setForeground(Color.red);
                        this.model.addElement("No safe state can be found");
                        break;
                    }
                }
            }
        }
    }
    class MyTableModel extends AbstractTableModel {
        private String[] title = {};
        private Object[][] data = {};
        private int nPro;
        private int nRes;
        public MyTableModel(int nPro, int nRes){
            this.nPro = nPro;
            this.nRes = nRes;
            title = new String[nRes*3+1];
            title[0] = "#";
            int count = 0;
            for(int i = 1; i < title.length; i++){
                count++;
                if(count<=nRes){
                    title[i] = "Max R" + (i);
                }else {
                    if(count <= nRes*2)
                        title[i] = "Allo R" + (i-nRes);
                    else
                        title[i] = "Avai R"+(i-nRes*2);
                }
            }
            data = new Object[nPro][nRes*3+1];
            for(int i = 0; i < data.length; i++){
                for(int j = 0; j < data[0].length; j++){
                    if(j == 0) data[i][0] = "P"+(i+1);
                    else {
                        if(!(i >=1 && j > nRes*2)){
                            data[i][j] = 0;
                        }
                    }

                }
            }
            if(nPro == 4 && nRes == 3 && DEBUG){
                int[][] allocated = { { 0, 1, 0 }, { 2, 0, 0 }, { 3, 0, 2 }, { 2, 1, 1 } };
                int[][] max = { { 7, 5, 3 }, { 3, 2, 2 }, { 9, 0, 2 }, { 2, 2, 2 } };
                int[] aval = {3, 3, 4};
                for(int i = 0; i < nPro ; i++){
                    for(int j = 1; j < nRes+1; j++){
                        data[i][j] = max[i][j-1];
                    }
                }
                for(int i = 0; i < nPro ; i++){
                    for(int j = nRes+1; j < nRes*2+1; j++){
                        data[i][j] = allocated[i][j-nRes-1];
                    }
                }
                for(int i = 0; i < 1 ; i++){
                    for(int j = nRes*2+1; j < nRes*3+1; j++){
                        data[i][j] = aval[j-nRes*2-1];
                    }
                }
            }


        }
        public int getColumnCount() {
            return title.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return title[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return "".getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col < 1) {
                return false;
            } else {
                if(row >= 1 && col > nRes*2){
                    return false;
                }
                return true;
            }
        }



        public void setValueAt(Object value, int row, int col) {
            if(row >= 1 && col > nRes*2){
                data[row][col] = "#";
            }else{
                data[row][col] = value;
            }
            fireTableCellUpdated(row, col);
        }
    }
    class ValidCell extends DefaultCellEditor {
        ValidCell() {
            super( new JTextField() );
        }

        public boolean stopCellEditing() {
            btnSolve.setEnabled(true);
            try
            {
                int editingValue = Integer.parseInt((String)getCellEditorValue());
                if(editingValue < 0){
                    JTextField textField = (JTextField)getComponent();
                    textField.setBorder(new LineBorder(Color.red));
                    textField.selectAll();
                    textField.requestFocusInWindow();
                    JOptionPane.showMessageDialog(
                            null,
                            "Please enter number greater than 0!",
                            "Alert!",JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            catch(Exception exception)
            {
                JTextField textField = (JTextField)getComponent();
                textField.setBorder(new LineBorder(Color.red));
                textField.selectAll();
                textField.requestFocusInWindow();
                JOptionPane.showMessageDialog(
                        null,
                        "Please enter number!",
                        "Alert!",JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return super.stopCellEditing();
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Component c = super.getTableCellEditorComponent(
                    table, value, isSelected, row, column);
//            ((JComponent)c).setBorder(new LineBorder(Color.black));
            return c;
        }
    }
    private boolean validInteger(String str){
        try{
            int num = Integer.parseInt(str);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}

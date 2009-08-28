/*
 * Copyright (C) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.validate;

import org.javarosa.core.model.FormDef;
import org.javarosa.xform.util.XFormUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Uses the javarosa-core library to process a form and show errors, if any.
 * 
 * @author Adam Lerer (adam.lerer@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
public class FormValidator implements ActionListener {

    JFrame validatorFrame;
    JPanel validatorPanel;
    JTextField formPath;
    JTextArea validatorOutput;
    JScrollPane validatorOutputScrollPane;
    JButton chooseFileButton, validateButton;
    JFileChooser fileChooser;


    public static void main(String[] args) {
        new FormValidator();
    }


    public FormValidator() {
        validatorFrame = new JFrame("ODK Validator");
        validatorPanel = new JPanel();
        validatorFrame.setResizable(false);

        // Add the widgets.
        addWidgets(validatorPanel);

        // redirect out/errors to the GUI
        System.setOut(new PrintStream(new JTextAreaOutputStream(validatorOutput)));
        System.setErr(new PrintStream(new JTextAreaOutputStream(validatorOutput)));

        // Add the panel to the frame.
        validatorFrame.getContentPane().add(validatorPanel, BorderLayout.CENTER);

        // Exit when the window is closed.
        validatorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Show the converter.
        validatorFrame.pack();
        validatorFrame.setVisible(true);
    }

    /**
     * An OutputStream that writes the output to a text area.
     * 
     * @author alerer@google.com (Adam Lerer)
     * 
     */
    class JTextAreaOutputStream extends OutputStream {
        private JTextArea textArea;


        public JTextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }


        @Override
        public void write(int b) {
            textArea.append(new String(new byte[] {(byte) (b % 256)}, 0, 1));
        }
    }


    private void addWidgets(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Create widgets.
        formPath = new JTextField(40);

        fileChooser = new JFileChooser();
        chooseFileButton = new JButton("Choose File...");
        chooseFileButton.addActionListener(this);

        validatorOutput = new JTextArea();
        validatorOutput.setEditable(false);

        validatorOutputScrollPane = new JScrollPane(validatorOutput);
        validatorOutputScrollPane.setPreferredSize(new Dimension(640, 480));

        validateButton = new JButton("Validate Again");
        validateButton.addActionListener(this);


        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 7, 0, 0);
        panel.add(formPath, c);

        c.gridx = 2;
        c.gridy = 1;
        c.insets = new Insets(10, 0, 10, 7);
        panel.add(chooseFileButton, c);

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.insets = new Insets(0, 10, 10, 10);
        panel.add(validatorOutputScrollPane, c);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 3;
        panel.add(validateButton, c);


    }


    // @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == validateButton) {
            validatorOutput.setText("");
            validate(formPath.getText());
        }

        if (e.getSource() == chooseFileButton) {
            int returnVal = fileChooser.showOpenDialog(validatorFrame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                formPath.setText(file.getPath());
            }
            validatorOutput.setText("");
            validate(formPath.getText());
        }
    }


    public void validate(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            System.err.println("Please choose a file before attempting to validate.");
            return;
        }
        try {
            FormDef form = XFormUtils.getFormFromInputStream(fis);
            if (form == null) {
                System.err.println("Form is invalid. See above for details.");
            } else {
                System.out.println("Form is valid. See above for warnings (if any).");
            }
        } catch (Exception e) {
            System.err.println("Error parsing form.");
            e.printStackTrace();
        }
    }

}

/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.opendatakit.validate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.GroupDef;
import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.condition.EvaluationContext;
import org.javarosa.core.model.condition.IFunctionHandler;
import org.javarosa.core.model.data.IAnswerData;
import org.javarosa.core.model.instance.InstanceInitializationFactory;
import org.javarosa.core.model.instance.InvalidReferenceException;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.utils.IPreloadHandler;
import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.core.services.PrototypeManager;
import org.javarosa.form.api.FormEntryCaption;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.form.api.FormEntryPrompt;
import org.javarosa.model.xform.XFormsModule;
import org.javarosa.xform.parse.XFormParseException;
import org.javarosa.xform.parse.XFormParser;
import org.javarosa.xform.util.XFormUtils;

import org.opendatakit.validate.buildconfig.BuildConfig;

/**
 * Uses the javarosa-core library to process a form and show errors, if any.
 *
 * @author Adam Lerer (adam.lerer@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
public class FormValidator implements ActionListener {
    /**
     * Classes needed to serialize objects. Need to put anything from JR in here.
     */
    public final static String[] SERIALIABLE_CLASSES = {
            "org.javarosa.core.services.locale.ResourceFileDataSource", // JavaRosaCoreModule
            "org.javarosa.core.services.locale.TableLocaleSource", // JavaRosaCoreModule
            "org.javarosa.core.model.FormDef",
            "org.javarosa.core.model.SubmissionProfile", // CoreModelModule
            "org.javarosa.core.model.QuestionDef", // CoreModelModule
            "org.javarosa.core.model.GroupDef", // CoreModelModule
            "org.javarosa.core.model.instance.FormInstance", // CoreModelModule
            "org.javarosa.core.model.data.BooleanData", // CoreModelModule
            "org.javarosa.core.model.data.DateData", // CoreModelModule
            "org.javarosa.core.model.data.DateTimeData", // CoreModelModule
            "org.javarosa.core.model.data.DecimalData", // CoreModelModule
            "org.javarosa.core.model.data.GeoPointData", // CoreModelModule
            "org.javarosa.core.model.data.GeoShapeData", // CoreModelModule
            "org.javarosa.core.model.data.GeoTraceData", // CoreModelModule
            "org.javarosa.core.model.data.IntegerData", // CoreModelModule
            "org.javarosa.core.model.data.LongData", // CoreModelModule
            "org.javarosa.core.model.data.MultiPointerAnswerData", // CoreModelModule
            "org.javarosa.core.model.data.PointerAnswerData", // CoreModelModule
            "org.javarosa.core.model.data.SelectMultiData", // CoreModelModule
            "org.javarosa.core.model.data.SelectOneData", // CoreModelModule
            "org.javarosa.core.model.data.StringData", // CoreModelModule
            "org.javarosa.core.model.data.TimeData", // CoreModelModule
            "org.javarosa.core.model.data.UncastData", // CoreModelModule
            "org.javarosa.core.model.data.helper.BasicDataPointer", // CoreModelModule
            "org.javarosa.core.model.actions.SetValueAction" //CoreModelModule
    };

    private JFrame validatorFrame;
    private JTextField formPath;
    private JTextArea validatorOutput;
    private JButton chooseFileButton;
    private JButton validateButton;
    private JFileChooser fileChooser;

    private ErrorListener errors = ErrorListener.DEFAULT_ERROR_LISTENER;
    private boolean inError = false;


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (args.length == 0) {
                new FormValidator().show();
            } else {
                List<String> paths = new ArrayList<>();
                boolean failFast = false;
                for (String arg : args) {
                    if (arg.equals("--failFast") || arg.equals("--fail-fast")) {
                        failFast = true;
                    } else {
                        paths.add(arg);
                    }
                }

                new FormValidator().validateAndExitWithErrorCode(
                        paths, failFast);
            }
        }
        catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    private void setError(boolean outcome) {
        inError = outcome;
    }

    public FormValidator() {}

    private FormValidator show() {
        validatorFrame = new JFrame(BuildConfig.NAME + " " + BuildConfig.VERSION);
        JPanel validatorPanel = new JPanel();
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
        return this;
    }

    /**
     * An OutputStream that writes the output to a text area.
     *
     * @author alerer@google.com (Adam Lerer)
     */
    class JTextAreaOutputStream extends OutputStream {
        private final JTextArea textArea;


        public JTextAreaOutputStream(JTextArea textArea) {
            this.textArea = textArea;
        }


        @Override
        public void write(int b) {
            textArea.append(new String(new byte[] {
                (byte) (b % 256)
            }, 0, 1));
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
        validatorOutput.setLineWrap(true);
        validatorOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        validatorOutput.setForeground(Color.BLACK);

        JScrollPane validatorOutputScrollPane = new JScrollPane(validatorOutput);
        validatorOutputScrollPane.setPreferredSize(new Dimension(800, 600));

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
            setError(false);
            validatorOutput.setText("");
            validatorOutput.setForeground(Color.BLUE);
            validate(formPath.getText());
            validatorOutput.setForeground(inError ? Color.red : Color.BLUE);
        }

        if (e.getSource() == chooseFileButton) {
            int returnVal = fileChooser.showOpenDialog(validatorFrame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                formPath.setText(file.getPath());
            }
            setError(false);
            validatorOutput.setText("");
            validatorOutput.setForeground(Color.BLUE);
            validate(formPath.getText());
            validatorOutput.setForeground(inError ? Color.red : Color.BLUE);
        }
    }

    boolean stepThroughEntireForm(FormEntryModel model) throws InvalidReferenceException {
        boolean outcome = false;
        Set<String> loops = new HashSet<String>();
        // step through every value in the form
        FormIndex idx = FormIndex.createBeginningOfFormIndex();
        int event;
        for (;;) {
            idx = model.incrementIndex(idx);
            event = model.getEvent(idx);
            if ( event == FormEntryController.EVENT_END_OF_FORM ) break;

            if (event == FormEntryController.EVENT_PROMPT_NEW_REPEAT) {
                String elementPath = idx.getReference().toString().replaceAll("\\[\\d+\\]", "");
                if ( !loops.contains(elementPath) ) {
                    loops.add(elementPath);
                    model.getForm().createNewRepeat(idx);
                    idx = model.getFormIndex();
                }
            } else if (event == FormEntryController.EVENT_GROUP) {
                GroupDef gd = (GroupDef) model.getForm().getChild(idx);
                if ( gd.getChildren() == null || gd.getChildren().size() == 0 ) {
                    outcome = true;
                    setError(true);
                    String elementPath = idx.getReference().toString().replaceAll("\\[\\d+\\]", "");
                    errors.error("Group has no children! Group: " + elementPath + ". The XML is invalid.\n");
                }
            } else if (event != FormEntryController.EVENT_QUESTION) {
                continue;
            } else {
                FormEntryPrompt prompt = model.getQuestionPrompt(idx);
                if ( prompt.getControlType() == Constants.CONTROL_SELECT_MULTI ||
                     prompt.getControlType() == Constants.CONTROL_SELECT_ONE ) {
                    String elementPath = idx.getReference().toString().replaceAll("\\[\\d+\\]", "");
                    List<SelectChoice> items;
                    items = prompt.getSelectChoices();
                    // check for null values...
                    for ( int i = 0 ; i < items.size() ; ++i ) {
                        SelectChoice s = items.get(i);
                        String text = prompt.getSelectChoiceText(s);
                        String image = prompt.getSpecialFormSelectChoiceText(s,
                                                FormEntryCaption.TEXT_FORM_IMAGE);
                        if ((text == null || text.trim().length() == 0 ) &&
                                (image == null || image.trim().length() == 0)) {
                            errors.error("Selection choice label text and image uri are both missing for: " + elementPath + " choice: " + (i+1) + ".\n");
                        }
                        if ( s.getValue() == null || s.getValue().trim().length() == 0) {
                            outcome = true;
                            setError(true);
                            errors.error("Selection value is missing for: " + elementPath + " choice: " + (i+1) + ". The XML is invalid.\n");
                        }
                    }
                }
            }
        }
        return outcome;
    }

    public void validateAndExitWithErrorCode(List<String> paths, boolean failFast) {
        List<String> failed = new ArrayList<>();
        for (String path : paths) {
            try {
                validate(path);
            } catch (Exception e) {
                errors.error("\nException: ", e);
                setError(true);
            }

            if (inError) {
               if (failFast) {
                    break;
               } else {
                   failed.add(path);
                   setError(false);
               }
            }
        }

        if (inError || !failed.isEmpty()) {
            if (!failed.isEmpty()) {
                errors.error("\nThe following files failed validation:");
                for (String path : failed) {
                    errors.error(path);
                }
            }

            errors.error("\nResult: Invalid");
            System.exit(1);
        } else {
            System.exit(0);
        }
    }

  public void validate(String path) {
        File src = new File(path);
        if (!src.exists()) {
            setError(true);
            errors.error("File: " + src.getAbsolutePath() + " does not exist.");
            return;
        }

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(src);
            errors.info("Validating: " + path);
            validate(fis);
        } catch (FileNotFoundException e) {
            setError(true);
            errors.error("Please choose a file before attempting to validate.");
            return;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                    e.printStackTrace();
                }
            }
        }

    }

    public void validateText(String xml) {
        validate(xml.getBytes());
    }

    public void validate(InputStream xmlSource) {
        byte[] xformData;
        try {
            // first read the whole form into memory since this stream is going to be read twice
            // first for xml validation
            // second for xform validation
            validate(copyToByteArray(xmlSource));
        } catch (IOException e) {
            errors.error("Failed to read XML Input Stream", e);
        }
    }

    public void validate(byte[] xformData) {
        // validate well formed xml
            // errors.info("Checking form...");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            try {
                factory.newDocumentBuilder().parse(new ByteArrayInputStream(xformData));
            } catch (Exception e) {
                setError(true);
                errors.error("\n\n\n>> XML is invalid. See above for the errors.",e);
                return;
            }

            // need a list of classes that formdef uses
            // unfortunately, the JR registerModule() functions do more than this.
            // register just the classes that would have been registered by:
            // new JavaRosaCoreModule().registerModule();
            // new CoreModelModule().registerModule();
            // replace with direct call to PrototypeManager
            PrototypeManager.registerPrototypes(SERIALIABLE_CLASSES);
            // initialize XForms module
            new XFormsModule().registerModule();

            // needed to override rms property manager
            org.javarosa.core.services.PropertyManager
                    .setPropertyManager(new StubPropertyManager());

            // For forms with external secondary instances
            final ReferenceManager referenceManager = ReferenceManager.instance();
            referenceManager.addReferenceFactory(new StubReferenceFactory());

            PrototypeManager.registerPrototype("org.opendatakit.validate.StubSetGeopointAction");
            XFormParser.registerActionHandler(StubSetGeopointActionHandler.ELEMENT_NAME, new StubSetGeopointActionHandler());

            // validate if the xform can be parsed.
            try {
                FormDef fd = XFormUtils.getFormFromInputStream(new ByteArrayInputStream(xformData));
                if (fd == null) {
                    setError(true);
                    errors.error("\n\n\n>> Something broke the parser. Try again.");
                    return;
                }

                // make sure properties get loaded
                fd.getPreloader().addPreloadHandler(new FakePreloadHandler("property"));

                // update evaluation context for function handlers
                fd.getEvaluationContext().addFunctionHandler(new IFunctionHandler() {

                    public String getName() {
                        return "pulldata";
                    }

                    public List<Class[]> getPrototypes() {
                        return new ArrayList<Class[]>();
                    }

                    public boolean rawArgs() {
                        return true;
                    }

                    public boolean realTime() {
                        return false;
                    }

                    public Object eval(Object[] args, EvaluationContext ec) {
                        // no actual implementation here -- just a stub to facilitate validation
                        return args[0];
                    }});

                // check for runtime errors
                fd.initialize(true, new InstanceInitializationFactory());

                errors.info("\n\n>> Xform parsing completed! See above for any warnings.\n");

                // create FormEntryController from formdef
                FormEntryModel fem = new FormEntryModel(fd);

                // and try to step through the form...
                if ( stepThroughEntireForm(fem) ) {
                    setError(true);
                    errors.error("\n\n>> Xform is invalid! See above for errors and warnings.");
                } else {
                    errors.info("\n\n>> Xform is valid! See above for any warnings.");
                }

            } catch (XFormParseException e) {
                setError(true);
                errors.error("\n\n>> XForm is invalid. See above for the errors.",e);

            } catch (Exception e) {
                setError(true);
                errors.error("\n\n>> Something broke the parser. See above for a hint.",e);

            }

    }

    private byte[] copyToByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        return baos.toByteArray();
    }

    public FormValidator setErrorListener(ErrorListener listener){
        if(listener == null){
            throw new NullPointerException("Cannot set a null error listener");
        }
        this.errors = listener;
        return this;
    }

    private class FakePreloadHandler implements IPreloadHandler {

        String preloadHandled;


        public FakePreloadHandler(String preloadHandled) {
            this.preloadHandled = preloadHandled;
        }


        public boolean handlePostProcess(TreeElement arg0, String arg1) {
            // TODO Auto-generated method stub
            return false;
        }


        public IAnswerData handlePreload(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }


        public String preloadHandled() {
            // TODO Auto-generated method stub
            return preloadHandled;
        }

    }

}

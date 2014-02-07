package com.processconfiguration.quaestio;

// Java 2 Standard classes

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
// Third party classes
import netscape.javascript.JSException;
import netscape.javascript.JSObject;
*/

// Local classes

/**
 * Present the questionnaire editor as a browser applet.
 */
public class QuaestioApplet extends JApplet {

    public void init() {
        super.init();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                Main main;
                public void run() {
                    main = new Main() {

                        @Override
                        protected void browse(final URL url) throws Exception {
                            main.log("Browse " + url);
                            getAppletContext().showDocument(url, "target");
                            main.log("Browsed " + url);
                        }

                        @Override
                        protected JPanel getJPanel_save2() {
                            if (jPanel_save2 == null) {
                                GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
                                gridBagConstraints34.gridx = 0;
                                gridBagConstraints34.insets = new Insets(4, 4, 6, 20);
                                gridBagConstraints34.gridy = 0;
                                GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
                                gridBagConstraints35.gridx = 1;
                                gridBagConstraints35.insets = new Insets(4, 16, 6, 4);
                                gridBagConstraints35.gridy = 0;
                                GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
                                gridBagConstraints36.gridx = 2;
                                gridBagConstraints36.insets = new Insets(4, 40, 6, 4);
                                gridBagConstraints36.gridy = 0;
                                jPanel_save2 = new JPanel();
                                jPanel_save2.setLayout(new GridBagLayout());
                                jPanel_save2.add(getJButton_Discard(), gridBagConstraints36);
                            }
                            return jPanel_save2;
                        }

                        /** @inheritDoc */
                        @Override
                        protected String getSaveText() { 
                            return "All the facts have been set correctly.\n"
                                + "\nRemember to Save Model before closing this window!";
                        }

			/*
                        @Override
                        protected void testLiveConnect() {
                            try {
                                JSObject window = JSObject.getWindow(QuaestioApplet.this);
                                window.eval("alert('showModel');");
                                //window.eval("showModel();");
                                window.eval("ORYX.Plugins.ApromoreSave.apromoreSave(null,null);");
                                window.eval("alert('shownModel');");
                            } catch (JSException e) {
                                e.printStackTrace();
                            } catch (NoClassDefFoundError e) {
                                e.printStackTrace();
                            }
                        }
			*/
                    };

                    try {
                        main.setEditorURL(new URL(getDocumentBase(), "../editor/"));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }

                    String cmap = getParameter("cmap_url");
                    main.log("Cmap URL: " + cmap);
                    if (cmap != null) {
                        try {
                            main.setLinkedCmap(new UrlCmap(cmap));
                        } catch (Exception e) {
                            showStatus("Unable to read configuration map URL " + cmap);
                            main.log("Exception in cmap URL");
                            e.printStackTrace();
                        }
                    }

                    main.log("Step 1");

                    String model = getParameter("model_url");
                    main.log("Model URL: " + model);
                    if (model != null) {
                        try {
                            main.setLinkedProcessModel(new UrlProcessModel(model));
                        } catch (Exception e) {
                            showStatus("Unable to read model URL " + model);
                            main.log("Exception in apromore model URL");
                            e.printStackTrace();
                        }
                    }

                    main.log("Step 2");

                    String qml = getParameter("qml_url");
                    main.log("QML URL: " + qml);
                    if (qml != null) {
                        try {
                            main.log("Step 2a");
                            main.openUrlQuestionnaireModel(qml);
                            main.log("Step 2b");
                        } catch (Exception e) {
                            showStatus("Unable to read questionnaire URL " + qml);
                            main.log("Exception in apromore QML URL");
                            e.printStackTrace();
                        }
                    }

                    main.log("Step 3");

                    /*String*/ model = getParameter("apromore_model");
                    if (model != null) {
                        try {
                            Pattern pattern = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)");
                            Matcher matcher = pattern.matcher(model);
                            if (!matcher.matches()) {
                                throw new Exception("Unable to parse apromore_model param: " + model);
                            }

                            int    processID = Integer.valueOf(matcher.group(1));
                            String branch    = matcher.group(2);
                            String version   = matcher.group(3);

                            main.setLinkedProcessModel(new ApromoreProcessModel(processID, branch, version, main));

                        } catch (Exception e) {
                            showStatus("Unable to read model " + model);
                            main.log("Exception in apromore model");
                            e.printStackTrace();
                        }
                    }

                    /*String*/ cmap = getParameter("cmap");
                    if (cmap != null) {
                        try {
                            main.setLinkedCmap(new FileCmap(new File(cmap)));
                        } catch (Exception e) {
                            showStatus("Unable to read configuration map " + cmap);
                            main.log("Exception in cmap");
                            e.printStackTrace();
                        }
                    }

                    /*String*/ model = getParameter("model");
                    if (model != null) {
                        try {
                            main.setLinkedProcessModel(new FileProcessModel(new File(model)));
                        } catch (Exception e) {
                            showStatus("Unable to read model " + model);
                            main.log("Exception in model");
                            e.printStackTrace();
                        }
                    }

                    /*String*/ qml = getParameter("qml");
                    if (qml != null) {
                        try {
                            System.err.println("Opening QML file " + qml);
                            main.openQuestionnaireModel(new File(qml));
                        } catch (Exception e) {
                            showStatus("Unable to read questionnaire " + qml);
                            main.log("Exception in QML");
                            e.printStackTrace();
                        }
                    }

                    add(main.getJContentPane());
                    //setJMenuBar(main.getJJMenuBar());  // No menu bar when embedded in the Apromore portal web application

                    main.log("Exiting applet init");
                }
            });
        } catch (Exception e) {
            System.err.println("Failed to initialize applet, exception message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void start() {
        System.out.println("Applet starting");
        Window ancestor = SwingUtilities.getWindowAncestor(this);
        ancestor.toFront();  // this is actually to force modal dialogs to the front, rather than the applet window
        System.out.println("Applet started");
    }
}

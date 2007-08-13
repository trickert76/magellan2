/*
 *  Copyright (C) 2000-2004 Roger Butenuth, Andreas Gampe,
 *                          Stefan Goetz, Sebastian Pappert,
 *                          Klaas Prause, Enno Rehling,
 *                          Sebastian Tusk, Ulrich Kuester,
 *                          Ilja Pavkovic
 *
 * This file is part of the Eressea Java Code Base, see the
 * file LICENSING for the licensing information applying to
 * this file.
 *
 */

package magellan.client.actions.file;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import magellan.client.Client;
import magellan.client.EMapOverviewPanel;
import magellan.client.actions.MenuAction;
import magellan.client.swing.EresseaFileFilter;
import magellan.client.swing.OpenOrdersAccessory;
import magellan.library.Region;
import magellan.library.event.GameDataEvent;
import magellan.library.event.GameDataListener;
import magellan.library.io.file.FileType;
import magellan.library.utils.OrderReader;
import magellan.library.utils.PropertiesHelper;
import magellan.library.utils.Resources;
import magellan.library.utils.logging.Logger;


/**
 * DOCUMENT ME!
 *
 * @author Andreas
 * @version
 */
public class OpenOrdersAction extends MenuAction implements GameDataListener {
	private static final Logger log = Logger.getInstance(OpenOrdersAction.class);

	/**
	 * Creates a new OpenOrdersAction object.
	 *
	 * @param client
	 */
	public OpenOrdersAction(Client client) {
        super(client);
        setEnabled(false);
        client.getDispatcher().addGameDataListener(this);
	}

	/**
	 * DOCUMENT-ME
	 *
	 * 
	 */
	public void menuActionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		Properties settings = client.getProperties();
		fc.addChoosableFileFilter(new EresseaFileFilter(EresseaFileFilter.TXT_FILTER));
		fc.setSelectedFile(new File(settings.getProperty("Client.lastOrdersOpened", "")));

		OpenOrdersAccessory acc = new OpenOrdersAccessory(settings, fc);
		fc.setAccessory(acc);

		if(fc.showOpenDialog(client) == JFileChooser.APPROVE_OPTION) {
			settings.setProperty("Client.lastOrdersOpened", fc.getSelectedFile().getAbsolutePath());

			OrderReader r = new OrderReader(client.getData());
			r.setAutoConfirm(acc.getAutoConfirm());
			r.ignoreSemicolonComments(acc.getIgnoreSemicolonComments());

			try {
				// apexo (Fiete) 20061205: if in properties, force ISO encoding
				if (!PropertiesHelper.getboolean(settings, "TextEncoding.ISOopenOrders", false)) {
					// old = default = system dependent
					r.read(new FileReader(fc.getSelectedFile().getAbsolutePath()));
				} else {
					// new: force our default = ISO
					Reader stream = new InputStreamReader(new FileInputStream(fc.getSelectedFile().getAbsolutePath()), FileType.DEFAULT_ENCODING.toString());
					r.read(stream);
				}

				// OrderReaderPatch010207 stm (manually by Fiete):
				if (client.getData().regions()!=null){ // added by fiete to be failsafe
					for (Iterator it = client.getData().regions().values().iterator();it.hasNext();){
						Region region = (Region)it.next();
						region.refreshUnitRelations(true);
					}
				}
				// OrderReaderPatch end
				
				OrderReader.Status status = r.getStatus();
				Object msgArgs[] = { new Integer(status.factions), new Integer(status.units) };
				JOptionPane.showMessageDialog(client,
											  (new java.text.MessageFormat(Resources.get("actions.openordersaction.msg.fileordersopen.status.text"))).format(msgArgs),
                                                     Resources.get("actions.openordersaction.msg.fileordersopen.status.title"),
											  JOptionPane.PLAIN_MESSAGE);
			} catch(Exception exc) {
				log.error(exc);
				JOptionPane.showMessageDialog(client,
            Resources.get("actions.openordersaction.msg.fileordersopen.error.text") + e.toString(),
                        Resources.get("actions.openordersaction.msg.fileordersopen.error.title"),
											  JOptionPane.ERROR_MESSAGE);
			}

			client.getDispatcher().fire(new GameDataEvent(client, client.getData()));
		}

		// repaint since command confirmation status may have changed
		client.getDesktop().repaint(EMapOverviewPanel.IDENTIFIER);
	}

	/* (non-Javadoc)
	 * @see com.eressea.event.GameDataListener#gameDataChanged(com.eressea.event.GameDataEvent)
	 */
	public void gameDataChanged(GameDataEvent e) {
		int i = e.getGameData().regions().size();
		if (i>0) {
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}
	
	// pavkovic 2003.01.28: this is a Map of the default Translations mapped to this class
	// it is called by reflection (we could force the implementation of an interface,
	// this way it is more flexible.)
	// Pls use this mechanism, so the translation files can be created automagically
	// by inspecting all classes.
	private static Map<String,String> defaultTranslations;

	/**
	 * DOCUMENT-ME
	 *
	 * 
	 */
	public static synchronized Map<String,String> getDefaultTranslations() {
		if(defaultTranslations == null) {
			defaultTranslations = new Hashtable<String, String>();
			defaultTranslations.put("name", "Open orders...");
			defaultTranslations.put("mnemonic", "p");
			defaultTranslations.put("accelerator", "");
			defaultTranslations.put("tooltip", "");
			defaultTranslations.put("msg.fileordersopen.status.text",
									"Read orders for {0} faction(s) and {1} unit(s).");
			defaultTranslations.put("msg.fileordersopen.status.title", "Orders read");
			defaultTranslations.put("msg.fileordersopen.error.text",
									"While loading the orders the following error occurred:\n");
		}

		return defaultTranslations;
	}
  

  /**
   * @see magellan.client.actions.MenuAction#getAcceleratorTranslated()
   */
  @Override
  protected String getAcceleratorTranslated() {
    return Resources.get("actions.openordersaction.accelerator",false);
  }

  /**
   * @see magellan.client.actions.MenuAction#getMnemonicTranslated()
   */
  @Override
  protected String getMnemonicTranslated() {
    return Resources.get("actions.openordersaction.mnemonic",false);
  }

  /**
   * @see magellan.client.actions.MenuAction#getNameTranslated()
   */
  @Override
  protected String getNameTranslated() {
    return Resources.get("actions.openordersaction.name");
  }

  @Override
  protected String getTooltipTranslated() {
    return Resources.get("actions.openordersaction.tooltip",false);
  }
  
}
